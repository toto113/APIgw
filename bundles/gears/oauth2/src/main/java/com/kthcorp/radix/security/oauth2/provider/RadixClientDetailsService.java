package com.kthcorp.radix.security.oauth2.provider;

import java.util.List;
import org.slf4j.Logger;
import com.kthcorp.radix.util.UuidViewableLoggerFactory;
import org.springframework.security.oauth2.common.exceptions.InvalidClientException;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
import org.springframework.security.oauth2.provider.BaseClientDetails;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import com.kthcorp.radix.api.service.ClientKeyManagerService;
import com.kthcorp.radix.api.service.ScopeManagerService;
import com.kthcorp.radix.domain.client.ClientKey;
import com.kthcorp.radix.domain.exception.ValidateException;
import com.kthcorp.radix.util.UUIDUtils;

public class RadixClientDetailsService implements ClientDetailsService {
	
	private final Logger LOG = UuidViewableLoggerFactory.getLogger(this.getClass());
	
	private ClientKeyManagerService clientKeyManagerService;
	public void setClientKeyManagerService(ClientKeyManagerService clientKeyManagerService) {
		this.clientKeyManagerService = clientKeyManagerService;
	}

	private ScopeManagerService scopeManagerService;
	public void setScopeManagerService(ScopeManagerService scopeManagerService) {
		this.scopeManagerService = scopeManagerService;
	}
	
	private String commaSeperatedResourceIds;
	
	private String commaSeparatedAuthorizedGrantTypes;
	
	private String commaSeparatedAuthorities;
	
	public ClientDetails loadClientByClientId(String clientID) throws OAuth2Exception {
		
		LOG.debug("loalClientByClientId({})", clientID);
		ClientKey clientKey = null;
		
		try {
			clientKey = clientKeyManagerService.getValidClientKey(UUIDUtils.getBytes(clientID));
		} catch(ValidateException e) {
			throw new OAuth2Exception("validation exception : " + e.getMessage());
		}
		
		BaseClientDetails details;
		if(clientKey != null) {
			details = new BaseClientDetails(commaSeperatedResourceIds, null, commaSeparatedAuthorizedGrantTypes, commaSeparatedAuthorities);
			List<String> scopes = scopeManagerService.getScopeList(UUIDUtils.getBytes(clientID));
			details.setScope(scopes);
			details.setClientId(clientID);
			details.setClientSecret(clientKey.getSecret());
			details.setRegisteredRedirectUri(clientKey.getRedirectUri());
		} else {
			throw new InvalidClientException("Client not found: " + clientID);
		}
		
		return details;
	}
	
	public void setCommaSeperatedResourceIds(String commaSeperatedResourceIds) {
		this.commaSeperatedResourceIds = commaSeperatedResourceIds;
	}
	
	public void setCommaSeparatedAuthorizedGrantTypes(String commaSeparatedAuthorizedGrantTypes) {
		this.commaSeparatedAuthorizedGrantTypes = commaSeparatedAuthorizedGrantTypes;
	}
	
	public void setCommaSeparatedAuthorities(String commaSeparatedAuthorities) {
		this.commaSeparatedAuthorities = commaSeparatedAuthorities;
	}
	
}
