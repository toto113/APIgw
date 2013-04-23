package com.kthcorp.radix.domain.service.api;

import java.io.Serializable;

public class ValueObject implements Serializable {
	
	private static final long serialVersionUID = -7718228762008524136L;
	private ValueGenerator valueGenerator;
	private String resource;
	
	public ValueGenerator getValueGenerator() {
		return valueGenerator;
	}
	
	public void setValueGenerator(ValueGenerator valueGenerator) {
		this.valueGenerator = valueGenerator;
	}
	
	public String getResource() {
		return resource;
	}
	
	public void setResource(String resource) {
		this.resource = resource;
	}
	
	@Override
	public String toString() {
		return resource;
	}
	
}
