package com.kthcorp.radix.api.service;

import java.security.NoSuchAlgorithmException;
import java.util.List;

import com.kthcorp.radix.domain.exception.ValidateException;
import com.kthcorp.radix.domain.policy.Policy;
import com.kthcorp.radix.domain.policy.PolicyType;

public interface PolicyManagerService {
	
	String createPolicy(final PolicyType policyType) throws ValidateException;
	
	void createPackagePolicy(final Policy policy) throws ValidateException, NoSuchAlgorithmException;
	
	void createPackageServiceAPIPolicy(final Policy policy) throws ValidateException, NoSuchAlgorithmException;
	
	PolicyType getPolicyType(final String policyTypeID) throws ValidateException;
	
	boolean modifyPolicy(final PolicyType policyType) throws ValidateException;
	
	boolean modifyPackagePolicy(byte[] packageID, List<Policy> policyList, boolean forceToDelete) throws ValidateException, NoSuchAlgorithmException;
	
	boolean modifyPackagePolicy(Policy policy) throws ValidateException;
		
	boolean modifyPackageServiceAPIPolicy(byte[] packageID, byte[] serviceAPIID, List<Policy> policyList, boolean forceToDelete) throws ValidateException, NoSuchAlgorithmException;
	
	boolean modifyPackageServiceAPIPolicy(Policy policy) throws ValidateException;
	
	boolean removePolicy(final String policyTypeID, boolean forceToDelete) throws ValidateException;
	
	boolean removePackagePolicy(byte[] packageID, byte[] policyID, boolean forceToDelete) throws ValidateException;
	
	boolean removePackageServiceAPIPolicy(byte[] packageID, byte[] serviceAPIID, byte[] policyID, boolean forceToDelete) throws ValidateException;
	
}
