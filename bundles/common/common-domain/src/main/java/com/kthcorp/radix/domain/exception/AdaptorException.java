package com.kthcorp.radix.domain.exception;

import org.springframework.http.HttpStatus;

public class AdaptorException extends RadixException {

	private static final long serialVersionUID = 1890417438099045760L;
	
	private final static String myStatus = "error_on_partner";
	private final static HttpStatus myStatusCode = HttpStatus.SERVICE_UNAVAILABLE;

	public AdaptorException(HttpStatus httpStatus, String status, Throwable t) {
		super(status, t);
		setHttpStatus(httpStatus);
	}
	
	public AdaptorException(HttpStatus httpStatus, String status, String msg, Throwable t) {
		super(status, msg, t);
		setHttpStatus(httpStatus);
	}
	
	public AdaptorException(String msg) {
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