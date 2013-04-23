package com.kthcorp.radix.component.adaptor;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import com.kthcorp.radix.util.UuidViewableLoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import com.kthcorp.radix.util.test.junit.MysqlEmbeddableSpringJUnit4ClassRunner;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestOperations;

@RunWith(MysqlEmbeddableSpringJUnit4ClassRunner.class)
@ContextConfiguration(
		locations = { "classpath:/META-INF/spring/spring-application-context.xml", 
				"classpath:/META-INF/spring/spring-service-component-context.xml",
				"classpath:/META-INF/spring/jetty.xml"})
public class HttpComponentClientTestSkip extends AbstractJUnit4SpringContextTests {
	
	private final Logger LOG = UuidViewableLoggerFactory.getLogger(this.getClass());
	
	@Autowired
	ClientHttpRequestFactory clientHttpRequestFactory;

	@Autowired
	private RestOperations restTemplate;
	
	@Test
	public void testOne() {
		
		String uri = "http://localhost:8181/rest/wait?number=";
		new Shooter(0, uri, restTemplate).start();
		
		try {
			Thread.sleep(10 * 1000);
		} catch(InterruptedException e) {
			LOG.error("interrupted : {}", e.getMessage());
		}
	}
	
	@Test
	public void testMaxRoute() {
		
		String uri = "http://localhost:8181/rest/wait?number=";
		for(int i = 0; i < 10; i++) {
			new Shooter(i, uri, restTemplate).start();			
			try {
				Thread.sleep(1000);
			} catch(InterruptedException e) {
				LOG.error("interrupted : {}", e.getMessage());
			}
		}
		
		try {
			Thread.sleep(10 * 1000);
		} catch(InterruptedException e) {
			LOG.error("interrupted : {}", e.getMessage());
		}
	}
	
	@Test
	public void testMaxRouteOverflow() {
		
		String uri = "http://localhost:8181/rest/wait?number=";
		for(int i = 0; i < 10; i++) {
			new Shooter(i, uri, restTemplate).start();			
			try {
				Thread.sleep(100);
			} catch(InterruptedException e) {
				LOG.error("interrupted : {}", e.getMessage());
			}
		}
		
		try {
			Thread.sleep(10 * 1000);
		} catch(InterruptedException e) {
			LOG.error("interrupted : {}", e.getMessage());
		}
	}
	
	@Test
	public void testInvalidRoute() {
		
		String uri = "http://localhost:8888/rest/wait?number=";
		new Shooter(0, uri, restTemplate).start();
		
		try {
			Thread.sleep(10 * 1000);
		} catch(InterruptedException e) {
			LOG.error("interrupted : {}", e.getMessage());
		}
	}
	
	@Test
	public void testMaxRoutes() {
		
		String[] uris = { "http://localhost:8181/rest/wait?number=", "http://211.113.53.126:8080/sonar/?number=" };
		for(String uri : uris) {
			for(int i = 0; i < 10; i++) {
				new Shooter(i, uri, restTemplate).start();			
				try {
					Thread.sleep(100);
				} catch(InterruptedException e) {
					LOG.error("interrupted : {}", e.getMessage());
				}
			}
		}
		
		try {
			Thread.sleep(10 * 1000);
		} catch(InterruptedException e) {
			LOG.error("interrupted : {}", e.getMessage());
		}
	}
	
	private static class Shooter extends Thread {
		
		private final Logger LOG = UuidViewableLoggerFactory.getLogger(this.getClass());
		private int number;
		private String uri;
		private RestOperations restTemplate;
		Shooter(int number, String uri, RestOperations restTemplate) {
			this.number = number;
			this.uri = uri + number;
			this.restTemplate = restTemplate;
		}
		
		@Override
		public void run() {
			try {
				LOG.debug(">>>>>>>> {}th message shoot for {}", number, uri);
				String ret = restTemplate.getForObject(uri, String.class);
				LOG.debug("<<<<<<<< {}th response length is {}", this.number, ret.length());
			} catch(RestClientException e) {
				LOG.error("RestClientException : {}", e.getMessage());
			} catch(Exception e) {
				LOG.error("Exception : {}", e.getMessage());
			}
		}
	}
}
