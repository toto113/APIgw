package com.kthcorp.radix.domain.scope;

import java.util.List;
import java.util.Map;

import com.kthcorp.radix.util.JsonBuilder;

public class ScopePolicies {
	private byte[] clientID;
	private byte[] packageID;
	private byte[] serviceAPIID;
	private String apiKey;
	private Map<String,String> packageParams;
	private List<Policy> policyList;
	private Map<String,String> status;
	
	public Map<String, String> getPackageParams() {
		return packageParams;
	}
	public void setPackageParams(Map<String, String> packageParams) {
		this.packageParams = packageParams;
	}
	public List<Policy> getPolicyList() {
		return policyList;
	}
	public void setPolicyList(List<Policy> policyList) {
		this.policyList = policyList;
	}
	public Map<String,String> getStatus() {
		return status;
	}
	public void setStatus(Map<String,String> status) {
		this.status = status;
	}
	public String getApiKey() {
		return apiKey;
	}
	public void setApiKey(String apiKey) {
		this.apiKey = apiKey;
	}
	public byte[] getPackageID() {
		return packageID;
	}
	public void setPackageID(byte[] packageID) {
		this.packageID = packageID;
	}
	public byte[] getClientID() {
		return clientID;
	}
	public void setClientID(byte[] clientID) {
		this.clientID = clientID;
	}
	public byte[] getServiceAPIID() {
		return serviceAPIID;
	}
	public void setServiceAPIID(byte[] serviceAPIID) {
		this.serviceAPIID = serviceAPIID;
	}
	
	@Override
	public String toString() {
		JsonBuilder jb = new JsonBuilder();
		jb.put("class", this.getClass());
		jb.put("clientID", clientID);
		jb.put("packageID", packageID);
		jb.put("serviceAPIID", serviceAPIID);
		jb.put("apiKey", apiKey);
		jb.put("packageParams", packageParams);
		jb.put("policyList", policyList);
		jb.put("status", status);
		return jb.toString();
	}
	
}