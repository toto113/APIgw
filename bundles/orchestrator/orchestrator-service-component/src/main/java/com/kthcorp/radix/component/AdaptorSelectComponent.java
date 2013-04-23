package com.kthcorp.radix.component;

import java.util.Map;
import java.util.concurrent.Future;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.ProducerTemplate;
import org.slf4j.Logger;
import com.kthcorp.radix.util.UuidViewableLoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.kthcorp.radix.domain.canonical.CanonicalMessage;
import com.kthcorp.radix.domain.canonical.CanonicalMessagePropertyName;
import com.kthcorp.radix.domain.exception.RadixException;

public class AdaptorSelectComponent implements Processor {
	
	/**
	 * log4j to log.
	 */
	private final Logger LOG = UuidViewableLoggerFactory.getLogger(this.getClass());
	
	@Autowired
	@Qualifier("producerTemplate")
	private ProducerTemplate producer;
	
	private Map<String, String> protocolAdaptorMap;
	
	public void setProtocolAdaptorMap(Map<String, String> protocolAdaptorMap) {
		this.protocolAdaptorMap = protocolAdaptorMap;
	}
	
	@Override
	public void process(Exchange exchange) throws RadixException {
		
		LOG.trace(">>> AdaptorSelectComponent.process({})", exchange);
		
		CanonicalMessage canonicalMessage = (CanonicalMessage) exchange.getIn().getBody();
		String selectKey = canonicalMessage.getHeader().getHeaderProperties().get(CanonicalMessagePropertyName.PROTOCOL);
		LOG.debug("selectKey={}", selectKey);
		
		String camelUrl = protocolAdaptorMap.get(selectKey);
		LOG.debug("camelUrl={}", camelUrl);
		
		if(camelUrl == null) {
//			throw new RadixException("Adaptor is not defined for protocol " + selectKey, HttpStatus.NOT_IMPLEMENTED);
			throw new RadixException("Adaptor is not defined for protocol " + selectKey);
		}
		
		Future<Object> future = producer.asyncRequestBody(camelUrl, canonicalMessage);
		CanonicalMessage resultCanonicalMessage = producer.extractFutureBody(future, CanonicalMessage.class);
		exchange.getOut().setBody(resultCanonicalMessage);
	}
}
