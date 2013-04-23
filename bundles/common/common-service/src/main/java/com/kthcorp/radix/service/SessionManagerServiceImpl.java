package com.kthcorp.radix.service;

import com.kthcorp.radix.api.dao.SessionManagerDao;
import com.kthcorp.radix.api.service.SessionManagerService;
import com.kthcorp.radix.domain.canonical.CanonicalMessage;

public class SessionManagerServiceImpl implements SessionManagerService {
	
	private SessionManagerDao sessionManagerDao;
	
	public void setSessionManagerDao(SessionManagerDao sessionManagerDao) {
		this.sessionManagerDao = sessionManagerDao;
	}
	
	@Override
	public void createSession(CanonicalMessage canonicalMessage) {
		this.sessionManagerDao.insertSession(canonicalMessage);
	}
	
	@Override
	public void modifySession(String correlationId, CanonicalMessage canonicalMessage) {
		this.sessionManagerDao.updateSession(correlationId, canonicalMessage);
		
	}
	
	@Override
	public void removeSession(String correlationId) {
		this.sessionManagerDao.deleteSession(correlationId);
	}
	
	@Override
	public CanonicalMessage getSession(String correlationId) {
		return this.sessionManagerDao.selectSession(correlationId);
	}
	
}
