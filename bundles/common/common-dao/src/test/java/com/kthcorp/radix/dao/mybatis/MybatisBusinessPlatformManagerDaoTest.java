package com.kthcorp.radix.dao.mybatis;

import java.security.NoSuchAlgorithmException;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import com.kthcorp.radix.util.test.junit.MysqlEmbeddableSpringJUnit4ClassRunner;

import com.kthcorp.radix.domain.businessPlatform.BusinessPlatformKey;

@RunWith(MysqlEmbeddableSpringJUnit4ClassRunner.class)
@ContextConfiguration(
		locations = { "classpath:/META-INF/spring/spring-application-context-test.xml", 
				"classpath:/META-INF/spring/spring-dao-mysql-context.xml", 
				"classpath:/META-INF/spring/spring-dao-mybatis-context-test.xml" })
public class MybatisBusinessPlatformManagerDaoTest extends AbstractJUnit4SpringContextTests {
	
	@Autowired
	private MyBatisBusinessPlatformKeyManagerDaoMapper myBatisBusinessPlatformKeyManagerDao;
	
	@Test
	public void businessPlatformKeyTest() throws NoSuchAlgorithmException {
		
		BusinessPlatformKey businessPlatformKey = new BusinessPlatformKey();
		businessPlatformKey.generateKeyAndSecret();
		businessPlatformKey.setRedirectUri("http://test.withapi.com/redirect_uri2");
		businessPlatformKey.setDescription("testDescription2");
		businessPlatformKey.setDomain("test2.withapi.com");
		
		// Here we go
		
		// Create
		myBatisBusinessPlatformKeyManagerDao.insertBusinessPlatformKey(businessPlatformKey);
		
		// Read
		BusinessPlatformKey businessPlatformKeyRetrieved1 = myBatisBusinessPlatformKeyManagerDao.selectBusinessPlatformKeyWithKeyAndSecretString(businessPlatformKey.getKeyString(), businessPlatformKey.getSecret());
		Assert.assertEquals(businessPlatformKey.getKeyString(), businessPlatformKeyRetrieved1.getKeyString());
		
		BusinessPlatformKey businessPlatformKeyRetrieved2 = myBatisBusinessPlatformKeyManagerDao.selectBusinessPlatformKeyWithKeyString(businessPlatformKey.getKeyString());
		Assert.assertEquals(businessPlatformKey.getKeyString(), businessPlatformKeyRetrieved2.getKeyString());
		
		// Check
		int ret1 = myBatisBusinessPlatformKeyManagerDao.selectExistsCount(businessPlatformKey.getKeyString(), businessPlatformKey.getSecret());
		Assert.assertEquals(ret1, 1);
		
		// Update
		BusinessPlatformKey newBusinessPlatformKey = new BusinessPlatformKey();
		newBusinessPlatformKey.setKeyString(businessPlatformKey.getKeyString());
		newBusinessPlatformKey.setSecret(businessPlatformKey.getSecret());
		newBusinessPlatformKey.setRedirectUri("http://test.withapi.com/redirect_uri");
		newBusinessPlatformKey.setDescription("testDescription");
		newBusinessPlatformKey.setDomain("test.withapi.com");
		
		int retUpdated = myBatisBusinessPlatformKeyManagerDao.updateBusinessPlatformKey(newBusinessPlatformKey);
		Assert.assertEquals(retUpdated, 1);
		
		// Delete
		int retDeleted = myBatisBusinessPlatformKeyManagerDao.deleteBusinessPlatformKey(businessPlatformKey.getKeyString(), businessPlatformKey.getSecret());
		Assert.assertEquals(retDeleted, 1);
	}
}
