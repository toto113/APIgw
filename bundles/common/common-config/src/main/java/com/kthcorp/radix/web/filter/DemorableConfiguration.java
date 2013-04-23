package com.kthcorp.radix.web.filter;

/**
 * service-controller-service-component와 dommon-service-component에서 동시에 사용하는 설정내용을 위한 클래스
 * 어느 한곳에서 설정하고 싶지만, 모듈간의 의존관계에서 순환이 발생하기 때문에 굳이 이런 클래스를 둔다.
 * 
 * @author dhrim
 *
 */
public class DemorableConfiguration {

	public static final String CLIENT_KEY_FOR_DEMO = "clinetKeyForDemo";
	
}
