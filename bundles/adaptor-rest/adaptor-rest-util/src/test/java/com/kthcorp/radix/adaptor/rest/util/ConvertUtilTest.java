package com.kthcorp.radix.adaptor.rest.util;

import org.json.JSONException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import com.kthcorp.radix.util.UuidViewableLoggerFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import com.kthcorp.radix.util.test.junit.MysqlEmbeddableSpringJUnit4ClassRunner;

import com.kthcorp.radix.adaptor.rest.domain.RestResponse;

@RunWith(MysqlEmbeddableSpringJUnit4ClassRunner.class)
@ContextConfiguration(
		locations = { "classpath:/META-INF/spring/spring-application-context.xml"})
public class ConvertUtilTest extends AbstractJUnit4SpringContextTests {
	
	private final Logger LOG = UuidViewableLoggerFactory.getLogger(this.getClass());
	
	String[] arr = { "{message:success}", "<message>success</message>", "success" };
	
	@Test
	public void testJson() throws JSONException {
		RestResponse restResponse = new RestResponse();
		restResponse.setBody(arr[0]);
		LOG.debug("source={}", arr[0]);
		restResponse = ConvertUtil.convert(restResponse);
		Assert.assertNotNull(restResponse.getJsonObject());
		LOG.debug("jsonObj={}", restResponse.getJsonObject());
		LOG.debug("contentType={}", restResponse.getContentType());
	}
	
	@Test
	public void testXml() throws JSONException {
		RestResponse restResponse = new RestResponse();
		restResponse.setBody(arr[1]);
		LOG.debug("source={}", arr[1]);
		restResponse = ConvertUtil.convert(restResponse);
		Assert.assertNotNull(restResponse.getJsonObject());
		LOG.debug("jsonObj={}", restResponse.getJsonObject());
		LOG.debug("contentType={}", restResponse.getContentType());
	}
	
	@Test
	public void testPlain() throws JSONException {
		RestResponse restResponse = new RestResponse();
		restResponse.setBody(arr[2]);
		LOG.debug("source={}", arr[2]);
		restResponse = ConvertUtil.convert(restResponse);
		Assert.assertNotNull(restResponse.getJsonObject());
		LOG.debug("jsonObj={}", restResponse.getJsonObject());
		LOG.debug("contentType={}", restResponse.getContentType());
	}
}
