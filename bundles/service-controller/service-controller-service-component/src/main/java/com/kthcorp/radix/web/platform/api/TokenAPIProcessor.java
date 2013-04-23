package com.kthcorp.radix.web.platform.api;

import javax.servlet.ServletException;

import org.slf4j.Logger;
import com.kthcorp.radix.util.UuidViewableLoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import com.kthcorp.radix.api.service.TokenManagerService;
import com.kthcorp.radix.domain.RequestBodyMap;
import com.kthcorp.radix.domain.RequestResource;
import com.kthcorp.radix.domain.exception.ValidateException;
import com.kthcorp.radix.domain.platform.api.ResponseMessage;
import com.kthcorp.radix.domain.platform.api.ResponseMessageBodyMap;
import com.kthcorp.radix.domain.platform.api.ResponseMessageHeader;
import com.kthcorp.radix.domain.token.AccessToken;
import com.kthcorp.radix.web.platform.api.annotation.APIProcessor;
import com.kthcorp.radix.web.platform.api.annotation.AcceptHttpMethod;

@Component
@APIProcessor("tokens")
public class TokenAPIProcessor {
	
	private static final Logger LOG = UuidViewableLoggerFactory.getLogger(TokenAPIProcessor.class);
	
	@Autowired
	private TokenManagerService tokenManagerService;
	
	/*
	@AcceptHttpMethod("POST")
	@AcceptContentType({ "application/x-www-form-urlencoded", "application/json" })
	public ResponseMessage createAccessToken(byte[] businessPlatformID, RequestResource<String> resource, RequestBodyMap<String, String> parameterMap) throws ServletException {
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
			
			AccessToken accessToken = this.tokenManagerService.getTokenInstanceFromServiceDescription(xmlBody, requestMsgType);
			
			service.setBusinessPlatformID(businessPlatformID);
			service.setPartnerID(parameterMap.get("partner_id"));
			
			byte[] serviceId = this.tokenManagerService.createToken(service);
			
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
	*/
	
	/*
	@AcceptHttpMethod("PUT")
	@AcceptContentType({ "application/x-www-form-urlencoded", "application/json" })
	public ResponseMessage modifyAccessToken(byte[] businessPlatformID, RequestResource<String> resource, RequestBodyMap<String, String> parameterMap) throws ServletException {
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
			
			AccessToken accessToken = this.tokenManagerService.getServiceInstanceFromServiceDescription(xmlBody, requestMsgType);
			
			service.setBusinessPlatformID(businessPlatformID);
			service.setPartnerID(parameterMap.get("partner_id"));
			service.setId(serviceId);
			
			this.tokenManagerService.modifyService(service);
			
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
	*/
	@AcceptHttpMethod("DELETE")
	public ResponseMessage removeAccessToken(byte[] businessPlatformID, RequestResource<String> resource, RequestBodyMap<String, String> parameterMap) throws ServletException {
		long start = System.nanoTime();
		
		ResponseMessage responseMessage = new ResponseMessage();
		ResponseMessageHeader header = new ResponseMessageHeader();
		
		try {
//			resource.validate(1);
//
//			byte[] serviceId = null;
//			try {
//				serviceId = UUIDUtils.getBytes(resource.get(0));
//			} catch(Exception e) {
//				throw new ValidateException("serviceID is invalid");
//			}
//			
			AccessToken accessToken = new AccessToken();
//			accessToken.setBusinessPlatformID(businessPlatformID);
//			accessToken.setId(serviceId);
			
			// TODO : perhaps bug.
			if(this.tokenManagerService.removeAccessToken(accessToken)) {
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
