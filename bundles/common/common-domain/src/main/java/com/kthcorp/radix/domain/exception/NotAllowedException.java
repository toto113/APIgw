package com.kthcorp.radix.domain.exception;

import org.springframework.http.HttpStatus;

public class NotAllowedException extends ValidateException {
	
	private static final long serialVersionUID = -484497510394421339L;
	private final static String myStatus = "not_allowed";
	private final static HttpStatus myStatusCode = HttpStatus.PRECONDITION_FAILED;
	
	public NotAllowedException(String msg) {
		super(myStatus, msg);
		httpStatus = myStatusCode;
	}
	
	public NotAllowedException(String status, String msg) {
		super(status, msg);
		httpStatus = myStatusCode;
	}
	
	public NotAllowedException(String msg, Throwable t) {
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
