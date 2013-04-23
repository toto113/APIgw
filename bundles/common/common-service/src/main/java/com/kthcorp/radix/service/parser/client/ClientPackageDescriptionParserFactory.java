package com.kthcorp.radix.service.parser.client;

import java.util.Locale;

import com.kthcorp.radix.api.service.ClientPackageDescriptionParser;
import com.kthcorp.radix.domain.exception.ValidateException;

public final class ClientPackageDescriptionParserFactory {
	
	private static ClientPackageDescriptionParserFactory instance = new ClientPackageDescriptionParserFactory();
	
	private ClientPackageDescriptionParserFactory() {
	}
	
	public static ClientPackageDescriptionParserFactory newInstance() {
		return instance;
	}
	
	public ClientPackageDescriptionParser createParser(final String contentType, final String source) throws ValidateException {
		
		/* Precondition check */
		if(contentType == null || contentType.length() < 1) {
			throw new ValidateException("contentType is null or not found");
		}
		
		if(source == null || source.length() < 1) {
			throw new ValidateException("input source does not exist");
		}
		
		/* Create Parser */
		final String contentTypeLC = contentType.toLowerCase(Locale.ENGLISH);
		
		/* TODO: We support the description in XML type only, but we will support variable types. */
		if("application/xml".equals(contentTypeLC)) {
			return new ClientPackageDescriptionXMLParserImpl(source);
		} else {
			throw new ValidateException("contentType(" + contentType + ") does not support");
		}
		
	}
	
}
