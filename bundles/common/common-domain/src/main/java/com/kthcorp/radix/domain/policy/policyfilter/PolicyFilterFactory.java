package com.kthcorp.radix.domain.policy.policyfilter;

import java.util.List;

import com.kthcorp.radix.domain.policy.Policy;

public class PolicyFilterFactory {
	
	private static PolicyFilterFactory instance;
	
	public static synchronized PolicyFilterFactory getInstance() {
		if (instance == null) {
			instance = new PolicyFilterFactory() ;
		}
		return instance;
	}
	
	public List<AbstractPolicyFilter> getPolicyFilterChain(List<Policy> policyList) {
		return null;
	}
}
