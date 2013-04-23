package com.kthcorp.radix.security.oauth2.provider.endpoint;

import java.net.URLEncoder;
import java.security.Principal;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import com.kthcorp.radix.util.UuidViewableLoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
import org.springframework.security.oauth2.provider.AuthorizationRequest;
import org.springframework.security.oauth2.provider.error.DefaultProviderExceptionHandler;
import org.springframework.security.oauth2.provider.error.ProviderExceptionHandler;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import com.kthcorp.radix.api.validator.ClientKeyValidator;
import com.kthcorp.radix.dao.mybatis.MyBatisClientKeyManagerDaoMapper;
import com.kthcorp.radix.util.UUIDUtils;
import com.kthcorp.radix.domain.client.ClientKey;
import com.kthcorp.radix.domain.exception.ValidateException;

@Controller
@SessionAttributes(types = AuthorizationRequest.class)
public class AccessConfirmEndpoint implements InitializingBean {

	@Autowired
	private MyBatisClientKeyManagerDaoMapper mapper;
	
	private final Logger LOG = UuidViewableLoggerFactory.getLogger(this.getClass());
	
	public void afterPropertiesSet() throws Exception {

	}
	
	private String resourceAuthorizeUrl;
	public void setResourceAuthorizeUrl(String resourceAuthorizeUrl) {
		this.resourceAuthorizeUrl = resourceAuthorizeUrl;
	}
	
	private String redirectUrl;
	public void setRedirectUrl(String redirectUrl) {
		this.redirectUrl = redirectUrl;
	}
	
	@RequestMapping(value = "/oauth/confirm_access", params = "response_type")
	public ModelAndView access_confirm(Map<String, Object> model, @RequestParam("response_type") String responseType,
			@RequestParam Map<String, String> parameters, SessionStatus sessionStatus, Principal principal) throws Exception {

		LOG.debug(">>>>> access_confirm, responseType={}", responseType);
		for(Entry<String, String> entry : parameters.entrySet()) {
			LOG.debug("parameters:{}={}", entry.getKey(), entry.getValue());
		}
		
		String clientId = parameters.get("client_id");
		if(clientId==null||clientId.equals("")) { throw new ValidateException("client_id must be provided"); }

		StringBuilder sb = new StringBuilder(redirectUrl);
		sb.append("?response_type=").append(responseType);
		sb.append("&client_id=").append(parameters.get("client_id"));
		sb.append("&redirect_uri=").append(parameters.get("redirect_uri"));
		sb.append("&app_name=").append(getAppNameWithClientId(parameters.get("client_id")));
		
		ModelAndView mav;
		RedirectView rv = new RedirectView(resourceAuthorizeUrl + "?scope=" + parameters.get("scope") + "&redirect_url=" + URLEncoder.encode(sb.toString(), "utf-8"));
		mav = new ModelAndView(rv);
		return mav;
	}
	
	private ProviderExceptionHandler providerExceptionHandler = new DefaultProviderExceptionHandler();
	
	@ExceptionHandler(OAuth2Exception.class)
	public HttpEntity<OAuth2Exception> handleException(OAuth2Exception e, ServletWebRequest webRequest) throws Exception {
		return providerExceptionHandler.handle(e);
	}
	
	private String getAppNameWithClientId(String clientId) throws ValidateException {
	
		ClientKey clientKey = getClientKey(UUIDUtils.getBytes(clientId));
		return clientKey.getApplicationName();
		
	}
	
	private ClientKey getClientKey(byte[] clientID) throws ValidateException {
		ClientKeyValidator.keyValidate(clientID);
		return mapper.selectClientKeyWithID(clientID);
	}
	
}
