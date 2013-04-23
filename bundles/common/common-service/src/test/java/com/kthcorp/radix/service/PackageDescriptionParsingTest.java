package com.kthcorp.radix.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import com.kthcorp.radix.util.UuidViewableLoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import com.kthcorp.radix.util.test.junit.MysqlEmbeddableSpringJUnit4ClassRunner;

import com.kthcorp.radix.api.service.PackageDescriptionParser;
import com.kthcorp.radix.api.service.PackageManagerService;
import com.kthcorp.radix.api.service.PolicyManagerService;
import com.kthcorp.radix.domain.exception.ValidateException;
import com.kthcorp.radix.domain.packages.Packages;
import com.kthcorp.radix.domain.policy.Policy;
import com.kthcorp.radix.domain.service.api.serviceAPI.ServiceAPI;
import com.kthcorp.radix.service.parser.packages.PackageDescriptionParserFactory;
import com.kthcorp.radix.util.FailedTest;
import com.kthcorp.radix.util.UUIDUtils;

@RunWith(MysqlEmbeddableSpringJUnit4ClassRunner.class)
@ContextConfiguration(
		locations = { 
				"classpath:/META-INF/spring/spring-application-context-test.xml", 
				"classpath:/META-INF/spring/spring-dao-hazelcast-context.xml", 
				"classpath:/META-INF/spring/spring-service-context-test.xml", 
				"classpath:/META-INF/spring/spring-dao-mysql-context.xml", 
				"classpath:/META-INF/spring/spring-dao-mybatis-context-test.xml" })
public class PackageDescriptionParsingTest {
	
	private final static Logger LOG = UuidViewableLoggerFactory.getLogger(PackageDescriptionParsingTest.class);
	private String description;
	
	private final byte[] businessPlatformID = UUIDUtils.getBytes("59ffa6f4-0901-4ffc-82ad-44687540ab4b");
	private final String partnerID = "partner ID";
	
	@Autowired
	private PackageManagerService packageManagerService;

	@Autowired
	private PolicyManagerService policyManagerService;

	@Before
	public void loadDescription() {
		BufferedReader fileReader = new BufferedReader(new InputStreamReader(Thread.currentThread().getContextClassLoader().getResourceAsStream("package_v0.3.xml")));
		String line = null;
		try {
			while((line = fileReader.readLine()) != null) {
				if(description == null) {
					description = line;
				} else {
					description = description + line;
				}
			}
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void parseTest() throws ValidateException {
		LOG.info("Description->" + description);
		try {
			LOG.info("Description(URI)->" + URLEncoder.encode(description, "UTF-8"));
		} catch(UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		PackageDescriptionParser parser = PackageDescriptionParserFactory.createParser("application/xml", this.description, policyManagerService);
		Packages packages = parser.getPackage(businessPlatformID, partnerID, false);
		
		Assert.assertTrue(packages.getName() != null);
		
		List<ServiceAPI> serviceAPIs = packages.getServiceApiList();
		
		Assert.assertEquals(serviceAPIs.size(), 2);
		
		List<Policy> apiPolicies = serviceAPIs.get(0).getPolicyList();
		Assert.assertEquals(apiPolicies.size(), 3);
		
		for(Policy policy : apiPolicies) {
			Assert.assertTrue(policy.getPolicyTypeID().equals("usageLimit")||policy.getPolicyTypeID().equals("usageTerm"));
			LOG.info("ServiceAPI(id:" + serviceAPIs.get(0).getId() + "),policy::"+policy.toString());
		}
		
		List<Policy> policies = packages.getPolicyList();
		Assert.assertEquals(policies.size(), 2);
		for(Policy policy : policies) {
			Assert.assertTrue(policy.getPolicyTypeID().equals("usageLimit")||policy.getPolicyTypeID().equals("usageTerm"));
			LOG.info("Package,Policy::"+policy.toString());
		}
	}
	
	@FailedTest
	public void createAndModifyAndRemove() throws ValidateException, NoSuchAlgorithmException {
		LOG.info("Description->" + description);
		try {
			LOG.info("Description(URI)->" + URLEncoder.encode(description, "UTF-8"));
		} catch(UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		PackageDescriptionParser parser = PackageDescriptionParserFactory.createParser("application/xml", this.description, policyManagerService);
		Packages packages = parser.getPackage(businessPlatformID, partnerID, false);
		
		Assert.assertTrue(packages.getName() != null);
		
		List<ServiceAPI> serviceAPIs = packages.getServiceApiList();
		
		Assert.assertEquals(serviceAPIs.size(), 2);
		
		List<Policy> apiPolicies = serviceAPIs.get(0).getPolicyList();
		Assert.assertEquals(apiPolicies.size(), 3);
		
		for(Policy policy : apiPolicies) {
			Assert.assertTrue(policy.getPolicyTypeID().equals("usageLimit")||policy.getPolicyTypeID().equals("usageTerm"));
			LOG.info("ServiceAPI(id:" + serviceAPIs.get(0).getId() + "),policy::"+policy.toString());
		}
		
		List<Policy> policies = packages.getPolicyList();
		Assert.assertEquals(policies.size(), 2);
		for(Policy policy : policies) {
			Assert.assertTrue(policy.getPolicyTypeID().equals("usageLimit")||policy.getPolicyTypeID().equals("usageTerm"));
			LOG.info("Package,Policy::"+policy.toString());
		}
		
		// Create/Make Package
		byte[] packageID = packageManagerService.createPackage(packages);
		Assert.assertTrue(packageID != null);
		
		String partnerID = packages.getPartnerID();
		
		// Get Package
		packages = packageManagerService.getPackage(partnerID, packageID);
		Assert.assertTrue(packages.getId()!=null);
		Assert.assertTrue(packages.getServiceApiList().size()==2);
		Assert.assertTrue(packages.getServiceApiList().get(0).getPolicyList().size()==3);
		
		// Remove Service API
		ServiceAPI serviceAPI2 = packages.getServiceApiList().get(1);
		packages.getServiceApiList().remove(1);
		packageManagerService.modifyPackage(packages, true);
		
		packages = packageManagerService.getPackage(partnerID, packageID);
		Assert.assertTrue(packages.getId()!=null);
		Assert.assertTrue(packages.getServiceApiList().size()==1);
		
		// Add Service API's policy
		ServiceAPI serviceAPI1 = packages.getServiceApiList().get(0);
		List<Policy> policyList = serviceAPI1.getPolicyList();
		
		Policy newPolicy = new Policy();
		newPolicy.generateID();
		newPolicy.setBusinessPlatformID(packages.getBusinessPlatformID());
		newPolicy.setName("test");
		newPolicy.setPartnerID(packages.getPartnerID());
		newPolicy.setPackageID(packageID);
		newPolicy.setServiceAPIID(serviceAPI1.getId());
		newPolicy.setPolicyTypeID("usageLimit");
		newPolicy.addProperty("startTimestamp", "123123123");
		newPolicy.addProperty("endTimestamp", "23423423324");
		newPolicy.addProperty("maxCount", "10000");
		newPolicy.addProperty("duration", "15D");
		newPolicy.addProperty("condition", "absolute");
		policyList.add(newPolicy);
		
		serviceAPI1.setPolicyList(policyList);
		
		
		packages.getServiceApiList().set(0, serviceAPI1);
		
		packageManagerService.modifyPackage(packages, true);
		
		packages = packageManagerService.getPackage(partnerID, packageID);
		Assert.assertTrue(packages.getId()!=null);
		Assert.assertTrue(packages.getServiceApiList().size()==1);
		Assert.assertTrue(packages.getServiceApiList().get(0).getPolicyList().size()==4);

		// Modify Policy (Remove Policy Parameter)
		newPolicy.removeProperty("minCount");
		policyList.set(1, newPolicy);
		serviceAPI1.setPolicyList(policyList);
		packages.getServiceApiList().set(0, serviceAPI1);
		
		packageManagerService.modifyPackage(packages, true);
		
		packages = packageManagerService.getPackage(partnerID, packageID);
		Assert.assertTrue(packages.getId()!=null);
		Assert.assertTrue(packages.getServiceApiList().size()==1);
		Assert.assertTrue(packages.getServiceApiList().get(0).getPolicyList().size()==3);
		
		// Remove Policy
		policyList.remove(1);
		serviceAPI1.setPolicyList(policyList);
		
		packages.getServiceApiList().set(0, serviceAPI1);
		
		packageManagerService.modifyPackage(packages, true);
		
		packages = packageManagerService.getPackage(partnerID, packageID);
		Assert.assertTrue(packages.getId()!=null);
		Assert.assertTrue(packages.getServiceApiList().size()==1);
		Assert.assertEquals(packages.getServiceApiList().get(0).getPolicyList().size(), 3);

		// Add Service API
		packages.getServiceApiList().add(serviceAPI2);
		packageManagerService.modifyPackage(packages, true);
		
		packages = packageManagerService.getPackage(partnerID, packageID);
		Assert.assertTrue(packages.getId()!=null);
		Assert.assertTrue(packages.getServiceApiList().size()==2);
		Assert.assertEquals(packages.getServiceApiList().get(0).getPolicyList().size(), 3);

		
		// Remove Package
		Assert.assertTrue(packageManagerService.removePackage(businessPlatformID, partnerID, packageID, false));
	}
	
}
