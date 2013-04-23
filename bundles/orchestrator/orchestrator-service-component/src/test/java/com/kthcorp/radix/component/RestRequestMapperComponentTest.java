package com.kthcorp.radix.component;

import java.util.UUID;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import com.kthcorp.radix.util.UuidViewableLoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import com.kthcorp.radix.util.test.junit.MysqlEmbeddableSpringJUnit4ClassRunner;

import com.kthcorp.radix.api.service.ServiceManagerService;
import com.kthcorp.radix.domain.canonical.CanonicalMessage;
import com.kthcorp.radix.domain.canonical.CanonicalMessageHeader;
import com.kthcorp.radix.domain.canonical.CanonicalMessagePayload;
import com.kthcorp.radix.domain.canonical.request.OrchestratorRestRequest;
import com.kthcorp.radix.domain.exception.NotSupportException;
import com.kthcorp.radix.domain.exception.ValidateException;
import com.kthcorp.radix.domain.service.ServiceVersion;
import com.kthcorp.radix.domain.service.api.protocol.http.HttpMethod;
import com.kthcorp.radix.domain.service.api.protocol.http.ParameterMap;
import com.kthcorp.radix.util.FailedTest;
import com.kthcorp.radix.util.UUIDUtils;

@RunWith(MysqlEmbeddableSpringJUnit4ClassRunner.class)
@ContextConfiguration(
		locations = { "classpath:/META-INF/spring/spring-application-context.xml",
				"classpath:/META-INF/spring/spring-camel-context-test.xml",
				"classpath:/META-INF/spring/spring-dao-hazelcast-context.xml", 
				"classpath:/META-INF/spring/spring-dao-mybatis-context.xml", 
				"classpath:/META-INF/spring/spring-dao-mysql-context.xml", 
				"classpath:/META-INF/spring/spring-service-component-context.xml", 
				"classpath:/META-INF/spring/spring-service-context.xml",
				"classpath:/META-INF/spring/spring-util-context.xml",
				"classpath:/META-INF/spring/reply-adaptor-context.xml" 
				}
)
public class RestRequestMapperComponentTest {
	
	private static final Logger LOG = UuidViewableLoggerFactory.getLogger(RestRequestMapperComponentTest.class);
	private final byte[] serviceID = UUIDUtils.getBytes("2341ed57-8772-11e1-a8c6-f0def154de37");
	
	@Autowired
	private ServiceManagerService serviceManagerService;
	
	@Autowired
	private RestRequestMapperComponent restRequestMapperComponent;
	
	@Before
	public void load() {
//		serviceManagerService.loadAllService();
		serviceManagerService.loadService(serviceID);
	}
	
	@Test
	public void nullTest() {}
	
	@FailedTest
	public void parameterMappingTest() throws ValidateException, NotSupportException {
		
		CanonicalMessageHeader header = new CanonicalMessageHeader();
		header.setMessageId(UUID.randomUUID().toString());
		
		OrchestratorRestRequest orchestratorRestRequest = new OrchestratorRestRequest();
		orchestratorRestRequest.setHttpMethod(HttpMethod.GET);
		String uri = serviceManagerService.getURIString("api.withapi.com", HttpMethod.GET, "Facebook", new ServiceVersion("0"), "me");
		orchestratorRestRequest.setKey(uri);
		
		ParameterMap parameters = new ParameterMap();
		parameters.put("token", "abcdefg");
		orchestratorRestRequest.setParameters(parameters);
		LOG.info("Map before mapping: {}", parameters);
		CanonicalMessagePayload payload = new CanonicalMessagePayload();
		payload.setOrchestratorRequest(orchestratorRestRequest);
		
		CanonicalMessage canonicalMessage = new CanonicalMessage();
		canonicalMessage.setHeader(header);
		canonicalMessage.setPayload(payload);
		
		restRequestMapperComponent.mapMessage(canonicalMessage);
		OrchestratorRestRequest mappedOrchestratorRestRequest = (OrchestratorRestRequest) canonicalMessage.getPayload().getOrchestratorRequest();
		LOG.info("Map after mapping: {}", mappedOrchestratorRestRequest.getParameters());
		for(String key : mappedOrchestratorRestRequest.getParameters().keys()) {
			LOG.debug(key + ":" + mappedOrchestratorRestRequest.getParameters().get(key));
		}
	}
	
	@FailedTest
	public void testTargetMap() {
		
		CanonicalMessageHeader header = new CanonicalMessageHeader();
		header.setMessageId(UUID.randomUUID().toString());
		header.setCorrelationId(UUID.randomUUID().toString());
		
		OrchestratorRestRequest orchestratorRestRequest = new OrchestratorRestRequest();
		orchestratorRestRequest.setHttpMethod(HttpMethod.GET);
		String uri = serviceManagerService.getURIString("api.withapi.com", HttpMethod.GET, "Facebook", new ServiceVersion("0"), "me");
		orchestratorRestRequest.setKey(uri);
		
		ParameterMap parameters = new ParameterMap();
		parameters.put("token", "abcdefg");
		orchestratorRestRequest.setParameters(parameters);
		LOG.info("Map before mapping: {}", parameters);
		CanonicalMessagePayload payload = new CanonicalMessagePayload();
		payload.setOrchestratorRequest(orchestratorRestRequest);
		
		CanonicalMessage canonicalMessage = new CanonicalMessage();
		canonicalMessage.setHeader(header);
		canonicalMessage.setPayload(payload);
		
		restRequestMapperComponent.mapMessage(canonicalMessage);
		OrchestratorRestRequest mappedOrchestratorRestRequest = (OrchestratorRestRequest) canonicalMessage.getPayload().getOrchestratorRequest();
		LOG.info("Map after mapping: {}", mappedOrchestratorRestRequest.getParameters());
		for(String key : mappedOrchestratorRestRequest.getParameters().keys()) {
			LOG.debug(key + ":" + mappedOrchestratorRestRequest.getParameters().get(key));
		}
	}
}
