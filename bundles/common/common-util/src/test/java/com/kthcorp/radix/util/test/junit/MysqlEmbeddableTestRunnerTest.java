package com.kthcorp.radix.util.test.junit;

import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;

import com.kthcorp.radix.util.test.EmbeddedMysql;

@RunWith(MysqlEmbeddableSpringJUnit4ClassRunner.class)
@ContextConfiguration(
		locations = {
				"classpath:/META-INF/spring/spring-dao-mysql-context.xml"
		}
)
public class MysqlEmbeddableTestRunnerTest {

	// 실제 테스트할 사항은 @BeforeClass, @AfterClass, @Before, @After에 의해 EmbeddedMysql이 잘 처리되는 것이다.
	// 그래서 테스트 케이스 메소드 자체는 따로 할게 없다.
	@Test
	public void EmbeddedMysql이_잘_구동되었는_지_확인() {
		assertTrue("mysql not running", EmbeddedMysql.isRunning());
	}

}
