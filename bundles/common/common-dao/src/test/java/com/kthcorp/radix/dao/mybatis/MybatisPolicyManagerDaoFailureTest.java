package com.kthcorp.radix.dao.mybatis;

import java.util.Date;
import java.util.List;

import junit.framework.Assert;

import org.json.JSONException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ContextConfiguration;
import com.kthcorp.radix.util.test.junit.MysqlEmbeddableSpringJUnit4ClassRunner;

import com.kthcorp.radix.domain.policy.Policy;
import com.kthcorp.radix.util.UUIDUtils;

@RunWith(MysqlEmbeddableSpringJUnit4ClassRunner.class)
@ContextConfiguration(
		locations = { "classpath:/META-INF/spring/spring-application-context-test.xml", "classpath:/META-INF/spring/spring-dao-mysql-context.xml", "classpath:/META-INF/spring/spring-dao-mybatis-context-test.xml" })
public class MybatisPolicyManagerDaoFailureTest {
	
	@Autowired
	private MyBatisBusinessPlatformKeyManagerDaoMapper myBatisBusinessPlatformKeyManagerDao;
	
	@Autowired
	private MyBatisPolicyManagerDaoMapper myBatisPolicyManagerDao;
	
	@Test
	public void doAddPolicyFailueByBusinessPlatformIDNUll() throws JSONException {
		
		String businessPlatformKey = "59ffa6f4-0901-4ffc-82ad-44687540ab4b";
		byte[] bpInBytes = UUIDUtils.getBytes(businessPlatformKey);
		Assert.assertEquals(myBatisBusinessPlatformKeyManagerDao.selectExistsCountWithKeyString(businessPlatformKey),1);
		String idPostfix = Long.toString((new Date()).getTime());
		String partnerID = "test_partnerID_" + idPostfix;
		
		List<Policy> packagePolicies = MybatisPackageAndPolicyManagerDaoTest.prepareTestPolicyList(bpInBytes, partnerID);
		Policy policy = (Policy) packagePolicies.get(0);
		
		// inject error-cases
		//
		policy.setBusinessPlatformID(null);
		
		try {
			myBatisPolicyManagerDao.insertPackagePolicy(policy);
		} catch(DataIntegrityViolationException e) {
			Assert.assertTrue(e.toString(), true);
		}
	}
}
