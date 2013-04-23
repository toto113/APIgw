package com.kthcorp.radix.api.service;

import org.json.JSONException;

import com.kthcorp.radix.domain.exception.PolicyException;
import com.kthcorp.radix.domain.scope.ScopePolicies;

public interface PolicyEnforcerService {
	
	void applyPolicy(ScopePolicies policies, byte[] clientID, String apiKey) throws PolicyException, JSONException;
	
}
