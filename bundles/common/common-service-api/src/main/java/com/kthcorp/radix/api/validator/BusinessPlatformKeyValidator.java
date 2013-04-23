package com.kthcorp.radix.api.validator;

import com.kthcorp.radix.domain.exception.ValidateException;

public final class BusinessPlatformKeyValidator {
	
	private static BusinessPlatformKeyValidator instance = new BusinessPlatformKeyValidator();
	
	private BusinessPlatformKeyValidator() {
	}
	
	public static BusinessPlatformKeyValidator getInstance() {
		return instance;
	}
	
	public static void keyValidate(final byte[] key) throws ValidateException {
		if(key == null) {
			throw new ValidateException("key must not be null");
		}
		if(key.length != 16) {
			throw new ValidateException("key is invalid(length)");
		}
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
	
	public static void keyAndSecretValidate(final String keyString, final String secret) throws ValidateException {
		if(keyString == null) {
			throw new ValidateException("KeyString must not be null");
		}
		if(keyString.length() == 0) {
			throw new ValidateException("KeyString's length should be above zero");
		}
		if(keyString.length() != 36) {
			throw new ValidateException("KeyString("+keyString+") is invalid(length)");
		}
		if(secret == null) {
			throw new ValidateException("secret must not be null");
		}
		if(secret.length() == 0) {
			throw new ValidateException("secret length should be above zero");
		}
		if(secret.length() != 40) {
			throw new ValidateException("secret("+secret+") is invalid(length)");
		}
	}
}
