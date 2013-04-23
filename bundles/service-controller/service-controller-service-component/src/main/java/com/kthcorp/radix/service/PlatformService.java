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

public interface PlatformService {
	
	ResponseMessage doService(HttpMethod httpMethod, byte[] businessPlatformID, RequestResource<String> resource, RequestBodyMap<String, String> parameterMap) throws XPathExpressionException, ValidateException, DataBaseProcessingException, JSONException, SAXException, IOException, MethodFailureException, NotSupportException, DOMException, NoSuchAlgorithmException, DataProcessingException;
	
}
