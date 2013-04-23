package com.kthcorp.radix.service;

import java.util.Map;

import com.kthcorp.radix.api.service.PolicyEnforcer;
import com.kthcorp.radix.domain.exception.PolicyException;
import com.kthcorp.radix.domain.exception.PolicyExpiredException;

public class PackagePolicyEnforcerImpl implements PolicyEnforcer {
	
	private long beginTermOfContract;
	private long finishTermOfContract;
	@SuppressWarnings("unused")
	private long maxLimitCount;

	@Override
	public String preCheck(Map<String, String> specificationForPolicy, String currentStatusForPolicy) throws PolicyException {
		// Get basic data of policy specification
		beginTermOfContract = Long.parseLong(specificationForPolicy.get("startTimestamp"));
		finishTermOfContract = Long.parseLong(specificationForPolicy.get("endTimestamp"));
		//maxLimitCount = Long.parseLong(specificationForPolicy.get("maxCount"));
		
		// Get current status data of policy
		long accumulativeCount = 0;
		if(!(currentStatusForPolicy == null) && !("".equals(currentStatusForPolicy))) {
			accumulativeCount = Long.parseLong(currentStatusForPolicy);
		}
		
		// Checking term of a contract for package
		if(!isInTermOfContract(beginTermOfContract, finishTermOfContract)) {
			throw new PolicyExpiredException("Package policy is expired or not ready!!");
		}
		
		/*
		 * Modified. 2012.04.16
		 * Because package policy have no max limit count.
		 * Max limit count is set in service policy
		// Checking used count(value of -1 is unlimit.)		
		if(0 <= maxLimitCount && maxLimitCount <= accumulativeCount) {
			throw new PolicyRateLimitException("Package policy exhausted available usage!!");
		}
		*/

		return Long.toString(accumulativeCount);
	}
	
	@Override
	public String postCheck(Map<String, String> specificationForPolicy, String currentStatusForPolicy) throws PolicyException {
		long accumulativeCount;
		
		if(currentStatusForPolicy == null || "".equals(currentStatusForPolicy)) {
			throw new PolicyException("Not found a status of package policy");
		}
		
		accumulativeCount = Long.parseLong(currentStatusForPolicy);
		
		return Long.toString(++accumulativeCount);
	}
	
	private boolean isInTermOfContract(long beginTime, long finishTime) {
		boolean result = false;
		
		long now = (long) (System.currentTimeMillis() / 1000);
		if((beginTime <= now) && (now <= finishTime)) {
			result = true;
		} else {
			result = false;
		}
		
		return result;
	}
}
