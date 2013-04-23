package com.kthcorp.radix.web;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

import junit.framework.Assert;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mortbay.util.MultiMap;
import org.mortbay.util.UrlEncoded;
import org.slf4j.Logger;
import com.kthcorp.radix.util.UuidViewableLoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.codec.Base64;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import com.kthcorp.radix.util.test.junit.MysqlEmbeddableSpringJUnit4ClassRunner;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestOperations;

@Ignore
@RunWith(MysqlEmbeddableSpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/META-INF/spring/spring-application-context-test.xml", "classpath:/META-INF/spring/rest-client/spring-rest-client-context-test.xml" })
public class BizPortalOAuthTestSkip extends AbstractJUnit4SpringContextTests {
	
	private static final Logger LOG = UuidViewableLoggerFactory.getLogger(BizPortalOAuthTestSkip.class);
	
	@Autowired
	private RestOperations restTemplate;
	
	private static final String AUTHORIZE_URL = "http://211.113.53.126:8090/oauth/authorize";
	private static final String PLATFORM_URL = "http://211.113.53.126:8090/platform/clientKey";
	
	@Test
	public void normalBizPortalApiTest() {
		String accessToken = null;
		
		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set("Authorization", "Basic " + new String(Base64.encode("59ffa6f4-0901-4ffc-82ad-44687540ab4b:21334d4ffb7994f5094eb41b5a70dd3a165780f9".getBytes())));
		requestHeaders.setAccept(Arrays.asList(MediaType.TEXT_HTML));
		
		final String testRedirectURI = "http://www.paran.com";
		
		MultiValueMap<String, String> postParameters = new LinkedMultiValueMap<String, String>();
		postParameters.add("user_oauth_approval", "true");
		postParameters.add("response_type", "token");
		postParameters.add("state", "mystateid");
		postParameters.add("client_id", "59ffa6f4-0901-4ffc-82ad-44687540ab4b");
		postParameters.add("redirect_uri", testRedirectURI);
		postParameters.add("scope", "testScope");
		
		HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<MultiValueMap<String, String>>(postParameters, requestHeaders);
		
		ResponseEntity<String> responseEntity = restTemplate.exchange(AUTHORIZE_URL, HttpMethod.POST, requestEntity, String.class);
		LOG.debug(responseEntity.getStatusCode().toString());
		Assert.assertTrue(responseEntity.getStatusCode().equals(HttpStatus.FOUND));
		HttpHeaders responseHeaders = responseEntity.getHeaders();
		List<String> location = responseHeaders.get("location");
		
		URL redirectURL;
		try {
			redirectURL = new URL(location.get(0));
			MultiMap parameters = new MultiMap();
			
			String ref = redirectURL.getRef();
			UrlEncoded.decodeTo(ref, parameters, "UTF-8");
			accessToken = parameters.getString("access_token");
			Assert.assertNotNull(accessToken);
		} catch(MalformedURLException e) {
			e.printStackTrace();
		}
		
		requestHeaders.clear();
		
		requestHeaders.set("Authorization", "Bearer " + accessToken);
		requestHeaders.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
		
		MultiValueMap<String, String> postParameters2 = new LinkedMultiValueMap<String, String>();
		postParameters2.add("redirect_uri", "http://www.paran.com");
		postParameters2.add("key_type", "P");
		postParameters2.add("scope", "testScope");
		postParameters2.add("client_id", "testClient");
		
		requestEntity = new HttpEntity<MultiValueMap<String, String>>(postParameters2, requestHeaders);
		
		responseEntity = restTemplate.exchange(PLATFORM_URL, HttpMethod.POST, requestEntity, String.class);
		Assert.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
	}
	
	@Test
	public void abnormalBizPortalApiTest() {
		String accessToken = null;
		
		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.set("Authorization", "Basic " + new String(Base64.encode("59ffa6f4-0901-4ffc-82ad-44687540ab4b:21334d4ffb7994f5094eb41b5a70dd3a165780f9".getBytes())));
		requestHeaders.setAccept(Arrays.asList(MediaType.TEXT_HTML));
		
		final String testRedirectURI = "http://www.paran.com";
		
		MultiValueMap<String, String> postParameters = new LinkedMultiValueMap<String, String>();
		postParameters.add("user_oauth_approval", "true");
		postParameters.add("response_type", "token");
		postParameters.add("state", "mystateid");
		postParameters.add("client_id", "59ffa6f4-0901-4ffc-82ad-44687540ab4b");
		postParameters.add("redirect_uri", testRedirectURI);
		postParameters.add("scope", "testScope");
		
		HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<MultiValueMap<String, String>>(postParameters, requestHeaders);
		
		ResponseEntity<String> responseEntity = restTemplate.exchange(AUTHORIZE_URL, HttpMethod.POST, requestEntity, String.class);
		LOG.debug(responseEntity.getStatusCode().toString());
		Assert.assertTrue(responseEntity.getStatusCode().equals(HttpStatus.FOUND));
		HttpHeaders responseHeaders = responseEntity.getHeaders();
		List<String> location = responseHeaders.get("location");
		
		URL redirectURL;
		try {
			redirectURL = new URL(location.get(0));
			MultiMap parameters = new MultiMap();
			
			String ref = redirectURL.getRef();
			UrlEncoded.decodeTo(ref, parameters, "UTF-8");
			accessToken = parameters.getString("access_token");
			Assert.assertNotNull(accessToken);
		} catch(MalformedURLException e) {
			e.printStackTrace();
		}
		
		requestHeaders.clear();
		
		requestHeaders.set("Authorization", "Bearer 123");
		requestHeaders.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
		
		MultiValueMap<String, String> postParameters2 = new LinkedMultiValueMap<String, String>();
		postParameters2.add("redirect_uri", "http%3A%2F%2Fapi.withapi.com%2Ftest");
		postParameters2.add("key_type", "P");
		postParameters2.add("scope", "testScope");
		postParameters2.add("client_id", "testClient");
		
		requestEntity = new HttpEntity<MultiValueMap<String, String>>(postParameters2, requestHeaders);
		
		try {
			responseEntity = restTemplate.exchange(PLATFORM_URL, HttpMethod.POST, requestEntity, String.class);
		} catch(HttpClientErrorException e) {
			Assert.assertEquals("401 Unauthorized", e.getMessage());
		}
	}
}
