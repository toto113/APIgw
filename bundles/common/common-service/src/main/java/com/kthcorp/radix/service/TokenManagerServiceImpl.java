package com.kthcorp.radix.service;

import java.util.List;

import org.slf4j.Logger;
import com.kthcorp.radix.util.UuidViewableLoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.kthcorp.radix.api.service.TokenManagerService;
import com.kthcorp.radix.dao.mybatis.MyBatisTokenManagerDaoMapper;
import com.kthcorp.radix.domain.exception.ValidateException;
import com.kthcorp.radix.domain.token.AccessToken;

/**
 * Service Managing Service Implementation
 * 
 * @author lemo2000@kthcorp.com
 * @since 2012.02.02
 */
public class TokenManagerServiceImpl implements TokenManagerService {

	@SuppressWarnings("unused")
	private static final Logger LOG = UuidViewableLoggerFactory.getLogger(TokenManagerServiceImpl.class);
	
	@Autowired
	private MyBatisTokenManagerDaoMapper mapper;
	
	@Override
	public List<AccessToken> getAccessTokenListWithUserName(String username) {
		List<AccessToken> accessTokenList = this.mapper.getAccessTokenListWithRadixClientByUserName(username);
		return accessTokenList;
	}

	@Override
	public boolean removeAccessToken(AccessToken accessToken)
			throws ValidateException {
		
		if(null == accessToken) {
			throw new ValidateException("cannot find AccessToken object");
		}
		
		if(null == accessToken.getTokenID()) {
			throw new ValidateException("client_id is null");
		}
		
		int removedRowCount = this.mapper.removeAccessTokenByTokenId(accessToken.getTokenID());
		
		if( 0 > removedRowCount ) {
			throw new ValidateException("removing accessToken failed. trying to remove not existing Service.DirectMethod, token_id->"+accessToken.getTokenID());
		}// else if ( 0 == removedRowCount ) {
//			throw new ValidateException("trying to remove not existing Service.DirectMethod, token_id->"+accessToken.getToken_id());
//		}
		
		return true;
	}

	@Override
	public AccessToken getAccessTokenByTokenId(String tokenID) {
		return this.mapper.getAccessTokenByTokenId(tokenID);
	}

}
