package com.kthcorp.radix.filter;

import java.io.UnsupportedEncodingException;
import java.util.Hashtable;
import java.util.Map;

import org.junit.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

public class skipAuthForDemoTest {

	@Test
	public void nullTest() {
		
	}
	
	// 요 테스트 케이스는 시스템 테스트이다. 그런데 2012/07/30 현재 시스템테스트가 자동화 되지 않았기에 수동으로 실행한다.
	// 실행방법
	// 1. service-controller-service-component를 mvn -e jetty:run으로 구동한다.
	// 2. 본 클래스를 java application으로 실행시킨다.
	// 검증은 없고 눈으로 값을 확인하여야 한다.
	public static void main(String[] args) throws UnsupportedEncodingException {
		
		RestTemplate restTemplate = new RestTemplate();
		
		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.add("Referer", "developer.pudding.to");
		
		Map<String, String> uriVariables = new Hashtable<String, String>();
		MultiValueMap<String, Object> requestBody = new LinkedMultiValueMap<String, Object>();
		
		HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<MultiValueMap<String, Object>>(requestBody, requestHeaders);
		String uri = "http://localhost:8090/puddingto/1/users/1234?access_token=ACCESS-TOKEN";
		ResponseEntity<byte[]> responseEntity = restTemplate.exchange(uri , HttpMethod.GET, requestEntity, byte[].class, uriVariables);
		
		System.out.println("statusCode="+responseEntity.getStatusCode());
		System.out.println("body="+new String(responseEntity.getBody(), "utf-8"));
		
	}
	
}
