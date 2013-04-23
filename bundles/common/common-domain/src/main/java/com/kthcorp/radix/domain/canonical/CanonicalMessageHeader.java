package com.kthcorp.radix.domain.canonical;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import com.kthcorp.radix.util.JsonBuilder;

public class CanonicalMessageHeader implements Serializable {

	private static final long serialVersionUID = 3237200744115993758L;

	private String messageId;
	
	private String correlationId;
	
	private Map<String, String> headerProperties = new HashMap<String, String>();
	
	public String getMessageId() {
		return messageId;
	}
	
	public void setMessageId(String messageId) {
		this.messageId = messageId;
	}
	
	public String getCorrelationId() {
		return correlationId;
	}
	
	public void setCorrelationId(String correlationId) {
		this.correlationId = correlationId;
	}
	
	public Map<String, String> getHeaderProperties() {
		return headerProperties;
	}
	
	public void setHeaderProperties(Map<String, String> headerProperties) {
		this.headerProperties = headerProperties;
	}
	
	@Override
	public String toString() {
		JsonBuilder jb = new JsonBuilder();
		jb.put("class", this.getClass());
		jb.put("messageId", messageId);
		jb.put("correlationId", correlationId);
		jb.put("headerProperties", headerProperties);
		return jb.toString();
	}
	
}
