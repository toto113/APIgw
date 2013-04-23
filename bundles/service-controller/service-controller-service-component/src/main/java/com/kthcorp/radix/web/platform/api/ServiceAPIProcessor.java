package com.kthcorp.radix.web.platform.api;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.xml.xpath.XPathExpressionException;

import org.json.JSONException;
import org.slf4j.Logger;
import com.kthcorp.radix.util.UuidViewableLoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.xml.sax.SAXException;

import com.kthcorp.radix.api.service.ServiceManagerService;
import com.kthcorp.radix.domain.RequestBodyMap;
import com.kthcorp.radix.domain.RequestResource;
import com.kthcorp.radix.domain.exception.ValidateException;
import com.kthcorp.radix.domain.platform.api.RequestMessageType;
import com.kthcorp.radix.domain.platform.api.ResponseMessage;
import com.kthcorp.radix.domain.platform.api.ResponseMessageBodyMap;
import com.kthcorp.radix.domain.platform.api.ResponseMessageHeader;
import com.kthcorp.radix.domain.service.Service;
import com.kthcorp.radix.util.UUIDUtils;
import com.kthcorp.radix.web.platform.api.annotation.APIProcessor;
import com.kthcorp.radix.web.platform.api.annotation.AcceptContentType;
import com.kthcorp.radix.web.platform.api.annotation.AcceptHttpMethod;

@Component
@APIProcessor("services")
public class ServiceAPIProcessor {
	
	private static final Logger LOG = UuidViewableLoggerFactory.getLogger(ServiceAPIProcessor.class);
	
	@Autowired
	private ServiceManagerService serviceManagerService;
	
	@AcceptHttpMethod("POST")
	@AcceptContentType({ "application/x-www-form-urlencoded", "application/json" })
	public ResponseMessage createService(byte[] businessPlatformID, RequestResource<String> resource, RequestBodyMap<String, String> parameterMap) throws ServletException {
		long start = System.nanoTime();
		
		ResponseMessage responseMessage = new ResponseMessage();
		ResponseMessageHeader header = new ResponseMessageHeader();
		
		try {
//			String[][] params = { { "partner_id", "32" }, { "service_description", "0" } };
//			parameterMap.validate(params);
			
			RequestMessageType requestMsgType = RequestMessageType.APPLICATION_XML;
			String xmlBody = parameterMap.get("service_description");
			if(xmlBody == null) {
				throw new ValidateException("service_description is not found");
			}
			
			Service service = this.serviceManagerService.getServiceInstanceFromServiceDescription(xmlBody, requestMsgType);
			
			service.setBusinessPlatformID(businessPlatformID);
			service.setPartnerID(parameterMap.get("partner_id"));
			
			byte[] serviceId = this.serviceManagerService.createService(service);
			
			header.setStatus(HttpStatus.CREATED, "success");
			
			ResponseMessageBodyMap<String, String> map = new ResponseMessageBodyMap<String, String>();
			map.put("service_id", serviceId.toString());
			responseMessage.setBody(map);
		} catch(XPathExpressionException e) {
			header.setStatus(HttpStatus.UNPROCESSABLE_ENTITY, ValidateException.getStatusMsg());
			header.setMessage(e.getMessage());
			LOG.warn("[addService] messageId->" + header.getMessageId() + ",status->" + header.getStatus() + ",message->" + e.getMessage());
		} catch(JSONException e) {
			header.setStatus(HttpStatus.UNPROCESSABLE_ENTITY, ValidateException.getStatusMsg());
			header.setMessage(e.getMessage());
			LOG.warn("[addService] messageId->" + header.getMessageId() + ",status->" + header.getStatus() + ",message->" + e.getMessage());
		} catch(SAXException e) {
			header.setStatus(HttpStatus.UNPROCESSABLE_ENTITY, ValidateException.getStatusMsg());
			header.setMessage(e.getMessage());
			LOG.warn("[addService] messageId->" + header.getMessageId() + ",status->" + header.getStatus() + ",message->" + e.getMessage());
		} catch(IOException e) {
			header.setStatus(HttpStatus.UNPROCESSABLE_ENTITY, ValidateException.getStatusMsg());
			header.setMessage(e.getMessage());
			LOG.warn("[addService] messageId->" + header.getMessageId() + ",status->" + header.getStatus() + ",message->" + e.getMessage());
		} catch(ValidateException e) {
			header.setStatus(HttpStatus.UNPROCESSABLE_ENTITY, e.getStatus());
			header.setMessage(e.getMessage());
			LOG.warn("[addService] messageId->" + header.getMessageId() + ",status->" + header.getStatus() + ",message->" + e.getMessage());
		} catch(Exception e) {
			e.printStackTrace();
			header.setStatus(HttpStatus.INTERNAL_SERVER_ERROR, "data_processing_error");
			header.setMessage(e.getMessage());
			LOG.error("[addService] messageId->" + header.getMessageId() + ",status->" + header.getStatus() + ",message->" + e.getMessage());
		}
		
		responseMessage.setHeader(header);
		
		LOG.debug("[addService] elapsed time->" + (System.nanoTime() - start) / 1000000 + "ms");
		
		return responseMessage;
	}
	
	@AcceptHttpMethod("PUT")
	@AcceptContentType({ "application/x-www-form-urlencoded", "application/json" })
	public ResponseMessage modifyService(byte[] businessPlatformID, RequestResource<String> resource, RequestBodyMap<String, String> parameterMap) throws ServletException {
		long start = System.nanoTime();
		
		ResponseMessage responseMessage = new ResponseMessage();
		ResponseMessageHeader header = new ResponseMessageHeader();
		
		try {
			resource.validate(1);

			byte[] serviceId = null;
			try {
				serviceId = UUIDUtils.getBytes(resource.get(0));
			} catch(Exception e) {
				throw new ValidateException("serviceID is invalid");
			}
			String[][] params = { { "partner_id", "40" }, { "service_description", "0" } };
			parameterMap.validate(params);
			
			RequestMessageType requestMsgType = RequestMessageType.APPLICATION_XML;
			String xmlBody = parameterMap.get("service_description");
			if(xmlBody == null) {
				throw new ValidateException("service_description is not found");
			}
			
			Service service = this.serviceManagerService.getServiceInstanceFromServiceDescription(xmlBody, requestMsgType);
			
			service.setBusinessPlatformID(businessPlatformID);
			service.setPartnerID(parameterMap.get("partner_id"));
			service.setId(serviceId);
			
			this.serviceManagerService.modifyService(service);
			
			header.setStatus(HttpStatus.ACCEPTED, "success");
			
			ResponseMessageBodyMap<String, String> map = new ResponseMessageBodyMap<String, String>();
			map.put("service_id", serviceId.toString());
			responseMessage.setBody(map);
		} catch(XPathExpressionException e) {
			header.setStatus(HttpStatus.UNPROCESSABLE_ENTITY, ValidateException.getStatusMsg());
			header.setMessage(e.getMessage());
			LOG.warn("[addService] messageId->" + header.getMessageId() + ",status->" + header.getStatus() + ",message->" + e.getMessage());
		} catch(JSONException e) {
			header.setStatus(HttpStatus.UNPROCESSABLE_ENTITY, ValidateException.getStatusMsg());
			header.setMessage(e.getMessage());
			LOG.warn("[addService] messageId->" + header.getMessageId() + ",status->" + header.getStatus() + ",message->" + e.getMessage());
		} catch(SAXException e) {
			header.setStatus(HttpStatus.UNPROCESSABLE_ENTITY, ValidateException.getStatusMsg());
			header.setMessage(e.getMessage());
			LOG.warn("[addService] messageId->" + header.getMessageId() + ",status->" + header.getStatus() + ",message->" + e.getMessage());
		} catch(IOException e) {
			header.setStatus(HttpStatus.UNPROCESSABLE_ENTITY, ValidateException.getStatusMsg());
			header.setMessage(e.getMessage());
			LOG.warn("[addService] messageId->" + header.getMessageId() + ",status->" + header.getStatus() + ",message->" + e.getMessage());
		} catch(ValidateException e) {
			header.setStatus(HttpStatus.UNPROCESSABLE_ENTITY, e.getStatus());
			header.setMessage(e.getMessage());
			LOG.warn("[addService] messageId->" + header.getMessageId() + ",status->" + header.getStatus() + ",message->" + e.getMessage());
		} catch(Exception e) {
			e.printStackTrace();
			header.setStatus(HttpStatus.INTERNAL_SERVER_ERROR, "data_processing_error");
			header.setMessage(e.getMessage());
			LOG.error("[addService] messageId->" + header.getMessageId() + ",status->" + header.getStatus() + ",message->" + e.getMessage());
		}
		
		responseMessage.setHeader(header);
		
		LOG.debug("[addService] elapsed time->" + (System.nanoTime() - start) / 1000000 + "ms");
		
		return responseMessage;
	}
	
	@AcceptHttpMethod("DELETE")
	public ResponseMessage removeService(byte[] businessPlatformID, RequestResource<String> resource, RequestBodyMap<String, String> parameterMap) throws ServletException {
		long start = System.nanoTime();
		
		ResponseMessage responseMessage = new ResponseMessage();
		ResponseMessageHeader header = new ResponseMessageHeader();
		
		try {
			resource.validate(1);

			byte[] serviceId = null;
			try {
				serviceId = UUIDUtils.getBytes(resource.get(0));
			} catch(Exception e) {
				throw new ValidateException("serviceID is invalid");
			}
			
			Service service = new Service();
			service.setBusinessPlatformID(businessPlatformID);
			service.setId(serviceId);
			
			if(this.serviceManagerService.removeService(service)) {
				header.setStatus(HttpStatus.OK, "success");
			}
			
			ResponseMessageBodyMap<String, String> map = new ResponseMessageBodyMap<String, String>();
			responseMessage.setBody(map);
		} catch(NumberFormatException e) {
			header.setStatus(HttpStatus.UNPROCESSABLE_ENTITY, ValidateException.getStatusMsg());
			header.setMessage("[removeService] service_id is not in a proper format, error occured while parsing Integer," + e.getMessage());
		} catch(Exception e) {
			e.printStackTrace();
			header.setStatus(HttpStatus.INTERNAL_SERVER_ERROR, "data_processing_error");
			LOG.error("[removeService] messageId->" + header.getMessageId() + ",status->" + header.getStatus() + ",message->" + e.getMessage());
		}
		
		responseMessage.setHeader(header);
		
		LOG.debug("[removeService] elapsed time->" + (System.nanoTime() - start) / 1000000 + "ms");
		
		return responseMessage;
	}
}
