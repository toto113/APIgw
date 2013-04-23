package com.kthcorp.radix.component;

import org.json.JSONException;
import org.slf4j.Logger;
import com.kthcorp.radix.util.UuidViewableLoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.kthcorp.radix.api.service.PolicyEnforcerService;
import com.kthcorp.radix.api.service.ScopeManagerService;
import com.kthcorp.radix.domain.canonical.CanonicalMessage;
import com.kthcorp.radix.domain.canonical.request.ServiceControllerRestRequest;
import com.kthcorp.radix.domain.exception.DataBaseProcessingException;
import com.kthcorp.radix.domain.exception.NotFoundException;
import com.kthcorp.radix.domain.exception.PolicyException;
import com.kthcorp.radix.domain.exception.PolicyRateLimitException;
import com.kthcorp.radix.domain.exception.ValidateException;
import com.kthcorp.radix.domain.scope.ScopePolicies;
import com.kthcorp.radix.util.UUIDUtils;
import com.kthcorp.radix.web.filter.DemorableConfiguration;

public class PolicyEnforcementComponent {
	
	/**
	 * LOG4j to LOG.
	 */
	@SuppressWarnings("unused")
	private static final Logger LOG = UuidViewableLoggerFactory.getLogger(PolicyEnforcementComponent.class);
	
	@Autowired
	private ScopeManagerService scopeManagerService;
	
	@Autowired
	private PolicyEnforcerService prePolicyEnforcerService;
	
	@Autowired
	private PolicyEnforcerService postPolicyEnforcerService;
	
	public CanonicalMessage enforcePolicyBeforeProcessing(CanonicalMessage canonicalMessage) throws NotFoundException, ValidateException, DataBaseProcessingException, PolicyException, PolicyRateLimitException, JSONException {
		// Enforcing policy before processing business logic
		enforcePolicy(prePolicyEnforcerService, canonicalMessage);
		
		return canonicalMessage;
	}
	
	public CanonicalMessage enforcePolicyAfterProcessing(CanonicalMessage canonicalMessage) throws NotFoundException, ValidateException, DataBaseProcessingException, JSONException, PolicyException {
		// Enforcing policy after processing business logic
		enforcePolicy(postPolicyEnforcerService, canonicalMessage);
		
		return canonicalMessage;
	}
	
	private void enforcePolicy(PolicyEnforcerService policyEnforcerService, CanonicalMessage canonicalMessage) throws NotFoundException, ValidateException, DataBaseProcessingException, PolicyException, JSONException {
		
		String clientKey = canonicalMessage.getHeader().getHeaderProperties().get("clientKey");

		if(isSkippable(clientKey)) { return; }
		
		byte[] clientID = UUIDUtils.getBytes(clientKey);
		ServiceControllerRestRequest request = (ServiceControllerRestRequest) canonicalMessage.getPayload().getRequest();
		String apiKey = request.getKey();
		String resourcePath = request.getResroucePath();
		ScopePolicies policies = scopeManagerService.getScopePolicies(apiKey, resourcePath, clientID);
		
		policyEnforcerService.applyPolicy(policies, clientID, apiKey);
		
		scopeManagerService.modifyStatus(clientID, apiKey, policies.getStatus());
	}
	
	private boolean isSkippable(String clientKey) {
		if(clientKey==null) { return false; }
		if(isForDemo(clientKey)) { return true; }
		if(isFromBusinessPlatform(clientKey)) { return true; }
		return false;
	}

	private boolean isForDemo(String clientKey) {
		// 데모를 위한 클라이언트 Key이다.
		return DemorableConfiguration.CLIENT_KEY_FOR_DEMO.equals(clientKey);
	}

	private boolean isFromBusinessPlatform(String clientKey) {
		// if clientkey equals businessplatform key then skip applying policy!
		return "59ffa6f4-0901-4ffc-82ad-44687540ab4b".equals(clientKey.toLowerCase());
	}
}
