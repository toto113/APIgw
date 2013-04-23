package com.kthcorp.radix.component.adaptor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;
import java.util.UUID;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.Logger;
import com.kthcorp.radix.util.UuidViewableLoggerFactory;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import com.kthcorp.radix.domain.canonical.CanonicalMessage;
import com.kthcorp.radix.domain.canonical.CanonicalMessageHeader;
import com.kthcorp.radix.domain.canonical.CanonicalMessagePayload;
import com.kthcorp.radix.domain.canonical.reply.AdaptorRestReply;
import com.kthcorp.radix.domain.canonical.request.OrchestratorRestRequest;
import com.kthcorp.radix.domain.exception.AdaptorException;
import com.kthcorp.radix.domain.service.api.protocol.http.HttpMethod;
import com.kthcorp.radix.domain.service.api.protocol.http.ParameterMap;
import com.kthcorp.radix.util.FailedTest;

@RunWith(MockitoJUnitRunner.class)
public class RestServiceCallComponentTest extends RestServiceCallComponent {
	
	private final Logger LOG = UuidViewableLoggerFactory.getLogger(this.getClass());
	
	@Mock
	private RestExecuteComponent restExecuteComponent;
	
	@InjectMocks
	private RestServiceCallComponent restServiceCallComponent;
	
	private String messageId;
	private String uri;
	private String key;
	private String contentType;
	private HttpMethod method;
	private ParameterMap parameters;
	private MultiValueMap<String, Object> body;
	private ParameterMap headers;
	private AdaptorRestReply reply;
	private String response;
	private byte[] responseBody;
	
	public RestServiceCallComponentTest() {
	
	}

	@Before
	public void init() {
		
		messageId = UUID.randomUUID().toString();
		uri = "http://localhost:8080";
		key = "http://localhost:8080";
		contentType = "application/json";
		method = HttpMethod.GET;
		parameters = new ParameterMap();
		body = new LinkedMultiValueMap<String, Object>();
		headers = new ParameterMap();
		response = "SUCCESS";
		responseBody = response.getBytes();
		
		reply = new AdaptorRestReply();
		reply.setHttpStatus(HttpStatus.OK);
		reply.setHttpHeaders(new HttpHeaders());
		reply.setBody(responseBody);
		reply.setContentLength(responseBody.length);
		reply.setMediaType(MediaType.APPLICATION_JSON);
	}
	
	private CanonicalMessage makeCanonicalMessage() {
		
		CanonicalMessageHeader header = new CanonicalMessageHeader();
		header.setMessageId(messageId);
		
		OrchestratorRestRequest request = new OrchestratorRestRequest();
		request.setKey(key);
		request.setContentType(contentType);
		request.setHttpMethod(method);
		request.setUri(uri);
		request.setParameters(parameters);
		request.setBody(body);
		request.setHeaders(headers);
		
		CanonicalMessagePayload payload = new CanonicalMessagePayload();
		payload.setOrchestratorRequest(request);
		
		CanonicalMessage canonicalMessage = new CanonicalMessage();
		canonicalMessage.setHeader(header);
		canonicalMessage.setPayload(payload);
		
		return canonicalMessage;
	}
	
	@FailedTest
	public void testGet() throws AdaptorException {
		
		//given
		HttpMethod method = HttpMethod.GET;
		CanonicalMessage request = makeCanonicalMessage();
		HttpHeaders httpHeaders = createHeaders(headers);
		uri = formulateRestTemplateUrl(uri, parameters);
		
		//when
		when(restExecuteComponent.doRequest(messageId, uri, method, body, httpHeaders)).thenReturn(reply);
		
		//then
		CanonicalMessage resultCanonicalMessage = restServiceCallComponent.doCall(request);
		AdaptorRestReply resultReply = (AdaptorRestReply) resultCanonicalMessage.getPayload().getAdaptorReply();
		verify(restExecuteComponent).doRequest(messageId, uri, method, body, httpHeaders);
		assertSame(HttpStatus.OK, resultReply.getHttpStatus());
		assertEquals(response, new String(resultReply.getBody()));
	}
	
	@FailedTest
	public void testPost() throws AdaptorException, IOException {
		
		//given
		method = HttpMethod.POST;
		parameters.put("paramKey", "paramValue");
		headers.put("headerKey", Arrays.asList("headerValue"));
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
		CanonicalMessage request = makeCanonicalMessage();
		HttpHeaders httpHeaders = createHeaders(headers);
		uri = formulateRestTemplateUrl(uri, parameters);
		
		//when
		when(restExecuteComponent.doRequest(messageId, uri, method, body, httpHeaders)).thenReturn(reply);
				
		//then
		CanonicalMessage resultCanonicalMessage = restServiceCallComponent.doCall(request);
		AdaptorRestReply resultReply = (AdaptorRestReply) resultCanonicalMessage.getPayload().getAdaptorReply();
		verify(restExecuteComponent).doRequest(messageId, uri, method, body, httpHeaders);
		assertSame(HttpStatus.OK, resultReply.getHttpStatus());
		assertEquals(response, new String(resultReply.getBody()));
	}
	
	private byte[] readResource(String resourcePath) throws IOException {
		
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
			throw e;
		} finally {
			is.close();
		}
		return data;
	}
	
	@FailedTest
	public void testPut() throws AdaptorException {
		
		//given
		method = HttpMethod.PUT;
		parameters.put("paramKey", "paramValue");
		headers.put("headerKey", Arrays.asList("headerValue"));
		body.add("body", "body");
		body.add("list", "list1");
		body.add("list", "list2");
		CanonicalMessage request = makeCanonicalMessage();
		HttpHeaders httpHeaders = createHeaders(headers);
		uri = formulateRestTemplateUrl(uri, parameters);
		
		//when
		when(restExecuteComponent.doRequest(messageId, uri, method, body, httpHeaders)).thenReturn(reply);
				
		//then
		CanonicalMessage resultCanonicalMessage = restServiceCallComponent.doCall(request);
		AdaptorRestReply resultReply = (AdaptorRestReply) resultCanonicalMessage.getPayload().getAdaptorReply();
		verify(restExecuteComponent).doRequest(messageId, uri, method, body, httpHeaders);
		assertSame(HttpStatus.OK, resultReply.getHttpStatus());
		assertEquals(response, new String(resultReply.getBody()));
	}
	
	@FailedTest
	public void testDelete() throws AdaptorException {
		
		//given
		method = HttpMethod.DELETE;
		parameters.put("paramKey", "paramValue");
		headers.put("headerKey", Arrays.asList("headerValue"));
		CanonicalMessage request = makeCanonicalMessage();
		HttpHeaders httpHeaders = createHeaders(headers);
		uri = formulateRestTemplateUrl(uri, parameters);
		
		//when
		when(restExecuteComponent.doRequest(messageId, uri, method, body, httpHeaders)).thenReturn(reply);
				
		//then
		CanonicalMessage resultCanonicalMessage = restServiceCallComponent.doCall(request);
		AdaptorRestReply resultReply = (AdaptorRestReply) resultCanonicalMessage.getPayload().getAdaptorReply();
		verify(restExecuteComponent).doRequest(messageId, uri, method, body, httpHeaders);
		assertSame(HttpStatus.OK, resultReply.getHttpStatus());
		assertEquals(response, new String(resultReply.getBody()));
	}
	
	@FailedTest
	public void testNullBody() throws AdaptorException {
		
		//given
		body = null;
		CanonicalMessage request = makeCanonicalMessage();
		HttpHeaders httpHeaders = createHeaders(headers);
		
		//when
		when(restExecuteComponent.doRequest(messageId, uri, method, body, httpHeaders)).thenReturn(reply);
		
		//then
		CanonicalMessage resultCanonicalMessage = restServiceCallComponent.doCall(request);
		AdaptorRestReply resultReply = (AdaptorRestReply) resultCanonicalMessage.getPayload().getAdaptorReply();
		verify(restExecuteComponent).doRequest(messageId, uri, method, body, httpHeaders);
		assertSame(HttpStatus.OK, resultReply.getHttpStatus());
	}
	
	@Test
	public void testNullParameter() throws AdaptorException {
		
		//given
		parameters = null;
		CanonicalMessage request = makeCanonicalMessage();
		
		//when
		
		
		//then
		try {
			restServiceCallComponent.doCall(request);
		} catch(Exception e) {
			assertSame(IllegalArgumentException.class, e.getClass());
			assertEquals("parameters cannot be null", e.getMessage());
		}
	}
	
	@Test
	public void testNullHeader() throws AdaptorException {
		
		//given
		headers = null;
		CanonicalMessage request = makeCanonicalMessage();
		
		//when
		
		
		//then
		try {
			restServiceCallComponent.doCall(request);
		} catch(Exception e) {
			assertSame(IllegalArgumentException.class, e.getClass());
			assertEquals("header parameters cannot be null", e.getMessage());
		}
	}
	
	@FailedTest
	public void testFormulateRestTemplateUrl() throws AdaptorException {
		
		//given
		String nullValue = null;
		parameters.put("paramKey", Arrays.asList("paramValue", nullValue, "paramValue"));
		parameters.put("paramKey2", Arrays.asList("paramValue2"));
		CanonicalMessage request = makeCanonicalMessage();
		HttpHeaders httpHeaders = createHeaders(headers);
		uri = formulateRestTemplateUrl(uri, parameters);
		
		//when
		when(restExecuteComponent.doRequest(messageId, uri, method, body, httpHeaders)).thenReturn(reply);
		
		//then
		CanonicalMessage resultCanonicalMessage = restServiceCallComponent.doCall(request);
		AdaptorRestReply resultReply = (AdaptorRestReply) resultCanonicalMessage.getPayload().getAdaptorReply();
		verify(restExecuteComponent).doRequest(messageId, uri, method, body, httpHeaders);
		assertSame(HttpStatus.OK, resultReply.getHttpStatus());
		assertEquals(response, new String(resultReply.getBody()));
	}
	
	@FailedTest
	public void testUri() throws AdaptorException {
		
		//given
		uri = "http://localhost:8080?";
		parameters.put("paramKey", "paramValue");
		CanonicalMessage request = makeCanonicalMessage();
		HttpHeaders httpHeaders = createHeaders(headers);
		uri = formulateRestTemplateUrl(uri, parameters);
		
		//when
		when(restExecuteComponent.doRequest(messageId, uri, method, body, httpHeaders)).thenReturn(reply);
		
		//then
		CanonicalMessage resultCanonicalMessage = restServiceCallComponent.doCall(request);
		AdaptorRestReply resultReply = (AdaptorRestReply) resultCanonicalMessage.getPayload().getAdaptorReply();
		verify(restExecuteComponent).doRequest(messageId, uri, method, body, httpHeaders);
		assertSame(HttpStatus.OK, resultReply.getHttpStatus());
		assertEquals(response, new String(resultReply.getBody()));
	}
}
