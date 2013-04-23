package com.kthcorp.radix.service;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import javax.xml.xpath.XPathExpressionException;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import com.kthcorp.radix.util.UuidViewableLoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.w3c.dom.DOMException;
import org.xml.sax.SAXException;

import com.kthcorp.radix.api.service.ServiceManagerService;
import com.kthcorp.radix.domain.RequestBodyMap;
import com.kthcorp.radix.domain.RequestResource;
import com.kthcorp.radix.domain.exception.DataProcessingException;
import com.kthcorp.radix.domain.exception.NotSupportException;
import com.kthcorp.radix.domain.exception.ValidateException;
import com.kthcorp.radix.domain.platform.api.RequestMessageType;
import com.kthcorp.radix.domain.platform.api.ResponseMessage;
import com.kthcorp.radix.domain.platform.api.ResponseMessageBodyMap;
import com.kthcorp.radix.domain.platform.api.ResponseMessageHeader;
import com.kthcorp.radix.domain.service.Service;
import com.kthcorp.radix.util.UUIDUtils;
import com.kthcorp.radix.web.platform.api.ServiceAPIProcessor;
import com.kthcorp.radix.web.util.ObjectToJSON;

public class ServicePlatformServiceImpl extends AbstractPlatformService {
	
	private static final Logger LOG = UuidViewableLoggerFactory.getLogger(ServiceAPIProcessor.class);
	
	@Autowired
	private ServiceManagerService serviceManagerService;
	
	@Override
	public ResponseMessage create(byte[] businessPlatformID, RequestResource<String> resource, RequestBodyMap<String, String> parameterMap) throws XPathExpressionException, JSONException, SAXException, IOException, ValidateException, NotSupportException, DOMException, NoSuchAlgorithmException {
		long start = System.nanoTime();
		
		ResponseMessage responseMessage = new ResponseMessage();
		ResponseMessageHeader header = new ResponseMessageHeader();
		
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
		
		byte[] serviceId = this.serviceManagerService.createService(service);
		
		header.setStatus(HttpStatus.CREATED, "success");
		
		ResponseMessageBodyMap<String, String> map = new ResponseMessageBodyMap<String, String>();		
		map.put("service_id", UUIDUtils.getString(serviceId));
		responseMessage.setBody(map);
		
		responseMessage.setHeader(header);
		
		LOG.debug("[addService] elapsed time->" + (System.nanoTime() - start) / 1000000 + "ms");
		
		return responseMessage;
	}
	
	@Override
	public ResponseMessage get(byte[] businessPlatformID, RequestResource<String> resource, RequestBodyMap<String, String> parameterMap) throws ValidateException, DataProcessingException {
		long start = System.nanoTime();
		
		ResponseMessage responseMessage = new ResponseMessage();
		ResponseMessageHeader header = new ResponseMessageHeader();
		String[][] params = { { "partner_id", "40" } };
		parameterMap.validate(params);
		String partnerID = parameterMap.get("partner_id");
		ResponseMessageBodyMap<String, JSONObject> map = new ResponseMessageBodyMap<String, JSONObject>();

		if(resource == null || resource.size() == 0) {
			List<Service> serviceList = this.serviceManagerService.getServiceListWithServiceInfo(businessPlatformID, partnerID);
			header.setStatus(HttpStatus.OK, "success");
			responseMessage.setHeader(header);
			int idx = 0;
			for(Service service : serviceList) {				
				try {
					JSONObject obj = ObjectToJSON.getService(service);
					map.put("service_" + (idx++), obj);
				} catch (JSONException e) {
					throw new DataProcessingException("result Message creating(service->"+UUIDUtils.getString(service.getId())+" failed->"+e.getMessage());
				}
			}
			responseMessage.setBody(map);
		} else {
			resource.validate(1);
			byte[] serviceId = null;
			try {
				serviceId = UUIDUtils.getBytes(resource.get(0));
			} catch(Exception e) {
				throw new ValidateException("serviceID is invalid");
			}
			Service service = this.serviceManagerService.getServiceWithServiceInfo(businessPlatformID, partnerID, serviceId);
			service.setBusinessPlatformID(businessPlatformID);
			service.setPartnerID(parameterMap.get("partner_id"));
			header.setStatus(HttpStatus.OK, "success");
			responseMessage.setHeader(header);
			
			try {
				JSONObject obj = ObjectToJSON.getService(service);
				map.put("service", obj);
			} catch (JSONException e) {
				throw new DataProcessingException("result Message creating(service->"+UUIDUtils.getString(service.getId())+" failed->"+e.getMessage());
			}
			
			responseMessage.setBody(map);
		}
		
		LOG.debug("[getService] elapsed time->" + (System.nanoTime() - start) / 1000000 + "ms");
		
		return responseMessage;
	}
	
	@Override
	public ResponseMessage modify(byte[] businessPlatformID, RequestResource<String> resource, RequestBodyMap<String, String> parameterMap) throws ValidateException, XPathExpressionException, JSONException, SAXException, IOException, NotSupportException, DOMException {
		long start = System.nanoTime();
		
		ResponseMessage responseMessage = new ResponseMessage();
		ResponseMessageHeader header = new ResponseMessageHeader();
		
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
		
		responseMessage.setHeader(header);
		
		LOG.debug("[addService] elapsed time->" + (System.nanoTime() - start) / 1000000 + "ms");
		
		return responseMessage;
	}
	
	@Override
	public ResponseMessage remove(byte[] businessPlatformID, RequestResource<String> resource, RequestBodyMap<String, String> parameterMap) throws ValidateException {
		long start = System.nanoTime();
		
		ResponseMessage responseMessage = new ResponseMessage();
		ResponseMessageHeader header = new ResponseMessageHeader();
		
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
		
		responseMessage.setHeader(header);
		
		LOG.debug("[removeService] elapsed time->" + (System.nanoTime() - start) / 1000000 + "ms");
		
		return responseMessage;
	}
	
}
