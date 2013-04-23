package com.kthcorp.radix.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import junit.framework.Assert;

import org.json.JSONException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import com.kthcorp.radix.util.UuidViewableLoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import com.kthcorp.radix.util.test.junit.MysqlEmbeddableSpringJUnit4ClassRunner;

import com.kthcorp.radix.api.service.ClientPackageDescriptionParser;
import com.kthcorp.radix.api.service.ClientPackageManagerService;
import com.kthcorp.radix.domain.client.CPackages;
import com.kthcorp.radix.domain.exception.DataBaseProcessingException;
import com.kthcorp.radix.domain.exception.ValidateException;
import com.kthcorp.radix.domain.packages.Parameters;
import com.kthcorp.radix.domain.service.YesOrNo;
import com.kthcorp.radix.service.parser.client.ClientPackageDescriptionParserFactory;
import com.kthcorp.radix.util.FailedTest;
import com.kthcorp.radix.util.UUIDUtils;

@RunWith(MysqlEmbeddableSpringJUnit4ClassRunner.class)
@ContextConfiguration(
		locations = { "classpath:/META-INF/spring/spring-application-context-test.xml", "classpath:/META-INF/spring/spring-dao-hazelcast-context.xml", "classpath:/META-INF/spring/spring-service-context-test.xml", "classpath:/META-INF/spring/spring-dao-mysql-context.xml", "classpath:/META-INF/spring/spring-dao-mybatis-context-test.xml" })
public class ClientPackageDescriptionParsingTest {
	
	private final static Logger LOG = UuidViewableLoggerFactory.getLogger(ClientPackageDescriptionParsingTest.class);
	private String description;
	
	private final byte[] clientID = UUIDUtils.getBytes("34d15b7e-ee1d-419f-9fc0-be22120df363");
	
	@Autowired
	private ClientPackageManagerService clientPackageManagerService;
	
	@Before
	public void loadTestDescrption() {
		BufferedReader fileReader = new BufferedReader(new InputStreamReader(Thread.currentThread().getContextClassLoader().getResourceAsStream("clientPackage_v0.3.xml")));
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
		
		ClientPackageDescriptionParser parser = ClientPackageDescriptionParserFactory.newInstance().createParser("application/xml", this.description);
		List<CPackages> packageList = parser.getPackageList(clientID);
		
		Assert.assertTrue(packageList.size() == 2);
		Assert.assertEquals(packageList.get(0).getParametersObj().get("startTimestamp"), "123456789");
		Assert.assertEquals(packageList.get(1).getParametersObj().get("startTimestamp"), "223456789");
		
	}
	
	@FailedTest
	public void createAndModifyAndRemove() throws ValidateException, JSONException, DataBaseProcessingException, NoSuchAlgorithmException {
		LOG.info("Description->" + description);
		try {
			LOG.info("Description(URI)->" + URLEncoder.encode(description, "UTF-8"));
		} catch(UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		ClientPackageDescriptionParser parser = ClientPackageDescriptionParserFactory.newInstance().createParser("application/xml", this.description);
		List<CPackages> packageList = parser.getPackageList(clientID);
		
		byte[] packageID = packageList.get(0).getId();
		
		Assert.assertTrue(packageList.size() == 2);
		Assert.assertEquals(packageList.get(0).getParametersObj().get("startTimestamp"), "123456789");
		Assert.assertEquals(packageList.get(1).getParametersObj().get("startTimestamp"), "223456789");
		
		Assert.assertTrue(clientPackageManagerService.createClientPackage(clientID, packageList));
		
		List<CPackages> packageListGot = clientPackageManagerService.getClientPackageList(clientID);
		Assert.assertTrue(packageListGot.size() == 2);
		Assert.assertEquals(packageListGot.get(0).getParametersObj().get("startTimestamp"), "123456789");
		Assert.assertEquals(packageListGot.get(1).getParametersObj().get("startTimestamp"), "223456789");
		
		Parameters parameters = packageList.get(0).getParametersObj();
		parameters.put("startTimestamp", "323456789");
		
		
		Assert.assertEquals(clientPackageManagerService.modifyClientPackage(clientID, packageID, parameters.toJSONString(), true), true);
		Assert.assertEquals(clientPackageManagerService.modifyClientPackageValid(clientID, packageID, YesOrNo.Y, "testStatus",true), true);
		
		Assert.assertTrue(clientPackageManagerService.removeClientPackage(clientID, packageID, false));
		Assert.assertTrue(clientPackageManagerService.removeClientPackage(clientID, false));
		
		Assert.assertTrue(clientPackageManagerService.removeBackupClientPackage(clientID, packageID));
		Assert.assertTrue(clientPackageManagerService.removeBackupClientPackage(clientID));
	}
	
}
