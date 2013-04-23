package com.kthcorp.radix.util;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import com.kthcorp.radix.util.test.junit.MysqlEmbeddableSpringJUnit4ClassRunner;

@RunWith(MysqlEmbeddableSpringJUnit4ClassRunner.class)
@ContextConfiguration(
		locations = { "classpath:META-INF/spring/spring-application-context.xml" }
)
public class HazelcastClusterInstanceCreatorTest extends AbstractJUnit4SpringContextTests {
	
	@SuppressWarnings("unused")
	private static final Logger LOG = UuidViewableLoggerFactory.getLogger(StartStopMainTest.class);
	
	private HazelcastClusterInstanceCreator hazelcastClusterInstanceCreator;
	
	@Test
	public void test() {
		
		hazelcastClusterInstanceCreator = new HazelcastClusterInstanceCreator();
		hazelcastClusterInstanceCreator.setLiteMember(Boolean.FALSE);
		hazelcastClusterInstanceCreator.setConfigLocation("hazelcast.xml");
		hazelcastClusterInstanceCreator.afterPropertiesSet();
		hazelcastClusterInstanceCreator.destroy();
	}
}
