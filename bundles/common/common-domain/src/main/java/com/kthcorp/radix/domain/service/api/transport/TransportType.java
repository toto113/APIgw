package com.kthcorp.radix.domain.service.api.transport;

public enum TransportType {
	REST("REST"), FILE("FILE"), DBMS("DBMS"), SOAP("SOAP"), JMS("JMS"), AWS("AWS");
	
	private String type;
	
	TransportType(String type) {
		this.type = type;
	}
	
	public String getType() {
		return this.type;
	}
	
	public static TransportType fromType(String type) {
		if("REST".equalsIgnoreCase(type)) {
			return TransportType.REST;
		}
		if("FILE".equalsIgnoreCase(type)) {
			return TransportType.FILE;
		}
		if("DBMS".equalsIgnoreCase(type)) {
			return TransportType.DBMS;
		}
		if("SOAP".equalsIgnoreCase(type)) {
			return TransportType.SOAP;
		}
		if("JMS".equalsIgnoreCase(type)) {
			return TransportType.JMS;
		}
		if("AWS".equalsIgnoreCase(type)) {
			return TransportType.AWS;
		}
		
		return null;
	}
}