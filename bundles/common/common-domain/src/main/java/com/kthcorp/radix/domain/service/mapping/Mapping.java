package com.kthcorp.radix.domain.service.mapping;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.kthcorp.radix.domain.service.api.protocol.http.ParameterMap;
import com.kthcorp.radix.util.JsonBuilder;

public class Mapping implements Serializable {
	
	private static final long serialVersionUID = -8989504971725826601L;
	
	private ParameterMap map = null;
	
	public Mapping() {
		this.map = new ParameterMap();
	}
	
	public void putMapping(String from, String to) {
		map.put(from, to);
	}
	
	public Collection<String> mapTo(String from) {
		if(from != null) {
			return map.get(from);
		}
		return Collections.emptyList();
	}
	
	public String toJSONString() throws JSONException {
		JSONObject obj = new JSONObject();
		for(String key : map.keySet()) {
			JSONArray vObj = new JSONArray();
			for(String value : map.get(key)) {
				vObj.put(value);
			}
			obj.put(key, vObj);
		}
		return obj.toString();
	}
	
	public static Mapping fromJSONString(String jsonString) throws JSONException {
		Mapping mapping = new Mapping();
		if(jsonString != null) {
			JSONObject obj = new JSONObject(jsonString);
			@SuppressWarnings("unchecked")
			Iterator<String> keys = obj.keys();
			while(keys.hasNext()) {
				String key = keys.next();
				JSONArray vObj = obj.getJSONArray(key);
				for(int i = 0, l = vObj.length(); i < l; i++) {
					mapping.map.put(key, vObj.getString(i));
				}
			}
		}
		return mapping;
	}
	
	public Set<String> getKeys() {
		return map.keySet();
	}
	
	@Override
	public String toString() {
		JsonBuilder jb = new JsonBuilder();
		jb.put("class", this.getClass());
		for(String key : map.keySet()) {
			jb.put(key, map.get(key));
		}
		return jb.toString();
	}
}