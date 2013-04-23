package com.kthcorp.radix.domain.exception;

import org.springframework.http.HttpStatus;

public class PolicyExpiredException extends PolicyException {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -720446098755308081L;
	
	private final static String myStatus = "expired_of_policy";
	private final static HttpStatus myStatusCode = HttpStatus.NOT_ACCEPTABLE;
	
	public PolicyExpiredException(HttpStatus httpStatus, String status, Throwable t) {
		super(status, t);
		setHttpStatus(httpStatus);
	}
	
	public PolicyExpiredException(HttpStatus httpStatus, String status, String msg, Throwable t) {
		super(status, msg, t);
		setHttpStatus(httpStatus);
	}
	
	public PolicyExpiredException(String msg) {
		super(myStatus, msg);
		setHttpStatus(myStatusCode);
	}
	
	public String getStatus() {
		return myStatus;
	}
	
	public static String getStatusMsg() {
		return myStatus;
	}
}
