package com.kthcorp.radix.domain.client;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.Random;
import java.util.UUID;

import org.slf4j.Logger;
import com.kthcorp.radix.util.UuidViewableLoggerFactory;

import com.fasterxml.uuid.EthernetAddress;
import com.fasterxml.uuid.Generators;
import com.fasterxml.uuid.impl.TimeBasedGenerator;
import com.kthcorp.radix.util.JsonBuilder;
import com.kthcorp.radix.util.UUIDUtils;

public class ClientKey {
	
	private static Logger LOG = UuidViewableLoggerFactory.getLogger(ClientKey.class);
	
	private byte[] id;
	private String keyString;
	private String secret;
	private byte[] businessPlatformID;
	private String partnerID;
	private String type;
	private String redirectUri;
	
	private Date regDate;
	private Date removedDate;
	
	private boolean isValid;
	
	private String applicationName;
	private String applicationDescription;
	
	
	/**
	 * Generate Key&ID, Secret
	 * 
	 * ID (in bytes, from KeyString)
	 * KeyString (in string, UUID v1, timestamp in UTC & mac address based, used as client_id from oAuth2)
	 * Secret (password from oAuth2, used to get access/refresh-token)
	 * 
	 * @throws NoSuchAlgorithmException 
	 */
	public void generateKeyAndSecret() throws NoSuchAlgorithmException {
		
		EthernetAddress ethernetAddress = EthernetAddress.fromInterface();
		TimeBasedGenerator generator = Generators.timeBasedGenerator(ethernetAddress);
		UUID newID = generator.generate();

		this.id = UUIDUtils.getBytes(newID);		
		this.keyString = newID.toString();
		
		// Secret (SHA-1 from Random bytes(512-bit fixed))
		Random rand = new Random();
		byte[] bytes = new byte[512];
		rand.nextBytes(bytes);
			
		MessageDigest md = MessageDigest.getInstance("SHA-1");
		this.secret = UUIDUtils.byteArray2Hex(md.digest(bytes));
		
		if(LOG.isTraceEnabled()) {
			LOG.trace("Generated ClientKey->"+newID+"(v1,length->"+this.id.length+"bytes), Secret->"+this.secret);
		}
	}
	
	public String getKeyString() {
		return keyString;
	}
	
	public void setKeyString(String keyString) {
		this.keyString = keyString;
	}
	
	public String getSecret() {
		return secret;
	}
	
	public void setSecret(String secret) {
		this.secret = secret;
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
	
	public String getType() {
		return type;
	}
	
	public void setType(String type) {
		this.type = type;
	}
	
	public void setType(ClientKeyType type) {
		this.type = type.getCode();
	}
	
	public Date getRegDate() {
		return regDate;
	}
	
	public void setRegDate(Date regDate) {
		this.regDate = regDate;
	}
	
	public Date getRemovedDate() {
		return removedDate;
	}
	
	public void setRemovedDate(Date removedDate) {
		this.removedDate = removedDate;
	}
	
	public boolean isValid() {
		return isValid;
	}
	
	public void setValid(boolean isValid) {
		this.isValid = isValid;
	}
	
	public String getRedirectUri() {
		return redirectUri;
	}
	
	public void setRedirectUri(String redirectUri) {
		this.redirectUri = redirectUri;
	}
	
	public byte[] getId() {
		return id;
	}
	
	public void setId(byte[] id) {
		this.id = id;
	}

	public String getApplicationName() {
		return applicationName;
	}

	public void setApplicationName(String applicationName) {
		this.applicationName = applicationName;
	}

	public String getApplicationDescription() {
		return applicationDescription;
	}

	public void setApplicationDescription(String applicationDescription) {
		this.applicationDescription = applicationDescription;
	}
	
	@Override
	public String toString() {
		JsonBuilder jb = new JsonBuilder();
		jb.put("class", this.getClass());
		jb.put("keyString", keyString);
		jb.put("secret", secret);
		jb.put("businessPlatformID", businessPlatformID);
		jb.put("partnerID", partnerID);
		jb.put("type", type);
		jb.put("redirectUri", redirectUri);
		jb.put("regDate", regDate);
		jb.put("removedDate", removedDate);
		jb.put("isValid", isValid);
		return jb.toString();
	}
}
