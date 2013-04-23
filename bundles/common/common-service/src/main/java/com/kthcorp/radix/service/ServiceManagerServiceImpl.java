package com.kthcorp.radix.service;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import com.kthcorp.radix.util.UuidViewableLoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import com.kthcorp.radix.api.dao.RoutingResourceManagerDao;
import com.kthcorp.radix.api.service.ServiceDescriptionParser;
import com.kthcorp.radix.api.service.ServiceManagerService;
import com.kthcorp.radix.dao.mybatis.MyBatisBusinessPlatformKeyManagerDaoMapper;
import com.kthcorp.radix.dao.mybatis.MyBatisServiceManagerDaoMapper;
import com.kthcorp.radix.domain.exception.ValidateException;
import com.kthcorp.radix.domain.platform.api.RequestMessageType;
import com.kthcorp.radix.domain.service.ResourceOwner;
import com.kthcorp.radix.domain.service.Service;
import com.kthcorp.radix.domain.service.ServiceVersion;
import com.kthcorp.radix.domain.service.YesOrNo;
import com.kthcorp.radix.domain.service.api.partnerAPI.PartnerAPI;
import com.kthcorp.radix.domain.service.api.protocol.Protocol;
import com.kthcorp.radix.domain.service.api.protocol.ProtocolMode;
import com.kthcorp.radix.domain.service.api.protocol.ProtocolType;
import com.kthcorp.radix.domain.service.api.protocol.http.HttpMethod;
import com.kthcorp.radix.domain.service.api.protocol.http.ServerProtocol;
import com.kthcorp.radix.domain.service.api.serviceAPI.ServiceAPI;
import com.kthcorp.radix.domain.service.mapping.MappingInfo;
import com.kthcorp.radix.domain.service.routing.RoutingMethod;
import com.kthcorp.radix.domain.service.routing.RoutingMethodType;
import com.kthcorp.radix.domain.service.routing.direct.DirectMethod;
import com.kthcorp.radix.service.parser.service.ServiceDescriptionJsonParserImpl;
import com.kthcorp.radix.service.parser.service.ServiceDescriptionXmlParserImpl;
import com.kthcorp.radix.util.JsonBuilder;
import com.kthcorp.radix.util.UUIDUtils;

/**
 * Service Managing Service Implementation
 * 
 * @author lemo2000@kthcorp.com
 * @since 2012.02.02
 */
public class ServiceManagerServiceImpl implements ServiceManagerService {

	private static final Logger LOG = UuidViewableLoggerFactory.getLogger(ServiceManagerServiceImpl.class);

	@Autowired
	private MyBatisServiceManagerDaoMapper mapper;

	@Autowired
	private MyBatisBusinessPlatformKeyManagerDaoMapper bpMapper;

	@Autowired
	private RoutingResourceManagerDao routingResourceManagerDao;

	@Override
	public Service getServiceInstanceFromServiceDescription(final String serviceDescription, final RequestMessageType type) throws ValidateException {
		Service service = new Service();

		ServiceDescriptionParser parser = null;

		if(type == null) {
			throw new ValidateException("Not support contentType");
		}

		if(type == RequestMessageType.APPLICATION_X_WWW_FORM_URLENCODED) {
			parser = new ServiceDescriptionXmlParserImpl();
		} else if(type == RequestMessageType.APPLICATION_XML) {
			parser = new ServiceDescriptionXmlParserImpl();
		} else if(type == RequestMessageType.APPLICATION_JSON) {
			parser = new ServiceDescriptionJsonParserImpl();
		} else {
			throw new ValidateException("Not support RequestMessageType->" + type);
		}

		// XML내에 &가 있으면 "The reference to entity "location" must end with the ';' delimiter." 요런 예외가 발생한다.
		// 이를 escaping 해주자.
		String escapedServiceDescrption = serviceDescription.replaceAll("&", "&amp;");
		if(parser != null) {
			try {
				service = parser.getService(escapedServiceDescrption);
			} catch(Exception e) {
				throw new ValidateException("parsing service description failed. serviceDescription="+serviceDescription, e);
			}
		}

		return service;
	}

	@Override
	public List<Service> getServiceListWithServiceInfo(byte[] businessPlatformID, String partnerID) throws ValidateException {

		if(partnerID == null || partnerID.length() == 0) {
			throw new ValidateException("input partnerID is null or size zero");
		}

		List<Service> serviceList = this.mapper.selectServiceListWithServiceInfo(businessPlatformID, partnerID);

		if(serviceList == null || serviceList.size() == 0) {
			throw new ValidateException("no service list found for partnerID=" + partnerID);
		}

		for(Service service : serviceList) {
			if(service != null) {
				byte[] serviceID = service.getId();
				List<RoutingMethod> apiList = getApiListByServiceID(businessPlatformID, serviceID);
				service.setApiList(apiList);
			}
		}

		return serviceList;
	}

	@Override
	public Service getServiceWithServiceInfo(byte[] businessPlatformID, String partnerID, byte[] serviceID) throws ValidateException {

		if(partnerID == null || partnerID.length() == 0) {
			throw new ValidateException("input partnerID is null or size zero");
		}

		if(serviceID == null||serviceID.length != 16) {
			throw new ValidateException("input serviceID is null or zero");
		}

		if(this.mapper.selectExistingServiceCountWithPartnerID(serviceID, partnerID) < 1) {
			throw new ValidateException("service with serviceID=" + serviceID + "and partnerID=" + partnerID + " not found");
		}

		//Service service = mapper.selectService(serviceID);
		Service service = mapper.selectServiceByPartnerID(partnerID, serviceID);

		if(service != null) {
			List<RoutingMethod> apiList = getApiListByServiceID(businessPlatformID, serviceID);			
			service.setApiList(apiList);
		}			

		return service ;
	}

	private List<RoutingMethod> getApiListByServiceID(byte[] businessPlatformID, byte[] serviceID) {
		List<RoutingMethod> apiList;
		apiList = new ArrayList<RoutingMethod> () ;
		List<ServiceAPI> serviceAPIList = mapper.selectAllServiceAPIList(serviceID);
		if(serviceAPIList != null) {
			if(serviceAPIList.size() > 0) {
				for(ServiceAPI serviceAPI : serviceAPIList) {
					if(RoutingMethodType.DIRECT == serviceAPI.getRoutingMethodType()) {
						DirectMethod method = mapper.selectDirectRoutingWithAPIInfo(serviceID, serviceAPI.getId());
						if(method != null) {
							method.setServiceAPI(serviceAPI);
							method.setPartnerAPI(mapper.selectPartnerAPI(method.getPartnerAPIID()));

							if(method.getParameterMappingID() != null) {
								method.setParameterMap(mapper.selectAPIMapping(method.getParameterMappingID()));
							}
							if(method.getResultMappingID() != null) {
								method.setResultMap(mapper.selectAPIMapping(method.getResultMappingID()));
							}
							if(businessPlatformID != null) {
								method.setBusinessPlatformID(businessPlatformID);
							}

							apiList.add(method);
						}
					}
				}
			}
		}
		return apiList;
	}

	@Override
	public void loadAllService() {

		List<Service> serviceList = mapper.selectAllServiceList();
		if(serviceList != null) {
			LOG.debug("Loaded EntireService, Count->" + serviceList.size());
			if(serviceList.size() > 0) {
				for(Service service : serviceList) {
					this.updateServiceAPIToRoutingMethodMapping(service);
				}
			}
		} else {
			LOG.debug("no service exist to load");
		}
	}

	@Override
	public void loadAllService(byte[] businessPlatformID) {

		List<Service> serviceList = mapper.selectAllBusinessPlatformServiceList(businessPlatformID);
		if(LOG.isDebugEnabled()) {
			if(serviceList != null) {
				LOG.debug("Loaded EntireService, businessPlatformID->" + businessPlatformID + ", Count->" + serviceList.size());
			}
		}
		if(serviceList != null) {
			if(serviceList.size() > 0) {
				for(Service service : serviceList) {
					this.updateServiceAPIToRoutingMethodMapping(service);
				}
			}
		}
	}

	@Override
	public void loadService(byte[] serviceID) {
		Service service = mapper.selectService(serviceID);
		if(service != null) {
			this.updateServiceAPIToRoutingMethodMapping(service);
		}
	}

	private void updateServiceAPIToRoutingMethodMapping(Service service) {
		/*if(routingResourceManagerDao.isAlreadyLoaded(service.getId())) {
			if(LOG.isDebugEnabled()) {
				LOG.debug("Already Loaded Service,BusinessPlatformID->" + service.getBusinessPlatformID() + ",Id->" + service.getId() + ",Name->" + service.getName() + ",Version->" + service.getVersion());
			}
			return;
		}*/
		List<ServiceAPI> serviceAPIList = mapper.selectAllServiceAPIList(service.getId());
		if(serviceAPIList==null||serviceAPIList.size()==0) {
			LOG.warn("nothing to map ServiceAPi to RoutingMethod. serviceAPIList of Service is empty. service={}", service);
			return;
		}
		String domain = bpMapper.selectBusinessPlatformDomainFromKey(UUIDUtils.getString(service.getBusinessPlatformID()));

		
		// Remove entire service API routing description that's associated with this service from DAO Cache Object
		routingResourceManagerDao.deleteService(UUIDUtils.getString(service.getBusinessPlatformID()), UUIDUtils.getString(service.getId()));
		routingResourceManagerDao.insertService(service);

		Map<String, List<RoutingMethod>> routingMethodListMap = new HashMap<String, List<RoutingMethod>>();

		for(ServiceAPI serviceAPI : serviceAPIList) {
			LOG.trace("ServiceAPI loaded. serviceAPI={}", serviceAPI);
			if(RoutingMethodType.DIRECT == serviceAPI.getRoutingMethodType()) {
				DirectMethod method = mapper.selectDirectRoutingWithAPIInfo(service.getId(), serviceAPI.getId());
				if(method == null) {
					LOG.warn("RoutingMethod not found for serviceAPI. serviceAPI={}", serviceAPI);
					continue;
				}
				LOG.trace("RoutingMethod loaded. routingMethod={}", method);


				String apiKey = serviceAPI.getApiKey();
				if(!StringUtils.hasText(apiKey)) {
					/* TODO: 현재는 HTTP 1.0 또는 1.1 기반의 SERVER 모드만 ServiceAPI 로 제공, 나중에 추가 할 수도 있음 */
					if(serviceAPI.getProtocolObj().getProtocolMode() == ProtocolMode.SERVER) {
						Protocol protocol = serviceAPI.getProtocolObj();
						if(ProtocolType.HTTP_v1_1 == protocol.getProtocolType() || ProtocolType.HTTP_v1_0 == protocol.getProtocolType()) {
							ServerProtocol httpServerProtocol = (ServerProtocol) protocol;
							apiKey = buildApiKey(domain, httpServerProtocol.getMethod(), service.getName(), service.getVersionObj(), httpServerProtocol.getResource());
							this.mapper.updateAPIKeyOfServiceAPI(serviceAPI.getServiceID(), serviceAPI.getId(), apiKey);
						}
					}
				}


				method.setServiceAPI(serviceAPI);
				method.setPartnerAPI(mapper.selectPartnerAPI(method.getPartnerAPIID()));

				if(method.getParameterMappingID() != null) {
					method.setParameterMap(mapper.selectAPIMapping(method.getParameterMappingID()));
				}
				if(method.getResultMappingID() != null) {
					method.setResultMap(mapper.selectAPIMapping(method.getResultMappingID()));
				}
				if(service.getBusinessPlatformID() != null) {
					method.setBusinessPlatformID(service.getBusinessPlatformID());
				}


				// TODO : extract "api.withapi.com"
				List<RoutingMethod> routingMethodList = routingMethodListMap.get(apiKey);
				if(routingMethodList==null) {
					routingMethodList = new ArrayList<RoutingMethod>();
				}
				routingMethodList.add(method);
				routingMethodListMap.put(apiKey,  routingMethodList);

			}
		}

		String businessPlatformID = UUIDUtils.getString(service.getBusinessPlatformID());
		String serviceId = UUIDUtils.getString(service.getId());
		for(String key : routingMethodListMap.keySet()) {
			List<RoutingMethod> methodList = routingMethodListMap.get(key);
			routingResourceManagerDao.updateRoutingMethods(businessPlatformID, serviceId, key, methodList);
		}


		if(LOG.isDebugEnabled()) {
			String mappingDetail = getMappingDetailString(routingMethodListMap);
			LOG.debug("serviceAPI and routingMethod mapping for service success.\nsrevice={}\nmappingDetail={}", service, mappingDetail);
		}
	}

	
	private String getMappingDetailString(Map<String, List<RoutingMethod>> routingMethodListMap) {
		
		JsonBuilder jb = new JsonBuilder();
		for(String key : routingMethodListMap.keySet()) {
			jb.put(key, routingMethodListMap.get(key));
		}
		return jb.toString();
		
		
	}
	
	
	@Override
	public byte[] createService(Service service) throws ValidateException, NoSuchAlgorithmException {

		byte[] serviceId = null;

		if(service ==null) { return serviceId; }

		if(service.getId()==null) {
			service.generateID();
		}

		validateService(service, true);
		mapper.insertService(service);
		serviceId = service.getId();

		// TODO : getApiList 이름이 이상하다. 반환하는 건 RoutingMethodList인데.
		List<RoutingMethod> apiList = service.getApiList();
		for(RoutingMethod method : apiList) {

			if(RoutingMethodType.DIRECT == method.getRoutingMethodType()) {
				createServiceAsDirectMethod(service, method);
			}

		}
		
		// Load to cache DAO
		this.updateServiceAPIToRoutingMethodMapping(service);

		return serviceId;
	}

	private void createServiceAsDirectMethod(Service service, RoutingMethod method) throws NoSuchAlgorithmException {

		DirectMethod directMethod = (DirectMethod) method;
		if(directMethod.getId()==null) {
			directMethod.generateID();
		}

		ServiceAPI serviceAPI = directMethod.getServiceAPI();
		PartnerAPI partnerAPI = directMethod.getPartnerAPI();

		if(serviceAPI.getId()==null) {
			serviceAPI.generateID();
		}

		serviceAPI.setServiceID(service.getId());
		if(serviceAPI.getIsValid() == null) {
			serviceAPI.setIsValid(YesOrNo.Y);
		}
		mapper.insertServiceAPI(serviceAPI);

		if(partnerAPI.getId()==null) {
			partnerAPI.generateID();
		}
		partnerAPI.setServiceID(service.getId());
		if(partnerAPI.getIsValid() == null) {
			partnerAPI.setIsValid(YesOrNo.Y);
		}
		mapper.insertPartnerAPI(partnerAPI);

		if(directMethod.getParameterMap() != null) {
			MappingInfo mappingInfo = directMethod.getParameterMap();
			if(mappingInfo.getId()==null) {
				mappingInfo.generateID();
			}
			mappingInfo.setServiceID(service.getId());
			mappingInfo.setServiceAPIID(serviceAPI.getId());
			mappingInfo.setPartnerAPIID(partnerAPI.getId());
			mapper.insertAPIMapping(mappingInfo);
			directMethod.setParameterMappingID(mappingInfo.getId());
		}
		if(directMethod.getResultMap() != null) {
			MappingInfo mappingInfo = directMethod.getResultMap();
			if(mappingInfo.getId()==null) {
				mappingInfo.generateID();
			}
			mappingInfo.setServiceID(service.getId());
			mappingInfo.setServiceAPIID(serviceAPI.getId());
			mappingInfo.setPartnerAPIID(partnerAPI.getId());
			mapper.insertAPIMapping(mappingInfo);
			directMethod.setResultMappingID(mappingInfo.getId());
		}
		directMethod.setServiceID(service.getId());
		directMethod.setServiceAPIID(serviceAPI.getId());
		directMethod.setPartnerAPIID(partnerAPI.getId());

		mapper.insertDirectRouting(directMethod);
	}

	@Override
	@Deprecated
	// 이전 코드들을 수정하지 않기 위해 남겨 둔다.
	// 이전 코드는 하나의 key에 하나의 RoutingMethod만 존재했다.
	public RoutingMethod getRoutingMethod(String key) {
		List<RoutingMethod> routingMethods = getRoutingMethods(key);
		if(routingMethods==null||routingMethods.size()!=1) {
			return null;
		}
		return routingMethods.get(0);
	}

	@Override
	public List<RoutingMethod> getRoutingMethods(String key) {
		return routingResourceManagerDao.selectRoutingMethods(key);
	}

	@Override
	public byte[] createServiceOnly(Service service) throws ValidateException, NoSuchAlgorithmException {
		return this.createServiceOnly(service, false);
	}

	@Override
	public byte[] createServiceOnly(Service service, boolean checkIfExists) throws ValidateException, NoSuchAlgorithmException {

		byte[] serviceId = null;

		if(service == null) {
			throw new ValidateException("cannot find Service object");
		}
		if(service.getBusinessPlatformID() == null) {
			throw new ValidateException("businessPlatformID should be setted");
		}
		if(service.getName() == null) {
			throw new ValidateException("name should be setted");
		}
		if(service.getName().equalsIgnoreCase("platform")) {
			throw new ValidateException("name of \"platform\" is reserved by platformAPI");
		}
		if(service.getPartnerID() == null) {
			throw new ValidateException("partnerID should be setted");
		}
		if(service.getResourceOwner() == null) {
			throw new ValidateException("businessPlatformID should be setted");
		}
		if(ResourceOwner.USER == service.getResourceOwner()) {
			if(service.getResourceAuthUrl() == null) {
				throw new ValidateException("resourceAuthURL should be setted");
			}
		}
		if(service.getVersion() == null || service.getVersion().length() == 0) {
			throw new ValidateException("version should be setted");
		}

		if(true == checkIfExists) {
			if(1 <= this.mapper.selectExistsServiceCount(service.getBusinessPlatformID(), service.getName(), service.getVersion())) {
				throw new ValidateException("businessPlatformID->" + service.getBusinessPlatformID() + ", name->" + service.getName() + " is already exists");
			}
		}

		if(service.getId()==null) {
			service.generateID();
		}
		serviceId = service.getId();
		mapper.insertService(service);

		return serviceId;
	}

	@Override
	public void createServiceAPI(ServiceAPI serviceAPI) throws ValidateException, NoSuchAlgorithmException {
		if(serviceAPI == null) {
			throw new ValidateException("cannot find ServiceAPI object");
		}
		if(serviceAPI.getId()==null) {
			serviceAPI.generateID();
		}
		if(serviceAPI.getServiceID() == null) {
			throw new ValidateException("serviceID should be setted");
		}
		if(!StringUtils.hasText(serviceAPI.getName())) {
			throw new ValidateException("name should be setted");
		}
		if(serviceAPI.getTransportType() == null) {
			throw new ValidateException("transportType should be setted");
		}
		if(serviceAPI.getDefaultTransformType() == null) {
			throw new ValidateException("defaultTransformType should be setted");
		}
		if(serviceAPI.getProtocolType() == null) {
			throw new ValidateException("protocolType should be setted");
		}
		if(!StringUtils.hasText(serviceAPI.getProtocolMeta())) {
			throw new ValidateException("protocolMeta should be setted");
		}
		if(!StringUtils.hasText(serviceAPI.getParameters())) {
			throw new ValidateException("parameters should be setted");
		}

		mapper.insertServiceAPI(serviceAPI);
	}

	@Override
	public void createPartnerAPI(PartnerAPI partnerAPI) throws ValidateException, NoSuchAlgorithmException {
		if(partnerAPI == null) {
			throw new ValidateException("cannot find PartnerAPI object");
		}
		if(partnerAPI.getId()==null) {
			partnerAPI.generateID();
		}
		if(partnerAPI.getServiceID() == null) {
			throw new ValidateException("serviceID should be setted");
		}
		if(!StringUtils.hasText(partnerAPI.getName())) {
			throw new ValidateException("name should be setted");
		}
		if(partnerAPI.getTransportType() == null) {
			throw new ValidateException("transportType should be setted");
		}
		if(partnerAPI.getDefaultTransformType() == null) {
			throw new ValidateException("defaultTransformType should be setted");
		}
		if(partnerAPI.getProtocolType() == null) {
			throw new ValidateException("protocolType should be setted");
		}
		if(!StringUtils.hasText(partnerAPI.getProtocolMeta())) {
			throw new ValidateException("protocolMeta should be setted");
		}
		if(!StringUtils.hasText(partnerAPI.getParameters())) {
			throw new ValidateException("parameters should be setted");
		}

		mapper.insertPartnerAPI(partnerAPI);
	}

	@Override
	public void createAPIMapping(MappingInfo mappingInfo) throws ValidateException, NoSuchAlgorithmException {
		if(mappingInfo == null) {
			throw new ValidateException("cannot find MappingInfo object");
		}
		if(mappingInfo.getId()==null) {
			mappingInfo.generateID();
		}
		if(mappingInfo.getServiceID() == null) {
			throw new ValidateException("serviceID should be setted");
		}
		if(mappingInfo.getServiceAPIID() == null) {
			throw new ValidateException("serviceAPIID should be setted");
		}
		if(mappingInfo.getPartnerAPIID() == null) {
			throw new ValidateException("partnerAPIID should be setted");
		}
		if(mappingInfo.getMappingType() == null) {
			throw new ValidateException("mappingType should be setted");
		}
		if(!StringUtils.hasText(mappingInfo.getMapping())) {
			throw new ValidateException("mapping should be setted");
		}

		mapper.insertAPIMapping(mappingInfo);
	}

	@Override
	public void createDirectRouting(DirectMethod directMethod) throws ValidateException, NoSuchAlgorithmException {
		if(directMethod == null) {
			throw new ValidateException("cannot find DirectMethod object");
		}
		if(directMethod.getId()==null) {
			directMethod.generateID();
		}
		if(directMethod.getServiceID() == null) {
			throw new ValidateException("serviceID should be setted");
		}
		if(directMethod.getServiceAPIID() == null) {
			throw new ValidateException("serviceAPIID should be setted");
		}
		if(directMethod.getPartnerAPIID() == null) {
			throw new ValidateException("partnerAPIID should be setted");
		}

		mapper.insertDirectRouting(directMethod);
	}

	public static String buildApiKey(String businessPlatformDomain, HttpMethod method, String serviceName, ServiceVersion serviceVersion, String resource) {
		return buildApiKey(businessPlatformDomain, method, serviceName, serviceVersion.getVersionNumber(), resource);
	}

	public static String buildApiKey(String businessPlatformDomain, HttpMethod method, String serviceName, int versionNumber, String resource) {
		String apiKey = businessPlatformDomain + "::" + method + ":/" + serviceName + "/" + versionNumber;
		if(resource != null) {
			apiKey = apiKey + "/" + resource;
		}
		return apiKey;
	}

	@Override
	public String getURIString(String businessPlatformDomain, HttpMethod method, String serviceName, ServiceVersion serviceVersion, String resource) {
		return buildApiKey(businessPlatformDomain, method, serviceName, serviceVersion, resource);
	}

	@Override
	public void validateService(Service service) throws ValidateException {
		this.validateService(service, false);
	}

	@Override
	public void validateService(Service service, boolean checkIfExists) throws ValidateException {
		if(service == null) {
			throw new ValidateException("cannot find Service object");
		}
		if(service.getBusinessPlatformID() == null) {
			throw new ValidateException("businessPlatformID should be setted. service="+service);
		}
		if(service.getId() == null) {
			throw new ValidateException("service->ID should be setted. service="+service);
		}
		if(service.getId().length != 16) {
			throw new ValidateException("service->ID is invalid. service="+service);
		}
		if(!StringUtils.hasText(service.getName())) {
			throw new ValidateException("name should be setted. service="+service);
		}
		if(service.getName().equalsIgnoreCase("platform")) {
			throw new ValidateException("name of \"platform\" is reserved by platformAPI. service="+service);
		}
		if(!StringUtils.hasText(service.getVersion())) {
			throw new ValidateException("version should be setted. service="+service);
		}

		if(true == checkIfExists) {
			if(1 <= this.mapper.selectExistsServiceCount(service.getBusinessPlatformID(), service.getName(), service.getVersion())) {
				throw new ValidateException("businessPlatformID->" + UUIDUtils.getString(service.getBusinessPlatformID()) + ", name->" + service.getName() + " is already exists. service="+service);
			}
		}

		if(!StringUtils.hasText(service.getPartnerID())) {
			throw new ValidateException("partnerID should be setted. service="+service);
		}
		if(service.getResourceOwner() == null) {
			throw new ValidateException("resourceOwner should be setted. service="+service);
		}
		if(ResourceOwner.USER == service.getResourceOwner()) {
			if(service.getResourceAuthUrl() == null) {
				throw new ValidateException("resourceAuthURL should be setted. service="+service);
			}
		}

		List<RoutingMethod> apiList = service.getApiList();
		if(apiList == null || apiList.size() < 1) {
			throw new ValidateException("service MUST has one or more service API");
		}
		for(RoutingMethod method : apiList) {

			if(method == null) {
				throw new ValidateException("cannot find method object");
			}
			if(RoutingMethodType.DIRECT == method.getRoutingMethodType()) {

				DirectMethod directMethod = (DirectMethod) method;

				ServiceAPI serviceAPI = directMethod.getServiceAPI();
				if(serviceAPI == null) {
					throw new ValidateException("cannot find ServiceAPI object. serviceAPI="+serviceAPI);
				}
				if(!StringUtils.hasText(serviceAPI.getName())) {
					throw new ValidateException("name should be setted. serviceAPI="+serviceAPI);
				}
				if(serviceAPI.getTransportType() == null) {
					throw new ValidateException("transportType should be setted. serviceAPI="+serviceAPI);
				}
				if(serviceAPI.getDefaultTransformType() == null) {
					throw new ValidateException("defaultTransformType should be setted. serviceAPI="+serviceAPI);
				}
				if(serviceAPI.getProtocolType() == null) {
					throw new ValidateException("protocolType should be setted. serviceAPI="+serviceAPI);
				}
				if(!StringUtils.hasText(serviceAPI.getProtocolMeta())) {
					throw new ValidateException("protocolMeta should be setted. serviceAPI="+serviceAPI);
				}
				if(!StringUtils.hasText(serviceAPI.getParameters())) {
					throw new ValidateException("parameters should be setted. serviceAPI="+serviceAPI);
				}

				PartnerAPI partnerAPI = directMethod.getPartnerAPI();
				if(partnerAPI == null) {
					throw new ValidateException("cannot find PartnerAPI object. partnerAPI="+serviceAPI);
				}
				if(!StringUtils.hasText(partnerAPI.getName())) {
					throw new ValidateException("name should be setted. partnerAPI="+serviceAPI);
				}
				if(partnerAPI.getTransportType() == null) {
					throw new ValidateException("transportType should be setted. partnerAPI="+serviceAPI);
				}
				if(partnerAPI.getDefaultTransformType() == null) {
					throw new ValidateException("defaultTransformType should be setted. partnerAPI="+serviceAPI);
				}
				if(partnerAPI.getProtocolType() == null) {
					throw new ValidateException("protocolType should be setted. partnerAPI="+serviceAPI);
				}
				if(!StringUtils.hasText(partnerAPI.getProtocolMeta())) {
					throw new ValidateException("protocolMeta should be setted. partnerAPI="+serviceAPI);
				}
				if(!StringUtils.hasText(partnerAPI.getParameters())) {
					throw new ValidateException("parameters should be setted. partnerAPI="+serviceAPI);
				}

				MappingInfo parameterMapping = directMethod.getParameterMap();
				if(parameterMapping != null) {
					if(parameterMapping.getMappingType() == null) {
						throw new ValidateException("mappingType should be setted");
					}
					if(!StringUtils.hasText(parameterMapping.getMapping())) {
						throw new ValidateException("mapping should be setted");
					}
				}

				MappingInfo resultMapping = directMethod.getResultMap();
				if(resultMapping != null) {
					if(resultMapping.getMappingType() == null) {
						throw new ValidateException("mappingType should be setted");
					}
					if(!StringUtils.hasText(resultMapping.getMapping())) {
						throw new ValidateException("mapping should be setted");
					}
				}

			}
		}
		
		// 각  ServiceAPI의 pathTemplate의 중복을 비교한다.
		// pathTempalte의 path variable을 똑같은 상수로 변경하면, 중복을 비교할 수 있다.
		for(RoutingMethod method : apiList) {

			Map<String, ServiceAPI> pathTemplateMap = new Hashtable<String, ServiceAPI>();
			if(RoutingMethodType.DIRECT == method.getRoutingMethodType()) {
				DirectMethod directMethod = (DirectMethod) method;
				ServiceAPI serviceAPI = directMethod.getServiceAPI();
				ServerProtocol serverProtocol = (ServerProtocol)serviceAPI.getProtocolObj();
				String pathTemplate = serverProtocol.getPathTemplate();

				// pattern의 ?는 non-greedy를 의미.
				// /a/b/c/abc/{abc}/def/{def}-->/a/b/c/abc/PATH_VARIABLE_123654234/def/PATH_VARIABLE_123654234
				String pattern = "\\{.*?\\}";
				String comparablePathTemplate = pathTemplate.replaceAll(pattern, "PATH_VARIABLE_123654234");
				if(pathTemplateMap.containsKey(comparablePathTemplate)) {
					ServiceAPI otherServiceAPI = pathTemplateMap.get(comparablePathTemplate);
					String otherPathTemplate = ((ServerProtocol)otherServiceAPI.getProtocolObj()).getPathTemplate();
					throw new ValidateException("duplicated pathTemplate. "+pathTemplate+", "+otherPathTemplate);
				}
				pathTemplateMap.put(comparablePathTemplate, serviceAPI);
			}
		}
		
	}
	
	
	@Override
	public boolean removeService(Service service) throws ValidateException {
		if(service == null) {
			throw new ValidateException("cannot find Service object");
		}
		if(service.getBusinessPlatformID() == null) {
			throw new ValidateException("businessPlatformID is null");
		}

		if(service.getId() == null) {
			throw new ValidateException("serviceId is null");
		}

		byte[] serviceId = service.getId();

		this.mapper.backupDirectRouting(serviceId);
		if(this.mapper.deleteDirectRouting(serviceId) <= 0) {
			throw new ValidateException("removing service failed. trying to remove not existing Service.DirectMethod, serviceId->"+UUIDUtils.getString(serviceId));
		}

		this.mapper.backupAPIMapping(serviceId);
		if(this.mapper.deleteAPIMapping(serviceId) <= 0) {
			throw new ValidateException("removing service failed. trying to remove not existing Service.APIMapping, serviceId->"+UUIDUtils.getString(serviceId));
		}

		this.mapper.backupPartnerAPIWithServiceID(serviceId);
		if(this.mapper.deletePartnerAPIWithServiceID(serviceId) <= 0) {
			throw new ValidateException("removing service failed. trying to remove not existing Service.PartnerAPI, serviceId->"+UUIDUtils.getString(serviceId));

		}

		this.mapper.backupServiceAPIWithServiceID(serviceId);
		if(this.mapper.deleteServiceAPIWithServiceID(serviceId) <= 0) {
			throw new ValidateException("removing service failed. trying to remove not existing Service.ServiceAPI, serviceId->"+UUIDUtils.getString(serviceId));
		}

		this.mapper.backupService(serviceId);
		if(this.mapper.deleteService(serviceId) <= 0) {
			throw new ValidateException("removing service failed. trying to remove not existing Service, serviceId->"+UUIDUtils.getString(serviceId));
		}

		// Remove entire service API routing description that's associated with this service from DAO Cache Object
		this.routingResourceManagerDao.deleteService(UUIDUtils.getString(service.getBusinessPlatformID()), UUIDUtils.getString(service.getId()));

		return true;
	}

	@Override
	public void modifyService(Service service) throws ValidateException {
		if(service == null) {
			throw new ValidateException("cannot find Service object");
		}

		if(service.getBusinessPlatformID() == null) {
			throw new ValidateException("businessPlatformID is null");
		}

		if(service.getId() == null) {
			throw new ValidateException("serviceId is null");
		}

		List<RoutingMethod> apiList = service.getApiList();
		ServiceAPI serviceAPIToBeUpdated = null;
		PartnerAPI partnerAPIToBeUPdated = null;
		MappingInfo ParameterMappingInfoToBeUpdated = null;
		MappingInfo ResultMappingInfoToBeUpdated = null;

		Map<String, List<RoutingMethod>> routingMethodListMap = new HashMap<String, List<RoutingMethod>>();
		for(RoutingMethod method : apiList) {
			if(RoutingMethodType.DIRECT == method.getRoutingMethodType()) {
				DirectMethod directMethod = (DirectMethod) method;

				// Updating ServiceAPI
				serviceAPIToBeUpdated = directMethod.getServiceAPI();
				serviceAPIToBeUpdated.setServiceID(service.getId());
				this.modifyServiceAPI(serviceAPIToBeUpdated);

				// Updating PartnerAPI
				partnerAPIToBeUPdated = directMethod.getPartnerAPI();
				partnerAPIToBeUPdated.setServiceID(service.getId());
				this.modifyPartnerAPI(partnerAPIToBeUPdated);

				// Updating Parameter MappingInfo
				ParameterMappingInfoToBeUpdated = directMethod.getParameterMap();
				ParameterMappingInfoToBeUpdated.setServiceID(service.getId());
				this.modifyAPIMapping(ParameterMappingInfoToBeUpdated);

				// Updating Result MappingInfo
				ResultMappingInfoToBeUpdated = directMethod.getParameterMap();
				ResultMappingInfoToBeUpdated.setServiceID(service.getId());
				this.modifyAPIMapping(ResultMappingInfoToBeUpdated);

				// Updating DirectRouting
				directMethod.setServiceID(service.getId());
				this.modifyDirectRouting(directMethod);

				// Updating Routing Method on Hazelcast
				String strHttpMethod = null;
				String strResource = null;
				JSONObject protocolMetaJsonObject;
				try {
					protocolMetaJsonObject = new JSONObject(serviceAPIToBeUpdated.getProtocolMeta());
					strHttpMethod = protocolMetaJsonObject.getString("method");
					strResource = protocolMetaJsonObject.getString("resource");
				} catch(JSONException e) {
					throw new ValidateException("loading old service failed. invalid metadata='"+serviceAPIToBeUpdated.getProtocolMeta()+"'", e);
				}

				HttpMethod httpMethod = null;
				if("GET".equals(strHttpMethod))
					httpMethod = HttpMethod.GET;
				else if("POST".equals(strHttpMethod))
					httpMethod = HttpMethod.POST;
				else if("DELETE".equals(strHttpMethod))
					httpMethod = HttpMethod.DELETE;
				else if("PUT".equals(strHttpMethod))
					httpMethod = HttpMethod.PUT;

				// TODO : extract "api.withapi.com"
				String keyForCache = getURIString("api.withapi.com", httpMethod, service.getName(), service.getVersionObj(), strResource);
				List<RoutingMethod> routingMethodList = routingMethodListMap.get(keyForCache);
				if(routingMethodList==null) {
					routingMethodList = new ArrayList<RoutingMethod>();
				}
				routingMethodList.add(method);
				routingMethodListMap.put(keyForCache,  routingMethodList);
			}
		}

		String businessPlatformID = UUIDUtils.getString(service.getBusinessPlatformID());
		String serviceId = UUIDUtils.getString(service.getId());
		for(String key : routingMethodListMap.keySet()) {
			List<RoutingMethod> methodList = routingMethodListMap.get(key);
			routingResourceManagerDao.updateRoutingMethods(businessPlatformID, serviceId, key, methodList);
		}


	}

	@Override
	public void modifyServiceAPI(ServiceAPI serviceAPI) throws ValidateException {

		if(serviceAPI.getId() == null) {
			throw new ValidateException("id is null");
		}

		if(this.mapper.updateServiceAPI(serviceAPI) <= 0) {
			throw new ValidateException("updateService failed: trying to remote not existing Service.ServiceAPI");
		}
	}

	@Override
	public void modifyPartnerAPI(PartnerAPI partnerAPI) throws ValidateException {

		if(partnerAPI.getId() == null) {
			throw new ValidateException("id is null");
		}

		if(this.mapper.updatePartnerAPI(partnerAPI) <= 0) {
			throw new ValidateException("updateService failed: trying to remote not existing Service.PartnerAPI");
		}
	}

	@Override
	public void modifyAPIMapping(MappingInfo mappingInfo) throws ValidateException {

		if(mappingInfo.getId() == null) {
			throw new ValidateException("id is null");
		}

		if(this.mapper.updateAPIMappingWithAPIInfo(mappingInfo) <= 0) {
			throw new ValidateException("updateService failed: trying to remote not existing Service.MappingInfo");
		}
	}

	@Override
	public void modifyDirectRouting(DirectMethod directMethod) throws ValidateException {

		if(directMethod.getId() == null) {
			throw new ValidateException("id is null");
		}

		if(this.mapper.updateDirectRoutingWithAPIInfo(directMethod) <= 0) {
			throw new ValidateException("updateService failed: trying to remote not existing DirectRouting");
		}
	}

	@Override
	@Deprecated
	// 기존 테스트 케이스들의 호환을 위해 남겨 둔다.
	public void replaceRoutingMethod(byte[] businessPlatformID, byte[] serviceID, String objectKey, RoutingMethod routingMethod) {
		List<RoutingMethod> routingMethodList = new ArrayList<RoutingMethod>();
		routingMethodList.add(routingMethod);
		replaceRoutingMethods(businessPlatformID, serviceID, objectKey, routingMethodList);
	}


	@Override
	public void replaceRoutingMethods(byte[] businessPlatformID, byte[] serviceID, String objectKey, List<RoutingMethod> routingMethods) {
		routingResourceManagerDao.updateRoutingMethods(UUIDUtils.getString(businessPlatformID), UUIDUtils.getString(serviceID), objectKey, routingMethods);
	}
}
