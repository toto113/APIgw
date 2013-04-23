package com.kthcorp.radix.component;

import org.slf4j.Logger;
import com.kthcorp.radix.util.UuidViewableLoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import com.kthcorp.radix.domain.canonical.CanonicalMessage;
import com.kthcorp.radix.domain.canonical.reply.AdaptorRestReply;

public class ReplyAdaptorTestComponent {
	
	private final Logger LOG = UuidViewableLoggerFactory.getLogger(this.getClass());
	
	public CanonicalMessage doCall(CanonicalMessage canonicalMessage) {
		
		LOG.trace(">>> ReplyAdaptorTestComponent.doCall({})", canonicalMessage);
		
		String result = String.format("{result:test}");		
		AdaptorRestReply reply = new AdaptorRestReply();
		reply.setHttpStatus(HttpStatus.OK);
		reply.setBody(result.getBytes());
		reply.setContentLength(result.getBytes().length);
		reply.setHttpHeaders(new HttpHeaders());
		reply.setMediaType(MediaType.ALL);
		canonicalMessage.getPayload().setAdaptorReply(reply);
		return canonicalMessage;
	}
}
