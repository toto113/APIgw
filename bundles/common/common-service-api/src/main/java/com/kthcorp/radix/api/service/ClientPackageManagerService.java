package com.kthcorp.radix.api.service;

import java.security.NoSuchAlgorithmException;
import java.util.List;

import com.kthcorp.radix.domain.client.CPackages;
import com.kthcorp.radix.domain.exception.DataBaseProcessingException;
import com.kthcorp.radix.domain.exception.ValidateException;
import com.kthcorp.radix.domain.service.YesOrNo;

public interface ClientPackageManagerService {
	
	boolean createClientPackage(byte[] clientID, List<CPackages> clientPackageList) throws ValidateException, DataBaseProcessingException, NoSuchAlgorithmException;
	boolean createClientPackage(byte[] clientID, byte[] packageID, String parameters) throws ValidateException, DataBaseProcessingException, NoSuchAlgorithmException;
	
	List<CPackages> getClientPackageList(byte[] clientID) throws ValidateException;
	CPackages getClientPackage(byte[] clientID, byte[] packageID) throws ValidateException;
	
	boolean modifyClientPackage(byte[] clientID, List<CPackages> clientPackageList, boolean forceToDelete) throws ValidateException, DataBaseProcessingException, NoSuchAlgorithmException;
	boolean modifyClientPackage(byte[] clientID, byte[] packageID, String parameters, boolean forceToDelete) throws ValidateException;
	boolean modifyClientPackageValid(byte[] clientID, byte[] packageID, YesOrNo isValid, String invalidStatus, boolean forceToDelete) throws ValidateException;
	
	boolean removeClientPackage(byte[] clientID, byte[] packageID, boolean forceToDelete) throws ValidateException;
	boolean removeClientPackage(byte[] clientID, boolean forceToDelete) throws ValidateException;
	boolean removeBackupClientPackage(byte[] clientID) throws ValidateException;
	boolean removeBackupClientPackage(byte[] clientID, byte[] packageID) throws ValidateException;
}
