package com.kthcorp.radix.dao.mybatis;

import java.util.Date;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ContextConfiguration;
import com.kthcorp.radix.util.test.junit.MysqlEmbeddableSpringJUnit4ClassRunner;

import com.kthcorp.radix.domain.packages.Packages;
import com.kthcorp.radix.util.UUIDUtils;

@RunWith(MysqlEmbeddableSpringJUnit4ClassRunner.class)
@ContextConfiguration(
		locations = { "classpath:/META-INF/spring/spring-application-context-test.xml", "classpath:/META-INF/spring/spring-dao-mysql-context.xml", "classpath:/META-INF/spring/spring-dao-mybatis-context-test.xml" })
public class MybatisPackageManagerDaoFailureTest {

	@Autowired
	private MyBatisBusinessPlatformKeyManagerDaoMapper myBatisBusinessPlatformKeyManagerDao;
	
	@Autowired
	private MyBatisPackageManagerDaoMapper myBatisPackageManagerDao;
	
	private final String businessPlatformKey = "59ffa6f4-0901-4ffc-82ad-44687540ab4b";
	
	@Test
	public void doAddPackageFailureByBusinessPlatformIDNull() {
		
		byte[] bpInBytes = UUIDUtils.getBytes(businessPlatformKey);
		Assert.assertEquals(myBatisBusinessPlatformKeyManagerDao.selectExistsCountWithKeyString(businessPlatformKey),1);
		String idPostfix = Long.toString((new Date()).getTime());
		String name = "test_name_" + idPostfix ;
		String partnerID = "test_partnerid_" + idPostfix ;

		
		Packages pkg = new Packages();
		pkg.setBusinessPlatformID(bpInBytes);
		pkg.setName(name);
		pkg.setPartnerID(partnerID);
		
		try {
			myBatisPackageManagerDao.insertPackage(pkg);
		} catch(DataIntegrityViolationException e) {
			Assert.assertTrue(e.toString(), true);
		}
	}
	
	@Test
	public void doAddPackageFailureByPartnerIDNull() {
		
		byte[] bpInBytes = UUIDUtils.getBytes(businessPlatformKey);
		Assert.assertEquals(myBatisBusinessPlatformKeyManagerDao.selectExistsCountWithKeyString(businessPlatformKey),1);
		String idPostfix = Long.toString((new Date()).getTime());
		String name = "test_name_" + idPostfix;
		String partnerID = "test_partnerid_" + idPostfix;
		
		// inject error-cases
		//
		partnerID = null;
		
		Packages pkg = new Packages();
		pkg.setBusinessPlatformID(bpInBytes);
		pkg.setName(name);
		pkg.setPartnerID(partnerID);
		
		try {
			myBatisPackageManagerDao.insertPackage(pkg);
		} catch(DataIntegrityViolationException e) {
			Assert.assertTrue(e.toString(), true);
		}
	}

	@Test
	public void doAddPackageFailureByNameNull() {
		
		byte[] bpInBytes = UUIDUtils.getBytes(businessPlatformKey);
		Assert.assertEquals(myBatisBusinessPlatformKeyManagerDao.selectExistsCountWithKeyString(businessPlatformKey),1);
		String idPostfix = Long.toString((new Date()).getTime());
		String name = "test_name_" + idPostfix;
		String partnerID = "test_partnerid_" + idPostfix;
		
		// inject error-cases
		//
		name = null;
		
		Packages pkg = new Packages();
		pkg.setBusinessPlatformID(bpInBytes);
		pkg.setName(name);
		pkg.setPartnerID(partnerID);
		
		try {
			myBatisPackageManagerDao.insertPackage(pkg);
		} catch(DataIntegrityViolationException e) {
			Assert.assertTrue(e.toString(), true);
		}
	}
	
	@Test
	public void doAddPackageFailureByNameSizeOverflow () {
		
		byte[] bpInBytes = UUIDUtils.getBytes(businessPlatformKey);
		Assert.assertEquals(myBatisBusinessPlatformKeyManagerDao.selectExistsCountWithKeyString(businessPlatformKey),1);

		String idPostfix = Long.toString((new Date()).getTime());
		String name = "test_name_" + idPostfix;
		String partnerID = "test_partnerid_" + idPostfix;
		
		// inject error-cases
		//
		name = "XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX" + "XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX" + "XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX" + "XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX" + "XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX" + "XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX" + "XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX";
		
		Packages pkg = new Packages();
		pkg.setBusinessPlatformID(bpInBytes);
		pkg.setName(name);
		pkg.setPartnerID(partnerID);
		
		try {
			myBatisPackageManagerDao.insertPackage(pkg);
		} catch(DataIntegrityViolationException e) {
			Assert.assertTrue(e.toString(), true);
		}
	}
}
