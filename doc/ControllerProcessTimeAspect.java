/**
 * KTH Developed by Java <br>
 *
 * @Copyright 2011 by Service Development Team, KTH, Inc. All rights reserved.
 *
 * This software is the confidential and proprietary information of KTH, Inc. <br>
 * You shall not disclose such Confidential Information and shall use it only <br>
 * in accordance with the terms of the license agreement you entered into with KTH.
 */
package kr.pudding.api.close.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * kr.pudding.sns.aspect.ControlProcessTimeAspect.java - Creation date: 2012. 1.
 * 3. <br>
 *
 * 컨트롤러 실행 시간 보기 위한 AOP
 *
 * @author KTH API Platform Team Song Gilju (gilju81@paran.com, 2925)
 * @version 1.0
 */
//@Aspect
//@Service("controllerProcessTimeAspect")
public class ControllerProcessTimeAspect {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

//	@Around("execution(public * kr.pudding.api.close.web.controller..*(..))")
	public Object around(ProceedingJoinPoint jp) throws Throwable {
		// 로직 실행 전
		long startTimeStamp = System.currentTimeMillis();
		// 로직 처리
		Object object = jp.proceed();
		// 로직 처리 후
		long endTimeStamp = System.currentTimeMillis();
		log.info("{} end - diff:{}", jp.getSignature().getName(),
				(endTimeStamp - startTimeStamp) + " ( " + startTimeStamp
						+ " ~ " + endTimeStamp + " ) ");
		return object;
	}

}
