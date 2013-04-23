package com.kthcorp.radix.component.adaptor;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import com.kthcorp.radix.util.UuidViewableLoggerFactory;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestOperations;

import com.kthcorp.radix.domain.canonical.RadixFileResource;
import com.kthcorp.radix.domain.canonical.reply.AdaptorRestReply;
import com.kthcorp.radix.domain.exception.AdaptorException;
import com.kthcorp.radix.domain.service.api.protocol.http.HttpMethod;
import com.kthcorp.radix.domain.service.api.protocol.http.ParameterMap;

public class RestExecuteComponent {

	private final Logger LOG = UuidViewableLoggerFactory.getLogger(this.getClass());

	private RestOperations restTemplate;
	public void setRestTemplate(RestOperations restTemplate) {
		this.restTemplate = restTemplate;
	}

	public AdaptorRestReply doRequest(String messageId, String uri, HttpMethod method, MultiValueMap<String, Object> requestBody, HttpHeaders requestHeaders) throws AdaptorException {
		ParameterMap parameterMap = new ParameterMap();
		return doRequest(messageId, uri, method, requestBody, requestHeaders, parameterMap);
	}

	public AdaptorRestReply doRequest(String messageId, String uri, HttpMethod method, MultiValueMap<String, Object> requestBody, HttpHeaders requestHeaders, ParameterMap parameterMap) throws AdaptorException {

		// 여기에 담긴 Host 헤더값은 Radix를 호출할 때의 것이다. 잘못된 값보다는 없는 것이 낳다.
		requestHeaders.remove("Host");

		Map<String, String> uriVariables = new Hashtable<String, String>();
		for(String key : parameterMap.keys()) {
			List<String> valueList = parameterMap.get(key);
			String value = "";
			if(valueList.size()>0) {
				value = valueList.get(0);
			}
			if(value==null) { value=""; }
			uriVariables.put(key.toLowerCase(), value);
		}
		
		uri = makeLowerCase(uri);
		uri = expandWithUriVariables(uri, uriVariables);
		
		LOG.trace("REST Adaptor >>> RestExecuteComponent.doRequest(..)");
		LOG.debug("messageId={}", messageId);
		LOG.debug("uri={}", uri);
		LOG.debug("method={}", method);
		LOG.debug("headers={}", requestHeaders);
		LOG.debug("contentType={}", requestHeaders.getContentType());
		LOG.debug("isMultipart={}", MediaType.MULTIPART_FORM_DATA.equals(requestHeaders.getContentType()));
		LOG.debug("body={}", requestBody);
		LOG.debug("parameterMap={}", parameterMap);


		if(MediaType.MULTIPART_FORM_DATA.equals(requestHeaders.getContentType())) {
			wrapMultipartFileWithByteArray(requestBody);
		}

		
		HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<MultiValueMap<String, Object>>(requestBody, requestHeaders);
		AdaptorRestReply reply = new AdaptorRestReply();
		ResponseEntity<byte[]> entity = null;

		try {
			if(!requestHeaders.containsKey("bypass")) {
				entity = restTemplate.exchange(uri, wrapHttpMethod(method), requestEntity, byte[].class, uriVariables);
			} else {
				Charset CHARSET = Charset.forName("utf-8");
				HttpHeaders headers = new HttpHeaders();
				Map<String, String> headerParameters = new HashMap<String, String>();
				headerParameters.put("charset", CHARSET.name());
				headers.setContentType(new MediaType(MediaType.TEXT_PLAIN, headerParameters));
				entity = new ResponseEntity<byte[]>("BYPASS".getBytes(CHARSET), headers, HttpStatus.OK);
			}
		} catch(RestClientException e) {
			AdaptorException ex = getException(e);
			throw ex;
		} catch(Exception e) {
			AdaptorException ex = getException(e);
			throw ex;
		}

		if(entity == null) {
			throw new AdaptorException(HttpStatus.SERVICE_UNAVAILABLE, "response is empty", null);
		}

		if(!checkStatus(entity.getStatusCode())) {
			String body = "";
			try {
				if(entity.getBody()!=null) {
					body = new String(entity.getBody());
				}
			} catch(Exception e) {
				// ignore
			}
			throw new AdaptorException(entity.getStatusCode(), body, null);
		}

		reply.setHttpStatus(entity.getStatusCode());
		reply.setHttpHeaders(entity.getHeaders());
		if(entity.getBody() == null) {
			reply.setBody(new byte[0]);
		} else {
			reply.setBody(entity.getBody());
		}
		HttpHeaders headers = entity.getHeaders();
		reply.setContentLength(headers.getContentLength());
		reply.setMediaType(headers.getContentType());

		return reply;
	}

	private String makeLowerCase(String uri) {
		return uri.toLowerCase();
	}
	
	private String expandWithUriVariables(String uri, Map<String, String> uriVariables) {
		// uri나 uriVariables의 key 전부 소문자인 것을 전제로 한다.
		List<String> toExpandedKeyList = new ArrayList<String>();
		for(String key : uriVariables.keySet()) {
			if(key.startsWith("radix.system")) { continue; }
			if(!uri.contains(key+"=")) { 
				toExpandedKeyList.add(key);
			}
		}
		
		if(toExpandedKeyList.isEmpty()) { return uri; }
		StringBuilder sb = new StringBuilder();
		sb.append(uri);
		boolean hasQuestionMark = false;
		boolean haQueryParameter = false;
		if(uri.contains("?")) {
			hasQuestionMark = true;
			if(!uri.endsWith("?")) {
				haQueryParameter = true;
			}
		}
		
		if(!hasQuestionMark) { sb.append("?"); }
		for(String key : toExpandedKeyList) {
			if(haQueryParameter) { sb.append("&"); }
			sb.append(key).append("={").append(key).append("}");
			haQueryParameter = true;
		}
		
	
		return sb.toString();
	}
	
	
	protected final void wrapMultipartFileWithByteArray(MultiValueMap<String, Object> requestBody) throws AdaptorException {

		LOG.debug("wrapMultipartFileWithByteArray");
		for(String key : requestBody.keySet()) {
			Object body = requestBody.getFirst(key);
			if(body instanceof RadixFileResource) {
				RadixFileResource radixFileResource = (RadixFileResource) body;
				ByteArrayResource resource = null;
				FileInputStream fileInputStream = null;
				FileChannel fileChannel = null;
				ByteBuffer byteBuffer = null;
				try {
					fileInputStream = new FileInputStream(radixFileResource.getResource());
					fileChannel = fileInputStream.getChannel();
					byteBuffer = ByteBuffer.allocate((int) fileChannel.size());
					fileChannel.read(byteBuffer);
					resource = new ByteArrayResource(byteBuffer.array(), radixFileResource.getOriginalFilename()) {
						@Override
						public String getFilename() throws IllegalStateException {
							return this.getDescription();
						}
					};
				} catch(FileNotFoundException e) {
					throw new AdaptorException(HttpStatus.SERVICE_UNAVAILABLE, "cannot found multipart file", e);
				} catch(IOException e) {
					throw new AdaptorException(HttpStatus.SERVICE_UNAVAILABLE, "cannot read multipart file", e);
				} finally {
					try {
						if(fileInputStream != null) fileInputStream.close();
					} catch (IOException e) {
						LOG.warn("fail to close stream : {}", fileInputStream, e);
					}
					try {
						if(fileChannel != null) fileChannel.close();
					} catch (IOException e) {
						LOG.warn("fail to close channel : {}", fileChannel, e);
					}
					if(byteBuffer != null) byteBuffer.clear();
					new File(radixFileResource.getResource()).delete();
				}
				requestBody.remove(key);
				requestBody.add(key, resource);
			}
		}
	}

	protected final boolean checkStatus(HttpStatus httpStatus) {

		if(httpStatus == HttpStatus.OK) {
			return true;
		} else {
			return false;
		}
	}

	protected final org.springframework.http.HttpMethod wrapHttpMethod(HttpMethod method) throws AdaptorException {

		if(method == HttpMethod.GET) {
			return org.springframework.http.HttpMethod.GET;
		} else if(method == HttpMethod.POST){
			return org.springframework.http.HttpMethod.POST;
		} else if(method == HttpMethod.PUT) {
			return org.springframework.http.HttpMethod.PUT;
		} else if(method == HttpMethod.DELETE) {
			return org.springframework.http.HttpMethod.DELETE;
		} else {
			throw new AdaptorException(HttpStatus.BAD_REQUEST, "not supported http request", null);
		}
	}

	protected final AdaptorException getException(Exception ex) {	
		return getException(ex, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	protected static final String ERROR_MESSAGE = "partner request has failed";
	protected final AdaptorException getException(Exception ex, HttpStatus status) {

		String message = ex.getMessage();
		HttpStatus errorStatus = null;

		if(message.indexOf(' ') > 0) {
			try {
				int errorCode = Integer.parseInt(message.substring(0, message.indexOf(' ')));
				errorStatus = HttpStatus.valueOf(errorCode);
				message = message.substring(message.indexOf(' ') + 1);
			} catch(NumberFormatException e) {
				errorStatus = status;
			}
		}

		AdaptorException exception = new AdaptorException(errorStatus, ERROR_MESSAGE, ex);
		return exception;
	}
}
