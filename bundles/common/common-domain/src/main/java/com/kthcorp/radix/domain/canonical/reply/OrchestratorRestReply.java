package com.kthcorp.radix.domain.canonical.reply;

import java.io.Serializable;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import com.kthcorp.radix.util.JsonBuilder;

public class OrchestratorRestReply implements Serializable, OrchestratorReply {

	private static final long serialVersionUID = -2530108299581903211L;
	private String jsonText;
	private byte[] body;
	private HttpStatus httpStatus;
	private HttpHeaders httpHeaders;
	private long contentLength;
	private MediaType mediaType;
	
	public String getJsonText() {
		return jsonText;
	}
	
	public void setJsonText(String jsonText) {
		this.jsonText = jsonText;
	}
	
	public byte[] getBody() {
		return body;
	}
	
	public void setBody(byte[] body) {
		this.body = body;
	}	
	
	public HttpStatus getHttpStatus() {
		return httpStatus;
	}

	
	public void setHttpStatus(HttpStatus httpStatus) {
		this.httpStatus = httpStatus;
	}

	
	public HttpHeaders getHttpHeaders() {
		return httpHeaders;
	}

	
	public void setHttpHeaders(HttpHeaders httpHeaders) {
		this.httpHeaders = httpHeaders;
	}

	public long getContentLength() {
		return contentLength;
	}
	
	public void setContentLength(long contentLength) {
		this.contentLength = contentLength;
	}
	
	public MediaType getMediaType() {
		return mediaType;
	}
	
	public void setMediaType(MediaType mediaType) {
		this.mediaType = mediaType;
	}
	
	@Override 
	public String toString() {
		JsonBuilder jb = new JsonBuilder();
		jb.put("class", this.getClass());
		jb.put("jsonText", jsonText);
		jb.put("body", body);
		jb.put("httpStatus", httpStatus);
		jb.put("httpHeaders", httpHeaders);
		jb.put("contentLength", contentLength);
		jb.put("mediaType", mediaType);
		return jb.toString();
	}

}
