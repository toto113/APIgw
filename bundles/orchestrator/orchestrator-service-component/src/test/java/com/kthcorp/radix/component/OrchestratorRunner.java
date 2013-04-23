package com.kthcorp.radix.component;

import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class OrchestratorRunner {
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		AbstractApplicationContext applicationContext = new ClassPathXmlApplicationContext(new String[] { "META-INF/spring/spring-all-context-test.xml" });
		
		applicationContext.registerShutdownHook();
		
	}
	
}
