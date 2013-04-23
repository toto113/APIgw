package com.kthcorp.radix.domain.exception;

import org.springframework.http.HttpStatus;

public class ValidateException extends RadixException {
	
	private static final long serialVersionUID = -985179794625412760L;
	
	private final static String myStatus = "invalid_data";
	private final static HttpStatus myStatusCode = HttpStatus.UNPROCESSABLE_ENTITY;
	
	public ValidateException(String msg) {
		super(myStatus, msg);
		httpStatus = myStatusCode; 
	}
	
	public ValidateException(String msg, Throwable t) {
		super(myStatus, msg, t);
		httpStatus = myStatusCode; 
	}
	
	public ValidateException(String status, String msg) {
		super(status, msg);
		httpStatus = myStatusCode; 
	}
	
	public ValidateException(String status, String msg, Throwable t) {
		super(status, msg, t);
		httpStatus = myStatusCode; 
	}
	
	public ValidateException(Throwable t) {
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
