package com.kthcorp.radix.util;

import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class StartStopMain {
	
	private static AbstractApplicationContext applicationContext;
	
	public static void main(String[] args) throws Exception {
		
		String methodName = args[0];
		
		if(methodName.equals("start")) {
			start();
		} else if(methodName.equals("stop")) {
			stop();
		} else {
			System.exit(1);
		}
		
	}
	
	public static void start() throws Exception {
		applicationContext = new ClassPathXmlApplicationContext(new String[] { "classpath:META-INF/spring/spring-application-context.xml" });
		applicationContext.registerShutdownHook();
	}
	
	public static void stop() {
		applicationContext.close();
	}
}
