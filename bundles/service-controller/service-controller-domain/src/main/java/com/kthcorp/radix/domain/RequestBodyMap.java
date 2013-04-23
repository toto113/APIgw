package com.kthcorp.radix.domain;

import java.util.HashMap;

import org.springframework.util.StringUtils;

import com.kthcorp.radix.domain.exception.ValidateException;

public class RequestBodyMap<K, V> extends HashMap<K, V> {
	
	// private static final Logger LOG = LoggerFactoryForUuid.getLogger(RequestBodyMap.class);
	
	// for serialization
	private static final long serialVersionUID = -1337125117323324313L;
	
	public void validate(String[][] keys) throws ValidateException {
		if(keys != null) {
			for(String[] key : keys) {
				if(this.containsKey(key[0])) {
					if(this.get(key[0]) != null) {
						String value = this.get(key[0]).toString();
						if(!StringUtils.hasText(value)) {
							throw new ValidateException("parameter->" + key[0] + "'s value does not exists");
						} else {
							if(key.length > 1) {
								int maxLength = Integer.valueOf(key[1]);
								if(maxLength > 0) {
									if(value.length() > maxLength) {
										throw new ValidateException("parameter->" + key[0] + "'s value is oversize(max:" + maxLength + ")");
									}
								}
							}
							// Validated
							continue;
						}
					} else {
						throw new ValidateException("parameter->" + key[0] + "'s value is null");
					}
				} else {
					throw new ValidateException("parameter->" + key[0] + " does not exists");
				}
			}
		}
	}
	
}
