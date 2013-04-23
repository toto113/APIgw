package com.kthcorp.radix.util;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.zeromq.ZMQ;
import org.zeromq.ZMQ.Socket;

public class ZeroMqSocketFactory implements FactoryBean<ZMQ.Socket>, InitializingBean {
	
	private ZMQ.Context context;
	
	private ZMQ.Socket socket;
	
	private String url;
	
	private ZeroMqSocketType zeroMqSocketType;
	
	private boolean binding;
	
	public void setBinding(boolean binding) {
		this.binding = binding;
	}
	
	public void setUrl(String url) {
		this.url = url;
	}
	
	public void setZeroMqSocketType(ZeroMqSocketType zeroMqSocketType) {
		this.zeroMqSocketType = zeroMqSocketType;
	}
	
	@Override
	public Socket getObject() throws Exception {
		return socket;
	}
	
	@Override
	public Class<?> getObjectType() {
		return ZMQ.Socket.class;
	}
	
	@Override
	public boolean isSingleton() {
		return true;
	}
	
	@Override
	public void afterPropertiesSet() throws Exception {
		int type = this.zeroMqSocketType.getValue();
		
		this.context = ZMQ.context(1);
		
		this.socket = this.context.socket(type);
		if(this.binding) {
			this.socket.bind(this.url);
		} else {
			this.socket.connect(url);
		}
	}
	
	public static enum ZeroMqSocketType {
		PAIR(0), PUB(1), SUB(2), REQ(3), REP(4), XREQ(5), XREP(6), PULL(7), PUSH(7);
		
		private int type;
		
		ZeroMqSocketType(int type) {
			this.type = type;
		}
		
		public int getValue() {
			return this.type;
		}
	}
}
