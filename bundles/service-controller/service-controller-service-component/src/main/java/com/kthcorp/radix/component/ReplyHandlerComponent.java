package com.kthcorp.radix.component;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import com.kthcorp.radix.util.UuidViewableLoggerFactory;

import com.kthcorp.radix.api.service.SessionManagerService;
import com.kthcorp.radix.domain.canonical.CanonicalMessage;
import com.kthcorp.radix.domain.canonical.CanonicalMessageWithThread;

public class ReplyHandlerComponent implements Processor {
	
	/**
	 * log4j to log.
	 */
	private static final Logger LOG = UuidViewableLoggerFactory.getLogger(ReplyHandlerComponent.class);
	
	private SessionManagerService sessionManagerService;
	
	public void setSessionManagerService(SessionManagerService sessionManagerService) {
		
		this.sessionManagerService = sessionManagerService;
	}
	
	@Override
	public void process(Exchange exchange) throws Exception {
//		DirectEndpoint rootEndpoint = (DirectEndpoint) (exchange.getContext().getEndpoint("direct:request"));
		
	}
	
	public void handleResponse(CanonicalMessage canonicalMessage) {
		
		LOG.debug(">>> canonicalMessage={}", canonicalMessage);
		LOG.debug(">>> payLoad={}", canonicalMessage.getPayload());
		
		// get correlation id.
		String correlationId = canonicalMessage.getHeader().getCorrelationId();
		LOG.info("get Cannonical Message correlationId: [" + correlationId + "]");
		
		// get the session canonical message from cache.
		CanonicalMessageWithThread canonicalMessageWithThreadLoaded = (CanonicalMessageWithThread) this.sessionManagerService.getSession(correlationId);
		
		LOG.debug("loadedPayload={}", canonicalMessageWithThreadLoaded.getPayload());
		canonicalMessageWithThreadLoaded.getPayload().setReply(canonicalMessage.getPayload().getReply());
		
		// update session message into cache.
		LOG.info("Update Cannonical Message correlationId: [" + correlationId + "]");
		this.sessionManagerService.modifySession(correlationId, canonicalMessageWithThreadLoaded);
		
		// interrupt the thread.
		canonicalMessageWithThreadLoaded.getT().interrupt();
	}
	
}