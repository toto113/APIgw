package com.kthcorp.radix.component.adaptor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.net.URISyntaxException;
import java.util.Map;
import java.util.UUID;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.Logger;
import com.kthcorp.radix.util.UuidViewableLoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestOperations;

import com.kthcorp.radix.domain.canonical.reply.AdaptorRestReply;
import com.kthcorp.radix.domain.exception.AdaptorException;
import com.kthcorp.radix.domain.service.api.protocol.http.HttpMethod;

@RunWith(MockitoJUnitRunner.class)
public class RestExecuteComponentTest extends RestExecuteComponent {

	private final Logger LOG = UuidViewableLoggerFactory.getLogger(this.getClass());

	@Mock
	private RestOperations restTemplate;
	
	@InjectMocks
	private RestExecuteComponent restExecuteComponent;
	
	private String messageId;
	private String uri;
	private HttpMethod method;
	private MultiValueMap<String, Object> requestBody;
	private HttpHeaders requestHeaders;
	private String response;
	private byte[] responseBody;
	private ResponseEntity<byte[]> responseEntity;

	
	
	@Before
	public void init() {
		
		messageId = UUID.randomUUID().toString();
		uri = "http://localhost:8080";
		method = HttpMethod.GET;
		requestBody = new LinkedMultiValueMap<String, Object>();
		requestHeaders = new HttpHeaders();
		response = "SUCCESS";
		responseBody = response.getBytes();
		responseEntity = new ResponseEntity<byte[]>(responseBody, HttpStatus.OK);
	}
	
	
	@SuppressWarnings("unchecked")
	@Test
	public void doRequestTest() throws RestClientException, AdaptorException, URISyntaxException {
		
		//given

		//when
		when(restTemplate.exchange(any(String.class), any(org.springframework.http.HttpMethod.class), any(HttpEntity.class), same(byte[].class), any(Map.class))).thenReturn(responseEntity);
		
		//then
		AdaptorRestReply reply = restExecuteComponent.doRequest(messageId, uri, method, requestBody, requestHeaders);
		LOG.debug("reply={}", reply);
		LOG.debug("status={}", reply.getHttpStatus());
		LOG.debug("body={}", new String(reply.getBody()));
		verify(restTemplate).exchange(any(String.class), any(org.springframework.http.HttpMethod.class), any(HttpEntity.class), same(byte[].class), any(Map.class));
		assertSame(HttpStatus.OK, reply.getHttpStatus());
		assertEquals(response, new String(reply.getBody()));
	}
	
	
	@Test
	public void checkStatusTest() {

		assertEquals(Boolean.TRUE, checkStatus(HttpStatus.OK));
		assertEquals(Boolean.FALSE, checkStatus(HttpStatus.SERVICE_UNAVAILABLE));
	}
	
	@Test
	public void wrapHttpMethodTest() throws AdaptorException {
		
		assertEquals(org.springframework.http.HttpMethod.GET, wrapHttpMethod(HttpMethod.GET));
		assertEquals(org.springframework.http.HttpMethod.POST, wrapHttpMethod(HttpMethod.POST));
		assertEquals(org.springframework.http.HttpMethod.PUT, wrapHttpMethod(HttpMethod.PUT));
		assertEquals(org.springframework.http.HttpMethod.DELETE, wrapHttpMethod(HttpMethod.DELETE));
	}
	
	@Test
	public void getExceptionTest() {
		AdaptorException e = getException(new RestClientException("404 not found"));
		assertSame(HttpStatus.NOT_FOUND, e.getHttpStatus());
		assertEquals(ERROR_MESSAGE, e.getMessage());
	}
}
