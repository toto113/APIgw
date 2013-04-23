package com.kthcorp.radix.api.dao;

import com.kthcorp.radix.domain.canonical.CanonicalMessage;

public interface SessionManagerDao {
	
	public void insertSession(CanonicalMessage canonicalMessage);
	
	public CanonicalMessage selectSession(String messageId);
	
	public void updateSession(String messageId, CanonicalMessage canonicalMessage);
	
	public void deleteSession(String messageId);
	
}
