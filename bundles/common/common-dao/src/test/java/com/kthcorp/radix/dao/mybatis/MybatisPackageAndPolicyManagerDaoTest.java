package com.kthcorp.radix.dao.mybatis;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import junit.framework.Assert;

import org.json.JSONException;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import com.kthcorp.radix.util.test.junit.MysqlEmbeddableSpringJUnit4ClassRunner;

import com.kthcorp.radix.domain.packages.Packages;
import com.kthcorp.radix.domain.policy.Policy;
import com.kthcorp.radix.domain.service.api.serviceAPI.ServiceAPI;
import com.kthcorp.radix.util.UUIDUtils;

@Ignore
@RunWith(MysqlEmbeddableSpringJUnit4ClassRunner.class)
@ContextConfiguration(
		locations = { "classpath:/META-INF/spring/spring-application-context-test.xml", "classpath:/META-INF/spring/spring-dao-mysql-context.xml", "classpath:/META-INF/spring/spring-dao-mybatis-context-test.xml" })
public class MybatisPackageAndPolicyManagerDaoTest {
	
	@Autowired
	private MyBatisBusinessPlatformKeyManagerDaoMapper myBatisBusinessPlatformKeyManagerDao;
	
	@Autowired
	private MyBatisPackageManagerDaoMapper myBatisPackageManagerDao;
	
	@Autowired
	private MyBatisPolicyManagerDaoMapper myBatisPolicyManagerDao;
		
	public static List<Policy> prepareTestPolicyList(byte[] businessPlatformID, String partnerID) throws JSONException {
		List<Policy> policyList = new ArrayList<Policy>();
		
		{
			Policy policy = new Policy();
			policy.setBusinessPlatformID(businessPlatformID);
			policy.setPartnerID(partnerID);
			policy.setPolicyTypeID("usageLimit");
			policy.setName("test_policy_name_1");
			policy.setProperties("{ \"maxCount\": \"10000\", \"starTimestamp\": \"-1\", \"endTimestamp\" : \"-1\", \"condition\" : \"absolute\", \"duration\" : \"1M\" }");
			policyList.add(policy);
		}
		{
			Policy policy = new Policy();
			policy.setBusinessPlatformID(businessPlatformID);
			policy.setPartnerID(partnerID);
			policy.setPolicyTypeID("usageLimit");
			policy.setName("test_policy_name_2");
			policy.setProperties("{ \"maxCount\": \"1000\", \"starTimestamp\": \"-1\", \"endTimestamp\" : \"-1\", \"condition\" : \"absolute\", \"duration\" : \"1D\" }");
			policyList.add(policy);
		}
		{
			Policy policy = new Policy();
			policy.setBusinessPlatformID(businessPlatformID);
			policy.setPartnerID(partnerID);
			policy.setPolicyTypeID("usageTerm");
			policy.setName("test_policy_name_3");
			policy.setProperties("{ \"starTimestamp\": \"-1\", \"endTimestamp\" : \"-1\" }");
			policyList.add(policy);
		}
		return policyList;
	}
	
	public static List<ServiceAPI> prepareTestServiceAPIList(byte[] businessPlatformID, String partnerID) throws JSONException, NoSuchAlgorithmException {
		List<ServiceAPI> ServiceAPIList = new ArrayList<ServiceAPI>();
		
		{
			ServiceAPI api = new ServiceAPI();
			api.setId(UUIDUtils.getBytes("2342146A-8772-11E1-A8C6-F0DEF154DE37"));
			List<Policy> policyList = new ArrayList<Policy>();
			{
				Policy policy = new Policy();
				policy.setBusinessPlatformID(businessPlatformID);
				policy.setPartnerID(partnerID);
				policy.setPolicyTypeID("usageLimit");
				policy.setName("test_policy1_"+System.currentTimeMillis());
				policy.setProperties("{ \"maxCount\": \"30000\", \"starTimestamp\": \"-1\", \"endTimestamp\" : \"-1\", \"condition\" : \"absolute\", \"duration\" : \"1M\" }");
				policyList.add(policy);
			}
			{
				Policy policy = new Policy();
				policy.setBusinessPlatformID(businessPlatformID);
				policy.setPartnerID(partnerID);
				policy.setPolicyTypeID("usageLimit");
				policy.setName("test_policy2_"+System.currentTimeMillis());
				policy.setProperties("{ \"maxCount\": \"1500\", \"starTimestamp\": \"-1\", \"endTimestamp\" : \"-1\", \"condition\" : \"absolute\", \"duration\" : \"1H\" }");
				policyList.add(policy);
			}
			{
				Policy policy = new Policy();
				policy.setBusinessPlatformID(businessPlatformID);
				policy.setPartnerID(partnerID);
				policy.setPolicyTypeID("usageTerm");
				policy.setName("test_policy3_"+System.currentTimeMillis());
				policy.setProperties("{ \"starTimestamp\": \"-1\", \"endTimestamp\" : \"-1\" }");
				policyList.add(policy);
			}
			api.setPolicyList(policyList);
			ServiceAPIList.add(api);
		}
		
		{
			ServiceAPI api = new ServiceAPI();
			api.setId(UUIDUtils.getBytes("2342146A-8772-11E1-A8C6-F0DEF154DE37"));
			List<Policy> policyList = new ArrayList<Policy>();
			{
				Policy policy = new Policy();
				policy.setBusinessPlatformID(businessPlatformID);
				policy.setPartnerID(partnerID);
				policy.setPolicyTypeID("usageLimit");
				policy.setName("test_policy4_"+System.currentTimeMillis());
				policy.setProperties("{ \"maxCount\": \"15000\", \"starTimestamp\": \"-1\", \"endTimestamp\" : \"-1\", \"condition\" : \"absolute\", \"duration\" : \"1D\" }");
				policyList.add(policy);
			}
			{
				Policy policy = new Policy();
				policy.setBusinessPlatformID(businessPlatformID);
				policy.setPartnerID(partnerID);
				policy.setPolicyTypeID("usageTerm");
				policy.setName("test_policy5_"+System.currentTimeMillis());
				policy.setProperties("{ \"starTimestamp\": \"-1\", \"endTimestamp\" : \"-1\" }");
				policyList.add(policy);
			}
			api.setPolicyList(policyList);
			ServiceAPIList.add(api);
		}
		
		return ServiceAPIList;
	}
	
	@Test
	public void doAddPackageAndPolicy() throws JSONException, NoSuchAlgorithmException {
		
		String businessPlatformKey = "59ffa6f4-0901-4ffc-82ad-44687540ab4b";
		Assert.assertEquals(myBatisBusinessPlatformKeyManagerDao.selectExistsCountWithKeyString(businessPlatformKey),1);
		byte[] bpKeyInBytes = UUIDUtils.getBytes(businessPlatformKey);

		// generate unique id using current timestamp value
		//
		String idPostfix = Long.toString((new Date()).getTime());
		String partnerID = "test_partnerID_" + idPostfix;
		
		/* Create Package */
		Packages pkg = new Packages();
		pkg.generateID();
		pkg.setBusinessPlatformID(bpKeyInBytes);
		pkg.setName("test_name_" + idPostfix);
		pkg.setPartnerID("test_partnerid_" + idPostfix);
		
		Assert.assertEquals(myBatisPackageManagerDao.insertPackage(pkg), 1);

		/* Package Policy Test */
		byte[] packageID = pkg.getId();
		Assert.assertTrue(myBatisPackageManagerDao.selectExistsPackageCount(packageID)==1);
		List<Policy> packagePolicies = MybatisPackageAndPolicyManagerDaoTest.prepareTestPolicyList(bpKeyInBytes, partnerID);
		
		// Create Policy
		for(Policy policy : packagePolicies) {
			policy.generateID();
			policy.setPackageID(packageID);
			
			Assert.assertTrue(myBatisPolicyManagerDao.selectExistsPolicyTypeCount(policy.getPolicyTypeID())==1);
			
			Assert.assertTrue(myBatisPolicyManagerDao.selectExistsPackagePolicyCount(packageID, policy.getId())==0);
			Assert.assertEquals(myBatisPolicyManagerDao.insertPackagePolicy(policy), 1);
			Assert.assertTrue(myBatisPolicyManagerDao.selectExistsPackagePolicyCount(packageID, policy.getId())==1);
			
			
			Assert.assertEquals(myBatisPolicyManagerDao.backupPackagePolicy(packageID, policy.getId()), 1);
			Assert.assertEquals(myBatisPolicyManagerDao.deletePackagePolicy(packageID, policy.getId()), 1);
			Assert.assertTrue(myBatisPolicyManagerDao.selectExistsPackagePolicyCount(packageID, policy.getId())==0);
			Assert.assertTrue(myBatisPolicyManagerDao.selectExistsPackagePolicyWithHiddenCount(packageID, policy.getId())==0);

			Assert.assertEquals(myBatisPolicyManagerDao.deleteBackupPackagePolicy(packageID, policy.getId()), 1);
		}
		
		/* Service APIs Test */
		List<ServiceAPI> serviceAPIList = MybatisPackageAndPolicyManagerDaoTest.prepareTestServiceAPIList(bpKeyInBytes, partnerID);
		
		for(ServiceAPI serviceAPI: serviceAPIList) {
		
			// Create Policy
			for(Policy policy : serviceAPI.getPolicyList()) {
				policy.generateID();
				policy.setPackageID(packageID);
				policy.setServiceAPIID(serviceAPI.getId());

				Assert.assertTrue(myBatisPolicyManagerDao.selectExistsPolicyTypeCount(policy.getPolicyTypeID())==1);
				
				Assert.assertTrue(myBatisPolicyManagerDao.selectExistsPackageServiceAPIPolicyCount(packageID, serviceAPI.getId(), policy.getId())==0);
				Assert.assertEquals(myBatisPolicyManagerDao.insertPackageServiceAPIPolicy(policy), 1);
				
				Assert.assertTrue(myBatisPolicyManagerDao.selectExistsPackageServiceAPIPolicyCount(packageID, serviceAPI.getId(), policy.getId())==1);
				Assert.assertEquals(myBatisPolicyManagerDao.backupPackageServiceAPIPolicy(packageID, serviceAPI.getId(), policy.getId()), 1);
				Assert.assertEquals(myBatisPolicyManagerDao.deletePackageServiceAPIPolicy(packageID, serviceAPI.getId(), policy.getId()), 1);
				Assert.assertTrue(myBatisPolicyManagerDao.selectExistsPackageServiceAPIPolicyCount(packageID, serviceAPI.getId(), policy.getId())==0);
				
				Assert.assertEquals(myBatisPolicyManagerDao.deleteBackupPackageServiceAPIPolicy(packageID, serviceAPI.getId(), policy.getId()), 1);
			}
			
		}
		
		Assert.assertEquals(myBatisPackageManagerDao.backupPackage(bpKeyInBytes, partnerID, packageID), 1);
		Assert.assertEquals(myBatisPackageManagerDao.deletePackage(bpKeyInBytes, partnerID, packageID), 1);
		Assert.assertTrue(myBatisPackageManagerDao.selectExistsPackageCount(packageID)==0);
		Assert.assertTrue(myBatisPackageManagerDao.selectExistsPackageWithHiddenCount(packageID)==0);
		
		Assert.assertEquals(myBatisPackageManagerDao.deleteBackupPackage(bpKeyInBytes, partnerID, packageID), 1);
	}
	
}