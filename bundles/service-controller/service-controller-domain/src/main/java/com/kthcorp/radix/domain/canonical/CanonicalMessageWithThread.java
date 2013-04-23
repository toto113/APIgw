package com.kthcorp.radix.domain.canonical;

public class CanonicalMessageWithThread extends CanonicalMessage {

	private static final long serialVersionUID = 1972560270938962595L;
	private Thread t;
	
	public Thread getT() {
		return t;
	}
	
	public void setT(Thread t) {
		this.t = t;
	}
}
