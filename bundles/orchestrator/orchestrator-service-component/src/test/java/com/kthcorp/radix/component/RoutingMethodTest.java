package com.kthcorp.radix.component;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import com.kthcorp.radix.util.UuidViewableLoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import com.kthcorp.radix.util.test.junit.MysqlEmbeddableSpringJUnit4ClassRunner;

import com.kthcorp.radix.api.service.ServiceManagerService;
import com.kthcorp.radix.domain.service.ServiceVersion;
import com.kthcorp.radix.domain.service.api.protocol.Protocol;
import com.kthcorp.radix.domain.service.api.protocol.ProtocolMode;
import com.kthcorp.radix.domain.service.api.protocol.http.ClientProtocol;
import com.kthcorp.radix.domain.service.api.protocol.http.HttpMethod;
import com.kthcorp.radix.domain.service.routing.RoutingMethod;
import com.kthcorp.radix.domain.service.routing.RoutingMethodType;
import com.kthcorp.radix.domain.service.routing.direct.DirectMethod;
import com.kthcorp.radix.util.UUIDUtils;

@RunWith(MysqlEmbeddableSpringJUnit4ClassRunner.class)
@ContextConfiguration(
		locations = { "classpath:/META-INF/spring/spring-all-context-test.xml", "classpath:/META-INF/spring/reply-adaptor-context.xml" }
)
public class RoutingMethodTest extends AbstractJUnit4SpringContextTests {
	
	private final Logger LOG = UuidViewableLoggerFactory.getLogger(this.getClass());
	private final byte[] serviceID = UUIDUtils.getBytes("2341ed57-8772-11e1-a8c6-f0def154de37");
	
	@Autowired
	private ServiceManagerService serviceManagerService;
	
	@Before
	public void before() {
		
		serviceManagerService.loadService(serviceID);
	}
	
	@Test
	public void getUri() {
		
//		api.withapi.com::GET:/Facebook/0/me
		String key = serviceManagerService.getURIString("api.withapi.com", HttpMethod.GET, "Facebook", new ServiceVersion("0"), "me");
		
		LOG.info("key={}", key);
		@SuppressWarnings("deprecation")
		RoutingMethod routingMethod = serviceManagerService.getRoutingMethod(key);
		LOG.info("routingMethod={}", routingMethod);
		
		if(routingMethod != null) {
			LOG.info("routingMethodType={}", routingMethod.getRoutingMethodType());
			if(routingMethod.getRoutingMethodType() == RoutingMethodType.DIRECT) {
				DirectMethod directMethod = (DirectMethod) routingMethod;
				LOG.info("directMethod={}", directMethod);
				Protocol protocol = directMethod.getPartnerAPI().getProtocolObj();
				LOG.info("protocol={}", protocol);
				if(protocol.getProtocolMode() == ProtocolMode.CLIENT) {
					ClientProtocol clientProtocol = (ClientProtocol) protocol;
					LOG.info("clientProtocol={}", clientProtocol);
					LOG.info("uri={}", clientProtocol.getUri());
					LOG.info("method={}", clientProtocol.getMethod());
					LOG.info("contentType={}", clientProtocol.getContentType());
				}
			}
		}
	}
}
