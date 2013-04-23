package com.kthcorp.radix.component.adaptor;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import com.kthcorp.radix.util.UuidViewableLoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class RestServiceCallComponentController {
	
	protected final Logger LOG = UuidViewableLoggerFactory.getLogger(this.getClass());
	
	private static final Charset CHARSET = Charset.forName("utf-8");
	
	private HttpHeaders createHttpHeader(MediaType mediaType) {
		
		return createHttpHeader(mediaType, new HashMap<String, List<String>>());
	}
	
	private HttpHeaders createHttpHeader(MediaType mediaType, Map<String, List<String>> httpHeaders) {
		
		HttpHeaders headers = new HttpHeaders();
		Map<String, String> headerParameters = new HashMap<String, String>();
		headerParameters.put("charset", CHARSET.name());
		headers.setContentType(new MediaType(mediaType, headerParameters));
		for (Entry<String, List<String>> entry : httpHeaders.entrySet()) {
			List<String> values = Collections.unmodifiableList(entry.getValue());
			headers.put(entry.getKey(), values);
		}
		return headers;
	}
	
	@RequestMapping(value = "/echo")
	public ResponseEntity<byte[]> echo(HttpEntity<byte[]> requestEntity) throws IOException {

		LOG.debug("entity={}", requestEntity);
		LOG.debug("header={}", requestEntity.getHeaders());
		LOG.debug("body={}", requestEntity.getBody());
		ResponseEntity<byte[]> entity = new ResponseEntity<byte[]>(requestEntity.getBody(), requestEntity.getHeaders(), HttpStatus.OK);
		return entity;
	}
	
	@RequestMapping(value = "/gif", method=RequestMethod.GET)
	public ResponseEntity<byte[]> getGif(HttpEntity<byte[]> requestEntity) throws IOException {

		LOG.debug("entity={}", requestEntity);
		LOG.debug("header={}", requestEntity.getHeaders());
		LOG.debug("body={}", requestEntity.getBody());
		
		HttpHeaders headers = createHttpHeader(MediaType.IMAGE_GIF);
		byte[] body = readResource("image.gif");
		ResponseEntity<byte[]> entity = new ResponseEntity<byte[]>(body, headers, HttpStatus.OK);
		return entity;
	}
	
	@RequestMapping(value = "/png", method=RequestMethod.GET)
	public ResponseEntity<byte[]> getPng(HttpEntity<byte[]> requestEntity) throws IOException {

		LOG.debug("entity={}", requestEntity);
		LOG.debug("header={}", requestEntity.getHeaders());
		LOG.debug("body={}", requestEntity.getBody());
		
		HttpHeaders headers = createHttpHeader(MediaType.IMAGE_PNG);
		byte[] body = readResource("image.png");
		ResponseEntity<byte[]> entity = new ResponseEntity<byte[]>(body, headers, HttpStatus.OK);
		return entity;
	}
	
	@RequestMapping(value = "/js", method=RequestMethod.GET)
	public ResponseEntity<byte[]> getJs(HttpEntity<byte[]> requestEntity) throws IOException {

		LOG.debug("entity={}", requestEntity);
		LOG.debug("header={}", requestEntity.getHeaders());
		LOG.debug("body={}", requestEntity.getBody());
		
//		HttpHeaders headers = createHttpHeader(MediaType.TEXT_HTML);
		HttpHeaders headers = new HttpHeaders();
		byte[] body = readResource("jquery-1.6.1.min.js");
		ResponseEntity<byte[]> entity = new ResponseEntity<byte[]>(body, headers, HttpStatus.OK);
		return entity;
	}
	
	@RequestMapping(value = "/resource/{name}", method=RequestMethod.GET)
	public ResponseEntity<byte[]> getResource(HttpEntity<byte[]> requestEntity, @PathVariable String name) throws IOException {

		LOG.debug("entity={}", requestEntity);
		LOG.debug("header={}", requestEntity.getHeaders());
		LOG.debug("body={}", requestEntity.getBody());
		LOG.debug("name={}", name);
		
		HttpHeaders headers = new HttpHeaders();
		byte[] body = readResource(name);
		ResponseEntity<byte[]> entity = new ResponseEntity<byte[]>(body, headers, HttpStatus.OK);
		return entity;
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
