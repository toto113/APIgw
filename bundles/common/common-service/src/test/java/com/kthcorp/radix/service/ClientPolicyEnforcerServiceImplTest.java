package com.kthcorp.radix.service;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.json.JSONException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import com.kthcorp.radix.util.test.junit.MysqlEmbeddableSpringJUnit4ClassRunner;

import com.kthcorp.radix.api.service.PolicyEnforcerService;
import com.kthcorp.radix.domain.exception.PolicyException;
import com.kthcorp.radix.domain.scope.Policy;
import com.kthcorp.radix.domain.scope.ScopePolicies;
import com.kthcorp.radix.util.FailedTest;
import com.kthcorp.radix.util.UUIDUtils;

@RunWith(MysqlEmbeddableSpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { 
		"classpath:/META-INF/spring/policy/spring-application-context-test.xml", 
		"classpath:/META-INF/spring/policy/spring-service-context-test.xml" 
		}
)
public class ClientPolicyEnforcerServiceImplTest extends AbstractJUnit4SpringContextTests {
	
	@Autowired
	private PolicyEnforcerService prePolicyEnforcerService;
	
	@Autowired
	private PolicyEnforcerService postPolicyEnforcerService;
	
	@Test
	public void nullTest() { }
	
	@FailedTest
	public void firstRequestTest() throws PolicyException, JSONException {
		@SuppressWarnings("unchecked")
		Map<String, String> packageProperties = mock(Map.class);
		@SuppressWarnings("unchecked")
		Map<String, String> serviceProperties = mock(Map.class);
		@SuppressWarnings("unchecked")
		Map<String, String> packageStatus = mock(Map.class);
		@SuppressWarnings("unchecked")
		List<Policy> policyList = mock(List.class);
		
		ScopePolicies policies = mock(ScopePolicies.class);
		Policy policy = mock(Policy.class);
		
		when(policies.getPackageID()).thenReturn(UUIDUtils.getBytes(UUID.randomUUID()));
		when(policies.getApiKey()).thenReturn("api.withapi.com::GET:/MapAPI/1/poi");
		when(policies.getPackageParams()).thenReturn(packageProperties);
		when(policies.getStatus()).thenReturn(packageStatus);
		
		when(packageProperties.get("startTimestamp")).thenReturn(Long.toString(System.currentTimeMillis() / 1000));
		when(packageProperties.get("endTimestamp")).thenReturn(Long.toString((System.currentTimeMillis() / 1000) + 100000));
		when(packageProperties.get("maxCount")).thenReturn("10");
		
		when(packageStatus.get("1-api.withapi.com::GET:/PublicTransport/0/BusLane-1")).thenReturn(null);
		when(packageStatus.get("1-api.withapi.com::GET:/PublicTransport/0/BusLane-1-1")).thenReturn(null);
		
		when(policies.getPolicyList()).thenReturn(policyList);
		when(policyList.get(0)).thenReturn(policy);
		
		when(policy.getServiceAPIID()).thenReturn(UUIDUtils.getBytes(UUID.randomUUID()));
		when(policy.getProperties()).thenReturn(serviceProperties);
		
		when(serviceProperties.get("duration")).thenReturn("1H");
		when(serviceProperties.get("maxCount")).thenReturn("100");
		
		prePolicyEnforcerService.applyPolicy(policies, policies.getPackageID(), policies.getApiKey());
		
		postPolicyEnforcerService.applyPolicy(policies, policies.getPackageID(), policies.getApiKey());
	}
}
