package com.kthcorp.radix.dao.hazelcast;

import java.util.UUID;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import com.kthcorp.radix.util.test.junit.MysqlEmbeddableSpringJUnit4ClassRunner;

import com.kthcorp.radix.api.dao.SessionManagerDao;
import com.kthcorp.radix.domain.canonical.CanonicalMessage;
import com.kthcorp.radix.domain.canonical.CanonicalMessageHeader;
import com.kthcorp.radix.domain.canonical.CanonicalMessagePayload;

@RunWith(MysqlEmbeddableSpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { 
		"classpath:/META-INF/spring/spring-application-context-test.xml", 
		"classpath:/META-INF/spring/spring-dao-hazelcast-context.xml" })
public class HazelcastSessionManagerDaoTest extends AbstractJUnit4SpringContextTests {
	
	@Autowired
	private SessionManagerDao sessionManagerDao;
	
	@Test
	public void insertSession() {
		String messageId = UUID.randomUUID().toString();
		
		CanonicalMessage canonicalMessage = this.createCanonicalMessage(messageId);
		
		this.sessionManagerDao.insertSession(canonicalMessage);
		
		CanonicalMessage ret = this.sessionManagerDao.selectSession(messageId);
		Assert.assertTrue(ret.getHeader().getMessageId().equals(messageId));
		
		this.sessionManagerDao.deleteSession(messageId);
	}
	
	private CanonicalMessage createCanonicalMessage(String messageId) {
		CanonicalMessageHeader header = new CanonicalMessageHeader();
		header.setMessageId(messageId);
		
		CanonicalMessagePayload payload = new CanonicalMessagePayload();
		
		CanonicalMessage canonicalMessage = new CanonicalMessage();
		canonicalMessage.setHeader(header);
		canonicalMessage.setPayload(payload);
		
		return canonicalMessage;
	}
	
	@Test
	public void updateSession() {
		String messageId = UUID.randomUUID().toString();
		
		CanonicalMessage canonicalMessage = this.createCanonicalMessage(messageId);
		
		this.sessionManagerDao.insertSession(canonicalMessage);
		
		CanonicalMessage ret = this.sessionManagerDao.selectSession(messageId);
		Assert.assertTrue(ret.getHeader().getMessageId().equals(messageId));
		
		ret.getHeader().getHeaderProperties().put("replyTo", "service-controll-a");
		
		this.sessionManagerDao.updateSession(messageId, ret);
		CanonicalMessage retUpdated = this.sessionManagerDao.selectSession(messageId);
		Assert.assertTrue(retUpdated.getHeader().getHeaderProperties().get("replyTo").equals("service-controll-a"));
		
		this.sessionManagerDao.deleteSession(messageId);
	}
}
