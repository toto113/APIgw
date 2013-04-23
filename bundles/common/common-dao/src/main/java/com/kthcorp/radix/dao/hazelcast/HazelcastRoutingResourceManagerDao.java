package com.kthcorp.radix.dao.hazelcast;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import com.kthcorp.radix.util.UuidViewableLoggerFactory;
import org.springframework.beans.factory.InitializingBean;

import com.hazelcast.client.ClusterClientException;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.core.MultiMap;
import com.hazelcast.query.SqlPredicate;
import com.kthcorp.radix.api.dao.RoutingResourceManagerDao;
import com.kthcorp.radix.domain.service.Service;
import com.kthcorp.radix.domain.service.routing.RoutingMethod;
import com.kthcorp.radix.domain.service.routing.direct.DirectMethod;
import com.kthcorp.radix.util.HazelcastClusterInstanceCreator;
import com.kthcorp.radix.util.HazelcastNativeClientCreator;
import com.kthcorp.radix.util.UUIDUtils;

public class HazelcastRoutingResourceManagerDao implements RoutingResourceManagerDao, InitializingBean {

	private static final Logger LOG = UuidViewableLoggerFactory.getLogger(HazelcastRoutingResourceManagerDao.class);

	private transient String daoName = "routingResource"; // Default DAO name is

	// MultiMap은 하나의 key에 대해 복수의 객체를 저장한다.
	private MultiMap<String, RoutingMethod> routingMethodStore;

	private transient IMap<String, Service> serviceStore;

	private transient boolean nativeClient = false;

	private HazelcastInstance instance = null;

	public void setHazelcastInstanceCreator(HazelcastClusterInstanceCreator instanceCreator) {
		nativeClient = true;
		this.instance = instanceCreator.getHazelcast();
	}

	public void setDaoName(String daoName) {
		this.daoName = daoName;
	}

	public void setClient(HazelcastNativeClientCreator client) {
		this.instance = client.getClientInstance();
	}

	@Override
	public void afterPropertiesSet() {
		if(routingMethodStore==null) {
			this.routingMethodStore = this.getRoutingMethodMap(this.daoName);
		}

		if(serviceStore == null) {
			this.serviceStore = this.getServiceMap(this.daoName + ".SList");
		}
	}

	private IMap<String, Service> getServiceMap(final String mapName) {
		IMap<String, Service> map = null;
		if(instance != null) {
			map = this.instance.getMap(mapName);
			if(map != null) {
				if(map.size() < 1) {
					map.addIndex("name", false);
					map.addIndex("businessPlatformID", false);
				}
			}
		}
		if(map == null) {
			LOG.error("cannot create/get Map("+mapName+") Object from hazelcast nativeClient->"+nativeClient);
			throw new ClusterClientException(mapName);
		} else {
			LOG.info("created/got Map("+mapName+") nativeClient->"+nativeClient);
		}
		return map;
	}

	private MultiMap<String, RoutingMethod> getRoutingMethodMap(final String mapName) {
		MultiMap<String, RoutingMethod> map = null;
		if(instance != null) {
			map = this.instance.getMultiMap(mapName);
		}
		if(map == null) {
			LOG.error("cannot create/get Map("+mapName+") Object from hazelcast");
			throw new ClusterClientException(mapName);
		} else {
			LOG.info("created/got Map("+mapName+") nativeClient->"+nativeClient);
		}
		return map;
	}
	

	@Override
	// TODO : delete할 때는 deleteService() 하나만 호출하고, insert할대는 밖에서 insertService(), updateService()를 각각 호출한다. 바람직하지 않다.
	public void insertService(Service service) {
		serviceStore.put(UUIDUtils.getString(service.getId()), service);
	}

	@Override
	public Collection<Service> selectLoadedAllServiceList() {
		return serviceStore.values();
	}

	@Override
	public Service selectService(String businessPlatformID, String serviceID) {
		return serviceStore.get(serviceID);
	}

	@Override
	public Collection<Service> selectServiceList(String businessPlatformID, String name) {
		return serviceStore.values(new SqlPredicate("businessPlatformID = " + businessPlatformID + " and name = '" + name + "'"));
	}

	@Override
	public List<RoutingMethod> selectRoutingMethods(String objectKey) {

		/**
		 * Object Key 의 값이 domainname::method/serviceName/version/resource 보다 길게 오는 경우가 있음 
		 * domainname::method/serviceName/version/resource/{id}   이런 경우 hazelcast 에서 찾을 수 없어 찾을 수있는 값으로 변경
		 */
		String[] keyArr = objectKey.split("/");
		if(keyArr.length > 4){
			objectKey = keyArr[0]+"/"+keyArr[1]+"/"+keyArr[2]+"/"+keyArr[3];
		}
		Collection<RoutingMethod> routingMethods = routingMethodStore.get(objectKey);
		List<RoutingMethod> routingMethodList = new ArrayList<RoutingMethod>();
		if(routingMethods==null) {
			
			return routingMethodList; 
		}
		
		
		
		
		for(RoutingMethod routingMethod : routingMethods) {
			routingMethodList.add(routingMethod);
		}
		return routingMethodList;

	}

	@Override
	public Set<String> selectObjectKeyList(String businessPlatformID) {
		return routingMethodStore.keySet();
	}


	@Override
	public void updateRoutingMethods(String businessPlatformID, String serviceID, String objectKey, List<RoutingMethod> routingMethodList) {
		routingMethodStore.remove(objectKey);
		for(RoutingMethod routingMethod : routingMethodList) {
			routingMethodStore.put(objectKey, routingMethod);
		}
	}

	@Override
	public void deleteService(String businessPlatformID, String serviceID) {
		Service service = serviceStore.get(serviceID);
		if(service==null) { return; }
		List<RoutingMethod> routingMethods = service.getApiList();
		if(routingMethods==null) { return; }
		for(RoutingMethod routingMethod : routingMethods) {
			String apiKey = ((DirectMethod)routingMethod).getServiceAPI().getApiKey();
			if(apiKey==null) { continue; }
			deleteRoutingMethod(businessPlatformID, serviceID, apiKey);
		}
		serviceStore.remove(serviceID);
	}

	@Override
	public void deleteRoutingMethod(String businessPlatformID, String serviceID, String objectKey) {
		routingMethodStore.remove(objectKey);
	}

	@Override
	public Collection<Service> selectLoadedAllServiceList(String businessPlatformID) {
		return serviceStore.values(new SqlPredicate("businessPlatformID = " + businessPlatformID));
	}

	@Override
	public void deleteService(String businessPlatformID) {
		Collection<String> services = serviceStore.keySet(new SqlPredicate("businessPlatformID = " + businessPlatformID));
		if(services != null) {
			if(services.size() > 0) {
				for(String serviceID : services) {
					this.deleteService(businessPlatformID, serviceID);
				}
			}
		}
	}

	@Override
	public boolean isAlreadyLoaded(String serviceID) {
		if(serviceStore.containsKey(serviceID)) {
			return true;
		} else {
			return false;
		}
	}

}
