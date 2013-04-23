package com.kthcorp.radix.web;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.xml.xpath.XPathExpressionException;

import org.json.JSONException;
import org.slf4j.Logger;
import com.kthcorp.radix.util.UuidViewableLoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.w3c.dom.DOMException;
import org.xml.sax.SAXException;

import com.kthcorp.radix.api.service.BusinessPlatformKeyManagerService;
import com.kthcorp.radix.domain.RequestBodyMap;
import com.kthcorp.radix.domain.RequestResource;
import com.kthcorp.radix.domain.exception.DataBaseProcessingException;
import com.kthcorp.radix.domain.exception.DataProcessingException;
import com.kthcorp.radix.domain.exception.MethodFailureException;
import com.kthcorp.radix.domain.exception.NotAllowedException;
import com.kthcorp.radix.domain.exception.NotSupportException;
import com.kthcorp.radix.domain.exception.RadixException;
import com.kthcorp.radix.domain.exception.RequestResourceMissingException;
import com.kthcorp.radix.domain.exception.ValidateException;
import com.kthcorp.radix.domain.platform.api.ResponseMessage;
import com.kthcorp.radix.domain.platform.api.ResponseMessageHeader;
import com.kthcorp.radix.service.PlatformServiceFactory;
import com.kthcorp.radix.util.JsonBuilder;
import com.kthcorp.radix.util.UUIDUtils;
import com.kthcorp.radix.web.util.RequestBodyMapUtil;

/**
 * 
 <bean id="platformApiController" class="com.kthcorp.radix.web.PlatformApiController"
 * p:clientKeyManagerService-ref="clientKeyManagerService"/>
 * 
 */
@Controller("platformApiController")
public class PlatformApiController {
	
	/* Property */
	private static final Logger LOG = UuidViewableLoggerFactory.getLogger(PlatformApiController.class);
	
	@Autowired
	private BusinessPlatformKeyManagerService businessPlatformKeyManagerService;
	
	private PlatformServiceFactory platformServiceFactory;
	
	public void setPlatformServiceFactory(PlatformServiceFactory platformServiceFactory) {
		this.platformServiceFactory = platformServiceFactory;
	}
	
	public void setBusinessPlatformKeyManagerService(BusinessPlatformKeyManagerService businessPlatformKeyManagerService) {
		this.businessPlatformKeyManagerService = businessPlatformKeyManagerService;
	}
	
	@RequestMapping(value = "/platform/**")
	public ResponseEntity<String> doService(HttpServletRequest request, HttpSession session) throws RequestResourceMissingException, MethodFailureException, XPathExpressionException, ValidateException, DataBaseProcessingException, JSONException, SAXException, IOException, NotAllowedException, NotSupportException, DOMException, NoSuchAlgorithmException, DataProcessingException {
		if(LOG.isDebugEnabled()) {
			LOG.debug("got new request. request={}", InnerUtil.toString(request));
		}
		
		String uri = request.getRequestURI();
		
		if(LOG.isDebugEnabled()) {
			LOG.debug("Request Information");
			LOG.debug("URI : " + uri);
			LOG.debug("Method : " + request.getMethod());
		}
		
		String resource = null;
		
		RequestResource<String> parameterResource = new RequestResource<String>();
		
		// EXAMPLE : uri = "/platform/clientKeys/package?some=123&other=456"
		if(StringUtils.hasText(uri)) {
			String[] part = uri.split("\\?");
			String[] resources = part[0].split("/");
			if(resources.length < 3) {
				throw new RequestResourceMissingException("not_found_resource");
			}
			
			// EXAMPLE : resource = "clientKeys"
			resource = resources[2];  // resource 는 services, clientkes, packages 등 관리 대상 resource 임.
			
			if(resources.length > 2) {
				for(int i = 3, l = resources.length; i < l; i++) {
					parameterResource.add(resources[i]);  // 관리 대상인 service, clientkey, package 등의 id 
				}
			}
		}
		
		String businessPlatformKey = (String) request.getAttribute("businessPlatformKey");
		
		if(businessPlatformKey == null) {
			throw new NotAllowedException("businessPlatformKey is null");
		}
		
		byte[] businessPlatformID = UUIDUtils.getBytes(businessPlatformKey);
		
		if(this.businessPlatformKeyManagerService.isExists(businessPlatformID)==false) {
			throw new NotAllowedException("businessPlatformID is invalid");
			
		}
		
		@SuppressWarnings("unchecked")
		Enumeration<Object> pNames = request.getParameterNames();
		RequestBodyMap<String, String> parameterMap = new RequestBodyMap<String, String>();
		if(pNames != null) {
			while(pNames.hasMoreElements()) {
				// Only support in String type
				Object pName = pNames.nextElement();
				if(pName != null) {
					String pNameInString = pName.toString();
					Object pValue = request.getParameter(pNameInString);
					if(pValue != null) {
						parameterMap.put(pNameInString, pValue.toString());
					} else {
						parameterMap.put(pNameInString, null);
					}
				}
			}
		}
		
		String contentEncoding = "utf-8";
		
		if(request.getCharacterEncoding() != null) {
			contentEncoding = request.getCharacterEncoding();
		}
		
		if(StringUtils.hasText(request.getQueryString())) {
			RequestBodyMap<String, String> queryStringMap = RequestBodyMapUtil.getBodyObjFromRequest("application/x-www-form-urlencoded", request.getQueryString().length(), contentEncoding, request.getQueryString());
			if(queryStringMap != null && parameterMap != null) {
				parameterMap.putAll(queryStringMap);
			}
		}
		
		if(LOG.isDebugEnabled()) {
			LOG.debug("ParameterMap : " + parameterMap);
		}
		ResponseMessage responseMessage = platformServiceFactory.getPlatformService(resource).doService(HttpMethod.valueOf(request.getMethod()), businessPlatformID, parameterResource, parameterMap);
		return getResultEntity(responseMessage);
	}
	
	private ResponseEntity<String> getResultEntity(ResponseMessage responseMessage) throws JSONException {
		
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.add("Content-Type", "application/json; charset=utf-8");
		
		if(LOG.isDebugEnabled()) {
			LOG.debug("ResultMessage:::" + responseMessage.toJSONString());
		}
		
		return new ResponseEntity<String>(responseMessage.toJSONString(), responseHeaders, responseMessage.getHeader().getHttpStatus());
	}
	
	@ExceptionHandler({ RequestResourceMissingException.class, MethodFailureException.class, ValidateException.class, DataBaseProcessingException.class, NotAllowedException.class })
	public ResponseEntity<String> radixExceptionHandle(RadixException exception) {
		try {
			return this.getResultEntity(exception.getHttpStatus(), exception.getStatus(), exception.getMessage());
		} catch(JSONException e) {
			return new ResponseEntity<String>("{\"status\":\""+e.getMessage()+"\"}", HttpStatus.BAD_REQUEST);
		} finally {
			LOG.warn("processing request failed.", exception);
		}
	}
	
	@ExceptionHandler({ XPathExpressionException.class, JSONException.class, SAXException.class, IOException.class })
	public ResponseEntity<String> otherExceptionHandle(Exception exception) {
		try {
			return this.getResultEntity(HttpStatus.BAD_REQUEST, "not_progressable_data", exception.getMessage());
		} catch(JSONException e) {
			return new ResponseEntity<String>("{\"status\":\""+e.getMessage()+"\"}", HttpStatus.BAD_REQUEST);
		} finally {
			LOG.warn("processing request failed.", exception);
		}
	}

	private ResponseEntity<String> getResultEntity(HttpStatus httpStatus, String status, String msg) throws JSONException {
		ResponseMessage responseMessage = new ResponseMessage();
		ResponseMessageHeader header = new ResponseMessageHeader();
		header.setStatus(httpStatus, status);
		header.setMessage(msg);
		responseMessage.setHeader(header);
		
		return this.getResultEntity(responseMessage);
	}
	
	private static class InnerUtil {
		
		public static String toString(HttpServletRequest request) {
			JsonBuilder jb = new JsonBuilder();
			jb.put("class", request.getClass());
			jb.put("uri", request.getRequestURI());
			jb.put("method", request.getMethod());
			jb.put("queryString", request.getQueryString());
			jb.put("contentLength", request.getContentLength());
			jb.put("attribute", getAttributeString(request));
			return jb.toString();
		}
		
		
		private static String getAttributeString(HttpServletRequest request) {
			StringBuilder sb = new StringBuilder();
			sb.append("{");
			@SuppressWarnings("unchecked")
			List<String> attributeNames = Collections.list(request.getAttributeNames());
			boolean isCommaRequired = false;
			for(String name : attributeNames) {
				if(name.startsWith("org.springframework")) { continue; }
				if(isCommaRequired) { sb.append(","); }
				String value = request.getAttribute(name).toString();
				sb.append("\n").append(name).append(":").append(value).append("");
				isCommaRequired = true;
			}
			sb.append("\n}");
			return sb.toString();
		}
		
	}
}
