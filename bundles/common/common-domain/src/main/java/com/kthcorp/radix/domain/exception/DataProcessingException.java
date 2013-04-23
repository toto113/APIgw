package com.kthcorp.radix.domain.exception;

import org.springframework.http.HttpStatus;

public class DataProcessingException extends RadixException {
	
	private static final long serialVersionUID = -1305460760193962115L;
	private final static String myStatus = "error_on_dataProcessing";
	private final static HttpStatus myStatusCode = HttpStatus.INTERNAL_SERVER_ERROR;
	
	public DataProcessingException(String msg) {
		super(myStatus, msg);
		httpStatus = myStatusCode;
	}
	
	public DataProcessingException(String msg, Throwable t) {
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
