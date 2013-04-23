package com.kthcorp.radix.api.dao;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import com.kthcorp.radix.domain.service.Service;
import com.kthcorp.radix.domain.service.routing.RoutingMethod;

public interface RoutingResourceManagerDao {
	
	// Create & Add
	public void insertService(Service service);
	
	// Get
	public Collection<Service> selectLoadedAllServiceList();
	
	public Collection<Service> selectLoadedAllServiceList(String businessPlatformID);
	
	public boolean isAlreadyLoaded(String serviceID);
	
	public Service selectService(String businessPlatformID, String serviceID);
	
	public Collection<Service> selectServiceList(String businessPlatformID, String name);
	
	public Set<String> selectObjectKeyList(String businessPlatformID);
	
	public List<RoutingMethod> selectRoutingMethods(String objectKey);
	
	// Update
	public void updateRoutingMethods(String businessPlatformID, String serviceID, String objectKey, List<RoutingMethod> routingMethods);
	
	// Destroy & Remove
	public void deleteService(String businessPlatformID);
	
	public void deleteService(String businessPlatformID, String serviceID);
	
	public void deleteRoutingMethod(String businessPlatformID, String serviceID, String objectKey);
	
}
