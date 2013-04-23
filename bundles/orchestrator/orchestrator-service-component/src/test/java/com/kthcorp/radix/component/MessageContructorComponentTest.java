package com.kthcorp.radix.component;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import com.kthcorp.radix.util.UuidViewableLoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import com.kthcorp.radix.util.test.junit.MysqlEmbeddableSpringJUnit4ClassRunner;

import com.kthcorp.radix.api.service.ServiceManagerService;
import com.kthcorp.radix.domain.canonical.CanonicalMessage;
import com.kthcorp.radix.domain.canonical.CanonicalMessageHeader;
import com.kthcorp.radix.domain.canonical.CanonicalMessagePayload;
import com.kthcorp.radix.domain.canonical.request.ServiceControllerRestRequest;
import com.kthcorp.radix.domain.exception.RadixException;
import com.kthcorp.radix.domain.service.ServiceVersion;
import com.kthcorp.radix.domain.service.api.protocol.http.HttpMethod;
import com.kthcorp.radix.domain.service.api.protocol.http.ParameterMap;
import com.kthcorp.radix.util.FailedTest;

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
public class MessageContructorComponentTest extends AbstractJUnit4SpringContextTests {
	
	private static final Logger LOG = UuidViewableLoggerFactory.getLogger(MessageContructorComponentTest.class);
	
	@Autowired
	private ServiceManagerService serviceManagerService;
	
	@Autowired
	private MessageConstructorComponent messageConstructorComponent;
	
	@Before
	public void before() {
		serviceManagerService.loadAllService();
	}
	
	@Test
	public void nullTest() {}
	
	@FailedTest
	public void construct() throws RadixException {
		
		LOG.trace("Test:messageConstructorComponent.construct");
		
		CanonicalMessageHeader header = new CanonicalMessageHeader();
		String messageId = UUID.randomUUID().toString();
		header.setMessageId(messageId);
		Map<String, String> headerProperties = new HashMap<String, String>();
		header.setHeaderProperties(headerProperties);
		LOG.debug("header={}", header);
		
		ServiceControllerRestRequest request = new ServiceControllerRestRequest();
		String key = serviceManagerService.getURIString("api.withapi.com", HttpMethod.GET, "Facebook", new ServiceVersion("0"), "me");
		LOG.debug("uri={}", key);
		request.setKey(key);
		ParameterMap parameters = new ParameterMap();
		request.setParameters(parameters);
		LOG.debug("request={}", request);
		
		CanonicalMessagePayload payload = new CanonicalMessagePayload();
		payload.setRequest(request);
		LOG.debug("payload={}", payload);
		
		CanonicalMessage canonicalMessage = new CanonicalMessage();
		canonicalMessage.setHeader(header);
		canonicalMessage.setPayload(payload);
	
		LOG.debug("canonicalMessage={}", canonicalMessage);
		CanonicalMessage ret = messageConstructorComponent.construct(canonicalMessage);
		Assert.assertNotNull(ret);
		Assert.assertNotNull(ret.getPayload().getOrchestratorRequest());
		if(ret.getPayload() != null) {
			LOG.info("payload={}", ret.getPayload());
			LOG.info("request={}", ret.getPayload().getRequest());
			LOG.info("orchestratorRequest={}", ret.getPayload().getOrchestratorRequest());
			LOG.info("adaptorReply={}", ret.getPayload().getAdaptorReply());
			LOG.info("reply={}", ret.getPayload().getReply());
		}
	}
}
