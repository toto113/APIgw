package com.kthcorp.radix.dao.hazelcast;

import org.slf4j.Logger;
import com.kthcorp.radix.util.UuidViewableLoggerFactory;
import org.springframework.beans.factory.InitializingBean;

import com.hazelcast.client.ClusterClientException;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.kthcorp.radix.api.dao.TransactionLogDao;
import com.kthcorp.radix.domain.exception.RadixException;
import com.kthcorp.radix.domain.stat.RadixTransactionLog;
import com.kthcorp.radix.util.HazelcastClusterInstanceCreator;

public class HazelcastTransactionLogDao implements TransactionLogDao, InitializingBean {
	
	private static final Logger LOG = UuidViewableLoggerFactory.getLogger(HazelcastTransactionLogDao.class);
	
	private transient String mapName;
	
	private transient IMap<String, RadixTransactionLog> map;
	
	private HazelcastInstance instance = null;
	
	public void setHazelcastInstanceCreator(HazelcastClusterInstanceCreator instanceCreator) {
		this.instance = instanceCreator.getHazelcast();
	}
	
	public void setMapName(final String mapName) {
		this.mapName = mapName;
	}
	
	@Override
	public void afterPropertiesSet() {
		
		// Create iMap if this instance run as server
		map = this.getMap();
		if(map!=null) {
			if(map.size()<1) {
				map.addIndex("header.messageId", true);
			}
			LOG.info("hazelcast MAP({}), initializing success", this.mapName);
		} else {
			LOG.error("cannot open the MAP({}), initializing failed", this.mapName);
			throw new ClusterClientException();
		}
		
	}
	
	private IMap<String, RadixTransactionLog> getMap() {

		IMap<String, RadixTransactionLog> map = null;
		if(instance != null) {
			map = this.instance.getMap(mapName);
			if(map != null) {
				if(map.size() < 1) {
					map.addIndex("serviceID", false); // addIndex(
					// propertyName,
					// ordering )
					map.addIndex("routingMethodType", false);
				}
			}
		}
		if(map == null) {
			LOG.error("cannot create/get Map("+mapName+") Object from hazelcast");
			throw new ClusterClientException(this.mapName);
		}
		return map;
	}
	
	@Override
	public void insertTransactionLog(final String messageId, final RadixTransactionLog transactionLog) throws RadixException {
		
		if(map == null) {
			throw new RadixException("cannot open the MAP("+this.mapName+")");
		}
		
		if(isExists(messageId)) {
			throw new RadixException("already exists");
		}
		
		map.put(messageId, transactionLog);
	}
	
	private boolean isExists(final String messageId) throws RadixException {
		
		if(map == null) {
			throw new RadixException("cannot open the MAP("+this.mapName+")");
		}
		
		return map.containsKey(messageId);
	}
	
	@Override
	public void updateTransactionLog(final String messageId, final RadixTransactionLog transactionLog) throws RadixException {
		
		if(map == null) {
			throw new RadixException("cannot open the MAP("+this.mapName+")");
		}
		
		map.replace(messageId, transactionLog);
		
	}
	
	@Override
	public void deleteTransactionLog(final String messageId) throws RadixException {
		
		if(map == null) {
			throw new RadixException("cannot open the MAP("+this.mapName+")");
		}
		
		if(!isExists(messageId)) {
			throw new RadixException("not exists");
		}
		
		map.remove(messageId);
		
	}
	
	@Override
	public RadixTransactionLog selectTransactionLog(final String messageId) throws RadixException {
		
		if(map == null) {
			throw new RadixException("cannot open the MAP("+this.mapName+")");
		}
		
		if(!isExists(messageId)) {
			throw new RadixException("not exists");
		}
		
		RadixTransactionLog transactionLog = map.get(messageId);
		return transactionLog;
	}
}
