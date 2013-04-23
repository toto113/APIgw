package com.kthcorp.radix.util;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import com.kthcorp.radix.util.test.junit.MysqlEmbeddableSpringJUnit4ClassRunner;
import org.springframework.util.Assert;
import org.zeromq.ZMQ.Socket;

import com.kthcorp.radix.util.ZeroMqSocketFactory.ZeroMqSocketType;

@Ignore
@RunWith(MysqlEmbeddableSpringJUnit4ClassRunner.class)
@ContextConfiguration(
		locations = { "classpath:META-INF/spring/spring-application-context.xml" }
)
public class ZeroMqSocketFactoryTestSkip extends AbstractJUnit4SpringContextTests {
	
	private static final Logger LOG = UuidViewableLoggerFactory.getLogger(ZeroMqSocketFactoryTestSkip.class);
	
	private static Socket serverSocket;
	
	@Before
	public void bindServer() throws Exception {
		
		ZeroMqSocketFactory zeroMqSocketFactory = new ZeroMqSocketFactory();
		zeroMqSocketFactory.setUrl("tcp://localhost:5000");
		zeroMqSocketFactory.setZeroMqSocketType(ZeroMqSocketType.PUSH);
		zeroMqSocketFactory.setBinding(Boolean.FALSE);
		zeroMqSocketFactory.afterPropertiesSet();
		Socket serverSocket = zeroMqSocketFactory.getObject();
		LOG.info("serverSocket={}", serverSocket);
		Assert.notNull(serverSocket);
	}
	
	@After
	public void closeServer() {

		if(serverSocket != null) {
			serverSocket.close();
			serverSocket = null;
		}
		LOG.info("serverSocket={}", serverSocket);
	}
	
	@Test
	public void test() throws Exception {
		
		ZeroMqSocketFactory zeroMqSocketFactory = new ZeroMqSocketFactory();
		zeroMqSocketFactory.setUrl("tcp://localhost:5000");
		zeroMqSocketFactory.setZeroMqSocketType(ZeroMqSocketType.PULL);
		zeroMqSocketFactory.setBinding(Boolean.FALSE);
		zeroMqSocketFactory.afterPropertiesSet();
		LOG.info("zeroMqSocketFactory={}", zeroMqSocketFactory);
		LOG.info("isSingleton={}", zeroMqSocketFactory.isSingleton());
		LOG.info("objectType={}", zeroMqSocketFactory.getObjectType());
		Socket socket = zeroMqSocketFactory.getObject();
		LOG.info("socket={}", socket);
		Assert.notNull(socket);
		socket.close();
		socket = null;
		LOG.info("socket={}", socket);
		Assert.isNull(socket);
	}
}
