package com.kthcorp.radix.security.oauth2.provider;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import com.kthcorp.radix.util.UuidViewableLoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;

import com.kthcorp.radix.api.service.ClientKeyManagerService;
import com.kthcorp.radix.domain.client.ClientKey;
import com.kthcorp.radix.domain.exception.ValidateException;
import com.kthcorp.radix.util.UUIDUtils;

public class RadixUserDetailsService implements UserDetailsService {
	
	private final Logger LOG = UuidViewableLoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private ClientKeyManagerService clientKeyManagerService;
	
	private GrantedAuthority defaultAuthority;
	
	@Override
	public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
		
		ClientKey clientKey = null;
		try {
			clientKey = clientKeyManagerService.getClientKey(UUIDUtils.getBytes(userName));
		} catch(ValidateException e) {
			throw new OAuth2Exception("can't found clientKey for userName "+userName+".", e);
		}
		LOG.debug("found clientKey for user {}", userName);
		String clientSecret = clientKey.getSecret() == null ? "" : clientKey.getSecret();
		List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
		authorities.add(defaultAuthority);
		
		return new User(userName, clientSecret, authorities);
	}
	
	public void setDefaultAuthority(GrantedAuthority defaultAuthority) {
		this.defaultAuthority = defaultAuthority;
	}
	
}
