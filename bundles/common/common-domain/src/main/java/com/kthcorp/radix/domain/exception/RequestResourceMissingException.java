package com.kthcorp.radix.domain.exception;

import org.springframework.http.HttpStatus;

public class RequestResourceMissingException extends ValidateException {
	
	private static final long serialVersionUID = 6259562061338710641L;
	private static final String myStatus = "not_found_resource";
	private final static HttpStatus myStatusCode = HttpStatus.NOT_FOUND;
	
	public RequestResourceMissingException(String msg) {
		super(myStatus, msg);
		httpStatus = myStatusCode;
	}
	
	public RequestResourceMissingException(String status, String msg, Throwable t) {
		super(status, msg, t);
		httpStatus = myStatusCode;
	}	
	
	public RequestResourceMissingException(String msg, Throwable t) {
		super(myStatus, msg, t);
		httpStatus = myStatusCode;
	}
	
	public String getStatus() {
		return myStatus;
	}
	
	public static String getStatusMsg() {
		return myStatus;
	}
}
