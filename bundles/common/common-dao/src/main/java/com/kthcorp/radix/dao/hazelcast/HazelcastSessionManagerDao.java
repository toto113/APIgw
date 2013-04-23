package com.kthcorp.radix.dao.hazelcast;

import org.slf4j.Logger;
import com.kthcorp.radix.util.UuidViewableLoggerFactory;
import org.springframework.beans.factory.InitializingBean;

import com.hazelcast.client.ClusterClientException;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.kthcorp.radix.api.dao.SessionManagerDao;
import com.kthcorp.radix.domain.canonical.CanonicalMessage;
import com.kthcorp.radix.util.HazelcastClusterInstanceCreator;

public class HazelcastSessionManagerDao implements SessionManagerDao, InitializingBean {
	
	private static final Logger LOG = UuidViewableLoggerFactory.getLogger(HazelcastSessionManagerDao.class);
	
	private transient String mapName;
	
	private transient IMap<String, CanonicalMessage> map;
	
	private transient boolean nativeClient = false;
	
	private HazelcastInstance instance = null;
	
	public void setMapName(final String mapName) {
		this.mapName = mapName;
	}
	
	public void setHazelcastInstanceCreator(HazelcastClusterInstanceCreator instanceCreator) {
		nativeClient = true;
		this.instance = instanceCreator.getHazelcast();
	}
	
	@Override
	public void afterPropertiesSet() {
		
		// Create iMap if this instance run as server
		map = this.getMap();
		if(map!=null) {
			if(map.size()<1) {
				map.addIndex("header.messageId", true);
			}
			LOG.info("hazelcast MAP("+this.mapName+"), initializing successful (nativeClient->"+nativeClient+")");
		} else {
			LOG.error("cannot open the MAP("+this.mapName+"), initializing failed");
			throw new ClusterClientException(this.mapName);
		}
		
	}
	
	private IMap<String, CanonicalMessage> getMap() {
		IMap<String, CanonicalMessage> map = null;
		if(instance != null) {
			map = this.instance.getMap(mapName);
		}
		return map;
	}
	
	@Override
	public void insertSession(final CanonicalMessage canonicalMessage) {
		
		if(map!=null) {
			map.put(canonicalMessage.getHeader().getMessageId(), canonicalMessage);
		} else {
			LOG.error("cannot open the MAP("+this.mapName+")");
		}
		
	}
	
	@Override
	public void updateSession(final String messageId, final CanonicalMessage canonicalMessage) {
		
		if(map!=null) {
			map.replace(messageId, canonicalMessage);
		} else {
			LOG.error("cannot open the MAP("+this.mapName+")");
		}
		
	}
	
	@Override
	public void deleteSession(final String messageId) {
		
		if(map!=null) {
			map.remove(messageId);
		} else {
			LOG.error("cannot open the MAP("+this.mapName+")");
		}
		
	}
	
	@Override
	public CanonicalMessage selectSession(final String messageId) {
		
		if(map!=null) {
			return map.get(messageId);
		} else {
			LOG.error("cannot open the MAP("+this.mapName+")");
		}
		
		return null;
	}
}
