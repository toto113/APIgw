package com.kthcorp.radix.domain.route;

import java.io.Serializable;

public class ServiceMapper implements Serializable {

	private static final long serialVersionUID = 4697380677868171441L;

	private String serviceId;
	
	private String apiId;
	
	private String targetUrl;
	
	public String getServiceId() {
		return serviceId;
	}
	
	public void setServiceId(String serviceId) {
		this.serviceId = serviceId;
	}
	
	public String getApiId() {
		return apiId;
	}
	
	public void setApiId(String apiId) {
		this.apiId = apiId;
	}
	
	public String getTargetUrl() {
		return targetUrl;
	}
	
	public void setTargetUrl(String targetUrl) {
		this.targetUrl = targetUrl;
	}
}
