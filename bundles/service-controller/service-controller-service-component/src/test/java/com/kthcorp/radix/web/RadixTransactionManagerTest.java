package com.kthcorp.radix.web;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import javax.servlet.ServletException;

import junit.framework.Assert;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import com.kthcorp.radix.util.UuidViewableLoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ContextConfiguration;
import com.kthcorp.radix.util.test.junit.MysqlEmbeddableSpringJUnit4ClassRunner;
import org.springframework.web.servlet.DispatcherServlet;

import com.kthcorp.radix.web.mock.MockWebApplication;
import com.kthcorp.radix.web.mock.MockWebApplicationContextLoader;

@RunWith(MysqlEmbeddableSpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/META-INF/spring/spring-all-context-test.xml" }, loader = MockWebApplicationContextLoader.class)
@MockWebApplication(name = "radixtest")
public class RadixTransactionManagerTest {
	
	private final Logger LOG = UuidViewableLoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private DispatcherServlet dispatcherServlet;
	
	@Test
	@Ignore
	public void testLogging() throws ServletException, IOException {

		File file = new File("/var/radix/stat/radix_transaction.log");
		if(file.exists()) {
			file.delete();
		}
		
		Assert.assertTrue(file.exists());
	}
	
	@Test
	public void testGetTransaction() throws ServletException, IOException {

		File file = new File("/var/radix/stat/radix_transaction.log");
		if(file.exists()) {
			file.delete();
		}
		
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setRequestURI("/MapAPI/1/map");
		request.setMethod("GET");
		request.addHeader("Authorization", "Bearer 297b9e64-2d3b-4e2c-a1fb-9a0cb7a59f47");
		request.setQueryString("module=");
		
		MockHttpServletResponse response = new MockHttpServletResponse();
		dispatcherServlet.service(request, response);
		
		LOG.debug("response={}", response.getContentAsString());
		Assert.assertNotNull(response.getContentAsString());
		
		Assert.assertTrue(file.exists());
		BufferedReader br = new BufferedReader(new FileReader(file));
		String line = br.readLine();
		LOG.debug("line={}", line);
		Assert.assertNotNull(line);
		LOG.debug("args={}", line.split("\t").length);
		Assert.assertTrue(line.split("\t").length > 0);
	}
}
