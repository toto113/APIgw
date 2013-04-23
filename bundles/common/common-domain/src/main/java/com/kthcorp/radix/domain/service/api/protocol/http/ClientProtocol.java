package com.kthcorp.radix.domain.service.api.protocol.http;

import java.io.Serializable;

import org.json.JSONException;
import org.json.JSONObject;

import com.kthcorp.radix.domain.service.api.protocol.Protocol;
import com.kthcorp.radix.domain.service.api.protocol.ProtocolMode;
import com.kthcorp.radix.domain.service.api.protocol.ProtocolType;
import com.kthcorp.radix.util.JsonBuilder;

public class ClientProtocol implements Protocol, Serializable {
	
	private static final long serialVersionUID = 6240259705278642342L;
	
	/* Protocol */
	private ProtocolType thisProtocolType = ProtocolType.HTTP_v1_1;
	private ProtocolMode thisProtocolMode = ProtocolMode.CLIENT;
	
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
		this.uri = obj.getString("uri");
		this.method = HttpMethod.valueOf(obj.getString("method"));
		this.contentType = obj.getString("content-type");
		if(!obj.isNull("user")) {
			this.user = obj.getString("user");
		}
		if(!obj.isNull("password")) {
			this.password = obj.getString("password");
		}
		if(!obj.isNull("signature-encoding")) {
			this.signatureEncoding = obj.getString("signature-encoding");
		}
	}
	
	@Override
	public String toJSONString() throws JSONException {
		JSONObject obj = new JSONObject();
		obj.put("uri", uri);
		obj.put("method", method);
		obj.put("content-type", contentType);
		if(user != null) {
			obj.put("user", user);
		}
		if(password != null) {
			obj.put("password", password);
		}
		if(signatureEncoding != null) {
			obj.put("signature-encoding", signatureEncoding);
		}
		return obj.toString();
	}
	
	/* Self Property */
	private String uri;
	private HttpMethod method;
	
	private String contentType;
	
	/* Authentication */
	private String user;
	private String password;
	private String signatureEncoding;
	
	public ProtocolType getThisProtocolType() {
		return thisProtocolType;
	}
	
	public void setThisProtocolType(ProtocolType thisProtocolType) {
		this.thisProtocolType = thisProtocolType;
	}
	
	public String getUri() {
		return uri;
	}
	
	public void setUri(String uri) {
		this.uri = uri;
	}
	
	public HttpMethod getMethod() {
		return method;
	}
	
	public void setMethod(HttpMethod method) {
		this.method = method;
	}
	
	public String getContentType() {
		return contentType;
	}
	
	public void setContentType(String contentType) {
		this.contentType = contentType;
	}
	
	public String getUser() {
		return user;
	}
	
	public void setUser(String user) {
		this.user = user;
	}
	
	public String getPassword() {
		return password;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}
	
	public String getSignatureEncoding() {
		return signatureEncoding;
	}
	
	public void setSignatureEncoding(String signatureEncoding) {
		this.signatureEncoding = signatureEncoding;
	}
	
	@Override
	public String toString() {
		JsonBuilder jb = new JsonBuilder();
		jb.put("class", this.getClass());
		jb.put("uri", uri);
		jb.put("method", method);
		jb.put("contentType", contentType);
		jb.put("user", user);
		jb.put("password", password);
		jb.put("signatureEncoding", signatureEncoding);
		return jb.toString();
	}
	
}
