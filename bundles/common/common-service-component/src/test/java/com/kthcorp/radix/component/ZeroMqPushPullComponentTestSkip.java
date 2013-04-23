package com.kthcorp.radix.component;

import java.util.HashMap;
import java.util.UUID;

import junit.framework.Assert;

import org.apache.camel.ConsumerTemplate;
import org.apache.camel.ExchangePattern;
import org.apache.camel.ProducerTemplate;
import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import com.kthcorp.radix.util.test.junit.MysqlEmbeddableSpringJUnit4ClassRunner;

import com.kthcorp.radix.domain.canonical.CanonicalMessage;
import com.kthcorp.radix.domain.canonical.CanonicalMessageHeader;
import com.kthcorp.radix.domain.canonical.CanonicalMessagePayload;
import com.kthcorp.radix.util.FailedTest;

@Ignore
@RunWith(MysqlEmbeddableSpringJUnit4ClassRunner.class)
@ContextConfiguration(
		locations = { 
				"classpath:/META-INF/spring/spring-application-context.xml", 
				"classpath:/META-INF/spring/zeromq/spring-camel-context-test.xml", 
				"classpath:/META-INF/spring/zeromq/spring-service-component-context-test.xml" 
		}
)
public class ZeroMqPushPullComponentTestSkip extends AbstractJUnit4SpringContextTests {
	
	@Autowired
	@Qualifier("producerTemplate")
	private ProducerTemplate producer;
	
	@Autowired
	@Qualifier("consumerTemplate")
	private ConsumerTemplate consumer;
	
	
	@FailedTest
	public void sendMessageViaZeroMq() {
		CanonicalMessageHeader header = new CanonicalMessageHeader();
		String messageId = UUID.randomUUID().toString();
		header.setMessageId(messageId);
		
		CanonicalMessagePayload payload = new CanonicalMessagePayload();
		
		CanonicalMessage canonicalMessage = new CanonicalMessage();
		canonicalMessage.setHeader(header);
		canonicalMessage.setPayload(payload);
		
		this.producer.sendBodyAndHeaders("seda:in", ExchangePattern.InOnly, canonicalMessage, new HashMap<String, Object>());
		
		CanonicalMessage ret = (CanonicalMessage) this.consumer.receiveBody("seda:result", 3000);
		Assert.assertTrue(ret.getHeader().getMessageId().equals(messageId));
	}
}
