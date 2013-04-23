package com.kthcorp.radix.dao.ehcache;

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
@ContextConfiguration(locations = { "classpath:/META-INF/spring/spring-application-context-test.xml", "classpath:/META-INF/spring/spring-dao-ehcache-context-test.xml" })
public class EhcacheSessionManagerDaoTest extends AbstractJUnit4SpringContextTests {
	
	@Autowired
	private SessionManagerDao sessionManagerDao;
	
	@Test
	public void addSession() {
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
		String correlationId = UUID.randomUUID().toString();
		
		CanonicalMessage canonicalMessage = this.createCanonicalMessage(correlationId);
		
		this.sessionManagerDao.insertSession(canonicalMessage);
		
		CanonicalMessage ret = this.sessionManagerDao.selectSession(correlationId);
		Assert.assertTrue(ret.getHeader().getMessageId().equals(correlationId));
	
		// Fail Test
		CanonicalMessage ret2 = this.sessionManagerDao.selectSession("wrongID");
		Assert.assertNull(ret2);
		
		ret.getHeader().getHeaderProperties().put("replyTo", "service-controll-a");
		
		this.sessionManagerDao.updateSession(correlationId, ret);
		CanonicalMessage retUpdated = this.sessionManagerDao.selectSession(correlationId);
		Assert.assertTrue(retUpdated.getHeader().getHeaderProperties().get("replyTo").equals("service-controll-a"));

		
		
		this.sessionManagerDao.deleteSession(correlationId);
	}
	
}
