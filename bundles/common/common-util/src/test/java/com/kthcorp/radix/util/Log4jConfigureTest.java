package com.kthcorp.radix.util;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import com.kthcorp.radix.util.test.junit.MysqlEmbeddableSpringJUnit4ClassRunner;

import org.springframework.util.Assert;

@RunWith(MysqlEmbeddableSpringJUnit4ClassRunner.class)
@ContextConfiguration(
		locations = { "classpath:/META-INF/spring/spring-empty-context.xml" }
)
public class Log4jConfigureTest extends AbstractJUnit4SpringContextTests {

	@Test
	public void test() {
		
		Log4jConfigurer log4jConfigurer = new Log4jConfigurer("/log4j.xml");
		Assert.notNull(log4jConfigurer);
	}
}
