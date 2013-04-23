package com.kthcorp.radix.service;

import java.util.HashMap;
import java.util.Map;

import junit.framework.Assert;

import org.json.JSONException;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import com.kthcorp.radix.util.UuidViewableLoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import com.kthcorp.radix.util.test.junit.MysqlEmbeddableSpringJUnit4ClassRunner;

import com.kthcorp.radix.api.service.PolicyEnforcer;
import com.kthcorp.radix.domain.exception.PolicyException;
import com.kthcorp.radix.domain.exception.PolicyExpiredException;
import com.kthcorp.radix.domain.exception.PolicyRateLimitException;

@RunWith(MysqlEmbeddableSpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/META-INF/spring/policy/spring-all-context-test.xml" })
public class PackagePolicyEnforcerTest extends AbstractJUnit4SpringContextTests {
	
	/**
	 * LOG4j to LOG.
	 */
	@SuppressWarnings("unused")
	private static final Logger LOG = UuidViewableLoggerFactory.getLogger(PackagePolicyEnforcerTest.class);
	
	@Autowired
	private PolicyEnforcer packagePolicyEnforcer;
	
	@Rule
	public ExpectedException expectedException = ExpectedException.none();
	
	@Test
	public void validContractTermOfPacakgeTest() throws PolicyException, JSONException {
		Map<String, Object> testPolicy = getPolicyMap(TestType.Valid_Contract_Term_Of_Package);
		@SuppressWarnings("unchecked")
		Map<String, String> specification = (HashMap<String, String>) testPolicy.get("Specification");
		
		String currentStatusForPolicy = (String) testPolicy.get("Status");
		currentStatusForPolicy = packagePolicyEnforcer.preCheck(specification, currentStatusForPolicy);
		Assert.assertEquals("0", currentStatusForPolicy);
	}
	
	@Test
	public void notYetContractTermOfPacakgeTest() throws PolicyException, PolicyExpiredException, JSONException {
		expectedException.expect(PolicyExpiredException.class);
		expectedException.expectMessage("Package policy is expired or not ready!!");
		
		Map<String, Object> testPolicy = getPolicyMap(TestType.Not_Yet_Contract_Term_Of_Package);
		@SuppressWarnings("unchecked")
		Map<String, String> specification = (HashMap<String, String>) testPolicy.get("Specification");
		
		String currentStatusForPolicy = (String) testPolicy.get("Status");
		currentStatusForPolicy = packagePolicyEnforcer.preCheck(specification, currentStatusForPolicy);
	}
	
	@Test
	public void alreadExpiredContractTermOfPacakgeTest() throws PolicyException, PolicyExpiredException, JSONException {
		expectedException.expect(PolicyExpiredException.class);
		expectedException.expectMessage("Package policy is expired or not ready!!");
		
		Map<String, Object> testPolicy = getPolicyMap(TestType.Already_Expired_Contract_Term_Of_Package);
		@SuppressWarnings("unchecked")
		Map<String, String> specification = (HashMap<String, String>) testPolicy.get("Specification");
		
		String currentStatusForPolicy = (String) testPolicy.get("Status");
		currentStatusForPolicy = packagePolicyEnforcer.preCheck(specification, currentStatusForPolicy);
	}
	
	@Ignore
	@Test
	public void equalMaxRateLimitOfPacakgeTest() throws PolicyException, PolicyRateLimitException, JSONException {
		expectedException.expect(PolicyRateLimitException.class);
		expectedException.expectMessage("Package policy exhausted available usage!!");
		
		Map<String, Object> testPolicy = getPolicyMap(TestType.Equal_Max_Rate_Limie_Of_Package);
		@SuppressWarnings("unchecked")
		Map<String, String> specification = (HashMap<String, String>) testPolicy.get("Specification");
		
		String currentStatusForPolicy = (String) testPolicy.get("Status");
		currentStatusForPolicy = packagePolicyEnforcer.preCheck(specification, currentStatusForPolicy);
	}
	
	@Ignore
	@Test
	public void exceedMaxRateLimitOfPacakgeTest() throws PolicyException, PolicyRateLimitException, JSONException {
		expectedException.expect(PolicyRateLimitException.class);
		expectedException.expectMessage("Package policy exhausted available usage!!");
		
		Map<String, Object> testPolicy = getPolicyMap(TestType.Exceeded_Max_Rate_Limie_Of_Package);
		@SuppressWarnings("unchecked")
		Map<String, String> specification = (HashMap<String, String>) testPolicy.get("Specification");
		
		String currentStatusForPolicy = (String) testPolicy.get("Status");
		currentStatusForPolicy = packagePolicyEnforcer.preCheck(specification, currentStatusForPolicy);
	}
	
	@Test
	public void unlimitMaxRateOfPacakgeTest() throws PolicyException, PolicyRateLimitException, JSONException {
		
		Map<String, Object> testPolicy = getPolicyMap(TestType.Unlimit_Max_Rate_Of_Package);
		@SuppressWarnings("unchecked")
		Map<String, String> specification = (HashMap<String, String>) testPolicy.get("Specification");
		
		String currentStatusForPolicy = (String) testPolicy.get("Status");
		currentStatusForPolicy = packagePolicyEnforcer.preCheck(specification, currentStatusForPolicy);
		Assert.assertEquals((String) testPolicy.get("Status"), currentStatusForPolicy);
	}
	
	@Test
	public void afterProcessingTest() throws PolicyException, PolicyRateLimitException, JSONException {
		
		Map<String, Object> testPolicy = getPolicyMap(TestType.Ater_Processing_Package);
		@SuppressWarnings("unchecked")
		Map<String, String> specification = (HashMap<String, String>) testPolicy.get("Specification");
		
		String currentStatusForPolicy = (String) testPolicy.get("Status");
		currentStatusForPolicy = packagePolicyEnforcer.postCheck(specification, currentStatusForPolicy);
		Assert.assertEquals(Long.parseLong((String) testPolicy.get("Status")) + 1, Long.parseLong(currentStatusForPolicy));
	}
	
	@Test
	public void afterProcessingErrorTest() throws PolicyException, PolicyRateLimitException, JSONException {
		expectedException.expect(PolicyException.class);
		expectedException.expectMessage("Not found a status of package policy");
		
		Map<String, Object> testPolicy = getPolicyMap(TestType.Ater_Processing_Package_Error);
		@SuppressWarnings("unchecked")
		Map<String, String> specification = (HashMap<String, String>) testPolicy.get("Specification");
		
		String currentStatusForPolicy = (String) testPolicy.get("Status");
		currentStatusForPolicy = packagePolicyEnforcer.postCheck(specification, currentStatusForPolicy);
	}
	
	private Map<String, Object> getPolicyMap(TestType type) {
		Map<String, Object> testDataMap = new HashMap<String, Object>();
		Map<String, String> policyDataForSpecification = new HashMap<String, String>();
		String policyDataForCurrentStatus = null;
		
		switch(type) {
			case Valid_Contract_Term_Of_Package:
				policyDataForSpecification.put("startTimestamp", Long.toString(System.currentTimeMillis() / 1000));
				policyDataForSpecification.put("endTimestamp", Long.toString((System.currentTimeMillis() / 1000) + 100000));
				policyDataForSpecification.put("maxCount", "10");
				policyDataForCurrentStatus = "";
				testDataMap.put("Specification", policyDataForSpecification);
				testDataMap.put("Status", policyDataForCurrentStatus);
				break;
			case Not_Yet_Contract_Term_Of_Package:
				policyDataForSpecification.put("startTimestamp", Long.toString((System.currentTimeMillis() / 1000) + 100000));
				policyDataForSpecification.put("endTimestamp", Long.toString((System.currentTimeMillis() / 1000) + 900000));
				policyDataForSpecification.put("maxCount", "10");
				policyDataForCurrentStatus = "";
				testDataMap.put("Specification", policyDataForSpecification);
				testDataMap.put("Status", policyDataForCurrentStatus);
				break;
			case Already_Expired_Contract_Term_Of_Package:
				policyDataForSpecification.put("startTimestamp", Long.toString((System.currentTimeMillis() / 1000) - 900000));
				policyDataForSpecification.put("endTimestamp", Long.toString((System.currentTimeMillis() / 1000) - 100000));
				policyDataForSpecification.put("maxCount", "10");
				policyDataForCurrentStatus = "";
				testDataMap.put("Specification", policyDataForSpecification);
				testDataMap.put("Status", policyDataForCurrentStatus);
				break;
			case Equal_Max_Rate_Limie_Of_Package:
				policyDataForSpecification.put("startTimestamp", Long.toString(System.currentTimeMillis() / 1000));
				policyDataForSpecification.put("endTimestamp", Long.toString((System.currentTimeMillis() / 1000) + 100000));
				policyDataForSpecification.put("maxCount", "10");
				policyDataForCurrentStatus = "";
				testDataMap.put("Specification", policyDataForSpecification);
				testDataMap.put("Status", "10");
				break;
			case Exceeded_Max_Rate_Limie_Of_Package:
				policyDataForSpecification.put("startTimestamp", Long.toString(System.currentTimeMillis() / 1000));
				policyDataForSpecification.put("endTimestamp", Long.toString((System.currentTimeMillis() / 1000) + 100000));
				policyDataForSpecification.put("maxCount", "10");
				policyDataForCurrentStatus = "";
				testDataMap.put("Specification", policyDataForSpecification);
				testDataMap.put("Status", "20");
				break;
			case Unlimit_Max_Rate_Of_Package:
				policyDataForSpecification.put("startTimestamp", Long.toString(System.currentTimeMillis() / 1000));
				policyDataForSpecification.put("endTimestamp", Long.toString((System.currentTimeMillis() / 1000) + 100000));
				policyDataForSpecification.put("maxCount", "-1");
				policyDataForCurrentStatus = "";
				testDataMap.put("Specification", policyDataForSpecification);
				testDataMap.put("Status", "0");
				break;
			case Ater_Processing_Package:
				policyDataForSpecification.put("startTimestamp", Long.toString(System.currentTimeMillis() / 1000));
				policyDataForSpecification.put("endTimestamp", Long.toString((System.currentTimeMillis() / 1000) + 100000));
				policyDataForSpecification.put("maxCount", "10");
				policyDataForCurrentStatus = "";
				testDataMap.put("Specification", policyDataForSpecification);
				testDataMap.put("Status", "10");
				break;
			case Ater_Processing_Package_Error:
				policyDataForSpecification.put("startTimestamp", Long.toString(System.currentTimeMillis() / 1000));
				policyDataForSpecification.put("endTimestamp", Long.toString((System.currentTimeMillis() / 1000) + 100000));
				policyDataForSpecification.put("maxCount", "10");
				policyDataForCurrentStatus = "";
				testDataMap.put("Specification", policyDataForSpecification);
				testDataMap.put("Status", "");
				break;
			default:
				break;
		}
		
		return testDataMap;
	}
	
	private enum TestType {
		Valid_Contract_Term_Of_Package, Not_Yet_Contract_Term_Of_Package, Already_Expired_Contract_Term_Of_Package, Equal_Max_Rate_Limie_Of_Package, Exceeded_Max_Rate_Limie_Of_Package, Unlimit_Max_Rate_Of_Package, Ater_Processing_Package, Ater_Processing_Package_Error
	}
}
