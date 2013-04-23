package com.kthcorp.radix.domain.service.api.serviceAPI;

import java.io.Serializable;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import com.kthcorp.radix.util.UuidViewableLoggerFactory;

import com.fasterxml.uuid.EthernetAddress;
import com.fasterxml.uuid.Generators;
import com.fasterxml.uuid.impl.TimeBasedGenerator;
import com.kthcorp.radix.domain.exception.NotSupportException;
import com.kthcorp.radix.domain.policy.Policy;
import com.kthcorp.radix.domain.service.YesOrNo;
import com.kthcorp.radix.domain.service.api.Parameters;
import com.kthcorp.radix.domain.service.api.protocol.Protocol;
import com.kthcorp.radix.domain.service.api.protocol.ProtocolFactory;
import com.kthcorp.radix.domain.service.api.protocol.ProtocolMode;
import com.kthcorp.radix.domain.service.api.protocol.ProtocolType;
import com.kthcorp.radix.domain.service.api.transform.TransformType;
import com.kthcorp.radix.domain.service.api.transport.TransportType;
import com.kthcorp.radix.domain.service.routing.RoutingMethodType;
import com.kthcorp.radix.util.JsonBuilder;
import com.kthcorp.radix.util.UUIDUtils;

public class ServiceAPI implements Serializable {
	
	private static final long serialVersionUID = 5924698987394829984L;
	private transient static final Logger LOG = UuidViewableLoggerFactory.getLogger(ServiceAPI.class);
	
	/* identification */
	private byte[] id;
	private byte[] serviceID;
	
	/* Attributes */
	private String apiKey;
	private String name;
	private RoutingMethodType routingMethodType = RoutingMethodType.DIRECT;
	private TransportType transportType;
	private ProtocolType protocolType;
	private TransformType defaultTransformType = TransformType.JSON;
	private String protocolMeta;
	private String parameters;
	
	private List<Policy> policyList = new ArrayList<Policy>();
	
	private Date regDate;
	private Date updateDate;
	private Date removeDate;
	
	private YesOrNo isValid;
	private String invalidStatus;
	
	/* Derived attributes from JSON Object */
	private Protocol protocolObj;
	private Parameters parametersObj;
	
	/* Setter & Getter */
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
	
	public byte[] getServiceID() {
		return serviceID;
	}
	
	public void setServiceID(byte[] serviceID) {
		this.serviceID = serviceID;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public RoutingMethodType getRoutingMethodType() {
		return routingMethodType;
	}
	
	public void setRoutingMethodType(RoutingMethodType routingMethodType) {
		this.routingMethodType = routingMethodType;
	}
	
	public TransportType getTransportType() {
		return transportType;
	}
	
	public void setTransportType(TransportType transportType) {
		this.transportType = transportType;
	}
	
	public ProtocolType getProtocolType() {
		return protocolType;
	}
	
	public void setProtocolType(ProtocolType protocolType) {
		this.protocolType = protocolType;
	}
	
	public String getProtocolMeta() {
		return protocolMeta;
	}
	
	public void setProtocolMeta(String protocolMeta) throws JSONException, NotSupportException {
		this.protocolMeta = protocolMeta;
		this.protocolObj = ProtocolFactory.createProtocol(protocolType, ProtocolMode.SERVER);
		this.protocolObj.fromJSONObject(new JSONObject(protocolMeta));
	}
	
	public String getParameters() {
		return parameters;
	}
	
	public void setParameters(String parameters) throws JSONException {
		this.parameters = parameters;
		this.parametersObj = Parameters.fromJSONArray(new JSONArray(this.parameters));
	}
	
	public TransformType getDefaultTransformType() {
		return defaultTransformType;
	}
	
	public void setDefaultTransformType(TransformType defaultTransformType) {
		this.defaultTransformType = defaultTransformType;
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
	
	public Protocol getProtocolObj() {
		return protocolObj;
	}
	
	public void setProtocolObj(Protocol protocolObj) throws JSONException {
		this.protocolObj = protocolObj;
		this.protocolMeta = protocolObj.toJSONString();
	}
	
	public Parameters getParametersObj() {
		return parametersObj;
	}
	
	public void setParametersObj(Parameters parametersObj) throws JSONException {
		this.parametersObj = parametersObj;
		this.parameters = parametersObj.toJSONString();
	}
	
	public List<Policy> getPolicyList() {
		return policyList;
	}
	
	public void setPolicyList(List<Policy> policyList) {
		this.policyList = policyList;
	}

	public String getApiKey() {
		return apiKey;
	}

	public void setApiKey(String apiKey) {
		this.apiKey = apiKey;
	}

	public String getInvalidStatus() {
		return invalidStatus;
	}

	public void setInvalidStatus(String invalidStatus) {
		this.invalidStatus = invalidStatus;
	}
	
	@Override
	public String toString() {

		JsonBuilder jb = new JsonBuilder();
		jb.put("class", this.getClass());
		jb.put("id", id);
		jb.put("serviceID", serviceID);
		jb.put("name", name);
		jb.put("apiKey", apiKey);
		jb.put("routingMethodType", routingMethodType);
		jb.put("protocolType", protocolType);
		jb.put("parameters", parameters);
		jb.put("policyList", policyList);
		jb.put("protocolObj", protocolObj);
		jb.put("parametersObj", parametersObj);
		return jb.toString();
		
	}
	
	
}