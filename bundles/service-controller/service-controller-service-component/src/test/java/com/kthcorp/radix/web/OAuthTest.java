package com.kthcorp.radix.web;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;

import javax.servlet.ServletException;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mortbay.util.MultiMap;
import org.mortbay.util.UrlEncoded;
import org.slf4j.Logger;
import com.kthcorp.radix.util.UuidViewableLoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.ResourceServerTokenServices;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import com.kthcorp.radix.util.test.junit.MysqlEmbeddableSpringJUnit4ClassRunner;
import org.springframework.web.servlet.DispatcherServlet;

import com.kthcorp.radix.util.FailedTest;
import com.kthcorp.radix.web.mock.MockWebApplication;
import com.kthcorp.radix.web.mock.MockWebApplicationContextLoader;

@RunWith(MysqlEmbeddableSpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/META-INF/spring/spring-all-context-test.xml" }, loader = MockWebApplicationContextLoader.class)
@MockWebApplication(name = "radixtest")
@Ignore
public class OAuthTest extends AbstractJUnit4SpringContextTests {
	
	@Autowired
	private DispatcherServlet dispatcherServlet;
	
	private static Logger LOG = UuidViewableLoggerFactory.getLogger(OAuthTest.class);
	
	private static String accessToken = null;
	
	private static int expiresIn = 0;
	
	@Before
	public void testCreateToken() throws ServletException, IOException {
		
		MockHttpServletRequest request = new MockHttpServletRequest();
		MockHttpServletResponse response = new MockHttpServletResponse();
		
		request.setServletPath("/oauth/authorize");
		request.setScheme("http");
		request.setMethod("POST");
		
		request.addHeader("accept", "text/html");
		
		UsernamePasswordAuthenticationToken userAuthentication = new UsernamePasswordAuthenticationToken("59ffa6f4-0901-4ffc-82ad-44687540ab4b", "21334d4ffb7994f5094eb41b5a70dd3a165780f9", Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")));
		
		SecurityContextHolder.getContext().setAuthentication(userAuthentication);
		
		request.getSession().setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, SecurityContextHolder.getContext());
		request.setUserPrincipal(userAuthentication);
		
		final String testRedirectURI = "http://redirect.withapi.com";
		
		request.addParameter("user_oauth_approval", "true");
		request.addParameter("response_type", "token");
		request.addParameter("state", "mystateid");
		request.addParameter("client_id", "59ffa6f4-0901-4ffc-82ad-44687540ab4b");
		request.addParameter("redirect_uri", testRedirectURI);
		request.addParameter("scope", "testScope");
		request.addParameter("state", "testState");
		
		dispatcherServlet.service(request, response);
		
		String location = response.getRedirectedUrl();
		
		try {
			URL redirectURL = new URL(location);
			String redirectURI = redirectURL.getProtocol() + "://" + redirectURL.getAuthority() + redirectURL.getPath() + (redirectURL.getQuery() == null || "".equals(redirectURL.getQuery()) ? "" : ("?" + redirectURL.getQuery()));
			
			Assert.assertTrue(redirectURI.startsWith(testRedirectURI));
			Assert.assertTrue(redirectURL.getRef() != null);
			Assert.assertTrue(!"".equals(redirectURL.getRef()));
			
			String ref = redirectURL.getRef();
			
			MultiMap parameters = new MultiMap();
			
			UrlEncoded.decodeTo(ref, parameters, "UTF-8");
			
			accessToken = parameters.getString("access_token");
			Assert.assertNotNull(accessToken);
			
			String expiresInString = parameters.getString("expires_in");
			Assert.assertNotNull(expiresInString);
			
			expiresIn = Integer.parseInt(expiresInString);
			Assert.assertTrue(expiresIn > 0);
			
		} catch(MalformedURLException e) {
			e.printStackTrace();
			Assert.fail("Test Fail : redirectURL is not well-formed. location="+location);
		}
	}
	
	@Test
	public void nullTest() { }
	
	@FailedTest
	public void testValidateToken() throws ServletException, IOException {
		
		MockHttpServletRequest request = new MockHttpServletRequest();
		MockHttpServletResponse response = new MockHttpServletResponse();
		
		request.setRequestURI("/traffic/1/BusLane/?busNo=501");
		request.setMethod("GET");
		
		LOG.debug("*** Use AccessToken : {}", accessToken);
		
		ResourceServerTokenServices tokenServices = (ResourceServerTokenServices) applicationContext.getBean("tokenServices");
		
		OAuth2Authentication userAuthentication = tokenServices.loadAuthentication(accessToken);
		
		SecurityContextHolder.getContext().setAuthentication(userAuthentication);
		
		request.getSession().setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, SecurityContextHolder.getContext());
		request.setUserPrincipal(userAuthentication);
		
		// request.addHeader("Authorization", "Bearer " + accessToken);
		request.addHeader("accept", "application/json");
		
		dispatcherServlet.service(request, response);
		
		Assert.assertEquals(HttpStatus.OK.value(), response.getStatus());
		Assert.assertEquals("it's test!", response.getContentAsString());
	}
	
	@Rule
	public ExpectedException exception = ExpectedException.none();
	
	@Ignore
	// @Test
	public void testExpireToken() throws ServletException, IOException {
		
		try {
			Thread.sleep(60 * 1000);
		} catch(InterruptedException e) {
			e.printStackTrace();
		}
		
		MockHttpServletRequest request = new MockHttpServletRequest();
		MockHttpServletResponse response = new MockHttpServletResponse();
		
		request.setRequestURI("/aro/BusLane/501");
		request.setMethod("GET");
		
		request.addHeader("Authorization", "Bearer " + accessToken);
		request.addHeader("accept", "application/json");
		
		dispatcherServlet.service(request, response);
		
		exception.expect(org.springframework.web.client.HttpClientErrorException.class);
		exception.expectMessage("401 Unauthorized");
		
		Assert.assertEquals(response.getStatus(), HttpStatus.OK.value());
	}
	
	@Ignore
	// @Test
	public void testRefreshToken() {
		
	}
	
	@Ignore
	// @Test
	public void testCreateTokenbyGet() {
		
		// String getUrl = CONTEXT_URL + "/oauth/authorize";
		//
		// HttpHeaders requestHeaders = new HttpHeaders();
		//
		// requestHeaders.set("Authorization", "Basic " + new String(Base64.encode("01df9407-ad22-4627-91d9-fce368b5bf01:9f57683a301af1bdab2f09a658881e2bcf72bd30".getBytes())));
		// requestHeaders.setAccept(Arrays.asList(MediaType.TEXT_HTML));
		//
		// final String testRedirectURI = "http://api.withapi.com/test";
		//
		// MultiValueMap<String, String> postParameters = new LinkedMultiValueMap<String, String>();
		// postParameters.add("user_oauth_approval", "true");
		// postParameters.add("response_type", "token");
		// postParameters.add("state", "mystateid");
		// postParameters.add("client_id", "01df9407-ad22-4627-91d9-fce368b5bf01");
		// postParameters.add("redirect_uri", testRedirectURI);
		// postParameters.add("scope", "testScope");
		//
		// HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<MultiValueMap<String, String>>(postParameters, requestHeaders);
		//
		// ResponseEntity<String> responseEntity = restTemplate.exchange(getUrl, HttpMethod.GET, requestEntity, String.class);
		// System.out.println(responseEntity.getStatusCode());
		// Assert.assertTrue(responseEntity.getStatusCode().equals(HttpStatus.FOUND));
		// HttpHeaders responseHeaders = responseEntity.getHeaders();
		// List<String> location = responseHeaders.get("location");
		//
		// try {
		// URL redirectURL = new URL(location.get(0));
		// String redirectURI = redirectURL.getProtocol() + "://" + redirectURL.getAuthority() + redirectURL.getPath() + (redirectURL.getQuery() == null || "".equals(redirectURL.getQuery()) ? "" :
		// ("?" + redirectURL.getQuery()));
		//
		// Assert.assertTrue(redirectURI.startsWith(testRedirectURI));
		// Assert.assertTrue(redirectURL.getRef() != null);
		// Assert.assertTrue(!"".equals(redirectURL.getRef()));
		//
		// String ref = redirectURL.getRef();
		//
		// MultiMap parameters = new MultiMap();
		//
		// UrlEncoded.decodeTo(ref, parameters, "UTF-8");
		//
		// accessToken = parameters.getString("access_token");
		// Assert.assertNotNull(accessToken);
		//
		// String expiresInString = parameters.getString("expires_in");
		// Assert.assertNotNull(expiresInString);
		//
		// expiresIn = Integer.parseInt(expiresInString);
		// Assert.assertTrue(expiresIn > 0);
		//
		// } catch (MalformedURLException e) {
		// e.printStackTrace();
		// Assert.fail("Test Fail : redirectURL is not well-formed.");
		// }
	}
	
}
