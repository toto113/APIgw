package com.kthcorp.radix.web;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServlet;

import org.json.JSONException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.servlet.Context;
import org.mortbay.jetty.servlet.ServletHolder;
import org.slf4j.Logger;
import com.kthcorp.radix.util.UuidViewableLoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.ContextConfiguration;
import com.kthcorp.radix.util.test.junit.MysqlEmbeddableSpringJUnit4ClassRunner;
import org.springframework.web.servlet.DispatcherServlet;

import com.kthcorp.radix.dao.mybatis.MyBatisServiceManagerDaoMapper;
import com.kthcorp.radix.domain.exception.RadixException;
import com.kthcorp.radix.domain.service.Service;
import com.kthcorp.radix.util.UUIDUtils;
import com.kthcorp.radix.util.test.TestResourceFinder;
import com.kthcorp.radix.web.mock.MockWebApplication;
import com.kthcorp.radix.web.mock.MockWebApplicationContextLoader;

/*
 * 요 테스트 케이스가 사용하는 Mock 객체는 mock.xml에 정의되어 있다.
 */
@RunWith(MysqlEmbeddableSpringJUnit4ClassRunner.class)
@ContextConfiguration(
		locations = {
				"classpath:/META-INF/spring/spring-all-context-test.xml"
		}
		, loader = MockWebApplicationContextLoader.class
		)
@MockWebApplication(name = "radixtest")
public class UrlPathParameterProcessTest {

	private static final String CLIENT_KEY = "SomeClientKey";
	private static final String USER_NAME = "someUserName";
	
	private static class JettyTestBed {

		private static Server server = null;
		private static final int JETTY_PORT = 3023;

		@SuppressWarnings("serial")
		private static class DummyServlet extends HttpServlet {
			private static String requestedUri = null;
			private static String queryString = null;
			@Override
			public void service(ServletRequest request, ServletResponse response) {
				try {
					org.mortbay.jetty.Request jettyRequest = (org.mortbay.jetty.Request)request;
					requestedUri = jettyRequest.getUri().getPath();
					queryString = jettyRequest.getQueryString();
					response.getWriter().write(requestedUri);
				} catch (IOException e) {
				}
			}
		}

		public static void startJetty() throws Exception {
			server = new Server(JETTY_PORT);
			Context rootContext = new Context(server, "/", Context.SESSIONS);
			rootContext.addServlet(new ServletHolder(new DummyServlet()), "/*");
			server.start();
		}

		public static void stopJetty() throws Exception {
			if(server!=null) {
				server.stop();
			}
		}

	}


	@BeforeClass
	public static void setup() throws Exception {
		JettyTestBed.startJetty();
	}



	@AfterClass
	public static void teardown() throws Exception {
		JettyTestBed.stopJetty();
	}



	private static final Logger logger = UuidViewableLoggerFactory.getLogger(UrlPathParameterProcessTest.class);

	@Autowired
	private DispatcherServlet dispatcherServlet;

	private final String BUSINESS_PLATFORM_KEY = "59ffa6f4-0901-4ffc-82ad-44687540ab4b";
	private final String PARTNER_ID = "testParterId";

	@Autowired
	private ServiceApiController controller;

	@Autowired
	private MyBatisServiceManagerDaoMapper mapper;

	private String serviceId = null;

	@Before
	public void prepareService() throws IOException, ServletException, JSONException {
		removeOldService();
		createService();
	}

	@After
	public void clearService() throws ServletException, IOException {
		removeService();
	}


	private void removeOldService() {

		List<Service> services = mapper.selectAllServiceByPartnerID(PARTNER_ID);
		for(Service service : services) {
			try {
				TestUtil.removeService(dispatcherServlet, PARTNER_ID, BUSINESS_PLATFORM_KEY, UUIDUtils.getString(service.getId()));
			} catch (Throwable e) {
				logger.warn("removing old service failed. service={}", service, e);
			}
		}

	}



	public void createService() throws IOException, ServletException, JSONException {
		String serviceDescriptionFileName = TestResourceFinder.getResourceFileNameOnClasspath("service_description.xml");
		serviceId = TestUtil.createService(dispatcherServlet, PARTNER_ID, BUSINESS_PLATFORM_KEY, serviceDescriptionFileName);
		System.out.println("service created. serviceId="+serviceId);
	}

	private void createServiceAs(List<String> serviceApiUriTemplateList, List<String> partnerApiUriTemplateList) throws Exception {

		Map<String, String> modifyMap = new Hashtable<String, String>();
		String tempDescriptionFileName = "temp_service_description.xml";
		
		if(serviceApiUriTemplateList.size()!=partnerApiUriTemplateList.size()) {
			throw new MyAssertionError("not same count of serviceApiUriTemplate and partnerApiUriTemplateList. serviceApiUriTemplateList="+serviceApiUriTemplateList+", partnerApiUriTemplateList="+partnerApiUriTemplateList);
		}
		String partnerApiHostUrl = "http://localhost:"+JettyTestBed.JETTY_PORT;
		for(int i=0; i<serviceApiUriTemplateList.size(); i++) {
			String willBeReplaced = "serviceApiPathTemplate"+i;
			modifyMap.put(willBeReplaced, serviceApiUriTemplateList.get(i));
			willBeReplaced = "partnerApiPathTemplate"+i;
			modifyMap.put(willBeReplaced, partnerApiHostUrl+partnerApiUriTemplateList.get(i));
		}
		modifyMap.put("PARTNER_HOST_AND_PORT", partnerApiHostUrl);
		
		InnerUtil.copyAndModifyFile("service_description.xml", tempDescriptionFileName, modifyMap);
		tempDescriptionFileName = TestResourceFinder.getResourceFileNameOnClasspath(tempDescriptionFileName);
		serviceId = TestUtil.createService(dispatcherServlet, PARTNER_ID, BUSINESS_PLATFORM_KEY, tempDescriptionFileName);
		System.out.println("created. serviceId="+serviceId);
	}

	public void removeService() throws ServletException, IOException {
		if(serviceId==null) { return; }
		System.out.println("trying removing. serviceId="+serviceId);
		TestUtil.removeService(dispatcherServlet, PARTNER_ID, BUSINESS_PLATFORM_KEY, serviceId);
		serviceId = null;
	}




	private final boolean SUCCESS = true;
	private final boolean FAIL = false;

	private void prepareBaseServices(List<String> serviceApiUriTemplateList, List<String> partnerapiUriTemplateList) throws Exception {
		removeOldService();
		createServiceAs(serviceApiUriTemplateList, partnerapiUriTemplateList);
	}


	private void sendRequest(String requestingUri) throws IOException, RadixException {

		MockHttpServletRequest request = new MockHttpServletRequest();

		String queryString = null;
		if(requestingUri.indexOf("?")!=-1) {
			queryString = requestingUri.substring(requestingUri.indexOf("?")+1);
			requestingUri = requestingUri.substring(0, requestingUri.indexOf("?"));
		}
		
		request = new MockHttpServletRequest();
		request.setMethod("GET");
		request.setRequestURI(requestingUri);

		request.addHeader("Authorization", "Bearer 297b9e64-2d3b-4e2c-a1fb-9a0cb7a59f47");
		request.setAttribute("clientKey", CLIENT_KEY);
		request.setAttribute("userName", USER_NAME);
		request.setQueryString(queryString);
		controller.doService(request);

		ResponseEntity<byte[]> response = controller.doService(request);
		logger.debug("response={}, {}", response, new String(response.getBody()));

		assertEquals("incorrect statusCode", HttpStatus.OK, response.getStatusCode());

	}


	private void doTest(String serviceApiUriTemplate, String partnerapiUriTemplate, String requestingUri, String expectedPartnerUri, boolean expectSucess) {
		
		List<String> serviceApiUriTemplateList = new ArrayList<String>();
		if(serviceApiUriTemplate!=null) {
			serviceApiUriTemplateList.add(serviceApiUriTemplate);
		}
		List<String> partnerApiUriTemplateList = new ArrayList<String>();
		if(partnerapiUriTemplate!=null) {
			partnerApiUriTemplateList.add(partnerapiUriTemplate);
		}
		doTest(serviceApiUriTemplateList, partnerApiUriTemplateList, requestingUri, expectedPartnerUri, expectSucess);

	}
	
	
	private void doTest(List<String> serviceApiUriTemplateList, List<String> partnerapiUriTemplateList, String requestingUri, String expectedPartnerUri, boolean expectSucess) {
		
		try {
			prepareBaseServices(serviceApiUriTemplateList, partnerapiUriTemplateList);
			String fullRequestingUri = "/ServiceForTest/1" + requestingUri;
			sendRequest(fullRequestingUri);
			
			String expectedUriPart = expectedPartnerUri;
			String expectedQueryPart = "";
			if(expectedPartnerUri.indexOf("?")!=-1) {
				expectedUriPart = expectedPartnerUri.substring(0, expectedPartnerUri.indexOf("?"));
				expectedQueryPart = expectedPartnerUri.substring(expectedPartnerUri.indexOf("?")+1);
			}
			assertEquals("incorrect request to partner api", expectedUriPart, JettyTestBed.DummyServlet.requestedUri);
			InnerUtil.assertQueryEquals("incorrect query string to partner api", expectedQueryPart, JettyTestBed.DummyServlet.queryString);
			if(!expectSucess) {
				throw new MyAssertionError("should be failed. requestingUri="+requestingUri+", expectedPartnerUri="+expectedPartnerUri);
			}
		} catch (RuntimeException e) {
			if(expectSucess) { throw new AssertionError(e); }
			else { e.printStackTrace(); }
		} catch (Exception e) {
			if(expectSucess) { throw new AssertionError(e); }
			else { e.printStackTrace(); }
		} catch(MyAssertionError e) {
			fail(e.message);
		} catch (AssertionError e) {
			if(expectSucess) { throw e; }
		}
	}
	
	@SuppressWarnings("serial")
	private class MyAssertionError extends AssertionError {
		private String message;
		public MyAssertionError(String message) {
			this.message = message;
			
		}
	}


	
	
	@Test
	public void 다양한_pathTemplate과_다양한_실제_url간의_동작_확인() throws Exception {

		String serviceApiUriTemplate;
		String partnerApiUriTemplate;
		String requestingUri;
		String expectedPartnerUri;
		List<String> serviceApiUriTemplateList = new ArrayList<String>();
		List<String> partnerApiUriTemplateList = new ArrayList<String>();
		
		// 기본
		{
			serviceApiUriTemplate 	= "/map/{key}/{module}";
			requestingUri 			= "/map/tom/seoul";
			partnerApiUriTemplate 	= "/partnermap/{key}/{module}";
			expectedPartnerUri 		= "/partnermap/tom/seoul?key=tom&module=seoul";
			doTest(serviceApiUriTemplate, partnerApiUriTemplate, requestingUri, expectedPartnerUri, SUCCESS);
		}
		
		
		// query parmeter에 대한 테스트
		{
			serviceApiUriTemplate 	= "/map/{key}/{module}";
			requestingUri 			= "/map/tom/seoul";
			partnerApiUriTemplate 	= "/partnermap?key={key}&module={module}";
			expectedPartnerUri 		= "/partnermap?key=tom&module=seoul";
			doTest(serviceApiUriTemplate, partnerApiUriTemplate, requestingUri, expectedPartnerUri, SUCCESS);
			
			serviceApiUriTemplate 	= "/map?key={key}&module={module}";
			requestingUri 			= "/map?key=tom&module=seoul";
			partnerApiUriTemplate 	= "/partnermap?key={key}&module={module}";
			expectedPartnerUri 		= "/partnermap?key=tom&module=seoul";
			doTest(serviceApiUriTemplate, partnerApiUriTemplate, requestingUri, expectedPartnerUri, SUCCESS);
			
		}
		
		// template의 마지막에 "/"에 대한 테스트들
		{
			// template의 마지막에  "/"가 있는 경우
			serviceApiUriTemplate 	= "/map/{key}/{module}/";
			requestingUri 			= "/map/tom/seoul";
			partnerApiUriTemplate 	= "/partnermap/{key}/{module}?key={key}&module={module}";
			expectedPartnerUri 		= "/partnermap/tom/seoul?key=tom&module=seoul";
			doTest(serviceApiUriTemplate, partnerApiUriTemplate, requestingUri, expectedPartnerUri, SUCCESS);


			// 실제 uri의 마지막에  "/"가 있는 경우
			serviceApiUriTemplate 	= "/map/{key}/{module}";
			requestingUri 			= "/map/tom/seoul/";
			partnerApiUriTemplate 	= "/partnermap/{key}/{module}?key={key}&module={module}";
			expectedPartnerUri 		= "/partnermap/tom/seoul?key=tom&module=seoul";
			doTest(serviceApiUriTemplate, partnerApiUriTemplate, requestingUri, expectedPartnerUri, SUCCESS);

			
			// uri 둘다  마지막에  "/"가 있는 경우
			serviceApiUriTemplate 	= "/map/{key}/{module}";
			requestingUri 			= "/map/tom/seoul/";
			partnerApiUriTemplate 	= "/partnermap/{key}/{module}?key={key}&module={module}";
			expectedPartnerUri 		= "/partnermap/tom/seoul?key=tom&module=seoul";
			doTest(serviceApiUriTemplate, partnerApiUriTemplate, requestingUri, expectedPartnerUri, SUCCESS);

		}
		
		// 다양한 uri template 변형에 대한 테스트
		{
			// 템플릿에 약간의 변형
			serviceApiUriTemplate 	= "/map/key/{key}/{module}";
			requestingUri 			= "/map/key/tom/seoul";
			partnerApiUriTemplate 	= "/partnermap/{key}/{module}?key={key}&module={module}";
			expectedPartnerUri 		= "/partnermap/tom/seoul?key=tom&module=seoul";
			doTest(serviceApiUriTemplate, partnerApiUriTemplate, requestingUri, expectedPartnerUri, SUCCESS);


			// partner 템플릿의 변형
			serviceApiUriTemplate 	= "/map/{key}/{module}";
			requestingUri 			= "/map/tom/seoul";
			partnerApiUriTemplate 	= "/{module}/{key}?key={key}&module={module}";
			expectedPartnerUri 		= "/seoul/tom?key=tom&module=seoul";
			doTest(serviceApiUriTemplate, partnerApiUriTemplate, requestingUri, expectedPartnerUri, SUCCESS);


			// partner 템플릿의 path part의 부분만 변수로
			serviceApiUriTemplate 	= "/map/{key}/{module}";
			requestingUri 			= "/map/tom/seoul";
			partnerApiUriTemplate 	= "/partnermap/hi_{key}/{module}?key={key}&module={module}";
			expectedPartnerUri 		= "/partnermap/hi_tom/seoul?key=tom&module=seoul";
			doTest(serviceApiUriTemplate, partnerApiUriTemplate, requestingUri, expectedPartnerUri, SUCCESS);

		}

		
		// 복수의 템플릿에 대한 테스트
		{
			serviceApiUriTemplateList.clear();
			serviceApiUriTemplateList.add("/map/{key}/{module}");
			serviceApiUriTemplateList.add("/map/key/{key}/{module}");
			serviceApiUriTemplateList.add("/map/key/{key}/module/{module}");
			requestingUri 			= "/map/tom/seoul";
			partnerApiUriTemplateList.clear();
			partnerApiUriTemplateList.add("/partnermap/1?key={key}&module={module}");
			partnerApiUriTemplateList.add("/partnermap/2?key={key}&module={module}");
			partnerApiUriTemplateList.add("/partnermap/3?key={key}&module={module}");
			expectedPartnerUri 		= "/partnermap/1?key=tom&module=seoul";
			doTest(serviceApiUriTemplateList, partnerApiUriTemplateList, requestingUri, expectedPartnerUri, SUCCESS);
			

			serviceApiUriTemplateList.clear();
			serviceApiUriTemplateList.add("/map/{key}/{module}");
			serviceApiUriTemplateList.add("/map/key/{key}/{module}");
			serviceApiUriTemplateList.add("/map/key/{key}/module/{module}");
			requestingUri 			= "/map/key/tom/seoul";
			partnerApiUriTemplateList.clear();
			partnerApiUriTemplateList.add("/partnermap/1?key={key}&module={module}");
			partnerApiUriTemplateList.add("/partnermap/2?key={key}&module={module}");
			partnerApiUriTemplateList.add("/partnermap/3?key={key}&module={module}");
			expectedPartnerUri 		= "/partnermap/2?key=tom&module=seoul";
			doTest(serviceApiUriTemplateList, partnerApiUriTemplateList, requestingUri, expectedPartnerUri, SUCCESS);
			
			
			serviceApiUriTemplateList.clear();
			serviceApiUriTemplateList.add("/map/{key}/{module}");
			serviceApiUriTemplateList.add("/map/key/{key}/{module}");
			serviceApiUriTemplateList.add("/map/key/{key}/module/{module}");
			requestingUri 			= "/map/key/tom/module/seoul";
			partnerApiUriTemplateList.clear();
			partnerApiUriTemplateList.add("/partnermap/1?key={key}&module={module}");
			partnerApiUriTemplateList.add("/partnermap/2?key={key}&module={module}");
			partnerApiUriTemplateList.add("/partnermap/3?key={key}&module={module}");
			expectedPartnerUri 		= "/partnermap/3?key=tom&module=seoul";
			doTest(serviceApiUriTemplateList, partnerApiUriTemplateList, requestingUri, expectedPartnerUri, SUCCESS);
			
		}

		// 실패 테스트
		{
			// serviceApi의 template과 매칭못하는 경우
			serviceApiUriTemplate 	= "/map/{key}/{module}";
			requestingUri 			= "/MAP/tom/seoul";
			partnerApiUriTemplate 	= "/partnermap/{key}/{module}?key={key}&module={module}";
			expectedPartnerUri 		= "/partnermap/tom/seoul?key=tom&module=seoul";
			doTest(serviceApiUriTemplate, partnerApiUriTemplate, requestingUri, expectedPartnerUri, FAIL);

			// template이 잘못된 경우
			serviceApiUriTemplate 	= "/map/hi_{key}/{module}";
			requestingUri 			= "/map/hi_tom/seoul";
			partnerApiUriTemplate 	= "/partnermap/{key}/{module}?key={key}&module={module}";
			expectedPartnerUri 		= "/partnermap/tom/seoul?key=tom&module=seoul";
			doTest(serviceApiUriTemplate, partnerApiUriTemplate, requestingUri, expectedPartnerUri, FAIL);

			// 중복된 template
			serviceApiUriTemplateList.clear();
			serviceApiUriTemplateList.add("/map/{key}/{module}");
			serviceApiUriTemplateList.add("/map/{module}/{key}");
			partnerApiUriTemplateList.clear();
			partnerApiUriTemplateList.add("/partnermap/1?key={key}&module={module}");
			partnerApiUriTemplateList.add("/partnermap/2?key={key}&module={module}");
			requestingUri 			= "";
			expectedPartnerUri 		= "";
			// 서비스 등록조차 않되야 한다.
			doTest(serviceApiUriTemplateList, partnerApiUriTemplateList, requestingUri, expectedPartnerUri, FAIL);
		}

	}

	
	@Test
	public void 정의된_query_파라매터가_없이_호출된_경우_처리_확인() {
		
		String serviceApiUriTemplate;
		String partnerApiUriTempalte;
		String requestingUri;
		String expectedPartnerUri;

		// 정상인 경우
		{
			serviceApiUriTemplate 	= "/map/withoutParameter?key={key}&module={module}";
			requestingUri 			= "/map/withoutParameter?key=KEY&module=MODULE";
			partnerApiUriTempalte 	= "/partnermap?key={key}&module={module}";
			expectedPartnerUri 		= "/partnermap?key=KEY&module=MODULE";
			doTest(serviceApiUriTemplate, partnerApiUriTempalte, requestingUri, expectedPartnerUri, SUCCESS);
		}
		
		// 파라매터 순서가 바뀐 경우
		{
			serviceApiUriTemplate 	= "/map/withoutParameter?key={key}&module={module}";
			requestingUri 			= "/map/withoutParameter?module=MODULE&key=KEY";
			partnerApiUriTempalte 	= "/partnermap?key={key}&module={module}";
			expectedPartnerUri 		= "/partnermap?key=KEY&module=MODULE";
			doTest(serviceApiUriTemplate, partnerApiUriTempalte, requestingUri, expectedPartnerUri, SUCCESS);
		}
		
		// 파라매터값 하나가 ""
		{
			serviceApiUriTemplate 	= "/map/withoutParameter?key={key}&module={module}";
			requestingUri 			= "/map/withoutParameter?key=KEY&module=";
			partnerApiUriTempalte 	= "/partnermap?key={key}&module={module}";
			expectedPartnerUri 		= "/partnermap?key=KEY&module=";
			doTest(serviceApiUriTemplate, partnerApiUriTempalte, requestingUri, expectedPartnerUri, SUCCESS);
		}
		
		// 아예 파라매터가 없다.
		{
			serviceApiUriTemplate 	= "/map/withoutParameter?key={key}&module={module}";
			requestingUri 			= "/map/withoutParameter?key=KEY";
			partnerApiUriTempalte 	= "/partnermap?key={key}&module={module}";
			expectedPartnerUri 		= "/partnermap?key=KEY&module=";
			doTest(serviceApiUriTemplate, partnerApiUriTempalte, requestingUri, expectedPartnerUri, SUCCESS);
		}
		
		// 모든 파라매터가 없다.
		{
			serviceApiUriTemplate 	= "/map/withoutParameter?key={key}&module={module}";
			requestingUri 			= "/map/withoutParameter";
			partnerApiUriTempalte 	= "/partnermap?key={key}&module={module}";
			expectedPartnerUri 		= "/partnermap?key=&module=";
			doTest(serviceApiUriTemplate, partnerApiUriTempalte, requestingUri, expectedPartnerUri, SUCCESS);
		}
		
	}
	
	
	@Test
	public void 서비스_디스트립션에_정의되지_않은_파라매터_처리_확인() {
		
		String serviceApiUriTemplate;
		String partnerApiUriTempalte;
		String requestingUri;
		String expectedPartnerUri;

		// 정상인 경우
		{
			serviceApiUriTemplate 	= "/map?name={name}&color={color}";
			requestingUri 			= "/map?name=NAME&color=COLOR";
			partnerApiUriTempalte 	= "/somemap?name={name}&color={color}";
			expectedPartnerUri 		= "/somemap?name=NAME&color=COLOR";
			doTest(serviceApiUriTemplate, partnerApiUriTempalte, requestingUri, expectedPartnerUri, SUCCESS);
		}
		
		// serviceApi에만 pathTemplate에 query parameter를 정의하지 않았다.
		{
			serviceApiUriTemplate 	= "/map";
			requestingUri 			= "/map?name=NAME&color=COLOR";
			partnerApiUriTempalte 	= "/somemap?name={name}&color={color}";
			expectedPartnerUri 		= "/somemap?name=NAME&color=COLOR";
			doTest(serviceApiUriTemplate, partnerApiUriTempalte, requestingUri, expectedPartnerUri, SUCCESS);
		}
		
		// serviceApi, parternApi 둘다 pathTemplate에 query parameter를 정의하지 않았다.
		{
			serviceApiUriTemplate 	= "/map";
			requestingUri 			= "/map?name=NAME&color=COLOR";
			partnerApiUriTempalte 	= "/somemap";
			expectedPartnerUri 		= "/somemap?name=NAME&color=COLOR";
			doTest(serviceApiUriTemplate, partnerApiUriTempalte, requestingUri, expectedPartnerUri, SUCCESS);
		}
		
		// 살짝 변형1. serviceApiUriTemplate이 ?로 끝난다.
		{
			serviceApiUriTemplate 	= "/map?";
			requestingUri 			= "/map?name=NAME&color=COLOR";
			partnerApiUriTempalte 	= "/somemap";
			expectedPartnerUri 		= "/somemap?name=NAME&color=COLOR";
			doTest(serviceApiUriTemplate, partnerApiUriTempalte, requestingUri, expectedPartnerUri, SUCCESS);
		}
		
		// 살짝 변형2. partnerApiUriTemplate이 ?로 끝난다.
		{
			serviceApiUriTemplate 	= "/map";
			requestingUri 			= "/map?name=NAME&color=COLOR";
			partnerApiUriTempalte 	= "/somemap?";
			expectedPartnerUri 		= "/somemap?name=NAME&color=COLOR";
			doTest(serviceApiUriTemplate, partnerApiUriTempalte, requestingUri, expectedPartnerUri, SUCCESS);
		}
		
		// 살짝 변형3. serviceApiUriTemplate, partnerApiUriTempalte 둘다 ?로 끝난다.
		{
			serviceApiUriTemplate 	= "/map?";
			requestingUri 			= "/map?name=NAME&color=COLOR";
			partnerApiUriTempalte 	= "/somemap?";
			expectedPartnerUri 		= "/somemap?name=NAME&color=COLOR";
			doTest(serviceApiUriTemplate, partnerApiUriTempalte, requestingUri, expectedPartnerUri, SUCCESS);
		}
		
	}
	
	
	@Test
	public void 파라매터_매핑에_관련된_처리_확인() {
		
		String serviceApiUriTemplate;
		String partnerApiUriTempalte;
		String requestingUri;
		String expectedPartnerUri;

		// radix_내부_값을_파라매터로_전달
		{
			serviceApiUriTemplate 	= null;
			requestingUri 			= "/map/with/token";
			partnerApiUriTempalte 	= null;
			expectedPartnerUri 		= "/map?token="+USER_NAME;
			doTest(serviceApiUriTemplate, partnerApiUriTempalte, requestingUri, expectedPartnerUri, SUCCESS);
		}

		// 파라매터_매핑에서_이름이_다를_경우_확인
		{
			serviceApiUriTemplate 	= null;
			requestingUri 			= "/map/with/token?old_param_name=tom";
			partnerApiUriTempalte 	= null;
			expectedPartnerUri 		= "/map?token="+USER_NAME+"&new_param_name=tom";
			doTest(serviceApiUriTemplate, partnerApiUriTempalte, requestingUri, expectedPartnerUri, SUCCESS);
		}
		
	}
	
	
	private static class InnerUtil {

		public static void copyAndModifyFile(String sourceFileName, String newFileName, Map<String, String> modifyMap) throws IOException {
			String sourceFileNameOnClasspath = TestResourceFinder.getResourceFileNameOnClasspath(sourceFileName);
			URL sourceFileUrl = Thread.currentThread().getContextClassLoader().getResource(sourceFileNameOnClasspath);
			if(sourceFileUrl==null) { throw new IOException("can't found file on classpath. sourceFileName="+sourceFileName); }

			String absoluteSourceFileName = sourceFileUrl.getFile();

			File sourceFile = new File(absoluteSourceFileName);
			String absolutePath = sourceFile.getParent();
			String absoluteNewFileName = absolutePath+"/"+newFileName;

			String description = readFile(absoluteSourceFileName);
			for(String oldValue : modifyMap.keySet()) {
				String newValue = modifyMap.get(oldValue);
				oldValue = escapeBrace(oldValue);
				newValue = escapeBrace(newValue);
				description = description.replaceAll(oldValue, newValue);
			}
			System.out.println(description);
			writeFile(absoluteNewFileName, description);
		}

		private static String escapeBrace(String string) {
			string = string.replaceAll("\\{", "\\\\{");
			string = string.replaceAll("\\}", "\\\\}");
			return string;
		}
		

		private static void assertQueryEquals(String message, String expectedQueryStirng, String actualQueryString) {
			Map<String, String> expectedParameterMap = parseQueryStringIntoMap(expectedQueryStirng);
			Map<String, String> actualParameterMap = parseQueryStringIntoMap(actualQueryString);
			
			assertTrue(message+". expected="+expectedQueryStirng+", actual="+actualQueryString, expectedParameterMap.size()==actualParameterMap.size());
			for(String name: expectedParameterMap.keySet()) {
				String expectedValue = expectedParameterMap.get(name);
				String actualValue = actualParameterMap.get(name);
				assertEquals(message+". expected="+expectedQueryStirng+", actual="+actualQueryString, expectedValue, actualValue);
			}
			
			
		}

		private static Map<String, String> parseQueryStringIntoMap(String queryString) {
			Map<String, String> parameterMap = new Hashtable<String, String>();
			if(queryString==null) { return parameterMap; }
			StringTokenizer tokenizer = new StringTokenizer(queryString, "&");
			while(tokenizer.hasMoreTokens()) {
				String aParameterPart = tokenizer.nextToken();
				int i = aParameterPart.indexOf("=");
				if(i==-1) { continue; }
				String name = aParameterPart.substring(0, i);
				String value = aParameterPart.substring(i+1);
				parameterMap.put(name, value);
			}
			return parameterMap;
		}
		
	}

	private static String readFile(String fileName) throws IOException {

		FileReader fileReader = null;
		BufferedReader bufferedReader = null;


		try {
			fileReader = new FileReader(fileName);
			bufferedReader = new BufferedReader(fileReader);
			String line;
			StringBuilder sb = new StringBuilder();
			do {
				line = bufferedReader.readLine();
				if(line!=null) { sb.append(line); }
			} while(line!=null);
			return sb.toString();
		} finally {
			if(bufferedReader!=null) { try { bufferedReader.close(); } catch(Exception e) {} }
			if(fileReader!=null) { try { fileReader.close(); } catch(Exception e) {} }
		}

	}

	private static void writeFile(String fileName, String content) throws IOException {

		FileWriter fileWriter = null;
		PrintWriter printWriter = null;

		try {
			fileWriter = new FileWriter(fileName);
			printWriter = new PrintWriter(fileWriter);
			fileWriter.write(content);
		} finally {
			if(printWriter!=null) { try { printWriter.close(); } catch(Exception e) {} }
			if(fileWriter!=null) { try { fileWriter.close(); } catch(Exception e) {} }
		}

	}

}
