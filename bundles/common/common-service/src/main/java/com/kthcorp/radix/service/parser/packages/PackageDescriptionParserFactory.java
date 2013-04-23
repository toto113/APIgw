package com.kthcorp.radix.service.parser.packages;

import java.util.Locale;

import com.kthcorp.radix.api.service.PackageDescriptionParser;
import com.kthcorp.radix.api.service.PolicyManagerService;
import com.kthcorp.radix.domain.exception.ValidateException;

public final class PackageDescriptionParserFactory {
	
	public static PackageDescriptionParser createParser(final String contentType, final String source, PolicyManagerService policyManagerService) throws ValidateException {
		
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
			return new PackageDescriptionXMLParserImpl(source, policyManagerService);
		} else {
			throw new ValidateException("contentType(" + contentType + ") does not support");
		}
		
	}
	
}
