package com.kthcorp.radix.api.service;

import com.kthcorp.radix.domain.service.mapping.Mapping;

public interface ApiMapperService {
	
	public void mapping(Mapping specMap, Object target);
	
}
