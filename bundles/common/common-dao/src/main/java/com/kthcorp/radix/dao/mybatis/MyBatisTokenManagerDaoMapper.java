package com.kthcorp.radix.dao.mybatis;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.InsertProvider;
import org.apache.ibatis.annotations.Select;

import com.kthcorp.radix.domain.service.Service;
import com.kthcorp.radix.domain.token.AccessToken;

public interface MyBatisTokenManagerDaoMapper {

	/*
	 * +-------------------------------+ | Tables_in_radix |
	 * +-------------------------------+ | radix_service | |
	 * radix_service_apis_mapping | | radix_service_apis_partnerapi | |
	 * radix_service_apis_serviceapi | | radix_service_routing_direct |
	 * +-------------------------------+
	 */
	
	/* radix_service */
	@InsertProvider(method = "insertServiceInDynamicSQL", type = MyBatisServiceManagerDaoDynamicSQL.class)
	public int insertService(Service service);

	@Select({
			"SELECT",
			"radix_oauth_access_token.token_id AS tokenID, radix_oauth_access_token.token, radix_oauth_access_token.authentication_id AS authenticationID,",
			"radix_oauth_access_token.authentication, radix_oauth_access_token.refresh_token AS refreshToken, radix_oauth_access_token.client_id AS clientID,",
			"radix_oauth_access_token.username, radix_oauth_access_token.scope,",
			"radix_client.application_name AS applicationName, radix_client.application_description AS applicationDescription, radix_client.isValid, radix_client.invalidStatus,",
			"UNIX_TIMESTAMP(radix_oauth_access_token.create_date) AS createDate",
			"FROM",
			"radix_client",
			"INNER JOIN radix_oauth_access_token",
			"ON radix_client.id = UUIDTOBIN(radix_oauth_access_token.client_id)",
			"WHERE", "radix_oauth_access_token.token_id=#{tokenID} ",
			"ORDER BY radix_oauth_access_token.create_date DESC" })
	public AccessToken getAccessTokenByTokenId(String tokenID);

	@Select({
			"SELECT",
			"radix_oauth_access_token.token_id AS tokenID, radix_oauth_access_token.token, radix_oauth_access_token.authentication_id AS authenticationID,",
			"radix_oauth_access_token.authentication, radix_oauth_access_token.refresh_token AS refreshToken, radix_oauth_access_token.client_id AS clientID,",
			"radix_oauth_access_token.username, radix_oauth_access_token.scope,",
			"radix_client.application_name AS applicationName, radix_client.application_description AS applicationDescription, radix_client.isValid, radix_client.invalidStatus,",
			"UNIX_TIMESTAMP(radix_oauth_access_token.create_date) AS createDate",
			"FROM",
			"radix_client",
			"INNER JOIN radix_oauth_access_token",
			"ON radix_client.id = UUIDTOBIN(radix_oauth_access_token.client_id)",
			"WHERE", "radix_oauth_access_token.username=#{username} ",
			"ORDER BY radix_oauth_access_token.create_date DESC"})
	public List<AccessToken> getAccessTokenListWithRadixClientByUserName(
			String username);

	@Delete({ "DELETE", "FROM", "radix_oauth_access_token", "WHERE",
			"token_id=#{tokenID}" })
	public int removeAccessTokenByTokenId(String tokenID);

}
