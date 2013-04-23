package com.kthcorp.radix.service;

import java.security.NoSuchAlgorithmException;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import com.kthcorp.radix.util.UuidViewableLoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import com.kthcorp.radix.api.service.PackageDescriptionParser;
import com.kthcorp.radix.api.service.PackageManagerService;
import com.kthcorp.radix.api.service.PolicyManagerService;
import com.kthcorp.radix.domain.RequestBodyMap;
import com.kthcorp.radix.domain.RequestResource;
import com.kthcorp.radix.domain.exception.DataProcessingException;
import com.kthcorp.radix.domain.exception.ValidateException;
import com.kthcorp.radix.domain.packages.Packages;
import com.kthcorp.radix.domain.platform.api.ResponseMessage;
import com.kthcorp.radix.domain.platform.api.ResponseMessageBodyMap;
import com.kthcorp.radix.domain.platform.api.ResponseMessageHeader;
import com.kthcorp.radix.service.parser.packages.PackageDescriptionParserFactory;
import com.kthcorp.radix.util.UUIDUtils;
import com.kthcorp.radix.web.platform.api.PackageAPIProcessor;
import com.kthcorp.radix.web.util.ObjectToJSON;

public class PackagePlatformServiceImpl extends AbstractPlatformService {
	
	private static final Logger LOG = UuidViewableLoggerFactory.getLogger(PackageAPIProcessor.class);
	
	@Autowired
	private PackageManagerService packageManagerService;
	
	@Autowired
	private PolicyManagerService policyManagerService;
	
	@Override
	public ResponseMessage create(byte[] businessPlatformID, RequestResource<String> resource, RequestBodyMap<String, String> parameterMap) throws ValidateException, NoSuchAlgorithmException {
		long start = System.nanoTime();
		
		ResponseMessage responseMessage = new ResponseMessage();
		ResponseMessageHeader header = new ResponseMessageHeader();
		
		String[][] params = { { "partner_id", "40" }, { "package_description", "0" } };
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
		map.put("package_id", UUIDUtils.getString(packageID));
		responseMessage.setBody(map);
		
		responseMessage.setHeader(header);
		
		if(LOG.isDebugEnabled()) {
			LOG.debug("[addPackage] elapsed time->" + (System.nanoTime() - start) / 1000000 + "ms");
		}
		
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
			List<Packages> packageList = this.packageManagerService.getPackageList(businessPlatformID, partnerID);
			header.setStatus(HttpStatus.OK, "success");
			responseMessage.setHeader(header);
			int idx = 0;
			
			for(Packages packages : packageList) {
				try {
					JSONObject obj = ObjectToJSON.getPackage(packages);
					map.put("package_" + (idx++), obj);
				} catch (JSONException e) {
					throw new DataProcessingException("result Message creating(package->"+UUIDUtils.getString(packages.getId())+" failed->"+e.getMessage());
				}
			}
			responseMessage.setBody(map);
		} else {
			resource.validate(1);
			byte[] packageID = null;
			try {
				packageID = UUIDUtils.getBytes(resource.get(0));
			} catch(Exception e) {
				throw new ValidateException("packageID is invalid");
			}
			
			Packages packages = this.packageManagerService.getPackage(partnerID, packageID);
			header.setStatus(HttpStatus.OK, "success");
			responseMessage.setHeader(header);
			try {
				JSONObject obj = ObjectToJSON.getPackage(packages);
				map.put("package", obj);
			} catch (JSONException e) {
				throw new DataProcessingException("result Message creating(package->"+UUIDUtils.getString(packages.getId())+" failed->"+e.getMessage());
			}
			responseMessage.setBody(map);
		}
		
		if(LOG.isDebugEnabled()) {
			LOG.debug("[getPackage] elapsed time->" + (System.nanoTime() - start) / 1000000 + "ms");
		}
		
		return responseMessage;
	}
	
	@Override
	public ResponseMessage modify(byte[] businessPlatformID, RequestResource<String> resource, RequestBodyMap<String, String> parameterMap) throws ValidateException, NoSuchAlgorithmException {
		long start = System.nanoTime();
		
		ResponseMessage responseMessage = new ResponseMessage();
		ResponseMessageHeader header = new ResponseMessageHeader();
		
		resource.validate(1);
		byte[] packageID = null;
		
		try {
			packageID = UUIDUtils.getBytes(resource.get(0));
		} catch(Exception e) {
			throw new ValidateException("[modifyPackage] package_id is invalid");
		}

		String[][] params = { { "partner_id", "40" }, { "package_description", "0" } };
		parameterMap.validate(params);
		
		String xmlBody = parameterMap.get("package_description");
		if(xmlBody == null) {
			throw new ValidateException("package_description is not found");
		}
		
		boolean forceToDelete = false;
		if(parameterMap.containsKey("forceToDelete")) {
			if("true".equalsIgnoreCase(parameterMap.get("forceToDelete"))) forceToDelete = true;
		}
		
		PackageDescriptionParser parser = PackageDescriptionParserFactory.createParser("application/xml", xmlBody, policyManagerService);
		Packages packages = parser.getPackage(businessPlatformID, parameterMap.get("partner_id"), true);
		
		if(packageID==null) {
			throw new ValidateException("package ID does not found");
		}
		packages.setId(packageID);
		this.packageManagerService.modifyPackage(packages, forceToDelete);
		
		header.setStatus(HttpStatus.OK, "success");		
		responseMessage.setHeader(header);
		
		if(LOG.isDebugEnabled()) {
			LOG.debug("[modifyPackage] elapsed time->" + (System.nanoTime() - start) / 1000000 + "ms");
		}
		
		return responseMessage;
	}
	
	@Override
	public ResponseMessage remove(byte[] businessPlatformID, RequestResource<String> resource, RequestBodyMap<String, String> parameterMap) throws ValidateException {
		long start = System.nanoTime();
		
		ResponseMessage responseMessage = new ResponseMessage();
		ResponseMessageHeader header = new ResponseMessageHeader();
		
		resource.validate(1);
		String[][] params = { { "partner_id", "40" } };
		parameterMap.validate(params);
		
		byte[] packageID = null;
		
		try {
			packageID = UUIDUtils.getBytes(resource.get(0));
		} catch(Exception e) {
			throw new ValidateException("[modifyPackage] package_id is invalid");
		}
		
		boolean forceToDelete = false;
		if(parameterMap.containsKey("forceToDelete")) {
			if("true".equalsIgnoreCase(parameterMap.get("forceToDelete"))) forceToDelete = true;
		}
		
		if(this.packageManagerService.removePackage(businessPlatformID, parameterMap.get("partner_id"), packageID, forceToDelete)) {
			header.setStatus(HttpStatus.OK, "success");
		}
		
		ResponseMessageBodyMap<String, String> map = new ResponseMessageBodyMap<String, String>();
		responseMessage.setBody(map);
		
		responseMessage.setHeader(header);
		
		if(LOG.isDebugEnabled()) {
			LOG.debug("[removePackage] elapsed time->" + (System.nanoTime() - start) / 1000000 + "ms");
		}
		
		return responseMessage;
	}
	
}
