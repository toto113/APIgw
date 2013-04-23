package com.kthcorp.radix.api.service;

import java.util.List;

import com.kthcorp.radix.domain.exception.ValidateException;
import com.kthcorp.radix.domain.token.AccessToken;

public interface TokenManagerService {

	List<AccessToken> getAccessTokenListWithUserName(String userName);

	boolean removeAccessToken(AccessToken accessToken) throws ValidateException;
	
	AccessToken getAccessTokenByTokenId(String tokenId);
	
//	byte[] createPackage(final Packages packages) throws ValidateException, NoSuchAlgorithmException;
//	
//	void createServiceAPI(final byte[] packageID, final ServiceAPI serviceAPI) throws ValidateException, NoSuchAlgorithmException;
//	
//	//Packages getPackage(final int packageID) throws ValidateException;	
//	Packages getPackage(String partnerID, byte[] packageID) throws ValidateException;
//
//	List<Packages> getPackageList(byte[] businessPlatformID, String partnerID);
//	
//	void modifyPackage(final Packages packages, boolean forceToDelete) throws ValidateException, NoSuchAlgorithmException;
//	
//	boolean removePackage(byte[] businessPlatformID, String partnerID, byte[] packageID, boolean forceToDelete) throws ValidateException;
//	
//	boolean removePackageServiceAPI(byte[] packageID, byte[] serviceAPIID, boolean forceToDelete) throws ValidateException;
}
