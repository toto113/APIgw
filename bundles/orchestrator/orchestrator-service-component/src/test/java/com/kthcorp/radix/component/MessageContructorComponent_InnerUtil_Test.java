package com.kthcorp.radix.component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;

import org.junit.Test;

import com.kthcorp.radix.domain.exception.RadixException;


public class MessageContructorComponent_InnerUtil_Test {
	
	
	/**
	 * @throws RadixException
	 */
	@Test
	public void 메소드_InnerUtil_getPathParameter_확인() throws RadixException {
		String pathTemplate;
		String resourcePath;
		HashMap<String, String> expectedMap = new HashMap<String, String>();
		Map<? extends String, ? extends List<String>> actualMap;
		

		expectedMap.put("name1", "VALUE1");
		expectedMap.put("name2", "VALUE2");
		expectedMap.put("name3", "VALUE3");
		
		
		pathTemplate = "/some/path/arg1/{name1}/arg2/{name2}/pre{name3}";
		resourcePath = "/some/path/arg1/VALUE1/arg2/VALUE2/preVALUE3";
		actualMap = MessageConstructorComponent.InnerUtil.getPathParameterMap(pathTemplate, resourcePath);
		assertEquals(expectedMap, actualMap);
		
		
		pathTemplate = "/some/path/arg1/{name1}/arg2/{name2}/pre{name3}/";
		resourcePath = "/some/path/arg1/VALUE1/arg2/VALUE2/preVALUE3/";
		actualMap = MessageConstructorComponent.InnerUtil.getPathParameterMap(pathTemplate, resourcePath);
		assertEquals(expectedMap, actualMap);

		
		pathTemplate = "/{name1}/arg2/{name2}/pre{name3}";
		resourcePath = "/VALUE1/arg2/VALUE2/preVALUE3";
		actualMap = MessageConstructorComponent.InnerUtil.getPathParameterMap(pathTemplate, resourcePath);
		assertEquals(expectedMap, actualMap);

		
		pathTemplate = "{name1}/arg2/{name2}/pre{name3}";
		resourcePath = "VALUE1/arg2/VALUE2/preVALUE3";
		actualMap = MessageConstructorComponent.InnerUtil.getPathParameterMap(pathTemplate, resourcePath);
		assertEquals(expectedMap, actualMap);

		
		pathTemplate = "/some/path/arg1/{name1}/arg2/{name2}/pre{name3}/hi";
		resourcePath = "/some/path/arg1/VALUE1/arg2/VALUE2/preVALUE3/hi";
		actualMap = MessageConstructorComponent.InnerUtil.getPathParameterMap(pathTemplate, resourcePath);
		assertEquals(expectedMap, actualMap);

		
		expectedMap.clear();
		expectedMap.put("name1", "VALUE1");
		expectedMap.put("name2", "arg2/VALUE2");
		expectedMap.put("name3", "VALUE3");
		
		pathTemplate = "/some/path/arg1/{name1}/arg2/{name2}/pre{name3}";
		resourcePath = "/some/path/arg1/VALUE1/arg2/arg2/VALUE2/preVALUE3";
		actualMap = MessageConstructorComponent.InnerUtil.getPathParameterMap(pathTemplate, resourcePath);
		assertEquals(expectedMap, actualMap);
		
		
		expectedMap.clear();
		expectedMap.put("name1", "");
		expectedMap.put("name2", "");
		expectedMap.put("name3", "");
		
		pathTemplate = "/some/path/arg1/{name1}/arg2/{name2}/pre{name3}";
		resourcePath = "/some/path/arg1//arg2//pre";
		actualMap = MessageConstructorComponent.InnerUtil.getPathParameterMap(pathTemplate, resourcePath);
		assertEquals(expectedMap, actualMap);
		
		
		
		expectedMap.clear();
		expectedMap.put("key", "004bfaafeefb4ef986d9");
		expectedMap.put("module", "Map%2CGeocoder%2CDirection");
		
		pathTemplate = "/map/key/{key}/{module}";
		resourcePath = "/map/key/004bfaafeefb4ef986d9/Map%2CGeocoder%2CDirection";
		actualMap = MessageConstructorComponent.InnerUtil.getPathParameterMap(pathTemplate, resourcePath);
		assertEquals(expectedMap, actualMap);
		
		
		
		
		pathTemplate = "/some/path/arg1/{name1}/arg2/{name2}/pre{name3}";
		resourcePath = "/some/path/arg1/VALUE1/arg2/VALUE2/preVALUE3/hi";
		try {
			actualMap = MessageConstructorComponent.InnerUtil.getPathParameterMap(pathTemplate, resourcePath);
			Assert.fail("should be fail for invalid resourcePath");
		} catch(Throwable e) { }
		
	}
	
	private void assertEquals(HashMap<String, String> expectedMap, Map<? extends String, ? extends List<String>> actualMap) {
		Assert.assertNotNull(actualMap);
		Assert.assertEquals("incorrect map size", expectedMap.size(), actualMap.size());
		for(String name : expectedMap.keySet()) {
			Assert.assertEquals("incorret value for "+name, expectedMap.get(name), actualMap.get(name).get(0));
		}
	}

}