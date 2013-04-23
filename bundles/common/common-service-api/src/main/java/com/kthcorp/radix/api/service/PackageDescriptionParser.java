package com.kthcorp.radix.api.service;

import com.kthcorp.radix.domain.exception.ValidateException;
import com.kthcorp.radix.domain.packages.Packages;

public interface PackageDescriptionParser {
	
	public Packages getPackage(byte[] businessPlatformID, String partnerID, boolean isModify) throws ValidateException;
}
