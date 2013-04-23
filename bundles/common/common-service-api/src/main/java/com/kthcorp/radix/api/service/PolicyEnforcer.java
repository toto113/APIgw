package com.kthcorp.radix.api.service;

import java.util.Map;

import org.json.JSONException;

import com.kthcorp.radix.domain.exception.PolicyException;

public interface PolicyEnforcer {
	
	String preCheck(Map<String, String> specificationForPolicy, String currentStatusForPolicy) throws PolicyException, JSONException;
	
	String postCheck(Map<String, String> specificationForPolicy, String currentStatusForPolicy) throws PolicyException, JSONException;
}
