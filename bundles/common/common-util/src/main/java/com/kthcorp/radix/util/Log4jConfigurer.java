package com.kthcorp.radix.util;

import org.apache.log4j.xml.DOMConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Log4jConfigurer {
	
	public Log4jConfigurer(final String confPath) {
		this.init(confPath);
	}
	
	private void init(final String confPath) {
		final java.net.URL url = this.getClass().getResource(confPath);
		
		DOMConfigurator.configure(url);
		
		final Logger log = LoggerFactory.getLogger(Log4jConfigurer.class);
		log.debug("log4j loaded...");
	}
}
