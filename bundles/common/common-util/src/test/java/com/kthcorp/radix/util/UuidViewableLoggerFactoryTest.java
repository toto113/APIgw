package com.kthcorp.radix.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

import org.apache.log4j.Appender;
import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Level;
import org.apache.log4j.spi.LocationInfo;
import org.apache.log4j.spi.LoggingEvent;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;

import com.fasterxml.uuid.EthernetAddress;
import com.fasterxml.uuid.Generators;
import com.fasterxml.uuid.impl.TimeBasedGenerator;

public class UuidViewableLoggerFactoryTest {

	private static Logger logger = null;
	private static byte[] idBytes = null;
	private static String stringizedId = null;
	
	@BeforeClass
	public static void configureLog4j() {
		
		org.apache.log4j.Logger log4jRootLogger = org.apache.log4j.Logger.getRootLogger();
		Appender appender = new MyAppender();
		log4jRootLogger.addAppender(appender);
		log4jRootLogger.setLevel(Level.DEBUG);
		
	}
	
	
	@Before
	public void clearOldLog() {
		
		logger = UuidViewableLoggerFactory.getLogger(MyAppender.NAME);
		
		MyAppender.logList.clear();
		
		// 다음 id 생성 코드는 generateID.generateID()에서 카피
		EthernetAddress ethernetAddress = EthernetAddress.fromInterface();
		TimeBasedGenerator generator = Generators.timeBasedGenerator(ethernetAddress);
		UUID newID = generator.generate();
		idBytes = UUIDUtils.getBytes(newID);
		
		stringizedId = UUIDUtils.getString(idBytes);
		
	}
	
	@Test
	public void uuid가_잘_String으로_찍히는지_확인() {

		// 직접 id를 넘기는 경우
		{
			MyAppender.logList.clear();
			logger.error("id={}", idBytes);
			assertTrue("not logged.", MyAppender.logList.size()==1);
			String actualLog = MyAppender.logList.get(0);
			assertTrue("not found uuid string. log="+actualLog, actualLog.contains(stringizedId));
		}
		
		// Map에 담아서
		{
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("id", idBytes);
			MyAppender.logList.clear();
			logger.error("map={}", map);
			assertTrue("not logged.", MyAppender.logList.size()==1);
			String actualLog = MyAppender.logList.get(0);
			assertTrue("not found uuid string. log="+actualLog, actualLog.contains(stringizedId));
		}
		
		// List에 담아서
		{
			List<Object> list = new ArrayList<Object>();
			list.add(idBytes);
			MyAppender.logList.clear();
			logger.error("list={}", list);
			assertTrue("not logged.", MyAppender.logList.size()==1);
			String actualLog = MyAppender.logList.get(0);
			assertTrue("not found uuid string. log="+actualLog, actualLog.contains(stringizedId));
		}	
		
		// array에 담아서
		{
			Object[] array = { idBytes };
			MyAppender.logList.clear();
			logger.error("array={}", array);
			assertTrue("not logged.", MyAppender.logList.size()==1);
			String actualLog = MyAppender.logList.get(0);
			assertTrue("not found uuid string. log="+actualLog, actualLog.contains(stringizedId));
		}	
		
		// Properties에 담아서
		{
			Properties properties = new Properties();
			properties.put("id", idBytes);
			MyAppender.logList.clear();
			logger.error("properties={}", properties);
			assertTrue("not logged.", MyAppender.logList.size()==1);
			String actualLog = MyAppender.logList.get(0);
			assertTrue("not found uuid string. log="+actualLog, actualLog.contains(stringizedId));
		}
		
	}
	
	
	@Test
	public void Map에_담긴_내용자체가_변경되지_않는지_확인() {
		
		// Map에 담아서
		{
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("id", idBytes);
			
			// map에 넣은 실제 객체의 hash 값을 챙겨주자. 요것이 변경되면 map의 내용이 변질된 것이다. 그러면 안되지.
			int orgHashcodeOfIdBytes = idBytes.hashCode();
			MyAppender.logList.clear();
			logger.error("map={}", map);
			assertTrue("not logged.", MyAppender.logList.size()==1);
			String actualLog = MyAppender.logList.get(0);
			assertTrue("not found uuid string. log="+actualLog, actualLog.contains(stringizedId));
			
			// 이제 map에 담긴 것의 hash 값을 구하자. 그리고 이전 것과 비교하자.
			int hashcodeOfIdBytes = map.get("id").hashCode();
			assertEquals("instance in map changed.", orgHashcodeOfIdBytes, hashcodeOfIdBytes);
			
		}
		
		
	}
	
	
	// 로그가 요따구로 찍히네
	// 2012-08-22 09:36:07,998 ERROR [main] util.UuidViewableLog4jLoggerAdapter (UuidViewableLog4jLoggerAdapter.java:317) - id=5d21b08e-ebf1-11e1-9dbe-70f3954f8a40
	// 바랬던 것은 (UuidViewableLoggerFactoryTest.java:152) 요런 식으로 로거를 사용하느 곳인데.
	@Test
	public void 찍히는_로그의_로거이름과_찍는곳의_이름이_UuidViewableLog4jLoggerAdapter로_고정되는_버그_픽스() {
		// 직접 id를 넘기는 경우
		{
			MyAppender.logList.clear();
			logger.error("id={}", idBytes);
			assertTrue("not logged.", MyAppender.logList.size()==1);
			String actualLog = MyAppender.logList.get(0);
			assertTrue("not found uuid string. log="+actualLog, actualLog.contains(stringizedId));
			assertTrue("logger name incorrect.", !MyAppender.loggingOccuredFileName.contains(UuidViewableLog4jLoggerAdapter.class.getSimpleName()));
		}
	}
	
	
	
	private static class MyAppender extends AppenderSkeleton {

		private static List<String> logList = new ArrayList<String>();
		private static final String NAME = "MyAppender";
		private static String loggingOccuredFileName = null;
		
		public MyAppender() {
			super.setName(NAME);
		}
		
		@Override
		public void close() {
		}

		@Override
		public boolean requiresLayout() {
			return false;
		}

		
		@Override
		protected void append(LoggingEvent loggingEvent) {
			logList.add(loggingEvent.getMessage().toString());
			LocationInfo locationInfo = loggingEvent.getLocationInformation();
			loggingOccuredFileName = locationInfo.getFileName();
		}
		
	}
}
