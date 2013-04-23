package com.kthcorp.radix.component.adaptor;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import com.kthcorp.radix.util.UuidViewableLoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import com.kthcorp.radix.util.test.junit.MysqlEmbeddableSpringJUnit4ClassRunner;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestOperations;

import com.kthcorp.radix.adaptor.rest.service.RadixHttpComponentsClientHttpRequestFactory;

@RunWith(MysqlEmbeddableSpringJUnit4ClassRunner.class)
@ContextConfiguration(
	locations = { "classpath:/META-INF/spring/spring-application-context.xml", 
		"classpath:/META-INF/spring/spring-service-component-context.xml",
		"classpath:/META-INF/spring/jetty.xml"})
public class RestTemplateBodyTestSkip extends AbstractJUnit4SpringContextTests {
	
	private final Logger LOG = UuidViewableLoggerFactory.getLogger(this.getClass());
	
	@Autowired
	@Qualifier("radixClientHttpRequestFactory")
	RadixHttpComponentsClientHttpRequestFactory clientHttpRequestFactory;

	@Autowired
	@Qualifier("restTemplate")
	private RestOperations restTemplate;
	
	private static final Charset CHARSET = Charset.forName("utf-8");

	@SuppressWarnings("unused")
	private HttpHeaders createHttpHeader(MediaType mediaType) {
		
		HttpHeaders headers = new HttpHeaders();
		Map<String, String> headerParameters = new HashMap<String, String>();
		headerParameters.put("charset", CHARSET.name());
		headers.setContentType(new MediaType(mediaType, headerParameters));
		headers.setAccept(Arrays.asList(MediaType.ALL));
		return headers;
	}
	
	@Test
	public void get() {
		
		String url = "http://localhost:8181/rest/get?query=query";
		HttpHeaders requestHeaders = new HttpHeaders();
//		HttpHeaders requestHeaders = createHttpHeader(MediaType.TEXT_PLAIN);
		requestHeaders.put("Radix", Arrays.asList("get"));
		
//		GET Method cannot read body
		String requestBody = "body";
//		MultiValueMap<String, Object> requestBody = new LinkedMultiValueMap<String, Object>();
//		requestBody.put("body", Arrays.asList("body"));
		
		HttpEntity<Object> requestEntity = new HttpEntity<Object>(requestBody, requestHeaders);

		ResponseEntity<byte[]> entity = restTemplate.exchange(url, HttpMethod.GET, requestEntity, byte[].class);
		
		LOG.debug("statusCode={}", entity.getStatusCode());
		HttpHeaders headers = entity.getHeaders();
		for(String key : headers.keySet()) {
			LOG.debug("{}={}", key, headers.get(key));
		}
		LOG.debug("body={}", entity.getBody());
		LOG.debug("text={}", new String(entity.getBody(), headers.getContentType().getCharSet()));		
	}

	@Test
	public void post() {
		
		String url = "http://localhost:8181/rest/post?query=query";
		HttpHeaders requestHeaders = new HttpHeaders();
//		HttpHeaders requestHeaders = createHttpHeader(MediaType.MULTIPART_FORM_DATA);	
		requestHeaders.put("Radix", Arrays.asList("post"));

		MultiValueMap<String, Object> requestBody = new LinkedMultiValueMap<String, Object>();
		requestBody.add("body", "body");
		ByteArrayResource file = new ByteArrayResource(readResource("image.gif")) {
		    @Override
		    public String getFilename() throws IllegalStateException {
		     return "image.gif";
		    }
		};
		requestBody.add("file", file);
		
		HttpEntity<Object> requestEntity = new HttpEntity<Object>(requestBody, requestHeaders);
		ResponseEntity<byte[]> entity = restTemplate.exchange(url, HttpMethod.POST, requestEntity, byte[].class);
		
		LOG.debug("statusCode={}", entity.getStatusCode());
		HttpHeaders headers = entity.getHeaders();
		for(String key : headers.keySet()) {
			LOG.debug("{}={}", key, headers.get(key));
		}
		LOG.debug("body={}", entity.getBody());
		LOG.debug("text={}", new String(entity.getBody(), headers.getContentType().getCharSet()));	
	}
	
	@Test
	public void put() {
		
		String url = "http://localhost:8181/rest/put?query=query";
		HttpHeaders requestHeaders = new HttpHeaders();
//		HttpHeaders requestHeaders = createHttpHeader(MediaType.MULTIPART_FORM_DATA);	
		requestHeaders.put("Radix", Arrays.asList("post"));

		MultiValueMap<String, Object> requestBody = new LinkedMultiValueMap<String, Object>();
		requestBody.add("body", "body");
		ByteArrayResource file = new ByteArrayResource(readResource("image.gif")) {
		    @Override
		    public String getFilename() throws IllegalStateException {
		     return "image.gif";
		    }
		};
		requestBody.add("file", file);
		
		HttpEntity<Object> requestEntity = new HttpEntity<Object>(requestBody, requestHeaders);
		ResponseEntity<byte[]> entity = restTemplate.exchange(url, HttpMethod.PUT, requestEntity, byte[].class);
		
		LOG.debug("statusCode={}", entity.getStatusCode());
		HttpHeaders headers = entity.getHeaders();
		for(String key : headers.keySet()) {
			LOG.debug("{}={}", key, headers.get(key));
		}
		LOG.debug("body={}", entity.getBody());
		LOG.debug("text={}", new String(entity.getBody(), headers.getContentType().getCharSet()));	
	}
	
	@Test
	public void delete() {
		
		String url = "http://localhost:8181/rest/delete?query=query";
		HttpHeaders requestHeaders = new HttpHeaders();
//		HttpHeaders requestHeaders = createHttpHeader(MediaType.TEXT_PLAIN);
		requestHeaders.put("Radix", Arrays.asList("delete"));
		
//		DELETE Method cannot read body
		String requestBody = "body";
//		MultiValueMap<String, Object> requestBody = new LinkedMultiValueMap<String, Object>();
//		requestBody.add("body", Arrays.asList("body"));
		
		HttpEntity<Object> requestEntity = new HttpEntity<Object>(requestBody, requestHeaders);

		ResponseEntity<byte[]> entity = restTemplate.exchange(url, HttpMethod.DELETE, requestEntity, byte[].class);
		
		LOG.debug("statusCode={}", entity.getStatusCode());
		HttpHeaders headers = entity.getHeaders();
		for(String key : headers.keySet()) {
			LOG.debug("{}={}", key, headers.get(key));
		}
		LOG.debug("body={}", entity.getBody());
		LOG.debug("text={}", new String(entity.getBody(), headers.getContentType().getCharSet()));	
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
}
