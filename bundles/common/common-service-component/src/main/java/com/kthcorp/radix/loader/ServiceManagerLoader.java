package com.kthcorp.radix.loader;

import org.slf4j.Logger;
import com.kthcorp.radix.util.UuidViewableLoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

import com.kthcorp.radix.api.service.ServiceManagerService;

public class ServiceManagerLoader implements InitializingBean {

	private static final Logger LOG = UuidViewableLoggerFactory.getLogger(ServiceManagerLoader.class);

	@Autowired
	private ServiceManagerService serviceManagerService;

	public void setServiceManagerService(ServiceManagerService serviceManagerService) {
		this.serviceManagerService = serviceManagerService;
	}

	@Override
	public void afterPropertiesSet() {

		try {
			// Load entire Service Information
			serviceManagerService.loadAllService();
		} catch(Throwable e) {
			LOG.warn("loading all service failed.", e);
		}
	}

}
