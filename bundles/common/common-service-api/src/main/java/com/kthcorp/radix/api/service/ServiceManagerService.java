package com.kthcorp.radix.api.service;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import javax.xml.xpath.XPathExpressionException;

import org.json.JSONException;
import org.w3c.dom.DOMException;
import org.xml.sax.SAXException;

import com.kthcorp.radix.domain.exception.NotSupportException;
import com.kthcorp.radix.domain.exception.ValidateException;
import com.kthcorp.radix.domain.platform.api.RequestMessageType;
import com.kthcorp.radix.domain.service.Service;
import com.kthcorp.radix.domain.service.ServiceVersion;
import com.kthcorp.radix.domain.service.api.partnerAPI.PartnerAPI;
import com.kthcorp.radix.domain.service.api.protocol.http.HttpMethod;
import com.kthcorp.radix.domain.service.api.serviceAPI.ServiceAPI;
import com.kthcorp.radix.domain.service.mapping.MappingInfo;
import com.kthcorp.radix.domain.service.routing.RoutingMethod;
import com.kthcorp.radix.domain.service.routing.direct.DirectMethod;

public interface ServiceManagerService {
	
	/* Load Method (store to cache DAO) */
	public void loadAllService();
	
	public void loadAllService(byte[] businessPlatformID);
	
	public void loadService(byte[] serviceID);
	
	public Service getServiceInstanceFromServiceDescription(String serviceDescription, RequestMessageType type) throws XPathExpressionException, JSONException, SAXException, IOException, ValidateException, NotSupportException, DOMException;
	
	public List<Service> getServiceListWithServiceInfo(byte[] businessPlatformID, String partnerID) throws ValidateException;
	
	public Service getServiceWithServiceInfo (byte[] businessPlatformID, String partnerID, byte[] serviceID) throws ValidateException;
	
	/* create or create service description to persistent layer */
	public byte[] createService(Service service) throws ValidateException, NoSuchAlgorithmException;
	
	public byte[] createServiceOnly(Service service) throws ValidateException, NoSuchAlgorithmException;
	
	public byte[] createServiceOnly(Service service, boolean checkIfExists) throws ValidateException, NoSuchAlgorithmException;
	
	public void createServiceAPI(ServiceAPI serviceAPI) throws ValidateException, NoSuchAlgorithmException;
	
	public void createPartnerAPI(PartnerAPI partnerAPI) throws ValidateException, NoSuchAlgorithmException;
	
	public void createAPIMapping(MappingInfo mappingInfo) throws ValidateException, NoSuchAlgorithmException;
	
	public void createDirectRouting(DirectMethod directMethod) throws ValidateException, NoSuchAlgorithmException;
	
	/* remove service description from persistent layer */
	public boolean removeService(Service service) throws ValidateException;
	
	/* get from cached DAO */
	@Deprecated
	// 하나의 key에 복수의 RoutingMethod가 올수 있다.
	// getRoutingMethods()를 사용하자.
	public RoutingMethod getRoutingMethod(String key);
	
	public List<RoutingMethod> getRoutingMethods(String key);
	
	/* Tools */
	public String getURIString(String businessPlatformDomain, HttpMethod method, String serviceName, ServiceVersion serviceVersion, String resource);
	
	public void validateService(Service service) throws ValidateException;
	
	public void validateService(Service service, boolean checkIfExists) throws ValidateException;
	
	/* modify service object */
	public void modifyService(Service service) throws ValidateException;
	
	public void modifyServiceAPI(ServiceAPI serviceAPI) throws ValidateException;
	
	public void modifyPartnerAPI(PartnerAPI partnerAPI) throws ValidateException;
	
	public void modifyAPIMapping(MappingInfo mappingInfo) throws ValidateException;
	
	public void modifyDirectRouting(DirectMethod directMethod) throws ValidateException;
	
	public void replaceRoutingMethods(byte[] businessPlatformID, byte[] serviceID, String objectKey, List<RoutingMethod> routingMethods);
	
	@Deprecated
	// 기존 테스트 코드를 위해 남겨 둔다. 왠만하면 테스트 코드를 수저하자.
	public void replaceRoutingMethod(byte[] businessPlatformID, byte[] serviceID, String objectKey, RoutingMethod routingMethod);
}
