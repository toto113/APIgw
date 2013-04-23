package com.kthcorp.radix.domain.platform.api;

import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import com.kthcorp.radix.util.JsonBuilder;

public class ResponseMessageBodyMap<E, V> extends HashMap<E, V> implements ResponseMessageBody {
	
	private static final long serialVersionUID = -1776476568457664229L;
	private ResponseMessageBodyType bodyType = ResponseMessageBodyType.MAP;
	
	@Override
	public JSONObject toJSONObject() throws JSONException {
		JSONObject returnObj = new JSONObject();
		for(java.util.Map.Entry<E, V> entry : this.entrySet()) {
			if(entry.getKey() != null) {
				if(entry.getKey() instanceof String) {
					returnObj.put(entry.getKey().toString(), entry.getValue());
				}
			}
		}
		return returnObj;
	}
	
	@Override
	public ResponseMessageBodyType getBodyType() {
		return bodyType;
	}
	
	@Override
	public String toString() {
		JsonBuilder jb = new JsonBuilder();
		jb.put("class", this.getClass());
		for(Object key : this.keySet()) {
			if(key==null) { continue; }
			jb.put(key.toString(), this.get(key));
		}
		return jb.toString();
	}
	
}

