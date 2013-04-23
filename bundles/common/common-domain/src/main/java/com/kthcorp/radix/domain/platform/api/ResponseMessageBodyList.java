package com.kthcorp.radix.domain.platform.api;

import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.kthcorp.radix.util.JsonBuilder;

public class ResponseMessageBodyList<E, V> extends ArrayList<Map<E, V>> implements ResponseMessageBody {
	
	private static final long serialVersionUID = -1776476568457664229L;
	private ResponseMessageBodyType bodyType = ResponseMessageBodyType.LIST;
	
	public JSONArray toJSONArray() throws JSONException {
		JSONArray returnObj = new JSONArray();
		if(this.size() > 0) {
			for(Map<E, V> map : this) {
				if(map != null) {
					JSONObject eObj = new JSONObject();
					for(Entry<E, V> entry : map.entrySet()) {
						if(entry.getKey() != null) {
							eObj.put(entry.getKey().toString(), entry.getValue());
						}
					}
					returnObj.put(eObj);
				}
			}
		}
		return returnObj;
	}
	
	public ResponseMessageBodyType getBodyType() {
		return bodyType;
	}
	
	@Override
	public JSONObject toJSONObject() throws JSONException {
		JSONObject obj = new JSONObject();
		
		JSONObject meta = new JSONObject();
		meta.put("meta", this.size());
		
		obj.put("meta", meta);
		obj.put("data", toJSONArray());
		return obj;
	}
	
	@Override
	public String toString() {
		JsonBuilder jb = new JsonBuilder();
		jb.put("class", this.getClass());
		jb.put("size", this.size());
		jb.put("data", this);
		return jb.toString();
	}
	
}
