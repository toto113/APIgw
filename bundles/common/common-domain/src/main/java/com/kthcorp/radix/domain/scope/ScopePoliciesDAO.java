package com.kthcorp.radix.domain.scope;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.kthcorp.radix.util.JsonBuilder;

public class ScopePoliciesDAO {
	
	private byte[] clientID;
	private byte[] packageID;
	private byte[] serviceAPIID;	
	private String apiKey;
	
	private String packageParams;
	private String policyList;
	private String status;
	
	/* Derived Attributes */
	private JSONObject packageParamsObj;
	private JSONArray policyListObj;
	private JSONObject statusObj;
	
	private Date regDate;
	private Date updateDate;
	
	public byte[] getClientID() {
		return clientID;
	}
	
	public void setClientID(byte[] clientID) {
		this.clientID = clientID;
	}
	
	public String getApiKey() {
		return apiKey;
	}
	
	public void setApiKey(String apiKey) {
		this.apiKey = apiKey;
	}
	
	public String getPackageParams() {
		return packageParams;
	}
	
	public void setPackageParams(String packageParams) throws JSONException {
		this.packageParamsObj = new JSONObject(packageParams);
		this.packageParams = packageParams;
	}
	
	public String getPolicyList() {
		return policyList;
	}
	
	public void setPolicyList(List<Policy> policyList) throws JSONException {
		if(policyList == null || policyList.size() < 1) {
			return;
		}
		JSONArray obj = new JSONArray();
		for(Policy policy : policyList) {
			obj.put(policy.toJSONObject());
		}
		this.policyListObj = obj;
		this.policyList = obj.toString();
	}
	
	public void setPolicyList(String policyList) throws JSONException {
		this.policyListObj = new JSONArray(policyList);
		this.policyList = policyList;
	}
	
	public String getStatus() {
		return status;
	}
	
	public void setStatus(String status) throws JSONException {
		this.statusObj = new JSONObject(status);
		this.status = status;
	}
	
	public JSONObject getPackageParamsObj() {
		return packageParamsObj;
	}
	
	public Map<String, String> getPackageParamsMap() throws JSONException {
		if(packageParamsObj != null) {
			Map<String, String> ret = new HashMap<String, String>();
			@SuppressWarnings("unchecked")
			Iterator<Object> keys = packageParamsObj.keys();
			while(keys.hasNext()) {
				Object key = keys.next();
				if(key != null) {
					if(key instanceof java.lang.String) {
						String keyInString = key.toString();
						ret.put(keyInString, packageParamsObj.getString(keyInString));
					}
				}
			}
			return ret;
		}
		return null;
	}
	
	public void setPackageParamsObj(Map<String, String> packageParamsObj) throws JSONException {
		if(packageParamsObj == null) {
			return;
		}
		JSONObject jsonObject = new JSONObject();
		for(Entry<String, String> entry : packageParamsObj.entrySet()) {
			jsonObject.put(entry.getKey(), entry.getValue());
		}
		this.packageParamsObj = jsonObject;
		this.packageParams = jsonObject.toString();
	}
	
	public void setPackageParamsObj(JSONObject packageParamsObj) {
		this.packageParamsObj = packageParamsObj;
		this.packageParams = packageParamsObj.toString();
	}
	
	public JSONArray getPolicyListObj() {
		return policyListObj;
	}
	
	public List<Policy> getPolicyListReal() throws JSONException {
		if(policyListObj != null) {
			List<Policy> ret = new ArrayList<Policy>();
			int sizeOfList = policyListObj.length();
			for(int i = 0; i < sizeOfList; i++) {
				JSONObject policyInJSON = policyListObj.getJSONObject(i);
				ret.add(Policy.fromJSONObject(policyInJSON));
			}
			return ret;
		}
		return null;
	}
	
	public void setPolicyListObj(JSONArray policyListObj) {
		this.policyListObj = policyListObj;
		this.policyList = policyListObj.toString();
	}
	
	public JSONObject getStatusObj() {
		return statusObj;
	}
	
	public Map<String, String> getStatusMap() throws JSONException {
		Map<String, String> ret = new HashMap<String, String>();
		if(statusObj==null) { return ret; }
		@SuppressWarnings("unchecked")
		Iterator<Object> keys = statusObj.keys();
		while(keys.hasNext()) {
			Object key = keys.next();
			if(key != null) {
				if(key instanceof java.lang.String) {
					String keyInString = key.toString();
					ret.put(keyInString, statusObj.getString(keyInString));
				}
			}
		}
		return ret;
	}
	
	public void setStatusObj(Map<String, String> statusObj) throws JSONException {
		if(statusObj == null) {
			return;
		}
		JSONObject jsonObject = new JSONObject();
		for(Entry<String, String> entry : statusObj.entrySet()) {
			jsonObject.put(entry.getKey(), entry.getValue());
		}
		this.statusObj = jsonObject;
		this.status = jsonObject.toString();
	}
	
	public void setStatusObj(JSONObject statusObj) {
		this.statusObj = statusObj;
		this.status = statusObj.toString();
	}
	
	public Date getRegDate() {
		return regDate;
	}
	
	public void setRegDate(Date regDate) {
		this.regDate = regDate;
	}
	
	public Date getUpdateDate() {
		return updateDate;
	}
	
	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}
	
	public byte[] getPackageID() {
		return packageID;
	}
	
	public void setPackageID(byte[] packageID) {
		this.packageID = packageID;
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
		jb.put("packageParamsObj", packageParamsObj);
		jb.put("policyListObj", policyListObj);
		jb.put("statusObj", statusObj);
		return jb.toString();
	}
	
}