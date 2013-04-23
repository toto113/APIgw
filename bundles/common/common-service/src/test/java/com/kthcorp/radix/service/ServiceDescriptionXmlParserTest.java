package com.kthcorp.radix.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.xml.xpath.XPathExpressionException;

import junit.framework.Assert;

import org.json.JSONException;
import org.junit.Test;
import org.w3c.dom.DOMException;
import org.xml.sax.SAXException;

import com.kthcorp.radix.domain.exception.NotSupportException;
import com.kthcorp.radix.domain.exception.ValidateException;
import com.kthcorp.radix.domain.platform.api.RequestMessageType;
import com.kthcorp.radix.domain.service.Service;

public class ServiceDescriptionXmlParserTest {
	
	public static String readServiceDescriptionTestXml(String xmlFileName) {
		StringBuffer sb = new StringBuffer();
		
		try {
			InputStreamReader isr = new InputStreamReader(Thread.currentThread().getContextClassLoader().getResource(xmlFileName).openStream(), "UTF-8");
			BufferedReader br = new BufferedReader(isr);
			String line = null;
			
			while((line = br.readLine()) != null) {
				sb.append(line);
			}
		} catch(IOException e) {
			e.printStackTrace();
			Assert.assertTrue(true);
		}
		
		return sb.toString();
	}
	
	public void doFailParseServiceDescriptionXML() throws XPathExpressionException, JSONException, SAXException, IOException, ValidateException {
		ServiceManagerServiceImpl serviceManagerSvc = new ServiceManagerServiceImpl();
		
		String input = ServiceDescriptionXmlParserTest.readServiceDescriptionTestXml("ServiceDescriptionNotValidFormatTest.xml");
		
		Service service = null;
		
		try {
			service = serviceManagerSvc.getServiceInstanceFromServiceDescription(input, RequestMessageType.APPLICATION_XML);
		} catch(NotSupportException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch(DOMException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Assert.assertNull(service);
	}
	
	@Test
	public void doParseServiceDescriptionXML() throws XPathExpressionException, JSONException, SAXException, IOException, ValidateException, NotSupportException, DOMException {
		ServiceManagerServiceImpl serviceManagerSvc = new ServiceManagerServiceImpl();
		
		String input = ServiceDescriptionXmlParserTest.readServiceDescriptionTestXml("ServiceDescriptionTest.xml");
		
		Service service = serviceManagerSvc.getServiceInstanceFromServiceDescription(input, RequestMessageType.APPLICATION_XML);
		
		Assert.assertNotNull(service);
	}
}
