package caesar.api.auth;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestOperations;

@Controller
@RequestMapping("/")
public class ClientController {
	
	private final Logger LOG = LoggerFactory.getLogger(this.getClass());
	private String redirectUriPath = null;
	public void setRedirectUriPath(String redirectUriPath) {
		this.redirectUriPath = redirectUriPath;
	}
	private String responseType = null;
	public void setResponseType(String responseType) {
		this.responseType = responseType;
	}
	private RestOperations restTemplate;
	public void setRestTemplate(RestOperations restTemplate) {
		this.restTemplate = restTemplate;
	}
	private String tokenUrl = null;
	public void setTokenUrl(String tokenUrl) {
		this.tokenUrl = tokenUrl;
	}
	
	@RequestMapping("/")
	public String doIndex(HttpServletRequest request, HttpServletResponse response) throws Exception {

		LOG.debug(">>>>> doIndex : request={}", request);
		LOG.debug(">>>>> accessToken={}", request.getAttribute("accessToken"));
		return "index";
	}
	
	@RequestMapping(value = "/radix", method = RequestMethod.POST)
	public ResponseEntity<byte[]> doCallRadix(HttpServletRequest request) throws Exception {
		
		LOG.debug(">>>>> doCallRadix : request={}", request);
		String uri = request.getParameter("uri");
		String method = request.getParameter("method");
		
		LOG.debug("uri={}", uri);
		LOG.debug("method={}", method);
		
		if(request.getSession().getAttribute("accessToken") == null) {
			return doGetAccessToken(request);
		}

		HttpMethod httpMethod = HttpMethod.valueOf(request.getParameter("method"));
		
		HttpHeaders requestHeaders = new HttpHeaders();
//		requestHeaders.add("Authorization", "Bearer " + request.getSession().getAttribute("accessToken"));
		Map<String, String> uriVariables = new Hashtable<String, String>();
		MultiValueMap<String, Object> requestBody = new LinkedMultiValueMap<String, Object>();
		
		HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<MultiValueMap<String, Object>>(requestBody, requestHeaders);
		ResponseEntity<byte[]> responseEntity = null;
		try{
			responseEntity = restTemplate.exchange(uri, httpMethod, requestEntity, byte[].class, uriVariables);
		}catch(Exception e){
			return new ResponseEntity<byte[]>(e.getMessage().getBytes(), HttpStatus.UNAUTHORIZED);
		}
		
		return responseEntity;
	}
	
	@RequestMapping(value = "/access", method = RequestMethod.GET)
	public ResponseEntity<byte[]> doGetAccessToken(HttpServletRequest request) {
		
		HttpHeaders requestHeaders = new HttpHeaders();
		Map<String, String> headerParameters = new HashMap<String, String>();
		headerParameters.put("charset", "utf-8");
		requestHeaders.setContentType(new MediaType(MediaType.APPLICATION_JSON, headerParameters));

		
		
		String apiKey = request.getParameter("apikey");
		String secret = request.getParameter("secret");
		String scope = request.getParameter("scope");
		
		requestHeaders.add("Authorization", "Basic " + encode(apiKey, secret));
		String redirectUri = "http://"+request.getLocalAddr()+":"+request.getLocalPort()+redirectUriPath;
		
		Map<String, String> uriVariables = new Hashtable<String, String>();
		uriVariables.put("client_id", apiKey);
		uriVariables.put("scope", scope);
		uriVariables.put("response_type", responseType);
		uriVariables.put("redirectUri", redirectUri);

		LOG.debug("requestHeaders="+requestHeaders);
		LOG.debug("apiKey="+apiKey);
		LOG.debug("secret="+secret);
		LOG.debug("scope="+scope);

		
		MultiValueMap<String, Object> requestBody = new LinkedMultiValueMap<String, Object>();
		HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<MultiValueMap<String, Object>>(requestBody, requestHeaders);
		String uri = this.tokenUrl + "?response_type={response_type}&client_id={client_id}&scope={scope}&redirect_uri={redirectUri}";
		ResponseEntity<byte[]> responseEntity = null;
		try {
			responseEntity = restTemplate.exchange(uri , HttpMethod.GET, requestEntity, byte[].class, uriVariables);
		} catch(Exception e) {
			String responseBody = "";
			if(e instanceof HttpClientErrorException) {
				responseBody = ((HttpClientErrorException)e).getResponseBodyAsString();
			}
			throw new RuntimeException("getting access token failed. responseBody="+responseBody+". uri="+uri+", uriVariables="+uriVariables, e);
		}
		return responseEntity;
		
	}
	
	private String encode(String userid, String userpw) {
		
		String key = userid + ":" + userpw;
		byte[] bytes = Base64.encodeBase64(key.getBytes());
		return new String(bytes);
	}

	
//	@ExceptionHandler({ Exception.class })
//	public ResponseEntity<byte[]> handleRequestResourceMissingException(HttpServletRequest request, HttpServletResponse response, Exception radixException)  {
//		LOG.error("RequestResourceMissingException, RequestInvalidVersionException {} : {}",  radixException.getMessage(), radixException.getStackTrace());
//		return new ResponseEntity<byte[]>(radixException.getMessage().getBytes(), null, HttpStatus.UNAUTHORIZED);
//	}
	
}
