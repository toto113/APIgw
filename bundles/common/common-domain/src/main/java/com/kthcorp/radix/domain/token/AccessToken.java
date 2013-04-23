package com.kthcorp.radix.domain.token;

import java.io.Serializable;

import com.kthcorp.radix.util.JsonBuilder;

public class AccessToken implements Serializable {

	private static final long serialVersionUID = -6092728458892497244L;
	
	/* Identification */
	private String tokenID;
	
	private byte[] token;
	private String authenticationID;
	private byte[] authentication;
	
	private String refreshToken;
	private String clientID;
	private String username;
	private String scope;
	
	private String applicationName;
	private String applicationDescription;
	private boolean isValid;
	private String invalidStatus;
	private String createDate;
	public String getTokenID() {
		return tokenID;
	}
	public void setTokenID(String tokenID) {
		this.tokenID = tokenID;
	}
	public byte[] getToken() {
		return token;
	}
	public void setToken(byte[] token) {
		this.token = token;
	}
	public String getAuthenticationID() {
		return authenticationID;
	}
	public void setAuthenticationID(String authenticationID) {
		this.authenticationID = authenticationID;
	}
	public byte[] getAuthentication() {
		return authentication;
	}
	public void setAuthentication(byte[] authentication) {
		this.authentication = authentication;
	}
	public String getRefreshToken() {
		return refreshToken;
	}
	public void setRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}
	public String getClientID() {
		return clientID;
	}
	public void setClientID(String clientID) {
		this.clientID = clientID;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getScope() {
		return scope;
	}
	public void setScope(String scope) {
		this.scope = scope;
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
	public boolean isValid() {
		return isValid;
	}
	public void setValid(boolean isValid) {
		this.isValid = isValid;
	}
	public String getInvalidStatus() {
		return invalidStatus;
	}
	public void setInvalidStatus(String invalidStatus) {
		this.invalidStatus = invalidStatus;
	}
	public String getCreateDate() {
		return createDate;
	}
	public void setCreateDate(String createDate) {
		this.createDate = createDate;
	}
	
	
	@Override
	public String toString() {
		JsonBuilder jb = new JsonBuilder();
		jb.put("class", this.getClass());
		jb.put("tokenID", tokenID);
		jb.put("token", token);
		jb.put("authenticationID", authenticationID);
		jb.put("authentication", authentication);
		jb.put("refreshToken", refreshToken);
		jb.put("clientID", clientID);
		jb.put("username", username);
		jb.put("scope", scope);
		jb.put("applicationName", applicationName);
		jb.put("applicationDescription", applicationDescription);
		jb.put("isValid", isValid);
		jb.put("invalidStatus", invalidStatus);
		jb.put("createDate", createDate);
		return jb.toString();
	}
	
}
