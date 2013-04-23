package com.kthcorp.radix.web.platform.api;

import javax.servlet.ServletException;

import org.slf4j.Logger;
import com.kthcorp.radix.util.UuidViewableLoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import com.kthcorp.radix.api.service.PackageDescriptionParser;
import com.kthcorp.radix.api.service.PackageManagerService;
import com.kthcorp.radix.api.service.PolicyManagerService;
import com.kthcorp.radix.domain.RequestBodyMap;
import com.kthcorp.radix.domain.RequestResource;
import com.kthcorp.radix.domain.exception.ValidateException;
import com.kthcorp.radix.domain.packages.Packages;
import com.kthcorp.radix.domain.platform.api.ResponseMessage;
import com.kthcorp.radix.domain.platform.api.ResponseMessageBodyMap;
import com.kthcorp.radix.domain.platform.api.ResponseMessageHeader;
import com.kthcorp.radix.service.parser.packages.PackageDescriptionParserFactory;
import com.kthcorp.radix.util.UUIDUtils;
import com.kthcorp.radix.web.platform.api.annotation.APIProcessor;
import com.kthcorp.radix.web.platform.api.annotation.AcceptContentType;
import com.kthcorp.radix.web.platform.api.annotation.AcceptHttpMethod;

@Component
@APIProcessor("packages")
public class PackageAPIProcessor {
	
	private static final Logger LOG = UuidViewableLoggerFactory.getLogger(PackageAPIProcessor.class);
	
	@Autowired
	private PackageManagerService packageManagerService;

	@Autowired
	private PolicyManagerService policyManagerService;
	
	@AcceptHttpMethod("POST")
	@AcceptContentType({ "application/x-www-form-urlencoded", "application/json" })
	public ResponseMessage createPackage(byte[] businessPlatformID, RequestResource<String> resource, RequestBodyMap<String, String> parameterMap) throws ServletException {
		long start = System.nanoTime();
		
		ResponseMessage responseMessage = new ResponseMessage();
		ResponseMessageHeader header = new ResponseMessageHeader();
		
		try {
			String[][] params = { {"partner_id","40"}, {"package_description","0"} };
			parameterMap.validate(params);
			
			String xmlBody = parameterMap.get("package_description");
			if(xmlBody == null) {
				throw new ValidateException("package_description is not found");
			}
			
			PackageDescriptionParser parser = PackageDescriptionParserFactory.createParser("application/xml", xmlBody, policyManagerService);
			Packages packages = parser.getPackage(businessPlatformID, parameterMap.get("partner_id"), false);
			
			byte[] packageID = this.packageManagerService.createPackage(packages);
			
			header.setStatus(HttpStatus.CREATED, "success");
			
			ResponseMessageBodyMap<String, String> map = new ResponseMessageBodyMap<String, String>();
			map.put("package_id", packageID.toString());
			responseMessage.setBody(map);
		} catch(ValidateException e) {
			header.setStatus(HttpStatus.UNPROCESSABLE_ENTITY, e.getStatus());
			header.setMessage(e.getMessage());
			LOG.warn("[addPackage] messageId->" + header.getMessageId() + ",status->" + header.getStatus() + ",message->" + e.getMessage());
		} catch(Exception e) {
			e.printStackTrace();
			header.setStatus(HttpStatus.INTERNAL_SERVER_ERROR, "data_processing_error");
			header.setMessage(e.getMessage());
			LOG.error("[addPackage] messageId->" + header.getMessageId() + ",status->" + header.getStatus() + ",message->" + e.getMessage());
		}
		
		responseMessage.setHeader(header);
		
		if(LOG.isDebugEnabled()) {
			LOG.debug("[addPackage] elapsed time->" + (System.nanoTime() - start) / 1000000 + "ms");
		}
		
		return responseMessage;
	}
	
	@AcceptHttpMethod("DELETE")
	public ResponseMessage removePackage(byte[] businessPlatformID, RequestResource<String> resource, RequestBodyMap<String, String> parameterMap) throws ServletException {
		long start = System.nanoTime();
		
		ResponseMessage responseMessage = new ResponseMessage();
		ResponseMessageHeader header = new ResponseMessageHeader();
		
		try {
			resource.validate(1);
			String[][] params = { {"partner_id","40"} };
			parameterMap.validate(params);
			
			byte[] packageID = null;
			try {
				packageID = UUIDUtils.getBytes(resource.get(0));
			} catch(Exception e) {
				throw new ValidateException("packageID is invalid");
			}
			
			if(this.packageManagerService.removePackage(businessPlatformID, parameterMap.get("partner_id"), packageID, false)) {
				header.setStatus(HttpStatus.OK, "success");
			}
			
			ResponseMessageBodyMap<String, String> map = new ResponseMessageBodyMap<String, String>();
			responseMessage.setBody(map);
		} catch(NumberFormatException e) {
			header.setStatus(HttpStatus.UNPROCESSABLE_ENTITY, ValidateException.getStatusMsg());
			header.setMessage("[removePackage] package_id is not in a proper format, error occured while parsing Integer," + e.getMessage());
		} catch(ValidateException e) {
			e.printStackTrace();
			header.setStatus(HttpStatus.UNPROCESSABLE_ENTITY, e.getStatus());
			header.setMessage(e.getMessage());
		} catch(Exception e) {
			e.printStackTrace();
			header.setStatus(HttpStatus.INTERNAL_SERVER_ERROR, "data_processing_error");
			LOG.error("[removePackage] messageId->" + header.getMessageId() + ",status->" + header.getStatus() + ",message->" + e.getMessage());
		}
		
		responseMessage.setHeader(header);
		
		if(LOG.isDebugEnabled()) {
			LOG.debug("[removePackage] elapsed time->" + (System.nanoTime() - start) / 1000000 + "ms");
		}
		
		return responseMessage;
	}
}
