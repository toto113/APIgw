package com.kthcorp.radix.util;

import org.slf4j.Logger;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

import com.hazelcast.client.ClientConfig;
import com.hazelcast.client.HazelcastClient;
import com.hazelcast.core.HazelcastInstance;

public class HazelcastNativeClientCreator implements InitializingBean, DisposableBean {
	
	/**
	 * log4j to log.
	 */
	private static final Logger LOG = UuidViewableLoggerFactory.getLogger(HazelcastNativeClientCreator.class);
	
	private transient String clusterName;
	private transient String password;
	private transient String serverHosts;
	
	private transient HazelcastInstance client;
	
	public void setClusterName(final String clusterName) {
		this.clusterName = clusterName;
	}
	
	public void setPassword(final String password) {
		this.password = password;
	}
	
	public void setServerHosts(final String serverHosts) {
		this.serverHosts = serverHosts;
	}
	
	@Override
	public void afterPropertiesSet() {
		
		final ClientConfig config = new ClientConfig();
		final String[] serverHost = this.serverHosts.replace(" ","").split(",");
		for(String server: serverHost) {
			config.addAddress(server);
		}
		
		config.getGroupConfig().setName(clusterName).setPassword(password);
		
		LOG.info("[Hazelcast-Client] Trying to connect cluster: "+serverHosts+" [" + clusterName + "]");
		client = HazelcastClient.newHazelcastClient(config);
		
		LOG.info("[Hazelcast-Client] Connected to cluster group: [" + clusterName + "]");
	}
	
	public HazelcastInstance getClientInstance() {
		return client;
	}

	@Override
	public void destroy() {
		if(client!=null) {
			client.getLifecycleService().shutdown();
			LOG.info("[Hazelcast-Client] Disconnect from cluster group: [" + clusterName + "]");
		}
	}
}
