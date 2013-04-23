package com.kthcorp.radix.adaptor.rest.service;

import java.io.IOException;
import java.net.URI;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.protocol.HttpContext;
import org.slf4j.Logger;
import com.kthcorp.radix.util.UuidViewableLoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.util.Assert;

public class RadixHttpComponentsClientHttpRequestFactory implements ClientHttpRequestFactory, DisposableBean, InitializingBean {
	
	private final Logger LOG = UuidViewableLoggerFactory.getLogger(this.getClass());

	private static final int DEFAULT_MAX_TOTAL_CONNECTIONS = 100;

	private static final int DEFAULT_MAX_CONNECTIONS_PER_ROUTE = 5;

	private static final int DEFAULT_READ_TIMEOUT_MILLISECONDS = (60 * 1000);

	private HttpClient httpClient;

	public RadixHttpComponentsClientHttpRequestFactory() {
		
	}
	
	private int maxTotalConnections;
	
	public void setMaxTotalConnections(int maxTotalConnections) {
		this.maxTotalConnections = maxTotalConnections;
	}
	
	private int maxTotalConnectionsPerRoute;
	
	public void setMaxTotalConnectionsPerRoute(int maxTotalConnectionsPerRoute) {
		this.maxTotalConnectionsPerRoute = maxTotalConnectionsPerRoute;
	}
	
	private int connectTimeout;
	
	public void setConnectTimeout(int connectTimeout) {
		Assert.isTrue(connectTimeout >= 0, "Connect Timeout must be a non-negative value");
		this.connectTimeout = connectTimeout;
	}
	
	private int readTimeout;
	
	public void setReadTimeout(int readTimeout) {
		Assert.isTrue(readTimeout >= 0, "Read Timeout must be a non-negative value");
		this.readTimeout = readTimeout;
	}
	
	@Override
	public void afterPropertiesSet() throws Exception {
		
		LOG.debug("initializing RadixHttpComponentClientHttpRequestFactory");
		
		SchemeRegistry schemeRegistry = new SchemeRegistry();
		schemeRegistry.register(new Scheme("http", 80, PlainSocketFactory.getSocketFactory()));
		schemeRegistry.register(new Scheme("https", 443, SSLSocketFactory.getSocketFactory()));

		ThreadSafeClientConnManager connectionManager = new ThreadSafeClientConnManager(schemeRegistry);
		if(this.maxTotalConnections > 0) {
			connectionManager.setMaxTotal(this.maxTotalConnections);
		} else {
			connectionManager.setMaxTotal(DEFAULT_MAX_TOTAL_CONNECTIONS);
		}		
		if(this.maxTotalConnectionsPerRoute > 0) {
			connectionManager.setDefaultMaxPerRoute(this.maxTotalConnectionsPerRoute);
		} else {
			connectionManager.setDefaultMaxPerRoute(DEFAULT_MAX_CONNECTIONS_PER_ROUTE);
		}

		this.httpClient = new DefaultHttpClient(connectionManager);
		if(this.connectTimeout > 0) {
			this.httpClient.getParams().setIntParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, this.connectTimeout);
		}
		if(this.readTimeout > 0) {
			this.httpClient.getParams().setIntParameter(CoreConnectionPNames.SO_TIMEOUT, this.readTimeout);
		} else {
			this.httpClient.getParams().setIntParameter(CoreConnectionPNames.SO_TIMEOUT, DEFAULT_READ_TIMEOUT_MILLISECONDS);
		}
	}
	
	@Override
	public void destroy() throws Exception {
		getHttpClient().getConnectionManager().shutdown();
	}

	@Override
	public ClientHttpRequest createRequest(URI uri, HttpMethod httpMethod) throws IOException {
		HttpUriRequest httpRequest = createHttpUriRequest(httpMethod, uri);
		postProcessHttpRequest(httpRequest);
		return new RadixHttpComponentsClientHttpRequest(getHttpClient(), httpRequest, createHttpContext(httpMethod, uri));
	}
	
	public HttpClient getHttpClient() {
		return this.httpClient;
	}
	
	protected HttpUriRequest createHttpUriRequest(HttpMethod httpMethod, URI uri) {
		switch (httpMethod) {
			case GET:
				return new HttpGet(uri);
			case DELETE:
				return new HttpDelete(uri);
//			case HEAD:
//				return new HttpHead(uri);
//			case OPTIONS:
//				return new HttpOptions(uri);
			case POST:
				return new HttpPost(uri);
			case PUT:
				return new HttpPut(uri);
//			case TRACE:
//				return new HttpTrace(uri);
			default:
				throw new IllegalArgumentException("Invalid HTTP method: " + httpMethod);
		}
	}
	
	protected void postProcessHttpRequest(HttpUriRequest request) {
	}
	
	protected HttpContext createHttpContext(HttpMethod httpMethod, URI uri) {
        return null;
    }
}
