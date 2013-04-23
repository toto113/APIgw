package com.kthcorp.radix.service;

import com.kthcorp.radix.domain.exception.MethodFailureException;
import com.kthcorp.radix.domain.exception.RequestResourceMissingException;

public class PlatformServiceFactory {
	
	private PlatformService clientKeyPlatformService;
	private PlatformService packagePlatformService;
	private PlatformService servicePlatformService;
	private PlatformService tokenPlatformService;
	
	public void setClientKeyPlatformService(PlatformService clientKeyPlatformService) {
		this.clientKeyPlatformService = clientKeyPlatformService;
	}
	
	public void setPackagePlatformService(PlatformService packagePlatformService) {
		this.packagePlatformService = packagePlatformService;
	}
	
	public void setServicePlatformService(PlatformService servicePlatformService) {
		this.servicePlatformService = servicePlatformService;
	}
	
	public void setTokenPlatformService(PlatformService tokenPlatformService) {
		this.tokenPlatformService = tokenPlatformService;
	}
	
	public PlatformService getPlatformService(String resource) throws RequestResourceMissingException, MethodFailureException {
		
		if("clientKeys".equals(resource)) {
			return clientKeyPlatformService;
		} else if("packages".equals(resource)) {
			return packagePlatformService;
		} else if("services".equals(resource)) {
			return servicePlatformService;
		} else if("tokens".equals(resource)) {
			return tokenPlatformService;
		} else {
			throw new RequestResourceMissingException("invalid_resource");
		}
	}
	
}
