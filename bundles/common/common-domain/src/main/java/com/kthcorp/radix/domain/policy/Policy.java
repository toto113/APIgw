package com.kthcorp.radix.domain.policy;

import java.io.Serializable;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import com.kthcorp.radix.util.UuidViewableLoggerFactory;

import com.fasterxml.uuid.EthernetAddress;
import com.fasterxml.uuid.Generators;
import com.fasterxml.uuid.impl.TimeBasedGenerator;
import com.kthcorp.radix.domain.service.YesOrNo;
import com.kthcorp.radix.util.JsonBuilder;
import com.kthcorp.radix.util.UUIDUtils;

public class Policy implements Serializable {
	
	private transient static final Logger LOG = UuidViewableLoggerFactory.getLogger(Policy.class);
	
	private static final long serialVersionUID = -265830131664355225L;
	private byte[] id;
	
	private byte[] businessPlatformID;
	private String partnerID;
	
	private byte[] packageID;
	private byte[] serviceAPIID;
	private String name;
	
	private String policyTypeID;
	private PolicyType policyType;
	
	private String properties;
	
	private Map<String,String> propertiesObj;
	
	private YesOrNo isValid;
	private String invalidStatus;
	private Date regDate;
	private Date updateDate;
	
	public Policy() {
		
	}
	
	/**
	 * Generate ID
	 * 
	 * ID (in bytes, from KeyString)
	 * KeyString (in string, UUID v1, timestamp in UTC & mac address based, used as client_id from oAuth2)
	 * Secret (password from oAuth2, used to get access/refresh-token)
	 * 
	 * @throws NoSuchAlgorithmException 
	 */
	public void generateID() throws NoSuchAlgorithmException {
		
		EthernetAddress ethernetAddress = EthernetAddress.fromInterface();
		TimeBasedGenerator generator = Generators.timeBasedGenerator(ethernetAddress);
		UUID newID = generator.generate();

		this.id = UUIDUtils.getBytes(newID);

		if(LOG.isTraceEnabled()) {
			LOG.trace("Generated ID->"+newID+"(v1,length->"+this.id.length+"bytes)");
		}
	}
	
	public byte[] getId() {
		return id;
	}
	
	public void setId(byte[] id) {
		this.id = id;
	}

	public byte[] getPackageID() {
		return packageID;
	}

	public void setPackageID(byte[] packageID) {
		this.packageID = packageID;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public Date getRegDate() {
		return regDate;
	}
	
	public void setRegDate(Date regDate) {
		this.regDate = regDate;
	}

	public String getProperties() {
		return properties;
	}
	
	public void setProperties(String properties) throws JSONException {
		this.properties = properties;
		this.setPropertiesObj(properties);
	}
	
	public Map<String,String> getPropertiesObj() {
		return this.propertiesObj;
	}

	public void setPropertiesObj(String properties) throws JSONException {
		if(properties==null||properties.length()<1) {
			return;
		}
		
		JSONObject descriptionObj = new JSONObject(properties);
		@SuppressWarnings("unchecked")
		Iterator<Object> keys = descriptionObj.keys();

		if(this.propertiesObj!=null) {
			this.propertiesObj.clear();
		} else {
			this.propertiesObj = new HashMap<String,String>();
		}
		while(keys.hasNext()) {
			Object key = keys.next();
			if(key!=null) {
				String keyInString = key.toString();
				Object value = descriptionObj.get(keyInString);
				if(value!=null) {
					this.propertiesObj.put(keyInString, value.toString());
				}
			}
		}
	}
	
	public void setPropertiesObj(Map<String,String> propertiesObj) throws JSONException {
		if(propertiesObj==null) {
			return;
		}
		
		JSONObject obj = this.fromMap(propertiesObj);
		
		if(obj!=null) {
			this.properties = obj.toString();
			this.propertiesObj = propertiesObj;
		}
	}
	
	public void addProperty(String key, String value) {
		if(this.propertiesObj==null) {
			this.propertiesObj = new HashMap<String,String>();
		}
		this.propertiesObj.put(key, value);

		try {
			JSONObject obj = this.fromMap(this.propertiesObj);
			this.properties = obj.toString();
		} catch(JSONException e) {
			LOG.warn(e.getMessage());
		}
	}
	
	public void removeProperty(String key) {
		if(this.propertiesObj==null) {
			this.propertiesObj = new HashMap<String,String>();
		}
		this.propertiesObj.remove(key);

		try {
			JSONObject obj = this.fromMap(this.propertiesObj);
			this.properties = obj.toString();
		} catch(JSONException e) {
			LOG.warn(e.getMessage());
		}
	}
	
	private JSONObject fromMap(Map<String,String> map) throws JSONException {
		if(map==null) {
			return null;
		}
		JSONObject obj = new JSONObject();
		for(Entry<String,String> entry: propertiesObj.entrySet()) {
			obj.put(entry.getKey(), entry.getValue());
		}
		return obj;
	}

	public String getInvalidStatus() {
		return invalidStatus;
	}

	public void setInvalidStatus(String invalidStatus) {
		this.invalidStatus = invalidStatus;
	}

	public YesOrNo getIsValid() {
		return isValid;
	}

	public void setIsValid(YesOrNo isValid) {
		this.isValid = isValid;
	}

	public byte[] getServiceAPIID() {
		return serviceAPIID;
	}

	public void setServiceAPIID(byte[] serviceAPIID) {
		this.serviceAPIID = serviceAPIID;
	}

	public PolicyType getPolicyType() {
		return policyType;
	}

	public void setPolicyType(PolicyType policyType) {
		this.policyType = policyType;
	}

	public Date getUpdateDate() {
		return updateDate;
	}

	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}

	public String getPartnerID() {
		return partnerID;
	}

	public void setPartnerID(String partnerID) {
		this.partnerID = partnerID;
	}

	public byte[] getBusinessPlatformID() {
		return businessPlatformID;
	}

	public void setBusinessPlatformID(byte[] businessPlatformID) {
		this.businessPlatformID = businessPlatformID;
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
		jb.put("id", id);
		jb.put("businessPlatformID", businessPlatformID);
		jb.put("partnerID", partnerID);
		jb.put("packageID", packageID);
		jb.put("serviceAPIID", serviceAPIID);
		jb.put("name", name);
		jb.put("policyTypeID", policyTypeID);
		jb.put("properties", properties);
		return jb.toString();
	}
}
