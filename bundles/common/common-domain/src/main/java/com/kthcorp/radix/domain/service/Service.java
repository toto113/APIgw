package com.kthcorp.radix.domain.service;

import java.io.Serializable;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import com.kthcorp.radix.util.UuidViewableLoggerFactory;

import com.fasterxml.uuid.EthernetAddress;
import com.fasterxml.uuid.Generators;
import com.fasterxml.uuid.impl.TimeBasedGenerator;
import com.kthcorp.radix.domain.service.routing.RoutingMethod;
import com.kthcorp.radix.util.JsonBuilder;
import com.kthcorp.radix.util.UUIDUtils;

public class Service implements Serializable {
	
	private static final long serialVersionUID = 4349850367028992558L;
	private transient static final Logger LOG = UuidViewableLoggerFactory.getLogger(Service.class);
	
	/* Identification */
	private byte[] id;
	private byte[] businessPlatformID;
	private String partnerID;
	
	/* Attributes */
	private String name;
	private ServiceVersion version;
	private ResourceOwner resourceOwner;
	private String resourceAuthUrl;
	
	private Date regDate;
	private Date updateDate;
	private Date removeDate;
	
	private YesOrNo isValid = YesOrNo.Y;
	private String invalidStatus;
	
	private List<RoutingMethod> apiList;
	
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
	
	public byte[] getBusinessPlatformID() {
		return businessPlatformID;
	}
	
	public void setBusinessPlatformID(byte[] businessPlatformID) {
		this.businessPlatformID = businessPlatformID;
	}
	
	public String getPartnerID() {
		return partnerID;
	}
	
	public void setPartnerID(String partnerID) {
		this.partnerID = partnerID;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getVersion() {
		if(version != null) {
			return version.toString();
		}
		return null;
	}
	
	public ServiceVersion getVersionObj() {
		return version;
	}
	
	public void setVersion(ServiceVersion version) {
		this.version = version;
	}
	
	public void setVersion(String version) {
		if(version != null) {
			if(version.length() > 0) {
				this.version = new ServiceVersion(version);
			}
		}
	}
	
	public ResourceOwner getResourceOwner() {
		return resourceOwner;
	}
	
	public void setResourceOwner(ResourceOwner resourceOwner) {
		this.resourceOwner = resourceOwner;
	}
	
	public String getResourceAuthUrl() {
		return resourceAuthUrl;
	}
	
	public void setResourceAuthUrl(String resourceAuthUrl) {
		this.resourceAuthUrl = resourceAuthUrl;
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
	
	public List<RoutingMethod> getApiList() {
		return apiList;
	}
	
	public void setApiList(List<RoutingMethod> apiList) {
		this.apiList = apiList;
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
		jb.put("partnerID", partnerID);
		jb.put("name", name);
		jb.put("version", version);
		jb.put("resourceOwner", resourceOwner);
		jb.put("resourceAuthUrl", resourceAuthUrl);
		return jb.toString();
	}
	
}