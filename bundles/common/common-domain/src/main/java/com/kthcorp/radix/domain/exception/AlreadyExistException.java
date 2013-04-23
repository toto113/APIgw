package com.kthcorp.radix.domain.exception;

import org.springframework.http.HttpStatus;

public class AlreadyExistException extends ValidateException {
	
	private static final long serialVersionUID = -7403544864344409349L;
	
	private final static String myStatus = "already_exists";
	private final static HttpStatus myStatusCode = HttpStatus.UNPROCESSABLE_ENTITY;
	
	public AlreadyExistException(HttpStatus httpStatus, String msg) {
		super(myStatus, msg);
		this.httpStatus = httpStatus;
	}
	
	public AlreadyExistException(HttpStatus httpStatus, String msg, Throwable t) {
		super(myStatus, msg, t);
		this.httpStatus = httpStatus;
	}
	
	public AlreadyExistException(String msg) {
		super(myStatus, msg);
		this.httpStatus = myStatusCode;
	}
	
	public AlreadyExistException(String msg, Throwable t) {
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
