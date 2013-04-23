package com.kthcorp.radix.api.service;

import java.security.NoSuchAlgorithmException;
import java.util.List;

import com.kthcorp.radix.domain.exception.ValidateException;
import com.kthcorp.radix.domain.packages.Packages;
import com.kthcorp.radix.domain.service.api.serviceAPI.ServiceAPI;

public interface PackageManagerService {
	
	byte[] createPackage(final Packages packages) throws ValidateException, NoSuchAlgorithmException;
	
	void createServiceAPI(final byte[] packageID, final ServiceAPI serviceAPI) throws ValidateException, NoSuchAlgorithmException;
	
	//Packages getPackage(final int packageID) throws ValidateException;	
	Packages getPackage(String partnerID, byte[] packageID) throws ValidateException;

	List<Packages> getPackageList(byte[] businessPlatformID, String partnerID);
	
	void modifyPackage(final Packages packages, boolean forceToDelete) throws ValidateException, NoSuchAlgorithmException;
	
	boolean removePackage(byte[] businessPlatformID, String partnerID, byte[] packageID, boolean forceToDelete) throws ValidateException;
	
	boolean removePackageServiceAPI(byte[] packageID, byte[] serviceAPIID, boolean forceToDelete) throws ValidateException;
}
