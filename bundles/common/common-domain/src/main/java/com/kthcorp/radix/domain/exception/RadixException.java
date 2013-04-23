package com.kthcorp.radix.domain.exception;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.http.HttpStatus;

public class RadixException extends Exception {
	
	private static final long serialVersionUID = -3582698847663956352L;
	
	protected HttpStatus httpStatus = HttpStatus.SERVICE_UNAVAILABLE;
	
	private Date occuredDate = new Date();
	
	public Date getOccuredDate() {
		return occuredDate;
	}
	
	public String getOccuredDateAsString(String dateFormat) {
		
		SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
		
		return sdf.format(occuredDate);
	}
	
	public void setOccuredDate(Date occuredDate) {
		this.occuredDate = occuredDate;
	}
	
	private String status;
	
	public RadixException(String status, String msg) {
		super(msg);
		this.status = status;
	}
	
	public RadixException(String status, String msg, Throwable t) {
		super(msg, t);
		this.status = status;
	}
	
	public RadixException(String status) {
		super(status);
		this.status = status;
	}
	
	public RadixException(String msg, Throwable t) {
		super(msg, t);
		this.status = msg;
	}
	
	public RadixException(Throwable t) {
		super(t);
	}
	
	public RadixException(String msg, HttpStatus httpStatus, Throwable t) {
		super(msg, t);
		this.httpStatus = httpStatus;
	}
	
	public RadixException(String msg, HttpStatus httpStatus) {
		super(msg);
		this.httpStatus = httpStatus;
	}
	
	public HttpStatus getHttpStatus() {
		return httpStatus;
	}
	
	public void setHttpStatus(HttpStatus httpStatus) {
		this.httpStatus = httpStatus;
	}
	
	public String getStatus() {
		return status;
	}
	
	public void setStatus(String status) {
		this.status = status;
	}
	
}