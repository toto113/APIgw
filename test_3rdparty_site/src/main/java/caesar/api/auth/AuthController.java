package caesar.api.auth;

import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.client.RestOperations;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

@Controller
@RequestMapping("/oauth")
public class AuthController {

	private final Logger LOG = LoggerFactory.getLogger(this.getClass());
	
	private RestOperations restTemplate;
	public void setRestTemplate(RestOperations restTemplate) {
		this.restTemplate = restTemplate;
	}
	
	@RequestMapping(value = "/auth", method = RequestMethod.GET)
	public String doGetAuth(HttpServletRequest request, HttpServletResponse response) throws Exception {

		LOG.debug(">>>>> doGetAuth : request={}", request);
		return "auth";
	}
	
	@RequestMapping(value = "/auth/{token}", method = RequestMethod.GET)
	public ModelAndView doGetToken(HttpServletRequest request, HttpServletResponse response, @PathVariable String token) throws Exception {

		LOG.debug(">>>>> doGetToken : request={}", request);
		LOG.debug(">>>>> token={}", token);
		try {
			String[] arr = token.split("&");
			String accessToken = arr[0];
//			String tokenType=arr[1];
			
			LOG.debug(">>>>> accessToken={}", accessToken);
//			LOG.debug(">>>>> tokenType={}", tokenType);
			
			request.getSession().setAttribute("accessToken", accessToken);
		} catch(Exception e) {
			LOG.error(e.getMessage(), e);
		}
		RedirectView rv = new RedirectView("/");
		return new ModelAndView(rv);
	}
	
	@RequestMapping(value = "/login", method = RequestMethod.GET)
	public String doGetLogin(HttpServletRequest request, HttpServletResponse response) throws Exception {

		LOG.debug(">>>>> doGetLogin : request={}", request);
		String redirectUrl = request.getParameter("redirect_url");
		LOG.debug("redirect_url={}", redirectUrl);
		String scope = request.getParameter("scope");
		LOG.debug("scope={}", scope);
		request.setAttribute("redirect_url", redirectUrl);
		List<String> scopeList = new ArrayList<String>();
		if(scope != null) {
			String[] arr = scope.split(",");
			for(String s : arr) scopeList.add(s);
		}
		request.setAttribute("scopeList", scopeList); 
		return "login";
	}
	
	@RequestMapping(value = "/login", method = RequestMethod.POST)
	public ResponseEntity<byte[]> doPostLogin(HttpServletRequest request, HttpServletResponse response) throws Exception {

		LOG.debug(">>>>> doPostLogin : request={}", request);
		String redirectUrl = request.getParameter("redirect_url");
		String userApproval = request.getParameter("user_oauth_approval");
		String userid = request.getParameter("userid");
		//String secret = "pudding";
		String secret = getSecret(userid);
		String userKey = encode(userid, secret);
		String scope = request.getParameter("scope");
		LOG.debug("redirect_url={}", redirectUrl);
		LOG.debug("userKey={}", userKey);
		LOG.debug("user_oauth_approval={}", userApproval);
		LOG.debug("scope={}", scope);
		
		if("true".equalsIgnoreCase(userApproval)) {		
			HttpHeaders requestHeaders = new HttpHeaders();
			Map<String, String> headerParameters = new HashMap<String, String>();
			headerParameters.put("charset", "utf-8");
			requestHeaders.setContentType(new MediaType(MediaType.APPLICATION_JSON, headerParameters));
			requestHeaders.add("Authorization", userKey);
			
			MultiValueMap<String, Object> requestBody = new LinkedMultiValueMap<String, Object>();
			HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<MultiValueMap<String, Object>>(requestBody, requestHeaders);
			return restTemplate.exchange(redirectUrl + "&user_oauth_approval=" + userApproval + "&scope=" + scope, HttpMethod.POST, requestEntity, byte[].class);
		}
		return null;
	}
	
	private String encode(String userid, String userpw) {
		
		String key = userid + ":" + userpw;
		byte[] bytes = Base64.encodeBase64(key.getBytes());
		return "Basic " + new String(bytes);
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
}