package com.kthcorp.radix.adaptor.rest.domain;

import org.json.JSONObject;

public class RestResponse {

	private ResponseContentType contentType;
	private Object body;
	private JSONObject jsonObject;
	
	public ResponseContentType getContentType() {
		return contentType;
	}

	public void setContentType(ResponseContentType contentType) {
		this.contentType = contentType;
	}	
	
	public Object getBody() {
		return body;
	}

	
	public void setBody(Object body) {
		this.body = body;
	}

	public JSONObject getJsonObject() {
		return jsonObject;
	}
	
	public void setJsonObject(JSONObject jsonObject) {
		this.jsonObject = jsonObject;
	}
}
