package com.kthcorp.radix.service;

import java.util.HashMap;
import java.util.Map;

import junit.framework.Assert;

import org.json.JSONException;
import org.json.JSONObject;
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
import com.kthcorp.radix.domain.exception.PolicyRateLimitException;
import com.kthcorp.radix.util.Time;

@RunWith(MysqlEmbeddableSpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/META-INF/spring/policy/spring-all-context-test.xml" })
public class ServicePolicyEnforcerTest extends AbstractJUnit4SpringContextTests {
	
	/**
	 * LOG4j to LOG.
	 */
	private static final Logger LOG = UuidViewableLoggerFactory.getLogger(ServicePolicyEnforcerTest.class);
	
	@Autowired
	private PolicyEnforcer servicePolicyEnforcer;
	
	@Rule
	public ExpectedException expectedException = ExpectedException.none();
	
	@Test
	public void firstRequestAfterBuyingTest() throws PolicyException, JSONException {
		Map<String, Object> testPolicy = getPolicyMap(TestType.First_Request_After_Buying);
		@SuppressWarnings("unchecked")
		Map<String, String> specification = (Map<String, String>) testPolicy.get("Specification");
		String currentStatusForPolicy = (String) testPolicy.get("Status");
		
		currentStatusForPolicy = servicePolicyEnforcer.preCheck(specification, currentStatusForPolicy);
		LOG.debug("firstRequestAfterBuyingTest after preCheck ======= {}", currentStatusForPolicy);
		
		currentStatusForPolicy = servicePolicyEnforcer.postCheck(specification, currentStatusForPolicy);
		LOG.debug("firstRequestAfterBuyingTest after postCheck ======= {}", currentStatusForPolicy);
		
		Assert.assertEquals("1", getStatusParameter(currentStatusForPolicy, "count"));
	}
	
	@Test
	public void validContractTermOfServiceTest() throws JSONException, PolicyException {
		Map<String, Object> testPolicy = getPolicyMap(TestType.Valid_Contract_Term_Of_Service);
		@SuppressWarnings("unchecked")
		Map<String, String> specification = (Map<String, String>) testPolicy.get("Specification");
		String currentStatusForPolicy = (String) testPolicy.get("Status");
		
		currentStatusForPolicy = servicePolicyEnforcer.preCheck(specification, currentStatusForPolicy);
		LOG.debug("validContractTermOfServiceTest after preCheck ======= {}", currentStatusForPolicy);
		
		currentStatusForPolicy = servicePolicyEnforcer.postCheck(specification, currentStatusForPolicy);
		LOG.debug("validContractTermOfServiceTest after postCheck ======= {}", currentStatusForPolicy);
		
		Assert.assertEquals("6", getStatusParameter(currentStatusForPolicy, "count"));
	}
	
	@Test
	public void alreadyExpiredContractTermOfServiceTest() throws PolicyException, JSONException {
		Map<String, Object> testPolicy = getPolicyMap(TestType.Already_Expired_Contract_Term_Of_Service);
		@SuppressWarnings("unchecked")
		Map<String, String> specification = (Map<String, String>) testPolicy.get("Specification");
		String currentStatusForPolicy = (String) testPolicy.get("Status");
		
		currentStatusForPolicy = servicePolicyEnforcer.preCheck(specification, currentStatusForPolicy);
		LOG.debug("alreadyExpiredContractTermOfServiceTest after preCheck ======= {}", currentStatusForPolicy);
		
		currentStatusForPolicy = servicePolicyEnforcer.postCheck(specification, currentStatusForPolicy);
		LOG.debug("alreadyExpiredContractTermOfServiceTest after postCheck ======= {}", currentStatusForPolicy);
		
		Assert.assertEquals("1", getStatusParameter(currentStatusForPolicy, "count"));
	}
	
	@Test
	public void equalMaxRateLimitOfService() throws JSONException, PolicyRateLimitException, PolicyException {
		expectedException.expect(PolicyRateLimitException.class);
		expectedException.expectMessage("Service policy exhausted available usage!!");
		
		Map<String, Object> testPolicy = getPolicyMap(TestType.Equal_Max_Rate_Limie_Of_Service);
		@SuppressWarnings("unchecked")
		Map<String, String> specification = (Map<String, String>) testPolicy.get("Specification");
		String currentStatusForPolicy = (String) testPolicy.get("Status");
		
		currentStatusForPolicy = servicePolicyEnforcer.preCheck(specification, currentStatusForPolicy);
	}
	
	@Test
	public void exceededMaxRateLimieOfServiceTest() throws JSONException, PolicyRateLimitException, PolicyException {
		expectedException.expect(PolicyRateLimitException.class);
		expectedException.expectMessage("Service policy exhausted available usage!!");
		
		Map<String, Object> testPolicy = getPolicyMap(TestType.Exceeded_Max_Rate_Limie_Of_Service);
		@SuppressWarnings("unchecked")
		Map<String, String> specification = (Map<String, String>) testPolicy.get("Specification");
		String currentStatusForPolicy = (String) testPolicy.get("Status");
		
		currentStatusForPolicy = servicePolicyEnforcer.preCheck(specification, currentStatusForPolicy);
	}
	
	@Test
	public void unlimitMaxRateOfServiceTest() throws PolicyException, JSONException {
		Map<String, Object> testPolicy = getPolicyMap(TestType.Unlimit_Max_Rate_Of_Service);
		@SuppressWarnings("unchecked")
		Map<String, String> specification = (Map<String, String>) testPolicy.get("Specification");
		String currentStatusForPolicy = (String) testPolicy.get("Status");
		
		for(int i = 0; i < 100; i++) {
			currentStatusForPolicy = servicePolicyEnforcer.preCheck(specification, currentStatusForPolicy);
			LOG.debug("unlimitMaxRateOfServiceTest after preCheck ======= {}", currentStatusForPolicy);
			
			currentStatusForPolicy = servicePolicyEnforcer.postCheck(specification, currentStatusForPolicy);
			LOG.debug("unlimitMaxRateOfServiceTest after postCheck ======= {}", currentStatusForPolicy);
		}
		
		Assert.assertEquals("100", getStatusParameter(currentStatusForPolicy, "count"));
	}
	
	@Test
	public void beforeProcessingServiceError() throws PolicyException, PolicyRateLimitException, JSONException {
		expectedException.expect(PolicyRateLimitException.class);
		expectedException.expectMessage("service don't begin yet");
		
		Map<String, Object> testPolicy = getPolicyMap(TestType.Before_Processing_Service_Error);
		@SuppressWarnings("unchecked")
		Map<String, String> specification = (Map<String, String>) testPolicy.get("Specification");
		String currentStatusForPolicy = (String) testPolicy.get("Status");
		
		currentStatusForPolicy = servicePolicyEnforcer.preCheck(specification, currentStatusForPolicy);
	}
	
	@Test
	public void aterProcessingServiceError() throws PolicyException, PolicyRateLimitException, JSONException {
		expectedException.expect(PolicyRateLimitException.class);
		expectedException.expectMessage("Not found a status of serviceAPI policy");
		
		Map<String, Object> testPolicy = getPolicyMap(TestType.Ater_Processing_Service_Error);
		@SuppressWarnings("unchecked")
		Map<String, String> specification = (Map<String, String>) testPolicy.get("Specification");
		String currentStatusForPolicy = (String) testPolicy.get("Status");
		
		currentStatusForPolicy = servicePolicyEnforcer.postCheck(specification, currentStatusForPolicy);
	}
	
	private Map<String, Object> getPolicyMap(TestType type) throws JSONException {
		Map<String, Object> testDataMap = new HashMap<String, Object>();
		Map<String, String> serviceDataForSpecification = new HashMap<String, String>();
		String serviceDataForCurrentStatus = null;
		
		JSONObject initStatus = new JSONObject();
		switch(type) {
			case First_Request_After_Buying:
				serviceDataForSpecification.put("duration", "1H");
				serviceDataForSpecification.put("maxCount", "10");
				serviceDataForCurrentStatus = "";
				testDataMap.put("Specification", serviceDataForSpecification);
				testDataMap.put("Status", serviceDataForCurrentStatus);
				break;
			case Valid_Contract_Term_Of_Service:
				serviceDataForSpecification.put("duration", "1H");
				serviceDataForSpecification.put("maxCount", "10");
				testDataMap.put("Specification", serviceDataForSpecification);
				initStatus.put("from", Time.strNow());
				initStatus.put("to", Time.dateAddFormat(Time.getTimeUnit("H"), 1, Time.now()));
				initStatus.put("count", "5");
				testDataMap.put("Status", initStatus.toString());
				break;
			case Already_Expired_Contract_Term_Of_Service:
				serviceDataForSpecification.put("duration", "1H");
				serviceDataForSpecification.put("maxCount", "10");
				testDataMap.put("Specification", serviceDataForSpecification);
				initStatus.put("from", "2012-04-10 16:32:00");
				initStatus.put("to", "2012-04-10 17:32:00");
				initStatus.put("count", "9");
				testDataMap.put("Status", initStatus.toString());
				break;
			case Equal_Max_Rate_Limie_Of_Service:
				serviceDataForSpecification.put("duration", "1H");
				serviceDataForSpecification.put("maxCount", "10");
				testDataMap.put("Specification", serviceDataForSpecification);
				initStatus.put("from", Time.strNow());
				initStatus.put("to", Time.dateAddFormat(Time.getTimeUnit("H"), 1, Time.now()));
				initStatus.put("count", "10");
				testDataMap.put("Status", initStatus.toString());
				break;
			case Exceeded_Max_Rate_Limie_Of_Service:
				serviceDataForSpecification.put("duration", "1H");
				serviceDataForSpecification.put("maxCount", "10");
				testDataMap.put("Specification", serviceDataForSpecification);
				initStatus.put("from", Time.strNow());
				initStatus.put("to", Time.dateAddFormat(Time.getTimeUnit("H"), 1, Time.now()));
				initStatus.put("count", "20");
				testDataMap.put("Status", initStatus.toString());
				break;
			case Unlimit_Max_Rate_Of_Service:
				serviceDataForSpecification.put("duration", "1H");
				serviceDataForSpecification.put("maxCount", "-1");
				serviceDataForCurrentStatus = "";
				testDataMap.put("Specification", serviceDataForSpecification);
				testDataMap.put("Status", serviceDataForCurrentStatus);
				break;
			case Before_Processing_Service_Error:
				serviceDataForSpecification.put("duration", "1H");
				serviceDataForSpecification.put("maxCount", "10");
				testDataMap.put("Specification", serviceDataForSpecification);
				initStatus.put("from", Time.dateAddFormat(Time.getTimeUnit("H"), 1, Time.now()));
				initStatus.put("to", Time.dateAddFormat(Time.getTimeUnit("H"), 2, Time.now()));
				initStatus.put("count", "20");
				testDataMap.put("Status", initStatus.toString());
				break;
			case Ater_Processing_Service_Error:
				serviceDataForSpecification.put("duration", "1H");
				serviceDataForSpecification.put("maxCount", "-1");
				serviceDataForCurrentStatus = "";
				testDataMap.put("Specification", serviceDataForSpecification);
				testDataMap.put("Status", serviceDataForCurrentStatus);
				break;
			default:
				break;
		}
		return testDataMap;
	}
	
	private enum TestType {
		First_Request_After_Buying, Valid_Contract_Term_Of_Service, Already_Expired_Contract_Term_Of_Service, Equal_Max_Rate_Limie_Of_Service, Exceeded_Max_Rate_Limie_Of_Service, Unlimit_Max_Rate_Of_Service, Ater_Processing_Service_Error, Before_Processing_Service_Error
	}
	
	private String getStatusParameter(String status, String key) throws JSONException {
		JSONObject currentStatus = new JSONObject(status);
		return currentStatus.getString(key);
	}
}
