package com.kthcorp.radix.api.service;

import java.security.NoSuchAlgorithmException;

import com.kthcorp.radix.domain.businessPlatform.BusinessPlatformKey;
import com.kthcorp.radix.domain.exception.AlreadyExistException;
import com.kthcorp.radix.domain.exception.DataBaseProcessingException;
import com.kthcorp.radix.domain.exception.NotSupportException;
import com.kthcorp.radix.domain.exception.ValidateException;

public interface BusinessPlatformKeyManagerService {
	
	// Is Exists?
	public boolean isExists(byte[] id) throws ValidateException;
	
	public boolean isExists(String keyString, String secret) throws ValidateException;
	
	public String getBusinessPlatformDomain(String keyString) throws ValidateException;
	
	public BusinessPlatformKey getBusinessPlatformKey(String keyString, String secret) throws ValidateException, NotSupportException;
	
	// Remove
	public boolean removeBusinessPlatformKey(String keyString, String secret) throws ValidateException, NotSupportException;
	
	// Add
	public BusinessPlatformKey createBusinessPlatformKey(String description, String domain, String redirectUri) throws ValidateException, AlreadyExistException, DataBaseProcessingException, NoSuchAlgorithmException;
	
	// Update
	public boolean modifyBusinessPlatformKey(String keyString, String secret, BusinessPlatformKey businessPlatformKey) throws ValidateException, NotSupportException;
	
}
