package com.kthcorp.radix.domain.client;

import java.io.Serializable;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import com.kthcorp.radix.util.UuidViewableLoggerFactory;

import com.fasterxml.uuid.EthernetAddress;
import com.fasterxml.uuid.Generators;
import com.fasterxml.uuid.impl.TimeBasedGenerator;
import com.kthcorp.radix.domain.packages.Parameters;
import com.kthcorp.radix.domain.policy.Policy;
import com.kthcorp.radix.domain.service.YesOrNo;
import com.kthcorp.radix.domain.service.api.serviceAPI.ServiceAPI;
import com.kthcorp.radix.util.JsonBuilder;
import com.kthcorp.radix.util.UUIDUtils;

public class CPackages implements Serializable {

	private static final long serialVersionUID = -5594494545595859977L;

	private transient static final Logger LOG = UuidViewableLoggerFactory.getLogger(CPackages.class);
	
	private byte[] id;
	private byte[] clientID;
	private byte[] packageID;
	
	private List<Policy> policyList;
	private List<ServiceAPI> serviceApiList;
	
	private Date regDate;
	private Date updateDate;
	private Date removeDate;
	private YesOrNo isValid;
	private String invalidStatus;
	
	private String parameters;
	
	/* Derived attributes from JSON Object */
	private Parameters parametersObj;
	
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

	public byte[] getClientID() {
		return clientID;
	}

	public void setClientID(byte[] clientID) {
		this.clientID = clientID;
	}
	
	public List<Policy> getPolicyList() {
		return policyList;
	}
	
	public void setPolicyList(List<Policy> policyList) {
		this.policyList = policyList;
	}
	
	public List<ServiceAPI> getServiceApiList() {
		return serviceApiList;
	}
	
	public void setServiceApiList(List<ServiceAPI> serviceApiList) {
		this.serviceApiList = serviceApiList;
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
	
	public Date getRemoveDate() {
		return removeDate;
	}
	
	public void setRemoveDate(Date removeDate) {
		this.removeDate = removeDate;
	}
	
	public YesOrNo getIsValid() {
		return isValid;
	}
	
	public void setIsValid(YesOrNo isValid) {
		this.isValid = isValid;
	}
	
	public String getInvalidStatus() {
		return invalidStatus;
	}
	
	public void setInvalidStatus(String invalidStatus) {
		this.invalidStatus = invalidStatus;
	}
	
	public String getJSONParameters() {
		return parameters;
	}
	
	public String getParameters() {
		if(parameters != null) {
			return parameters.toString();
		}
		return null;
	}
	
	public void setParameters(String parameters) throws JSONException {
		this.parameters = parameters;
		this.parametersObj = Parameters.fromJSONObject(new JSONObject(this.parameters));
	}
	
	public Parameters getParametersObj() {
		return parametersObj;
	}
	
	public void setParametersObj(Parameters parametersObj) throws JSONException {
		this.parametersObj = parametersObj;
		this.parameters = parametersObj.toJSONString();
	}

	public byte[] getPackageID() {
		return packageID;
	}

	public void setPackageID(byte[] packageID) {
		this.packageID = packageID;
	}
	
	@Override 
	public String toString() {
		JsonBuilder jb = new JsonBuilder();
		jb.put("class", this.getClass());
		jb.put("id", id);
		jb.put("clientID", clientID);
		jb.put("packageID", packageID);
		jb.put("policyList", policyList);
		jb.put("serviceApiList", serviceApiList);
		jb.put("parameters", parameters);
		jb.put("parametersObj", parametersObj);
		return jb.toString();
	}
	
}
