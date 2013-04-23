package com.kthcorp.radix.domain.stat;

import java.text.SimpleDateFormat;
import java.util.Date;

public class RadixTransactionLog {
	
	public RadixTransactionLog(String transactionId) {
		this.transactionId = transactionId;
	}
	
	private String transactionId;
	private String hostName;
	private String ip;
	private String clientIp;
	private String clientKey;
	private String apiName;
	private String pathTemplate;
	private String requestUri;
	private String method;
	private String statusCode;
	private String errorCode;
	private Date requestTime;
	private long responseTime;
//	private String sectionTime;
	private long responseBodyByteLength;
	private String userAgent;
	private String os;
	private String osVersion;
	private String terminal;
	private String referer;
	
	private long controllerTime;
	private long controllerBeginTime;
	private long orchestratorTime;
	private long orchestratorBeginTime;
	private long adaptorTime;
	private long adaptorBeginTime;
	private long partnerTime;
	private long partnerBeginTime;
	
	public String getTransactionId() {
		return transactionId;
	}
	
	public String getHostName() {
		return hostName;
	}
	
	public void setHostName(String hostName) {
		this.hostName = hostName;
	}
	
	public String getIp() {
		return ip;
	}
	
	public void setIp(String ip) {
		this.ip = ip;
	}
	
	public String getClientIp() {
		return clientIp;
	}
	
	public void setClientIp(String clientIp) {
		this.clientIp = clientIp;
	}
	
	public String getClientKey() {
		return clientKey;
	}
	
	public void setClientKey(String clientKey) {
		this.clientKey = clientKey;
	}
	
	public String getApiName() {
		return apiName;
	}
	
	public void setApiName(String apiName) {
		this.apiName = apiName;
	}
	
	public String getPathTemplate() {
		return pathTemplate;
	}
	
	public void setPathTemplate(String pathTemplate) {
		this.pathTemplate = pathTemplate;
	}
	
	public String getRequestUri() {
		return requestUri;
	}
	
	public void setRequestUri(String requestUri) {
		this.requestUri = requestUri;
	}
	
	public String getMethod() {
		return method;
	}
	
	public void setMethod(String method) {
		this.method = method;
	}
	
	public String getStatusCode() {
		return statusCode;
	}
	
	public void setStatusCode(String statusCode) {
		this.statusCode = statusCode;
	}
	
	public String getErrorCode() {
		return errorCode;
	}
	
	public void setErrorCode(String errorCode) {
		if(errorCode==null) { errorCode=""; }
		errorCode = errorCode.replaceAll("\n", " ");
		this.errorCode = errorCode;
	}
	
	public Date getRequestTime() {
		return requestTime;
	}
	
	public void setRequestTime(Date requestTime) {
		this.requestTime = requestTime;
	}
	
	public long getResponseTime() {
		return responseTime;
	}
	
	public void setResponseTime(long responseTime) {
		this.responseTime = responseTime - requestTime.getTime();		
	}
	
	public long getResponseBodyByteLength() {
		return responseBodyByteLength;
	}
	
	public void setResponseBodyByteLength(long responseBodyByteLength) {
		this.responseBodyByteLength = responseBodyByteLength;
	}
	
	public String getUserAgent() {
		return userAgent;
	}
	
	public void setUserAgent(String userAgent) {
		this.userAgent = userAgent;
	}
	
	public String getOs() {
		return os;
	}
	
	public void setOs(String os) {
		this.os = os;
	}
	
	public String getOsVersion() {
		return osVersion;
	}
	
	public void setOsVersion(String osVersion) {
		this.osVersion = osVersion;
	}
	
	public String getTerminal() {
		return terminal;
	}
	
	public void setTerminal(String terminal) {
		this.terminal = terminal;
	}
	
	public String getReferer() {
		return referer;
	}
	
	public void setReferer(String referer) {
		this.referer = referer;
	}
	
	public long getControllerTime() {
		return controllerTime;
	}

	public void setControllerTime(long controllerTime) {
		this.controllerTime = controllerTime - this.controllerBeginTime;
	}
	
	public long getControllerBeginTime() {
		return controllerBeginTime;
	}
	
	public void setControllerBeginTime(long controllerBeginTime) {
		this.controllerBeginTime = controllerBeginTime;
	}

	public long getOrchestratorTime() {
		return orchestratorTime;
	}

	public void setOrchestratorTime(long orchestratorTime) {
		this.orchestratorTime = orchestratorTime - this.orchestratorBeginTime;
	}

	public long getOrchestratorBeginTime() {
		return orchestratorBeginTime;
	}

	public void setOrchestratorBeginTime(long orchestratorBeginTime) {
		this.orchestratorBeginTime = orchestratorBeginTime;
	}
	
	public long getAdaptorTime() {
		return adaptorTime;
	}

	public void setAdaptorTime(long adaptorTime) {
		this.adaptorTime = adaptorTime - this.adaptorBeginTime;
	}

	public long getAdaptorBeginTime() {
		return adaptorBeginTime;
	}

	public void setAdaptorBeginTime(long adaptorBeginTime) {
		this.adaptorBeginTime = adaptorBeginTime;
	}

	public long getPartnerTime() {
		return partnerTime;
	}

	public void setPartnerTime(long partnerTime) {
		this.partnerTime = partnerTime - this.partnerBeginTime;
	}

	public long getPartnerBeginTime() {
		return partnerBeginTime;
	}

	public void setPartnerBeginTime(long partnerBeginTime) {
		this.partnerBeginTime = partnerBeginTime;
	}
	
	public String getSectionTime() {
		
		final String delimeter = "|";
		StringBuilder sb = new StringBuilder();
		sb.append("CONTROLLER:").append(controllerTime - orchestratorTime).append(delimeter);
		sb.append("ORCHESTRATOR:").append(orchestratorTime - adaptorTime).append(delimeter);
		sb.append("ADAPTOR:").append(adaptorTime - partnerTime).append(delimeter);
		sb.append("PARTNER:").append(partnerTime);
		return sb.toString();
	}
	
	public String getFormatted() {
		
		final String delimeter = "\t";
		StringBuilder sb = new StringBuilder();
		sb = sb.append(transactionId).append(delimeter);
		sb = sb.append(hostName).append(delimeter);
		sb = sb.append(ip).append(delimeter);
		sb = sb.append(clientIp).append(delimeter);
		sb = sb.append(clientKey).append(delimeter);
		sb = sb.append(apiName).append(delimeter);
		sb = sb.append(requestUri).append(delimeter);
		sb = sb.append(method).append(delimeter);
		sb = sb.append(statusCode).append(delimeter);
		// errorCode에 delimeter가 포함되어 있을 수 있다. 이러면 파싱에 실패하겠지. 
		sb = sb.append(errorCode==null? "" : errorCode.replaceAll(delimeter, " ")).append(delimeter);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		sb = sb.append(sdf.format(requestTime)).append(delimeter);
		sb = sb.append(responseTime).append(delimeter);
		sb = sb.append(getSectionTime()).append(delimeter);
		sb = sb.append(responseBodyByteLength).append(delimeter);
		sb = sb.append(userAgent == null ? "" : userAgent).append(delimeter);
		sb = sb.append(os == null ? "" : userAgent).append(delimeter);
		sb = sb.append(osVersion == null ? "" : userAgent).append(delimeter);
		sb = sb.append(terminal == null ? "" : userAgent).append(delimeter);
		sb = sb.append(referer == null ? "" : userAgent).append(delimeter);
		sb = sb.append(pathTemplate == null ? "" : pathTemplate).append(delimeter);
		sb = sb.append(delimeter).append(delimeter);
		
		return sb.toString();
	}

}
