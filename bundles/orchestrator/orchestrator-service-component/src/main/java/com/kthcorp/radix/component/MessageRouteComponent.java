package com.kthcorp.radix.component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.ProducerTemplate;
import org.slf4j.Logger;
import com.kthcorp.radix.util.UuidViewableLoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;

import com.kthcorp.radix.api.service.ServiceManagerService;
import com.kthcorp.radix.domain.canonical.CanonicalMessage;
import com.kthcorp.radix.domain.canonical.reply.AdaptorRestReply;
import com.kthcorp.radix.domain.canonical.reply.OrchestratorRestReply;
import com.kthcorp.radix.domain.canonical.request.ServiceControllerRestRequest;
import com.kthcorp.radix.domain.exception.RadixException;
import com.kthcorp.radix.domain.service.api.protocol.http.ParameterMap;
import com.kthcorp.radix.domain.service.api.protocol.http.ServerProtocol;
import com.kthcorp.radix.domain.service.routing.RoutingMethod;
import com.kthcorp.radix.domain.service.routing.RoutingMethodType;
import com.kthcorp.radix.domain.service.routing.direct.DirectMethod;
import com.kthcorp.radix.domain.service.routing.RoutingMethodSelector;

public class MessageRouteComponent implements InitializingBean, Processor {
	
	/**
	 * LOG4j to LOG.
	 */
	private static final Logger LOG = UuidViewableLoggerFactory.getLogger(MessageRouteComponent.class);
	
	@Autowired
	@Qualifier("producerTemplate")
	private ProducerTemplate producer;
	
	private ServiceManagerService serviceManagerService;
	
	public void setServiceManagerService(ServiceManagerService serviceManagerService) {
		this.serviceManagerService = serviceManagerService;
	}

	private Map<String, String> routingMethodMap;
	
	public void setRoutingMethodMap(Map<String, String> routingMethodMap) {
		this.routingMethodMap = routingMethodMap;
	}
	
	@Override
	public void afterPropertiesSet() throws Exception {
		
	}
	
	public MessageRouteComponent() {
		
	}
	
	@Override
	public void process(Exchange exchange) throws RadixException {
		
		LOG.trace(">>> MessageRouteComponent.process({})", exchange);
		doRoute(exchange);
	}
	
	public void doRoute(Exchange exchange) throws RadixException {
		
		LOG.trace(">>> MessageRouteComponent.doRoute({})", exchange);
		
		CanonicalMessage canonicalMessage = exchange.getIn().getBody(CanonicalMessage.class);
		
		ServiceControllerRestRequest request = (ServiceControllerRestRequest) canonicalMessage.getPayload().getRequest();
		LOG.trace(">>> ServiceControllerRestRequest={}", request);
		LOG.trace("key={}", request.getKey());
		ParameterMap parameters = request.getParameters();
		if(parameters != null) {
		for(String key : parameters.keys())
			LOG.trace("{}={}", key, parameters.get(key));
		}
		
		String key = request.getKey();
		List<RoutingMethod> routingMethods = serviceManagerService.getRoutingMethods(key);
		
		RoutingMethod routingMethod = RoutingMethodSelector.selectRoutingMethod(routingMethods, request.getResroucePath());
		LOG.debug("key={}", key);
		LOG.debug("routingMethod={}", routingMethod);
		
		if(routingMethod == null) {
			
			if(routingMethods==null||routingMethods.size()==0) {
				throw new RadixException("no routingMethod defined for key "+key);
			}

			
			boolean isCommaRequired = false;
			StringBuilder sb = new StringBuilder();
			sb.append("[");
			for(RoutingMethod aRoutingMethod : routingMethods) {
				if(isCommaRequired) { sb.append(", "); }
				ServerProtocol serverProtocol = (ServerProtocol)((DirectMethod)aRoutingMethod).getServiceAPI().getProtocolObj();
				String pathTemplate = serverProtocol.getPathTemplate();
				sb.append("'").append(pathTemplate).append("'");
				isCommaRequired = true;
			}
			sb.append("]");
			String requestedPath = request.getResroucePath();
			throw new RadixException("there are routingMethods for key but not matched with requetedPath. key="+key+", candidated routingMethods="+sb.toString()+", requestedPath="+requestedPath);
		} 
		
		if(routingMethod.getRoutingMethodType() == RoutingMethodType.DIRECT) {
			doDirect(exchange, canonicalMessage);
		} else {
			throw new RadixException(String.format("ReoutingMethodType %s is not supported", routingMethod.getRoutingMethodType()), HttpStatus.BAD_REQUEST);
		}
	}
	
	private void doDirect(Exchange exchange, CanonicalMessage canonicalMessage) throws RadixException {
		
		LOG.trace(">>> MessageRouteComponent.doDirect({}, {})", exchange, canonicalMessage);
		
		String camelUrl = routingMethodMap.get("DIRECT");
		LOG.debug("camelUrl={}", camelUrl);
		if(camelUrl == null) {
			throw new RadixException("Camel url is not defined", HttpStatus.BAD_REQUEST);
		}
		
		Future<Object> future = producer.asyncRequestBody(camelUrl, canonicalMessage);
		CanonicalMessage resultCanonicalMessage = producer.extractFutureBody(future, CanonicalMessage.class);
		
		OrchestratorRestReply reply = new OrchestratorRestReply();
		AdaptorRestReply adaptorReply = (AdaptorRestReply) resultCanonicalMessage.getPayload().getAdaptorReply();
//		reply.setJsonText(adaptorReply.getJsonText());
		reply.setHttpStatus(adaptorReply.getHttpStatus());
		reply.setBody(adaptorReply.getBody());
		reply.setContentLength(adaptorReply.getContentLength());
		reply.setHttpHeaders(adaptorReply.getHttpHeaders());
		reply.setMediaType(adaptorReply.getMediaType());
		canonicalMessage.getPayload().setReply(reply);
		
		exchange.getOut().setBody(canonicalMessage);
	}
}
