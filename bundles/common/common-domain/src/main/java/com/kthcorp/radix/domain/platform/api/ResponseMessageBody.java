package com.kthcorp.radix.domain.platform.api;

import org.json.JSONException;
import org.json.JSONObject;

public interface ResponseMessageBody {
	
	public JSONObject toJSONObject() throws JSONException;
	
	public ResponseMessageBodyType getBodyType();
}
