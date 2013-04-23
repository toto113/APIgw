package com.kthcorp.radix.domain.packages;

import java.util.HashMap;
import java.util.Iterator;

import org.json.JSONException;
import org.json.JSONObject;

import com.kthcorp.radix.util.JsonBuilder;

public class Parameters extends HashMap<String, String> {
	
	private static final long serialVersionUID = -8684007083697117012L;
	
	public static Parameters fromJSONObject(JSONObject obj) throws JSONException {
		Parameters r = new Parameters();
		if(obj != null) {
			if(obj.length() > 0) {
				@SuppressWarnings("rawtypes")
				Iterator keys = obj.keys();
				while(keys.hasNext()) {
					Object key = keys.next();
					if(key!=null) {
						if(key instanceof String) {
							String keyInString = key.toString();
							r.put(keyInString, obj.getString(keyInString));
						}
					}
				}
			}
		}
		return r;
	}
	
	public String toJSONString() throws JSONException {
		JSONObject obj = new JSONObject();
		for(java.util.Map.Entry<String, String> entry : this.entrySet()) {
			obj.put(entry.getKey(), entry.getValue());
		}
		return obj.toString();
	}
	
	@Override
	public String toString() {
		JsonBuilder jb = new JsonBuilder();
		jb.put("class", this.getClass());
		for(String key : this.keySet()) {
			jb.put(key, this.get(key));
		}
		return jb.toString();
	}
}
