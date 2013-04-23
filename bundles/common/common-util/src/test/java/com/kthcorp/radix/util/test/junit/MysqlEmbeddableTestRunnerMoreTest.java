package com.kthcorp.radix.util.test.junit;

import static org.junit.Assert.assertFalse;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;

import com.kthcorp.radix.util.test.EmbeddedMysql;

/**
 * 요 테스트 케이스는 MysqlEmbeddableTestRunnerTest와 쌍이다.
 * 테스트 케이스 자체의 ContextConfiguration을 가지고 embedded mysql의 구동 여부가 결정되기 때문에
 * 별개의 다른 테스트케이스 클래스에서 테스트 하여야 한다.
 *
 */
@RunWith(MysqlEmbeddableSpringJUnit4ClassRunner.class)
@ContextConfiguration(
		locations = {
				// 요 테스트케이스는 mysql을 사용하지 않는 경우이다.
				// 그래서 mysql 설정파일을 커멘트아웃하고 싶은데... 아무것도 설정이 없으면 뻑난다.
				// 그래서 비어있는 설정파일하나를 설정한다.
				"classpath:com/kthcorp/radix/util/test/junit/MysqlEmbeddableTestRunnerMoreTest_resource/spring-empty-context.xml"
		}
)
public class MysqlEmbeddableTestRunnerMoreTest {

	@Test
	public void EmbeddedMysql이_구동되지_않아야_한다() {
		assertFalse("mysql is running", EmbeddedMysql.isRunning());
	}

}
