package com.kthcorp.radix.web;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
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

import com.kthcorp.radix.util.FailedTest;
import com.kthcorp.radix.web.mock.MockWebApplication;
import com.kthcorp.radix.web.mock.MockWebApplicationContextLoader;

@RunWith(MysqlEmbeddableSpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/META-INF/spring/spring-all-context-test.xml" }, loader = MockWebApplicationContextLoader.class)
@MockWebApplication(name = "radixtest")
public class PlatformAPIClientPackageManageTest extends AbstractJUnit4SpringContextTests {
	
	private static final Logger LOG = UuidViewableLoggerFactory.getLogger(PlatformAPIClientPackageManageTest.class);
	
	@Autowired
	private DispatcherServlet dispatcherServlet;
	
	private String businessPlatformKey = "59ffa6f4-0901-4ffc-82ad-44687540ab4b";
	private String clientKey = "34d15b7e-ee1d-419f-9fc0-be22120df363";
	
	/* Create Client Key Test(Start) */
	
	@Test
	public void doNFClientKeyOnClientPackage() throws ServletException, IOException, JSONException {
		
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setRequestURI("/platform/clientKeys/package");
		request.setMethod("POST");
		request.setContentType("application/x-www-form-urlencoded");
		request.setAttribute("businessPlatformKey", businessPlatformKey);
		
		String clientPackageDescription = this.getClientPackageDescription("clientPackage_v0.3.xml");
		Map<String, String> map = new HashMap<String, String>();
		map.put("package_info", clientPackageDescription);
		
		request.addParameters(map);
		
		MockHttpServletResponse response = new MockHttpServletResponse();
		
		dispatcherServlet.service(request, response);
		
		LOG.info("status->" + response.getStatus());
		
		String result = response.getContentAsString();
		LOG.info("result->" + result);
		
		Assert.assertEquals(response.getStatus(), HttpStatus.SC_UNPROCESSABLE_ENTITY);
		
		JSONObject obj = new JSONObject(result);
		Assert.assertEquals(obj.get("status"), "invalid_data");
	}
	
	@Test
	public void doIVClientKeyOnClientPackage() throws ServletException, IOException, JSONException {
		
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setRequestURI("/platform/clientKeys/tesfsdgsdg/package");
		request.setMethod("POST");
		request.setContentType("application/x-www-form-urlencoded");
		request.setAttribute("businessPlatformKey", businessPlatformKey);
		
		String clientPackageDescription = this.getClientPackageDescription("clientPackage_v0.3.xml");
		Map<String, String> map = new HashMap<String, String>();
		map.put("package_info", clientPackageDescription);
		
		request.addParameters(map);
		
		MockHttpServletResponse response = new MockHttpServletResponse();
		
		dispatcherServlet.service(request, response);
		LOG.info("status->" + response.getStatus());
		
		String result = response.getContentAsString();
		LOG.info("result->" + result);
		
		Assert.assertEquals(response.getStatus(), HttpStatus.SC_UNPROCESSABLE_ENTITY);
		
		JSONObject obj = new JSONObject(result);
		Assert.assertEquals(obj.get("status"), "invalid_data");
	}
	
	@Test
	public void doNFPackageInfoOnClientPackage() throws ServletException, IOException, JSONException {
		
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setRequestURI("/platform/clientKeys/"+clientKey+"/package");
		request.setMethod("POST");
		request.setContentType("application/x-www-form-urlencoded");
		request.setAttribute("businessPlatformKey", businessPlatformKey);
		
		MockHttpServletResponse response = new MockHttpServletResponse();
		
		dispatcherServlet.service(request, response);
		LOG.info("status->" + response.getStatus());
		
		String result = response.getContentAsString();
		LOG.info("result->" + result);
		
		Assert.assertEquals(response.getStatus(), HttpStatus.SC_UNPROCESSABLE_ENTITY);
		
		JSONObject obj = new JSONObject(result);
		Assert.assertEquals(obj.get("status"), "invalid_data");
		
	}
	
	@Test
	public void doIVPackageInfoOnClientPackage() throws ServletException, IOException, JSONException {
		
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setRequestURI("/platform/clientKeys/"+clientKey+"/package");
		request.setMethod("POST");
		request.setContentType("application/x-www-form-urlencoded");
		request.setAttribute("businessPlatformKey", businessPlatformKey);
		
		String clientPackageDescription = this.getClientPackageDescription("clientPackage_v0.3.xml");
		clientPackageDescription = clientPackageDescription.replace("</packages>", "</>");
		Map<String, String> map = new HashMap<String, String>();
		map.put("package_info", clientPackageDescription);
		
		request.addParameters(map);
		
		MockHttpServletResponse response = new MockHttpServletResponse();
		
		dispatcherServlet.service(request, response);
		LOG.info("status->" + response.getStatus());
		
		String result = response.getContentAsString();
		LOG.info("result->" + result);
		
		Assert.assertEquals(response.getStatus(), HttpStatus.SC_UNPROCESSABLE_ENTITY);
		
		JSONObject obj = new JSONObject(result);
		Assert.assertEquals(obj.get("status"), "invalid_data");
		
	}

	@FailedTest
	public void doSuccessCreateClientPackage() throws ServletException, IOException, JSONException {
		
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setRequestURI("/platform/clientKeys/"+clientKey+"/package");
		request.setMethod("POST");
		request.setContentType("application/x-www-form-urlencoded");
		request.setAttribute("businessPlatformKey", businessPlatformKey);
		
		String clientPackageDescription = this.getClientPackageDescription("clientPackage_v0.3.xml");
		Map<String, String> map = new HashMap<String, String>();
		map.put("package_info", clientPackageDescription);
		
		request.addParameters(map);
		
		MockHttpServletResponse response = new MockHttpServletResponse();
		
		dispatcherServlet.service(request, response);
		LOG.info("status->" + response.getStatus());
		
		String result = response.getContentAsString();
		LOG.info("result->" + result);

		Assert.assertEquals(response.getStatus(), HttpStatus.SC_CREATED);
		
		JSONObject obj = new JSONObject(result);
		Assert.assertEquals(obj.get("status"), "success");
		
		this.doSuccessModifyClientPackage();
		this.doSuccessRemoveClientPackage();
	}
	
	/* Modify Client Key Test (start) */
	public void doSuccessModifyClientPackage() throws ServletException, IOException, JSONException {
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setRequestURI("/platform/clientKeys/"+clientKey+"/package");
		request.setMethod("PUT");
		request.setContentType("application/x-www-form-urlencoded");
		request.setAttribute("businessPlatformKey", businessPlatformKey);
		
		String clientPackageDescription = this.getClientPackageDescription("clientPackage_v0.3U.xml");
		Map<String, String> map = new HashMap<String, String>();
		map.put("package_info", clientPackageDescription);
		
		request.addParameters(map);
		
		MockHttpServletResponse response = new MockHttpServletResponse();
		
		dispatcherServlet.service(request, response);
		LOG.info("status->" + response.getStatus());
		
		String result = response.getContentAsString();
		LOG.info("result->" + result);

		Assert.assertEquals(response.getStatus(), HttpStatus.SC_OK);
		
		JSONObject obj = new JSONObject(result);
		Assert.assertEquals(obj.get("status"), "success");
	}
	
	/* Remove Client Key Test (Start) */
	
	@Test
	public void doNFClientKeyRemoveClientPackage() throws ServletException, IOException, JSONException {
		
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setRequestURI("/platform/clientKeys/package");
		request.setMethod("DELETE");
		request.setAttribute("businessPlatformKey", businessPlatformKey);
		
		MockHttpServletResponse response = new MockHttpServletResponse();
		
		dispatcherServlet.service(request, response);
		
		LOG.info("status->" + response.getStatus());
		
		String result = response.getContentAsString();
		LOG.info("result->" + result);
		
		Assert.assertEquals(response.getStatus(), HttpStatus.SC_UNPROCESSABLE_ENTITY);
		
		JSONObject obj = new JSONObject(result);
		Assert.assertEquals(obj.get("status"), "invalid_data");
	}

	
	@Test
	public void doIVClientKeyRemoveClientPackage() throws ServletException, IOException, JSONException {
		
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setRequestURI("/platform/clientKeys/324234234/package");
		request.setMethod("DELETE");
		request.setAttribute("businessPlatformKey", businessPlatformKey);
		
		MockHttpServletResponse response = new MockHttpServletResponse();
		
		dispatcherServlet.service(request, response);
		
		LOG.info("status->" + response.getStatus());
		
		String result = response.getContentAsString();
		LOG.info("result->" + result);
		
		Assert.assertEquals(response.getStatus(), HttpStatus.SC_UNPROCESSABLE_ENTITY);
		
		JSONObject obj = new JSONObject(result);
		Assert.assertEquals(obj.get("status"), "invalid_data");
	}
	
	public void doSuccessRemoveClientPackage() throws ServletException, IOException, JSONException {
		
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setRequestURI("/platform/clientKeys/" + clientKey+"/package");
		request.setMethod("DELETE");
		request.setAttribute("businessPlatformKey", businessPlatformKey);
		
		Map<String, String> map = new HashMap<String, String>();
		map.put("forceToDelete", "true");
		request.addParameters(map);
		
		MockHttpServletResponse response = new MockHttpServletResponse();
		
		dispatcherServlet.service(request, response);
		
		LOG.info("status->" + response.getStatus());
		
		String result = response.getContentAsString();
		LOG.info("result->" + result);
		
		Assert.assertEquals(response.getStatus(), HttpStatus.SC_OK);
	}
	
	private String getClientPackageDescription(String fileResource) throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(Thread.currentThread().getContextClassLoader().getResourceAsStream(fileResource)));
		String packageDescription = null, line = null;
		
		while((line = reader.readLine()) != null) {
			if(packageDescription == null) {
				packageDescription = line;
			} else {
				packageDescription = packageDescription + line;
			}
		}
		
		return packageDescription;
	}
}
