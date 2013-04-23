package com.kthcorp.radix.service;

import java.security.NoSuchAlgorithmException;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import com.kthcorp.radix.util.test.junit.MysqlEmbeddableSpringJUnit4ClassRunner;

import com.kthcorp.radix.api.service.ClientKeyManagerService;
import com.kthcorp.radix.domain.client.ClientKey;
import com.kthcorp.radix.domain.client.ClientKeyType;
import com.kthcorp.radix.domain.exception.AlreadyExistException;
import com.kthcorp.radix.domain.exception.DataBaseProcessingException;
import com.kthcorp.radix.domain.exception.NotSupportException;
import com.kthcorp.radix.domain.exception.ValidateException;
import com.kthcorp.radix.util.UUIDUtils;

@RunWith(MysqlEmbeddableSpringJUnit4ClassRunner.class)
@ContextConfiguration(
		locations = { "classpath:/META-INF/spring/spring-application-context-test.xml", "classpath:/META-INF/spring/spring-dao-hazelcast-context.xml", "classpath:/META-INF/spring/spring-service-context-test.xml", "classpath:/META-INF/spring/spring-dao-mysql-context.xml", "classpath:/META-INF/spring/spring-dao-mybatis-context-test.xml" })
public class ClientKeyManagerTest {
	
	@Autowired
	private ClientKeyManagerService clientKeyManagerService;
	
	private final String partnerID = "testPartner199";
	private final ClientKeyType clientKeyType = ClientKeyType.PRODUCTION;
	private String redirectUri = "http://redirectURI.com";
	private final String businessPlatformKey = "59ffa6f4-0901-4ffc-82ad-44687540ab4b";
	private final String applicationName = "testApplicationName";
	private final String applicationDescription = "testApplicationDescription";
	
	@Test
	public void test() throws ValidateException, AlreadyExistException, DataBaseProcessingException, NotSupportException, NoSuchAlgorithmException {
		
		byte[] businessPlatformID = UUIDUtils.getBytes(businessPlatformKey);
		ClientKey clientKey = clientKeyManagerService.createClientKey(businessPlatformID, partnerID, clientKeyType, redirectUri, applicationName, applicationDescription);
		
		List<ClientKey> clientKeyList = clientKeyManagerService.getClientKey(partnerID);
		
		Assert.assertTrue(clientKeyList.size()>0);
		Assert.assertTrue(clientKeyList.get(0).getKeyString().equals(clientKey.getKeyString()));
		
		Assert.assertTrue(clientKeyManagerService.isExists(clientKey.getKeyString(), clientKey.getSecret()));
		Assert.assertTrue(clientKeyManagerService.isExists(businessPlatformID, partnerID, clientKeyType, clientKey.getSecret()));
		Assert.assertTrue(clientKeyManagerService.getClientKey(businessPlatformID, partnerID, clientKeyType, clientKey.getSecret())!=null);
		Assert.assertTrue(clientKeyManagerService.getClientKey(clientKey.getKeyString(), clientKeyType, clientKey.getSecret())!=null);
		
		Assert.assertTrue(clientKeyManagerService.modifyClientKey(clientKey.getKeyString(), clientKey.getSecret(), clientKey));
		Assert.assertTrue(clientKeyManagerService.removeClientKey(clientKey.getKeyString(), clientKeyType, clientKey.getSecret()));
	}
	
}
