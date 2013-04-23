package com.kthcorp.radix.service;

import java.security.NoSuchAlgorithmException;
import java.util.List;

import org.slf4j.Logger;
import com.kthcorp.radix.util.UuidViewableLoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import com.kthcorp.radix.api.service.ClientKeyManagerService;
import com.kthcorp.radix.api.service.ClientPackageDescriptionParser;
import com.kthcorp.radix.api.service.ClientPackageManagerService;
import com.kthcorp.radix.api.validator.ClientKeyValidator;
import com.kthcorp.radix.domain.RequestBodyMap;
import com.kthcorp.radix.domain.RequestResource;
import com.kthcorp.radix.domain.client.CPackages;
import com.kthcorp.radix.domain.client.ClientKey;
import com.kthcorp.radix.domain.client.ClientKeyType;
import com.kthcorp.radix.domain.exception.DataBaseProcessingException;
import com.kthcorp.radix.domain.exception.MethodFailureException;
import com.kthcorp.radix.domain.exception.ValidateException;
import com.kthcorp.radix.domain.platform.api.ResponseMessage;
import com.kthcorp.radix.domain.platform.api.ResponseMessageBodyMap;
import com.kthcorp.radix.domain.platform.api.ResponseMessageHeader;
import com.kthcorp.radix.service.parser.client.ClientPackageDescriptionParserFactory;
import com.kthcorp.radix.util.UUIDUtils;
import com.kthcorp.radix.web.platform.api.ClientKeyAPIProcessor;

public class ClientKeyPlatformServiceImpl extends AbstractPlatformService {
	
	private static final Logger LOG = UuidViewableLoggerFactory.getLogger(ClientKeyAPIProcessor.class);
	
	@Autowired
	private ClientKeyManagerService clientKeyManagerService;
	
	@Autowired
	private ClientPackageManagerService clientPackageManagerService;
	
	@Override
	public ResponseMessage create(byte[] businessPlatformID, RequestResource<String> resource, RequestBodyMap<String, String> parameterMap) throws ValidateException, DataBaseProcessingException, NoSuchAlgorithmException {
		long start = System.nanoTime();
		
		ResponseMessage responseMessage = null;
		
		// resource evaluation
		if(resource.size()>1) {
			String clientKey = resource.get(0);
			String object = resource.get(1);
			ClientKeyValidator.keyValidate(clientKey);
			
			if("package".equalsIgnoreCase(object)) {
				responseMessage = this.createClientPackage(businessPlatformID, clientKey, parameterMap);
			} else {
				throw new ValidateException("destination object does not found");
			}
			
		} else {
			responseMessage = this.createClientKey(businessPlatformID, parameterMap);
		}
		
		if(LOG.isDebugEnabled()) {
			LOG.debug("elapsed time->" + (System.nanoTime() - start) / 1000000 + "ms");
		}
		return responseMessage;
	}
	
	private ResponseMessage createClientKey(byte[] businessPlatformID, RequestBodyMap<String, String> parameterMap) throws ValidateException, DataBaseProcessingException, NoSuchAlgorithmException {
		ResponseMessage responseMessage = new ResponseMessage();
		ResponseMessageHeader header = new ResponseMessageHeader();
		
		String[][] needParameters = { { "partner_id", "40" }, { "key_type", "1" }, { "redirect_uri", "250" } };
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
		map.put("application_description", clientKey.getApplicationDescription());
		
		responseMessage.setBody(map);
		
		responseMessage.setHeader(header);

		return responseMessage;
	}
	
	/* TODO : Client-Package 간에 같은 Package 에 대해 여러개를 등록 할 수 있도록 고려되어야 함 (DB는 되어 있음) */
	private ResponseMessage createClientPackage(byte[] businessPlatformID, String clientKey, RequestBodyMap<String, String> parameterMap) throws ValidateException, DataBaseProcessingException, NoSuchAlgorithmException {
		ResponseMessage responseMessage = new ResponseMessage();
		ResponseMessageHeader header = new ResponseMessageHeader();
		
		String[][] needParameters = { { "package_info", "0" } };
		parameterMap.validate(needParameters);
		
		ClientKeyValidator.keyValidate(clientKey);

		byte[] clientID = UUIDUtils.getBytes(clientKey);

		ClientPackageDescriptionParser parser = ClientPackageDescriptionParserFactory.newInstance().createParser("application/xml", parameterMap.get("package_info"));
		List<CPackages> packageList = parser.getPackageList(clientID);
		
		if(clientPackageManagerService.createClientPackage(clientID, packageList)) {
			header.setStatus(HttpStatus.CREATED, "success");
		}

		responseMessage.setHeader(header);

		return responseMessage;
	}
	
	@Override
	public ResponseMessage get(byte[] businessPlatformID, RequestResource<String> resource, RequestBodyMap<String, String> parameterMap) {
		// TODO 나중에 만들자.
		return null;
	}
	
	@Override
	public ResponseMessage modify(byte[] businessPlatformID, RequestResource<String> resource, RequestBodyMap<String, String> parameterMap) throws ValidateException, DataBaseProcessingException, NoSuchAlgorithmException {
		long start = System.nanoTime();
		
		ResponseMessage responseMessage = null;
		
		// resource evaluation
		if(resource.size()>1) {
			String clientKey = resource.get(0);
			String object = resource.get(1);
			ClientKeyValidator.keyValidate(clientKey);
			
			boolean forceToDelete = false;
			if(parameterMap.containsKey("forceToDelete")) {
				if("true".equalsIgnoreCase(parameterMap.get("forceToDelete"))) forceToDelete = true;
			}
			
			if("package".equalsIgnoreCase(object)) {
				responseMessage = this.modifyClientPackage(businessPlatformID, clientKey, parameterMap, forceToDelete);
			} else {
				throw new ValidateException("destination object does not found");
			}
			
		} else {
			throw new MethodFailureException("not_implemented_method");
		}
		
		if(LOG.isDebugEnabled()) {
			LOG.debug("elapsed time->" + (System.nanoTime() - start) / 1000000 + "ms");
		}
		return responseMessage;
	}
	
	/* TODO : Client-Package 간에 같은 Package 에 대해 여러개를 등록 할 수 있도록 고려되어야 함 (DB는 되어 있음) */
	private ResponseMessage modifyClientPackage(byte[] businessPlatformID, String clientKey, RequestBodyMap<String, String> parameterMap, boolean forceToDelete) throws ValidateException, DataBaseProcessingException, NoSuchAlgorithmException {
		ResponseMessage responseMessage = new ResponseMessage();
		ResponseMessageHeader header = new ResponseMessageHeader();
		
		String[][] needParameters = { { "package_info", "0" } };
		parameterMap.validate(needParameters);

		ClientKeyValidator.keyValidate(clientKey);
		byte[] clientID = UUIDUtils.getBytes(clientKey);

		ClientPackageDescriptionParser parser = ClientPackageDescriptionParserFactory.newInstance().createParser("application/xml", parameterMap.get("package_info"));
		List<CPackages> packageList = parser.getPackageList(clientID);
		
		if(clientPackageManagerService.modifyClientPackage(clientID, packageList, forceToDelete)) {
			header.setStatus(HttpStatus.OK, "success");
		}

		responseMessage.setHeader(header);

		return responseMessage;
	}
	
	@Override
	public ResponseMessage remove(byte[] businessPlatformID, RequestResource<String> resource, RequestBodyMap<String, String> parameterMap) throws ValidateException, DataBaseProcessingException {
		long start = System.nanoTime();
		
		ResponseMessage responseMessage = null;
		
		// resource evaluation
		if(resource.size()>1) {
			String clientKey = resource.get(0);
			String object = resource.get(1);
			ClientKeyValidator.keyValidate(clientKey);
			byte[] clientID = UUIDUtils.getBytes(clientKey);
			
			if("package".equalsIgnoreCase(object)) {
				byte[] packageID = null;
				if(resource.size()>2) {
					try {
						packageID = UUIDUtils.getBytes(resource.get(2));
					} catch(Exception e) {
						throw new ValidateException("packageID is invalid");
					}
				}
				
				boolean forceToDelete = false;
				if(parameterMap.containsKey("forceToDelete")) {
					if("true".equalsIgnoreCase(parameterMap.get("forceToDelete"))) forceToDelete = true;
				}
				responseMessage = this.removeClientPackage(businessPlatformID, clientID, packageID, forceToDelete);
			} else {
				throw new ValidateException("destination object does not found");
			}
			
		} else {
			responseMessage = this.removeClientKey(businessPlatformID, resource, parameterMap);
		}
		
		if(LOG.isDebugEnabled()) {
			LOG.debug("elapsed time->" + (System.nanoTime() - start) / 1000000 + "ms");
		}
		return responseMessage;
		
	}
	
	private ResponseMessage removeClientKey(byte[] businessPlatformID, RequestResource<String> resource, RequestBodyMap<String, String> parameterMap) throws ValidateException {
		ResponseMessage responseMessage = new ResponseMessage();
		ResponseMessageHeader header = new ResponseMessageHeader();
		
		resource.validate(1);
		
		String[][] needParameters = { { "key_type", "1" }, { "secret", "40" } };
		parameterMap.validate(needParameters);
		
		ClientKeyType type = ClientKeyType.fromCode(parameterMap.get("key_type"));
		if(type == null) {
			throw new ValidateException("the keyType(" + parameterMap.get("key_type") + ") does not support");
		}

		String clientKey = resource.get(0);
		String secret = parameterMap.get("secret");
		ClientKeyValidator.keyValidate(clientKey);
		
//		boolean result = this.clientKeyManagerService.removeClientKey(resource.get(0), type, parameterMap.get("secret"));
		boolean result = this.clientKeyManagerService.invalidateClientKey(clientKey, secret);
		
		if(result) {
			header.setStatus(HttpStatus.OK, "success");
		} else {
			header.setStatus(HttpStatus.UNPROCESSABLE_ENTITY, "invalid_data");
			header.setMessage("clientKey or secret incorrect");
		}
		
		responseMessage.setHeader(header);
		
		return responseMessage;
	}
	
	private ResponseMessage removeClientPackage(byte[] businessPlatformID, byte[] clientID, byte[] packageID, boolean forceToDelete) throws ValidateException {
		ResponseMessage responseMessage = new ResponseMessage();
		ResponseMessageHeader header = new ResponseMessageHeader();
		
		boolean result = false;
		if(packageID==null) {
			result = this.clientPackageManagerService.removeClientPackage(clientID, forceToDelete);
		} else {
			result = this.clientPackageManagerService.removeClientPackage(clientID, packageID, forceToDelete);		
		}

		if(result) {
			header.setStatus(HttpStatus.OK, "success");
		} else {
			header.setStatus(HttpStatus.UNPROCESSABLE_ENTITY, "invalid_data");
			header.setMessage("clientKey or secret incorrect");
		}
		
		responseMessage.setHeader(header);
		
		return responseMessage;
	}
	
}
