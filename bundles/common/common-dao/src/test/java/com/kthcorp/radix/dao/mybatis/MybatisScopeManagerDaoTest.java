package com.kthcorp.radix.dao.mybatis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;

import org.json.JSONException;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import com.kthcorp.radix.util.test.junit.MysqlEmbeddableSpringJUnit4ClassRunner;

import com.kthcorp.radix.domain.scope.Policy;
import com.kthcorp.radix.domain.scope.ScopePoliciesDAO;
import com.kthcorp.radix.util.UUIDUtils;

@Ignore
@RunWith(MysqlEmbeddableSpringJUnit4ClassRunner.class)
@ContextConfiguration(
		locations = { "classpath:/META-INF/spring/spring-application-context-test.xml", "classpath:/META-INF/spring/spring-dao-mysql-context.xml", "classpath:/META-INF/spring/spring-dao-mybatis-context-test.xml" })
public class MybatisScopeManagerDaoTest extends AbstractJUnit4SpringContextTests {
	
	@Autowired
	private MyBatisScopeManagerDaoMapper myBatisScopeManagerDaoMapper;
	
	private final String clientID = "34d15b7e-ee1d-419f-9fc0-be22120df363";
	private final String packageID = "f63bec14-876e-11e1-a764-f0def154de37";
	private final byte[] serviceAPIID = UUIDUtils.getBytes("2342146A-8772-11E1-A8C6-F0DEF154DE37");
	
	private final String apiKey = "testAPIKey";
	
	@Before
	public void createTest() throws JSONException {
		
		ScopePoliciesDAO scopePoliciesDAO = new ScopePoliciesDAO();
		scopePoliciesDAO.setApiKey(apiKey);
		scopePoliciesDAO.setClientID(UUIDUtils.getBytes(clientID));
		scopePoliciesDAO.setPackageID(UUIDUtils.getBytes(packageID));
		scopePoliciesDAO.setServiceAPIID(serviceAPIID);
		
		Map<String,String> packageParamsObj = new HashMap<String,String>();
		packageParamsObj.put("test1", "testValue1");
		scopePoliciesDAO.setPackageParamsObj(packageParamsObj);
		
		Map<String,String> statusObj = new HashMap<String,String>();
		statusObj.put("test2", "testValue2");
		scopePoliciesDAO.setStatusObj(statusObj);
		
		List<Policy> policyList = new ArrayList<Policy>();
		Policy policy = new Policy();
		policy.setClientID(UUIDUtils.getBytes(clientID));
		policy.setPackageID(UUIDUtils.getBytes(packageID));
		policyList.add(policy);
		scopePoliciesDAO.setPolicyList(policyList);
		
		Assert.assertTrue(myBatisScopeManagerDaoMapper.insertScopePolicies(scopePoliciesDAO)>0);
	}
	
	@Test
	public void selectTest() throws JSONException {
		List<ScopePoliciesDAO> daoList = myBatisScopeManagerDaoMapper.selectScopePoliciesDAOListWithAPIKey(apiKey, UUIDUtils.getBytes(clientID));
		Assert.assertNotNull(daoList);
		Assert.assertTrue(daoList.size()==1);
		ScopePoliciesDAO ret = daoList.get(0);
		Assert.assertNotNull(ret);
		Assert.assertEquals(ret.getApiKey(), apiKey);
		Assert.assertTrue(UUIDUtils.compare(ret.getClientID(), UUIDUtils.getBytes(clientID)));
		Assert.assertTrue(UUIDUtils.compare(ret.getPackageID(), UUIDUtils.getBytes(packageID)));
		Assert.assertEquals(ret.getPackageParamsMap().get("test1"), "testValue1");
		Assert.assertEquals(ret.getStatusMap().get("test2"), "testValue2");
		Assert.assertTrue(UUIDUtils.compare(ret.getPolicyListReal().get(0).getClientID(), UUIDUtils.getBytes(clientID)));
		
		@SuppressWarnings("deprecation")
		String status = myBatisScopeManagerDaoMapper.selectStatus(apiKey, UUIDUtils.getBytes(clientID));
		Assert.assertNotNull(status);
		
		ScopePoliciesDAO scopePoliciesDAO = new ScopePoliciesDAO();
		Map<String,String> statusObj = new HashMap<String,String>();
		statusObj.put("test3", "testValue3");
		scopePoliciesDAO.setStatusObj(statusObj);
		
		myBatisScopeManagerDaoMapper.updateStatus(apiKey, UUIDUtils.getBytes(clientID), scopePoliciesDAO.getStatus());
		
		daoList = myBatisScopeManagerDaoMapper.selectScopePoliciesDAOListWithAPIKey(apiKey, UUIDUtils.getBytes(clientID));
		Assert.assertNotNull(daoList);
		Assert.assertTrue(daoList.size()==1);
		ret = daoList.get(0);
		Assert.assertEquals(ret.getStatusMap().get("test3"), "testValue3");
	}
	
	@After
	public void removeTest() {
		Assert.assertTrue(myBatisScopeManagerDaoMapper.deleteScopePoliciesWithAPIKey(apiKey, UUIDUtils.getBytes(clientID))>0);
		myBatisScopeManagerDaoMapper.deleteScopePolicies(apiKey, UUIDUtils.getBytes(clientID), UUIDUtils.getBytes(packageID));
		myBatisScopeManagerDaoMapper.deleteScopePoliciesWithClientID(UUIDUtils.getBytes(clientID));
		myBatisScopeManagerDaoMapper.deleteScopePoliciesWithCP(UUIDUtils.getBytes(clientID), UUIDUtils.getBytes(packageID));
	}
}
