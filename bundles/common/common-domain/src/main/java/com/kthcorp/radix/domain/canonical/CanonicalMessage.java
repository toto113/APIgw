package com.kthcorp.radix.domain.canonical;

import java.io.Serializable;

import com.kthcorp.radix.util.JsonBuilder;

public class CanonicalMessage implements Serializable {

	private static final long serialVersionUID = -5596433094590842281L;

	private CanonicalMessageHeader header;
	
	private CanonicalMessagePayload payload;
	
	public CanonicalMessageHeader getHeader() {
		return header;
	}
	
	public void setHeader(CanonicalMessageHeader header) {
		this.header = header;
	}
	
	public CanonicalMessagePayload getPayload() {
		return payload;
	}
	
	public void setPayload(CanonicalMessagePayload payload) {
		this.payload = payload;
	}
	
	@Override
	public String toString() {
		JsonBuilder jb = new JsonBuilder();
		jb.put("class", this.getClass());
		jb.put("header", header);
		jb.put("payload", payload);
		return jb.toString();
	}
}