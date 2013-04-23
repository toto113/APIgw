package com.kthcorp.radix.domain.canonical.request;

import java.io.Serializable;

import org.springframework.util.MultiValueMap;

import com.kthcorp.radix.domain.service.api.protocol.http.ParameterMap;
import com.kthcorp.radix.util.JsonBuilder;

public class ServiceControllerRestRequest implements Serializable, ServiceControllerRequest {
	
	private static final long serialVersionUID = -2808458541137773940L;
//	private String serviceId;
//	private String apiId;
//	private HttpMethod requestMethod;
//	private String query;
//	private String path;
//	private String serviceName;
//	private ServiceVersion serviceVersion;
	private String key;
	private ParameterMap parameters;
	private ParameterMap headers;
	private MultiValueMap<String, Object> body;
	private String resourcePath;
	
	public String getKey() {
		return key;
	}
	
	public void setKey(String key) {
		this.key = key;
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
	
	public MultiValueMap<String, Object> getBody() {
		return body;
	}
	
	public void setBody(MultiValueMap<String, Object> body) {
		this.body = body;
	}

	public void setResourcePath(String resourcePath) {
		this.resourcePath = resourcePath;
	}
	
	public String getResroucePath() { 
		return this.resourcePath;
	}
	
	@Override
	public String toString() {
		JsonBuilder jb = new JsonBuilder();
		jb.put("class", this.getClass());
		jb.put("key", key);
		jb.put("parameters", parameters);
		jb.put("headers", headers);
		jb.put("body", body);
		jb.put("resourcePath", resourcePath);
		return jb.toString();
	}
}
