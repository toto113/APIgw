package com.kthcorp.radix.util;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.springframework.beans.factory.InitializingBean;

import com.kthcorp.radix.domain.route.ServiceMapper;

@Deprecated
public class ServiceMapperBuilder implements InitializingBean {
	
	private static final String DELIMITER = ":";
	
	private final ConcurrentMap<String, String> serviceMap = new ConcurrentHashMap<String, String>();
	
	private List<ServiceMapper> serviceMapperList;
	
	public void setServiceMapperList(List<ServiceMapper> serviceMapperList) {
		this.serviceMapperList = serviceMapperList;
	}
	
	public String getTargetUrl(String serviceId, String apiId) {
		String id = serviceId + DELIMITER + apiId;
		
		return this.serviceMap.get(id);
	}
	
	@Override
	public void afterPropertiesSet() throws Exception {
		for(ServiceMapper serviceMapper : this.serviceMapperList) {
			String id = serviceMapper.getServiceId() + ServiceMapperBuilder.DELIMITER + serviceMapper.getApiId();
			
			this.serviceMap.put(id, serviceMapper.getTargetUrl());
		}
	}
	
}
