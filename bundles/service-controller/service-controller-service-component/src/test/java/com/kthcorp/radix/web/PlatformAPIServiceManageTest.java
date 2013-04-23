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
import org.junit.Ignore;
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
import com.kthcorp.radix.util.test.TestResourceFinder;
import com.kthcorp.radix.web.mock.MockWebApplication;
import com.kthcorp.radix.web.mock.MockWebApplicationContextLoader;

@RunWith(MysqlEmbeddableSpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/META-INF/spring/spring-all-context-test.xml" }, loader = MockWebApplicationContextLoader.class)
@MockWebApplication(name = "radixtest")
public class PlatformAPIServiceManageTest extends AbstractJUnit4SpringContextTests {

	private static final Logger LOG = UuidViewableLoggerFactory.getLogger(PlatformAPIPackageManageTest.class);

	@Autowired
	private DispatcherServlet dispatcherServlet;

	private final String BUSINESS_PLATFORM_KEY = "59ffa6f4-0901-4ffc-82ad-44687540ab4b";
	private final String PARTNER_ID = "testPartner1";

	/* Create Service Test(Start) */

	@Test
	public void doNFPartnerIDCreateService() throws ServletException, IOException, JSONException {

		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setRequestURI("/platform/services");
		request.setMethod("POST");
		request.setContentType("application/x-www-form-urlencoded");
		request.setAttribute("businessPlatformKey", BUSINESS_PLATFORM_KEY);

		String serviceDescription = getServiceDescription();


		Map<String, String> parameterMap = new HashMap<String, String>();
		parameterMap.put("service_description", serviceDescription);

		request.addParameters(parameterMap);

		MockHttpServletResponse response = new MockHttpServletResponse();

		dispatcherServlet.service(request, response);

		System.out.println("response.status->" + response.getStatus());

		String result = response.getContentAsString();
		System.out.println("response.result->" + result);

		//		JSONObject obj = new JSONObject(result);

		Assert.assertEquals(HttpStatus.SC_UNPROCESSABLE_ENTITY, response.getStatus());

		//		Assert.assertEquals(obj.get("status"), "invalid_data");
	}

	@Test
	public void doIVPartnerIDCreateService() throws ServletException, IOException, JSONException {

		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setRequestURI("/platform/services");
		request.setMethod("POST");
		request.setContentType("application/x-www-form-urlencoded");
		request.setAttribute("businessPlatformKey", BUSINESS_PLATFORM_KEY);

		String serviceDescription = getServiceDescription();

		Map<String, String> map = new HashMap<String, String>();
		map.put("partner_id", "11111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111");
		map.put("service_description", serviceDescription);

		request.addParameters(map);

		MockHttpServletResponse response = new MockHttpServletResponse();

		dispatcherServlet.service(request, response);

		LOG.info("status->" + response.getStatus());

		String result = response.getContentAsString();
		LOG.info("result->" + result);

		//		JSONObject obj = new JSONObject(result);

		Assert.assertEquals(HttpStatus.SC_UNPROCESSABLE_ENTITY, response.getStatus());

		//		Assert.assertEquals(obj.get("status"), "invalid_data");
	}

	@Test
	public void doNoAPIPartnerIDCreateService() throws ServletException, IOException, JSONException {

		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setRequestURI("/platform/services");
		request.setMethod("POST");
		request.setContentType("application/x-www-form-urlencoded");
		request.setAttribute("businessPlatformKey", BUSINESS_PLATFORM_KEY);

		String serviceDescriptionOrg = getServiceDescription();
		String serviceDescription = serviceDescriptionOrg.replace("TourInfo1", "Tour" + System.currentTimeMillis());
		serviceDescription = serviceDescription.replace("api-list", "no-list");

		Map<String, String> map = new HashMap<String, String>();
		map.put("partner_id", "NOT_EXISTING_PARTNER_ID");
		map.put("service_description", serviceDescription);

		request.addParameters(map);

		MockHttpServletResponse response = new MockHttpServletResponse();

		dispatcherServlet.service(request, response);

		LOG.info("status->" + response.getStatus());

		String result = response.getContentAsString();
		LOG.info("result->" + result);

		//		JSONObject obj = new JSONObject(result);

		Assert.assertEquals(HttpStatus.SC_UNPROCESSABLE_ENTITY, response.getStatus());

		//		Assert.assertEquals(obj.get("status"), "invalid_data");
	}

	@FailedTest
	public void doIVDescriptionPartnerIDCreateService() throws ServletException, IOException, JSONException {

		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setRequestURI("/platform/services");
		request.setMethod("POST");
		request.setContentType("application/x-www-form-urlencoded");
		request.setAttribute("businessPlatformKey", BUSINESS_PLATFORM_KEY);

		String serviceDescriptionOrg = getServiceDescription();
		String serviceDescription = serviceDescriptionOrg.replace("TourInfo1", "Tour" + System.currentTimeMillis());
		serviceDescription = serviceDescription.replace("api-list", "no-list");
		serviceDescription = serviceDescription.replace("</Service>", "</s>");

		Map<String, String> map = new HashMap<String, String>();
		map.put("partner_id", "testPartnerAPI1");
		map.put("service_description", serviceDescription);

		request.addParameters(map);

		MockHttpServletResponse response = new MockHttpServletResponse();

		dispatcherServlet.service(request, response);

		LOG.info("status->" + response.getStatus());

		String result = response.getContentAsString();
		LOG.info("result->" + result);

		//		JSONObject obj = new JSONObject(result);

		Assert.assertEquals(HttpStatus.SC_BAD_REQUEST, response.getStatus());

		//		Assert.assertEquals(obj.get("status"), "invalid_data");
	}


	@Ignore // 2012/05/28 현재 이미 서비스가 존재하여 새로 생성하지 못한다.
	public void 파트너_SamHwa로의_서비스_생성_확인() throws IOException, ServletException, JSONException {

		String serviceId = null;
		String partnerId = "samhwa_admin";
		String businessPlatformKey = BUSINESS_PLATFORM_KEY;
		try {
			String serviceDescriptionFileName = TestResourceFinder.getResourceFileNameOnClasspath("samhwa.xml");
			serviceId = TestUtil.createService(dispatcherServlet, partnerId, businessPlatformKey, serviceDescriptionFileName);
		} finally {
			try {
				TestUtil.removeService(dispatcherServlet, partnerId, businessPlatformKey, serviceId);
			} catch(Throwable e) {
				// ignore
			}
		}

	}

	@Test
	public void 서비스_생성_확인() throws ServletException, IOException, JSONException {

		String serviceId = null;
		String parterId = PARTNER_ID;
		String businessPlatformKey = BUSINESS_PLATFORM_KEY;
		try {
			String serviceDescriptionFileName = TestResourceFinder.getResourceFileNameOnClasspath("service_description.xml");
			serviceId = TestUtil.createService(dispatcherServlet, parterId, businessPlatformKey, serviceDescriptionFileName);
		} finally {
			try {
				TestUtil.removeService(dispatcherServlet, parterId, businessPlatformKey, serviceId);
			} catch(Throwable e) {
				// ignore
			}
		}


	}

	public void remove_old_test_record() {

		// next value could be get by executing sql
		// select bintouuid(serviceID) from radix_service_apis_serviceapi where name="getBusLane" or name="getMap"
		String[] serviceIds = {
				"277DEEFA-AA22-11E1-AB1E-70F3954F8A40"
		};
		for(String serviceId : serviceIds) {
			try {
				TestUtil.removeService(dispatcherServlet, PARTNER_ID, BUSINESS_PLATFORM_KEY, serviceId);
			} catch(Throwable e) {
				e.printStackTrace();
			}
		}

	}

	@Test
	public void 서비스_조회_확인() throws IOException, ServletException, JSONException {

		String serviceId = null;
		String partnerId = PARTNER_ID;
		String businessPlatformKey = BUSINESS_PLATFORM_KEY;


		try {
			// 우선 서비스를 하나 생성하자.
			String serviceDescriptionFileName = TestResourceFinder.getResourceFileNameOnClasspath("service_description.xml");
			serviceId = TestUtil.createService(dispatcherServlet, partnerId, businessPlatformKey, serviceDescriptionFileName);

			// 그리고 이제 조회해 보자.
			TestUtil.getService(dispatcherServlet, partnerId, businessPlatformKey, serviceId);
		} finally {
			try {
				TestUtil.removeService(dispatcherServlet, partnerId, businessPlatformKey, serviceId);
			} catch(Throwable e) {
				// ignore
			}
		}

	}


	/* Remove Service Test (End) */
	private String getServiceDescription() throws IOException {
		return this.getServiceDescription("ServiceDescriptionTest.xml");
	}

	private String getServiceDescription(String fileName) throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(Thread.currentThread().getContextClassLoader().getResourceAsStream(fileName)));
		String serviceDescription = null, line = null;

		while((line = reader.readLine()) != null) {
			if(serviceDescription == null) {
				serviceDescription = line;
			} else {
				serviceDescription = serviceDescription + line;
			}
		}

		return serviceDescription;
	}

}
