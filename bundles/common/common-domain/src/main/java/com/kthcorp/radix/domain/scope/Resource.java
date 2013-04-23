package com.kthcorp.radix.domain.scope;

import org.springframework.util.StringUtils;

import com.kthcorp.radix.domain.exception.ValidateException;
import com.kthcorp.radix.domain.scope.Scope.IDType;
import com.kthcorp.radix.util.JsonBuilder;

public class Resource {
	private IDType idType;
	private String id;
	
	public IDType getIdType() {
		return idType;
	}
	public void setIdType(IDType idType) {
		this.idType = idType;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public static Resource fromString(String resource) throws ValidateException {
		if(!StringUtils.hasText(resource)) {
			throw new ValidateException("resource contain scope does not exists");
		}
		Resource ret = new Resource();
		if('S'==resource.charAt(0)) {
			ret.idType = IDType.ServiceAPI;
			ret.setId(resource.substring(1));
		} else {
			ret.idType = IDType.Package;
			if('P'==resource.charAt(0)) {
				ret.setId(resource.substring(1));
			} else {
				ret.setId(resource);
			}
		}
		return ret;
	}
	@Override
	public boolean equals(Object obj) {
		Resource resource = (Resource) obj;
		if(idType==resource.idType) {
			if(id==resource.id) {
				return true;
			}
		}
		return false;
	}
	
	@Override
	public String toString() {
		JsonBuilder jb = new JsonBuilder();
		jb.put("class", this.getClass());
		jb.put("id", id);
		jb.put("idType", idType);
		return jb.toString();
	}
	
}