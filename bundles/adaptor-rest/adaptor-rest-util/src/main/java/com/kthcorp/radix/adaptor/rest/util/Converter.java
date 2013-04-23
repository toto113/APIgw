package com.kthcorp.radix.adaptor.rest.util;

public interface Converter {
	
	<T> T convert(String key, String requestContentType, Object responseBody) throws Exception;
}
