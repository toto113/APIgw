package com.kthcorp.radix.adaptor.rest.service;

import java.io.IOException;
import java.io.InputStream;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;


public class RadixHttpComponentsClientHttpResponse implements ClientHttpResponse {

	private final HttpResponse httpResponse;

	private HttpHeaders headers;
	
	public RadixHttpComponentsClientHttpResponse(HttpResponse httpResponse) {
		this.httpResponse = httpResponse;
	}
	
	@Override
	public InputStream getBody() throws IOException {
		HttpEntity entity = this.httpResponse.getEntity();
		return entity != null ? entity.getContent() : null;
	}

	@Override
	public HttpHeaders getHeaders() {
		if (this.headers == null) {
			this.headers = new HttpHeaders();
			for (Header header : this.httpResponse.getAllHeaders()) {
				this.headers.add(header.getName(), header.getValue());
			}
		}
		return this.headers;
	}

	@Override
	public HttpStatus getStatusCode() throws IOException {
		return HttpStatus.valueOf(this.httpResponse.getStatusLine().getStatusCode());
	}

	@Override
	public String getStatusText() throws IOException {
		return this.httpResponse.getStatusLine().getReasonPhrase();
	}

	@Override
	public void close() {
		HttpEntity entity = this.httpResponse.getEntity();
		if (entity != null) {
			try {
				// Release underlying connection back to the connection manager
				EntityUtils.consume(entity);
			}
			catch (IOException e) {
				// ignore
			}
		}
	}
	
}
