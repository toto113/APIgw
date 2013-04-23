package com.kthcorp.radix.component.adaptor;

import java.nio.charset.Charset;
import java.util.concurrent.Future;

import junit.framework.Assert;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import com.kthcorp.radix.util.test.junit.MysqlEmbeddableSpringJUnit4ClassRunner;

import com.kthcorp.radix.domain.canonical.CanonicalMessage;
import com.kthcorp.radix.domain.canonical.reply.AdaptorRestReply;
import com.kthcorp.radix.domain.service.api.protocol.http.HttpMethod;
import com.kthcorp.radix.domain.service.api.protocol.http.ParameterMap;

@Ignore
@RunWith(MysqlEmbeddableSpringJUnit4ClassRunner.class)
@ContextConfiguration(
		locations = { "classpath:/META-INF/spring/spring-application-context.xml", 
				"classpath:/META-INF/spring/spring-camel-context-test.xml",
				"classpath:/META-INF/spring/spring-service-component-context.xml"})
public class AroTestSkip extends AbstractProvider {

	private static final String SvcID = "f78480bc1c06734607e4c7107d0642f3";
	private static final String output = "xml";
	
//	2.1 버스노선 조회
	@Test
	public void testBusSearch() {
		
//		String uri = "http://218.234.32.207/denny_test/appletree/v1/0/Bus/Lane/Search.asp?busNo={busNo}&CID={CID}&SvcID={SvcID}&output={output}";
		String uri = "http://218.234.32.207/denny_test/appletree/v1/0/Bus/Lane/Search.asp";
		ParameterMap parameters = new ParameterMap();
		parameters.put("busNo", "150");
		parameters.put("CID", "1");
		parameters.put("SvcID", SvcID);
		parameters.put("output", output);
		
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
			String bodyString = new String(reply.getBody(), charSet);
			LOG.info("reply.bodyAsString={}", bodyString);
			Assert.assertNotNull(bodyString);
		}
	}
	
//	2.2 버스노선 정보 조회
	@Test
	public void testBusLane() {
		
//		String uri = "http://218.234.32.207/denny_test/appletree/v1/0/Bus/Lane/result.asp?busID={busID}&SvcID={SvcID}&output={output}";
		String uri = "http://218.234.32.207/denny_test/appletree/v1/0/Bus/Lane/result.asp";
		ParameterMap parameters = new ParameterMap();
		parameters.put("busID", "12018");
		parameters.put("SvcID", SvcID);
		parameters.put("output", output);
		
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
			String bodyString = new String(reply.getBody(), charSet);
			LOG.info("reply.bodyAsString={}", bodyString);
			Assert.assertNotNull(bodyString);
		}
	}
	
//	2.3 대중교통 정류장 검색
	@Test
	public void testSearchStation() {
		
//		String uri = "http://218.234.32.207/denny_test/appletree/v1/0/Bus/station/Search.asp?stationName={stationName}&CID={CID}&stationClass={stationClass}&SvcID={SvcID}&output={output}";
		String uri = "http://218.234.32.207/denny_test/appletree/v1/0/Bus/station/Search.asp";
		ParameterMap parameters = new ParameterMap();
		parameters.put("stationName", "광화문");
		parameters.put("CID", "1000");
		parameters.put("stationClass", "1");
		parameters.put("SvcID", SvcID);
		parameters.put("output", output);
		
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
			String bodyString = new String(reply.getBody(), charSet);
			LOG.info("reply.bodyAsString={}", bodyString);
			Assert.assertNotNull(bodyString);
		}
	}
	
//	2.4 버스정류장 세부 정보 조회
	@Test
	public void testStationResult() {
		
//		String uri = "http://218.234.32.207/denny_test/appletree/v1/0/Bus/Station/Result.asp?StationID={stationID}&SvcID={SvcID}&output={output}";
		String uri = "http://218.234.32.207/denny_test/appletree/v1/0/Bus/Station/Result.asp";
		ParameterMap parameters = new ParameterMap();
		parameters.put("stationID", "107475");
		parameters.put("SvcID", SvcID);
		parameters.put("output", output);
		
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
			String bodyString = new String(reply.getBody(), charSet);
			LOG.info("reply.bodyAsString={}", bodyString);
			Assert.assertNotNull(bodyString);
		}
	}
	
//	2.5 노선 그래픽 데이터 검색
	@Test
	public void testLoadLaneMap() {
		
//		String uri = "http://218.234.32.207/denny_test/appletree/v1/0/map/LoadLane.asp?param={param}&SvcID={SvcID}&output={output}";
		String uri = "http://218.234.32.207/denny_test/appletree/v1/0/map/LoadLane.asp";
		ParameterMap parameters = new ParameterMap();
		parameters.put("param", "0:0@12018:1:-1:-1");
		parameters.put("SvcID", SvcID);
		parameters.put("output", output);
		
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
			String bodyString = new String(reply.getBody(), charSet);
			LOG.info("reply.bodyAsString={}", bodyString);
			Assert.assertNotNull(bodyString);
		}
	}
	
//	2.6 지하철역 세부 정보 조회
	@Test
	public void testSubwayInfoResult() {
		
//		String uri = "http://218.234.32.207/denny_test/appletree/v1/0/subway/SubwayInfoResult.asp?stationID={stationID}&SvcID={SvcID}&output={output}";
		String uri = "http://218.234.32.207/denny_test/appletree/v1/0/subway/SubwayInfoResult.asp";
		ParameterMap parameters = new ParameterMap();
		parameters.put("stationID", "130");
		parameters.put("SvcID", SvcID);
		parameters.put("output", output);
		
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
			String bodyString = new String(reply.getBody(), charSet);
			LOG.info("reply.bodyAsString={}", bodyString);
			Assert.assertNotNull(bodyString);
		}
	}
	
//	2.7 지하철역 환승 정보 조회
	@Test
	public void testSubwayTransitResult() {
		
//		String uri = "http://218.234.32.207/denny_test/appletree/v1/0/subway/SubwayTransitResult.asp?stationID={stationID}&SvcID={SvcID}&output={output}";
		String uri = "http://218.234.32.207/denny_test/appletree/v1/0/subway/SubwayTransitResult.asp";
		ParameterMap parameters = new ParameterMap();
		parameters.put("stationID", "130");
		parameters.put("SvcID", SvcID);
		parameters.put("output", output);
		
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
			String bodyString = new String(reply.getBody(), charSet);
			LOG.info("reply.bodyAsString={}", bodyString);
			Assert.assertNotNull(bodyString);
		}
	}
	
//	2.8 반경내 대중교통 POI 검색
	@Test
	public void testPointSearch() {
		
//		String uri = "http://218.234.32.207/denny_test/appletree/v1/0/POI/PointSearch.asp?x={x}&y={y}&radius={radius}&SvcID={SvcID}&output={output}";
		String uri = "http://218.234.32.207/denny_test/appletree/v1/0/POI/PointSearch.asp";
		ParameterMap parameters = new ParameterMap();
		parameters.put("x", "126.933361407195");
		parameters.put("y", "37.3643392278118");
		parameters.put("radius", "100");
		parameters.put("SvcID", SvcID);
		parameters.put("output", output);
		
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
			String bodyString = new String(reply.getBody(), charSet);
			LOG.info("reply.bodyAsString={}", bodyString);
			Assert.assertNotNull(bodyString);
		}
	}
	
//	2.9 지도 위 대중교통 POI 검색
	@Test
	public void testBoundarySearch() {
		
//		String uri = "http://218.234.32.207/denny_test/appletree/v1/0/POI/BoundarySearch.asp?Param={param}&SvcID={SvcID}&output={output}";
		String uri = "http://218.234.32.207/denny_test/appletree/v1/0/POI/BoundarySearch.asp";
		ParameterMap parameters = new ParameterMap();
		parameters.put("param", "127.045478316811:37.68882830829:127.055063420699:37.6370465749586");
		parameters.put("SvcID", SvcID);
		parameters.put("output", output);
		
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
			String bodyString = new String(reply.getBody(), charSet);
			LOG.info("reply.bodyAsString={}", bodyString);
			Assert.assertNotNull(bodyString);
		}
	}
}
