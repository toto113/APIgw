package com.kthcorp.radix.component.adaptor;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import com.kthcorp.radix.util.UuidViewableLoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import com.kthcorp.radix.util.test.junit.MysqlEmbeddableSpringJUnit4ClassRunner;
import org.springframework.web.client.RestOperations;

@RunWith(MysqlEmbeddableSpringJUnit4ClassRunner.class)
@ContextConfiguration(
		locations = { "classpath:/META-INF/spring/spring-application-context.xml", 
				"classpath:/META-INF/spring/spring-service-component-context.xml"})
public class RestTemplateTestSkip extends AbstractJUnit4SpringContextTests {
	
	private final Logger LOG = UuidViewableLoggerFactory.getLogger(this.getClass());
	
	@Autowired
	@Qualifier("restTemplate")
	private RestOperations restTemplate;
	
	@Test
	public void testGetPng() {
		
		String url = "http://wiki.kthcorp.com/download/attachments/425986/global.logo";
		
		ResponseEntity<byte[]> entity = restTemplate.getForEntity(url, byte[].class);
		LOG.debug("entity={}", entity);
		
//		HttpHeaders requestHeaders = new HttpHeaders();
//		requestHeaders.set( "Accept", "*/*" );
//		ResponseEntity<byte[]> entity = restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<String>(requestHeaders), byte[].class);
		
		LOG.debug("statusCode={}", entity.getStatusCode());
		HttpHeaders headers = entity.getHeaders();
		MediaType mediaType = headers.getContentType();
		LOG.debug("mediaType={}", mediaType);
		LOG.debug("length={}", headers.getContentLength());
		LOG.debug("body={}", entity.getBody());
		
		save("/var/radix/downloaded_image.png", entity.getBody());
	}
	
	@Test
	public void testGetGif() {
		
		String url = "http://i.kthimg.com/TOP/new/bi_paran_default.gif";
		
		ResponseEntity<byte[]> entity = restTemplate.getForEntity(url, byte[].class);
		
//		HttpHeaders requestHeaders = new HttpHeaders();
//		requestHeaders.set( "Accept", "*/*" );
//		ResponseEntity<byte[]> entity = restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<String>(requestHeaders), byte[].class);
		
		LOG.debug("statusCode={}", entity.getStatusCode());
		HttpHeaders headers = entity.getHeaders();
		MediaType mediaType = headers.getContentType();
		LOG.debug("mediaType={}", mediaType);
		LOG.debug("length={}", headers.getContentLength());
		LOG.debug("body={}", entity.getBody());
		
		save("/var/radix/downloaded_image.gif", entity.getBody());
	}
	
	@Test
	public void testGetJson() {
		
		String url = "https://graph.facebook.com/1145951642";
		
		ResponseEntity<byte[]> entity = restTemplate.getForEntity(url, byte[].class);
		
//		HttpHeaders requestHeaders = new HttpHeaders();
//		requestHeaders.set( "Accept", "*/*" );
//		ResponseEntity<byte[]> entity = restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<String>(requestHeaders), byte[].class);
		
		HttpStatus httpStatus = entity.getStatusCode();
		LOG.debug("statusCode={}", httpStatus);
		HttpHeaders headers = entity.getHeaders();
//		for(String key : headers.keySet()) {
//			LOG.debug("{}={}", key, headers.get(key));
//		}
		LOG.debug("mediaType={}", headers.getContentType());
		Charset charSet = headers.getContentType().getCharSet();
		LOG.debug("charset={}", charSet);
		LOG.debug("length={}", headers.getContentLength());
		LOG.debug("raw={}", entity.getBody());
		LOG.debug("body={}", new String(entity.getBody(), headers.getContentType().getCharSet()));
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
