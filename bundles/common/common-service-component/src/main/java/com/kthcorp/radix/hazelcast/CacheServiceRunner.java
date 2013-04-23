package com.kthcorp.radix.hazelcast;

import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;


public class CacheServiceRunner {

	public static void main(String[] args) {
		AbstractApplicationContext applicationContext = new ClassPathXmlApplicationContext(new String[] { "META-INF/spring/spring-application-context.xml", "META-INF/spring/spring-dao-hazelcast-server-context.xml" });
		applicationContext.registerShutdownHook();
	}
	
}
