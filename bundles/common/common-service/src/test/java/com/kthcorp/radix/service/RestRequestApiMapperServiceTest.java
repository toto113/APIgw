package com.kthcorp.radix.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.json.JSONException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.Logger;
import com.kthcorp.radix.util.UuidViewableLoggerFactory;

import com.kthcorp.radix.domain.canonical.request.OrchestratorRestRequest;
import com.kthcorp.radix.domain.service.api.protocol.http.ParameterMap;
import com.kthcorp.radix.domain.service.mapping.Mapping;

@RunWith(MockitoJUnitRunner.class)
public class RestRequestApiMapperServiceTest {
	
	private final Logger LOG = UuidViewableLoggerFactory.getLogger(this.getClass());

	@InjectMocks
	RestRequestApiMapperServiceImpl restRequestApiMapperService;
	
	private static final String jsonString = "{'name1':['mapping-name1', 'mapping-name1-1'], 'name2':['mapping-name2']}";
	private static Mapping mapping;
	ParameterMap parameters;
	
	@Before
	public void init() throws JSONException {
		
		mapping = Mapping.fromJSONString(jsonString);
		LOG.debug("mapping : {}", mapping);
	}
	
	@Test
	public void testMapping() throws JSONException {
		
		OrchestratorRestRequest request = new OrchestratorRestRequest();
		ParameterMap parameters = new ParameterMap();
		parameters.put("name1", "value1");
		parameters.put("name2", "value2");
		request.setParameters(parameters);
		
		for(String key : parameters.keys()) {
			LOG.debug("before : {}={}", key, parameters.get(key));
		}
		
		long start = System.nanoTime();
		restRequestApiMapperService.mapping(mapping, request);
		LOG.info("estimate : {}", System.nanoTime() - start);
		
		for(String key : parameters.keys()) {
			LOG.debug("after : {}={}", key, parameters.get(key));
		}
		
		assertTrue(parameters.containsKey("mapping-name1"));
		assertEquals(Arrays.asList("value1"), parameters.get("mapping-name1"));
		assertTrue(parameters.containsKey("mapping-name2"));
		assertEquals(Arrays.asList("value2"), parameters.get("mapping-name2"));
	}
	
	@Test
	public void testParamterMapping() throws JSONException {

		ParameterMap parameters = new ParameterMap();
		parameters.put("name1", "value1");
		parameters.put("name2", "value2");
		
		for(String key : parameters.keys()) {
			LOG.debug("before : {}={}", key, parameters.get(key));
		}
		
		long start = System.nanoTime();
		restRequestApiMapperService.mapping(mapping, parameters);
		LOG.info("estimate : {}", System.nanoTime() - start);
		
		for(String key : parameters.keys()) {
			LOG.debug("after : {}={}", key, parameters.get(key));
		}
		
		assertTrue(parameters.containsKey("mapping-name1"));
		assertEquals(Arrays.asList("value1"), parameters.get("mapping-name1"));
		assertTrue(parameters.containsKey("mapping-name2"));
		assertEquals(Arrays.asList("value2"), parameters.get("mapping-name2"));
	}
}
