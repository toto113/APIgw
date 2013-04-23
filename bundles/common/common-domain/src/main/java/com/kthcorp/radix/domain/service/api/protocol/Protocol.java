package com.kthcorp.radix.domain.service.api.protocol;

import org.json.JSONException;
import org.json.JSONObject;

public interface Protocol {
	
	public ProtocolMode getProtocolMode();
	
	public ProtocolType getProtocolType();
	
	public void fromJSONObject(JSONObject obj) throws JSONException;
	
	public String toJSONString() throws JSONException;
	
}
