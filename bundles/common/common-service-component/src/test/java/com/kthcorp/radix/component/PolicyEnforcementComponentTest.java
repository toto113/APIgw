package com.kthcorp.radix.component;

import java.util.HashMap;
import java.util.Map;

import org.apache.camel.ConsumerTemplate;
import org.apache.camel.ExchangePattern;
import org.apache.camel.ProducerTemplate;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import com.kthcorp.radix.util.UuidViewableLoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import com.kthcorp.radix.util.test.junit.MysqlEmbeddableSpringJUnit4ClassRunner;

import com.kthcorp.radix.domain.canonical.CanonicalMessage;
import com.kthcorp.radix.domain.canonical.CanonicalMessageHeader;
import com.kthcorp.radix.domain.canonical.CanonicalMessagePayload;
import com.kthcorp.radix.domain.canonical.request.ServiceControllerRestRequest;

@RunWith(MysqlEmbeddableSpringJUnit4ClassRunner.class)
@ContextConfiguration(
		locations = { 
			"classpath:/META-INF/spring/policy/spring-application-context-test.xml",
			"classpath:/META-INF/spring/policy/spring-camel-context-test.xml",
			"classpath:/META-INF/spring/policy/spring-dao-mybatis-context-test.xml",
			"classpath:/META-INF/spring/policy/spring-service-component-context-test.xml",
			"classpath:/META-INF/spring/policy/spring-service-context-test.xml",
			"classpath:/META-INF/spring/policy/spring-all-context-test.xml"
		}
)
public class PolicyEnforcementComponentTest extends AbstractJUnit4SpringContextTests {
	
	@SuppressWarnings("unused")
	private final Logger LOG = UuidViewableLoggerFactory.getLogger(PolicyEnforcementComponentTest.class);
	
	@Autowired
	@Qualifier("producerTemplate")
	private ProducerTemplate producer;
	
	@SuppressWarnings("unused")
	@Autowired
	@Qualifier("consumerTemplate")
	private ConsumerTemplate consumer;
	
	@Test
	public void initializingPolicyStatusTest() {
		// Setting CanonicalMessageHeader
		String messageId = "MSGID_POLICY_TEST_0001";
		Map<String, String> headerProperties = new HashMap<String, String>();
		headerProperties.put("clientID", "1");
		
		CanonicalMessageHeader cmHeader = new CanonicalMessageHeader();
		cmHeader.setMessageId(messageId);
		cmHeader.setHeaderProperties(headerProperties);
		
		// Setting CanonicalMessagePayload
		ServiceControllerRestRequest serviceControllerRestRequest = new ServiceControllerRestRequest();
		serviceControllerRestRequest.setKey("api.withapi.com::GET:/PublicTransport/1/BusLane");
		
		CanonicalMessagePayload payload = new CanonicalMessagePayload();
		payload.setRequest(serviceControllerRestRequest);
		
		// Setting CanonicalMessage
		CanonicalMessage canonicalMessage = new CanonicalMessage();
		canonicalMessage.setHeader(cmHeader);
		canonicalMessage.setPayload(payload);
		
		// Sending to policyenforcementcomponent
		this.producer.sendBodyAndHeaders("seda:policyIn", ExchangePattern.InOnly, canonicalMessage, new HashMap<String, Object>());
		
	}
}
