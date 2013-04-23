package com.kthcorp.radix.web;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;

import junit.framework.Assert;

import org.apache.http.HttpStatus;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import com.kthcorp.radix.util.UuidViewableLoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import com.kthcorp.radix.util.test.junit.MysqlEmbeddableSpringJUnit4ClassRunner;
import org.springframework.web.servlet.DispatcherServlet;

import com.kthcorp.radix.web.mock.MockWebApplication;
import com.kthcorp.radix.web.mock.MockWebApplicationContextLoader;

@RunWith(MysqlEmbeddableSpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/META-INF/spring/spring-all-context-test.xml" }, loader = MockWebApplicationContextLoader.class)
@MockWebApplication(name = "radixtest")
public class PlatformAPIClientKeyManageTest extends AbstractJUnit4SpringContextTests {
	
	private static final Logger LOG = UuidViewableLoggerFactory.getLogger(PlatformAPIClientKeyManageTest.class);
	
	@Autowired
	private DispatcherServlet dispatcherServlet;
	
	private String businessPlatformKey = "59ffa6f4-0901-4ffc-82ad-44687540ab4b";
	private String clientKey = "95aff93e-a15e-4836-928b-2065de2bd6e4";
	private String secret = "658efa095f37a9f044b21a2a278b26674c3a65b1";
	
	/* Create Client Key Test(Start) */
	
	@Test
	public void doNFPartnerIDCreateClientKey() throws ServletException, IOException, JSONException {
		
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setRequestURI("/platform/clientKeys");
		request.setMethod("POST");
		request.setContentType("application/x-www-form-urlencoded");
		request.setAttribute("businessPlatformKey", businessPlatformKey);
		
		Map<String, String> map = new HashMap<String, String>();
		map.put("key_type", "P");
		map.put("redirect_uri", "http://api.withapi.com/test");
		
		request.addParameters(map);
		
		MockHttpServletResponse response = new MockHttpServletResponse();
		
		dispatcherServlet.service(request, response);
		
		LOG.info("status->" + response.getStatus());
		
		String result = response.getContentAsString();
		LOG.info("result->" + result);
		
		Assert.assertEquals(response.getStatus(), HttpStatus.SC_UNPROCESSABLE_ENTITY);
		
//		JSONObject obj = new JSONObject(result);
//		Assert.assertEquals(obj.get("parameter"), "invalid_data");
	}
	
	@Test
	public void doIVPartnerIDCreateClientKey() throws ServletException, IOException, JSONException {
		
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setRequestURI("/platform/clientKeys");
		request.setMethod("POST");
		request.setContentType("application/x-www-form-urlencoded");
		request.setAttribute("businessPlatformKey", businessPlatformKey);
		
		Map<String, String> map = new HashMap<String, String>();
		map.put("partner_id", "testPartner12222222222222222222222222222222222222222222222222222222222222222222222211111111111111111111111111111111111111111111111111111111111111");
		map.put("key_type", "P");
		map.put("redirect_uri", "http://api.withapi.com/test");
		
		request.addParameters(map);
		
		MockHttpServletResponse response = new MockHttpServletResponse();
		
		dispatcherServlet.service(request, response);
		LOG.info("status->" + response.getStatus());
		
		String result = response.getContentAsString();
		LOG.info("result->" + result);
		
		Assert.assertEquals(response.getStatus(), HttpStatus.SC_UNPROCESSABLE_ENTITY);
		
//		JSONObject obj = new JSONObject(result);
//		Assert.assertEquals(obj.get("status"), "invalid_data");
	}
	
	@Test
	public void doNFKeyTypeCreateClientKey() throws ServletException, IOException, JSONException {
		
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setRequestURI("/platform/clientKeys");
		request.setMethod("POST");
		request.setContentType("application/x-www-form-urlencoded");
		request.setAttribute("businessPlatformKey", businessPlatformKey);
		
		Map<String, String> map = new HashMap<String, String>();
		map.put("partner_id", "testPartner1");
		map.put("redirect_uri", "http://api.withapi.com/test");
		
		request.addParameters(map);
		
		MockHttpServletResponse response = new MockHttpServletResponse();
		
		dispatcherServlet.service(request, response);
		LOG.info("status->" + response.getStatus());
		
		String result = response.getContentAsString();
		LOG.info("result->" + result);
		
		Assert.assertEquals(response.getStatus(), HttpStatus.SC_UNPROCESSABLE_ENTITY);
		
//		JSONObject obj = new JSONObject(result);
//		Assert.assertEquals(obj.get("status"), "invalid_data");
		
	}
	
	@Test
	public void doIVKeyTypeCreateClientKey() throws ServletException, IOException, JSONException {
		
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setRequestURI("/platform/clientKeys");
		request.setMethod("POST");
		request.setContentType("application/x-www-form-urlencoded");
		request.setAttribute("businessPlatformKey", businessPlatformKey);
		
		Map<String, String> map = new HashMap<String, String>();
		map.put("partner_id", "testPartner2");
		map.put("key_type", "K");
		map.put("redirect_uri", "http://api.withapi.com/test");
		
		request.addParameters(map);
		
		MockHttpServletResponse response = new MockHttpServletResponse();
		
		dispatcherServlet.service(request, response);
		LOG.info("status->" + response.getStatus());
		
		String result = response.getContentAsString();
		LOG.info("result->" + result);
		
		Assert.assertEquals(response.getStatus(), HttpStatus.SC_UNPROCESSABLE_ENTITY);
		
//		JSONObject obj = new JSONObject(result);
//		Assert.assertEquals(obj.get("status"), "invalid_data");
	}
	
	@Test
	public void doNFReURICreateClientKey() throws ServletException, IOException, JSONException {
		
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setRequestURI("/platform/clientKeys");
		request.setMethod("POST");
		request.setContentType("application/x-www-form-urlencoded");
		request.setAttribute("businessPlatformKey", businessPlatformKey);
		
		Map<String, String> map = new HashMap<String, String>();
		map.put("partner_id", "testPartner1");
		map.put("key_type", "P");
		
		request.addParameters(map);
		
		MockHttpServletResponse response = new MockHttpServletResponse();
		
		dispatcherServlet.service(request, response);
		LOG.info("status->" + response.getStatus());
		
		String result = response.getContentAsString();
		LOG.info("result->" + result);
		
		Assert.assertEquals(response.getStatus(), HttpStatus.SC_UNPROCESSABLE_ENTITY);
		
//		JSONObject obj = new JSONObject(result);
//		Assert.assertEquals(obj.get("status"), "invalid_data");
	}
	
	@Test
	public void doSuccessCreateClientKey() throws ServletException, IOException, JSONException {
		
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setRequestURI("/platform/clientKeys");
		request.setMethod("POST");
		request.setContentType("application/x-www-form-urlencoded");
		request.setAttribute("businessPlatformKey", businessPlatformKey);
		
		Map<String, String> map = new HashMap<String, String>();
		map.put("partner_id", "testPartner1");
		map.put("key_type", "P");
		map.put("redirect_uri", "http://api.withapi.com/test");
		map.put("application_name", "someApplicationName");
		map.put("application_description", "someApplicationDescription");
		
		request.addParameters(map);
		
		MockHttpServletResponse response = new MockHttpServletResponse();
		
		dispatcherServlet.service(request, response);
		LOG.info("status->" + response.getStatus());
		
		String result = response.getContentAsString();
		LOG.info("result->" + result);
		
		Assert.assertEquals(response.getStatus(), HttpStatus.SC_CREATED);
		
		JSONObject obj = new JSONObject(result);
		Assert.assertEquals(obj.get("status"), "success");
		Assert.assertEquals(obj.get("body_type"), "MAP");
		
		JSONObject body = obj.getJSONObject("body");
		clientKey = body.getString("client_key");
		secret = body.getString("secret");
		
		this.doSuccessRemoveClientKey();
	}
	
	/* Remove Client Key Test (Start) */
	
	@Test
	public void doNFClientKeyRemoveClientKey() throws ServletException, IOException, JSONException {
		
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setRequestURI("/platform/clientKeys/");
		request.setMethod("DELETE");
		request.setQueryString("secret=" + secret + "&key_type=P");
		request.setAttribute("businessPlatformKey", businessPlatformKey);
		
		MockHttpServletResponse response = new MockHttpServletResponse();
		
		dispatcherServlet.service(request, response);
		
		LOG.info("status->" + response.getStatus());
		
		String result = response.getContentAsString();
		LOG.info("result->" + result);
		
		Assert.assertEquals(response.getStatus(), HttpStatus.SC_UNPROCESSABLE_ENTITY);
		
//		JSONObject obj = new JSONObject(result);
//		Assert.assertEquals(obj.get("status"), "invalid_data");
	}
	
	@Test
	public void doNFSecretRemoveClientKey() throws ServletException, IOException, JSONException {
		
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setRequestURI("/platform/clientKeys/" + clientKey);
		request.setMethod("DELETE");
		request.setQueryString("secret=&key_type=P");
		request.setAttribute("businessPlatformKey", businessPlatformKey);
		
		MockHttpServletResponse response = new MockHttpServletResponse();
		
		dispatcherServlet.service(request, response);
		
		LOG.info("status->" + response.getStatus());
		
		String result = response.getContentAsString();
		LOG.info("result->" + result);
		
		Assert.assertEquals(response.getStatus(), HttpStatus.SC_UNPROCESSABLE_ENTITY);
		
//		JSONObject obj = new JSONObject(result);
//		Assert.assertEquals(obj.get("status"), "invalid_data");
	}
	
	@Test
	public void doIVSecretRemoveClientKey() throws ServletException, IOException, JSONException {
		
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setRequestURI("/platform/clientKeys/" + clientKey);
		request.setMethod("DELETE");
		request.setQueryString("secret=12345&key_type=S");
		request.setAttribute("businessPlatformKey", businessPlatformKey);
		
		MockHttpServletResponse response = new MockHttpServletResponse();
		
		dispatcherServlet.service(request, response);
		
		LOG.info("status->" + response.getStatus());
		
		String result = response.getContentAsString();
		LOG.info("result->" + result);
		
		Assert.assertEquals(response.getStatus(), HttpStatus.SC_UNPROCESSABLE_ENTITY);
		
//		JSONObject obj = new JSONObject(result);
//		Assert.assertEquals(obj.get("status"), "invalid_data");
	}
	
	@Test
	public void doIVSecretRemoveClientKey2() throws ServletException, IOException, JSONException {
		
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setRequestURI("/platform/clientKeys/" + clientKey);
		request.setMethod("DELETE");
		request.setQueryString("secret=658efa095f37a9f044b21a2a278b26674c3a65b9&key_type=P");
		request.setAttribute("businessPlatformKey", businessPlatformKey);
		
		MockHttpServletResponse response = new MockHttpServletResponse();
		
		dispatcherServlet.service(request, response);
		
		LOG.info("status->" + response.getStatus());
		
		String result = response.getContentAsString();
		LOG.info("result->" + result);
		
		Assert.assertEquals(response.getStatus(), HttpStatus.SC_UNPROCESSABLE_ENTITY);
		
//		JSONObject obj = new JSONObject(result);
//		Assert.assertEquals(obj.get("status"), "invalid_data");
	}
	
	@Test
	public void doNFKeyTypeRemoveClientKey() throws ServletException, IOException, JSONException {
		
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setRequestURI("/platform/clientKeys/" + clientKey);
		request.setMethod("DELETE");
		request.setQueryString("secret=" + secret);
		request.setAttribute("businessPlatformKey", businessPlatformKey);
		
		MockHttpServletResponse response = new MockHttpServletResponse();
		
		dispatcherServlet.service(request, response);
		
		LOG.info("status->" + response.getStatus());
		
		String result = response.getContentAsString();
		LOG.info("result->" + result);
		
		Assert.assertEquals(response.getStatus(), HttpStatus.SC_UNPROCESSABLE_ENTITY);
		
//		JSONObject obj = new JSONObject(result);
//		Assert.assertEquals(obj.get("status"), "invalid_data");
	}
	
	@Test
	public void doIVKeyTypetRemoveClientKey() throws ServletException, IOException, JSONException {
		
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setRequestURI("/platform/clientKeys/" + clientKey);
		request.setMethod("DELETE");
		request.setQueryString("secret=" + secret + "&key_type=S");
		request.setAttribute("businessPlatformKey", businessPlatformKey);
		
		MockHttpServletResponse response = new MockHttpServletResponse();
		
		dispatcherServlet.service(request, response);
		
		LOG.info("status->" + response.getStatus());
		
		String result = response.getContentAsString();
		LOG.info("result->" + result);
		
		Assert.assertEquals(response.getStatus(), HttpStatus.SC_UNPROCESSABLE_ENTITY);
		
//		JSONObject obj = new JSONObject(result);
//		Assert.assertEquals(obj.get("status"), "invalid_data");
	}
	
	public void doSuccessRemoveClientKey() throws ServletException, IOException, JSONException {
		
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setRequestURI("/platform/clientKeys/" + clientKey);
		request.setMethod("DELETE");
		request.setQueryString("secret=" + secret + "&key_type=P");
		request.setAttribute("businessPlatformKey", businessPlatformKey);
		
		MockHttpServletResponse response = new MockHttpServletResponse();
		
		dispatcherServlet.service(request, response);
		
		LOG.info("status->" + response.getStatus());
		
		String result = response.getContentAsString();
		LOG.info("result->" + result);
		
		Assert.assertEquals(response.getStatus(), HttpStatus.SC_OK);
	}
	
	/* Remove Client Key Test (End) */
	
}
