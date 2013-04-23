package com.kthcorp.radix.domain.canonical;

import com.kthcorp.radix.util.JsonBuilder;

public class RadixFileResource {

	private String originalFilename;
	private String resource;
	
	public RadixFileResource(String originalFilename, String resource) {
		this.originalFilename = originalFilename;
		this.resource = resource;
	}
	public String getOriginalFilename() {
		return originalFilename;
	}
	public void setOriginalFilename(String originalFilename) {
		this.originalFilename = originalFilename;
	}
	public String getResource() {
		return resource;
	}
	public void setResource(String resource) {
		this.resource = resource;
	}
	
	@Override 
	public String toString() {
		JsonBuilder jb = new JsonBuilder();
		jb.put("class", this.getClass());
		jb.put("originalFilename", originalFilename);
		jb.put("resource", resource);
		return jb.toString();
	}
	
}
