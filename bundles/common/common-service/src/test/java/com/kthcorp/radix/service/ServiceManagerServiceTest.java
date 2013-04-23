package com.kthcorp.radix.service;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.xpath.XPathExpressionException;

import junit.framework.Assert;

import org.json.JSONException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import com.kthcorp.radix.util.UuidViewableLoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import com.kthcorp.radix.util.test.junit.MysqlEmbeddableSpringJUnit4ClassRunner;
import org.w3c.dom.DOMException;
import org.xml.sax.SAXException;

import com.kthcorp.radix.api.service.BusinessPlatformKeyManagerService;
import com.kthcorp.radix.api.service.ServiceManagerService;
import com.kthcorp.radix.domain.exception.NotSupportException;
import com.kthcorp.radix.domain.exception.ValidateException;
import com.kthcorp.radix.domain.platform.api.RequestMessageType;
import com.kthcorp.radix.domain.service.ResourceOwner;
import com.kthcorp.radix.domain.service.Service;
import com.kthcorp.radix.domain.service.ServiceVersion;
import com.kthcorp.radix.domain.service.api.partnerAPI.PartnerAPI;
import com.kthcorp.radix.domain.service.api.protocol.ProtocolType;
import com.kthcorp.radix.domain.service.api.protocol.http.HttpMethod;
import com.kthcorp.radix.domain.service.api.serviceAPI.ServiceAPI;
import com.kthcorp.radix.domain.service.api.transform.TransformType;
import com.kthcorp.radix.domain.service.api.transport.TransportType;
import com.kthcorp.radix.domain.service.mapping.MappingInfo;
import com.kthcorp.radix.domain.service.mapping.MappingType;
import com.kthcorp.radix.domain.service.routing.RoutingMethod;
import com.kthcorp.radix.domain.service.routing.RoutingMethodType;
import com.kthcorp.radix.domain.service.routing.direct.DirectMethod;
import com.kthcorp.radix.util.UUIDUtils;

@RunWith(MysqlEmbeddableSpringJUnit4ClassRunner.class)
@ContextConfiguration(
		locations = { "classpath:/META-INF/spring/spring-application-context-test.xml", "classpath:/META-INF/spring/spring-service-context-test.xml", "classpath:/META-INF/spring/spring-dao-hazelcast-context.xml", "classpath:/META-INF/spring/spring-dao-mysql-context.xml", "classpath:/META-INF/spring/spring-dao-mybatis-context-test.xml" })
public class ServiceManagerServiceTest {
	
	private static final Logger LOG = UuidViewableLoggerFactory.getLogger(ServiceManagerServiceTest.class);
	
	@Autowired
	private ServiceManagerService serviceManagerService;
	
	@Autowired
	private BusinessPlatformKeyManagerService businessPlatformKeyManagerService;
	
	private final byte[] businessPlatformID = UUIDUtils.getBytes("59ffa6f4-0901-4ffc-82ad-44687540ab4b");
	String businessPlatformDomain = "api.withapi.com";
	
	@Test
	public void saveAndRemoveService() throws NotSupportException, NoSuchAlgorithmException {
		
		Service service = new Service();
		service.generateID();
		service.setBusinessPlatformID(businessPlatformID);
		service.setName("service" + System.currentTimeMillis());
		service.setPartnerID("partner1");
		service.setResourceAuthUrl("http://auth.container.com/doAuth");
		service.setResourceOwner(ResourceOwner.USER);
		service.setVersion(new ServiceVersion("999"));
		
		ServiceAPI serviceAPI = new ServiceAPI();
		try {
			String mappingString = "{\"name1\":[\"name2\"],\"name.lastname\":[\"name.name2\"]}";
			
			String partnerProtocolMetaInString = "{ \"uri\":\"api.koreatour.com:80/tourinfo/hotel.jsp\", \"method\":\"GET\", \"content-type\":\"application/xml\", \"user\":\"kth\", \"password\":\"123456789\", \"signature-encoding\":\"SHA256(timestamp+{password}+\\\"static string\\\")\" }";
			String parametersInString = "[  {name=\"region\"},  {name=\"hotelId\"},  {name=\"reference\",valueGenerator=\"SCRIPT\",resource=\"random.string(16)\"},  {name=\"reference\",valueGenerator=\"LITERAL\",resource=\"value1\"} ]";
			String serviceProtocolMetaInString = "{  \"pathTemplate\":\"/busLane/{attr1}/{attr2}\",  \"method\":\"GET\" }";
			
			serviceAPI.generateID();
			serviceAPI.setName("testS" + System.currentTimeMillis());
			serviceAPI.setTransportType(TransportType.REST);
			serviceAPI.setDefaultTransformType(TransformType.JSON);
			serviceAPI.setProtocolType(ProtocolType.HTTP_v1_1);
			serviceAPI.setProtocolMeta(serviceProtocolMetaInString);
			serviceAPI.setParameters(parametersInString);
			
			PartnerAPI partnerAPI = new PartnerAPI();
			partnerAPI.generateID();
			partnerAPI.setName("testP" + System.currentTimeMillis());
			partnerAPI.setTransportType(TransportType.REST);
			partnerAPI.setDefaultTransformType(TransformType.JSON);
			partnerAPI.setProtocolType(ProtocolType.HTTP_v1_1);
			partnerAPI.setProtocolMeta(partnerProtocolMetaInString);
			partnerAPI.setParameters(parametersInString);
			
			MappingInfo parameterMap = new MappingInfo();
			parameterMap.generateID();
			parameterMap.setMappingType(MappingType.PARAMETER);
			parameterMap.setMapping(mappingString);
			
			DirectMethod directMethod = new DirectMethod();
			directMethod.generateID();
			directMethod.setServiceAPI(serviceAPI);
			directMethod.setPartnerAPI(partnerAPI);
			directMethod.setParameterMap(parameterMap);
			
			List<RoutingMethod> apiList = new ArrayList<RoutingMethod>();
			apiList.add(directMethod);
			
			service.setApiList(apiList);
			
			serviceManagerService.createService(service);
			serviceManagerService.removeService(service);
		} catch(JSONException e) {
			LOG.error(e.getMessage());
		} catch(ValidateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void loadEntireService(String serviceName) {
		serviceManagerService.loadAllService();
		
		String resource = "buslane";
		ServiceVersion serviceVersion = new ServiceVersion("1");
		
		String uri = serviceManagerService.getURIString(businessPlatformDomain, HttpMethod.GET, serviceName, serviceVersion, resource);
		
		LOG.debug("Get CachedObject, Key->" + uri);
		@SuppressWarnings("deprecation")
		RoutingMethod method = serviceManagerService.getRoutingMethod(uri);
		if(method != null) {
			if(RoutingMethodType.DIRECT == method.getRoutingMethodType()) {
				DirectMethod directMethod = (DirectMethod) method;
				LOG.debug("Id->" + directMethod.getId() + ", serviceAPIID->" + directMethod.getServiceAPIID() + ", partnerAPIID->" + directMethod.getPartnerAPIID());
			}
		}
	}
	
	//@Test
	public void doAddService() throws XPathExpressionException, JSONException, SAXException, IOException, ValidateException, NotSupportException, DOMException, NoSuchAlgorithmException {
		@SuppressWarnings("unused")
		String businessPlatformKey = "59ffa6f4-0901-4ffc-82ad-44687540ab4b";
		String serviceDescription = ServiceDescriptionXmlParserTest.readServiceDescriptionTestXml("ServiceDescriptionTest.xml");
		
		Service service = this.serviceManagerService.getServiceInstanceFromServiceDescription(serviceDescription, RequestMessageType.APPLICATION_XML);
		service.setBusinessPlatformID(businessPlatformID);
		service.setPartnerID("aro1");
		
		//// modify 'name' with adding current timestamp to avoid duplicate 'name' when repeating AddService tests.
		////
		//service.setName( service.getName() + "_" + (new Date()).getTime() ) ;
		
		LOG.debug("businessPlatformID->" + businessPlatformID + ",name->" + service.getName() + ",partnerID->" + service.getPartnerID());

		this.serviceManagerService.validateService(service, true);
		
		byte[] serviceId = this.serviceManagerService.createService(service);
		
		Assert.assertNotNull(serviceId);
		
		LOG.debug("serviceId=" + serviceId);
		
	}
	
	// @Test
	public void doRemoveService() throws ValidateException {
		// String serviceDescription = ServiceDescriptionXmlParserTest.readServiceDescriptionTestXml() ;
		// Service service = this.serviceManagerService.getServiceInstanceFromServiceDescription(serviceDescription, RequestMessageType.APPLICATION_XML ) ;
		
		// String businessPlatformKey = "59ffa6f4-0901-4ffc-82ad-44687540ab4b";
		// Integer businessPlatformID = this.businessPlatformKeyManagerService.getBusinessPlatformID(businessPlatformKey);
		
		String partnerID = "aro1";
		
		List<Service> serviceList = this.serviceManagerService.getServiceListWithServiceInfo(businessPlatformID, partnerID);
		
		if(serviceList != null) {
			for(int i = 0; i < serviceList.size(); i++) {
				Service service = serviceList.get(i);
				
				LOG.debug("id=" + service.getId());
				
				this.serviceManagerService.removeService(service);
			}
		}
	}
	
	public BusinessPlatformKeyManagerService getBusinessPlatformKeyManagerService() {
		return businessPlatformKeyManagerService;
	}
	
	public void setBusinessPlatformKeyManagerService(BusinessPlatformKeyManagerService businessPlatformKeyManagerService) {
		this.businessPlatformKeyManagerService = businessPlatformKeyManagerService;
	}
}
