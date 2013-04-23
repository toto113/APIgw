package com.kthcorp.radix.domain;

import java.util.ArrayList;

import com.kthcorp.radix.domain.exception.ValidateException;

public class RequestResource<V> extends ArrayList<V> {
	
	// for serialization
	private static final long serialVersionUID = -1337125227323324313L;
	
	public void validate(int needParamCount) throws ValidateException {
		if(this.size() < needParamCount) {
			throw new ValidateException("resource parameters does not exists or need more");
		}
	}
}
