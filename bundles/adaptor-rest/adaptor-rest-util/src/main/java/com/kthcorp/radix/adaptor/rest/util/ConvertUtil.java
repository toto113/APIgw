package com.kthcorp.radix.adaptor.rest.util;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;
import org.slf4j.Logger;
import com.kthcorp.radix.util.UuidViewableLoggerFactory;

import com.kthcorp.radix.adaptor.rest.domain.ResponseContentType;
import com.kthcorp.radix.adaptor.rest.domain.RestResponse;

public class ConvertUtil {
	
	private static final Logger LOG = UuidViewableLoggerFactory.getLogger(ConvertUtil.class);
	private static final String MESSAGE_FORMAT = "{\"message\":\"%s\"}";
	
	public static RestResponse convert(RestResponse restResponse) throws JSONException {
		
		LOG.debug("contentType={}", restResponse.getContentType());
		LOG.debug("responseBody={}", restResponse.getBody());
		
		JSONObject jsonObj = null;
		if(restResponse.getContentType() == ResponseContentType.Json) {
			jsonObj = jsonToJson((String) restResponse.getBody());
		} else if(restResponse.getContentType() == ResponseContentType.Xml) {
			jsonObj = xmlToJson((String) restResponse.getBody());
		} else if(restResponse.getContentType() == ResponseContentType.Plain) {
			jsonObj = plainToJson((String) restResponse.getBody());
		}
		
		if(jsonObj == null) {
			try {
				jsonObj = jsonToJson((String) restResponse.getBody());
				restResponse.setContentType(ResponseContentType.Json);
			} catch(JSONException e) {
				try {
					jsonObj = xmlToJson((String) restResponse.getBody());
					restResponse.setContentType(ResponseContentType.Xml);
					if(jsonObj.length() == 0) {
						jsonObj = plainToJson((String) restResponse.getBody());
						restResponse.setContentType(ResponseContentType.Plain);
					}
				} catch(JSONException e1) {
					jsonObj = plainToJson((String) restResponse.getBody());
					restResponse.setContentType(ResponseContentType.Plain);
				}
			}
		}
		
		LOG.debug("jsonObj={}", jsonObj);
		restResponse.setJsonObject(jsonObj);
		return restResponse;
	}
	
	private static JSONObject jsonToJson(String src) throws JSONException {
		
		JSONObject jsonObj = new JSONObject(src);
		return jsonObj;
	}
	
	private static JSONObject xmlToJson(String src) throws JSONException {
		
		JSONObject jsonObj = XML.toJSONObject(src);
		return jsonObj;
	}
	
	private static JSONObject plainToJson(String src) throws JSONException {
		
		JSONObject jsonObj = new JSONObject(String.format(MESSAGE_FORMAT, src));
		return jsonObj;
	}
}
