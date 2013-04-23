package com.kthcorp.radix.adaptor.rest.util;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import com.kthcorp.radix.util.UuidViewableLoggerFactory;

import com.kthcorp.radix.adaptor.rest.domain.ResponseContentType;
import com.kthcorp.radix.adaptor.rest.domain.RestResponse;

public class JsonConverter implements Converter {
	
	@SuppressWarnings("unused")
	private final Logger LOG = UuidViewableLoggerFactory.getLogger(this.getClass());
	
	private Map<String, ResponseContentType> contentTypeMap;
	private Map<String, ResponseContentType> responseContentTypeMap;
	
	private JsonConverter() {
		contentTypeMap = new HashMap<String, ResponseContentType>();
		responseContentTypeMap = new HashMap<String, ResponseContentType>();
	}
	
	@SuppressWarnings("unchecked")
	public JSONObject convert(String key, String requestContentType, Object responseBody) throws JSONException {
		
		RestResponse restResponse = new RestResponse();
		restResponse.setBody(responseBody);
		if(responseContentTypeMap.containsKey(key)) {
			restResponse.setContentType(responseContentTypeMap.get(key));
		} else {
			restResponse.setContentType(getResponseContentType(requestContentType));
		}
		
		restResponse = ConvertUtil.convert(restResponse);
		if(restResponse.getContentType() != ResponseContentType.Unknown && restResponse.getContentType() != responseContentTypeMap.get(key)) {
			responseContentTypeMap.put(key, restResponse.getContentType());
		}
		
		return restResponse.getJsonObject();		
	}
	
	private ResponseContentType getResponseContentType(String requestContentType) {
		
		if(contentTypeMap.containsKey(requestContentType)) {
			return contentTypeMap.get(requestContentType);
		} else {
			return ResponseContentType.Unknown;
		}
	}
}
