package com.kthcorp.radix.util.test.junit;

import java.lang.reflect.Field;

import org.apache.commons.dbcp.BasicDataSource;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Statement;
import org.slf4j.Logger;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.TestContextManager;
import org.springframework.test.context.TestExecutionListener;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.kthcorp.radix.util.UuidViewableLoggerFactory;
import com.kthcorp.radix.util.test.EmbeddedMysql;

public class MysqlEmbeddableSpringJUnit4ClassRunner extends SpringJUnit4ClassRunner {

	private static final String DATA_SOURCE_BEAN_NAME = "dataSource";

	private static final Logger LOG = UuidViewableLoggerFactory.getLogger(MysqlEmbeddableSpringJUnit4ClassRunner.class);

	public MysqlEmbeddableSpringJUnit4ClassRunner(Class<?> clazz) throws InitializationError {
		super(clazz);
	}

	private boolean isEmbeddedMysqlStarted = false;

	@Override
	protected Statement withBeforeClasses(Statement statement) {

		TestContextManager testContextManager = this.getTestContextManager();
		TestExecutionListener testExecutionListener = new MyTestExecutionListener(testContextManager);
		testContextManager.registerTestExecutionListeners(testExecutionListener);

		return super.withBeforeClasses(statement);
	}


	private class MyTestExecutionListener implements TestExecutionListener {

		TestContextManager testContextManager = null;
		public MyTestExecutionListener(TestContextManager testContextManager) {
			this.testContextManager = testContextManager;
		}

		@Override
		public void beforeTestClass(TestContext testContext) throws Exception {
			
			if(isUsingMysql()) {
				try {
					EmbeddedMysql.startupDbms();
					isEmbeddedMysqlStarted = true;
				} catch (Exception e) {
					LOG.debug("starting embedded mysql failed.", e);
				}
				modifyMysqlDataSourceDefinition();
			}
			
		}

		@Override
		public void afterTestClass(TestContext testContext) throws Exception {
			if(isUsingMysql()&&isEmbeddedMysqlStarted) {
				try {
					EmbeddedMysql.shutdownDbms();
					isEmbeddedMysqlStarted = false;
				} catch (Exception e) {
					LOG.debug("shutdown embedded mysql failed.", e);
				}
			}
		}
		
		@Override
		public void prepareTestInstance(TestContext testContext) throws Exception {
			
		}

		@Override
		public void beforeTestMethod(TestContext testContext) throws Exception {
			if(isUsingMysql()&&isEmbeddedMysqlStarted) {
				try {
					EmbeddedMysql.prepareDataBase(EmbeddedMysql.DB_DUMP_FILE);
					isEmbeddedMysqlStarted = true;
				} catch (Exception e) {
					LOG.debug("preparing database failed.", e);
				}
			}
			
		}

		@Override
		public void afterTestMethod(TestContext testContext) throws Exception {
			if(isUsingMysql()&&isEmbeddedMysqlStarted) {
				try {
					EmbeddedMysql.clearDataBase();
					isEmbeddedMysqlStarted = true;
				} catch (Exception e) {
					LOG.debug("clearing database failed.", e);
				}
			}
			
		}


		private void modifyMysqlDataSourceDefinition() {
			modifyMysqlDataSourceDefintion_for_BasicDataSource();
		}


		// dataSource bean에서 사용되는 클래스가 BasicDataSource일 때의 처리 메소드.
		// 그래서 클래스 BasicDataSource가  하드코딩 되어 있다.
		// 물론 다른 클래스가 사용된다면 요런 식의 메소드가 그에 맞게 다시 작성되어야 한다.
		private void modifyMysqlDataSourceDefintion_for_BasicDataSource() {
			BasicDataSource basicDataSource = null;
			try {
				basicDataSource = (BasicDataSource)getLoadedBean(org.apache.commons.dbcp.BasicDataSource.class);
			} catch (NoSuchBeanDefinitionException e) {
				LOG.debug("can't find bean of class "+BasicDataSource.class.getName()+". if the name changed in spring-dao-mysql-context.xml, change name in "+this.getClass().getSimpleName());
				return;
			} catch(Exception e) {
				// ignore : reflection으로 구하지 못하는 경우는 어쩔 수 없다.
				return;
			}
			String newUrl = EmbeddedMysql.BASE_URL+"/"+EmbeddedMysql.DEFAULT_DB_NAME;
			// 사실 요기서 이렇게 설정을 변경하는 것이 제대로 동작하는 것을 보장하지 못한다.
			// 이미 빈이 로딩된 상태이고, 다른 클래스에서 사용한 상태일 수도 있다.
			// 그런데 다행이 잘 적용되네.
			// 다시 말하면 어떤 경우에는 적용되지 않을 수 있는 불안한 코드이다.
			// 하지만 빈 로딩 전에 설정을 변경할 손쉬운 방법이 없네.
			basicDataSource.setUsername(EmbeddedMysql.RADIX_USER_NAME);
			basicDataSource.setPassword(EmbeddedMysql.RADIX_PASSWORD);			
			basicDataSource.setUrl(newUrl);
			
		}
		
		
		// 현재 실행중인 테스트 케이스가 mysql을 사용하고 있는 지 파악한다.
		// 로딩된 빈 중에 'dataSource'이름의 빈이 있는 지 확인하는 방법으로.
		// 물론 설정에서 그 빈의 이름이 바뀌면 요 코드도 바뀌어야 하네. 요건 아름답진 않네.
		private boolean isUsingMysql() {
			try {
				getLoadedBean(DATA_SOURCE_BEAN_NAME);
			} catch (NoSuchBeanDefinitionException e) {
				LOG.debug("can't find bean of name 'dataSource'. if the name changed in spring-dao-mysql-context.xml, change name in "+this.getClass().getSimpleName());
				return false;
			} catch(Exception e) {
				// ignore : reflection으로 구하지 못하는 경우는 어쩔 수 없다.
				return false;
			}

			return true;
		}

		private Object getLoadedBean(String beanName) throws NoSuchBeanDefinitionException, SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
			Field testContextField = TestContextManager.class.getDeclaredField("testContext");
			testContextField.setAccessible(true);
			TestContext testContext = (TestContext)testContextField.get(testContextManager);
			AutowireCapableBeanFactory autowireCapableBeanFactory = testContext.getApplicationContext().getAutowireCapableBeanFactory();
			return autowireCapableBeanFactory.getBean(beanName);
		}
		
		@SuppressWarnings("unchecked")
		private Object getLoadedBean(@SuppressWarnings("rawtypes") Class dataSourceClass) throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
			Field testContextField = TestContextManager.class.getDeclaredField("testContext");
			testContextField.setAccessible(true);
			TestContext testContext = (TestContext)testContextField.get(testContextManager);
			AutowireCapableBeanFactory autowireCapableBeanFactory = testContext.getApplicationContext().getAutowireCapableBeanFactory();
			return autowireCapableBeanFactory.getBean(dataSourceClass);
		}

		
	}
	



}
