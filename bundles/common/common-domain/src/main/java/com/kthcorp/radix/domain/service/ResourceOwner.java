package com.kthcorp.radix.domain.service;

public enum ResourceOwner {
	USER("USER"), PARTNER("PARTNER");
	
	private String type;
	
	ResourceOwner(String type) {
		this.type = type;
	}
	
	public String getType() {
		return this.type;
	}
	
	public static ResourceOwner fromType(String type) {
		if("USER".equalsIgnoreCase(type)) {
			return ResourceOwner.USER;
		}
		if("PARTNER".equalsIgnoreCase(type)) {
			return ResourceOwner.PARTNER;
		}
		return null;
	}
}
