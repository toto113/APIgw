package com.kthcorp.radix.domain.service.routing;

public interface RoutingMethod {
	
	// Routing Object Information
	public RoutingMethodType getRoutingMethodType();
	
	// Business Platform ID
	public void setBusinessPlatformID(byte[] businessPlatformID);
	
	public byte[] getBusinessPlatformID();
	
	// Service Property
	public void setServiceID(byte[] serviceId);
	
	public byte[] getServiceID();

}
