package com.kthcorp.radix.domain.exception;

import org.springframework.http.HttpStatus;

public class MethodFailureException extends ValidateException {
	
	private static final long serialVersionUID = 8358537356107264933L;
	private static final String myStatus = "invalid_method";
	private final static HttpStatus myStatusCode = HttpStatus.METHOD_FAILURE;
	
	public MethodFailureException(String msg) {
		super(myStatus, msg);
		httpStatus = myStatusCode;
	}
	
	public MethodFailureException(String status, String msg) {
		super(status, msg);
		httpStatus = myStatusCode;
	}
	
	public MethodFailureException(String msg, Throwable t) {		
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
