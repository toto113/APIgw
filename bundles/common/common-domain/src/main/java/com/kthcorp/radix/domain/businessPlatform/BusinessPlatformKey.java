package com.kthcorp.radix.domain.businessPlatform;

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
import com.kthcorp.radix.util.UUIDUtils;

public class BusinessPlatformKey {
	
	private static Logger LOG = UuidViewableLoggerFactory.getLogger(BusinessPlatformKey.class);
	private byte[] id;
	private String keyString;
	private String secret;
	private String description;
	private String domain;
	private String redirectUri;
	
	private Date regDate;
	private Date removedDate;
	
	private boolean isValid;
	
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
			LOG.trace("Generated BusinessPlatformKey->"+newID+"(v1,length->"+this.id.length+"bytes), Secret->"+this.secret);
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
	
	public String getDomain() {
		return domain;
	}
	
	public void setDomain(String domain) {
		this.domain = domain;
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
	
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("keyString");
		sb.append(":");
		sb.append(keyString);
		sb.append(",");
		sb.append("secret");
		sb.append(":");
		sb.append(secret);
		sb.append(",");
		sb.append("businessPlatformKey");
		sb.append(":");
		sb.append(keyString);
		sb.append(",");
		sb.append("domain");
		sb.append(":");
		sb.append(domain);
		sb.append(",");
		sb.append("redirectUri");
		sb.append(":");
		sb.append(redirectUri);
		sb.append(",");
		sb.append("regDate");
		sb.append(":");
		sb.append(regDate);
		sb.append(",");
		sb.append("removedDate");
		sb.append(":");
		sb.append(removedDate);
		sb.append(",");
		sb.append("isValid");
		sb.append(":");
		sb.append(isValid);
		return sb.toString();
	}
	
	public String getRedirectUri() {
		return redirectUri;
	}
	
	public void setRedirectUri(String redirectUri) {
		this.redirectUri = redirectUri;
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public byte[] getId() {
		return id;
	}
	
	public void setId(byte[] id) {
		this.id = id;
	}
}
