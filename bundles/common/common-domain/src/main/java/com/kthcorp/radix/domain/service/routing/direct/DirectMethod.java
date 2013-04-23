package com.kthcorp.radix.domain.service.routing.direct;

import java.io.Serializable;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.UUID;

import org.slf4j.Logger;
import com.kthcorp.radix.util.UuidViewableLoggerFactory;

import com.fasterxml.uuid.EthernetAddress;
import com.fasterxml.uuid.Generators;
import com.fasterxml.uuid.impl.TimeBasedGenerator;
import com.kthcorp.radix.domain.service.YesOrNo;
import com.kthcorp.radix.domain.service.api.partnerAPI.PartnerAPI;
import com.kthcorp.radix.domain.service.api.serviceAPI.ServiceAPI;
import com.kthcorp.radix.domain.service.mapping.MappingInfo;
import com.kthcorp.radix.domain.service.routing.RoutingMethod;
import com.kthcorp.radix.domain.service.routing.RoutingMethodType;
import com.kthcorp.radix.util.JsonBuilder;
import com.kthcorp.radix.util.UUIDUtils;

public class DirectMethod implements RoutingMethod, Serializable {
	
	private static final long serialVersionUID = -5319387408705889449L;
	private final RoutingMethodType routingMethodType = RoutingMethodType.DIRECT;
	private transient static final Logger LOG = UuidViewableLoggerFactory.getLogger(DirectMethod.class);
	
	@Override
	public RoutingMethodType getRoutingMethodType() {
		return routingMethodType;
	}
	
	private byte[] id;
	private byte[] businessPlatformID;
	private byte[] serviceID;
	private byte[] serviceAPIID;
	private byte[] partnerAPIID;
	private byte[] parameterMappingID;
	private byte[] resultMappingID;
	
	private Date regDate;
	private Date updateDate;
	private Date removeDate;
	
	private YesOrNo isValid;
	private String invalidStatus;
	
	// Service Object
	private ServiceAPI serviceAPI;
	private PartnerAPI partnerAPI;
	
	private MappingInfo parameterMap;
	private MappingInfo resultMap;
	
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
	
	@Override
	public byte[] getServiceID() {
		return serviceID;
	}
	
	@Override
	public void setServiceID(byte[] serviceID) {
		this.serviceID = serviceID;
	}
	
	@Override
	public byte[] getBusinessPlatformID() {
		return businessPlatformID;
	}
	
	@Override
	public void setBusinessPlatformID(byte[] businessPlatformID) {
		this.businessPlatformID = businessPlatformID;
	}
	
	public byte[] getServiceAPIID() {
		return serviceAPIID;
	}
	
	public void setServiceAPIID(byte[] serviceAPIID) {
		this.serviceAPIID = serviceAPIID;
	}
	
	public byte[] getPartnerAPIID() {
		return partnerAPIID;
	}
	
	public void setPartnerAPIID(byte[] partnerAPIID) {
		this.partnerAPIID = partnerAPIID;
	}
	
	public byte[] getParameterMappingID() {
		return parameterMappingID;
	}
	
	public void setParameterMappingID(byte[] parameterMappingID) {
		this.parameterMappingID = parameterMappingID;
	}
	
	public byte[] getResultMappingID() {
		return resultMappingID;
	}
	
	public void setResultMappingID(byte[] resultMappingID) {
		this.resultMappingID = resultMappingID;
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
	
	public ServiceAPI getServiceAPI() {
		return serviceAPI;
	}
	
	public void setServiceAPI(ServiceAPI serviceAPI) {
		this.serviceAPI = serviceAPI;
	}
	
	public PartnerAPI getPartnerAPI() {
		return partnerAPI;
	}
	
	public void setPartnerAPI(PartnerAPI partnerAPI) {
		this.partnerAPI = partnerAPI;
	}
	
	public MappingInfo getParameterMap() {
		return parameterMap;
	}
	
	public void setParameterMap(MappingInfo parameterMap) {
		this.parameterMap = parameterMap;
	}
	
	public MappingInfo getResultMap() {
		return resultMap;
	}
	
	public void setResultMap(MappingInfo resultMap) {
		this.resultMap = resultMap;
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
		jb.put("businessPlatformID", businessPlatformID);
		jb.put("serviceID", serviceID);
		jb.put("serviceAPIID", serviceAPIID);
		jb.put("partnerAPIID", partnerAPIID);
		jb.put("parameterMappingID", parameterMappingID);
		jb.put("resultMappingID", resultMappingID);
		jb.put("serviceAPI", serviceAPI);
		jb.put("partnerAPI", partnerAPI);
		jb.put("parameterMap", parameterMap);
		jb.put("resultMap", resultMap);
		return jb.toString();
		
	}
	

}