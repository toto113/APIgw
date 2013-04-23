package com.kthcorp.radix.domain.platform.api;

import org.slf4j.Logger;
import com.kthcorp.radix.util.UuidViewableLoggerFactory;

public enum RequestMessageType {
	
	APPLICATION_JSON("application/json"), APPLICATION_X_WWW_FORM_URLENCODED("application/x-www-form-urlencoded"), APPLICATION_XML("application/xml");
	
	private String type;
	private static final Logger LOG = UuidViewableLoggerFactory.getLogger(RequestMessageType.class);
	
	RequestMessageType(String type) {
		this.type = type;
	}
	
	public String getType() {
		return this.type;
	}
	
	public static RequestMessageType fromCode(String typeString) {
		String typeString2 = typeString;
		if(typeString2 != null) {
			typeString2 = typeString2.split(";")[0];
		}
		
		if(typeString2 != null) {
			if("application/json".equals(typeString2.toLowerCase())) {
				return RequestMessageType.APPLICATION_JSON;
			} else if("application/x-www-form-urlencoded".equals(typeString2.toLowerCase())) {
				return RequestMessageType.APPLICATION_X_WWW_FORM_URLENCODED;
			} else if("application/xml".equals(typeString2.toLowerCase())) {
				return RequestMessageType.APPLICATION_XML;
			} else {
				LOG.error("Not support contentType->" + typeString2);
			}
		} else {
			LOG.error("typeString is null");
		}
		
		return null;
	}
}
