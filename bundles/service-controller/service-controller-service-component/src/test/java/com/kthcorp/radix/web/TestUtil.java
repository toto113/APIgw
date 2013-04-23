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
import org.junit.Ignore;
import org.slf4j.Logger;
import com.kthcorp.radix.util.UuidViewableLoggerFactory;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.servlet.DispatcherServlet;

@Ignore
public class TestUtil {

	
	/**
	 * return serviceId
	 */
	public static String createService(DispatcherServlet dispatcherServlet, String partnerId, String businessPlatformKey, String serviceDescriptionFileName) throws IOException, ServletException, JSONException {

		Logger logger = getLogger();

		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setRequestURI("/platform/services");
		request.setMethod("POST");
		request.setContentType("application/x-www-form-urlencoded");
		request.setAttribute("businessPlatformKey", businessPlatformKey);

		String serviceDescription = getServiceDescription(serviceDescriptionFileName);


		Map<String, String> map = new HashMap<String, String>();
		map.put("partner_id", partnerId);
		map.put("service_description", serviceDescription);
		
		request.addParameters(map);
		
		MockHttpServletResponse response = new MockHttpServletResponse();
		
		dispatcherServlet.service(request, response);
		
		logger.debug("response status=" + response.getStatus());
		
		String result = response.getContentAsString();
		logger.debug("resposne result=" + result);

		Assert.assertEquals("incorrect response status. response content="+response.getContentAsString(), HttpStatus.SC_CREATED, response.getStatus());
		
		JSONObject obj = new JSONObject(result);
		Assert.assertEquals(obj.get("status"), "success");
		Assert.assertEquals(obj.get("body_type"), "MAP");
		
		JSONObject body = obj.getJSONObject("body");
		String serviceID = body.getString("service_id");
		
		logger.debug("service created. serviceID=" + serviceID);
		return serviceID;
		
	}
	
	public static void removeService(DispatcherServlet dispatcherServlet, String partnerId, String businessPlatformKey, String serviceId) throws ServletException, IOException {

		Logger logger = getLogger();

		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setRequestURI("/platform/services/" + serviceId);
		request.setMethod("DELETE");
		request.setQueryString("partner_id="+partnerId);
		request.setAttribute("businessPlatformKey", businessPlatformKey);
		
		MockHttpServletResponse response = new MockHttpServletResponse();
		
		dispatcherServlet.service(request, response);
		logger.debug("response status=" + response.getStatus());
		
		String result = response.getContentAsString();
		logger.debug("response result=" + result);
		
		Assert.assertEquals("removing service failed. serviceId="+serviceId, HttpStatus.SC_OK, response.getStatus());
		
	}
	
	public static void getService(DispatcherServlet dispatcherServlet, String partnerId, String businessPlatformKey, String serviceId) throws ServletException, IOException {

		Logger logger = getLogger();

		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setRequestURI("/platform/services/" + serviceId);
		request.setMethod("GET");
		request.setQueryString("partner_id="+partnerId);
		request.setAttribute("businessPlatformKey", businessPlatformKey);
		
		MockHttpServletResponse response = new MockHttpServletResponse();
		
		
		dispatcherServlet.service(request, response);
		logger.debug("response status=" + response.getStatus());
		
		String result = response.getContentAsString();
		logger.debug("resposne result=" + result);
		
		Assert.assertEquals(response.getStatus(), HttpStatus.SC_OK);

	}


	private static String getServiceDescription(String fileName) throws IOException {

		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(Thread.currentThread().getContextClassLoader().getResourceAsStream(fileName)));
		} catch(NullPointerException e) {
			throw new IOException("unable to load description file '"+fileName+"'");
		}

		String serviceDescription = null, line = null;

		while((line = reader.readLine()) != null) {
			if(serviceDescription == null) {
				serviceDescription = line;
			} else {
				serviceDescription = serviceDescription + line;
			}
		}

		//			serviceDescription = serviceDescription.replace("AroTrafficInfo", "Tour" + System.currentTimeMillis());

		return serviceDescription;
	}

	public static Logger getLogger() {

		String callerClassName = getCallerClassName();

		Logger logger = UuidViewableLoggerFactory.getLogger(callerClassName);

		return logger;
	}


	// TODO : remove. TestResourceFinder의 메소드와 중복된다. 삭제하자.
	public static String getCallerClassName() {
		try {
			throw new Exception("HI");
		} catch(Exception e) {
			StackTraceElement[] stackTraces = e.getStackTrace();
			for(StackTraceElement aStack : stackTraces) {
				String fullClassName = aStack.getClassName();
				if(fullClassName.startsWith(TestUtil.class.getName()) ) { continue; }
				// inner class 일수도 있다.
				if(fullClassName.indexOf("$")>0) {
					fullClassName = fullClassName.substring(0, fullClassName.indexOf("$"));
				}
				return fullClassName;
			}
		}
		return null;
	}
	
	
}
