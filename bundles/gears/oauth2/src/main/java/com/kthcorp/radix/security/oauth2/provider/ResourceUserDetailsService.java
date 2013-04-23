package com.kthcorp.radix.security.oauth2.provider;

import java.util.Arrays;
import java.util.List;
import org.slf4j.Logger;
import com.kthcorp.radix.util.UuidViewableLoggerFactory;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import java.security.MessageDigest;

public class ResourceUserDetailsService implements UserDetailsService {

	private final Logger LOG = UuidViewableLoggerFactory.getLogger(this.getClass());
	
	private static final String DEFAULT_SECRET = "somewhereoverrainbow";
	
	@SuppressWarnings("unused")
	private String secret = DEFAULT_SECRET;
	public void setSecret(String secret) {
		this.secret = secret;
	}
	
	private GrantedAuthority defaultAuthority;
	public void setDefaultAuthority(GrantedAuthority defaultAuthority) {
		this.defaultAuthority = defaultAuthority;
	}

	@Override
	public UserDetails loadUserByUsername(String username)
			throws UsernameNotFoundException {
		
		LOG.debug("loadUserByUsername({})", username);
		List<GrantedAuthority> authorities = Arrays.asList(defaultAuthority); 
		return new User(username, getSecret(username), authorities);
	}
	
	private String getSecret(String userName) {
		String encoded = null;
		try {
			encoded = encode(userName);
		} catch(Exception e) {
			LOG.error("md5 encoding error : {}", e.getMessage(), e);
		}
		return encoded; 
	}
	
	private static String encode(String src) throws Exception {
		byte[] code = encode_raw(src.getBytes("UTF-8"));
		StringBuffer sb = new StringBuffer();
		for(int i = 0; i < code.length; i++)
			sb.append(String.format("%02x", 0xff&(char)code[i]));
		
		return sb.toString();
	}
	
	private static byte[] encode_raw(byte[] raw) throws Exception {
		MessageDigest md = null;
		try {
			md = MessageDigest.getInstance("MD5");
			md.update(raw);
		} catch(Exception e) {
			throw new Exception(e);			
		}
		return md.digest();
	}
	
	public static void main(String[] args) throws Exception {
		
		System.out.println(encode("2cbbf691c1c2997521acacee78867a4eb4244a74f323817b8349eef08ba99c56"));
		System.out.println("226be6ecd102d42433cb4743fc3eb758");
	}
}