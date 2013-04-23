package com.kthcorp.radix.service;

import java.security.NoSuchAlgorithmException;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import com.kthcorp.radix.util.test.junit.MysqlEmbeddableSpringJUnit4ClassRunner;

import com.kthcorp.radix.api.service.BusinessPlatformKeyManagerService;
import com.kthcorp.radix.domain.businessPlatform.BusinessPlatformKey;
import com.kthcorp.radix.domain.exception.AlreadyExistException;
import com.kthcorp.radix.domain.exception.DataBaseProcessingException;
import com.kthcorp.radix.domain.exception.NotSupportException;
import com.kthcorp.radix.domain.exception.ValidateException;

@RunWith(MysqlEmbeddableSpringJUnit4ClassRunner.class)
@ContextConfiguration(
		locations = { "classpath:/META-INF/spring/spring-application-context-test.xml", "classpath:/META-INF/spring/spring-dao-hazelcast-context.xml", "classpath:/META-INF/spring/spring-service-context-test.xml", "classpath:/META-INF/spring/spring-dao-mysql-context.xml", "classpath:/META-INF/spring/spring-dao-mybatis-context-test.xml" })
public class BusinessPlatformKeyManagerTest {
	
	@Autowired
	private BusinessPlatformKeyManagerService businessPlatformKeyManagerService;
	
	private String description = "test";
	private String domain1 = "test.test.com";
	private String domain2 = "test2.test.com";
	private String redirectUri = "http://redirectURI.com";
	
	@Test
	public void test() throws ValidateException, AlreadyExistException, DataBaseProcessingException, NotSupportException, NoSuchAlgorithmException {
		
		// Create
		BusinessPlatformKey bKey = businessPlatformKeyManagerService.createBusinessPlatformKey(description, domain1, redirectUri);
		BusinessPlatformKey bKey2 = businessPlatformKeyManagerService.createBusinessPlatformKey(description, domain2, redirectUri);
		Assert.assertNotNull(bKey);
		Assert.assertNotNull(bKey2);
		
		// isExists
		Assert.assertTrue(businessPlatformKeyManagerService.isExists(bKey.getKeyString(), bKey.getSecret()));
		Assert.assertFalse(businessPlatformKeyManagerService.isExists(bKey.getKeyString(), "0123456789012345678901234567890123456789"));
		Assert.assertEquals(businessPlatformKeyManagerService.getBusinessPlatformDomain(bKey.getKeyString()), domain1);
		Assert.assertNotNull(businessPlatformKeyManagerService.getBusinessPlatformKey(bKey.getKeyString(), bKey.getSecret()));
		
		// Modify
		Assert.assertTrue(businessPlatformKeyManagerService.modifyBusinessPlatformKey(bKey.getKeyString(), bKey.getSecret(), bKey));
		Assert.assertFalse(businessPlatformKeyManagerService.modifyBusinessPlatformKey("012345678901234567890123456789012345", bKey2.getSecret(), bKey2));
		
		// Remove
		Assert.assertTrue(businessPlatformKeyManagerService.removeBusinessPlatformKey(bKey.getKeyString(), bKey.getSecret()));
	}
	
}
