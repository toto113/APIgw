package com.kthcorp.radix.domain.policy.policyfilter;

import java.util.List;

import com.kthcorp.radix.domain.policy.Policy;

public class PolicyFilterChain {
	
	private List<AbstractPolicyFilter> policyFilterChain;
	
	public List<AbstractPolicyFilter> getPolicyFilterChain() {
		return policyFilterChain;
	}
	
	public void setPolicyFilterChain(List<AbstractPolicyFilter> policyFilterChain) {
		this.policyFilterChain = policyFilterChain;
	}
	
	public boolean doFilter(List<Policy> policyList) {
		return false;
	}
}
