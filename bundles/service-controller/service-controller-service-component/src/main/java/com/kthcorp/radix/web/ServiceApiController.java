package com.kthcorp.radix.web;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.UUID;
import java.util.concurrent.Future;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.camel.CamelExecutionException;
import org.apache.camel.ProducerTemplate;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import com.kthcorp.radix.util.UuidViewableLoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartRequest;

import com.kthcorp.radix.api.service.ServiceManagerService;
import com.kthcorp.radix.domain.canonical.CanonicalMessage;
import com.kthcorp.radix.domain.canonical.CanonicalMessageHeader;
import com.kthcorp.radix.domain.canonical.CanonicalMessagePayload;
import com.kthcorp.radix.domain.canonical.RadixFileResource;
import com.kthcorp.radix.domain.canonical.reply.OrchestratorRestReply;
import com.kthcorp.radix.domain.canonical.request.ServiceControllerRequest;
import com.kthcorp.radix.domain.canonical.request.ServiceControllerRestRequest;
import com.kthcorp.radix.domain.exception.AdaptorException;
import com.kthcorp.radix.domain.exception.PolicyException;
import com.kthcorp.radix.domain.exception.PolicyExpiredException;
import com.kthcorp.radix.domain.exception.PolicyRateLimitException;
import com.kthcorp.radix.domain.exception.RadixException;
import com.kthcorp.radix.domain.exception.RequestResourceMissingException;
import com.kthcorp.radix.domain.exception.ValidateException;
import com.kthcorp.radix.domain.service.ServiceVersion;
import com.kthcorp.radix.domain.service.api.protocol.http.HttpMethod;
import com.kthcorp.radix.domain.service.api.protocol.http.ParameterMap;
import com.kthcorp.radix.transaction.RadixTransactionManager;
import com.kthcorp.radix.util.ApiKeyUtil;

@Controller
public class ServiceApiController {

	private final Logger LOG = UuidViewableLoggerFactory.getLogger(this.getClass());
	
	@SuppressWarnings("unused")
	private final Charset CHARSET;
	private String url;
	private final String charset = "utf-8";
	private ProducerTemplate producer;
	private ServiceManagerService serviceManagerService;
	private String platformDomain;
	private RadixTransactionManager radixTransactionManager;
	private String uploadPath;
	
	public ServiceApiController() {
		CHARSET = Charset.forName(this.charset);
	}
	
	public void setProducer(ProducerTemplate producer) {	
		this.producer = producer;
	}
	
	public void setUrl(String url) {
		this.url = url;
	}
	
	public void setServiceManagerService(ServiceManagerService serviceManagerService) {
		this.serviceManagerService = serviceManagerService;
	}
	
	public void setPlatformDomain(String platformDomain) {
		this.platformDomain = platformDomain;
	}
	
	public void setRadixTransactionManager(RadixTransactionManager radixTransactionManager) {
		this.radixTransactionManager = radixTransactionManager;
	}
	
	public void setUploadPath(String uploadPath) {
		this.uploadPath = uploadPath;
	}
	
	@RequestMapping(value = "/**")
	public ResponseEntity<byte[]> doService(HttpServletRequest request) throws IOException, RadixException {
		
		LOG.debug("doService : {}", request);
		return executeService(request, new ArrayList<MultipartFile>());
	}
	
	@RequestMapping(value = "/**", method=RequestMethod.POST, headers="content-type=multipart/*")
	public ResponseEntity<byte[]> doService(MultipartRequest request) throws IOException, RadixException {
		
		LOG.debug("doServiceMultipart : {}", request);
		
		List<MultipartFile> files = new ArrayList<MultipartFile>();
		Iterator<String> em = request.getFileNames();
		while(em.hasNext()) {
			String fileName = em.next();
			MultipartFile file = request.getFile(fileName);
			LOG.debug("file : fileName={}, originalName={}", file.getName(), file.getOriginalFilename());
			LOG.debug("contentType={}, size={}", file.getContentType(), file.getSize());
			files.add(file);
		}
		return executeService((HttpServletRequest)request, files);
	}
	
	private ResponseEntity<byte[]> executeService(HttpServletRequest request, List<MultipartFile> files) throws IOException, RadixException {
		
		LOG.debug("executeService : {}, {}", request, files);
		
		CanonicalMessage canonicalMessage = makeCanonicalMessage((HttpServletRequest)request, files);
		Future<Object> future = this.producer.asyncRequestBody(this.url, canonicalMessage);
		CanonicalMessage replyCanonicalMessage = this.producer.extractFutureBody(future, CanonicalMessage.class);
		CanonicalMessagePayload payload = replyCanonicalMessage.getPayload();
		OrchestratorRestReply reply = (OrchestratorRestReply) payload.getReply();
	
		HttpHeaders headers = reply.getHttpHeaders();
		if(reply.getMediaType()!=null) {
			try {
				headers.setContentType(reply.getMediaType());
			} catch(UnsupportedOperationException e) {
				// ignore. 어떤 경우 전달된 header 클래스는 수정이 불가한 unmodifiable일 수 있다. 걍 무시.
			}
		}
		
		ResponseEntity<byte[]> entity = new ResponseEntity<byte[]>(reply.getBody(), reply.getHttpHeaders(), HttpStatus.OK);
		return entity;
	}
	
	protected final CanonicalMessage makeCanonicalMessage(HttpServletRequest request, List<MultipartFile> files) throws ValidateException, IOException {
		
		String clientKey = (String) request.getAttribute("clientKey");
		String userName = (String) request.getAttribute("userName");
		LOG.debug("clientKey={}", clientKey);
		LOG.debug("userName={}", userName);
		
		if(clientKey==null) { throw new ValidateException("clientKey cannot be null"); }
		// userName은 없을 수도 있다. 인증하지 않은 /photos/popular와 같은 경우
//		if(userName==null) { throw new ValidateException("userName cannot be null"); }
		
		//canonical header
		CanonicalMessageHeader header = new CanonicalMessageHeader();
		String messageId = radixTransactionManager.getTransactionId();
		header.setMessageId(messageId);
		Map<String, String> properties = new HashMap<String, String>();
		properties.put("clientKey", clientKey);
		properties.put("userName", userName);
		header.setHeaderProperties(properties);
		
		//meta infomation
		ServiceControllerRestRequest serviceControllerRequest = (ServiceControllerRestRequest) makeServiceControllerRequest(request, files);
		serviceControllerRequest.getParameters().put("Radix.System.Meta.client_key", clientKey);
		serviceControllerRequest.getParameters().put("Radix.System.Meta.user_name", userName);
		
		//canonical payload
		CanonicalMessagePayload payload = new CanonicalMessagePayload();
		payload.setRequest(serviceControllerRequest);
		
		CanonicalMessage canonicalMessage = new CanonicalMessage();
		canonicalMessage.setHeader(header);
		canonicalMessage.setPayload(payload);
		
		return canonicalMessage;
	}

	protected final ServiceControllerRequest makeServiceControllerRequest(HttpServletRequest request, List<MultipartFile> files) throws ValidateException, IOException {
		
		String method = request.getMethod();
		String path = request.getRequestURI();
		String query = request.getQueryString();
		
		LOG.debug("method={}", method);
		LOG.debug("path={}", path);
		LOG.debug("query={}", query);
		
		ServiceControllerRestRequest serviceControllerRequest = new ServiceControllerRestRequest();
		HttpMethod httpMethod = HttpMethod.valueOf(method.toUpperCase());

		//set key
		String[] pathArray = path.split("/");
		if(pathArray.length >= 4) {
			ServiceVersion serviceVersion = null;
			try {
				serviceVersion = new ServiceVersion(pathArray[2]);
				LOG.debug("get service version : {}", pathArray[2]);
			} catch(NumberFormatException e) {
				throw new ValidateException("Error in service version");
			}
			
			serviceControllerRequest.setKey(serviceManagerService.getURIString(platformDomain, httpMethod, pathArray[1], serviceVersion, pathArray[3]));	
		} else {
			throw new RequestResourceMissingException("no resource in request. path="+path);
		}
		
	
		String resourcePath = ApiKeyUtil.getResourcePath(path);
		serviceControllerRequest.setResourcePath(resourcePath);
		
		//set header
		ParameterMap headers = parseHeader(request);
		serviceControllerRequest.setHeaders(headers);
		
		//set query parameters
		ParameterMap queryParam = 
		parseQuery(request.getQueryString(), request.getCharacterEncoding() == null ? this.charset : request.getCharacterEncoding());
		serviceControllerRequest.setParameters(queryParam);
		
		//set body parameters
		MultiValueMap<String, Object> body = new LinkedMultiValueMap<String, Object>();
		ParameterMap formParam = parseForm(request, queryParam);
		for(String name : formParam.keys()) {
			for(String value : formParam.get(name)) {
				body.add(name, value);
			}
		}
		
		//set multipart files
		for(MultipartFile file : files) {
			String filePath = save(file);
			RadixFileResource resource = new RadixFileResource(file.getOriginalFilename(), filePath);
			body.add(file.getName(), resource);
		}
		if(files.size() > 0) {
			headers.remove("Content-Type");
			headers.put("Content-Type", MediaType.MULTIPART_FORM_DATA.toString());
		}
		
		serviceControllerRequest.setBody(body);
		return serviceControllerRequest;
	}
	
	protected final String save(MultipartFile multipartFile) throws IllegalStateException, IOException {

		String fileName = multipartFile.getOriginalFilename();
		String extension = fileName.substring(fileName.indexOf(".") + 1);
		fileName = UUID.randomUUID().toString();
		File file = new File(uploadPath.concat(UUID.randomUUID().toString()).concat(".").concat(extension));
		while (file.exists()) {
			file = new File(uploadPath.concat(UUID.randomUUID().toString()).concat(".").concat(extension));
		}
		multipartFile.transferTo(file);
		return file.getAbsolutePath();
	}

	@SuppressWarnings("rawtypes")
	protected final ParameterMap parseHeader(HttpServletRequest request) {
		
		ParameterMap map = new ParameterMap();
		Enumeration em = request.getHeaderNames();
		while(em.hasMoreElements()) {
			String name = (String) em.nextElement();
			map.put(name, request.getHeader(name));
		}
		return map;
	}
	
	protected final ParameterMap parseQuery(String queryString, String charset) {
		
		ParameterMap map = new ParameterMap();
		if(queryString == null) {
			return map;
		}
		List<NameValuePair> parameters = new ArrayList<NameValuePair>();
		URLEncodedUtils.parse(parameters, new Scanner(queryString), charset);
		
		for(NameValuePair pair : parameters) {
			map.put(pair.getName(), pair.getValue());
		}
		return map;
	}
	
	@SuppressWarnings("rawtypes")
	protected final ParameterMap parseForm(HttpServletRequest request, ParameterMap queryParam) {
		
		ParameterMap map = new ParameterMap();
		Enumeration em = request.getParameterNames();
		while(em.hasMoreElements()) {
			String name = (String) em.nextElement();
			List<String> values = new ArrayList<String>();
			values.addAll(Arrays.asList(request.getParameterValues(name)));
			LOG.debug("{}={}", name, values);
			if(queryParam.containsKey(name)) {
				for(String value : queryParam.get(name)) {
					values.remove(value);
				}
			}
			LOG.debug("{}={}", name, values);
			if(values.size() > 0) {
				map.put(name, values);
			}
		}
		return map;
	}
	
	@ExceptionHandler({ PolicyException.class, PolicyExpiredException.class, PolicyRateLimitException.class })
	public ResponseEntity<String> handlePolicyException(HttpServletRequest request, HttpServletResponse response, RadixException radixException) throws IOException {
		LOG.error("RequestResourceMissingException, RequestInvalidVersionException {} : {}", radixException.getHttpStatus() + ", " + radixException.getMessage(), radixException.getStackTrace());
		
		Logger tempnotiLogger = UuidViewableLoggerFactory.getLogger("tempnoti");
		
		/*
		 * format  날짜/시간/Client_KEY/API_ID/Reason
		 */

		final String delimeter = "\t";
		
		StringBuffer sb = new StringBuffer();
		
		sb.append(radixException.getOccuredDateAsString("yyyy-MM-dd")).append(delimeter);
		sb.append(radixException.getOccuredDateAsString("HH:mm:ss")).append(delimeter);
		sb.append(request.getAttribute("clientKey")).append(delimeter);
		sb.append(request.getAttribute("serviceApiName")).append(delimeter);
		sb.append(radixException.getStatus());
		
		tempnotiLogger.info(sb.toString());
		
		// TODO : 시스템 내부의 오류 정보를 사용자 Response 에 설정하는 Message 에 대한 규칙 정의와 적용이 필요함
		return new ResponseEntity<String>(getJsonFormattedMessage(radixException), null, radixException.getHttpStatus());
	}
	
	@ExceptionHandler({ RequestResourceMissingException.class, ValidateException.class })
	public ResponseEntity<String> handleRequestResourceMissingException(HttpServletRequest request, HttpServletResponse response, RadixException radixException) throws IOException {
		LOG.error("RequestResourceMissingException, RequestInvalidVersionException {} : {}", radixException.getHttpStatus() + ", " + radixException.getMessage(), radixException.getStackTrace());
		return new ResponseEntity<String>(getJsonFormattedMessage(radixException), null, radixException.getHttpStatus());
	}
	
	@ExceptionHandler({ RadixException.class })
	public ResponseEntity<String> handleRadixException(HttpServletRequest request, HttpServletResponse response, RadixException radixException) throws IOException {
		LOG.error("RadixException {} : {}", radixException.getHttpStatus() + ", " + radixException.getMessage(), radixException.getStackTrace());
		return new ResponseEntity<String>(getJsonFormattedMessage(radixException), null, radixException.getHttpStatus());
	}
	
	@ExceptionHandler({ CamelExecutionException.class })
	public ResponseEntity<String> handleCamelExecutionException(HttpServletRequest request, HttpServletResponse response, CamelExecutionException e) throws IOException {
		LOG.error("CamelExecutionException {} : {}", e.getMessage(), e.getStackTrace());
		Throwable cause = e.getCause();
		LOG.error("CamelExecutionException.getCause={}", cause);
		if(cause instanceof AdaptorException) {
			AdaptorException adaptorException = (AdaptorException) cause;
			LOG.error("AdaptorException {} : {}", adaptorException.getHttpStatus() + ", " + adaptorException.getMessage(), adaptorException.getStackTrace());
			return new ResponseEntity<String>(getJsonFormattedMessage(e), null, adaptorException.getHttpStatus());
		} else if(cause instanceof RadixException) {
			RadixException radixException = (RadixException) cause;
			LOG.error("RadixException {} : {}", radixException.getHttpStatus() + ", " + radixException.getMessage(), radixException.getStackTrace());
			return new ResponseEntity<String>(getJsonFormattedMessage(e), null, radixException.getHttpStatus());
		} else {
			LOG.error("Exception {} : {}", cause.getMessage(), cause.getStackTrace());
			return new ResponseEntity<String>(getJsonFormattedMessage(e), null, HttpStatus.SERVICE_UNAVAILABLE);
		}
	}
	
	@ExceptionHandler({ Exception.class })
	public ResponseEntity<String> handleRadixException(HttpServletRequest request, HttpServletResponse response, Exception exception) throws IOException {
		LOG.error("Exception {} : {}", exception.getMessage(), exception.getStackTrace());
		return new ResponseEntity<String>(exception.getMessage(), null, HttpStatus.SERVICE_UNAVAILABLE);
	}
	
	private String getJsonFormattedMessage(Exception e) {
		Throwable cause = getRootCause(e);

		String message = "undefined";
		if(cause instanceof HttpServerErrorException) {
			message = ((HttpServerErrorException)cause).getResponseBodyAsString();
		}
		else {
			try {
				String exceptionMessage = cause.getMessage();
				JSONObject jsonObject = new JSONObject();
				jsonObject.put("errorMessage", exceptionMessage);
				message = jsonObject.toString();
			} catch (JSONException jsonException) {
				LOG.error("creating json object failed.", jsonException);
			}
		}

		return message;
	}


	private Throwable getRootCause(Throwable e) {
		Throwable cause = e.getCause();
		if(cause==null) { return e; }
		return getRootCause(cause);
	}
}
