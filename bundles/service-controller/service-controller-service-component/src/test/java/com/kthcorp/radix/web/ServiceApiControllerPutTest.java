package com.kthcorp.radix.web;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.nio.charset.Charset;

import javax.servlet.ServletException;

import org.apache.camel.ProducerTemplate;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.Logger;
import com.kthcorp.radix.util.UuidViewableLoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;

import com.kthcorp.radix.api.service.ServiceManagerService;
import com.kthcorp.radix.domain.canonical.CanonicalMessage;
import com.kthcorp.radix.domain.canonical.CanonicalMessagePayload;
import com.kthcorp.radix.domain.canonical.reply.OrchestratorRestReply;
import com.kthcorp.radix.domain.exception.RadixException;
import com.kthcorp.radix.domain.exception.RequestResourceMissingException;
import com.kthcorp.radix.domain.exception.ValidateException;
import com.kthcorp.radix.domain.service.ServiceVersion;
import com.kthcorp.radix.domain.service.api.protocol.http.HttpMethod;
import com.kthcorp.radix.transaction.RadixTransactionManager;
import com.kthcorp.radix.util.FailedTest;

@RunWith(MockitoJUnitRunner.class)
public class ServiceApiControllerPutTest {

	private final Logger LOG = UuidViewableLoggerFactory.getLogger(this.getClass());
	
	@Mock
	private ProducerTemplate producer;
	
	@Mock
	private ServiceManagerService serviceManagerService;
	
	@Mock
	private RadixTransactionManager radixTransactionManager;
	
	@InjectMocks
	private ServiceApiController controller;
	
	private String url = "seda:request";
	private String platformDomain = "api.withapi.com";
	
	private MockHttpServletRequest request;
	private CanonicalMessage canonicalMessage;
	private String message;
	MediaType contentType;
	
	@Before
	public void init() throws ValidateException {
		
		//given
		controller.setProducer(producer);
		controller.setServiceManagerService(serviceManagerService);
		controller.setPlatformDomain(this.platformDomain);
		controller.setUrl(this.url);
		controller.setRadixTransactionManager(radixTransactionManager);
		
		//when
		request = new MockHttpServletRequest();
		request.setMethod("PUT");
		request.setRequestURI("/MapAPI/1/map");
		request.addHeader("Authorization", "Bearer 297b9e64-2d3b-4e2c-a1fb-9a0cb7a59f47");
		request.setAttribute("clientKey", "someClientKey");
		request.setAttribute("userName", "someUserName");
		request.setQueryString("query1=query-value1&query2=query-value2&query3=query-value3");
		request.setContentType("application/x-www-form-urlencoded");
		request.addParameter("parameter1", "parameter-value1");
		request.addParameter("parameter2", "parameter-value2");
		request.addParameter("parameter3", "parameter-value3");
		
		message = "test success";
		contentType = new MediaType("application", "json", Charset.forName("utf-8"));
		
		canonicalMessage = new CanonicalMessage();
		CanonicalMessagePayload payload = new CanonicalMessagePayload();
		OrchestratorRestReply reply = new OrchestratorRestReply();
		reply.setHttpStatus(HttpStatus.OK);
		reply.setHttpHeaders(new HttpHeaders());
		reply.setMediaType(contentType);
		reply.setContentLength(message.getBytes().length);
		reply.setBody(message.getBytes());
		payload.setReply(reply);
		canonicalMessage.setPayload(payload);
	}
	
	@Test
	public void doServiceTest() throws ServletException, IOException, RadixException {
		
		//given
		
		//when
		when(serviceManagerService.getURIString(platformDomain, HttpMethod.PUT, "MapAPI", new ServiceVersion("1"), "map")).thenReturn("api.withapi.com::PUT:/MapAPI/1/map");
		when(producer.extractFutureBody(null, CanonicalMessage.class)).thenReturn(canonicalMessage);
		
		//then
		ResponseEntity<byte[]> response = controller.doService(request);
		LOG.debug("response={}, {}", response, new String(response.getBody()));
		assertEquals(message, new String(response.getBody()));
		assertEquals(contentType, response.getHeaders().getContentType());
		verify(producer).extractFutureBody(null, CanonicalMessage.class);
	}
	
	@Test
	public void doServiceWithoutCharsetTest() throws ServletException, IOException, RadixException {
		
		//given
		
		//when
		when(serviceManagerService.getURIString(platformDomain, HttpMethod.PUT, "MapAPI", new ServiceVersion("1"), "map")).thenReturn("api.withapi.com::PUT:/MapAPI/1/map");
		MediaType contentType = new MediaType("application", "json");
		((OrchestratorRestReply)canonicalMessage.getPayload().getReply()).setMediaType(new MediaType("application", "json"));
		when(producer.extractFutureBody(null, CanonicalMessage.class)).thenReturn(canonicalMessage);
		
		//then
		ResponseEntity<byte[]> response = controller.doService(request);
		LOG.debug("response={}, {}", response, new String(response.getBody()));
		assertEquals(message, new String(response.getBody()));
		assertEquals(contentType, response.getHeaders().getContentType());
		verify(producer).extractFutureBody(null, CanonicalMessage.class);
	}
	
	@Test
	public void doServiceLongPathTest() throws ServletException, IOException, RadixException {
		
		//given
		request.setRequestURI("/MapAPI/1/map/addtional");
		
		//when
		when(serviceManagerService.getURIString(platformDomain, HttpMethod.PUT, "MapAPI", new ServiceVersion("1"), "map")).thenReturn("api.withapi.com::PUT:/MapAPI/1/map");
		when(producer.extractFutureBody(null, CanonicalMessage.class)).thenReturn(canonicalMessage);
		
		//then
		ResponseEntity<byte[]> response = controller.doService(request);
		LOG.debug("response={}, {}", response, new String(response.getBody()));
		assertEquals(message, new String(response.getBody()));
		assertEquals(contentType, response.getHeaders().getContentType());
		verify(producer).extractFutureBody(null, CanonicalMessage.class);
	}
	
	@Test
	public void doServiceWithoutQueryTest() throws ServletException, IOException, RadixException {
		
		//given
		request.setQueryString(null);
		
		//when
		when(serviceManagerService.getURIString(platformDomain, HttpMethod.PUT, "MapAPI", new ServiceVersion("1"), "map")).thenReturn("api.withapi.com::PUT:/MapAPI/1/map");
		when(producer.extractFutureBody(null, CanonicalMessage.class)).thenReturn(canonicalMessage);
		
		//then
		ResponseEntity<byte[]> response = controller.doService(request);
		LOG.debug("response={}, {}", response, new String(response.getBody()));
		assertEquals(message, new String(response.getBody()));
		assertEquals(contentType, response.getHeaders().getContentType());
		verify(producer).extractFutureBody(null, CanonicalMessage.class);
	}
	
	@FailedTest
	public void doServiceShortPathTest() throws ServletException, IOException, RadixException {
		
		//given
		request.setRequestURI("/MapAPI/1");
		
		//when

		//then
		try {
			ResponseEntity<byte[]> response = controller.doService(request);
			LOG.debug("response={}, {}", response, new String(response.getBody()));
		} catch(Exception e) {
			assertSame(RequestResourceMissingException.class, e.getClass());
			assertEquals("no resource in request", e.getMessage());
		}
	}
	
	@Test
	public void doServiceWrongVersionTest() throws ServletException, IOException, RadixException {
		
		//given
		request.setRequestURI("/MapAPI/NAN/map");
		
		//when

		//then
		try {
			ResponseEntity<byte[]> response = controller.doService(request);
			LOG.debug("response={}, {}", response, new String(response.getBody()));
		} catch(Exception e) {
			assertSame(ValidateException.class, e.getClass());
			assertEquals("Error in service version", e.getMessage());
		}
	}
}
