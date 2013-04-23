package com.kthcorp.radix.domain.policy.policyfilter;

import com.kthcorp.radix.domain.policy.Policy;

public class AbstractPolicyFilter {
	
	public boolean doFilter(Policy policy) {
		return false;
	}
	
	public boolean doChain(Policy policy) {
		return false;
	}
}
