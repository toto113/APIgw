package com.kthcorp.radix.domain.client;

public enum ClientKeyType {
	PRODUCTION("P"), DEVELOPMENT("D");
	
	private String code;
	
	ClientKeyType(String code) {
		this.code = code;
	}
	
	public String getCode() {
		return this.code;
	}
	
	public static ClientKeyType fromCode(String code) {
		if("P".equals(code)) {
			return ClientKeyType.PRODUCTION;
		} else if("D".equals(code)) {
			return ClientKeyType.DEVELOPMENT;
		}
		return null;
	}
}
