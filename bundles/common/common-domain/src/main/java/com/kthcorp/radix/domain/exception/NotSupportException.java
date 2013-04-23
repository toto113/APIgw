package com.kthcorp.radix.domain.exception;

import org.springframework.http.HttpStatus;

public class NotSupportException extends ValidateException {
	
	private static final long serialVersionUID = 5266486728238036637L;
	private final static String myStatus = "not_support";
	private final static HttpStatus myStatusCode = HttpStatus.UNPROCESSABLE_ENTITY;
	
	public NotSupportException(String msg) {
		super(myStatus, msg);
		this.httpStatus = myStatusCode;
	}
	
	public NotSupportException(String msg, Throwable t) {
		super(myStatus, msg, t);
		this.httpStatus = myStatusCode;
	}
	
	public String getStatus() {
		return myStatus;
	}
	
	public static String getStatusMsg() {
		return myStatus;
	}
}
