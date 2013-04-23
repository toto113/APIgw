package com.kthcorp.radix.api.service;

import com.kthcorp.radix.domain.canonical.CanonicalMessage;

public interface SessionManagerService {
	
	public void createSession(CanonicalMessage canonicalMessage);
	
	public CanonicalMessage getSession(String messageId);
	
	public void modifySession(String messageId, CanonicalMessage canonicalMessage);
	
	public void removeSession(String messageId);
	
}
