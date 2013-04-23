package com.kthcorp.radix.domain.platform.api;

import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import com.kthcorp.radix.util.JsonBuilder;

public class ResponseMessageBodyMultiMap<E, V> extends HashMap<E, V> implements ResponseMessageBody {
	
	private static final long serialVersionUID = 2858688597808533429L;
	private ResponseMessageBodyType bodyType = ResponseMessageBodyType.MULTIMAP;
	
	@Override
	public JSONObject toJSONObject() throws JSONException {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public ResponseMessageBodyType getBodyType() {
		return bodyType;
	}
	
	@Override
	public String toString() {
		return JsonBuilder.getJsonString(this);
	}
}
