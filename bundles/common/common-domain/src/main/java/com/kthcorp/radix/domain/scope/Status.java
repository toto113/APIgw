package com.kthcorp.radix.domain.scope;

import java.io.Serializable;
import java.util.Map;

import com.kthcorp.radix.util.JsonBuilder;

public class Status implements Serializable {
	
	private static final long serialVersionUID = 5633564043380817401L;
	
	
	private boolean packagePolicy;
	private String id;
	private String clientID;
	private String packageID;
	
	private Map<String, String> status;
	
	public boolean isPackagePolicy() {
		return packagePolicy;
	}
	
	public void setPackagePolicy(boolean packagePolicy) {
		this.packagePolicy = packagePolicy;
	}
	
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public String getClientID() {
		return clientID;
	}
	
	public void setClientID(String clientID) {
		this.clientID = clientID;
	}
	
	public String getPackageID() {
		return packageID;
	}
	
	public void setPackageID(String packageID) {
		this.packageID = packageID;
	}
	
	public Map<String, String> getStatus() {
		return status;
	}
	
	public void setStatus(Map<String, String> status) {
		this.status = status;
	}

	@Override
	public String toString() {
		JsonBuilder jb = new JsonBuilder();
		jb.put("class", this.getClass());
		jb.put("id", id);
		jb.put("clientID", clientID);
		jb.put("packageID", packageID);
		jb.put("status", status);
		jb.put("packagePolicy", packagePolicy);
		return jb.toString();
	}
}
