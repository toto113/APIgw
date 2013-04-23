package com.kthcorp.radix.dao.mybatis;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.kthcorp.radix.domain.client.ClientKey;

public interface MyBatisClientKeyManagerDaoMapper {
	
	
	/** radix_client **/
	
	/**
	 * Check if the key string exists
	 * 
	 * @param keyString
	 * @return
	 */
	@Select(
			"SELECT " + "IFNULL(count(1),0) as pcount " + "FROM radix_client " + " WHERE " + "clientKey=#{keyString} and secret=#{secret}"
	)
	public int isExistsWithKeyString(@Param("keyString") String keyString, @Param("secret") String secret);
	
	/**
	 * Check if the key string exists
	 * 
	 * @param businessPlatformID
	 * @param clientID
	 * @param keyTypeCode
	 * @return
	 */
	@Select(
			"SELECT " + "IFNULL(count(1),0) as pcount " + "FROM radix_client " + " WHERE " + "businessPlatformID=#{businessPlatformID} and partnerID=#{partnerID} and type=#{keyTypeCode} and secret=#{secret}"
	)
	public int isExists(@Param("businessPlatformID") byte[] businessPlatformID, @Param("partnerID") String partnerID, @Param("keyTypeCode") String keyTypeCode, @Param("secret") String secret);

	/**
	 * Check if the key string exists
	 * 
	 * @param businessPlatformID
	 * @param clientID
	 * @param keyTypeCode
	 * @return
	 */
	@Select(
			"SELECT " + "IFNULL(count(1),0) as pcount " + "FROM radix_client " + " WHERE " + "id=#{clientID}"
	)
	public int isExistsWithID(@Param("clientID") byte[] clientID);

	/**
	 * Get Client Key Information
	 * 
	 * @param keyString
	 * @param secret
	 * @return
	 */
	@Select("SELECT " + "id,clientKey as keyString,secret,businessPlatformID,partnerID,redirect_uri as redirectUri,type,regDate,isValid, application_name as applicationName, application_description as applicationDescription " + "FROM radix_client " + "WHERE " + "clientKey=#{keyString} and secret=#{secret} and type=#{keyTypeCode}")
	public ClientKey selectClientKeyWithKeyAndSecretString(@Param("keyString") String keyString, @Param("secret") String secret, @Param("keyTypeCode") String keyTypeCode);
	
	/**
	 * Get Client Key Information
	 * 
	 * @param keyString
	 * @return
	 */
	@Select("SELECT " + "id,clientKey as keyString,secret,businessPlatformID,partnerID,redirect_uri as redirectUri,type,regDate,isValid, application_name as applicationName, application_description as applicationDescription " + "FROM radix_client " + "WHERE " + "clientKey=#{keyString}")
	public ClientKey selectClientKeyWithKeyString(@Param("keyString") String keyString);
	
	/**
	 * Get Client Key Information
	 * 
	 * @param keyString
	 * @return
	 */
	@Select("SELECT " + "id,clientKey as keyString,secret,businessPlatformID,partnerID,redirect_uri as redirectUri,type,regDate,isValid, application_name as applicationName, application_description as applicationDescription " + "FROM radix_client " + "WHERE " + "partnerID=#{partnerID}")
	public List<ClientKey> selectClientKeyWithPartnerID(@Param("partnerID") String partnerID);
	
	/**
	 * Get Client Key Information
	 * 
	 * @param keyString
	 * @return
	 */
	@Select("SELECT " + "id,clientKey as keyString,secret,businessPlatformID,partnerID,redirect_uri as redirectUri,type,regDate,isValid, application_name as applicationName, application_description as applicationDescription " + "FROM radix_client " + "WHERE " + "businessPlatformID=#{businessPlatformID} and partnerID=#{partnerID} and type=#{keyTypeCode} and secret=#{secret}" + "limit 1" // Just
																																																																																		// Top
																																																																																		// one
	)
	public ClientKey selectClientKey(@Param("businessPlatformID") byte[] businessPlatformID, @Param("partnerID") String partnerID, @Param("keyTypeCode") String keyTypeCode, @Param("secret") String secret);
	
	/**
	 * Get Client Key Information
	 * 
	 * @param keyString
	 * @return
	 */
	@Select("SELECT " + "id,clientKey as keyString,secret,businessPlatformID,partnerID,redirect_uri as redirectUri,type,regDate,isValid, application_name as applicationName, application_description as applicationDescription " + "FROM radix_client " + "WHERE " + "id=#{clientID}" // Just
																																																																																		// Top
																																																																																		// one
	)
	public ClientKey selectClientKeyWithID(@Param("clientID") byte[] clientID);

	
	/**
	 * Remove Client Key
	 * 
	 * @param keyString
	 * @param keyTypeCode
	 * @param secret
	 * @return
	 */
	@Delete("DELETE " + "FROM radix_client " + "WHERE " + "clientKey=#{keyString} and type=#{keyTypeCode} and secret=#{secret}")
	public int deleteClientKey(@Param("keyString") String keyString, @Param("keyTypeCode") String keyTypeCode, @Param("secret") String secret);
	
	/**
	 * Create Client Key
	 * 
	 * @param clientKey
	 * @return
	 */
	@Insert(
			{
				"INSERT INTO",
				"radix_client",
				"(id, clientKey, secret, businessPlatformID, partnerID, type, redirect_uri, regDate, application_name, application_description)",
				"VALUES",
				"(#{id}, #{keyString}, #{secret}, #{businessPlatformID}, #{partnerID}, #{type}, #{redirectUri}, NOW(), #{applicationName}, #{applicationDescription})"
			}
	)
	public int insertClientKey(ClientKey clientKey);
	
	/**
	 * Backup Client Key
	 * 
	 * @param keyString
	 * @param keyTypeCode
	 * @param secret
	 * @return
	 */
	@Insert( 
		{
			"INSERT",
			"INTO",
			"backup.radix_client",
			"(id, clientKey, secret, businessPlatformID, partnerID, type, redirect_uri, regDate, removeDate, isValid)",
			"SELECT",
			"id, clientKey, secret, businessPlatformID, partnerID, type, redirect_uri, regDate, NOW() as removeDate, isValid",
			"FROM",
			"radix_client",
			"WHERE",
			"clientKey=#{keyString} and type=#{keyTypeCode} and secret=#{secret}"
		}
	)
	public int backupClientKey(@Param("keyString") String keyString, @Param("keyTypeCode") String keyTypeCode, @Param("secret") String secret);
	
	/**
	 * Remove Backup Client Key
	 * 
	 * @param keyString
	 * @param keyTypeCode
	 * @param secret
	 * @return
	 */
	@Delete("DELETE " + "FROM backup.radix_client " + "WHERE " + "clientKey=#{keyString} and type=#{keyTypeCode} and secret=#{secret}")
	public int deleteBackupClientKey(@Param("keyString") String keyString, @Param("keyTypeCode") String keyTypeCode, @Param("secret") String secret);

	/**
	 * Update Client Key & their information
	 * 
	 * @param clientKey
	 * @return
	 */
	@Update("UPDATE " + "radix_client " + "SET " + "businessPlatformID=#{businessPlatformID}, partnerID=#{partnerID}, type=#{type}, redirect_uri=#{redirectUri}" + "WHERE " + "clientKey=#{keyString} and secret=#{secret}")
	public int updateClientKey(ClientKey clientKey);
	/**
	 * Update Client Key & their information
	 * 
	 * @param clientKey
	 * @return
	 */
	@Update("UPDATE " + "radix_client " + "SET " + "isValid='N'" + "WHERE " + "clientKey=#{keyString} and secret=#{secret}")
	public int invalidateClientKey(ClientKey clientKey);
	
	
}
