package com.kthcorp.radix.api.service;

import java.io.IOException;

import javax.xml.xpath.XPathExpressionException;

import org.json.JSONException;
import org.w3c.dom.DOMException;
import org.xml.sax.SAXException;

import com.kthcorp.radix.domain.exception.NotSupportException;
import com.kthcorp.radix.domain.exception.ValidateException;
import com.kthcorp.radix.domain.service.Service;

public interface ServiceDescriptionParser {
	
	Service getService(String inputSource) throws JSONException, XPathExpressionException, SAXException, IOException, ValidateException, NotSupportException, DOMException;
}
