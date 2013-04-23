package com.kthcorp.radix.web;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;

import junit.framework.Assert;

import org.apache.http.HttpStatus;
import org.json.JSONException;
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
public class PlatformAPICommonTest extends AbstractJUnit4SpringContextTests {
	
	private static final Logger LOG = UuidViewableLoggerFactory.getLogger(PlatformAPICommonTest.class);
	
	@Autowired
	private DispatcherServlet dispatcherServlet;
	
	private String businessPlatformKey = "59ffa6f4-0901-4ffc-82ad-44687540ab4b";
	
	/* Common Test */
	@Test
	public void doWithoutResource() throws ServletException, IOException, JSONException {
		
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setRequestURI("/platform/");
		request.setMethod("POST");
		request.setContentType("application/x-www-form-urlencoded");
		request.setAttribute("businessPlatformKey", businessPlatformKey);
		
		Map<String, String> map = new HashMap<String, String>();
		map.put("test", "test");
		
		request.addParameters(map);
		
		MockHttpServletResponse response = new MockHttpServletResponse();
		
		dispatcherServlet.service(request, response);
		
		LOG.info("status->" + response.getStatus());
		
		String result = response.getContentAsString();
		LOG.info("result->" + result);
		
		// JSONObject obj = new JSONObject(result);
		
		Assert.assertEquals(HttpStatus.SC_NOT_FOUND, response.getStatus());
		// Assert.assertEquals(obj.get("status"), "uri_incorrect");
	}
	
	@Test
	public void doNoProcessor() throws ServletException, IOException, JSONException {
		
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setRequestURI("/platform/businessPlatform2");
		request.setMethod("GET");
		request.setContentType("application/x-www-form-urlencoded");
		request.setAttribute("businessPlatformKey", businessPlatformKey);
		
		Map<String, String> map = new HashMap<String, String>();
		map.put("test", "test");
		
		request.addParameters(map);
		
		MockHttpServletResponse response = new MockHttpServletResponse();
		
		dispatcherServlet.service(request, response);
		
		LOG.info("status->" + response.getStatus());
		
		String result = response.getContentAsString();
		LOG.info("result->" + result);
		
		// JSONObject obj = new JSONObject(result);
		
		Assert.assertEquals(HttpStatus.SC_NOT_FOUND, response.getStatus());
		// Assert.assertEquals(obj.get("status"), "not_found_processor");
	}
	
	@Test
	public void doNotImplementedMethod() throws ServletException, IOException, JSONException {
		
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setRequestURI("/platform/clientKeys");
		request.setMethod("GET");
		request.setContentType("application/x-www-form-urlencoded");
		request.setAttribute("businessPlatformKey", businessPlatformKey);
		
		Map<String, String> map = new HashMap<String, String>();
		map.put("test", "test");
		
		request.addParameters(map);
		
		MockHttpServletResponse response = new MockHttpServletResponse();
		
		dispatcherServlet.service(request, response);
		
		LOG.info("status->" + response.getStatus());
		
		String result = response.getContentAsString();
		LOG.info("result->" + result);
		
		// JSONObject obj = new JSONObject(result);
		
		Assert.assertEquals(response.getStatus(), HttpStatus.SC_METHOD_FAILURE);
		// Assert.assertEquals(obj.get("status"), "method_not_implemented");
	}
	
	@Test
	public void doNoBusinessPlatformKey() throws ServletException, IOException, JSONException {
		
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setRequestURI("/platform/packages");
		request.setMethod("GET");
		request.setContentType("application/x-www-form-urlencoded");
		
		Map<String, String> map = new HashMap<String, String>();
		map.put("test", "test");
		
		request.addParameters(map);
		
		MockHttpServletResponse response = new MockHttpServletResponse();
		
		dispatcherServlet.service(request, response);
		
		LOG.info("status->" + response.getStatus());
		
		String result = response.getContentAsString();
		LOG.info("result->" + result);
		
		// JSONObject obj = new JSONObject(result);
		
		Assert.assertEquals(response.getStatus(), HttpStatus.SC_PRECONDITION_FAILED);
		// Assert.assertEquals(obj.get("status"), "does_not_found_businessPlatformKey");
	}
	
	@Test
	public void doIVBusinessPlatformKey() throws ServletException, IOException, JSONException {
		
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setRequestURI("/platform/packages");
		request.setMethod("GET");
		request.setContentType("application/x-www-form-urlencoded");
		request.setAttribute("businessPlatformKey", businessPlatformKey + "1");
		
		Map<String, String> map = new HashMap<String, String>();
		map.put("test", "test");
		
		request.addParameters(map);
		
		MockHttpServletResponse response = new MockHttpServletResponse();
		
		dispatcherServlet.service(request, response);
		
		LOG.info("status->" + response.getStatus());
		
		String result = response.getContentAsString();
		LOG.info("result->" + result);
		
		// JSONObject obj = new JSONObject(result);
		
		Assert.assertEquals(HttpStatus.SC_PRECONDITION_FAILED, response.getStatus());
		// Assert.assertEquals(obj.get("status"), "invalid_businessPlatformKey");
	}
	
	@Test
	public void doIVBusinessPlatformKey2() throws ServletException, IOException, JSONException {
		
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setRequestURI("/platform/packages");
		request.setMethod("GET");
		request.setContentType("application/x-www-form-urlencoded");
		request.setAttribute("businessPlatformKey", "95aff93e-a15e-4836-928b-2065de2bd6e0");
		
		Map<String, String> map = new HashMap<String, String>();
		map.put("test", "test");
		
		request.addParameters(map);
		
		MockHttpServletResponse response = new MockHttpServletResponse();
		
		dispatcherServlet.service(request, response);
		
		LOG.info("status->" + response.getStatus());
		
		String result = response.getContentAsString();
		LOG.info("result->" + result);
		
		// JSONObject obj = new JSONObject(result);
		
		Assert.assertEquals(response.getStatus(), HttpStatus.SC_PRECONDITION_FAILED);
		// Assert.assertEquals(obj.get("status"), "invalid_businessPlatformKey");
	}
	
	@Test
	public void doIVContentType() throws ServletException, IOException, JSONException {
		
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setRequestURI("/platform/clientKeys");
		request.setMethod("POST");
		request.setContentType("application/x-www-form-urlencoded2");
		request.setAttribute("businessPlatformKey", businessPlatformKey);
		
		Map<String, String> map = new HashMap<String, String>();
		map.put("test", "test");
		
		request.addParameters(map);
		
		MockHttpServletResponse response = new MockHttpServletResponse();
		
		dispatcherServlet.service(request, response);
		
		LOG.info("status->" + response.getStatus());
		
		String result = response.getContentAsString();
		LOG.info("result->" + result);
		
		// JSONObject obj = new JSONObject(result);
		
		Assert.assertEquals(response.getStatus(), HttpStatus.SC_UNPROCESSABLE_ENTITY);
		// Assert.assertEquals(obj.get("status"), "not_supported_contentType");
	}
	
}
