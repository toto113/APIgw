package com.kthcorp.radix.component;

import java.util.HashMap;

import org.apache.camel.ExchangePattern;
import org.apache.camel.ProducerTemplate;
import org.springframework.beans.factory.InitializingBean;
import org.zeromq.ZMQ;

public class ZeroMqReplyServerComponent implements InitializingBean, Runnable {
	
	private ZMQ.Socket socket;
	
	private ProducerTemplate producer;
	
	private String url;
	
	public void setSocket(ZMQ.Socket socket) {
		this.socket = socket;
	}
	
	public void setProducer(ProducerTemplate producer) {
		this.producer = producer;
	}
	
	public void setUrl(String url) {
		this.url = url;
	}
	
	@Override
	public void afterPropertiesSet() throws Exception {
		Thread t = new Thread(this);
		t.start();
	}
	
	@Override
	public void run() {
		while(!Thread.currentThread().isInterrupted()) {
			byte[] reply = this.socket.recv(0);
			
			this.producer.sendBodyAndHeaders(this.url, ExchangePattern.InOnly, reply, new HashMap<String, Object>());
			
			this.socket.send("OK".getBytes(), 0);
		}
		
	}
	
}
