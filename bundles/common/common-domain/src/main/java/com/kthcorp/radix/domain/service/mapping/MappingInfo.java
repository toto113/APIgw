package com.kthcorp.radix.domain.service.mapping;

import java.io.Serializable;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.UUID;

import org.json.JSONException;
import org.slf4j.Logger;
import com.kthcorp.radix.util.UuidViewableLoggerFactory;

import com.fasterxml.uuid.EthernetAddress;
import com.fasterxml.uuid.Generators;
import com.fasterxml.uuid.impl.TimeBasedGenerator;
import com.kthcorp.radix.domain.service.YesOrNo;
import com.kthcorp.radix.util.JsonBuilder;
import com.kthcorp.radix.util.UUIDUtils;

public class MappingInfo implements Serializable {
	
	private static final long serialVersionUID = -9038629375235832501L;
	private transient static final Logger LOG = UuidViewableLoggerFactory.getLogger(MappingInfo.class);
	
	private byte[] id;
	private byte[] serviceID;
	private byte[] serviceAPIID;
	private byte[] partnerAPIID;
	
	private MappingType mappingType;
	private String mapping;
	
	private Mapping mappingObj;
	
	private Date regDate;
	private Date updateDate;
	private Date removeDate;
	
	private YesOrNo isValid;
	private String invalidStatus;
	
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
	
	public MappingType getMappingType() {
		return mappingType;
	}
	
	public void setMappingType(MappingType mappingType) {
		this.mappingType = mappingType;
	}
	
	public String getMapping() {
		return mapping.toString();
	}
	
	public void setMapping(String mapping) throws JSONException {
		this.mapping = mapping;
		this.mappingObj = Mapping.fromJSONString(mapping);
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
	
	public Mapping getMappingObj() {
		return mappingObj;
	}
	
	public void setMappingObj(Mapping mappingObj) throws JSONException {
		this.mappingObj = mappingObj;
		this.mapping = this.mappingObj.toJSONString();
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
		jb.put("serviceAPIID", serviceAPIID);
		jb.put("partnerAPIID", partnerAPIID);
		jb.put("mappingType", mappingType);
		jb.put("mapping", mapping);
		return jb.toString();
	}
	
}
