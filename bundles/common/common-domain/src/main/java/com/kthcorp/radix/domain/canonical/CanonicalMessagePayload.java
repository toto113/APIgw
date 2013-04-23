package com.kthcorp.radix.domain.canonical;

import java.io.Serializable;

import com.kthcorp.radix.domain.canonical.reply.AdaptorReply;
import com.kthcorp.radix.domain.canonical.reply.OrchestratorReply;
import com.kthcorp.radix.domain.canonical.request.OrchestratorRequest;
import com.kthcorp.radix.domain.canonical.request.ServiceControllerRequest;
import com.kthcorp.radix.util.JsonBuilder;

public class CanonicalMessagePayload implements Serializable {

	private static final long serialVersionUID = 1313092566093573275L;
	
	private ServiceControllerRequest request;
	private OrchestratorRequest orchestratorRequest;
	private AdaptorReply adaptorReply;
	private OrchestratorReply reply;
	
	public ServiceControllerRequest getRequest() {
		return request;
	}
	
	public void setRequest(ServiceControllerRequest request) {
		this.request = request;
	}

	public OrchestratorRequest getOrchestratorRequest() {
		return orchestratorRequest;
	}
	
	public void setOrchestratorRequest(OrchestratorRequest orchestratorRequest) {
		this.orchestratorRequest = orchestratorRequest;
	}

	public AdaptorReply getAdaptorReply() {
		return adaptorReply;
	}

	
	public void setAdaptorReply(AdaptorReply adaptorReply) {
		this.adaptorReply = adaptorReply;
	}

	
	public OrchestratorReply getReply() {
		return reply;
	}

	
	public void setReply(OrchestratorReply reply) {
		this.reply = reply;
	}
	
	@Override
	public String toString() {
		JsonBuilder jb = new JsonBuilder();
		jb.put("class", this.getClass());
		jb.put("request", request);
		jb.put("orchestratorRequest", orchestratorRequest);
		jb.put("adaptorReply", adaptorReply);
		jb.put("reply", reply);
		return jb.toString();
	}
	
}
