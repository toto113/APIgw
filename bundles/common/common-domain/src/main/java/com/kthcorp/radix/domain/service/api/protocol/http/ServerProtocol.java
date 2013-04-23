package com.kthcorp.radix.domain.service.api.protocol.http;

import java.io.Serializable;

import org.json.JSONException;
import org.json.JSONObject;

import com.kthcorp.radix.domain.service.api.protocol.Protocol;
import com.kthcorp.radix.domain.service.api.protocol.ProtocolMode;
import com.kthcorp.radix.domain.service.api.protocol.ProtocolType;
import com.kthcorp.radix.util.JsonBuilder;

public class ServerProtocol implements Protocol, Serializable {
	
	private static final long serialVersionUID = 4435517403363808495L;
	
	/* Protocol */
	private ProtocolType thisProtocolType = ProtocolType.HTTP_v1_1;
	private ProtocolMode thisProtocolMode = ProtocolMode.SERVER;
	
	@Override
	public ProtocolType getProtocolType() {
		return thisProtocolType;
	}
	
	@Override
	public ProtocolMode getProtocolMode() {
		return thisProtocolMode;
	}
	
	@Override
	public void fromJSONObject(JSONObject obj) throws JSONException {
		// TODO : remove OLD code
		// 옛 스펙에는  pathTemplate이 없고,  resource와 additionalAttribute가 있었다.
		// if 코드는 나중엔 삭제되야 한다.
		this.method = HttpMethod.valueOf(obj.getString("method"));
		if(!obj.isNull("pathTemplate")) {
			this.pathTemplate = obj.getString("pathTemplate");
		}
		else if(obj.isNull("resource")) {
			this.pathTemplate = "/"+obj.getString("resource");
			if(!obj.isNull("additionalAttributes")) {
				String[] addtionalAttributes = (String[]) obj.get("additionalAttributes");
				for(String attribute : addtionalAttributes) {
					this.pathTemplate = this.pathTemplate + "/" + attribute;
				}
			}
		}

	}
	
	@Override
	public String toJSONString() throws JSONException {
		JSONObject obj = new JSONObject();
		obj.put("method", method);
		obj.put("pathTemplate", pathTemplate);
		return obj.toString();
	}
	
	/* Self Property */
	private String pathTemplate;
	private HttpMethod method;
	
	public ProtocolType getThisProtocolType() {
		return thisProtocolType;
	}
	
	public void setThisProtocolType(ProtocolType thisProtocolType) {
		this.thisProtocolType = thisProtocolType;
	}
	
	public ProtocolMode getThisProtocolMode() {
		return thisProtocolMode;
	}
	
	public void setThisProtocolMode(ProtocolMode thisProtocolMode) {
		this.thisProtocolMode = thisProtocolMode;
	}

	// 'resource'는 pathTemplate의 첫 부분에 해당한다.
	// 예를 들어 /BusLane/{userId}/location/{some} 에서 "BusLane"에 해당한다.
	public String getResource() {
		if(pathTemplate==null) { return null; }
		String onlyPathPart = pathTemplate;
		if(pathTemplate.contains("?")) {
			onlyPathPart = pathTemplate.substring(0, pathTemplate.indexOf("?"));
		}
		String[] parts = onlyPathPart.split("/");
		if(parts.length<2) { return null; }
		return parts[1];
	}
	
	
	public void setPathTemplate(String pathTemplate) {
		this.pathTemplate = pathTemplate;
	}
	
	public String getPathTemplate() {
		return this.pathTemplate;
	}
	
	public HttpMethod getMethod() {
		return method;
	}
	
	public void setMethod(HttpMethod method) {
		this.method = method;
	}
	
	@Override
	public String toString() {
		JsonBuilder jb = new JsonBuilder();
		jb.put("class", this.getClass());
		jb.put("pathTemplate", pathTemplate);
		jb.put("method", method);
		return jb.toString();
	}
	
}
