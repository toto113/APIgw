package com.kthcorp.radix.dao.mybatis;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.apache.ibatis.annotations.UpdateProvider;

import com.kthcorp.radix.domain.policy.Policy;
import com.kthcorp.radix.domain.policy.PolicyType;

public interface MyBatisPolicyManagerDaoMapper {
	
	/*
	 +---------------------------------+
	 | Tables_in_radix about package   |
	 +---------------------------------+
	 | radix_policy                    |
	 | radix_package_policy            |
	 | radix_package_serviceapi_policy |
	 +---------------------------------+
	 */
	
	
	/* radix_policy_type */
	
	/* ADD */
	@Insert(
			{ 
				"INSERT INTO",
				"radix_policy_type",
				"(id, name, description, priority, properties, isActivated, regDate, updateDate)",
				"VALUES",
				"(#{id}, #{name}, #{description}, #{priority}, #{properties}, #{isActivated}, NOW(), NOW())"
			}
	)
	public int insertPolicyType(PolicyType policyType);
	
	/* CHECK */
	@Select(
			{
				"SELECT",
				"IFNULL(count(1),0) as pcount",
				"FROM",
				"radix_policy_type",
				"WHERE",
				"id=#{policyTypeID} and isValid='Y'"
			}
	)
	public int selectExistsPolicyTypeCount(@Param("policyTypeID") String policyTypeID);
	
	@Select(
			{
				"SELECT",
				"IFNULL(count(1),0) as pcount",
				"FROM",
				"radix_policy_type",
				"WHERE",
				"id=#{policyTypeID}"
			}
	)
	public int selectExistsPolicyTypeWithHiddenCount(@Param("policyTypeID") String policyTypeID);
	
	/* GET */
	@Select(
			{ 
				"SELECT", 
				"id, name, description, priority, properties, isActivated, regDate, updateDate",
				"FROM", 
				"radix_policy",
				"WHERE",
				"isValid='Y'"
			}
	)
	public List<PolicyType> selectAllPolicyTypeList();
	
	@Select(
			{ 
				"SELECT", 
				"id, name, description, priority, properties, isActivated, regDate, updateDate",
				"FROM", 
				"radix_policy_type",
				"WHERE",
				"id=#{policyTypeID} and isValid='Y'"
			}
	)
	public PolicyType selectPolicyType(@Param("policyTypeID") String policyTypeID);
	
	/* GET (include hidden Object) */
	@Select(
			{ 
				"SELECT", 
				"id, name, description, priority, properties, isActivated, regDate, updateDate, isValid, invalidStatus",  
				"FROM", 
				"radix_policy_type"
			}
	)
	public List<PolicyType> selectAllPolicyTypeListWithHidden();
	
	@Select(
			{ 
				"SELECT", 
				"id, name, description, priority, isActivated, regDate, updateDate, isValid, invalidStatus",  
				"FROM", 
				"radix_policy_type",
				"WHERE",
				"id=#{policyTypeID}"
			}
	)
	public PolicyType selectPolicyTypeWithHidden(@Param("policyTypeID") String policyTypeID);

	/* MODIFY */
	@Update(
			{
				"UPDATE",
				"radix_policy_type",
				"SET",
				"name=#{name}, description=#{description}, updateDate=NOW(), priority=#{priority}, properties=#{properties}, isActivated=#{isActivated}, isValid=#{isValid}, invalidStatus=#{invalidStatus}",
				"WHERE",
				"id=#{id}"
			}
	)
	public int modifyPolicyType(PolicyType policyType);
	
	/* REMOVE */
	@Insert( 
			{
				"INSERT",
				"INTO",
				"backup.radix_policy_type",
				"(id, name, description, priority, properties, isActivated, regDate, updateDate, isValid, invalidStatus, removeDate)",
				"SELECT",
				"id, name, description, priority, properties, isActivated, regDate, updateDate, isValid, invalidStatus, NOW() as removeDate",
				"FROM",
				"radix_policy_type",
				"WHERE",
				"id=#{policyTypeID}"
			}
		)
	public int backupPolicyType(@Param("policyTypeID") String policyTypeID);
	
	@Delete(
			{
				"DELETE",
				"FROM",
				"radix_policy_type",
				"WHERE",
				"id=#{policyTypeID}"
			}
	)
	public int deletePolicyType(@Param("policyTypeID") String policyTypeID);

	@Delete(
			{
				"DELETE",
				"FROM",
				"backup.radix_policy_type",
				"WHERE",
				"id=#{policyID}"
			}
	)
	public int deleteBackupPolicyType(@Param("policyTypeID") String policyTypeID);
	
	
	
	
	/* radix_package_policy */
	
	/* ADD */
	@Insert(
			{ 
				"INSERT INTO", 
				"radix_package_policy", 
				"(id, businessPlatformID, partnerID, packageID, policyTypeID, name, properties, regDate, updateDate)", 
				"VALUES", 
				"(#{id}, #{businessPlatformID}, #{partnerID}, #{packageID}, #{policyTypeID}, #{name}, #{properties}, NOW(), NOW())" 
			}
	)
	public int insertPackagePolicy(Policy policy);
	
	/* CHECK */
	@Select(
			{
				"SELECT",
				"IFNULL(count(1),0) as pcount",
				"FROM",
				"radix_package_policy",
				"WHERE",
				"packageID=#{packageID} and policyTypeID=#{policyTypeID} and isValid='Y'"
			}
	)
	public Integer selectExistsPackagePolicyWithTypeCount(@Param("packageID") byte[] packageID, @Param("policyTypeID") String policyTypeID);
	
	@Select(
			{
				"SELECT",
				"IFNULL(count(1),0) as pcount",
				"FROM",
				"radix_package_policy",
				"WHERE",
				"packageID=#{packageID} and id=#{policyID} and isValid='Y'"
			}
	)
	public Integer selectExistsPackagePolicyCount(@Param("packageID") byte[] packageID, @Param("policyID") byte[] policyID);
	
	/* CHECK */
	@Select(
			{
				"SELECT",
				"IFNULL(count(1),0) as pcount",
				"FROM",
				"radix_package_policy",
				"WHERE",
				"packageID=#{packageID} and policyTypeID=#{policyTypeID}"
			}
	)
	public Integer selectExistsPackagePolicyWithTypeHiddenCount(@Param("packageID") byte[] packageID, @Param("policyTypeID") String policyTypeID);
	
	@Select(
			{
				"SELECT",
				"IFNULL(count(1),0) as pcount",
				"FROM",
				"radix_package_policy",
				"WHERE",
				"packageID=#{packageID} and id=#{policyID}"
			}
	)
	public Integer selectExistsPackagePolicyWithHiddenCount(@Param("packageID") byte[] packageID, @Param("policyID") byte[] policyID);
	
	/* GET */
	@Select(
			{
				"SELECT",
				"id, businessPlatformID, partnerID, packageID, policyTypeID, name, properties, regDate, updateDate",
				"FROM",
				"radix_package_policy",
				"WHERE",
				"packageID=#{packageID}"
			}
	)
	public List<Policy> selectPackagePolicy(@Param("packageID") byte[] packageID);
	
	@Select(
			{
				"SELECT",
				"id, businessPlatformID, partnerID, packageID, policyTypeID, name, properties, regDate, updateDate, isValid, invalidStatus",
				"FROM",
				"radix_package_policy",
				"WHERE",
				"packageID=#{packageID}"
			}
	)
	public List<Policy> selectPackagePolicyWithHidden(@Param("packageID") byte[] packageID);
	
	@Select(
			{
				"SELECT",
				"id, businessPlatformID, partnerID, packageID, policyTypeID, name, properties, regDate, updateDate",
				"FROM",
				"radix_package_policy",
				"WHERE",
				"packageID=#{packageID} and policyTypeID=#{policyTypeID}"
			}
	)
	public List<Policy> selectPackageWithTypePolicy(@Param("packageID") byte[] packageID, @Param("policyTypeID") String policyTypeID);
	
	@Select(
			{
				"SELECT",
				"id, businessPlatformID, partnerID, packageID, policyTypeID, name, properties, regDate, updateDate, isValid, invalidStatus",
				"FROM",
				"radix_package_policy",
				"WHERE",
				"packageID=#{packageID} and policyTypeID=#{policyTypeID}"
			}
	)
	public List<Policy> selectPackagePolicyWithTypeHidden(@Param("packageID") byte[] packageID, @Param("policyTypeID") String policyTypeID);
	
	/* Modify */
	@UpdateProvider(method="updatePackagePolicyInDynamicSQL", type = MyBatisPolicyManagerDaoDynamicSQL.class)
	public int modifyPackagePolicy(Policy policy);
	
	/* REMOVE */
	@Insert( 
			{
				"INSERT",
				"INTO",
				"backup.radix_package_policy",
				"(id, businessPlatformID, partnerID, packageID, policyTypeID, name, properties, regDate, updateDate, isValid, invalidStatus, removeDate)",
				"SELECT",
				"id, businessPlatformID, partnerID, packageID, policyTypeID, name, properties, regDate, updateDate, isValid, invalidStatus, NOW() as removeDate",
				"FROM",
				"radix_package_policy",
				"WHERE",
				"packageID=#{packageID}"
			}
		)
	public int backupAllPackagePolicy(@Param("packageID") byte[] packageID);
	
	@Delete(
			{
				"DELETE",
				"FROM",
				"radix_package_policy",
				"WHERE",
				"packageID=#{packageID}"
			}
	)
	public int deleteAllPackagePolicy(@Param("packageID") byte[] packageID);

	@Insert( 
			{
				"INSERT",
				"INTO",
				"backup.radix_package_policy",
				"(id, businessPlatformID, partnerID, packageID, policyTypeID, name, properties, regDate, updateDate, isValid, invalidStatus, removeDate)",
				"SELECT",
				"id, businessPlatformID, partnerID, packageID, policyTypeID, name, properties, regDate, updateDate, isValid, invalidStatus, NOW() as removeDate",
				"FROM",
				"radix_package_policy",
				"WHERE",
				"packageID=#{packageID} and id=#{policyID}" 
			}
		)
	public int backupPackagePolicy(@Param("packageID") byte[] packageID, @Param("policyID") byte[] policyID);	
	
	@Delete(
			{
				"DELETE",
				"FROM",
				"radix_package_policy",
				"WHERE",
				"packageID=#{packageID} and id=#{policyID}" 
			}
	)
	public int deletePackagePolicy(@Param("packageID") byte[] packageID, @Param("policyID") byte[] policyID);

	@Delete(
			{ 
				"DELETE", 
				"FROM", 
				"backup.radix_package_policy", 
				"WHERE",
				"packageID=#{packageID}" })
	public int deleteBackupAllPackagePolicy(@Param("packageID") byte[] packageID);

	@Delete(
			{ 
				"DELETE", 
				"FROM", 
				"backup.radix_package_policy", 
				"WHERE", 
				"packageID=#{packageID} and id=#{policyID}" 
			}
	)
	public int deleteBackupPackagePolicy(@Param("packageID") byte[] packageID, @Param("policyID") byte[] policyID);
	

	/* radix_package_serviceapi_policy */
	
	/* ADD */
	@Insert(
			{ 
				"INSERT INTO", 
				"radix_package_serviceapi_policy", 
				"(id, businessPlatformID, partnerID, packageID, serviceAPIID, policyTypeID, name, properties, regDate, updateDate)", 
				"VALUES", 
				"(#{id}, #{businessPlatformID}, #{partnerID}, #{packageID}, #{serviceAPIID}, #{policyTypeID}, #{name}, #{properties}, NOW(), NOW())" 
			}
	)
	public int insertPackageServiceAPIPolicy(Policy policy);
	
	/* CHECK */
	@Select(
			{
				"SELECT",
				"IFNULL(count(1),0) as pcount",
				"FROM",
				"radix_package_serviceapi_policy",
				"WHERE",
				"packageID=#{packageID} and serviceAPIID=#{serviceAPIID} and id=#{policyID} and isValid='Y'"
			}
	)
	public Integer selectExistsPackageServiceAPIPolicyCount(@Param("packageID") byte[] packageID, @Param("serviceAPIID") byte[] serviceAPIID, @Param("policyID") byte[] policyID);
	
	@Select(
			{
				"SELECT",
				"IFNULL(count(1),0) as pcount",
				"FROM",
				"radix_package_serviceapi_policy",
				"WHERE",
				"packageID=#{packageID} and serviceAPIID=#{serviceAPIID} and id=#{policyID}"
			}
	)
	public Integer selectExistsPackageServiceAPIPolicyWithHiddenCount(@Param("packageID") byte[] packageID, @Param("serviceAPIID") byte[] serviceAPIID, @Param("policyID") byte[] policyID);
	
	@Select(
			{
				"SELECT",
				"IFNULL(count(1),0) as pcount",
				"FROM",
				"radix_package_serviceapi_policy",
				"WHERE",
				"packageID=#{packageID} and serviceAPIID=#{serviceAPIID} and policyTypeID=#{policyTypeID} and isValid='Y'"
			}
	)
	public Integer selectExistsPackageServiceAPIPolicyWithTypeCount(@Param("packageID") byte[] packageID, @Param("serviceAPIID") byte[] serviceAPIID, @Param("policyTypeID") String policyTypeID);
	
	@Select(
			{
				"SELECT",
				"IFNULL(count(1),0) as pcount",
				"FROM",
				"radix_package_serviceapi_policy",
				"WHERE",
				"packageID=#{packageID} and serviceAPIID=#{serviceAPIID} and policyTypeID=#{policyTypeID}"
			}
	)
	public Integer selectExistsPackageServiceAPIPolicyWithTypeHiddenCount(@Param("packageID") byte[] packageID, @Param("serviceAPIID") byte[] serviceAPIID, @Param("policyTypeID") String policyTypeID);
	
	
	/* GET */
	@Select(
			{
				"SELECT",
				"id, businessPlatformID, partnerID, packageID, serviceAPIID, policyTypeID, name, properties, regDate, updateDate",
				"FROM",
				"radix_package_serviceapi_policy",
				"WHERE",
				"packageID=#{packageID} and serviceAPIID=#{serviceAPIID}"
			}
	)
	public List<Policy> selectPackageServiceAPIPolicyList(@Param("packageID") byte[] packageID, @Param("serviceAPIID") byte[] serviceAPIID);
	
	@Select(
			{
				"SELECT",
				"id, businessPlatformID, partnerID, packageID, serviceAPIID, policyTypeID, name, properties, regDate, updateDate, isValid, invalidStatus",
				"FROM",
				"radix_package_serviceapi_policy",
				"WHERE",
				"packageID=#{packageID} and serviceAPIID=#{serviceAPIID}"
			}
	)
	public List<Policy> selectPackageServiceAPIPolicyListWithHidden(@Param("packageID") byte[] packageID, @Param("serviceAPIID") byte[] serviceAPIID);
	
	/* Modify */
	@UpdateProvider(method = "updatePackageServiceAPIPolicyListInDynamicSQL",type=MyBatisPolicyManagerDaoDynamicSQL.class)
	public int modifyPackageServiceAPIPolicy(Policy policy);
	
	/* REMOVE */
	@Insert( 
			{
				"INSERT",
				"INTO",
				"backup.radix_package_serviceapi_policy",
				"(id, businessPlatformID, partnerID, packageID, serviceAPIID, policyTypeID, name, properties, regDate, updateDate, isValid, invalidStatus, removeDate)",
				"SELECT",
				"id, businessPlatformID, partnerID, packageID, serviceAPIID, policyTypeID, name, properties, regDate, updateDate, isValid, invalidStatus, NOW() as removeDate",
				"FROM",
				"radix_package_serviceapi_policy",
				"WHERE",
				"packageID=#{packageID}"
			}
		)
	public int backupAllPackageServiceAPIPolicy(@Param("packageID") byte[] packageID);
	
	@Delete(
			{
				"DELETE",
				"FROM",
				"radix_package_serviceapi_policy",
				"WHERE",
				"packageID=#{packageID}"
			}
	)
	public int deleteAllPackageServiceAPIPolicy(@Param("packageID") byte[] packageID);

	@Insert( 
			{
				"INSERT",
				"INTO",
				"backup.radix_package_serviceapi_policy",
				"(id, businessPlatformID, partnerID, packageID, serviceAPIID, policyTypeID, name, properties, regDate, updateDate, isValid, invalidStatus, removeDate)",
				"SELECT",
				"id, businessPlatformID, partnerID, packageID, serviceAPIID, policyTypeID, name, properties, regDate, updateDate, isValid, invalidStatus, NOW() as removeDate",
				"FROM",
				"radix_package_serviceapi_policy",
				"WHERE",
				"packageID=#{packageID} and serviceAPIID=#{serviceAPIID}" 
			}
		)
	public int backupPackageServiceAPIPolicys(@Param("packageID") byte[] packageID, @Param("serviceAPIID") byte[] serviceAPIID);
	
	@Delete(
			{
				"DELETE",
				"FROM",
				"radix_package_serviceapi_policy",
				"WHERE",
				"packageID=#{packageID} and serviceAPIID=#{serviceAPIID}" 
			}
	)
	public int deletePackageServiceAPIPolicys(@Param("packageID") byte[] packageID, @Param("serviceAPIID") byte[] serviceAPIID);

	@Insert( 
			{
				"INSERT",
				"INTO",
				"backup.radix_package_serviceapi_policy",
				"(id, businessPlatformID, partnerID, packageID, serviceAPIID, policyTypeID, name, properties, regDate, updateDate, isValid, invalidStatus, removeDate)",
				"SELECT",
				"id, businessPlatformID, partnerID, packageID, serviceAPIID, policyTypeID, name, properties, regDate, updateDate, isValid, invalidStatus, NOW() as removeDate",
				"FROM",
				"radix_package_serviceapi_policy",
				"WHERE",
				"packageID=#{packageID} and serviceAPIID=#{serviceAPIID} and id=#{policyID}" 
			}
		)
	public int backupPackageServiceAPIPolicy(@Param("packageID") byte[] packageID, @Param("serviceAPIID") byte[] serviceAPIID, @Param("policyID") byte[] policyID);
	
	@Delete(
			{
				"DELETE",
				"FROM",
				"radix_package_serviceapi_policy",
				"WHERE",
				"packageID=#{packageID} and serviceAPIID=#{serviceAPIID} and id=#{policyID}" 
			}
	)
	public int deletePackageServiceAPIPolicy(@Param("packageID") byte[] packageID, @Param("serviceAPIID") byte[] serviceAPIID, @Param("policyID") byte[] policyID);

	@Delete(
			{ 
				"DELETE", 
				"FROM", 
				"backup.radix_package_serviceapi_policy", 
				"WHERE",
				"packageID=#{packageID}" 
	})
	public int deleteBackupAllPackageServiceAPIPolicy(@Param("packageID") byte[] packageID);

	@Delete(
			{ 
				"DELETE", 
				"FROM", 
				"backup.radix_package_serviceapi_policy", 
				"WHERE", 
				"packageID=#{packageID} and serviceAPIID=#{serviceAPIID}" 
			}
	)
	public int deleteBackupPackageServiceAPIPolicys(@Param("packageID") byte[] packageID, @Param("serviceAPIID") byte[] serviceAPIID);
	
	@Delete(
			{ 
				"DELETE", 
				"FROM", 
				"backup.radix_package_serviceapi_policy", 
				"WHERE", 
				"packageID=#{packageID} and serviceAPIID=#{serviceAPIID} and id=#{policyID}" 
			}
	)
	public int deleteBackupPackageServiceAPIPolicy(@Param("packageID") byte[] packageID, @Param("serviceAPIID") byte[] serviceAPIID, @Param("policyID") byte[] policyID);
	
}