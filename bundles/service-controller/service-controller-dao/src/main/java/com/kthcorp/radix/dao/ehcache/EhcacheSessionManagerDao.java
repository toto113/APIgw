package com.kthcorp.radix.dao.ehcache;

import java.util.List;

import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;

import org.slf4j.Logger;
import com.kthcorp.radix.util.UuidViewableLoggerFactory;

import com.kthcorp.radix.api.dao.SessionManagerDao;
import com.kthcorp.radix.domain.canonical.CanonicalMessage;

public class EhcacheSessionManagerDao implements SessionManagerDao {
	
	/** LOG4j to LOG. */
	private static final Logger LOG = UuidViewableLoggerFactory.getLogger(EhcacheSessionManagerDao.class);
	
	private Ehcache cache;
	
	public void setCache(Ehcache cache) {
		this.cache = cache;
	}
	
	private void printKey() {
		LOG.debug("************ EHCache Data ************");
		@SuppressWarnings("unchecked")
		List<String> keys = cache.getKeys();
		for(String key : keys) {
			LOG.debug("** include key : {} **", key);
		}
		LOG.debug("*************************************");
		
	}
	
	@Override
	public void insertSession(CanonicalMessage canonicalMessage) {
		this.cache.put(new Element(canonicalMessage.getHeader().getMessageId(), canonicalMessage));
		
		if(LOG.isDebugEnabled()) {
			printKey();
		}
	}
	
	@Override
	public CanonicalMessage selectSession(String correlationId) {
		
		if(LOG.isDebugEnabled()) {
			printKey();
		}
		
		Element e = this.cache.get(correlationId);
		return (e != null) ? (CanonicalMessage) e.getValue() : null;
	}
	
	@Override
	public void updateSession(String correlationId, CanonicalMessage canonicalMessage) {
		
		this.cache.put(new Element(correlationId, canonicalMessage));
		
		if(LOG.isDebugEnabled()) {
			printKey();
		}
	}
	
	@Override
	public void deleteSession(String correlationId) {
		
		this.cache.remove(correlationId);
		
		if(LOG.isDebugEnabled()) {
			printKey();
		}
	}
	
}
