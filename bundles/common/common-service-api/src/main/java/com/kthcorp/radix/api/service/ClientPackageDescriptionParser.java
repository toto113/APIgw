package com.kthcorp.radix.api.service;

import java.util.List;

import com.kthcorp.radix.domain.client.CPackages;
import com.kthcorp.radix.domain.exception.ValidateException;

public interface ClientPackageDescriptionParser {
	
	List<CPackages> getPackageList(final byte[] clientID) throws ValidateException;
}
