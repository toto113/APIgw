package com.kthcorp.radix.component.adaptor;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;
import java.util.concurrent.Future;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.test.context.ContextConfiguration;
import com.kthcorp.radix.util.test.junit.MysqlEmbeddableSpringJUnit4ClassRunner;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import com.kthcorp.radix.domain.canonical.CanonicalMessage;
import com.kthcorp.radix.domain.canonical.reply.AdaptorRestReply;
import com.kthcorp.radix.domain.service.api.protocol.http.HttpMethod;
import com.kthcorp.radix.domain.service.api.protocol.http.ParameterMap;

@RunWith(MysqlEmbeddableSpringJUnit4ClassRunner.class)
@ContextConfiguration(
		locations = { "classpath:/META-INF/spring/spring-application-context.xml", 
				"classpath:/META-INF/spring/spring-camel-context-test.xml",
				"classpath:/META-INF/spring/spring-service-component-context.xml",
				"classpath:/META-INF/spring/jetty.xml"})
public class RestServiceCallComponentWithContextTestSkip extends AbstractProvider {

	@Test
	public void testGetGif() {
		
		String uri = "http://localhost:8181/gif/";
		ParameterMap parameters = new ParameterMap();
		ParameterMap headers = new ParameterMap();
		
		CanonicalMessage canonicalMessage = super.createCanonicalMessage(HttpMethod.GET, uri, parameters, headers);
		
		Future<Object> future = producer.asyncRequestBody("direct:testComponent", canonicalMessage);
		CanonicalMessage ret = producer.extractFutureBody(future, CanonicalMessage.class);

		LOG.info("ret={}", ret);
		Assert.assertNotNull(ret);
		AdaptorRestReply reply = (AdaptorRestReply) ret.getPayload().getAdaptorReply();
		LOG.info("reply={}", reply);
		Assert.assertNotNull(reply);
		LOG.info("reply.body={}", reply.getBody());
		Assert.assertNotNull(reply.getBody());
		LOG.info("reply.contentLength={}, reply.body.length={}", reply.getContentLength(), reply.getBody().length);
		Assert.assertEquals(reply.getContentLength(), reply.getBody().length);
		save("/var/radix/image.gif", reply.getBody());
	}
	
	@Test
	public void testGetPng() {
		
		String uri = "http://localhost:8181/png/";
		ParameterMap parameters = new ParameterMap();
		ParameterMap headers = new ParameterMap();
		
		CanonicalMessage canonicalMessage = super.createCanonicalMessage(HttpMethod.GET, uri, parameters, headers);
		
		Future<Object> future = producer.asyncRequestBody("direct:testComponent", canonicalMessage);
		CanonicalMessage ret = producer.extractFutureBody(future, CanonicalMessage.class);

		LOG.info("ret={}", ret);
		Assert.assertNotNull(ret);
		AdaptorRestReply reply = (AdaptorRestReply) ret.getPayload().getAdaptorReply();
		LOG.info("reply={}", reply);
		Assert.assertNotNull(reply);
		LOG.info("reply.body={}", reply.getBody());
		Assert.assertNotNull(reply.getBody());
		LOG.info("reply.contentLength={}, reply.body.length={}", reply.getContentLength(), reply.getBody().length);
		Assert.assertEquals(reply.getContentLength(), reply.getBody().length);
		save("/var/radix/image.png", reply.getBody());
	}
	
	@Test
	public void testGetJs() {
		
		String uri = "http://localhost:8181/js/";
		ParameterMap parameters = new ParameterMap();
		ParameterMap headers = new ParameterMap();
		
		CanonicalMessage canonicalMessage = super.createCanonicalMessage(HttpMethod.GET, uri, parameters, headers);
		
		Future<Object> future = producer.asyncRequestBody("direct:testComponent", canonicalMessage);
		CanonicalMessage ret = producer.extractFutureBody(future, CanonicalMessage.class);

		LOG.info("ret={}", ret);
		Assert.assertNotNull(ret);
		AdaptorRestReply reply = (AdaptorRestReply) ret.getPayload().getAdaptorReply();
		LOG.info("reply={}", reply);
		Assert.assertNotNull(reply);
		LOG.info("reply.body={}", reply.getBody());
		Assert.assertNotNull(reply.getBody());
		LOG.info("reply.contentLength={}, reply.body.length={}", reply.getContentLength(), reply.getBody().length);
		Assert.assertEquals(reply.getContentLength(), reply.getBody().length);
		save("/var/radix/jquery-1.6.1.min.js", reply.getBody());
	}
	
	@Test
	public void testGetResource() {
		
		String uri = "http://localhost:8181/resource/image.png/";
		ParameterMap parameters = new ParameterMap();
		ParameterMap headers = new ParameterMap();
		
		CanonicalMessage canonicalMessage = super.createCanonicalMessage(HttpMethod.GET, uri, parameters, headers);
		
		Future<Object> future = producer.asyncRequestBody("direct:testComponent", canonicalMessage);
		CanonicalMessage ret = producer.extractFutureBody(future, CanonicalMessage.class);

		LOG.info("ret={}", ret);
		Assert.assertNotNull(ret);
		AdaptorRestReply reply = (AdaptorRestReply) ret.getPayload().getAdaptorReply();
		LOG.info("reply={}", reply);
		Assert.assertNotNull(reply);
		LOG.info("reply.body={}", reply.getBody());
		Assert.assertNotNull(reply.getBody());
		LOG.info("reply.contentLength={}, reply.body.length={}", reply.getContentLength(), reply.getBody().length);
		Assert.assertEquals(reply.getContentLength(), reply.getBody().length);
		save("/var/radix/image.png", reply.getBody());
	}
	
	@Test
	public void testEchoGet() {
		
		//String uri = "http://localhost:8181/echo/";
		String uri = "http://211.42.140.128:8080?a=aaa&b=bbb&c=ccc" ;
		ParameterMap parameters = new ParameterMap();
		parameters.put("paramKey", "paramValue");
		
		ParameterMap headers = new ParameterMap();
		headers.put("headerKey", Arrays.asList("headerValue"));
		
		CanonicalMessage canonicalMessage = super.createCanonicalMessage(HttpMethod.GET, uri, parameters, headers);
		
		Future<Object> future = producer.asyncRequestBody("direct:testComponent", canonicalMessage);
		CanonicalMessage ret = producer.extractFutureBody(future, CanonicalMessage.class);

		LOG.info("ret={}", ret);
		Assert.assertNotNull(ret);
		AdaptorRestReply reply = (AdaptorRestReply) ret.getPayload().getAdaptorReply();
		LOG.info("reply={}", reply);
		Assert.assertNotNull(reply);
	}
	
	@Test
	public void testEchoPost() {
		
		//String uri = "http://localhost:8181/echo/";
		String uri = "http://211.42.140.128:8080?a=aaa&b=bbb&c=ccc";
		ParameterMap parameters = new ParameterMap();
		parameters.put("paramKey", "paramValue");
		
		ParameterMap headers = new ParameterMap();
		headers.put("headerKey", Arrays.asList("headerValue"));
		
		MultiValueMap<String, Object> body = new LinkedMultiValueMap<String, Object>();
		body.add("body", "body");
		body.add("list", "list1");
		body.add("list", "list2");
		ByteArrayResource file = new ByteArrayResource(readResource("image.gif")) {
		    @Override
		    public String getFilename() throws IllegalStateException {
		     return "image.gif";
		    }
		};
		body.add("file", file);
		
		CanonicalMessage canonicalMessage = super.createCanonicalMessage(HttpMethod.POST, uri, parameters, body, headers);
		
		Future<Object> future = producer.asyncRequestBody("direct:testComponent", canonicalMessage);
		CanonicalMessage ret = producer.extractFutureBody(future, CanonicalMessage.class);

		LOG.info("ret={}", ret);
		Assert.assertNotNull(ret);
		AdaptorRestReply reply = (AdaptorRestReply) ret.getPayload().getAdaptorReply();
		LOG.info("reply={}", reply);
		Assert.assertNotNull(reply);
	}
	
	@Test
	public void testEchoPut() {
		
		String uri = "http://localhost:8181/echo/";
		ParameterMap parameters = new ParameterMap();
		parameters.put("paramKey", "paramValue");
		
		ParameterMap headers = new ParameterMap();
		headers.put("headerKey", Arrays.asList("headerValue"));
		
		MultiValueMap<String, Object> body = new LinkedMultiValueMap<String, Object>();
		body.add("body", "body");
		
		CanonicalMessage canonicalMessage = super.createCanonicalMessage(HttpMethod.PUT, uri, parameters, body, headers);
		
		Future<Object> future = producer.asyncRequestBody("direct:testComponent", canonicalMessage);
		CanonicalMessage ret = producer.extractFutureBody(future, CanonicalMessage.class);

		LOG.info("ret={}", ret);
		Assert.assertNotNull(ret);
		AdaptorRestReply reply = (AdaptorRestReply) ret.getPayload().getAdaptorReply();
		LOG.info("reply={}", reply);
		Assert.assertNotNull(reply);
	}
	
	@Test
	public void testEchoDelete() {
		
		String uri = "http://localhost:8181/echo/";
		ParameterMap parameters = new ParameterMap();
		parameters.put("paramKey", "paramValue");
		
		ParameterMap headers = new ParameterMap();
		headers.put("headerKey", Arrays.asList("headerValue"));
		
		CanonicalMessage canonicalMessage = super.createCanonicalMessage(HttpMethod.DELETE, uri, parameters, headers);
		
		Future<Object> future = producer.asyncRequestBody("direct:testComponent", canonicalMessage);
		CanonicalMessage ret = producer.extractFutureBody(future, CanonicalMessage.class);

		LOG.info("ret={}", ret);
		Assert.assertNotNull(ret);
		AdaptorRestReply reply = (AdaptorRestReply) ret.getPayload().getAdaptorReply();
		LOG.info("reply={}", reply);
		Assert.assertNotNull(reply);
	}
	
	private byte[] readResource(String resourcePath) {
		
		byte[] data = null;
		URL url = getClass().getClassLoader().getResource(resourcePath);
		LOG.debug("resourceUrl={}", url);
		
		InputStream is = null;
		try {
			URLConnection conn = url.openConnection();
			is = conn.getInputStream();
			data = new byte[is.available()];
			is.read(data);
		} catch(IOException e) {
			e.printStackTrace();
		} finally {
			try {
				is.close();
			} catch(IOException e) {
				e.printStackTrace();
			}
		}
		return data;
	}
	
	private void save(String path, byte[] data) {
		
		File file = new File(path);
		FileOutputStream os = null;
		try {
			os = new FileOutputStream(file);
			for(byte b : data) {
				os.write(b);
			}
		} catch(FileNotFoundException e) {
			e.printStackTrace();
		} catch(IOException e) {
			e.printStackTrace();
		} finally {
			try {
				os.close();
			} catch(IOException e) {
				e.printStackTrace();
			}
		}
	}
}
