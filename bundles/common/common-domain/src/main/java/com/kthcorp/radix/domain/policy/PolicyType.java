package com.kthcorp.radix.domain.policy;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.slf4j.Logger;
import com.kthcorp.radix.util.UuidViewableLoggerFactory;

import com.kthcorp.radix.domain.service.YesOrNo;
import com.kthcorp.radix.util.JsonBuilder;

public class PolicyType implements Serializable {
	
	private transient static final Logger LOG = UuidViewableLoggerFactory.getLogger(PolicyType.class);
	
	private static final long serialVersionUID = -265830131664355223L;
	private String id;
	private String name;
	
	private String description;
	private String properties;
	
	private List<String> propertiesObj;
	
	private Integer priority = 0;
	private YesOrNo isActivated;
	private YesOrNo isValid;
	private String invalidStatus;

	private Date regDate;
	private Date updateDate;
	
	public PolicyType() {

	}
	
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public int getPriority() {
		return priority;
	}
	
	public void setPriority(int priority) {
		this.priority = priority;
	}
	
	public YesOrNo isActivated() {
		return isActivated;
	}
	
	public void setActivated(YesOrNo isActivated) {
		this.isActivated = isActivated;
	}
	
	public Date getRegDate() {
		return regDate;
	}
	
	public void setRegDate(Date regDate) {
		this.regDate = regDate;
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) throws JSONException {
		this.description = description;
	}

	public String getProperties() {
		return properties;
	}
	
	public void setProperties(String properties) throws JSONException {
		this.properties = properties;
		this.setPropertiesObj(properties);
	}
	
	public List<String> getPropertiesObj() {
		return this.propertiesObj;
	}

	public void setPropertiesObj(String properties) throws JSONException {
		if(properties==null||properties.length()<1) {
			return;
		}
		
		JSONArray propertiesObj = new JSONArray(properties);

		if(this.propertiesObj!=null) {
			this.propertiesObj.clear();
		} else {
			this.propertiesObj = new ArrayList<String>();
		}
		for(int i=0,l=propertiesObj.length();i<l;i++) {
			this.propertiesObj.add(propertiesObj.getString(i));
		}
	}
	
	public void setPropertiesObj(List<String> propertiesObj) throws JSONException {
		if(propertiesObj==null) {
			return;
		}
		
		JSONArray obj = this.fromList(propertiesObj);
		
		if(obj!=null) {
			this.properties = obj.toString();
			this.propertiesObj = propertiesObj;
		}
	}
	
	public void addProperty(String propertyName) {
		if(this.propertiesObj==null) {
			this.propertiesObj = new ArrayList<String>();
		}
		this.propertiesObj.add(propertyName);

		try {
			JSONArray obj = this.fromList(this.propertiesObj);
			this.properties = obj.toString();
		} catch(JSONException e) {
			LOG.warn(e.getMessage());
		}
	}
	
	public void removeProperty(String key) {
		if(this.propertiesObj==null) {
			this.propertiesObj = new ArrayList<String>();
		}
		this.propertiesObj.remove(key);

		try {
			JSONArray obj = this.fromList(this.propertiesObj);
			this.properties = obj.toString();
		} catch(JSONException e) {
			LOG.warn(e.getMessage());
		}
	}
	
	private JSONArray fromList(List<String> list) throws JSONException {
		if(list==null) {
			return null;
		}
		JSONArray obj = new JSONArray();
		for(String e: list) {
			obj.put(e);
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

	public Date getUpdateDate() {
		return updateDate;
	}

	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}
	
	@Override
	public String toString() {
		JsonBuilder jb = new JsonBuilder();
		jb.put("class", this.getClass());
		jb.put("id", id);
		jb.put("name", name);
		jb.put("description", description);
		jb.put("properties", properties);
		jb.put("propertiesObj", propertiesObj);
		jb.put("priority", priority);
		jb.put("isActivated", isActivated);
		jb.put("isValid", isValid);
		jb.put("invalidStatus", invalidStatus);
		return jb.toString();
	}
	
}
