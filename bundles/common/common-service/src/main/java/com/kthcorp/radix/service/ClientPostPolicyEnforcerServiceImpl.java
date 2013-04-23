package com.kthcorp.radix.service;

import java.util.Map;

import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;

import com.kthcorp.radix.api.service.PolicyEnforcer;
import com.kthcorp.radix.api.service.PolicyEnforcerService;
import com.kthcorp.radix.domain.exception.PolicyException;
import com.kthcorp.radix.domain.scope.Policy;
import com.kthcorp.radix.domain.scope.ScopePolicies;
import com.kthcorp.radix.util.UUIDUtils;

public class ClientPostPolicyEnforcerServiceImpl implements PolicyEnforcerService {
	
	@Autowired
	private PolicyEnforcer packagePolicyEnforcer;
	
	@Autowired
	private PolicyEnforcer servicePolicyEnforcer;
	
	@Override
	public void applyPolicy(ScopePolicies policies, byte[] clientID, String apiKey) throws PolicyException, JSONException {
		/*
		 * Applying policies of client after processing business concern.
		 */
		byte[] packageID = policies.getPackageID();
		
		/* TODO: (v0.3) 현재는 Package Policy 1개만을 지원하며(usageTerm Policy 에 대해서만) 나중에 다양한 Policy 를 여러개 지원하려면 수정되어야 함 */
		String packageStatusKey = 
				UUIDUtils.getString(clientID) + ":" + 
						UUIDUtils.getString(packageID)+ ":" + 
						UUIDUtils.getString(policies.getPolicyList().get(0).getId());
		
		Map<String, String> packagePolicyProperties = policies.getPackageParams();
		Map<String, String> packagePolicyTotalStatus = policies.getStatus();
		String currentPackageStatus = packagePolicyTotalStatus.get(packageStatusKey);
		
		// Checking policies of service api
		Map<String, String> servicePolicyProperties = null;
		String serviceStatusKey = null;
		String currentServiceStatus = null;
		String serviceStatus = null;
		
		for(Policy policy : policies.getPolicyList()) {
			
			/* TODO: (v0.3) 현재는 usageLimit 타입의 Policy 만 지원 */
			if("usageLimit".equals(policy.getPolicyTypeID())) {
				serviceStatusKey = UUIDUtils.getString(clientID) + ":" + 
						UUIDUtils.getString(packageID) + ":" + 
						UUIDUtils.getString(policies.getServiceAPIID()) + ":" + 
						UUIDUtils.getString(policy.getId());
				servicePolicyProperties = policy.getProperties();
				currentServiceStatus = packagePolicyTotalStatus.get(serviceStatusKey);
				serviceStatus = servicePolicyEnforcer.postCheck(servicePolicyProperties, currentServiceStatus);
				packagePolicyTotalStatus.put(serviceStatusKey, serviceStatus);
			}
		}
		
		// Checking policy of package
		String packageStatus = packagePolicyEnforcer.postCheck(packagePolicyProperties, currentPackageStatus);
		packagePolicyTotalStatus.put(packageStatusKey, packageStatus);
	}
	
}
