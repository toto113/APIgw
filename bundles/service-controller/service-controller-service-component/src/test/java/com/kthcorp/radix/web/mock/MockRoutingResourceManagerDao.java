package com.kthcorp.radix.web.mock;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.InitializingBean;

import com.kthcorp.radix.api.dao.RoutingResourceManagerDao;
import com.kthcorp.radix.domain.service.Service;
import com.kthcorp.radix.domain.service.routing.RoutingMethod;

public class MockRoutingResourceManagerDao implements RoutingResourceManagerDao, InitializingBean {
	
	@Override
	public void afterPropertiesSet() throws Exception {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void insertService(Service service) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public Collection<Service> selectLoadedAllServiceList() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public Collection<Service> selectLoadedAllServiceList(String businessPlatformID) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public boolean isAlreadyLoaded(String serviceID) {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public Service selectService(String businessPlatformID, String serviceID) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public Collection<Service> selectServiceList(String businessPlatformID, String name) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public Set<String> selectObjectKeyList(String businessPlatformID) {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	@Override
	public List<RoutingMethod> selectRoutingMethods(String objectKey) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void updateRoutingMethods(String businessPlatformID, String serviceID, String objectKey, List<RoutingMethod> routingMethodList) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void deleteService(String businessPlatformID) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void deleteService(String businessPlatformID, String serviceID) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void deleteRoutingMethod(String businessPlatformID, String serviceID, String objectKey) {
		// TODO Auto-generated method stub
		
	}
	
}
