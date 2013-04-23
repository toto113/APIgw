package com.kthcorp.radix.dao.mybatis;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.UpdateProvider;

import com.kthcorp.radix.domain.packages.Packages;
import com.kthcorp.radix.domain.service.api.serviceAPI.ServiceAPI;

public interface MyBatisPackageManagerDaoMapper {
	
	/*
	 +---------------------------------+
	 | Tables_in_radix about package   |
	 +---------------------------------+
	 | radix_package                   |
	 | radix_package_serviceapi        |
	 +---------------------------------+
	 */
	
	
	/* radix_package */
	
	/* ADD */
	@Insert(
			{ 
				"INSERT INTO",
				"radix_package",
				"(id, businessPlatformID, partnerID, name, regDate, updateDate)",
				"VALUES",
				"(#{id}, #{businessPlatformID}, #{partnerID}, #{name}, NOW(), NOW())"
			}
	)
	public int insertPackage(Packages pkg);
	
	/* CHECK */
	@Select(
			{				
				"SELECT",
				"IFNULL(count(1),0) as pcount",
				"FROM",
				"radix_package",
				"WHERE",
				"id=#{packageID} and isValid='Y'"
			}
	)
	public int selectExistsPackageCount(@Param("packageID") byte[] packageID);
	
	/* CHECK */
	@Select(
			{				
				"SELECT",
				"IFNULL(count(1),0) as pcount",
				"FROM",
				"radix_package",
				"WHERE",
				"id=#{packageID} and isValid='Y' and partnerID=#{partnerID}"
			}
	)
	public int selectExistsPackageCountWithPartnerID(@Param("packageID") byte[] packageID, @Param("partnerID") String partnerID);
	
	@Select(
			{
				"SELECT",
				"IFNULL(count(1),0) as pcount",
				"FROM",
				"radix_package",
				"WHERE",
				"id=#{packageID}"
			}
	)
	public int selectExistsPackageWithHiddenCount(@Param("packageID") byte[] packageID);
	
	/* GET */
	@Select(
			{ 
				"SELECT", 
				"id, businessPlatformID, partnerID, name, regDate, updateDate", 
				"FROM", 
				"radix_package", 
				"WHERE",
				"businessPlatformID=#{businessPlatformID} and partnerID=#{partnerID} and isValid='Y'" 
			}
	)
	public List<Packages> selectPartnerPackageList(@Param("businessPlatformID") byte[] businessPlatformID, @Param("partnerID") String partnerID);
	
	@Select(
			{ 
				"SELECT", 
				"id, businessPlatformID, partnerID, name, regDate, updateDate", 
				"FROM", 
				"radix_package", 
				"WHERE",
				"businessPlatformID=#{businessPlatformID} and isValid='Y'" 
			}
	)
	public List<Packages> selectAllBusinessPlatformPackageList(@Param("businessPlatformID") byte[] businessPlatformID);
	
	@Select(
			{ 
				"SELECT", 
				"id, businessPlatformID, partnerID, name, regDate, updateDate", 
				"FROM", 
				"radix_package",
				"WHERE",
				"isValid='Y'"
			}
	)
	public List<Packages> selectAllPackageList();
	
	@Select(
			{ 
				"SELECT", 
				"id, businessPlatformID, partnerID, name, regDate, updateDate", 
				"FROM", 
				"radix_package",
				"WHERE",
				"id=#{packageID} and isValid='Y'"
			}
	)
	public Packages selectPackage(@Param("packageID") byte[] packageID);
	
	@Select(
			{ 
				"SELECT", 
				"id, businessPlatformID, partnerID, name, regDate, updateDate", 
				"FROM", 
				"radix_package",
				"WHERE",
				"id=#{packageID} and isValid='Y' and partnerID=#{partnerID}"
			}
	)
	public Packages selectPackageWithPartnerID(@Param("partnerID") String partnerID, @Param("packageID") byte[] packageID);	
	
	/* GET (include hidden Object) */
	@Select(
			{ 
				"SELECT", 
				"id, businessPlatformID, partnerID, name, regDate, updateDate, isValid, invalidStatus", 
				"FROM", 
				"radix_package", 
				"WHERE",
				"businessPlatformID=#{businessPlatformID} and partnerID=#{partnerID}" 
			}
	)
	public List<Packages> selectPartnerPackageListWithHidden(@Param("businessPlatformID") byte[] businessPlatformID, @Param("partnerID") String partnerID);
	
	@Select(
			{ 
				"SELECT", 
				"id, businessPlatformID, partnerID, name, regDate, updateDate, isValid, invalidStatus", 
				"FROM", 
				"radix_package", 
				"WHERE",
				"businessPlatformID=#{businessPlatformID}"
			}
	)
	public List<Packages> selectAllBusinessPlatformPackageListWithHidden(@Param("businessPlatformID") byte[] businessPlatformID);
	
	@Select(
			{ 
				"SELECT", 
				"id, businessPlatformID, partnerID, name, regDate, updateDate, isValid, invalidStatus", 
				"FROM", 
				"radix_package"
			}
	)
	public List<Packages> selectAllPackageListWithHidden();
	
	@Select(
			{ 
				"SELECT", 
				"id, businessPlatformID, partnerID, name, regDate, updateDate, isValid, invalidStatus",  
				"FROM", 
				"radix_package",
				"WHERE",
				"id=#{packageID}"
			}
	)
	public Package selectPackageWithHidden(@Param("packageID") byte[] packageID);

	/* MODIFY */
	@UpdateProvider(method="updatePackageInDynamicSQL", type = MyBatisPackageManagerDaoDynamicSQL.class)
	public int updatePackage(Packages packages);
	
	/* REMOVE */

	@Insert( 
			{
				"INSERT",
				"INTO",
				"backup.radix_package",
				"(id, businessPlatformID, partnerID, name, regDate, updateDate, isValid, invalidStatus, removeDate)",
				"SELECT",
				"id, businessPlatformID, partnerID, name, regDate, updateDate, isValid, invalidStatus, NOW() as removeDate",
				"FROM",
				"radix_package",
				"WHERE",
				"businessPlatformID=#{businessPlatformID} and partnerID=#{partnerID} and id=#{packageID}"
			}
		)
	public int backupPackage(@Param("businessPlatformID") byte[] businessPlatformID, @Param("partnerID") String partnerID, @Param("packageID") byte[] packageID);
	
	@Delete(
			{
				"DELETE",
				"FROM",
				"radix_package",
				"WHERE",
				"businessPlatformID=#{businessPlatformID} and partnerID=#{partnerID} and id=#{packageID}"
			}
	)
	public int deletePackage(@Param("businessPlatformID") byte[] businessPlatformID, @Param("partnerID") String partnerID, @Param("packageID") byte[] packageID);

	@Delete(
			{
				"DELETE",
				"FROM",
				"backup.radix_package",
				"WHERE",
				"businessPlatformID=#{businessPlatformID} and partnerID=#{partnerID} and id=#{packageID}"
			}
	)
	public int deleteBackupPackage(@Param("businessPlatformID") byte[] businessPlatformID, @Param("partnerID") String partnerID, @Param("packageID") byte[] packageID);
	
	
	/* radix_package_serviceapi */
	
	/* ADD */
	@Insert(
			{ 
				"INSERT INTO", 
				"radix_package_serviceapi", 
				"(packageID, serviceAPIID, regDate, updateDate)", 
				"VALUES", 
				"(#{packageID},#{serviceAPIID},NOW(),NOW())" 
			}
	)
	public int insertPackageServiceAPI(@Param("packageID") byte[] packageID, @Param("serviceAPIID") byte[] serviceAPIID);
	
	/* CHECK */
	@Select(
			{				
				"SELECT",
				"IFNULL(count(1),0) as scount",
				"FROM",
				"radix_package_serviceapi",
				"WHERE",
				"packageID=#{packageID} and serviceAPIID=#{serviceAPIID} and isValid='Y'"
			}
	)
	public int selectExistsPackageServiceAPICount(@Param("packageID") byte[] packageID, @Param("serviceAPIID") byte[] serviceAPIID);
	
	@Select(
			{				
				"SELECT",
				"IFNULL(count(1),0) as scount",
				"FROM",
				"radix_package_serviceapi",
				"WHERE",
				"packageID=#{packageID} and serviceAPIID=#{serviceAPIID}"
			}
	)
	public int selectExistsPackageServiceAPIWithHiddenCount(@Param("packageID") byte[] packageID, @Param("serviceAPIID") byte[] serviceAPIID);

	/* GET */
	@Select(
			{
				"SELECT",
				"serviceAPIID as id",
				"FROM",
				"radix_package_serviceapi",
				"WHERE",
				"packageID=#{packageID}"
			}
	)
	public List<ServiceAPI> selectServiceAPIList(@Param("packageID") byte[] packageID);
	
	@Select(
			{
				"SELECT",
				"a.serviceAPIID as id, b.apiKey",
				"FROM",
				"radix_package_serviceapi a INNER JOIN radix_service_apis_serviceapi b ON a.serviceAPIID = b.id",
				"WHERE",
				"packageID=#{packageID}"
			}
	)
	public List<ServiceAPI> selectServiceAPIDetailList(@Param("packageID") byte[] packageID);
	
	@Select(
			{
				"SELECT",
				"serviceAPIID as id",
				"FROM",
				"radix_package_serviceapi",
				"WHERE",
				"packageID=#{packageID}"
			}
	)
	public List<ServiceAPI> selectServiceAPIListWithHidden(@Param("packageID") byte[] packageID);	
	
	/* REMOVE */
	@Insert( 
			{
				"INSERT",
				"INTO",
				"backup.radix_package_serviceapi",
				"(packageID, serviceAPIID, regDate, updateDate, isValid, invalidStatus, removeDate)",
				"SELECT",
				"packageID, serviceAPIID, regDate, updateDate, isValid, invalidStatus, NOW() as removeDate",
				"FROM",
				"radix_package_serviceapi",
				"WHERE",
				"businessPlatformID=#{businessPlatformID} and partnerID=#{partnerID}" 
			}
		)
	public int backupAllPartnerServiceAPI(@Param("businessPlatformID") byte[] businessPlatformID, @Param("partnerID") String partnerID);
	
	@Delete(
			{
				"DELETE",
				"FROM",
				"radix_package_serviceapi",
				"WHERE",
				"businessPlatformID=#{businessPlatformID} and partnerID=#{partnerID}" 
			}
	)
	public int deleteAllPartnerServiceAPI(@Param("businessPlatformID") byte[] businessPlatformID, @Param("partnerID") String partnerID);
	
	
	@Insert( 
			{
				"INSERT",
				"INTO",
				"backup.radix_package_serviceapi",
				"(packageID, serviceAPIID, regDate, updateDate, isValid, invalidStatus, removeDate)",
				"SELECT",
				"packageID, serviceAPIID, regDate, updateDate, isValid, invalidStatus, NOW() as removeDate",
				"FROM",
				"radix_package_serviceapi",
				"WHERE",
				"packageID=#{packageID}"
			}
		)
	public int backupAllPackageServiceAPI(@Param("packageID") byte[] packageID);
	
	@Delete(
			{
				"DELETE",
				"FROM",
				"radix_package_serviceapi",
				"WHERE",
				"packageID=#{packageID}"
			}
	)
	public int deleteAllPackageServiceAPI(@Param("packageID") byte[] packageID);

	
	@Insert( 
			{
				"INSERT",
				"INTO",
				"backup.radix_package_serviceapi",
				"(packageID, serviceAPIID, regDate, updateDate, isValid, invalidStatus, removeDate)",
				"SELECT",
				"packageID, serviceAPIID, regDate, updateDate, isValid, invalidStatus, NOW() as removeDate",
				"FROM",
				"radix_package_serviceapi",
				"WHERE",
				"packageID=#{packageID} and serviceAPIID=#{serviceAPIID}" 
			}
		)
	public int backupPackageServiceAPI(@Param("packageID") byte[] packageID, @Param("serviceAPIID") byte[] serviceAPIID);	
	
	@Delete(
			{
				"DELETE",
				"FROM",
				"radix_package_serviceapi",
				"WHERE",
				"packageID=#{packageID} and serviceAPIID=#{serviceAPIID}" 
			}
	)
	public int deletePackageServiceAPI(@Param("packageID") byte[] packageID, @Param("serviceAPIID") byte[] serviceAPIID);	
	
	@Delete(
			{ 
				"DELETE", 
				"FROM", 
				"backup.radix_package_serviceapi", 
				"WHERE",
				"businessPlatformID=#{businessPlatformID} and partnerID=#{partnerID}" 
			}
	)
	public int deleteBackupAllPartnerServiceAPI(@Param("businessPlatformID") byte[] businessPlatformID, @Param("partnerID") String partnerID);
	
	@Delete(
			{ 
				"DELETE", 
				"FROM", 
				"backup.radix_package_serviceapi", 
				"WHERE",
				"packageID=#{packageID}" })
	public int deleteBackupAllPackageServiceAPI(@Param("packageID") byte[] packageID);

	@Delete(
			{ 
				"DELETE", 
				"FROM", 
				"backup.radix_package_serviceapi", 
				"WHERE", 
				"packageID=#{packageID} and serviceAPIID=#{serviceAPIID}" 
			}
	)
	public int deleteBackupPackageServiceAPI(@Param("packageID") byte[] packageID, @Param("serviceAPIID") byte[] serviceAPIID);

}
