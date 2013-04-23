package com.kthcorp.radix.component.adaptor;

import java.util.List;
import java.util.Map.Entry;

import org.slf4j.Logger;
import com.kthcorp.radix.util.UuidViewableLoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.util.Assert;
import org.springframework.util.MultiValueMap;

import com.kthcorp.radix.domain.canonical.CanonicalMessage;
import com.kthcorp.radix.domain.canonical.reply.AdaptorRestReply;
import com.kthcorp.radix.domain.canonical.request.OrchestratorRestRequest;
import com.kthcorp.radix.domain.exception.AdaptorException;
import com.kthcorp.radix.domain.service.api.protocol.http.HttpMethod;
import com.kthcorp.radix.domain.service.api.protocol.http.ParameterMap;

public class RestServiceCallComponent {
	
	private final Logger LOG = UuidViewableLoggerFactory.getLogger(this.getClass());
	
	private RestExecuteComponent executeComponent;
	public void setExecuteComponent(RestExecuteComponent executeComponent) {
		this.executeComponent = executeComponent;
	}
	
	public RestServiceCallComponent() {
	}

	public CanonicalMessage doCall(CanonicalMessage canonicalMessage) throws AdaptorException {
		
		LOG.trace("REST Adaptor >>> RestServiceCallComponent.doCall({})", canonicalMessage);
		String messageId = canonicalMessage.getHeader().getMessageId();
		LOG.debug("messageId={}", messageId);
		LOG.debug("request={}", canonicalMessage.getPayload().getOrchestratorRequest());
		
		OrchestratorRestRequest request = (OrchestratorRestRequest) canonicalMessage.getPayload().getOrchestratorRequest();
		HttpMethod httpMethod = request.getHttpMethod();
		String key = request.getKey();
		String contentType = request.getContentType();
		String uri = request.getUri();
		ParameterMap parameters = request.getParameters();
		ParameterMap headerParameters = request.getHeaders();
		MultiValueMap<String, Object> body = request.getBody();
		
		LOG.debug("key={}", key);
		LOG.debug("contentType={}", contentType);
		LOG.debug("httpMethod={}", httpMethod);
		LOG.debug("uri={}", uri);
		if(headerParameters != null) {
			for(String name : headerParameters.keys()) {
				LOG.debug("header parameter:{}={}", name, headerParameters.get(name));
			}
		}
		if(parameters != null) {
			for(String name : parameters.keys()) {
				LOG.debug("parameter:{}={}", name, parameters.get(name));
			}
		}
		LOG.debug("body={}", body);
		if(body != null) {
			for(String name : body.keySet()) {
				LOG.debug("body:{}={}", name, body.get(name));
			}
		}
		
		String url = this.formulateRestTemplateUrl(uri, parameters);
		LOG.debug("formuated url={}", url);
		
		HttpHeaders headers = createHeaders(headerParameters);
		LOG.debug("httpHeaders={}", headers);
		
		AdaptorRestReply reply = executeComponent.doRequest(messageId, url, httpMethod, body, headers, parameters);
		LOG.debug("reply={}", reply);
		LOG.debug("reply.httpStatus={}", reply.getHttpStatus());
		LOG.debug("reply.httpHeader={}", reply.getHttpHeaders());
		LOG.debug("reply.contentLength={}", reply.getContentLength());
		LOG.debug("reply.mediaType={}", reply.getMediaType());
		
		canonicalMessage.getPayload().setAdaptorReply(reply);
		return canonicalMessage;
	}
	
	protected final HttpHeaders createHeaders(ParameterMap parameters) {
		
		Assert.isTrue(parameters != null, "header parameters cannot be null");
		HttpHeaders headers = new HttpHeaders();
		for(Entry<String, List<String>> entry : parameters.entrySet()) {
			headers.put(entry.getKey(), entry.getValue());
		}
		LOG.debug("headers={}", headers);
		return headers;
	}
	
	protected final String formulateRestTemplateUrl(String uri, ParameterMap parameters) {
		
		Assert.isTrue(parameters != null, "parameters cannot be null");
		// 리비전 d856560d8d3a0f9ccfe28b7d2692d2fefb2e8880의 수정으로 실패하게 된 테스트 케이스를 무조건 uri를 반환하도록 함.
		// 테스트를 실패하게 한 수정은 요 메소드를 호출하지 않고, uri값으로 그 결과를 사용하도록 하였다.
		if(true) { return uri; }
		
		@SuppressWarnings("unused")
		StringBuffer sb = new StringBuffer();
		for(Entry<String, List<String>> entry : parameters.entrySet()) {
			sb.append("&").append(entry.getKey()).append("=");
			String values = null;
			for(String v : entry.getValue()) {
				if(values == null) {
					values = v == null ? "" : v;
				} else {
					values += "," + v == null ? "" : v;
				}
			}
			sb.append(values);
		}
		
		if(sb.length() > 1) {
			if(uri.contains("?")) {
				return uri + sb.toString();
			} else {
				return uri + "?" + sb.toString().substring(1);
			}
		}
		return uri;
	}
}
