package com.kthcorp.radix.service;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import javax.xml.xpath.XPathExpressionException;

import org.json.JSONException;
import org.springframework.http.HttpMethod;
import org.w3c.dom.DOMException;
import org.xml.sax.SAXException;

import com.kthcorp.radix.domain.RequestBodyMap;
import com.kthcorp.radix.domain.RequestResource;
import com.kthcorp.radix.domain.exception.DataBaseProcessingException;
import com.kthcorp.radix.domain.exception.DataProcessingException;
import com.kthcorp.radix.domain.exception.MethodFailureException;
import com.kthcorp.radix.domain.exception.NotSupportException;
import com.kthcorp.radix.domain.exception.ValidateException;
import com.kthcorp.radix.domain.platform.api.ResponseMessage;

public abstract class AbstractPlatformService implements PlatformService {
	
	public abstract ResponseMessage create(byte[] businessPlatformID, RequestResource<String> resource, RequestBodyMap<String, String> parameterMap) throws XPathExpressionException, JSONException, SAXException, IOException, ValidateException, DataBaseProcessingException, NotSupportException, DOMException, NoSuchAlgorithmException;
	
	public abstract ResponseMessage get(byte[] businessPlatformID, RequestResource<String> resource, RequestBodyMap<String, String> parameterMap) throws ValidateException, DataProcessingException ;

	public abstract ResponseMessage modify(byte[] businessPlatformID, RequestResource<String> resource, RequestBodyMap<String, String> parameterMap) throws ValidateException, XPathExpressionException, JSONException, SAXException, IOException, NotSupportException, DOMException, DataBaseProcessingException, NoSuchAlgorithmException;
	
	public abstract ResponseMessage remove(byte[] businessPlatformID, RequestResource<String> resource, RequestBodyMap<String, String> parameterMap) throws ValidateException, DataBaseProcessingException;
	
	public ResponseMessage doService(HttpMethod httpMethod, byte[] businessPlatformID, RequestResource<String> resource, RequestBodyMap<String, String> parameterMap) throws XPathExpressionException, ValidateException, DataBaseProcessingException, JSONException, SAXException, IOException, MethodFailureException, NotSupportException, DOMException, NoSuchAlgorithmException, DataProcessingException {
		
		ResponseMessage ret = null;
		if(httpMethod == HttpMethod.POST) {
			ret = create(businessPlatformID, resource, parameterMap);
		} else if(httpMethod == HttpMethod.GET) {
			ret = get(businessPlatformID, resource, parameterMap);
		} else if(httpMethod == HttpMethod.PUT) {
			ret = modify(businessPlatformID, resource, parameterMap);
		} else if(httpMethod == HttpMethod.DELETE) {
			ret = remove(businessPlatformID, resource, parameterMap);
		} else {
			throw new MethodFailureException("invalid_method", httpMethod.toString()+" does not support");
		}
		
		if(ret==null) {
			throw new MethodFailureException("not_implemented_method");
		}
		
		return ret;
	}
	
}
