package com.kthcorp.radix.domain.service.business;

import java.io.Serializable;
import java.util.UUID;

import com.kthcorp.radix.domain.service.routing.RoutingMethod;
import com.kthcorp.radix.util.JsonBuilder;

public class TransactionInfo implements Serializable {
	
	private static final long serialVersionUID = -1191541524583778838L;
	
	private UUID transactionID;
	private String businessPlatformId;
	private String serviceId;
	private RoutingMethod routingMethod;
	
	/* TODO: 로깅에 관련된 파라메터 정리 필요 */
	
	// Setter & Getter
	public UUID getTransactionID() {
		return transactionID;
	}
	
	public void setTransactionID(UUID transactionID) {
		this.transactionID = transactionID;
	}
	
	public String getBusinessPlatformId() {
		return businessPlatformId;
	}
	
	public void setBusinessPlatformId(String businessPlatformId) {
		this.businessPlatformId = businessPlatformId;
	}
	
	public String getServiceId() {
		return serviceId;
	}
	
	public void setServiceId(String serviceId) {
		this.serviceId = serviceId;
	}
	
	public RoutingMethod getRoutingMethod() {
		return routingMethod;
	}
	
	public void setRoutingMethod(RoutingMethod routingMethod) {
		this.routingMethod = routingMethod;
	}
	
	@Override
	public String toString() {
		JsonBuilder jb = new JsonBuilder();
		jb.put("class", this.getClass());
		jb.put("transactionID", transactionID);
		jb.put("businessPlatformId", businessPlatformId);
		jb.put("serviceId", serviceId);
		jb.put("routingMethod", routingMethod);
		return jb.toString();
	}
	
}
