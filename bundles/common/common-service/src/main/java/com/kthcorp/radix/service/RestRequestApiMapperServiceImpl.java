package com.kthcorp.radix.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import com.kthcorp.radix.api.service.ApiMapperService;
import com.kthcorp.radix.domain.canonical.request.OrchestratorRestRequest;
import com.kthcorp.radix.domain.service.api.protocol.http.ParameterMap;
import com.kthcorp.radix.domain.service.mapping.Mapping;

public class RestRequestApiMapperServiceImpl implements ApiMapperService {
	
	@Override
	public void mapping(Mapping specMap, Object target) {
		// TODO Auto-generated method stub
		OrchestratorRestRequest request = (OrchestratorRestRequest) target;
		ParameterMap targetMap = (ParameterMap) request.getParameters();
		
		Set<String> specFroms = specMap.getKeys();
		Collection<String> specTos = null;
		String specTo = null;
		
		Collection<String> valueInTarget = null;
		int i = 0;
		String mappingType;
		for(String specFrom : specFroms) {
			if(specFrom.startsWith("RadixMapping")){
				String[] radixMappingFunctions = specFrom.split(":");
				mappingType=radixMappingFunctions[1];
				if(mappingType.equalsIgnoreCase("Const")){
					valueInTarget = new ArrayList<String>();
					valueInTarget.add(radixMappingFunctions[2]);
				}
			}else{
				valueInTarget = targetMap.get(specFrom);
			}
			
			// 정의된 파라매터가 없는 경우이다. required가 아닐 경우. 요럴때 default 값으로 ""를 사용한다.
			if(valueInTarget==null) {
				valueInTarget = new ArrayList<String>();
				valueInTarget.add("");
			}
			
			String[] valueInTargetClone = new String[valueInTarget.size()];


			for(String temp : valueInTarget) {
				valueInTargetClone[i++] = temp;
			}
			targetMap.remove(specFrom);
			
			specTos = specMap.mapTo(specFrom);
			specTo = specTos.toArray()[0].toString();
			
			for(String temp : valueInTargetClone) {
				targetMap.put(specTo, temp);
			}
			i = 0;
		}
	}
	
	public void mapping(Mapping mapping, ParameterMap parameters) {
		
		for(String key : mapping.getKeys()) {
			if(parameters.containsKey(key)) {
				String name = mapping.mapTo(key).toArray()[0].toString();
				List<String> value = parameters.get(key);
				parameters.remove(key);
				parameters.put(name, value);
			}
		}
	}
}
