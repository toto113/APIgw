package com.kthcorp.radix.domain.service;

import java.io.Serializable;

public class ServiceVersion implements Serializable {
	
	private static final long serialVersionUID = -3860487924969087462L;
	
	private int versionNumber;
	
	public ServiceVersion() {
		
		this.versionNumber = 0;
	}
	
	public ServiceVersion(String version) throws NumberFormatException {
		
		if(version != null) {
			this.versionNumber = Integer.parseInt(version);
		}
	}
	
	@Override
	public String toString() {
		return String.valueOf(versionNumber);
	}
	
	public int getVersionNumber() {
		return versionNumber;
	}
	
	public void setVersionNumber(int versionNumber) {
		this.versionNumber = versionNumber;
	}
	
	public int compare(ServiceVersion b) {
		return this.versionNumber - b.getVersionNumber();
	}
}