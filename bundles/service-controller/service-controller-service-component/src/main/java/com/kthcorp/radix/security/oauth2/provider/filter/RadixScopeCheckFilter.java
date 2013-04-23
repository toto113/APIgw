package com.kthcorp.radix.security.oauth2.provider.filter;

import java.io.IOException;
import java.util.List;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import com.kthcorp.radix.util.UuidViewableLoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.common.exceptions.InvalidScopeException;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.util.Assert;
import org.springframework.web.filter.GenericFilterBean;

import com.kthcorp.radix.api.service.ClientKeyManagerService;
import com.kthcorp.radix.api.service.ScopeManagerService;
import com.kthcorp.radix.api.service.ServiceManagerService;
import com.kthcorp.radix.domain.exception.DataBaseProcessingException;
import com.kthcorp.radix.domain.exception.NotFoundException;
import com.kthcorp.radix.domain.exception.ValidateException;
import com.kthcorp.radix.domain.service.api.protocol.http.ServerProtocol;
import com.kthcorp.radix.domain.service.api.serviceAPI.ServiceAPI;
import com.kthcorp.radix.domain.service.routing.RoutingMethod;
import com.kthcorp.radix.domain.service.routing.direct.DirectMethod;
import com.kthcorp.radix.util.ApiKeyUtil;
import com.kthcorp.radix.util.UUIDUtils;
import com.kthcorp.radix.domain.service.routing.RoutingMethodSelector;

public class RadixScopeCheckFilter extends GenericFilterBean {
	
	/**
	 * log4j to log.
	 */
	private static final Logger LOG = UuidViewableLoggerFactory.getLogger(RadixScopeCheckFilter.class);
	private static final String KEY_SEPARATOR = ":";
	private String domain;
	
	@Autowired
	private ScopeManagerService scopeManagerService;
	
	@Autowired
	private ServiceManagerService serviceManagerService;
	
	@SuppressWarnings("unused")
	@Autowired
	private ClientKeyManagerService clientKeyManagerService;
	
	public void setDomain(String domain) {
		this.domain = domain;
	}
	
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {
		
		if(!equalApiType(request, "oauth")) {
			
			final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
			LOG.debug("authentication={}", authentication);
			Assert.state(authentication != null, "cannot find authentication");
			
			String clientKey = null;
			String userName = null;
			if(authentication instanceof OAuth2Authentication) {
				OAuth2Authentication oauth2Authentication = (OAuth2Authentication)authentication;
				clientKey = oauth2Authentication.getAuthorizationRequest().getClientId();
				userName = oauth2Authentication.getUserAuthentication().getName();
			} else if(authentication instanceof UsernamePasswordAuthenticationToken) {
				clientKey = authentication.getName();
			}
			LOG.debug("clientKey={}", clientKey);
			LOG.debug("username={}", userName);
			
			// making requested scope		
			String apiKey = null;
			apiKey = getRequestScope(request);
			LOG.debug("requestScopeUri={}", apiKey);
			String resourcePath = ApiKeyUtil.getResourcePath(((HttpServletRequest)request).getRequestURI());
			
			byte[] clientID = UUIDUtils.getBytes(clientKey);
			LOG.debug("clientID={}", clientID);
			
			// chekcing scope in registered list of scope
			try {
				// if clientkey equals businessplatform key then skip checking scope!
				if(!"59ffa6f4-0901-4ffc-82ad-44687540ab4b".equals(clientKey)) {
					// 만약 clientKey가 null이라서 checkScope를 타게 되는 경우, http request의 헤더 authorization의 값 "Basic"의 소대문자를 확인하라.
					checkScope(clientID, resourcePath, apiKey);
					LOG.debug("scope checked. apiKey={}", apiKey);
				}
				request.setAttribute("businessPlatformKey", clientKey);
				request.setAttribute("clientKey", clientKey);
				request.setAttribute("apiKey", apiKey);
				request.setAttribute("userName", userName);
				
				if(equalApiType(request, "platform")) {
					request.setAttribute("serviceApiName", getPlatformApiName(request));
				} else {
					List<RoutingMethod> routingMethodList = serviceManagerService.getRoutingMethods(apiKey);

					RoutingMethod routingMethod = RoutingMethodSelector.selectRoutingMethod(routingMethodList, resourcePath);
					if(routingMethod==null) {
						LOG.info("not found routingMethod for apiKey. apiKey={}", apiKey);
						responseScopeError((HttpServletRequest) request, (HttpServletResponse) response, "Not found api information");
						return;
					}
					ServiceAPI serviceApi = ((DirectMethod)routingMethod).getServiceAPI();
					request.setAttribute("serviceApiName", serviceApi.getName());
					request.setAttribute("pathTemplate", ((ServerProtocol)serviceApi.getProtocolObj()).getPathTemplate());
				}
				filterChain.doFilter(request, response);
			} catch(InvalidScopeException ise) {
				LOG.info("responseScopeError clientID={}, errorMessage={}", clientID, ise.getMessage());
				responseScopeError((HttpServletRequest) request, (HttpServletResponse) response, ise.getMessage());
				return;
			} 

		} else {
			filterChain.doFilter(request, response);
		}
	}
	
	private void responseScopeError(HttpServletRequest request, HttpServletResponse response, String errorMessage) {
		response.setHeader("Cache-Control", "no-store");
		response.setContentType("text/plain");
		response.setCharacterEncoding("UTF-8");
		response.setStatus(HttpServletResponse.SC_NOT_ACCEPTABLE);
		
		try {
			response.getWriter().print(errorMessage);
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	private boolean equalApiType(ServletRequest request, String apiType) {
		boolean rtn = false;
		String requestUrl = ((HttpServletRequest) request).getRequestURI();
		if(requestUrl.toLowerCase().startsWith("/" + apiType + "/") && requestUrl.length() > 1) {
			rtn = true;
		} else {
			rtn = false;
		}
		return rtn;
	}
	
	private String getPlatformApiName(ServletRequest request) {
		String platformApiName = "";
		String requestUrl = ((HttpServletRequest) request).getRequestURI();
		if(requestUrl.length() > "/platform/".length()) {
			requestUrl = requestUrl.substring("/platform/".length(), requestUrl.length());
			platformApiName = requestUrl.substring(0, requestUrl.indexOf("/") < 0 ? requestUrl.length() : requestUrl.indexOf("/"));
		}
		return platformApiName;
	}
	
	private String getRequestScope(ServletRequest request) {
		
		StringBuffer sbUri = new StringBuffer();
		sbUri.append(domain);
		sbUri.append(KEY_SEPARATOR);
		sbUri.append(KEY_SEPARATOR);
		sbUri.append(((HttpServletRequest) request).getMethod().toString().toUpperCase());
		sbUri.append(KEY_SEPARATOR);
		sbUri.append(((HttpServletRequest) request).getRequestURI());
		
		return sbUri.toString();
	}
	
	private void checkScope(byte[] clientID, String resourcePath, String apiKey) {
		// TODO Checking Scope		
		try {
			scopeManagerService.getScopePolicies(apiKey, resourcePath, clientID);
		} catch(NotFoundException e) {
			e.printStackTrace();
			throw new InvalidScopeException("Not found matched scope");
		} catch(ValidateException e) {
			e.printStackTrace();
			throw new InvalidScopeException("Invalid scope");
		} catch(DataBaseProcessingException e) {
			e.printStackTrace();
			throw new InvalidScopeException(e.getMessage());
		}
	}
	
}
