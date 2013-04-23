package com.kthcorp.radix.util;

import java.io.InputStream;

import org.slf4j.Logger;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

import com.hazelcast.config.Config;
import com.hazelcast.config.XmlConfigBuilder;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;

public class HazelcastClusterInstanceCreator implements InitializingBean, DisposableBean {
	
	private static final Logger LOG = UuidViewableLoggerFactory.getLogger(HazelcastClusterInstanceCreator.class);
	
	private String configLocation;
	
	private String testConfigLocation;

	private boolean isProductionMode = false;

	private HazelcastInstance hazelcast;
	
	private boolean liteMember;
	
	@Override
	public void afterPropertiesSet() {

		InputStream is = null;
		if(isProductionMode) {
			is = getClass().getResourceAsStream("/" + configLocation);
			LOG.info("This is production machine, so we use configuration file->"+this.configLocation);
		} else {
			is = getClass().getResourceAsStream("/" + testConfigLocation);
			LOG.info("This is test machine, so we use configuration file->"+this.testConfigLocation);
		}
		final Config config = new XmlConfigBuilder(is).build();
		config.setLiteMember(liteMember);

		hazelcast = Hazelcast.newHazelcastInstance(config);			

		LOG.info("Created/Join to cluster group: [" + hazelcast.getConfig().getGroupConfig().getName() + "] init... (liteMember:"+this.liteMember+")");

		
	}
	
	public void destroy() {
		Hazelcast.shutdownAll();
	}
	
	public void setIsProductionMode(boolean isProductionMode) {
		this.isProductionMode = isProductionMode;
	}
	
	public void setTestConfigLocation(String testConfigLocation) {
		this.testConfigLocation = testConfigLocation;
	}

	public void setConfigLocation(final String configLocation) {
		this.configLocation = configLocation;
	}
	
	public HazelcastInstance getHazelcast() {
		return hazelcast;
	}
	
	public void setLiteMember(final boolean liteMember) {
		this.liteMember = liteMember;
	}

}
