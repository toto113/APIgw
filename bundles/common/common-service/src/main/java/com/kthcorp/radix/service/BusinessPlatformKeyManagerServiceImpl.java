package com.kthcorp.radix.service;

import java.security.NoSuchAlgorithmException;

import org.slf4j.Logger;
import com.kthcorp.radix.util.UuidViewableLoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.kthcorp.radix.api.service.BusinessPlatformKeyManagerService;
import com.kthcorp.radix.api.validator.BusinessPlatformKeyValidator;
import com.kthcorp.radix.dao.mybatis.MyBatisBusinessPlatformKeyManagerDaoMapper;
import com.kthcorp.radix.domain.businessPlatform.BusinessPlatformKey;
import com.kthcorp.radix.domain.exception.AlreadyExistException;
import com.kthcorp.radix.domain.exception.DataBaseProcessingException;
import com.kthcorp.radix.domain.exception.NotSupportException;
import com.kthcorp.radix.domain.exception.ValidateException;

public class BusinessPlatformKeyManagerServiceImpl implements BusinessPlatformKeyManagerService {
	
	private static final Logger LOG = UuidViewableLoggerFactory.getLogger(BusinessPlatformKeyManagerServiceImpl.class);
	
	@Autowired
	private MyBatisBusinessPlatformKeyManagerDaoMapper mapper;
	
	@Override
	public boolean isExists(String keyString, String secret) throws ValidateException {
		BusinessPlatformKeyValidator.keyAndSecretValidate(keyString, secret);
		
		int rCount = mapper.selectExistsCount(keyString, secret);
		if(rCount > 0) {
			return true;
		} else {
			return false;
		}
	}
	
	@Override
	public boolean isExists(byte[] id) throws ValidateException {
		BusinessPlatformKeyValidator.keyValidate(id);
		
		int rCount = mapper.selectExistsCountWithID(id);
		if(rCount > 0) {
			return true;
		} else {
			return false;
		}
	}
	
	@Override
	public String getBusinessPlatformDomain(String keyString) throws ValidateException {
		BusinessPlatformKeyValidator.keyValidate(keyString);
		
		return mapper.selectBusinessPlatformDomainFromKey(keyString);
	}
	
	@Override
	public BusinessPlatformKey getBusinessPlatformKey(String keyString, String secret) throws ValidateException, NotSupportException {
		BusinessPlatformKeyValidator.keyAndSecretValidate(keyString, secret);
		
		return mapper.selectBusinessPlatformKeyWithKeyAndSecretString(keyString, secret);
	}
	
	@Override
	public boolean removeBusinessPlatformKey(String keyString, String secret) throws ValidateException, NotSupportException {
		BusinessPlatformKeyValidator.keyAndSecretValidate(keyString, secret);

		int removedRowCount = mapper.deleteBusinessPlatformKey(keyString, secret);
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
	public BusinessPlatformKey createBusinessPlatformKey(String description, String domain, String redirectUri) throws ValidateException, AlreadyExistException, DataBaseProcessingException, NoSuchAlgorithmException {
		
		BusinessPlatformKey businessPlatformKey = new BusinessPlatformKey();
		
		// Generate New Key & Secret
		businessPlatformKey.generateKeyAndSecret();
		
		businessPlatformKey.setDescription(description);
		businessPlatformKey.setDomain(domain);
		businessPlatformKey.setRedirectUri(redirectUri);
		
		int retCount = mapper.insertBusinessPlatformKey(businessPlatformKey);
		if(retCount < 1) {
			throw new DataBaseProcessingException("failed to add business platform key information");
		}
		return businessPlatformKey;
	}
	
	@Override
	public boolean modifyBusinessPlatformKey(String keyString, String secret, BusinessPlatformKey businessPlatformKey) throws ValidateException, NotSupportException {
		BusinessPlatformKeyValidator.keyAndSecretValidate(keyString, secret);
		if(businessPlatformKey == null) {
			throw new ValidateException("not found business platform key information");
		}
		
		businessPlatformKey.setKeyString(keyString);
		businessPlatformKey.setSecret(secret);
		
		int updatedRowCount = mapper.updateBusinessPlatformKey(businessPlatformKey);
		if(updatedRowCount > 0) {
			if(LOG.isWarnEnabled()) {
				if(updatedRowCount > 1) {
					LOG.warn("updated two or more rows than one,input parameter->[keyString:" + keyString + ",secret:" + secret + ",businessPlatformKey:" + businessPlatformKey + "],affectedRows->" + updatedRowCount);
				}
			}
			return true;
		} else {
			return false;
		}
	}
	
}
