package com.kthcorp.radix.domain.canonical.request;

import java.io.Serializable;
import java.util.List;

import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import com.kthcorp.radix.domain.service.api.protocol.http.HttpMethod;
import com.kthcorp.radix.domain.service.api.protocol.http.ParameterMap;
import com.kthcorp.radix.util.JsonBuilder;

public class OrchestratorRestRequest implements Serializable, OrchestratorRequest {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7991535984062215833L;
	
	private String key;
	private String contentType;
	private String requestedUri;
	private String uri;
	private HttpMethod httpMethod;
	private ParameterMap parameters;
	private ParameterMap headers;
	private MultiValueMap<String, Object> body;
	
	public String getKey() {
		return key;
	}
	
	public void setKey(String key) {
		this.key = key;
	}	
	
	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public String getRequestedUri() {
		return requestedUri;
	}
	
	public void setRequestedUri(String requestedUri) {
		this.requestedUri = requestedUri;
	}
	
	public String getUri() {
		return uri;
	}
	
	public void setUri(String uri) {
		this.uri = uri;
	}

	public HttpMethod getHttpMethod() {
		return httpMethod;
	}
	
	public void setHttpMethod(HttpMethod httpMethod) {
		this.httpMethod = httpMethod;
	}
	
	public ParameterMap getParameters() {
		return parameters;
	}
	
	public void setParameters(ParameterMap parameters) {
		this.parameters = parameters;
	}

	public ParameterMap getHeaders() {
		return headers;
	}
	
	public void setHeaders(ParameterMap headers) {
		this.headers = headers;
	}
	
	public void addHeader(String key, List<String> value) {
		if(this.headers == null) {
			this.headers = new ParameterMap();
		}
		this.headers.put(key, value);
	}
	
	public MultiValueMap<String, Object> getBody() {
		return body;
	}

	public void setBody(MultiValueMap<String, Object> body) {
		this.body = body;
	}
	
	public void addBody(String key, Object value) {
		if(this.body == null) {
			this.body = new LinkedMultiValueMap<String, Object>();
		}
		this.body.add(key, value);
	}
	
	@Override 
	public String toString() {
		JsonBuilder jb = new JsonBuilder();
		jb.put("class", this.getClass());
		jb.put("key", key);
		jb.put("contentType", contentType);
		jb.put("uri", uri);
		jb.put("httpMethod", httpMethod);
		jb.put("parameters", parameters);
		jb.put("headers", headers);
		jb.put("body", body);
		return jb.toString();
	}
	
}
