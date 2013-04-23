package com.kthcorp.radix.util;

import java.lang.reflect.Field;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.impl.Log4jLoggerAdapter;

/**
 * slf4j 로거 대신 byte[] 타입의 uuid를 String으로 찍을 수 있는 UuidViewableLog4jLoggerAdapter를 반환한다.
 *
 */
public class UuidViewableLoggerFactory {

	public static Logger getLogger(@SuppressWarnings("rawtypes") Class clazz) {
		Logger slf4jLogger = LoggerFactory.getLogger(clazz.getName());
		// 구한 Log4jLoggerAdapter의 속성 log4jLogger를 그대로 꺼내서
		// Log4jloggerAdapter를 그대로 짝퉁한 UuidViewableLog4jLoggerAdapter 인스턴스를 생성해서 반환한다.
		try {
			return createUuidViewable(slf4jLogger);
		} catch (Exception e) {
			// ignore
		}
		return slf4jLogger;
		
	}


	public static Logger getLogger(String name) {
		Logger slf4jLogger = LoggerFactory.getLogger(name);
		// 구한 Log4jLoggerAdapter의 속성 log4jLogger를 그대로 꺼내서
		// Log4jloggerAdapter를 그대로 짝퉁한 UuidViewableLog4jLoggerAdapter 인스턴스를 생성해서 반환한다.
		try {
			return createUuidViewable(slf4jLogger);
		} catch (Exception e) {
			// ignore
		}
		return slf4jLogger;
	}

	private static Logger createUuidViewable(Logger slf4jLogger) throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
		Field loggerField = Log4jLoggerAdapter.class.getDeclaredField("logger");
		loggerField.setAccessible(true);
		org.apache.log4j.Logger log4jLogger = (org.apache.log4j.Logger) loggerField.get(slf4jLogger);
		UuidViewableLog4jLoggerAdapter uuidViewableLog4jLoggerAdapter = new UuidViewableLog4jLoggerAdapter(log4jLogger);
		return uuidViewableLog4jLoggerAdapter;
	}


}
