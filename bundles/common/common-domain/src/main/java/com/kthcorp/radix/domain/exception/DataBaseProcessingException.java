package com.kthcorp.radix.domain.exception;

import org.springframework.http.HttpStatus;

public class DataBaseProcessingException extends RadixException {
	
	private static final long serialVersionUID = -1305460760193962575L;
	private final static String myStatus = "error_on_serverside";
	private final static HttpStatus myStatusCode = HttpStatus.INTERNAL_SERVER_ERROR;
	
	public DataBaseProcessingException(String msg) {
		super(myStatus, msg);
		httpStatus = myStatusCode;
	}
	
	public DataBaseProcessingException(String msg, Throwable t) {
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
