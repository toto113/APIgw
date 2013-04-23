package com.kthcorp.radix.dao.mybatis;

import java.security.NoSuchAlgorithmException;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import com.kthcorp.radix.util.test.junit.MysqlEmbeddableSpringJUnit4ClassRunner;

import com.kthcorp.radix.domain.client.ClientKey;
import com.kthcorp.radix.domain.client.ClientKeyType;
import com.kthcorp.radix.util.UUIDUtils;

@RunWith(MysqlEmbeddableSpringJUnit4ClassRunner.class)
@ContextConfiguration(
		locations = { "classpath:/META-INF/spring/spring-application-context-test.xml", "classpath:/META-INF/spring/spring-dao-mysql-context.xml", "classpath:/META-INF/spring/spring-dao-mybatis-context-test.xml" })
public class MybatisClientKeyManagerDaoTest extends AbstractJUnit4SpringContextTests {
	
	@Autowired
	private MyBatisClientKeyManagerDaoMapper myBatisClientKeyManagerDao;
	
	@Autowired
	private MyBatisBusinessPlatformKeyManagerDaoMapper myBatisBusinessPlatformKeyManagerDao;
	
	@Test
	public void createTest() throws NoSuchAlgorithmException {
		String businessPlatformKey = "59ffa6f4-0901-4ffc-82ad-44687540ab4b";
		
		ClientKey clientKey = new ClientKey();
		clientKey.generateKeyAndSecret();
		clientKey.setRedirectUri("http://test.withapi.com/redirect_uri");
		clientKey.setPartnerID("testPartnerID");
		clientKey.setType(ClientKeyType.DEVELOPMENT);
		
		// Here we go
		
		// Get Business Platform Information
		Assert.assertEquals(myBatisBusinessPlatformKeyManagerDao.selectExistsCountWithKeyString(businessPlatformKey),1);
		clientKey.setBusinessPlatformID(UUIDUtils.getBytes(businessPlatformKey));
		
		// Create
		myBatisClientKeyManagerDao.insertClientKey(clientKey);		
	}
	
	@Test
	public void clientKeyTest() throws NoSuchAlgorithmException {
		
		String businessPlatformKey = "59ffa6f4-0901-4ffc-82ad-44687540ab4b";
		
		ClientKey clientKey = new ClientKey();
		clientKey.generateKeyAndSecret();
		clientKey.setRedirectUri("http://test.withapi.com/redirect_uri");
		clientKey.setPartnerID("testPartnerID");
		clientKey.setType(ClientKeyType.DEVELOPMENT);
		
		// Here we go
		
		// Get Business Platform Information
		byte[] bpKeyInBytes = UUIDUtils.getBytes(businessPlatformKey);
		Assert.assertEquals(myBatisBusinessPlatformKeyManagerDao.selectExistsCountWithKeyString(businessPlatformKey),1);
		clientKey.setBusinessPlatformID(UUIDUtils.getBytes(businessPlatformKey));
		
		// Create
		myBatisClientKeyManagerDao.insertClientKey(clientKey);
		
		// Read
		ClientKey clientKeyRetrieved1 = myBatisClientKeyManagerDao.selectClientKey(bpKeyInBytes, clientKey.getPartnerID(), clientKey.getType(), clientKey.getSecret());
		Assert.assertEquals(clientKey.getKeyString(), clientKeyRetrieved1.getKeyString());
		
		ClientKey clientKeyRetrieved2 = myBatisClientKeyManagerDao.selectClientKeyWithKeyAndSecretString(clientKey.getKeyString(), clientKey.getSecret(), clientKey.getType());
		Assert.assertEquals(clientKey.getKeyString(), clientKeyRetrieved2.getKeyString());
		
		ClientKey clientKeyRetrieved3 = myBatisClientKeyManagerDao.selectClientKeyWithKeyString(clientKey.getKeyString());
		Assert.assertEquals(clientKey.getKeyString(), clientKeyRetrieved3.getKeyString());
		
		// Check
		int ret1 = myBatisClientKeyManagerDao.isExists(bpKeyInBytes, clientKey.getPartnerID(), clientKey.getType(), clientKey.getSecret());
		Assert.assertEquals(ret1, 1);
		
		int ret2 = myBatisClientKeyManagerDao.isExistsWithKeyString(clientKey.getKeyString(), clientKey.getSecret());
		Assert.assertEquals(ret2, 1);
		
		// Update
		ClientKey newClientKey = new ClientKey();
		newClientKey.setKeyString(clientKey.getKeyString());
		newClientKey.setSecret(clientKey.getSecret());
		newClientKey.setRedirectUri("http://test.withapi.com/redirect_uri2");
		newClientKey.setPartnerID("testPartnerID2");
		newClientKey.setType(ClientKeyType.DEVELOPMENT);
		newClientKey.setBusinessPlatformID(bpKeyInBytes);
		
		int retUpdated = myBatisClientKeyManagerDao.updateClientKey(newClientKey);
		Assert.assertEquals(retUpdated, 1);
		
		// Delete
		int retDeleted = myBatisClientKeyManagerDao.deleteClientKey(clientKey.getKeyString(), clientKey.getType(), clientKey.getSecret());
		Assert.assertEquals(retDeleted, 1);
	}
}
