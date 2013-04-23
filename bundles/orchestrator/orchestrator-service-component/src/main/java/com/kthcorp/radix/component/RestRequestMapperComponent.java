package com.kthcorp.radix.component;

import java.util.List;

import org.slf4j.Logger;
import com.kthcorp.radix.util.UuidViewableLoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.kthcorp.radix.api.service.ApiMapperService;
import com.kthcorp.radix.api.service.ServiceManagerService;
import com.kthcorp.radix.domain.canonical.CanonicalMessage;
import com.kthcorp.radix.domain.canonical.request.OrchestratorRestRequest;
import com.kthcorp.radix.domain.service.mapping.Mapping;
import com.kthcorp.radix.domain.service.mapping.MappingInfo;
import com.kthcorp.radix.domain.service.routing.RoutingMethod;
import com.kthcorp.radix.domain.service.routing.direct.DirectMethod;
import com.kthcorp.radix.domain.service.routing.RoutingMethodSelector;

public class RestRequestMapperComponent {
	
	/**
	 * log4j to log.
	 */
	private static final Logger LOG = UuidViewableLoggerFactory.getLogger(RestRequestMapperComponent.class);
	
	@Autowired
	private ApiMapperService apiMapperService;
	
	@Autowired
	private ServiceManagerService serviceManagerService;
	
	public ServiceManagerService getServiceManagerService() {
		return serviceManagerService;
	}
	
	public void setServiceManagerService(ServiceManagerService serviceManagerService) {
		this.serviceManagerService = serviceManagerService;
	}
	
	public CanonicalMessage mapMessage(CanonicalMessage canonicalMessage) {
		
		OrchestratorRestRequest request = (OrchestratorRestRequest) canonicalMessage.getPayload().getOrchestratorRequest();
		String uriKey = request.getKey();
		
		List<RoutingMethod> routingMethods = serviceManagerService.getRoutingMethods(uriKey);
		RoutingMethod routingMethod = RoutingMethodSelector.selectRoutingMethod(routingMethods, request.getRequestedUri());
		DirectMethod directMethod = (DirectMethod)routingMethod;
		
		MappingInfo mappingInfo = directMethod.getParameterMap();
		Mapping specMap = mappingInfo.getMappingObj();
		
		LOG.debug("mapping result : uriKey={}", uriKey);
		LOG.debug("mapping result : directMethod={}", directMethod);
		LOG.debug("mapping result : mappingInfo={}", mappingInfo);
		LOG.debug("mapping result : specMap={}", specMap);
		
		apiMapperService.mapping(specMap, request);
		
		return canonicalMessage;
	}
	
}
