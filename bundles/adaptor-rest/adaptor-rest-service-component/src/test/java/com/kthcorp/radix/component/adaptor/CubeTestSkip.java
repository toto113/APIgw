package com.kthcorp.radix.component.adaptor;

import java.nio.charset.Charset;
import java.util.concurrent.Future;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import com.kthcorp.radix.util.test.junit.MysqlEmbeddableSpringJUnit4ClassRunner;

import com.kthcorp.radix.domain.canonical.CanonicalMessage;
import com.kthcorp.radix.domain.canonical.reply.AdaptorRestReply;
import com.kthcorp.radix.domain.service.api.protocol.http.HttpMethod;
import com.kthcorp.radix.domain.service.api.protocol.http.ParameterMap;

@RunWith(MysqlEmbeddableSpringJUnit4ClassRunner.class)
@ContextConfiguration(
		locations = { "classpath:/META-INF/spring/spring-application-context.xml", 
				"classpath:/META-INF/spring/spring-camel-context-test.xml",
				"classpath:/META-INF/spring/spring-service-component-context.xml"})
public class CubeTestSkip extends AbstractProvider {
	
	private final static Charset DEFAULT_CHARSET = Charset.forName("euc-kr");
	
	@Test
	public void testNearPlain() {
		
		String uri = "http://near-api.bizmeka.com/http/getMLocStr.php";
		ParameterMap parameters = new ParameterMap();
		parameters.put("key", "11111174");
		parameters.put("ip", "203.245.50.115");
		CanonicalMessage canonicalMessage = createCanonicalMessage(HttpMethod.GET, uri, parameters);
		
		Future<Object> future = producer.asyncRequestBody("direct:testComponent", canonicalMessage);
		CanonicalMessage ret = producer.extractFutureBody(future, CanonicalMessage.class);

		LOG.debug("ret={}", ret);
		Assert.assertNotNull(ret);		
		
		AdaptorRestReply reply = (AdaptorRestReply) ret.getPayload().getAdaptorReply();
		Assert.assertNotNull(reply);
		if(reply.getBody() != null) {
			LOG.info("reply.body={}", reply.getBody());
			Charset charSet = reply.getMediaType().getCharSet();
			if(charSet == null) {
				charSet = DEFAULT_CHARSET;
			}
			String bodyString = new String(reply.getBody(), charSet);
			LOG.info("reply.bodyAsString={}", bodyString);
			Assert.assertNotNull(bodyString);
		}
	}
	
	@Test
	public void testNearXml() {
		
		String uri = "http://125.140.114.21/nearapi_useraccept.php";
		ParameterMap parameters = new ParameterMap();
		parameters.put("api_apkey", "kth1333689405979");
		parameters.put("user_ip", "203.245.50.115");
//		parameters.put("serverip", "203.245.50.115");
		CanonicalMessage canonicalMessage = createCanonicalMessage(HttpMethod.GET, uri, parameters);
		
		Future<Object> future = producer.asyncRequestBody("direct:testComponent", canonicalMessage);
		CanonicalMessage ret = producer.extractFutureBody(future, CanonicalMessage.class);

		LOG.debug("ret={}", ret);
		Assert.assertNotNull(ret);		
		
		AdaptorRestReply reply = (AdaptorRestReply) ret.getPayload().getAdaptorReply();
		Assert.assertNotNull(reply);
		if(reply.getBody() != null) {
			LOG.info("reply.body={}", reply.getBody());
			Charset charSet = reply.getMediaType().getCharSet();
			if(charSet == null) {
				charSet = DEFAULT_CHARSET;
			}
			String bodyString = new String(reply.getBody(), charSet);
			LOG.info("reply.bodyAsString={}", bodyString);
			Assert.assertNotNull(bodyString);
		}
	}
}
