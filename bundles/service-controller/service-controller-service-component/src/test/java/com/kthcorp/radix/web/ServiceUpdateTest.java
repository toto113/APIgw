package com.kthcorp.radix.web;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;

import org.apache.http.HttpStatus;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
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
public class ServiceUpdateTest extends AbstractJUnit4SpringContextTests {

	private static final Logger LOG = UuidViewableLoggerFactory.getLogger(ServiceUpdateTest.class);

	private String businessPlatformKey = "59ffa6f4-0901-4ffc-82ad-44687540ab4b";

	private String serviceID = "2341ed57-8772-11e1-a8c6-f0def154de37";

	@Autowired
	private DispatcherServlet dispatcherServlet;

	@Rule
	public ExpectedException exception = ExpectedException.none();

	//@Before
	@Ignore
	public void createTestService() throws ServletException, IOException, JSONException {

		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setRequestURI("/platform/services");
		request.setMethod("POST");
		request.setContentType("application/x-www-form-urlencoded");
		request.setAttribute("businessPlatformKey", businessPlatformKey);

		Map<String, String> map = new HashMap<String, String>();
		map.put("partner_id", "justTestPartner");
		map.put("service_description", getDescriptionFromXml("ServiceDescriptionTestForInsert.xml"));

		request.addParameters(map);

		MockHttpServletResponse response = new MockHttpServletResponse();

		dispatcherServlet.service(request, response);
		LOG.info(">>>>> status of createService in ServiceUpdateTest ->" + response.getStatus());

		String result = response.getContentAsString();
		LOG.info(">>>>> result  of createService in ServiceUpdateTest ->" + result);

		Assert.assertEquals(response.getStatus(), HttpStatus.SC_CREATED);

		JSONObject obj = new JSONObject(result);
		Assert.assertEquals(obj.get("status"), "success");
		Assert.assertEquals(obj.get("body_type"), "MAP");

		JSONObject body = obj.getJSONObject("body");
		serviceID = body.getString("service_id");
	}

	//@After
	@Ignore
	public void removeTestService() throws ServletException, IOException {

		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setRequestURI("/platform/services/" + serviceID);
		request.setMethod("DELETE");
		request.setQueryString("partner_id=justTestPartner");
		request.setAttribute("businessPlatformKey", businessPlatformKey);

		MockHttpServletResponse response = new MockHttpServletResponse();

		dispatcherServlet.service(request, response);
		LOG.info("<<<<< status of removeTestService in ServiceUpdateTest ->" + response.getStatus());

		String result = response.getContentAsString();
		LOG.info("<<<<< result of removeTestService in ServiceUpdateTest ->" + result);

		Assert.assertEquals( HttpStatus.SC_OK, response.getStatus());
	}



	//	@Test
	// 잘못 들어간 service 데이타를 삭제하기 위한 메소드.
	// 테스트를 위한 메소드가 아니다.
	public void deleteService() throws ServletException, IOException {

		// DB에는 id들이 binary로 들어 있다.
		// 실제 String 값은 select bintouuid(id) from radix_service_apis_serviceapi으로 볼수 있다.
		// 아래의 serviceId들은 이미 실행되어 삭제된 것들이다.
		String[] serviceIDs = { 
				"029070C2-A581-11E1-962B-70F3954F8A40",
				"166494B5-A581-11E1-8733-70F3954F8A40",
				"1CD15987-A589-11E1-9B01-70F3954F8A40",
				"393F2D0F-A56F-11E1-B7A9-70F3954F8A40",
				"8251A784-A588-11E1-8C7C-70F3954F8A40",
				"BBA5B410-A56E-11E1-9CA9-70F3954F8A40",
				"D3363F4F-A580-11E1-B526-70F3954F8A40",
				"DC37A00A-A582-11E1-9517-70F3954F8A40",
				"DF5573CF-A588-11E1-B726-70F3954F8A40",
				"F1456885-8942-11E1-8BF0-F0DEF154CF31",
				"FE0E0A36-A582-11E1-AB32-70F3954F8A40"
		};
		for(String serviceID : serviceIDs) {
			deleteService(serviceID, serviceID);
		}
	}

	public void deleteService(String partnerID, String serviceID) throws ServletException, IOException {

		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setRequestURI("/platform/services/" + serviceID);
		request.setMethod("DELETE");
		request.setQueryString("partner_id="+partnerID);
		request.setAttribute("businessPlatformKey", businessPlatformKey);

		MockHttpServletResponse response = new MockHttpServletResponse();

		dispatcherServlet.service(request, response);
		LOG.info("<<<<< status of removeTestService in ServiceUpdateTest ->" + response.getStatus());

		String result = response.getContentAsString();
		LOG.info("<<<<< result of removeTestService in ServiceUpdateTest ->" + result);

		Assert.assertEquals( HttpStatus.SC_OK, response.getStatus());

	}


	@Test
	public void nullTest() { }
	
	@FailedTest
	public void 서비스_업데이트_확인() throws Exception {

		MockHttpServletRequest request = new MockHttpServletRequest();

		Map<String, String> map = new HashMap<String, String>();
		map.put("partner_id", "justTestPartner");
		map.put("service_description", getDescriptionFromXml("ServiceDescriptionTestForUpdate.xml"));

		request.addHeader("Content-Type", "application/x-www-form-urlencoded");
		request.setRequestURI("/platform/services/"+serviceID);
		request.setMethod("PUT");
		request.setAttribute("businessPlatformKey", "59ffa6f4-0901-4ffc-82ad-44687540ab4b");

		request.setParameters(map);

		MockHttpServletResponse response = new MockHttpServletResponse();

		dispatcherServlet.service(request, response);

		Assert.assertEquals(HttpStatus.SC_ACCEPTED, response.getStatus());
	}

	public String getDescriptionFromXml(String xmlFileName) {
		StringBuffer sb = new StringBuffer();

		try {
			InputStreamReader isr = new InputStreamReader(Thread.currentThread().getContextClassLoader().getResource(xmlFileName).openStream(), "UTF-8");
			BufferedReader br = new BufferedReader(isr);
			String line = null;

			while((line = br.readLine()) != null) {
				sb.append(line);
			}
		} catch(IOException e) {
			e.printStackTrace();
			Assert.assertTrue(true);
		}

		return sb.toString();
	}

}
