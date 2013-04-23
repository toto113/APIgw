package com.kthcorp.radix.component.adaptor;

import java.util.UUID;

import org.apache.camel.ProducerTemplate;
import org.slf4j.Logger;
import com.kthcorp.radix.util.UuidViewableLoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import com.kthcorp.radix.domain.canonical.CanonicalMessage;
import com.kthcorp.radix.domain.canonical.CanonicalMessageHeader;
import com.kthcorp.radix.domain.canonical.CanonicalMessagePayload;
import com.kthcorp.radix.domain.canonical.request.OrchestratorRestRequest;
import com.kthcorp.radix.domain.service.api.protocol.http.HttpMethod;
import com.kthcorp.radix.domain.service.api.protocol.http.ParameterMap;

abstract class AbstractProvider extends AbstractJUnit4SpringContextTests {
	
	protected final Logger LOG = UuidViewableLoggerFactory.getLogger(this.getClass());
	
	@Autowired
	@Qualifier("producerTemplate")
	protected ProducerTemplate producer;
	
	protected CanonicalMessage createCanonicalMessage(HttpMethod method, String uri, ParameterMap parameters) {
		return createCanonicalMessage(uri, "application/json", method, uri, parameters, new LinkedMultiValueMap<String, Object>(), new ParameterMap());
	}
	
	protected CanonicalMessage createCanonicalMessage(HttpMethod method, String uri, ParameterMap parameters, MultiValueMap<String, Object> body) {
		return createCanonicalMessage(uri, "application/json", method, uri, parameters, body, new ParameterMap());
	}
	
	protected CanonicalMessage createCanonicalMessage(HttpMethod method, String uri, ParameterMap parameters, ParameterMap headers) {
		return createCanonicalMessage(uri, "application/json", method, uri, parameters, new LinkedMultiValueMap<String, Object>(), headers);
	}
	
	protected CanonicalMessage createCanonicalMessage(HttpMethod method, String uri, ParameterMap parameters, MultiValueMap<String, Object> body, ParameterMap headers) {
		return createCanonicalMessage(uri, "application/json", method, uri, parameters, body, headers);
	}

	protected CanonicalMessage createCanonicalMessage(String key, String contentType, HttpMethod method, String uri, ParameterMap parameters, MultiValueMap<String, Object> body, ParameterMap headers) {
		
		CanonicalMessageHeader header = new CanonicalMessageHeader();
		String messageId = UUID.randomUUID().toString();
		header.setMessageId(messageId);
		
		OrchestratorRestRequest request = new OrchestratorRestRequest();
		request.setKey(key);
		request.setContentType(contentType);
		request.setHttpMethod(method);
		request.setUri(uri);
		request.setParameters(parameters);
		request.setBody(body);
		request.setHeaders(headers);
		
		CanonicalMessagePayload payload = new CanonicalMessagePayload();
		payload.setOrchestratorRequest(request);
		
		CanonicalMessage canonicalMessage = new CanonicalMessage();
		canonicalMessage.setHeader(header);
		canonicalMessage.setPayload(payload);
		
		return canonicalMessage;
	}
}
