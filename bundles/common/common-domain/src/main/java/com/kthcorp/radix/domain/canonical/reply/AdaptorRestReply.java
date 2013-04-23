package com.kthcorp.radix.domain.canonical.reply;

import java.io.Serializable;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import com.kthcorp.radix.util.JsonBuilder;

public class AdaptorRestReply implements Serializable, AdaptorReply {

	private static final long serialVersionUID = 2908591927196193334L;
	private String jsonText;
	private byte[] body;
	private HttpStatus httpStatus;
	private HttpHeaders httpHeaders;
	private long contentLength;
	private MediaType mediaType;
	
	@Deprecated
	public String getJsonText() {
		return jsonText;
	}
	
	@Deprecated
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

	public MediaType getMediaType() {
		return mediaType;
	}
	
	public void setMediaType(MediaType mediaType) {
		this.mediaType = mediaType;
	}
	
	public long getContentLength() {
		return contentLength;
	}

	public void setContentLength(long contentLength) {
		this.contentLength = contentLength;
	}
	
	@Override 
	public String toString() {
		String bodyConsice = InnerUtil.getBodyConsice(body);
		JsonBuilder jb = new JsonBuilder();
		jb.put("class", this.getClass());
		jb.put("httpStatus", httpStatus);
		jb.put("httpHeaders", httpHeaders);
		jb.put("contentLength", contentLength);
		jb.put("mediaType", mediaType);
		jb.put("jsonText", jsonText);
		jb.put("body", bodyConsice);
		return jb.toString();
	}
	

	private static class InnerUtil {

		public static String getBodyConsice(byte[] bodyBytes) {
			if(bodyBytes==null) { bodyBytes = new byte[0]; }
			int sampleLength = 100;
			if(bodyBytes.length<sampleLength) {
				sampleLength = bodyBytes.length;
			}
			String sample = null;
			
			@SuppressWarnings("unused")
			String[] candidateCharactersets = { "utf-8", "euc_kr" };
			String bodyString = "";
			boolean decodingSuccess = false;
			try {
				bodyString = new String(bodyBytes);
				decodingSuccess = true;
			} catch(Throwable e) {
				// 어짜피 bytes가 어떤 charset으로 인코딩되었는 지 모른다. 알 방법도 없다.
				// ignore
			}
			if(bodyString.length()<sampleLength) {
				sampleLength = bodyString.length();
			}
			sample = bodyString.substring(0, sampleLength);
			
			
			
			String name = null;
			String value = null;
			if(decodingSuccess) {
				name = "fisrt_"+sampleLength+"_Strings";
				value = sample;
			}
			else {
				name = "value";
				value = "NOT_DECODABLE_DATA";
			}

			JsonBuilder jb = new JsonBuilder();
			jb.put("size", bodyBytes.length);
			jb.put(name, value);
			
			return jb.toString();
			
		}

	}
	
}
