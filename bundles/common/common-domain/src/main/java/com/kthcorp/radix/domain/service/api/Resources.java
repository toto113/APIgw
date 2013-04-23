package com.kthcorp.radix.domain.service.api;

import java.util.ArrayList;
import java.util.Collection;

import org.json.JSONArray;
import org.json.JSONException;

import com.kthcorp.radix.util.JsonBuilder;

public class Resources extends ArrayList<String> {
	
	private static final long serialVersionUID = 8618324252592519328L;
	
	public static Resources fromJSONArray(JSONArray obj) throws JSONException {
		Resources r = new Resources();
		if(obj != null) {
			if(obj.length() > 0) {
				for(int i = 0, l = obj.length(); i < l; i++) {
					r.add(obj.getString(i));
				}
			}
		}
		return r;
	}
	
	@Override
	public String toString() {
		return JsonBuilder.getJsonableString((Collection<? extends Object>)this);
	}
	
}
