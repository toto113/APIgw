package com.kthcorp.radix.api.service;

import java.util.List;
import java.util.Map;

import com.kthcorp.radix.domain.exception.DataBaseProcessingException;
import com.kthcorp.radix.domain.exception.NotFoundException;
import com.kthcorp.radix.domain.exception.ValidateException;
import com.kthcorp.radix.domain.scope.ScopePolicies;

public interface ScopeManagerService {

	
	// High Level
	void createPackagePolicies(byte[] clientID, byte[] packageID) throws NotFoundException, ValidateException, DataBaseProcessingException;
	
	Map<String,String> getStatus(byte[] clientID, String apiKey) throws NotFoundException, ValidateException, DataBaseProcessingException;
	
	void modifyPackagePolicies(byte[] clientID, byte[] packageID) throws NotFoundException, ValidateException, DataBaseProcessingException;
	
	void modifyStatus(byte[] clientID, String apiKey, Map<String, String> status) throws NotFoundException, ValidateException, DataBaseProcessingException;
	
	void removePackagePolicies(byte[] clientID, byte[] packageID) throws NotFoundException, ValidateException;
	
	List<String> getScopeList(byte[] clientID);
	
	// Low Level
	void createScopePolicies(String apiKey, byte[] clientID, byte[] packageID, byte[] serviceAPIID, ScopePolicies scopePolicies) throws ValidateException, DataBaseProcessingException;
	
	ScopePolicies getScopePolicies(String apiKey, String resourcePath, byte[] clientID) throws ValidateException, DataBaseProcessingException;
		
	void modifyStatus(String apiKey, byte[] clientID,  Map<String,String> status) throws ValidateException, DataBaseProcessingException;
}