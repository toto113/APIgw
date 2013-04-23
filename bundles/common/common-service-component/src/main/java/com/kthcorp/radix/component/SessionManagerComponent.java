package com.kthcorp.radix.component;

import com.kthcorp.radix.api.service.SessionManagerService;
import com.kthcorp.radix.domain.canonical.CanonicalMessage;
import com.kthcorp.radix.domain.canonical.reply.AdaptorRestReply;
import com.kthcorp.radix.domain.canonical.reply.OrchestratorRestReply;

public class SessionManagerComponent {
	
	private SessionManagerService sessionManagerService;
	
	public void setSessionManagerService(SessionManagerService sessionManagerService) {
		this.sessionManagerService = sessionManagerService;
	}
	
	public CanonicalMessage saveSession(CanonicalMessage canonicalMessage) {
		this.sessionManagerService.createSession(canonicalMessage);
		
		return canonicalMessage;
	}
	
	@SuppressWarnings("deprecation")
	public CanonicalMessage updateOrchestratorSession(CanonicalMessage canonicalMessage) {
		String messageId = canonicalMessage.getHeader().getCorrelationId();
		
		OrchestratorRestReply reply = new OrchestratorRestReply();
		// TODO : don't use deprecated method
		reply.setJsonText(((AdaptorRestReply) canonicalMessage.getPayload().getReply()).getJsonText());
		
		CanonicalMessage canonicalMessageLoaded = this.sessionManagerService.getSession(messageId);
		canonicalMessageLoaded.getPayload().setReply(reply);
		
		this.sessionManagerService.modifySession(messageId, canonicalMessageLoaded);
		
		return canonicalMessage;
	}
}
