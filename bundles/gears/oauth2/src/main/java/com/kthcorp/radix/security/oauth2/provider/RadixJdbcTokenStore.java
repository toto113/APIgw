package com.kthcorp.radix.security.oauth2.provider;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.support.SqlLobValue;
import org.springframework.security.oauth2.common.ExpiringOAuth2RefreshToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.util.OAuth2Utils;
import org.springframework.security.oauth2.common.util.SerializationUtils;
import org.springframework.security.oauth2.provider.AuthorizationRequest;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.AuthenticationKeyGenerator;
import org.springframework.security.oauth2.provider.token.DefaultAuthenticationKeyGenerator;
import org.springframework.security.oauth2.provider.token.JdbcTokenStore;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.util.Assert;

public class RadixJdbcTokenStore implements TokenStore {

	private static final Log LOG = LogFactory.getLog(JdbcTokenStore.class);

	private static final String DEFAULT_ACCESS_TOKEN_INSERT_STATEMENT = "insert into radix_oauth_access_token (token_id, token, authentication_id, authentication, refresh_token, client_id, username, scope, create_date) values (?, ?, ?, ?, ?, ?, ?, ?,  CURRENT_TIMESTAMP)";

	private static final String DEFAULT_ACCESS_TOKEN_SELECT_STATEMENT = "select token_id, token from radix_oauth_access_token where token_id = ?";

	private static final String DEFAULT_ACCESS_TOKEN_AUTHENTICATION_SELECT_STATEMENT = "select token_id, authentication from radix_oauth_access_token where token_id = ?";

	private static final String DEFAULT_ACCESS_TOKEN_FROM_AUTHENTICATION_SELECT_STATEMENT = "select token_id, token from radix_oauth_access_token where authentication_id = ?";

	private static final String DEFAULT_ACCESS_TOKEN_DELETE_STATEMENT = "delete from radix_oauth_access_token where token_id = ?";

	private static final String DEFAULT_ACCESS_TOKEN_DELETE_FROM_REFRESH_TOKEN_STATEMENT = "delete from radix_oauth_access_token where refresh_token = ?";

	private static final String DEFAULT_REFRESH_TOKEN_INSERT_STATEMENT = "insert into radix_oauth_refresh_token (token_id, token, authentication) values (?, ?, ?)";

	private static final String DEFAULT_REFRESH_TOKEN_SELECT_STATEMENT = "select token_id, token from radix_oauth_refresh_token where token_id = ?";

	private static final String DEFAULT_REFRESH_TOKEN_AUTHENTICATION_SELECT_STATEMENT = "select token_id, authentication from radix_oauth_refresh_token where token_id = ?";

	private static final String DEFAULT_REFRESH_TOKEN_DELETE_STATEMENT = "delete from radix_oauth_refresh_token where token_id = ?";
	
	//for radix
	private static final String DEFAULT_ACCESS_TOKEN_COUNT_FROM_CLIENTID_STATEMENT = "select count(token_id) from radix_oauth_access_token where client_id = ?";
	
	private static final String DEFAULT_ACCESS_TOKEN_AUTHENTICATION_SELECT_FROM_CLIENTID_STATEMENT = "select token_id, authentication from radix_oauth_access_token where client_id = ? limit ?, ?";
	
	private static final String DEFAULT_ACCESS_TOKEN_AUTHENTICATION_SELECT_FROM_USERNAME_STATEMENT = "select token_id, authentication from radix_oauth_access_token where username = ?";

	private String insertAccessTokenSql = DEFAULT_ACCESS_TOKEN_INSERT_STATEMENT;

	private String selectAccessTokenSql = DEFAULT_ACCESS_TOKEN_SELECT_STATEMENT;

	private String selectAccessTokenAuthenticationSql = DEFAULT_ACCESS_TOKEN_AUTHENTICATION_SELECT_STATEMENT;

	private String selectAccessTokenFromAuthenticationSql = DEFAULT_ACCESS_TOKEN_FROM_AUTHENTICATION_SELECT_STATEMENT;

	private String deleteAccessTokenSql = DEFAULT_ACCESS_TOKEN_DELETE_STATEMENT;

	private String insertRefreshTokenSql = DEFAULT_REFRESH_TOKEN_INSERT_STATEMENT;

	private String selectRefreshTokenSql = DEFAULT_REFRESH_TOKEN_SELECT_STATEMENT;

	private String selectRefreshTokenAuthenticationSql = DEFAULT_REFRESH_TOKEN_AUTHENTICATION_SELECT_STATEMENT;

	private String deleteRefreshTokenSql = DEFAULT_REFRESH_TOKEN_DELETE_STATEMENT;

	private String deleteAccessTokenFromRefreshTokenSql = DEFAULT_ACCESS_TOKEN_DELETE_FROM_REFRESH_TOKEN_STATEMENT;
	
	private String selectAccessTokenCountFromClientidSql = DEFAULT_ACCESS_TOKEN_COUNT_FROM_CLIENTID_STATEMENT;
	
	private String selectAccessTokenAuthenticationFromClientidSql = DEFAULT_ACCESS_TOKEN_AUTHENTICATION_SELECT_FROM_CLIENTID_STATEMENT;
	
	private String selectAccessTokenAuthenticationFromUsernameSql = DEFAULT_ACCESS_TOKEN_AUTHENTICATION_SELECT_FROM_USERNAME_STATEMENT;

	private AuthenticationKeyGenerator authenticationKeyGenerator = new DefaultAuthenticationKeyGenerator();

	private final JdbcTemplate jdbcTemplate;

	public RadixJdbcTokenStore(DataSource dataSource) {
		Assert.notNull(dataSource, "DataSource required");
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	public void setAuthenticationKeyGenerator(AuthenticationKeyGenerator authenticationKeyGenerator) {
		this.authenticationKeyGenerator = authenticationKeyGenerator;
	}

	public OAuth2AccessToken getAccessToken(OAuth2Authentication authentication) {
		OAuth2AccessToken accessToken = null;

		try {
			accessToken = jdbcTemplate.queryForObject(selectAccessTokenFromAuthenticationSql,
					new RowMapper<OAuth2AccessToken>() {
						public OAuth2AccessToken mapRow(ResultSet rs, int rowNum) throws SQLException {
							return SerializationUtils.deserialize(rs.getBytes(2));
						}
					}, authenticationKeyGenerator.extractKey(authentication));
		}
		catch (EmptyResultDataAccessException e) {
			if (LOG.isInfoEnabled()) {
				LOG.debug("Failed to find access token for authentication " + authentication);
			}
		}

		if (accessToken != null && !authentication.equals(readAuthentication(accessToken))) {
			removeAccessToken(accessToken.getValue());
			// Keep the store consistent (maybe the same user is represented by this authentication but the details have
			// changed)
			storeAccessToken(accessToken, authentication);
		}
		return accessToken;
	}

	public void storeAccessToken(OAuth2AccessToken token, OAuth2Authentication authentication) {
		String refreshToken = null;
		if (token.getRefreshToken() != null) {
			refreshToken = token.getRefreshToken().getValue();
		}
		
		AuthorizationRequest authorizationRequest = authentication.getAuthorizationRequest();
		String clientId;
		String username = null;
		String scope = null;
		clientId = authorizationRequest.getClientId();
		if (!authentication.isClientOnly()) {
			username = authentication.getName();
		}
		if (authorizationRequest.getScope() != null) {
			scope = OAuth2Utils.formatParameterList(authorizationRequest.getScope());
		}

		jdbcTemplate.update(
				insertAccessTokenSql,
				new Object[] { token.getValue(), new SqlLobValue(SerializationUtils.serialize(token)),
						authenticationKeyGenerator.extractKey(authentication),
						new SqlLobValue(SerializationUtils.serialize(authentication)), refreshToken,
						clientId, username, scope}, new int[] {
						Types.VARCHAR, Types.BLOB, Types.VARCHAR, Types.BLOB, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR });
	}

	public OAuth2AccessToken readAccessToken(String tokenValue) {
		OAuth2AccessToken accessToken = null;

		try {
			accessToken = jdbcTemplate.queryForObject(selectAccessTokenSql, new RowMapper<OAuth2AccessToken>() {
				public OAuth2AccessToken mapRow(ResultSet rs, int rowNum) throws SQLException {
					return SerializationUtils.deserialize(rs.getBytes(2));
				}
			}, tokenValue);
		}
		catch (EmptyResultDataAccessException e) {
			if (LOG.isInfoEnabled()) {
				LOG.info("Failed to find access token for token " + tokenValue);
			}
		}

		return accessToken;
	}

	public void removeAccessToken(String tokenValue) {
		jdbcTemplate.update(deleteAccessTokenSql, tokenValue);
	}

	public OAuth2Authentication readAuthentication(OAuth2AccessToken token) {
		OAuth2Authentication authentication = null;

		try {
			authentication = jdbcTemplate.queryForObject(selectAccessTokenAuthenticationSql,
					new RowMapper<OAuth2Authentication>() {
						public OAuth2Authentication mapRow(ResultSet rs, int rowNum) throws SQLException {
							return SerializationUtils.deserialize(rs.getBytes(2));
						}
					}, token.getValue());
		}
		catch (EmptyResultDataAccessException e) {
			if (LOG.isInfoEnabled()) {
				LOG.info("Failed to find access token for token " + token);
			}
		}

		return authentication;
	}

	public void storeRefreshToken(ExpiringOAuth2RefreshToken refreshToken, OAuth2Authentication authentication) {
		jdbcTemplate.update(insertRefreshTokenSql,
				new Object[] { refreshToken.getValue(), new SqlLobValue(SerializationUtils.serialize(refreshToken)),
						new SqlLobValue(SerializationUtils.serialize(authentication)) }, new int[] { Types.VARCHAR,
						Types.BLOB, Types.BLOB });
	}

	public ExpiringOAuth2RefreshToken readRefreshToken(String token) {
		ExpiringOAuth2RefreshToken refreshToken = null;

		try {
			refreshToken = jdbcTemplate.queryForObject(selectRefreshTokenSql,
					new RowMapper<ExpiringOAuth2RefreshToken>() {
						public ExpiringOAuth2RefreshToken mapRow(ResultSet rs, int rowNum) throws SQLException {
							return SerializationUtils.deserialize(rs.getBytes(2));
						}
					}, token);
		}
		catch (EmptyResultDataAccessException e) {
			if (LOG.isInfoEnabled()) {
				LOG.info("Failed to find refresh token for token " + token);
			}
		}

		return refreshToken;
	}

	public void removeRefreshToken(String token) {
		jdbcTemplate.update(deleteRefreshTokenSql, token);
	}

	public OAuth2Authentication readAuthentication(ExpiringOAuth2RefreshToken token) {
		OAuth2Authentication authentication = null;

		try {
			authentication = jdbcTemplate.queryForObject(selectRefreshTokenAuthenticationSql,
					new RowMapper<OAuth2Authentication>() {
						public OAuth2Authentication mapRow(ResultSet rs, int rowNum) throws SQLException {
							return SerializationUtils.deserialize(rs.getBytes(2));
						}
					}, token.getValue());
		}
		catch (EmptyResultDataAccessException e) {
			if (LOG.isInfoEnabled()) {
				LOG.info("Failed to find access token for token " + token);
			}
		}

		return authentication;
	}

	public void removeAccessTokenUsingRefreshToken(String refreshToken) {
		jdbcTemplate.update(deleteAccessTokenFromRefreshTokenSql, new Object[] { refreshToken },
				new int[] { Types.VARCHAR });
	}
	
	//for radix
	public Map<Integer, List<OAuth2Authentication>> getAuthenticationFromClientid(String clientId, int start, int limit) {

		Map<Integer, List<OAuth2Authentication>> map = new HashMap<Integer, List<OAuth2Authentication>>();
		int count = 0;
		List<OAuth2Authentication> list = new ArrayList<OAuth2Authentication>();
		try {
			count = jdbcTemplate.queryForInt(selectAccessTokenCountFromClientidSql, clientId);
			list = jdbcTemplate.query(selectAccessTokenAuthenticationFromClientidSql,
					new RowMapper<OAuth2Authentication>() {
						public OAuth2Authentication mapRow(ResultSet rs, int rowNum) throws SQLException {
							return SerializationUtils.deserialize(rs.getBytes(2));
						}
					}, new Object[] { clientId, start, limit });
		}
		catch (DataAccessException e) {
			if (LOG.isInfoEnabled()) {
				LOG.info("Failed to find access token for client_id " + clientId);
			}
		}
		map.put(count, list);
		return map;
	}
	
	public int getAuthenticationCountFromClientid(String clientId) {
		
		int count = 0;
		try {
			count = jdbcTemplate.queryForInt(selectAccessTokenCountFromClientidSql, clientId);
		}
		catch (DataAccessException e) {
			if (LOG.isInfoEnabled()) {
				LOG.info("Failed to find access token count for client_id " + clientId);
			}
		}
		return count;
	}
	
	public List<OAuth2Authentication> getAuthenticationFromClientidList(String clientId, int start, int limit) {

		List<OAuth2Authentication> list = new ArrayList<OAuth2Authentication>();
		try {
			list = jdbcTemplate.query(selectAccessTokenAuthenticationFromClientidSql,
					new RowMapper<OAuth2Authentication>() {
						public OAuth2Authentication mapRow(ResultSet rs, int rowNum) throws SQLException {
							return SerializationUtils.deserialize(rs.getBytes(2));
						}
					}, new Object[] { clientId, start, limit });
		}
		catch (DataAccessException e) {
			if (LOG.isInfoEnabled()) {
				LOG.info("Failed to find access token for client_id " + clientId);
			}
		}
		return list;
	}
	
	public List<OAuth2Authentication> getAuthenticationFromUsername(String username) {

		List<OAuth2Authentication> list = new ArrayList<OAuth2Authentication>();
		try {
			list = jdbcTemplate.query(selectAccessTokenAuthenticationFromUsernameSql,
					new RowMapper<OAuth2Authentication>() {
						public OAuth2Authentication mapRow(ResultSet rs, int rowNum) throws SQLException {
							return SerializationUtils.deserialize(rs.getBytes(2));
						}
					}, username);
		}
		catch (DataAccessException e) {
			if (LOG.isInfoEnabled()) {
				LOG.info("Failed to find access token for username " + username);
			}
		}

		return list;
	}

	public void setInsertAccessTokenSql(String insertAccessTokenSql) {
		this.insertAccessTokenSql = insertAccessTokenSql;
	}

	public void setSelectAccessTokenSql(String selectAccessTokenSql) {
		this.selectAccessTokenSql = selectAccessTokenSql;
	}

	public void setDeleteAccessTokenSql(String deleteAccessTokenSql) {
		this.deleteAccessTokenSql = deleteAccessTokenSql;
	}

	public void setInsertRefreshTokenSql(String insertRefreshTokenSql) {
		this.insertRefreshTokenSql = insertRefreshTokenSql;
	}

	public void setSelectRefreshTokenSql(String selectRefreshTokenSql) {
		this.selectRefreshTokenSql = selectRefreshTokenSql;
	}

	public void setDeleteRefreshTokenSql(String deleteRefreshTokenSql) {
		this.deleteRefreshTokenSql = deleteRefreshTokenSql;
	}

	public void setSelectAccessTokenAuthenticationSql(String selectAccessTokenAuthenticationSql) {
		this.selectAccessTokenAuthenticationSql = selectAccessTokenAuthenticationSql;
	}

	public void setSelectRefreshTokenAuthenticationSql(String selectRefreshTokenAuthenticationSql) {
		this.selectRefreshTokenAuthenticationSql = selectRefreshTokenAuthenticationSql;
	}

	public void setSelectAccessTokenFromAuthenticationSql(String selectAccessTokenFromAuthenticationSql) {
		this.selectAccessTokenFromAuthenticationSql = selectAccessTokenFromAuthenticationSql;
	}

	public void setDeleteAccessTokenFromRefreshTokenSql(String deleteAccessTokenFromRefreshTokenSql) {
		this.deleteAccessTokenFromRefreshTokenSql = deleteAccessTokenFromRefreshTokenSql;
	}
}
