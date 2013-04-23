package com.kthcorp.radix.api.service;

import java.security.NoSuchAlgorithmException;
import java.util.List;

import com.kthcorp.radix.domain.client.ClientKey;
import com.kthcorp.radix.domain.client.ClientKeyType;
import com.kthcorp.radix.domain.exception.DataBaseProcessingException;
import com.kthcorp.radix.domain.exception.NotSupportException;
import com.kthcorp.radix.domain.exception.ValidateException;

public interface ClientKeyManagerService {
	
	// Is Exists?
	boolean isExists(byte[] businessPlatformID, String partnerID, ClientKeyType clientKeyType, String secret) throws ValidateException;
	
	boolean isExists(String keyString, String secret) throws ValidateException;
	
	// Get Client Key
	ClientKey getClientKey(byte[] businessPlatformID, String partnerID, ClientKeyType clientKeyType, String secret) throws ValidateException;
	
	ClientKey getClientKey(String keyString, ClientKeyType clientKeyType, String secret) throws ValidateException;
	
	List<ClientKey> getClientKey(String partnerID) throws ValidateException, NotSupportException;
	
	ClientKey getClientKey(byte[] clientID) throws ValidateException, NotSupportException;
	
	ClientKey getValidClientKey(byte[] clientID) throws ValidateException, NotSupportException;
	
	// Remove
	boolean removeClientKey(String keyString, ClientKeyType clientKeyType, String secret) throws ValidateException;
	
	// Add
	ClientKey createClientKey(byte[] businessPlatformID, String partnerID, ClientKeyType clientKeyType, String redirectUri, String applicationName, String applicationDescription) throws ValidateException, DataBaseProcessingException, NoSuchAlgorithmException;
	
	// Update
	boolean modifyClientKey(String keyString, String secret, ClientKey clientKey) throws ValidateException;

	// Invalidate ClientKey
	boolean invalidateClientKey(String clientKeyString, String secret) throws ValidateException;
	
}
