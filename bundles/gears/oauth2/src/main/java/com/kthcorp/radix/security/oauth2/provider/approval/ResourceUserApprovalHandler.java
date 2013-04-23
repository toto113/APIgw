package com.kthcorp.radix.security.oauth2.provider.approval;

import org.slf4j.Logger;
import com.kthcorp.radix.util.UuidViewableLoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.provider.AuthorizationRequest;
import org.springframework.security.oauth2.provider.approval.UserApprovalHandler;

public class ResourceUserApprovalHandler implements UserApprovalHandler {

private final Logger LOG = UuidViewableLoggerFactory.getLogger(this.getClass());
	
	@Override
	public boolean isApproved(AuthorizationRequest authorizationRequest,
			Authentication userAuthentication) {

		LOG.debug("isApproved({}, {})", authorizationRequest, authorizationRequest);
		LOG.debug("oauth_user_approval={}", authorizationRequest.getParameters().get("user_oauth_approval"));
		return "true".equalsIgnoreCase(authorizationRequest.getParameters().get("user_oauth_approval"));
	}
}
