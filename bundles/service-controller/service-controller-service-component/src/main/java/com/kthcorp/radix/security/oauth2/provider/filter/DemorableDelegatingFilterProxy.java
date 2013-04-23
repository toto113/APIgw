package com.kthcorp.radix.security.oauth2.provider.filter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import com.kthcorp.radix.util.UuidViewableLoggerFactory;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.web.filter.DelegatingFilterProxy;

import com.kthcorp.radix.web.filter.DemorableConfiguration;
/**
 * 받은 요청의 accessToken이 데모를 위한 경우
 * 기존 spring security의 인증과정을 생략하기 위한 클래스.
 * 
 * 상속한 DelegatingFilterProxy 클래스가 spring-security-context.xml의 설정을 실제 시행하는 클래스이다.
 * 조건을 보고 실행여부를 결정한다.
 * 
 * 인증해야 하는 경우 슈퍼클래스의 doFilter()를 호출하여 spring에 설정된 필터들이 실행되도록 하고,
 * 그렇지 않은 경우 전달받은 filterChain(요건 was의 일반적인 필터들이다)를 호출한다.
 * 
 * 조건에 필요한 값들은 web.xml에서 설정한다.
 * 
 * @author dhrim
 *
 */
public class DemorableDelegatingFilterProxy extends DelegatingFilterProxy {
	
	private static final Logger LOG = UuidViewableLoggerFactory.getLogger(DemorableDelegatingFilterProxy.class);

	
	private List<String> demoSiteList = new ArrayList<String>();
	private String userNameForDemo = null;
	private String accessTokenForDemo = null;
	
	public void setDemoSite(String demoeSitesString) {
		if(demoeSitesString==null) {
			LOG.warn("configurered referer is empty.");
			return; 
		}
		String[] demoSites = demoeSitesString.split(",");
		for(String demoSite : demoSites) {
			demoSite = demoSite.trim();
			if("".equals(demoSite)) { continue; }
			this.demoSiteList.add(demoSite);
		}
	}
	
	public void setUserNameForDemo(String userNameForDemo) {
		if(userNameForDemo==null) {
			LOG.warn("configurered userNameForDemo is empty.");
			return; 
		}
		this.userNameForDemo = userNameForDemo;
	}
	
	public void setAccessTokenForDemo(String accessTokenForDemo) {
		if(accessTokenForDemo==null) {
			LOG.warn("configurered accessTokenForDemo is empty.");
			return; 
		}
		this.accessTokenForDemo = accessTokenForDemo;
	}
	
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws ServletException, IOException {
		
		if(isForDemo((HttpServletRequest)request)) {
			LOG.debug("the request is for demo. skip authenticating. url={}", ((HttpServletRequest)request).getRequestURL());
			
			// 뒷단의 로직에서 하여간에 null check를 하기 때문에 의미 없는 값이라도 채워둔다.
			((HttpServletRequest)request).setAttribute("userName", userNameForDemo);
			((HttpServletRequest)request).setAttribute("clientKey", DemorableConfiguration.CLIENT_KEY_FOR_DEMO);
			
			// web.xml에 설정된 다른 filter들을 호출한다. 
			// 요 filterChain의 맨 마지막에 호출할 servlet이 있다.
			// 따라서 요 호출을 빠뜨리면 servlet이 호출되지 않는다.
			filterChain.doFilter(request, response);
		}
		else {
			// spring의 Filter들을 호출한다.
			super.doFilter(request, response, filterChain);
		}

	}

	private boolean isForDemo(HttpServletRequest request) {
		if(!isFromDemoSite(request)) { return false; }
		if(!isAccessTokenForDemo(request)) { return false; }
		return true;
	}

	private boolean isFromDemoSite(HttpServletRequest request) {
		String referer = request.getHeader("Referer");
		for(String demoSite : demoSiteList) {
			if(demoSite.equalsIgnoreCase(referer)) { return true; }
		}
		return false;
	}

	private boolean isAccessTokenForDemo(HttpServletRequest request) {
		if(accessTokenForDemo==null) { return false; }
		String accessToken = request.getParameter(OAuth2AccessToken.ACCESS_TOKEN);
		return accessTokenForDemo.equals(accessToken);
	}
	
	
}
