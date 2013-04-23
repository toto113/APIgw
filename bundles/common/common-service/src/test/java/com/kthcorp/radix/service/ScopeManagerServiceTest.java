package com.kthcorp.radix.service;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;

import org.json.JSONException;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import com.kthcorp.radix.util.test.junit.MysqlEmbeddableSpringJUnit4ClassRunner;

import com.kthcorp.radix.api.service.ScopeManagerService;
import com.kthcorp.radix.domain.exception.DataBaseProcessingException;
import com.kthcorp.radix.domain.exception.NotFoundException;
import com.kthcorp.radix.domain.exception.ValidateException;
import com.kthcorp.radix.domain.scope.Policy;
import com.kthcorp.radix.domain.scope.ScopePolicies;
import com.kthcorp.radix.service.ScopeManagerServiceTest_resource.MockRoutingResourceManagerDao;
import com.kthcorp.radix.util.FailedTest;
import com.kthcorp.radix.util.UUIDUtils;

@RunWith(MysqlEmbeddableSpringJUnit4ClassRunner.class)
@ContextConfiguration(
		locations = { 
				"classpath:/META-INF/spring/spring-application-context-test.xml", 
				"classpath:/META-INF/spring/spring-service-context-test.xml",  				
				"classpath:/META-INF/spring/spring-dao-mysql-context.xml", 
				"classpath:/META-INF/spring/spring-dao-mybatis-context-test.xml",
				"ScopeManagerServiceTest_resource/mock.xml" })
public class ScopeManagerServiceTest {
	
	@Autowired
	private ScopeManagerService scopeManagerService;
	
	private final byte[] clientID = UUIDUtils.getBytes("34d15b7e-ee1d-419f-9fc0-be22120df363");
	private final byte[] packageID = UUIDUtils.getBytes("f63bec14-876e-11e1-a764-f0def154de37");
	private final byte[] serviceAPIID = UUIDUtils.getBytes("2342146A-8772-11E1-A8C6-F0DEF154DE37");
	
	private final String apiKey = "testAPIKey";
	private final String resourcePath = "/test";
	

	// 요 테스트케이스는  service, package등의 데이타가 이미 있어야 가능하다.
	@FailedTest
	public void createTest() throws ValidateException, DataBaseProcessingException, NoSuchAlgorithmException {
		
		ScopePolicies scopePolicies = new ScopePolicies();
		
		Map<String,String> packageParamsObj = new HashMap<String,String>();
		packageParamsObj.put("test1", "testValue1");
		scopePolicies.setPackageParams(packageParamsObj);
		
		Map<String,String> statusObj = new HashMap<String,String>();
		statusObj.put("test2", "testValue2");
		scopePolicies.setStatus(statusObj);
		
		List<Policy> policyList = new ArrayList<Policy>();
		Policy policy = new Policy();
		policy.setClientID(clientID);
		policy.setPackageID(packageID);
		policy.setPolicyTypeID("usageLimit");
		policyList.add(policy);
		scopePolicies.setPolicyList(policyList);
		
		scopeManagerService.createScopePolicies(apiKey, clientID, packageID, serviceAPIID, scopePolicies);
		
		MockRoutingResourceManagerDao.serviceAPIID = serviceAPIID;
		MockRoutingResourceManagerDao.apiKey = apiKey;
		MockRoutingResourceManagerDao.pathTemplate = resourcePath;
		
	}
	
	@FailedTest
	public void selectTest() throws JSONException, NotFoundException, ValidateException, DataBaseProcessingException {
		
		ScopePolicies ret = scopeManagerService.getScopePolicies(apiKey, resourcePath, clientID);
		Assert.assertNotNull(ret);
		Assert.assertEquals(ret.getPackageParams().get("test1"), "testValue1");
		Assert.assertEquals(ret.getStatus().get("test2"), "testValue2");
		Assert.assertEquals(ret.getPolicyList().get(0).getPolicyTypeID(), "usageLimit");
		Assert.assertTrue(UUIDUtils.compare(ret.getPolicyList().get(0).getClientID(), clientID));
		
		Map<String,String> status = scopeManagerService.getStatus(clientID, apiKey);
		Assert.assertNotNull(status);
		Assert.assertEquals(status.get("test2"), "testValue2");
		
		status.put("test3", "testValue3");
		scopeManagerService.modifyStatus(apiKey, clientID, status);
		Map<String,String> statusGot = scopeManagerService.getStatus(clientID, apiKey);
		Assert.assertNotNull(statusGot);
		Assert.assertEquals(statusGot.get("test3"), "testValue3");
	}
	
	@After
	public void removeTest() throws NotFoundException, ValidateException {
	}
	
	@Test
	public void nullTest() {
	}
	
}
