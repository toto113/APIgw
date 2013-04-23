package com.kthcorp.radix.service;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;
import java.util.List;

import org.slf4j.Logger;
import com.kthcorp.radix.util.UuidViewableLoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import com.kthcorp.radix.api.service.ClientKeyManagerService;
import com.kthcorp.radix.api.validator.ClientKeyValidator;
import com.kthcorp.radix.dao.mybatis.MyBatisClientKeyManagerDaoMapper;
import com.kthcorp.radix.domain.client.ClientKey;
import com.kthcorp.radix.domain.client.ClientKeyType;
import com.kthcorp.radix.domain.exception.DataBaseProcessingException;
import com.kthcorp.radix.domain.exception.NotSupportException;
import com.kthcorp.radix.domain.exception.ValidateException;
import com.kthcorp.radix.util.UUIDUtils;

public class ClientKeyManagerServiceImpl implements ClientKeyManagerService {
	
	private static final Logger LOG = UuidViewableLoggerFactory.getLogger(ClientKeyManagerServiceImpl.class);
	
	@Autowired
	private MyBatisClientKeyManagerDaoMapper mapper;
	
	public void setClientKeyManagerDaoMapper(MyBatisClientKeyManagerDaoMapper clientKeyManagerDaoMapper) {
		this.mapper = clientKeyManagerDaoMapper;
	}
	
	@Override
	public boolean isExists(byte[] businessPlatformID, String partnerID, ClientKeyType clientKeyType, String secret) throws ValidateException {
		ClientKeyValidator.propertyValidator(businessPlatformID, partnerID, clientKeyType);
		
		int rCount = 0;
		if(businessPlatformID != null) {
			rCount = mapper.isExists(businessPlatformID, partnerID, clientKeyType.getCode(), secret);
		}
		
		if(rCount > 0) {
			return true;
		}
		return false;
	}
	
	@Override
	public boolean isExists(String keyString, String secret) throws ValidateException {
		ClientKeyValidator.keyAndSecretValidate(keyString, secret);
		
		int rCount = 0;
		rCount = mapper.isExistsWithKeyString(keyString, secret);
		
		if(rCount > 0) {
			return true;
		}
		return false;
	}
	
	@Override
	public ClientKey getClientKey(byte[] businessPlatformID, String partnerID, ClientKeyType clientKeyType, String secret) throws ValidateException {
		ClientKeyValidator.propertyValidator(businessPlatformID, partnerID, clientKeyType);
		
		if(businessPlatformID != null) {
			return mapper.selectClientKey(businessPlatformID, partnerID, clientKeyType.getCode(), secret);
		}
		return null;
	}
	
	@Override
	public ClientKey getClientKey(byte[] clientID) throws ValidateException {
		ClientKeyValidator.keyValidate(clientID);
		
		return mapper.selectClientKeyWithID(clientID);
	}
	
	@Override
	public ClientKey getValidClientKey(byte[] clientID) throws ValidateException, NotSupportException {
		ClientKeyValidator.keyValidate(clientID);
		ClientKey clientKey = mapper.selectClientKeyWithID(clientID);
		
		if( !( null == clientKey || clientKey.isValid() ) ) {
			throw new ValidateException("application is deleted");
		}
		
		return clientKey;
	}
	
	@Override
	public ClientKey getClientKey(String keyString, ClientKeyType clientKeyType, String secret) throws ValidateException {
		ClientKeyValidator.keyAndSecretValidate(keyString, secret);
		
		return mapper.selectClientKeyWithKeyAndSecretString(keyString, secret, clientKeyType.getCode());
	}
	
	@Override
	public List<ClientKey> getClientKey(String partnerID) throws ValidateException {
		if(!StringUtils.hasText(partnerID)) {
			throw new ValidateException("partnerID does not found");
		}
		
		return mapper.selectClientKeyWithPartnerID(partnerID);
	}
	
	@Override
	public boolean removeClientKey(String keyString, ClientKeyType clientKeyType, String secret) throws ValidateException {
		
		// TODO Exception 처리를 하지 않음. 리팩토링 할것.
		ClientKeyValidator.keyAndSecretValidate(keyString, secret);
		if(mapper.backupClientKey(keyString, clientKeyType.getCode(), secret)<1) {
			return false;
		}
		int removedRowCount = mapper.deleteClientKey(keyString, clientKeyType.getCode(), secret);
		if(removedRowCount > 0) {
			if(LOG.isWarnEnabled()) {
				if(removedRowCount > 1) {
					LOG.warn("removed two or more rows than one,input parameter->[keyString:" + keyString + ",secret:" + secret + "],affectedRows->" + removedRowCount);
				}
			}
			return true;
		} else {
			return false;
		}
	}
	
	@Override
	public ClientKey createClientKey(byte[] businessPlatformID, String partnerID, ClientKeyType clientKeyType, String redirectUri, String applicationName, String applicationDescription) throws ValidateException, DataBaseProcessingException, NoSuchAlgorithmException {
		ClientKeyValidator.propertyValidator(businessPlatformID, partnerID, clientKeyType);
		
		if(businessPlatformID != null) {
			ClientKey clientKey = new ClientKey();
			
			// Generate New Key & Secret
			clientKey.generateKeyAndSecret();
			
			clientKey.setBusinessPlatformID(businessPlatformID);
			clientKey.setPartnerID(partnerID);
			clientKey.setRegDate(Calendar.getInstance().getTime());
			clientKey.setType(clientKeyType);
			clientKey.setRedirectUri(redirectUri);
			try {
				applicationName = URLDecoder.decode(applicationName, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				LOG.warn("parsing applicationName failed. applicationName={}", applicationName);
			}
			try {
				applicationDescription = URLDecoder.decode(applicationDescription, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				LOG.warn("parsing applicationDescription failed. applicationDescription={}", applicationDescription);
			}
			clientKey.setApplicationName(applicationName);
			clientKey.setApplicationDescription(applicationDescription);
			
			int retCount = mapper.insertClientKey(clientKey);
			if(retCount < 1) {
				throw new DataBaseProcessingException("failed to add client key information");
			}
			return clientKey;
		} else {
			throw new ValidateException("your businessPlatformKey is invalid");
		}
	}
	
	@Override
	public boolean modifyClientKey(String keyString, String secret, ClientKey clientKey) throws ValidateException {
		ClientKeyValidator.keyAndSecretValidate(keyString, secret);
		if(clientKey == null) {
			throw new ValidateException("not found client key information");
		}
		
		clientKey.setKeyString(keyString);
		clientKey.setSecret(secret);
		
		int updatedRowCount = mapper.updateClientKey(clientKey);
		if(updatedRowCount > 0) {
			if(LOG.isWarnEnabled()) {
				if(updatedRowCount > 1) {
					LOG.warn("updated two or more rows than one,input parameter->[keyString:" + keyString + ",secret:" + secret + ",clientKey:" + clientKey + "],affectedRows->" + updatedRowCount);
				}
			}
			return true;
		} else {
			return false;
		}
	}
	
	@Override
	public boolean invalidateClientKey(String clientKeyString, String secret) throws ValidateException {
		ClientKeyValidator.keyAndSecretValidate(clientKeyString, secret);
		
		ClientKey clientKey = getClientKey(UUIDUtils.getBytes(clientKeyString));
		if(clientKey == null) {
			throw new ValidateException("not found client key information");
		}
		
		clientKey.setKeyString(clientKeyString);
		clientKey.setSecret(secret);
		
		int updatedRowCount = mapper.invalidateClientKey(clientKey);
		if(updatedRowCount > 0) {
			if(LOG.isWarnEnabled()) {
				if(updatedRowCount > 1) {
					LOG.warn("updated two or more rows than one,input parameter->[keyString:" + clientKeyString + ",secret:" + secret + ",clientKey:" + clientKey + "],affectedRows->" + updatedRowCount);
				}
			}
			return true;
		} else {
			return false;
		}
	}
}
