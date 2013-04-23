package com.kthcorp.radix.domain.scope;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.util.StringUtils;

import com.kthcorp.radix.domain.exception.ValidateException;
import com.kthcorp.radix.util.JsonBuilder;

/**
 * Scope
 * 
 * Type 
 * 		ALL (entire policies at request time)
 * 			scope->ALL
 * 		Each
 * 			Package Only
 * 				scope->{packageID} {packageID}...
 * 			Mixed
 * 				scope->
 * 						{packageID} S:{serviceAPIID}
 * 						P:{packageID} S:{serviceAPIID}
 * 						S:{serviceAPIID} S:{serviceAPIID}
 * @author starlight60
 *
 */
public class Scope {
	
	// Legend
	public static enum Type {ALL,COLLECTION};
	public static enum IDType {Package,ServiceAPI};
	
	// Attributes
	private Type type;
	private String scopeKey;
	private List<Resource> resources;
	
	public Scope() {
	}
	
	public List<Resource> getResources() {
		return resources;
	}

	public void setResources(List<Resource> resources) {
		this.resources = resources;
	}
	
	public static Scope fromString(String scopeString) throws ValidateException {

		// Default "ALL"
		Scope scope = new Scope();
		if(!StringUtils.hasText(scopeString)) {
			scope.type = Scope.Type.ALL;
			return scope;
		}
		String[] scopeList = scopeString.split(" ");
		
		if("ALL".equalsIgnoreCase(scopeList[0])) {
			scope.setType(Type.ALL);
		} else {
			scope.setType(Type.COLLECTION);
			List<Resource> resources = new ArrayList<Resource>();
			for(String scopeEach: scopeList) {
				resources.add(Resource.fromString(scopeEach));
			}
			scope.setResources(resources);
		}
		return scope;
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	public String getScopeKey() {
		return scopeKey;
	}

	public void setScopeKey(String scopeKey) {
		this.scopeKey = scopeKey;
	}
	
	public String generateScopeKey() {
		String key = UUID.randomUUID().toString();
		this.scopeKey = key;
		return key;
	}
	
	@Override
	public String toString() {
		JsonBuilder jb = new JsonBuilder();
		jb.put("class", this.getClass());
		jb.put("type", type);
		jb.put("scopeKey", scopeKey);
		jb.put("resources", resources);
		return jb.toString();
	}
}
