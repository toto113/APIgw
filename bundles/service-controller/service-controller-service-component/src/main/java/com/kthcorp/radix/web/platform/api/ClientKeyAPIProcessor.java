package com.kthcorp.radix.web.platform.api;

import javax.servlet.ServletException;

import org.slf4j.Logger;
import com.kthcorp.radix.util.UuidViewableLoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import com.kthcorp.radix.api.service.ClientKeyManagerService;
import com.kthcorp.radix.domain.RequestBodyMap;
import com.kthcorp.radix.domain.RequestResource;
import com.kthcorp.radix.domain.client.ClientKey;
import com.kthcorp.radix.domain.client.ClientKeyType;
import com.kthcorp.radix.domain.exception.ValidateException;
import com.kthcorp.radix.domain.platform.api.ResponseMessage;
import com.kthcorp.radix.domain.platform.api.ResponseMessageBodyMap;
import com.kthcorp.radix.domain.platform.api.ResponseMessageHeader;
import com.kthcorp.radix.web.platform.api.annotation.APIProcessor;
import com.kthcorp.radix.web.platform.api.annotation.AcceptContentType;
import com.kthcorp.radix.web.platform.api.annotation.AcceptHttpMethod;

@Component
@APIProcessor("clientKeys")
public class ClientKeyAPIProcessor {
	
	private static final Logger LOG = UuidViewableLoggerFactory.getLogger(ClientKeyAPIProcessor.class);
	
	@Autowired
	private ClientKeyManagerService clientKeyManagerService;
	
	@AcceptHttpMethod("POST")
	@AcceptContentType({ "application/x-www-form-urlencoded", "application/json" })
	public ResponseMessage createClientKey(byte[] businessPlatformID, RequestResource<String> resource, RequestBodyMap<String, String> parameterMap) throws ServletException {
		long start = System.nanoTime();
		
		ResponseMessage responseMessage = new ResponseMessage();
		ResponseMessageHeader header = new ResponseMessageHeader();
		
		try {
			String[][] needParameters = { { "partner_id", "40" }, { "key_type", "1" } };
			parameterMap.validate(needParameters);
			
			ClientKeyType type = ClientKeyType.fromCode(parameterMap.get("key_type"));
			
			if(type == null) {
				throw new ValidateException("the keyType(" + parameterMap.get("key_type") + ") does not support");
			}
			
			ClientKey clientKey = this.clientKeyManagerService.createClientKey(businessPlatformID, parameterMap.get("partner_id"), type, parameterMap.get("redirect_uri"), parameterMap.get("application_name"), parameterMap.get("application_description"));
			
			header.setStatus(HttpStatus.CREATED, "success");
			
			ResponseMessageBodyMap<String, String> map = new ResponseMessageBodyMap<String, String>();
			map.put("client_key", clientKey.getKeyString());
			map.put("secret", clientKey.getSecret());
			map.put("redirect_uri", clientKey.getRedirectUri());
			map.put("application_name", clientKey.getApplicationName());
			
			responseMessage.setBody(map);
		} catch(ValidateException e) {
			header.setStatus(HttpStatus.UNPROCESSABLE_ENTITY, e.getStatus());
			header.setMessage(e.getMessage());
		} catch(Exception e) {
			header.setStatus(HttpStatus.INTERNAL_SERVER_ERROR, "error_on_serverside");
			header.setMessage(e.getMessage());
			LOG.error("[addClientKey] messageId->" + header.getMessageId() + ",status->" + header.getStatus() + ",message->" + e.getMessage());
		}
		
		responseMessage.setHeader(header);
		
		if(LOG.isDebugEnabled()) {
			LOG.debug("[addClientKey] elapsed time->" + (System.nanoTime() - start) / 1000000 + "ms");
		}
		
		return responseMessage;
	}
	
	@AcceptHttpMethod("DELETE")
	public ResponseMessage removeClientKey(int businessPlatformID, RequestResource<String> resource, RequestBodyMap<String, String> parameterMap) {
		long start = System.nanoTime();
		
		ResponseMessage responseMessage = new ResponseMessage();
		ResponseMessageHeader header = new ResponseMessageHeader();
		
		try {
			resource.validate(1);
			
			String[][] needParameters = { { "key_type", "1" }, { "secret", "40" } };
			parameterMap.validate(needParameters);
			
			ClientKeyType type = ClientKeyType.fromCode(parameterMap.get("key_type"));
			if(type == null) {
				throw new ValidateException("the keyType(" + parameterMap.get("key_type") + ") does not support");
			}
			
			boolean result = this.clientKeyManagerService.removeClientKey(resource.get(0), type, parameterMap.get("secret"));
			if(result) {
				header.setStatus(HttpStatus.OK, "success");
			} else {
				header.setStatus(HttpStatus.UNPROCESSABLE_ENTITY, "invalid_data");
				header.setMessage("clientKey or secret incorrect");
			}
			
		} catch(ValidateException e) {
			header.setStatus(HttpStatus.UNPROCESSABLE_ENTITY, e.getStatus());
			header.setMessage(e.getMessage());
		} catch(Exception e) {
			header.setStatus(HttpStatus.INTERNAL_SERVER_ERROR, "data_processing_error");
			header.setMessage(e.getMessage());
			LOG.error("[removeClientKey] messageId->" + header.getMessageId() + ",status->" + header.getStatus() + ",message->" + e.getMessage());
		}
		
		responseMessage.setHeader(header);
		
		if(LOG.isDebugEnabled()) {
			LOG.debug("[removeClientKey] elapsed time->" + (System.nanoTime() - start) / 1000000 + "ms");
		}
		
		return responseMessage;
	}
}
