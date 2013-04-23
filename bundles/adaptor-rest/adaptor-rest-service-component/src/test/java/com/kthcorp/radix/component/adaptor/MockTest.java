package com.kthcorp.radix.component.adaptor;

import java.io.IOException;

import javax.servlet.ServletException;

import org.junit.Assert;
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

@RunWith(MysqlEmbeddableSpringJUnit4ClassRunner.class)
@ContextConfiguration(
	locations = { 
			"classpath:/META-INF/spring/spring-application-context.xml"
			},
	loader = MockWebApplicationContextLoader.class
)
@MockWebApplication(name="radix-rest-adaptor", locations={ "classpath:/META-INF/spring/servlet-context.xml" })
public class MockTest {
	
	private final Logger LOG = UuidViewableLoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private DispatcherServlet dispatcherServlet;
	
	@Test
	public void testMock() throws ServletException, IOException {
		
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setRequestURI("/rest/");
		request.setQueryString("query=query");
		request.setMethod("GET");
		
		MockHttpServletResponse response = new MockHttpServletResponse();
		
		LOG.debug("dispatcherServlet={}", dispatcherServlet);
		LOG.debug("request={}", request);
		LOG.debug("response={}", response);
		dispatcherServlet.service(request, response);
		
		LOG.info("response={}", response.getContentAsString());
		Assert.assertEquals("rest", response.getContentAsString());
	}
}
