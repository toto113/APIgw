package com.kthcorp.radix.domain.exception;

import org.springframework.http.HttpStatus;

public class PolicyException extends RadixException {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -720446098755308081L;
	
	private final static String myStatus = "error_on_policy";
	private final static HttpStatus myStatusCode = HttpStatus.NOT_ACCEPTABLE;
	
	public PolicyException(String msg) {
		super(myStatus, msg);
		setHttpStatus(myStatusCode);
	}
	
	public PolicyException(String msg, Throwable t) {
		super(myStatus, msg, t);
		httpStatus = myStatusCode;
	}
	
	public PolicyException(String status, String msg) {
		super(status, msg);
		httpStatus = myStatusCode;
	}
	
	public PolicyException(String status, String msg, Throwable t) {
		super(status, msg, t);
		httpStatus = myStatusCode;
	}
	
	public PolicyException(Throwable t) {
		super(t);
		httpStatus = myStatusCode;
	}
	
	public String getStatus() {
		return myStatus;
	}
	
	public static String getStatusMsg() {
		return myStatus;
	}
}
