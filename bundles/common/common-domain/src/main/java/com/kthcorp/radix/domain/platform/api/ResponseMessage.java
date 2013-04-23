package com.kthcorp.radix.domain.platform.api;

import org.json.JSONException;
import org.json.JSONObject;

import com.kthcorp.radix.util.JsonBuilder;

public class ResponseMessage {
	
	private ResponseMessageHeader header;
	private ResponseMessageBody body;
	
	public ResponseMessageHeader getHeader() {
		return header;
	}
	
	public void setHeader(ResponseMessageHeader header) {
		this.header = header;
	}
	
	public ResponseMessageBody getBody() {
		return body;
	}
	
	public void setBody(ResponseMessageBody body) {
		this.body = body;
	}
	
	public String toJSONString() throws JSONException {
		if(header != null) {
			JSONObject obj = header.toJSONObject();
			
			if(body != null) {
				obj.put("body_type", body.getBodyType());
				obj.put("body", body.toJSONObject());
			}
			return obj.toString();
		}
		
		return null;
	}
	
	@Override
	public String toString() {
		JsonBuilder jb = new JsonBuilder();
		jb.put("class", this.getClass());
		jb.put("body", body);
		jb.put("header", header);
		return jb.toString();
	}
	
}
