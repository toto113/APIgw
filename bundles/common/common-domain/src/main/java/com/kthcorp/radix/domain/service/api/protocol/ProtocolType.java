package com.kthcorp.radix.domain.service.api.protocol;

public enum ProtocolType {
	
	HTTP_v1_1("HTTP1.1"), HTTP_v1_0("HTTP1.0"), FTP("FTP"), SSH("SSH");
	
	private String code;
	
	ProtocolType(String code) {
		this.code = code;
	}
	
	public String getCode() {
		return this.code;
	}
	
	public static ProtocolType fromCode(String code) {
		if("HTTP1.1".equalsIgnoreCase(code)) {
			return ProtocolType.HTTP_v1_1;
		}
		if("HTTP1.0".equalsIgnoreCase(code)) {
			return ProtocolType.HTTP_v1_0;
		}
		if("FTP".equalsIgnoreCase(code)) {
			return ProtocolType.FTP;
		}
		if("SSH".equalsIgnoreCase(code)) {
			return ProtocolType.SSH;
		}
		
		return null;
	}
}