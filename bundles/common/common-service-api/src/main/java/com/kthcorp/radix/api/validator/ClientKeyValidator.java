package com.kthcorp.radix.api.validator;

import com.kthcorp.radix.domain.client.ClientKeyType;
import com.kthcorp.radix.domain.exception.ValidateException;

public final class ClientKeyValidator {
	
	private static ClientKeyValidator instance = new ClientKeyValidator();
	
	private ClientKeyValidator() {
		
	}
	
	public static ClientKeyValidator getInstance() {
		return instance;
	}
	
	public static void keyValidate(final String keyString) throws ValidateException {
		if(keyString == null) {
			throw new ValidateException("KeyString must not be null");
		}
		if(keyString.length() == 0) {
			throw new ValidateException("KeyString's length should be above zero");
		}
		if(keyString.length() != 36) {
			throw new ValidateException("KeyString is invalid(length)");
		}
	}
	
	public static void keyValidate(final byte[] key) throws ValidateException {
		if(key == null) {
			throw new ValidateException("key must not be null");
		}
		if(key.length != 16) {
			throw new ValidateException("key is invalid");
		}
	}
	
	public static void keyAndSecretValidate(final String keyString, final String secret) throws ValidateException {
		if(keyString == null) {
			throw new ValidateException("KeyString must not be null");
		}
		if(keyString.length() == 0) {
			throw new ValidateException("KeyString's length should be above zero");
		}
		if(keyString.length() != 36) {
			throw new ValidateException("KeyString is invalid(length)");
		}
		if(secret == null) {
			throw new ValidateException("secret must not be null");
		}
		if(secret.length() == 0) {
			throw new ValidateException("secret length should be above zero");
		}
		if(secret.length() != 40) {
			throw new ValidateException("secret is invalid(length)");
		}
	}
	
	public static void propertyValidator(byte[] businessPlatformID, String partnerID, ClientKeyType clientKeyType) throws ValidateException {
		if(businessPlatformID == null) {
			throw new ValidateException("businessPlatformID must not be null");
		}
		if(businessPlatformID.length != 16) {
			throw new ValidateException("businessPlatformID is invalid");
		}
		if(partnerID == null) {
			throw new ValidateException("partnerID must not be null");
		}
		if(partnerID.length() == 0) {
			throw new ValidateException("partnerID length should be above zero");
		}
		if(clientKeyType == null) {
			throw new ValidateException("clientKeyType must not be null");
		}
	}
}
