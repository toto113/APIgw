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
public class KtoTestSkip extends AbstractProvider {

/**
 * 
 * 		183.98.48.18
{
“RETCODE”:”리턴코드”,
“RETMSG”:”리턴코드메시지”,
“ITEM”:[
        … 서비스별 각 object형 결과 data …
{ name : value },
{ name : value }
]
}

리턴코드	리턴코드 메시지
RET0000	SUCCESS
RET0001	NOT EXIST NECESSARY PARAMS(파라미터이름)
RET0002	NOT EXIST RESOURCE
RET0003	NOT ACCESS HTTPS
RET0004	
RET0005	INTERNAL SERVICE_ERROR(에러코드)
RET9999	UNKNOWN ERROR

 */
	@Test
	public void testGetMainList() {

//		String uri = "http://app.visitkorea.or.kr/Api.do?method={method}&lang_code={lang_code}";
		String uri = "http://app.visitkorea.or.kr/Api.do";
		ParameterMap parameters = new ParameterMap();
		parameters.put("method", "getMainList");
		parameters.put("lang_code", "K");
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
	
	@Test
	public void testGetContentList() {

//		String uri = "http://app.visitkorea.or.kr/Api.do?method={method}&lang_code={lang_code}&category_id={category_id}&category_type={category_type}";
		String uri = "http://app.visitkorea.or.kr/Api.do";
		ParameterMap parameters = new ParameterMap();
		parameters.put("method", "getContentList");
		parameters.put("lang_code", "K");
		parameters.put("category_id", "1234");
		parameters.put("category_type", "Folderid");
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
	
	@Test
	public void searchContentList() {

//		String uri = "http://app.visitkorea.or.kr/Api.do?method={method}&lang_code={lang_code}&category_id={category_id}&category_type={category_type}&sort_term={sort_term}&Cnt={cnt}" +
//				"&page={page}&area_code={area_code}&sigungu_code={sigungu_code}&initial={initial}&location_x={location_x}&location_y={location_y}";
		String uri = "http://app.visitkorea.or.kr/Api.do";
		ParameterMap parameters = new ParameterMap();
		parameters.put("method", "getContentList");
//		lang_code	String	언어 모드 (K : 국문, E : 영문)
		parameters.put("lang_code", "K");
//		category_id	String 	사용자 선택 카테고리
		parameters.put("category_id", "1234");
//		category_type	String	중분류, 소분류, Folderid, chktextbook, benikia, traditional, goodstay, theme, shopetc, eatetc, sleepetc, visitinfo
		parameters.put("category_type", "Folderid");
//		sort_term	Number	0	인기순 (Default)
//		1	지역별 그룹핑 2건씩
//		2	가나다순 그룹핑 2건씩
//		3	지역별 정렬
//		4	가나다순 정렬
//		5	내주변
		parameters.put("sort_term", "0");
		parameters.put("cnt", "100");
		parameters.put("page", "1");
		parameters.put("area_code", "");
		parameters.put("sigungu_code", "");
		parameters.put("initial", "");
		parameters.put("location_x", "");
		parameters.put("location_y", "");

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
	
	@Test
	public void testGetAreaContentList() {

//		String uri = "http://app.visitkorea.or.kr/Api.do?method={method}&lang_code={lang_code}&sigungu_code={sigungu_code}&area_code={area_code}";
		String uri = "http://app.visitkorea.or.kr/Api.do";
		ParameterMap parameters = new ParameterMap();
		parameters.put("method", "getAreaContentList");
		parameters.put("lang_code", "K");
		parameters.put("sigungu_code", "1");
		parameters.put("area_code", "1");
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
	
	@Test
	public void testGetAreaDetailContentList() {

//		String uri = "http://app.visitkorea.or.kr/Api.do?method={method}&lang_code={lang_code}&sigungu_code={sigungu_code}&area_code={area_code}" +
//				"&req_category={req_category}&Cnt={cnt}&page={page}&sort_term={sort_term}";
		String uri = "http://app.visitkorea.or.kr/Api.do";
		ParameterMap parameters = new ParameterMap();
		parameters.put("method", "getAreaDetailContentList");
		parameters.put("lang_code", "K");
		parameters.put("sigungu_code", "1");
		parameters.put("area_code", "1");
		parameters.put("req_category", "");
		parameters.put("cnt", "100");
		parameters.put("page", "1");
		parameters.put("sort_term", "0");
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
	
	@Test
	public void testGetSearchContentList() {

//		String uri = "http://app.visitkorea.or.kr/Api.do?method={method}&lang_code={lang_code}&search_keyword={search_keyword}&Cnt={cnt}&page={page}&sort_term={sort_term}";
		String uri = "http://app.visitkorea.or.kr/Api.do";
		ParameterMap parameters = new ParameterMap();
		parameters.put("method", "getSearchContentList");
		parameters.put("lang_code", "K");
		parameters.put("search_keyword", "한국");
		parameters.put("cnt", "100");
		parameters.put("page", "1");
		parameters.put("sort_term", "0");
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
	
	@Test
	public void testGetEndPageUrl() {

//		String uri = "http://app.visitkorea.or.kr/Api.do?method={method}&lang_code={lang_code}&cid={cid}";
		String uri = "http://app.visitkorea.or.kr/Api.do";
		ParameterMap parameters = new ParameterMap();
		parameters.put("method", "getEndPageUrl");
		parameters.put("lang_code", "K");
		parameters.put("cid", "192");
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
	
	@Test
	public void testGetMyBoxList() {

//		String uri = "http://app.visitkorea.or.kr/Api.do?method={method}&lang_code={lang_code}&mac_addr={mac_addr}&cnt={cnt}&page={page}";
		String uri = "http://app.visitkorea.or.kr/Api.do";
		ParameterMap parameters = new ParameterMap();
		parameters.put("method", "getMyBoxList");
		parameters.put("lang_code", "K");
		parameters.put("mac_addr", "1234567890");
		parameters.put("cnt", "100");
		parameters.put("page", "1");
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
	
	@Test
	public void testDeleteExecMyBox() {

//		String uri = "http://app.visitkorea.or.kr/Api.do?method={method}&lang_code={lang_code}&mac_addr={mac_addr}&mode={mode}&cid={cid}";
		String uri = "http://app.visitkorea.or.kr/Api.do";
		ParameterMap parameters = new ParameterMap();
		parameters.put("method", "execMyBox");
		parameters.put("lang_code", "K");
		parameters.put("mac_addr", "1234567890");
//		mode		등록(ADD), 삭제(DEL)
		parameters.put("mode", "DEL");
		parameters.put("cid", "192");
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
	
	@Test
	public void testExecMyBox() {

//		String uri = "http://app.visitkorea.or.kr/Api.do?method={method}&lang_code={lang_code}&mac_addr={mac_addr}&mode={mode}&cid={cid}";
		String uri = "http://app.visitkorea.or.kr/Api.do";
		ParameterMap parameters = new ParameterMap();
		parameters.put("method", "execMyBox");
		parameters.put("lang_code", "K");
		parameters.put("mac_addr", "1234567890");
//		mode		등록(ADD), 삭제(DEL)
		parameters.put("mode", "ADD");
		parameters.put("cid", "192");
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
	
	@Test
	public void testDuplicateExecMyBox() {

//		String uri = "http://app.visitkorea.or.kr/Api.do?method={method}&lang_code={lang_code}&mac_addr={mac_addr}&mode={mode}&cid={cid}";
		String uri = "http://app.visitkorea.or.kr/Api.do";
		ParameterMap parameters = new ParameterMap();
		parameters.put("method", "execMyBox");
		parameters.put("lang_code", "K");
		parameters.put("mac_addr", "1234567890");
//		mode		등록(ADD), 삭제(DEL)
		parameters.put("mode", "ADD");
		parameters.put("cid", "192");
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
	
	@Test
	public void testGetNearbyMap() {

//		String uri = "http://app.visitkorea.or.kr/Api.do?method={method}&lang_code={lang_code}&location_x={location_x}&location_y={location_y}&group_code={group_code}&current_radius={current_radius}";
		String uri = "http://app.visitkorea.or.kr/Api.do";
		ParameterMap parameters = new ParameterMap();
		parameters.put("method", "getNearbyMap");
		parameters.put("lang_code", "K");
		parameters.put("location_x", "37.33");
		parameters.put("location_y", "126.55");
		parameters.put("group_code", "AAA");
		parameters.put("current_radius", "100");
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
	
	@Test
	public void testGetNearbyMapDetail() {

//		String uri = "http://app.visitkorea.or.kr/Api.do?method={method}&lang_code={lang_code}&location_x={location_x}&location_y={location_y}&group_code={group_code}&mod_x={mod_x}&mod_y={mod_y}";
		String uri = "http://app.visitkorea.or.kr/Api.do";
		ParameterMap parameters = new ParameterMap();
		parameters.put("method", "getNearbyMapDetail");
		parameters.put("lang_code", "K");
		parameters.put("location_x", "37.33");
		parameters.put("location_y", "126.55");
		parameters.put("group_code", "AAA");
		parameters.put("mod_x", "10");
		parameters.put("mod_y", "10");
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
	
	@Test
	public void testGetNearbyMapContent() {

//		String uri = "http://app.visitkorea.or.kr/Api.do?method={method}&lang_code={lang_code}&location_x={location_x}&location_y={location_y}&group_code={group_code}&current_radius={current_radius}&page={page}";
		String uri = "http://app.visitkorea.or.kr/Api.do";
		ParameterMap parameters = new ParameterMap();
		parameters.put("method", "getNearbyMapContent");
		parameters.put("lang_code", "K");
		parameters.put("location_x", "37.33");
		parameters.put("location_y", "126.55");
		parameters.put("group_code", "AAA");
		parameters.put("current_radius", "100");
		parameters.put("cnt", "100");
		parameters.put("page", "1");
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
	
	@Test
	public void testGetPhoneInfo() {

//		String uri = "http://app.visitkorea.or.kr/Api.do?method={method}&lang_code={lang_code}&cid={cid}";
		String uri = "http://app.visitkorea.or.kr/Api.do";
		ParameterMap parameters = new ParameterMap();
		parameters.put("method", "getPhoneInfo");
		parameters.put("lang_code", "K");
		parameters.put("cid", "192");
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
	
	@Test
	public void testGetMediaInfo() {

//		String uri = "http://app.visitkorea.or.kr/Api.do?method={method}&lang_code={lang_code}&cid={cid}&encode={encode}";
		String uri = "http://app.visitkorea.or.kr/Api.do";
		ParameterMap parameters = new ParameterMap();
		parameters.put("method", "getMediaInfo");
		parameters.put("lang_code", "K");
		parameters.put("cid", "192");
//		ecode	String	이벤트를(요청하는 자료타입을) 구분하기 위한 코드 (I:이미지, V:동영상)
		parameters.put("encode", "I");
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
	
	@Test
	public void testGetMobileBookIndex() {

//		String uri = "http://app.visitkorea.or.kr/Api.do?method={method}&lang_code={lang_code}";
		String uri = "http://app.visitkorea.or.kr/Api.do";
		ParameterMap parameters = new ParameterMap();
		parameters.put("method", "getMobileBookIndex");
		parameters.put("lang_code", "K");
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
	
	@Test
	public void testGetNotice() {

//		String uri = "http://app.visitkorea.or.kr/Api.do?method={method}&lang_code={lang_code}";
		String uri = "http://app.visitkorea.or.kr/Api.do";
		ParameterMap parameters = new ParameterMap();
		parameters.put("method", "getNotice");
		parameters.put("lang_code", "K");
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
	
	@Test
	public void testGetNearbyVisitMap() {

//		String uri = "http://app.visitkorea.or.kr/Api.do?method={method}&lang_code={lang_code}&location_x={location_x}&location_y={location_y}&group_code={group_code}&current_radius={current_radius}";
		String uri = "http://app.visitkorea.or.kr/Api.do";
		ParameterMap parameters = new ParameterMap();
		parameters.put("method", "getNearbyVisitMap");
		parameters.put("lang_code", "K");
		parameters.put("location_x", "37");
		parameters.put("location_y", "126");
		parameters.put("group_code", "AAA");
		parameters.put("current_radius", "100");
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
	
	@Test
	public void testGetNearbyVisitMapDetail() {

//		String uri = "http://app.visitkorea.or.kr/Api.do?method={method}&lang_code={lang_code}&location_x={location_x}&location_y={location_y}&group_code={group_code}&mod_x={mod_x}&mod_y={mod_y}&cid={cid}";
		String uri = "http://app.visitkorea.or.kr/Api.do";
		ParameterMap parameters = new ParameterMap();
		parameters.put("method", "getNearbyVisitMapDetail");
		parameters.put("lang_code", "K");
		parameters.put("location_x", "37");
		parameters.put("location_y", "126");
		parameters.put("group_code", "AAA");
		parameters.put("mod_x", "10");
		parameters.put("mod_y", "10");
		parameters.put("cid", "1234");
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
	
	@Test
	public void testGetNearbyVisitMapContent() {

//		String uri = "http://app.visitkorea.or.kr/Api.do?method={method}&lang_code={lang_code}&location_x={location_x}&location_y={location_y}&group_code={group_code}&current_radius={current_radius}&page={page}";
		String uri = "http://app.visitkorea.or.kr/Api.do";
		ParameterMap parameters = new ParameterMap();
		parameters.put("method", "getNearbyVisitMapContent");
		parameters.put("lang_code", "K");
		parameters.put("location_x", "37");
		parameters.put("location_y", "126");
		parameters.put("group_code", "AAA");
		parameters.put("current_radius", "100");
		parameters.put("cnt", "100");
		parameters.put("page", "1");
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
	
	@Test
	public void testGetRecommendList() {

//		String uri = "http://app.visitkorea.or.kr/Api.do?method={method}&lang_code={lang_code}&cnt={cnt}&page={page}&sort_term={sort_term}&sort_msg={sort_msg}";
		String uri = "http://app.visitkorea.or.kr/Api.do";
		ParameterMap parameters = new ParameterMap();
		parameters.put("method", "getRecommendList");
		parameters.put("lang_code", "K");
		parameters.put("cnt", "100");
		parameters.put("page", "1");
//		Sort_term	Number	0	연도별
//		1	월별
//		2	지역별
		parameters.put("sort_term", "0");
		parameters.put("sort_msg", "0");
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
