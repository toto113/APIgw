package com.kthcorp.radix.component;

import java.util.List;

import org.slf4j.Logger;
import com.kthcorp.radix.util.UuidViewableLoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.kthcorp.radix.api.service.ServiceManagerService;
import com.kthcorp.radix.domain.canonical.CanonicalMessage;
import com.kthcorp.radix.domain.canonical.CanonicalMessagePayload;
import com.kthcorp.radix.domain.canonical.request.OrchestratorRestRequest;
import com.kthcorp.radix.domain.canonical.request.ServiceControllerRestRequest;
import com.kthcorp.radix.domain.service.api.Parameters;
import com.kthcorp.radix.domain.service.api.ValueGenerator;
import com.kthcorp.radix.domain.service.api.ValueObject;
import com.kthcorp.radix.domain.service.api.protocol.http.ParameterMap;
import com.kthcorp.radix.domain.service.routing.RoutingMethod;
import com.kthcorp.radix.domain.service.routing.direct.DirectMethod;
import com.kthcorp.radix.domain.service.routing.RoutingMethodSelector;

public class DefaultLiteralComponent {
	
	private static final Logger LOG = UuidViewableLoggerFactory.getLogger(RestRequestMapperComponent.class);
	
	@Autowired
	private ServiceManagerService serviceManagerService;
	
	public ServiceManagerService getServiceManagerService() {
		return serviceManagerService;
	}
	
	public void setServiceManagerService(ServiceManagerService serviceManagerService) {
		this.serviceManagerService = serviceManagerService;
	}
	
	public CanonicalMessage affectDefaultLiteral(CanonicalMessage canonicalMessage) {
		OrchestratorRestRequest request = (OrchestratorRestRequest) canonicalMessage.getPayload().getOrchestratorRequest();
		String uriKey = request.getKey();
		List<RoutingMethod> routingMethodList = serviceManagerService.getRoutingMethods(uriKey);
		ServiceControllerRestRequest serviceControllerRestRequest = (ServiceControllerRestRequest) ((CanonicalMessagePayload)canonicalMessage.getPayload()).getRequest();
		String resourcePath = serviceControllerRestRequest.getResroucePath();
		DirectMethod directMethod = (DirectMethod) RoutingMethodSelector.selectRoutingMethod(routingMethodList, resourcePath);
		Parameters parameters = directMethod.getPartnerAPI().getParametersObj();
		
		for(java.util.Map.Entry<String, ValueObject> entry : parameters.entrySet()) {
			String key = entry.getKey();
			ValueObject obj = entry.getValue();
			ValueGenerator generator = obj.getValueGenerator();
			
			if(generator == ValueGenerator.LITERAL) {
				String value = obj.getResource();
				ParameterMap targetMap = (ParameterMap) request.getParameters();
				LOG.debug("partnerAPI.parameters key={}, value={}", key, value);
				targetMap.put(key, value);
			}
		}
		
		return canonicalMessage;
	}
}
