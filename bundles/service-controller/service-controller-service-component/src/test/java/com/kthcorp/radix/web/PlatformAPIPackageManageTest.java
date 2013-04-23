package com.kthcorp.radix.web;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;

import junit.framework.Assert;

import org.apache.http.HttpStatus;
import org.json.JSONArray;
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
public class PlatformAPIPackageManageTest extends AbstractJUnit4SpringContextTests {
	
	private static final Logger LOG = UuidViewableLoggerFactory.getLogger(PlatformAPIPackageManageTest.class);
	
	@Autowired
	private DispatcherServlet dispatcherServlet;
	
	private String businessPlatformKey = "59ffa6f4-0901-4ffc-82ad-44687540ab4b";
	
	@SuppressWarnings("unused")
	private String policyID1, policyID2, policyID3, policyID4, policyID5;
	
	/* Create Package Test(Start) */
	
	@Test
	public void doNFPartnerIDCreatePackage() throws ServletException, IOException, JSONException {
		
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setRequestURI("/platform/packages");
		request.setMethod("POST");
		request.setContentType("application/x-www-form-urlencoded");
		request.setAttribute("businessPlatformKey", businessPlatformKey);
		
		String packageDescription = getPackageDescription("package_v0.3.xml");
		
		Map<String, String> map = new HashMap<String, String>();
		map.put("package_description", packageDescription);
		
		request.addParameters(map);
		
		MockHttpServletResponse response = new MockHttpServletResponse();
		
		dispatcherServlet.service(request, response);
		
		LOG.info("status->" + response.getStatus());
		
		String result = response.getContentAsString();
		LOG.info("result->" + result);
		
		// JSONObject obj = new JSONObject(result);
		
		Assert.assertEquals(response.getStatus(), HttpStatus.SC_UNPROCESSABLE_ENTITY);
		
		// Assert.assertEquals(obj.get("status"), "invalid_data");
	}
	
	@Test
	public void doIVPartnerIDCreatePackage() throws ServletException, IOException, JSONException {
		
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setRequestURI("/platform/packages");
		request.setMethod("POST");
		request.setContentType("application/x-www-form-urlencoded");
		request.setAttribute("businessPlatformKey", businessPlatformKey);
		
		String packageDescription = getPackageDescription("package_v0.3.xml");
		
		Map<String, String> map = new HashMap<String, String>();
		map.put("partner_id", "11111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111");
		map.put("package_description", packageDescription);
		
		request.addParameters(map);
		
		MockHttpServletResponse response = new MockHttpServletResponse();
		
		dispatcherServlet.service(request, response);
		
		LOG.info("status->" + response.getStatus());
		
		String result = response.getContentAsString();
		LOG.info("result->" + result);
		
		// JSONObject obj = new JSONObject(result);
		
		Assert.assertEquals(response.getStatus(), HttpStatus.SC_UNPROCESSABLE_ENTITY);
		
		// Assert.assertEquals(obj.get("status"), "invalid_data");
	}
	
	@Test
	public void doNoAPIPartnerIDCreatePackage() throws ServletException, IOException, JSONException {
		
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setRequestURI("/platform/packages");
		request.setMethod("POST");
		request.setContentType("application/x-www-form-urlencoded");
		request.setAttribute("businessPlatformKey", businessPlatformKey);
		
		String packageDescriptionOrg = getPackageDescription("package_v0.3.xml");
		String packageDescription = packageDescriptionOrg.replace("package1", "package" + System.currentTimeMillis());
		packageDescription = packageDescription.replace("apis", "no");
		
		Map<String, String> map = new HashMap<String, String>();
		map.put("partner_id", "testPartnerAPI1");
		map.put("package_description", packageDescription);
		
		request.addParameters(map);
		
		MockHttpServletResponse response = new MockHttpServletResponse();
		
		dispatcherServlet.service(request, response);
		
		LOG.info("status->" + response.getStatus());
		
		String result = response.getContentAsString();
		LOG.info("result->" + result);
		
		// JSONObject obj = new JSONObject(result);
		
		Assert.assertEquals(response.getStatus(), HttpStatus.SC_UNPROCESSABLE_ENTITY);
		
		// Assert.assertEquals(obj.get("status"), "invalid_data");
	}
	
	@Test
	public void doIVDescriptionPartnerIDCreatePackage() throws ServletException, IOException, JSONException {
		
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setRequestURI("/platform/packages");
		request.setMethod("POST");
		request.setContentType("application/x-www-form-urlencoded");
		request.setAttribute("businessPlatformKey", businessPlatformKey);
		
		String packageDescriptionOrg = getPackageDescription("package_v0.3.xml");
		String packageDescription = packageDescriptionOrg.replace("package1", "package" + System.currentTimeMillis());
		packageDescription = packageDescription.replace("apis", "no");
		packageDescription = packageDescription.replace("</package>", "</p>");
		
		Map<String, String> map = new HashMap<String, String>();
		map.put("partner_id", "testPartnerAPI1");
		map.put("package_description", packageDescription);
		
		request.addParameters(map);
		
		MockHttpServletResponse response = new MockHttpServletResponse();
		
		dispatcherServlet.service(request, response);
		
		LOG.info("status->" + response.getStatus());
		
		String result = response.getContentAsString();
		LOG.info("result->" + result);
		
		// JSONObject obj = new JSONObject(result);
		
		Assert.assertEquals(response.getStatus(), HttpStatus.SC_UNPROCESSABLE_ENTITY);
		
		// Assert.assertEquals(obj.get("status"), "invalid_data");
	}
	
	@FailedTest
	public void doSuccessCreatePackage() throws ServletException, IOException, JSONException {
		
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setRequestURI("/platform/packages");
		request.setMethod("POST");
		request.setContentType("application/x-www-form-urlencoded");
		request.setAttribute("businessPlatformKey", businessPlatformKey);
		
		String packageDescriptionOrg = getPackageDescription("package_v0.3.xml");
		String packageDescription = packageDescriptionOrg.replace("package1", "package" + System.currentTimeMillis());
		
		Map<String, String> map = new HashMap<String, String>();
		map.put("partner_id", "testPartner1");
		map.put("package_description", packageDescription);
		
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
		String packageID = body.getString("package_id");
		
		LOG.info("package_id->" + packageID);
		
		this.doSuccessGetPackage(packageID);
//		this.doSuccessGetPackage("1234");	// set packageID=0 for testing GetPackageList
		this.doSuccessModifyPackage(packageID);
		this.doSuccessRemovePackage(packageID);
	}
	
	public void doSuccessGetPackage(String packageID) throws ServletException, IOException, JSONException
	{
		MockHttpServletRequest request = new MockHttpServletRequest();
		
		if(packageID != null) {
			request.setRequestURI("/platform/packages/" + packageID);
		} else {
			request.setRequestURI("/platform/packages/");
		}	
		
		request.setMethod("GET");
		request.setContentType("application/x-www-form-urlencoded");
		request.setAttribute("businessPlatformKey", businessPlatformKey);
		Map<String, String> map = new HashMap<String, String>();
		map.put("partner_id", "testPartner1");
		//map.put("partner_id", "test_partnerid_1334047471901");
		//map.put("partner_id", "test_partnerid_1334047471900");
		request.addParameters(map);
		
		MockHttpServletResponse response = new MockHttpServletResponse();
		dispatcherServlet.service(request, response);
		
		LOG.info("status->" + response.getStatus());
		
		String result = response.getContentAsString();
		LOG.info("result->" + result);
		
		Assert.assertEquals(response.getStatus(), HttpStatus.SC_OK);
		
		JSONObject obj = new JSONObject(result);
		JSONArray packagePolicyList = obj.getJSONObject("body").getJSONObject("package").getJSONArray("policyList");
		JSONArray serviceAPIList = obj.getJSONObject("body").getJSONObject("package").getJSONArray("serviceAPIList");
		for(int i=0,l=serviceAPIList.length();i<l;i++) {
			JSONArray policyList = serviceAPIList.getJSONObject(i).getJSONArray("policyList");
			for(int j=0,lj=policyList.length();j<lj;j++) {
				JSONObject policy = policyList.getJSONObject(j);
				if(i==0) {
					if(j==0) {
						this.policyID1 = policy.getString("id");
					}
					if(policy.get("policyTypeID").equals("usageTerm")) {
						this.policyID2 = policy.getString("id");
					}
				}
				if(i==1) {
					if(j==0) {
						this.policyID3 = policy.getString("id");
					}
				}
			}
		}
		for(int j=0,lj=packagePolicyList.length();j<lj;j++) {
			JSONObject policy = packagePolicyList.getJSONObject(j);
			if(policy.get("policyTypeID").equals("usageTerm")) {
				this.policyID4 = policy.getString("id");
			}
		}
		LOG.info("Setted PolicyID");
		LOG.info("\tpolicyID1->"+this.policyID1);
		LOG.info("\tpolicyID2->"+this.policyID2);
		LOG.info("\tpolicyID3->"+this.policyID3);
		LOG.info("\tpolicyID4->"+this.policyID4);
	}
	
	public void doSuccessModifyPackage(String packageID) throws ServletException, IOException, JSONException {
		
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setRequestURI("/platform/packages/" + packageID);
		request.setMethod("PUT");
		request.setContentType("application/x-www-form-urlencoded");
		request.setAttribute("businessPlatformKey", businessPlatformKey);
		
		String packageDescriptionOrg = getPackageDescription("package_v0.3U.xml");
		
		String packageDescription = packageDescriptionOrg.replace("package1", "package" + System.currentTimeMillis());
		packageDescription = packageDescription.replace("policyID1", this.policyID1);
		packageDescription = packageDescription.replace("policyID2", this.policyID2);
		packageDescription = packageDescription.replace("policyID3", this.policyID3);
		packageDescription = packageDescription.replace("policyID4", this.policyID4);
		
		Map<String, String> map = new HashMap<String, String>();
		map.put("partner_id", "testPartner1");
		map.put("package_description", packageDescription);
		map.put("forceToDelete", "true");
		
		request.addParameters(map);
		
		MockHttpServletResponse response = new MockHttpServletResponse();
		
		dispatcherServlet.service(request, response);
		
		LOG.info("status->" + response.getStatus());
		
		String result = response.getContentAsString();
		LOG.info("result->" + result);
		
		Assert.assertEquals(response.getStatus(), HttpStatus.SC_OK);
	}
	
	/* Remove Service Test (Start) */
	public void doSuccessRemovePackage(String packageID) throws ServletException, IOException, JSONException {
		
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setRequestURI("/platform/packages/" + packageID);
		request.setMethod("DELETE");
		request.setQueryString("partner_id=testPartner1&forceToDelete=true");
		request.setAttribute("businessPlatformKey", businessPlatformKey);
		
		MockHttpServletResponse response = new MockHttpServletResponse();
		
		dispatcherServlet.service(request, response);
		
		LOG.info("status->" + response.getStatus());
		
		String result = response.getContentAsString();
		LOG.info("result->" + result);
		
		Assert.assertEquals(response.getStatus(), HttpStatus.SC_OK);
	}
	
	/* Remove Service Test (End) */
	
	private String getPackageDescription(String fileResource) throws IOException {
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
