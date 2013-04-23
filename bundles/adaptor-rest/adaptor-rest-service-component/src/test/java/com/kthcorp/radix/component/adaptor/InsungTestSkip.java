package com.kthcorp.radix.component.adaptor;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import com.kthcorp.radix.util.test.junit.MysqlEmbeddableSpringJUnit4ClassRunner;

@RunWith(MysqlEmbeddableSpringJUnit4ClassRunner.class)
@ContextConfiguration(
	locations = { "classpath:/META-INF/spring/spring-application-context.xml", 
				"classpath:/META-INF/spring/spring-camel-context-test.xml",
				"classpath:/META-INF/spring/spring-service-component-context.xml"})
public class InsungTestSkip extends AbstractProvider {
	@Test
	public void test() {
//		TO DO not implemented yet
		Assert.assertTrue(Boolean.TRUE);
	}
}
