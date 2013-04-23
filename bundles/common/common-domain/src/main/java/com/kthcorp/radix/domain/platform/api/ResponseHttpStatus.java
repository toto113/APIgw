package com.kthcorp.radix.domain.platform.api;

public enum ResponseHttpStatus {
	SUCCESS(200), INVALID_REQUEST(400), PROCESSING_ERROR(500);
	
	private int statusCode;
	
	ResponseHttpStatus(int statusCode) {
		this.statusCode = statusCode;
	}
	
	public int getCode() {
		return statusCode;
	}
}
