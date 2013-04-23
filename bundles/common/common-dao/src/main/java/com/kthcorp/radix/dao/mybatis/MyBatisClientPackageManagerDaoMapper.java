package com.kthcorp.radix.dao.mybatis;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.kthcorp.radix.domain.client.CPackages;

public interface MyBatisClientPackageManagerDaoMapper {
	
	
	/** radix_client_package **/
	
	@Select(
			{
				"SELECT",
				"IFNULL(count(1),0) as pcount",
				"FROM",
				"radix_client_package",
				"WHERE",
				"clientID=#{clientID}"
			}
	)
	public int selectExistsCountWithClientID(@Param("clientID") byte[] clientID);
	
	@Select(
			{
				"SELECT",
				"IFNULL(count(1),0) as pcount",
				"FROM",
				"radix_client_package",
				"WHERE",
				"packageID=#{packageID}"
			}
	)
	public int selectExistsCountWithPackageID(@Param("packageID") byte[] packageID);
	
	@Select(
			{
				"SELECT",
				"IFNULL(count(1),0) as pcount",
				"FROM",
				"radix_client_package",
				"WHERE",
				"clientID=#{clientID} and packageID=#{packageID}"
			}
	)
	public int selectExistsCountWithCP(@Param("clientID") byte[] clientID, @Param("packageID") byte[] packageID);
	
	@Select(
			{
				"SELECT",
				"id, clientID, packageID, parameters, regDate, updateDate, isValid, invalidStatus",
				"FROM",
				"radix_client_package",
				"WHERE",
				"clientID=#{clientID}"
			}
	)
	public List<CPackages> selectPackageListWithClientID(@Param("clientID") byte[] clientID);
	
	@Select(
			{
				"SELECT",
				"id, clientID, packageID, parameters, regDate, updateDate, isValid, invalidStatus",
				"FROM",
				"radix_client_package",
				"WHERE",
				"clientID=#{clientID} and packageID=#{packageID} order by regDate desc limit 1"
			}
	)
	public CPackages selectPackageLastOneWithCP(@Param("clientID") byte[] clientID, @Param("packageID") byte[] packageID);

	@Delete(
			{
				"DELETE",
				"FROM",
				"radix_client_package",
				"WHERE",
				"clientID=#{clientID}"
			}
	)
	public int deletePackageWithClientID(@Param("clientID") byte[] clientID);
	
	@Delete(
			{
				"DELETE",
				"FROM",
				"radix_client_package",
				"WHERE",
				"clientID=#{clientID} and packageID=#{packageID}"
			}
	)
	public int deleteClientPackageWithCP(@Param("clientID") byte[] clientID, @Param("packageID") byte[] packageID);
	
	@Insert(
			{
				"INSERT",
				"INTO",
				"radix_client_package",
				"(id, clientID, packageID, parameters, regDate, updateDate)",
				"VALUES",
				"(#{id}, #{clientID}, #{packageID}, #{parameters}, NOW(), NOW())"
			}
	)
	public int insertClientPackage(CPackages packages);
	
	@Insert( 
			{
				"INSERT",
				"INTO",
				"backup.radix_client_package",
				"(id, clientID, packageID, parameters, regDate, updateDate, removeDate, isValid, invalidStatus)",
				"SELECT",
				"id, clientID, packageID, parameters, regDate, updateDate, NOW() as removeDate, isValid, invalidStatus",
				"FROM",
				"radix_client_package",
				"WHERE",
				"id=#{id}"
			}
		)
	public int backupClientPackagesWithID(@Param("id") byte[] id);
	
	@Insert( 
		{
			"INSERT",
			"INTO",
			"backup.radix_client_package",
			"(id, clientID, packageID, parameters, regDate, updateDate, removeDate, isValid, invalidStatus)",
			"SELECT",
			"id, clientID, packageID, parameters, regDate, updateDate, NOW() as removeDate, isValid, invalidStatus",
			"FROM",
			"radix_client_package",
			"WHERE",
			"clientID=#{clientID}"
		}
	)
	public int backupClientPackagesWithClientID(@Param("clientID") byte[] clientID);

	@Insert( 
			{
				"INSERT",
				"INTO",
				"backup.radix_client_package",
				"(id, clientID, packageID, parameters, regDate, updateDate, removeDate, isValid, invalidStatus)",
				"SELECT",
				"id, clientID, packageID, parameters, regDate, updateDate, NOW() as removeDate, isValid, invalidStatus",
				"FROM",
				"radix_client_package",
				"WHERE",
				"clientID=#{clientID} and packageID=#{packageID}"
			}
		)
	public int backupClientPackageWithCP(@Param("clientID") byte[] clientID, @Param("packageID") byte[] packageID);

	@Delete(
			{
				"DELETE",
				"FROM",
				"backup.radix_client_package",
				"WHERE",
				"clientID=#{clientID} and packageID=#{packageID}"
			}
	)
	public int deleteBackupClientPackageWithCP(@Param("clientID") byte[] clientID, @Param("packageID") byte[] packageID);
	
	@Delete(
			{
				"DELETE",
				"FROM",
				"backup.radix_client_package",
				"WHERE",
				"clientID=#{clientID}"
			}
	)
	public int deleteBackupClientPackagesWithClientID(@Param("clientID") byte[] clientID);
	
	@Delete(
			{
				"DELETE",
				"FROM",
				"backup.radix_client_package",
				"WHERE",
				"id=#{id}"
			}
	)
	public int deleteBackupClientPackagesWithID(@Param("id") byte[] id);
	
	@Update(
			{
				"UPDATE",
				"radix_client_package",
				"SET",
				"parameters=#{parameters}, updateDate=NOW()",
				"WHERE",
				"clientID=#{clientID} and packageID=#{packageID}"
			}
	)
	public int updateClientPackageParameterWithCP(CPackages packages);
	
	@Update(
			{
				"UPDATE",
				"radix_client_package",
				"SET",
				"parameters=#{parameters}, updateDate=NOW()",
				"WHERE",
				"id=#{id}"
			}
	)
	public int updateClientPackageParameterWithID(CPackages packages);
	
	@Update(
			{
				"UPDATE",
				"radix_client_package",
				"SET",
				"isValid=#{isValid}, invalidStatus=#{invalidStatus}, updateDate=NOW()",
				"WHERE",
				"id=#{id}"
			}
	)
	public int updateClientPackageValidInfoWithID(CPackages packages);
	
	@Update(
			{
				"UPDATE",
				"radix_client_package",
				"SET",
				"isValid=#{isValid}, invalidStatus=#{invalidStatus}, updateDate=NOW()",
				"WHERE",
				"clientID=#{clientID} and packageID=#{packageID}"
			}
	)
	public int updateClientPackageValidInfoWithCP(CPackages packages);
	
}
