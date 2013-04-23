package com.kthcorp.radix.dao.mybatis;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.kthcorp.radix.domain.businessPlatform.BusinessPlatformKey;

public interface MyBatisBusinessPlatformKeyManagerDaoMapper {
	
	/**
	 * Check if the key string exists
	 * 
	 * @param keyString
	 * @return
	 */
	@Select(
			{
				"SELECT",
				"IFNULL(count(1),0) as pcount",
				"FROM",
				"radix_business_platform",
				"WHERE",
				"businessPlatformKey=#{keyString}"
			}
	)
	public int selectExistsCountWithKeyString(@Param("keyString") String keyString);
	
	/**
	 * Check if the key string exists
	 * 
	 * @param keyString
	 * @return
	 */
	@Select(
			{
				"SELECT",
				"IFNULL(count(1),0) as pcount",
				"FROM",
				"radix_business_platform",
				"WHERE",
				"businessPlatformKey=#{keyString} and secret=#{secret}"
			}
	)
	public int selectExistsCount(@Param("keyString") String keyString, @Param("secret") String secret);
	
	@Select(
			{
				"SELECT",
				"IFNULL(count(1),0) as pcount",
				"FROM",
				"radix_business_platform",
				"WHERE",
				"id=#{id}"
			}
	)
	public int selectExistsCountWithID(@Param("id") byte[] id);
	
	/**
	 * Get Business Platform Key Information
	 * 
	 * @param keyString
	 * @param secret
	 * @return
	 */
	@Select("SELECT " + "id,businessPlatformKey as keyString,secret,description,redirect_uri as redirectUri,domain,regDate,isValid " + "FROM radix_business_platform " + "WHERE " + "businessPlatformKey=#{keyString} and secret=#{secret}")
	public BusinessPlatformKey selectBusinessPlatformKeyWithKeyAndSecretString(@Param("keyString") String keyString, @Param("secret") String secret);
	
	/**
	 * Get Business Platform Key Information
	 * 
	 * @param keyString
	 * @return
	 */
	@Select("SELECT " + "id,businessPlatformKey as keyString,secret,description,redirect_uri as redirectUri,domain,regDate,isValid " + "FROM radix_business_platform " + "WHERE " + "businessPlatformKey=#{keyString}")
	public BusinessPlatformKey selectBusinessPlatformKeyWithKeyString(@Param("keyString") String keyString);
	
	/**
	 * Get business Platform's Domain
	 * 
	 * @param businessPlatformKey
	 * @return
	 */
	@Select({ "SELECT", "domain", "FROM radix_business_platform", "WHERE", "businessPlatformKey=#{businessPlatformKey}" })
	public String selectBusinessPlatformDomainFromKey(@Param("businessPlatformKey") String businessPlatformKey);
	
	/**
	 * Remove Business Platform Key
	 * 
	 * @param keyString
	 * @param secret
	 * @return
	 */
	@Delete("DELETE " + "FROM radix_business_platform " + "WHERE " + "businessPlatformKey=#{keyString} and secret=#{secret}")
	public int deleteBusinessPlatformKey(@Param("keyString") String keyString, @Param("secret") String secret);
	
	/**
	 * Create Business Platform Key
	 * 
	 * @param businessPlatformKey
	 * @return
	 */
	@Insert(
			{
				"INSERT INTO",
				"radix_business_platform",
				"(id, businessPlatformKey, description, secret, domain, redirect_uri, regDate)",
				"VALUES",
				"(#{id}, #{keyString}, #{description}, #{secret}, #{domain}, #{redirectUri}, NOW())"
			}
	)
	public int insertBusinessPlatformKey(BusinessPlatformKey businessPlatformKey);
	
	/**
	 * Update Business Platform Key & their information
	 * 
	 * @param businessPlatformKey
	 * @return
	 */
	@Update("UPDATE " + "radix_business_platform " + "SET " + "domain=#{domain}, description=#{description}, redirect_uri=#{redirectUri}" + " WHERE " + "businessPlatformKey=#{keyString} and secret=#{secret}")
	public int updateBusinessPlatformKey(BusinessPlatformKey businessPlatformKey);
	
}
