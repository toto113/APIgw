package com.kthcorp.radix.domain.platform.api;

import java.util.UUID;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;

import com.kthcorp.radix.util.JsonBuilder;

public class ResponseMessageHeader {
	
	private String messageId;
	private HttpStatus httpStatus;
	private String status;
	private String message;
	
	public ResponseMessageHeader() {
		this.messageId = UUID.randomUUID().toString();
		this.httpStatus = HttpStatus.OK;
	}
	
	public String getMessageId() {
		return messageId;
	}
	
	public void setMessageId(String messageId) {
		this.messageId = messageId;
	}
	
	public String getStatus() {
		return status;
	}
	
	public void setStatus(String status) {
		this.status = status;
	}
	
	public void setStatus(HttpStatus httpStatus, String status) {
		this.status = status;
		this.httpStatus = httpStatus;
	}
	
	public JSONObject toJSONObject() throws JSONException {
		if(messageId == null) {
			messageId = UUID.randomUUID().toString();
		}
		JSONObject returnObj = new JSONObject();
		returnObj.put("messageId", messageId);
		returnObj.put("status", status);
		returnObj.put("message", message);
		return returnObj;
	}
	
	public String getMessage() {
		return message;
	}
	
	public void setMessage(String message) {
		this.message = message;
	}
	
	public HttpStatus getHttpStatus() {
		return httpStatus;
	}
	
	public void setHttpStatus(HttpStatus httpStatus) {
		this.httpStatus = httpStatus;
	}
	
	@Override
	public String toString() {
		JsonBuilder jb = new JsonBuilder();
		jb.put("class", this.getClass());
		jb.put("messageId", messageId);
		jb.put("httpStatus", httpStatus);
		jb.put("status", status);
		jb.put("message", message);
		return jb.toString();
	}
	
}
