package com.kthcorp.radix.domain.exception;

import org.springframework.http.HttpStatus;

public class NotFoundException extends ValidateException {
	
	private static final long serialVersionUID = 5266486728238036637L;
	private final static String myStatus = "not_found";
	private final static HttpStatus myStatusCode = HttpStatus.NOT_FOUND;
	
	public NotFoundException(String msg) {
		super(myStatus, msg);
		httpStatus = myStatusCode;
	}
	
	public NotFoundException(String msg, Throwable t) {
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
