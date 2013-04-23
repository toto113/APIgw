package com.kthcorp.radix.web.mock;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;

import com.kthcorp.radix.domain.canonical.CanonicalMessage;
import com.kthcorp.radix.domain.canonical.reply.OrchestratorRestReply;

public class MockReplyHandlerComponent implements Processor {
	
	/** log4j to log. */
//	private static final Logger LOG = LoggerFactoryForUuid.getLogger(MockReplyHandlerComponent.class);

	
//	public void setSessionManagerService(SessionManagerService sessionManagerService) {
//		
//		this.sessionManagerService = sessionManagerService;
//	}
	
	public void process(Exchange exchange) throws Exception {
//		DirectEndpoint rootEndpoint = (DirectEndpoint) (exchange.getContext().getEndpoint("direct:request"));
		CanonicalMessage canonicalMessage = (CanonicalMessage)exchange.getIn().getBody();
		
		OrchestratorRestReply reply = new OrchestratorRestReply();
		reply.setJsonText("it's test!");
		canonicalMessage.getPayload().setReply(reply);
		
		exchange.getOut().setBody(canonicalMessage);
	}
}
