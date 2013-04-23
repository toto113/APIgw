package com.kthcorp.radix.domain.canonical;

import java.io.Serializable;

import com.kthcorp.radix.util.JsonBuilder;

public class Fault implements Serializable {

	private static final long serialVersionUID = -3021928422465962208L;

	private String faultCode;
	
	private String faultMessage;
	
	public String getFaultCode() {
		return faultCode;
	}
	
	public void setFaultCode(String faultCode) {
		this.faultCode = faultCode;
	}
	
	public String getFaultMessage() {
		return faultMessage;
	}
	
	public void setFaultMessage(String faultMessage) {
		this.faultMessage = faultMessage;
	}
	
	@Override 
	public String toString() {
		JsonBuilder jb = new JsonBuilder();
		jb.put("class", this.getClass());
		jb.put("faultCode", faultCode);
		jb.put("faultMessage", faultMessage);
		return jb.toString();
	}
}
