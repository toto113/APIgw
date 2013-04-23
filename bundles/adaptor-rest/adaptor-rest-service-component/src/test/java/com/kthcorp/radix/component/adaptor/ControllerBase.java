package com.kthcorp.radix.component.adaptor;

import java.io.BufferedReader;
import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import com.kthcorp.radix.util.UuidViewableLoggerFactory;


public class ControllerBase {
	
	protected final Logger LOG = UuidViewableLoggerFactory.getLogger(this.getClass());
	
	@SuppressWarnings("unchecked")
	protected void debug(HttpServletRequest request) {

		LOG.info("##### request={} parameter debugging #####", request);
		LOG.info("method:{}", request.getMethod());
		LOG.info("requestURI:{}", request.getRequestURI());
		LOG.info("queryString:{}", request.getQueryString());
		LOG.info("headers:");
		Enumeration<String> em = request.getHeaderNames();
		while (em.hasMoreElements()) {
			String key = em.nextElement();
			LOG.info("\t{}={}", key, request.getHeader(key));
		}

		LOG.info("body:");
		BufferedReader br = null;
		try {
			br = request.getReader();
			if(br != null) {
				String line = null;
				while((line = br.readLine()) != null) {
					LOG.info("{}", line);
				}
			}
		} catch(Exception e) {
			e.printStackTrace();
		}

		try {
			LOG.info("parameters:");
			em = request.getParameterNames();
			while (em.hasMoreElements()) {
				String key = em.nextElement();
				LOG.info("\t{}={}", key, request.getParameter(key));
			}
		} catch(Exception e) {
			LOG.error(e.getMessage());
		}
		// logger.info("attributes:");
		// em = request.getAttributeNames();
		// while(em.hasMoreElements()) {
		// String key = (String)em.nextElement();
		// logger.info("\t{}={}", key, request.getAttribute(key));
		// }
		
		LOG.info("\n");
	}
}
