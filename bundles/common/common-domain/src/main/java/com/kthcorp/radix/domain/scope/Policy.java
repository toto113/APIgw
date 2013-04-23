package com.kthcorp.radix.domain.scope;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.json.JSONException;
import org.json.JSONObject;

import com.kthcorp.radix.util.JsonBuilder;
import com.kthcorp.radix.util.UUIDUtils;

public class Policy implements Serializable {
	
	private static final long serialVersionUID = 5633564043380817401L;
	
	// private transient static final Logger LOG = LoggerFactoryForUuid.getLogger(Policy.class);
	
	private boolean packagePolicy;
	private byte[] id;
	private byte[] clientID;
	private byte[] packageID;
	private byte[] serviceAPIID;
	
	private String policyTypeID;
	private Map<String, String> properties;
	
	public boolean isPackagePolicy() {
		return packagePolicy;
	}
	
	public void setPackagePolicy(boolean packagePolicy) {
		this.packagePolicy = packagePolicy;
	}
	
	public byte[] getId() {
		return id;
	}
	
	public void setId(byte[] id) {
		this.id = id;
	}
	
	public byte[] getClientID() {
		return clientID;
	}
	
	public void setClientID(byte[] clientID) {
		this.clientID = clientID;
	}
	
	public byte[] getPackageID() {
		return packageID;
	}
	
	public void setPackageID(byte[] packageID) {
		this.packageID = packageID;
	}
	
	public Map<String, String> getProperties() {
		return properties;
	}
	
	public void setProperties(Map<String, String> properties) {
		this.properties = properties;
	}
	
	public byte[] getServiceAPIID() {
		return serviceAPIID;
	}
	
	public void setServiceAPIID(byte[] serviceAPIID) {
		this.serviceAPIID = serviceAPIID;
	}
	
	public JSONObject toJSONObject() throws JSONException {
		JSONObject ret = new JSONObject();
		ret.put("packagePolicy", packagePolicy);
		if(id != null) {
			ret.put("id", UUIDUtils.getString(id));
		}
		if(clientID != null) {
			ret.put("clientID", UUIDUtils.getString(clientID));
		}
		if(packageID != null) {
			ret.put("packageID", UUIDUtils.getString(packageID));
		}
		if(serviceAPIID != null) {
			ret.put("serviceAPIID", UUIDUtils.getString(serviceAPIID));
		}
		if(policyTypeID != null) {
			ret.put("policyTypeID", policyTypeID);
		}
		if(properties != null && properties.size() > 0) {
			JSONObject propertiesObj = new JSONObject();
			for(Entry<String, String> entry : properties.entrySet()) {
				propertiesObj.put(entry.getKey(), entry.getValue());
			}
			ret.put("properties", propertiesObj);
		}
		return ret;
	}
	
	public static Policy fromJSONObject(JSONObject obj) throws JSONException {
		if(obj == null) {
			return null;
		}
		Policy policy = new Policy();
		if(obj.has("id")) {
			policy.id = UUIDUtils.getBytes(obj.getString("id"));
		}
		if(obj.has("packagePolicy")) {
			policy.packagePolicy = obj.getBoolean("packagePolicy");
		}
		if(obj.has("clientID")) {
			policy.clientID = UUIDUtils.getBytes(obj.getString("clientID"));
		}
		if(obj.has("packageID")) {
			policy.packageID = UUIDUtils.getBytes(obj.getString("packageID"));
		}
		if(obj.has("serviceAPIID")) {
			policy.serviceAPIID = UUIDUtils.getBytes(obj.getString("serviceAPIID"));
		}
		if(obj.has("policyTypeID")) {
			policy.policyTypeID = obj.getString("policyTypeID");
		}
		if(obj.has("properties")) {
			JSONObject propertiesObj = obj.getJSONObject("properties");
			policy.properties = new HashMap<String, String>();
			@SuppressWarnings("unchecked")
			Iterator<Object> keys = propertiesObj.keys();
			while(keys.hasNext()) {
				Object key = keys.next();
				if(key != null) {
					if(key instanceof java.lang.String) {
						String keyInString = key.toString();
						policy.properties.put(keyInString, propertiesObj.getString(keyInString));
					}
				}
			}
		}
		return policy;
	}

	public String getPolicyTypeID() {
		return policyTypeID;
	}

	public void setPolicyTypeID(String policyTypeID) {
		this.policyTypeID = policyTypeID;
	}
	
	@Override
	public String toString() {
		JsonBuilder jb = new JsonBuilder();
		jb.put("class", this.getClass());
		jb.put("packagePolicy", packagePolicy);
		jb.put("clientID", clientID);
		jb.put("packageID", packageID);
		jb.put("serviceAPIID", serviceAPIID);
		jb.put("policyTypeID", policyTypeID);
		jb.put("properties", properties);
		return jb.toString();
	}
	
}
