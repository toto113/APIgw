package com.kthcorp.radix.dao.mybatis;

import java.security.NoSuchAlgorithmException;
import java.util.List;

import junit.framework.Assert;

import org.json.JSONException;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import com.kthcorp.radix.util.test.junit.MysqlEmbeddableSpringJUnit4ClassRunner;

import com.kthcorp.radix.domain.client.CPackages;
import com.kthcorp.radix.domain.packages.Parameters;
import com.kthcorp.radix.domain.service.YesOrNo;
import com.kthcorp.radix.util.FailedTest;
import com.kthcorp.radix.util.UUIDUtils;

@Ignore
@RunWith(MysqlEmbeddableSpringJUnit4ClassRunner.class)
@ContextConfiguration(
		locations = { "classpath:/META-INF/spring/spring-application-context-test.xml", "classpath:/META-INF/spring/spring-dao-mysql-context.xml", "classpath:/META-INF/spring/spring-dao-mybatis-context-test.xml" })
public class MybatisClientPackageManagerDaoTest extends AbstractJUnit4SpringContextTests {
	
	@Autowired
	private MyBatisClientPackageManagerDaoMapper myBatisClientPackageManagerDaoMapper;
	
	private final String clientID = "34d15b7e-ee1d-419f-9fc0-be22120df363";
	private final String packageID = "f63bec14-876e-11e1-a764-f0def154de37";
	
	@Before
	public void createTest() throws NoSuchAlgorithmException {
		
		CPackages packages = new CPackages();
		packages.generateID();
		packages.setClientID(UUIDUtils.getBytes(clientID));
		packages.setPackageID(UUIDUtils.getBytes(packageID));
		
		Parameters parametersObj = new Parameters();
		parametersObj.put("test", "testValue");
		
		try {
			packages.setParametersObj(parametersObj);
			
			Assert.assertTrue(myBatisClientPackageManagerDaoMapper.insertClientPackage(packages) > 0);
		} catch(JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	@FailedTest
	public void selectTest() {
		
		List<CPackages> packageList = myBatisClientPackageManagerDaoMapper.selectPackageListWithClientID(UUIDUtils.getBytes(clientID));
		
		Assert.assertNotNull(packageList);
		Assert.assertEquals((packageList.size() >= 1), true);
		Assert.assertEquals(packageList.get(0).getParametersObj().get("test"), "testValue");
		
		setInvalidTest();
	}
	
	public void setInvalidTest() {
		List<CPackages> packageList = myBatisClientPackageManagerDaoMapper.selectPackageListWithClientID(UUIDUtils.getBytes(clientID));
		
		CPackages packages = packageList.get(0);
		packages.setIsValid(YesOrNo.N);
		packages.setInvalidStatus("test");
		
		Assert.assertEquals((myBatisClientPackageManagerDaoMapper.updateClientPackageValidInfoWithCP(packages) >= 1), true);
		modifyParameters();
	}
	
	public void modifyParameters() {
		List<CPackages> packageList = myBatisClientPackageManagerDaoMapper.selectPackageListWithClientID(UUIDUtils.getBytes(clientID));
		
		CPackages packages = packageList.get(0);
		packages.setIsValid(YesOrNo.N);
		packages.setInvalidStatus("test");
		Parameters parameters = packages.getParametersObj();
		parameters.put("test2", "testValue2");
		parameters.put("test3", "testValue3");
		parameters.remove("test");
		try {
			packages.setParametersObj(parameters);
			
			Assert.assertEquals(myBatisClientPackageManagerDaoMapper.updateClientPackageParameterWithCP(packages), 1);
			selectTest2();
		} catch(JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void selectTest2() {
		
		List<CPackages> packageList = myBatisClientPackageManagerDaoMapper.selectPackageListWithClientID(UUIDUtils.getBytes(clientID));
		
		Assert.assertNotNull(packageList);
		Assert.assertTrue(packageList.size() == 1);
		Assert.assertTrue(packageList.get(0).getParametersObj().size() == 2);
	}
	
	@After
	public void removeTest() {
		Assert.assertTrue(myBatisClientPackageManagerDaoMapper.backupClientPackageWithCP(UUIDUtils.getBytes(clientID), UUIDUtils.getBytes(packageID)) == 1);
		Assert.assertTrue(myBatisClientPackageManagerDaoMapper.deleteBackupClientPackageWithCP(UUIDUtils.getBytes(clientID), UUIDUtils.getBytes(packageID)) == 1);
		Assert.assertTrue(myBatisClientPackageManagerDaoMapper.deleteClientPackageWithCP(UUIDUtils.getBytes(clientID), UUIDUtils.getBytes(packageID)) == 1);
		Assert.assertTrue(myBatisClientPackageManagerDaoMapper.deletePackageWithClientID(UUIDUtils.getBytes(clientID)) == 0);
	}
}
