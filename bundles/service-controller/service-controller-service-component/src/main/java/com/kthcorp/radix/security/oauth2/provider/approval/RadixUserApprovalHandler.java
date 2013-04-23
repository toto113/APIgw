package com.kthcorp.radix.security.oauth2.provider.approval;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.provider.AuthorizationRequest;
import org.springframework.security.oauth2.provider.approval.UserApprovalHandler;

public class RadixUserApprovalHandler implements UserApprovalHandler {
	
	@Override
	public boolean isApproved(AuthorizationRequest authorizationRequest, Authentication userAuthentication) {
		
		return userAuthentication.isAuthenticated() && authorizationRequest.isApproved();
	}
	
}
