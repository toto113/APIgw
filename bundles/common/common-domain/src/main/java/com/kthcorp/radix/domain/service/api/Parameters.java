package com.kthcorp.radix.domain.service.api;

import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.kthcorp.radix.util.JsonBuilder;

public class Parameters extends HashMap<String, ValueObject> {
	
	private static final long serialVersionUID = -8684007083697117012L;
	
	public static Parameters fromJSONArray(JSONArray obj) throws JSONException {
		Parameters r = new Parameters();
		if(obj != null) {
			if(obj.length() > 0) {
				for(int i = 0, l = obj.length(); i < l; i++) {
					JSONObject eobj = (JSONObject) obj.get(i);
					if(eobj != null) {
						ValueObject vObj = new ValueObject();
						
						if(!eobj.isNull("resource")) {
							vObj.setResource(eobj.getString("resource"));
						}
						
						if(!eobj.isNull("valueGenerator")) {
							vObj.setValueGenerator(ValueGenerator.valueOf(eobj.getString("valueGenerator")));
						}
						
						r.put(eobj.getString("name"), vObj);
					}
				}
			}
		}
		return r;
	}
	
	public String toJSONString() throws JSONException {
		JSONArray obj = new JSONArray();
		for(java.util.Map.Entry<String, ValueObject> entry : this.entrySet()) {
			JSONObject eObj = new JSONObject();
			
			// Name
			eObj.put("name", entry.getKey());
			
			// Value if it exists
			if(entry.getValue() != null) {
				eObj.put("resource", entry.getValue().getResource());
				eObj.put("valueGenerator", entry.getValue().getValueGenerator());
			}
			obj.put(eObj);
		}
		return obj.toString();
	}
	
	@Override
	public String toString() {

		JsonBuilder jb = new JsonBuilder();
		jb.put("class", this.getClass());
		for(String key : this.keySet()) {
			jb.put(key, this.get(key).toString());
		}
		return jb.toString();
		
	}
	
}
