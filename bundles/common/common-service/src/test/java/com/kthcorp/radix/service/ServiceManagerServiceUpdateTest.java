package com.kthcorp.radix.service;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import junit.framework.Assert;

import org.json.JSONException;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import com.kthcorp.radix.util.UuidViewableLoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import com.kthcorp.radix.util.test.junit.MysqlEmbeddableSpringJUnit4ClassRunner;

import com.kthcorp.radix.api.service.ServiceManagerService;
import com.kthcorp.radix.domain.exception.NotSupportException;
import com.kthcorp.radix.domain.exception.ValidateException;
import com.kthcorp.radix.domain.service.ResourceOwner;
import com.kthcorp.radix.domain.service.Service;
import com.kthcorp.radix.domain.service.ServiceVersion;
import com.kthcorp.radix.domain.service.YesOrNo;
import com.kthcorp.radix.domain.service.api.partnerAPI.PartnerAPI;
import com.kthcorp.radix.domain.service.api.protocol.ProtocolType;
import com.kthcorp.radix.domain.service.api.protocol.http.HttpMethod;
import com.kthcorp.radix.domain.service.api.serviceAPI.ServiceAPI;
import com.kthcorp.radix.domain.service.api.transform.TransformType;
import com.kthcorp.radix.domain.service.api.transport.TransportType;
import com.kthcorp.radix.domain.service.mapping.MappingInfo;
import com.kthcorp.radix.domain.service.mapping.MappingType;
import com.kthcorp.radix.domain.service.routing.RoutingMethod;
import com.kthcorp.radix.domain.service.routing.direct.DirectMethod;
import com.kthcorp.radix.util.FailedTest;
import com.kthcorp.radix.util.UUIDUtils;

// TODO : remove warning supression of "deprecation"
@SuppressWarnings("deprecation")
@RunWith(MysqlEmbeddableSpringJUnit4ClassRunner.class)
@ContextConfiguration(
		locations = { "classpath:/META-INF/spring/spring-application-context-test.xml", "classpath:/META-INF/spring/spring-service-context-test.xml", "classpath:/META-INF/spring/spring-dao-mysql-context.xml", "classpath:/META-INF/spring/spring-dao-mybatis-context-test.xml", "classpath:/META-INF/spring/spring-dao-hazelcast-context.xml" })
public class ServiceManagerServiceUpdateTest {
	
	private static final Logger LOG = UuidViewableLoggerFactory.getLogger(ServiceManagerServiceUpdateTest.class);
	
	@Autowired
	private ServiceManagerService serviceManagerService;
	
	String uriForFindingServiceInCache;
	String strServiceVersion = "0";
	String partnerID = "WantToBeAro";
	String resourceAuthUrl = "http://localhost/doAuth";
	String businessPlatformDomain = "api.withapi.com";
	String resource = "BusLane";
	
	String serviceName = "PublicTransport";
	String serviceAPIName = "BusLaneFront";
	String partnerAPIName = "BusLaneBackend";
	private final byte[] serviceID = UUIDUtils.getBytes("2341ed57-8772-11e1-a8c6-f0def154de37");
	private final byte[] businessPlatformID = UUIDUtils.getBytes("59ffa6f4-0901-4ffc-82ad-44687540ab4b");
	
	@Rule
	public ExpectedException exception = ExpectedException.none();
	
	@Before
	public void initDbAndCacheWithService() {
		try {
			if(!hasTestData()) {
				initTestData();
			}
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			serviceManagerService.loadAllService();
		}
	}
	
	private boolean hasTestData() {
		
		ServiceVersion serviceVersion = new ServiceVersion(strServiceVersion);
		String uri = serviceManagerService.getURIString(businessPlatformDomain, HttpMethod.GET, serviceName, serviceVersion, resource);
		DirectMethod method = (DirectMethod) serviceManagerService.getRoutingMethod(uri);
		
		return ((method == null) ? false : true);
	}
	
	private void initTestData() throws ValidateException, NotSupportException, NoSuchAlgorithmException {
		// 1. Creating Service
		Service service = new Service();
		service.generateID();
		service.setBusinessPlatformID(businessPlatformID);
		service.setName(serviceName);
		service.setPartnerID(partnerID);
		service.setResourceAuthUrl(resourceAuthUrl);
		service.setResourceOwner(ResourceOwner.USER);
		service.setVersion(strServiceVersion);
		
		// 2. Creating ServiceAPI within Service
		ServiceAPI serviceAPI = new ServiceAPI();
		
		try {
			/*
			 * Partner URI : 218.234.32.207/denny_test/appletree/v1/0/Bus/Lane/Search.asp
			 * Partner API Parameter : busNo only
			 * Map service api parameter to partner api parameter : BusNumber ==> busNo
			 */
			String mappingString = "{\"BusNumber\":[\"busNo\"]}";
			String partnerProtocolMetaInString = "{ \"uri\":\"218.234.32.207/denny_test/appletree/v1/0/Bus/Lane/Search.asp\", \"method\":\"GET\", \"content-type\":\"application/xml\", \"user\":\"kth\", \"password\":\"123456789\", \"signature-encoding\":\"SHA256(timestamp+{password}+\\\"static string\\\")\" }";
			String parametersInString = "[  {name=\"busNumber\"} ]";
			String parametersInStringForPartnerAPI = "[  {name=\"busNo\"} ]";
			String serviceProtocolMetaInString = "{\"resource\":\"BusLane\", \"method\":\"GET\"}";
			
			serviceAPI.generateID();
			serviceAPI.setName(serviceAPIName);
			serviceAPI.setTransportType(TransportType.REST);
			serviceAPI.setDefaultTransformType(TransformType.JSON);
			serviceAPI.setProtocolType(ProtocolType.HTTP_v1_1);
			serviceAPI.setProtocolMeta(serviceProtocolMetaInString);
			serviceAPI.setParameters(parametersInString);
			
			// 3. Creating PartnerAPI within Service
			PartnerAPI partnerAPI = new PartnerAPI();
			partnerAPI.generateID();
			partnerAPI.setName(partnerAPIName);
			partnerAPI.setTransportType(TransportType.REST);
			partnerAPI.setDefaultTransformType(TransformType.JSON);
			partnerAPI.setProtocolType(ProtocolType.HTTP_v1_1);
			partnerAPI.setProtocolMeta(partnerProtocolMetaInString);
			partnerAPI.setParameters(parametersInStringForPartnerAPI);
			
			// 4. Creating Mapping Info within Service
			MappingInfo parameterMap = new MappingInfo();
			parameterMap.generateID();
			parameterMap.setMappingType(MappingType.PARAMETER);
			parameterMap.setMapping(mappingString);
			
			// 5. Creating Routing Mehtod within Service
			DirectMethod directMethod = new DirectMethod();
			directMethod.generateID();
			directMethod.setServiceAPI(serviceAPI);
			directMethod.setPartnerAPI(partnerAPI);
			directMethod.setParameterMap(parameterMap);
			
			List<RoutingMethod> apiList = new ArrayList<RoutingMethod>();
			apiList.add(directMethod);
			
			service.setApiList(apiList);
			
			serviceManagerService.createService(service);
		} catch(JSONException e) {
			LOG.error(e.getMessage());
		}
	}
	
	@Test
	public void nullTest() {}
	
	@FailedTest
	public void itMustHaveOneServiceInCache() {
		ServiceVersion serviceVersion = new ServiceVersion(strServiceVersion);
		String uri = serviceManagerService.getURIString(businessPlatformDomain, HttpMethod.GET, serviceName, serviceVersion, resource);
		RoutingMethod method = serviceManagerService.getRoutingMethod(uri);
		Assert.assertNotNull(method);
	}
	
	@FailedTest
	public void modifyServiceAPI() throws JSONException, ValidateException, NotSupportException {
		ServiceVersion serviceVersion = new ServiceVersion(strServiceVersion);
		String uri = serviceManagerService.getURIString(businessPlatformDomain, HttpMethod.GET, serviceName, serviceVersion, resource);
		DirectMethod method = (DirectMethod) serviceManagerService.getRoutingMethod(uri);
		
		ServiceAPI serviceAPI = method.getServiceAPI();
		
		TransportType transportTypeBackup = serviceAPI.getTransportType();
		String parametersBackup = serviceAPI.getParameters();
		YesOrNo isValidBackup = serviceAPI.getIsValid();
		/*
		 * -Setting mutable fields to be updated
		 *   [ transportType, parameters, isDeleted, isValid ]
		 * -Immutable fields
		 *   [ ServiceAPIName ]
		 * -It except fileds to test intended
		 *   [ ProtocolType, TransformType ]
		 */
		TransportType transportTypeUpdate = TransportType.DBMS;
		String parametersUpdate = "[ {name=\"PlaneNo\"} ]";
		YesOrNo isValidUpdate = YesOrNo.N;
		
		serviceAPI.setTransportType(transportTypeUpdate);
		serviceAPI.setParameters(parametersUpdate);
		serviceAPI.setIsValid(isValidUpdate);
		
		// Updating
		serviceManagerService.modifyServiceAPI(serviceAPI);
		// Updating cache data in Hazelcast
		method.setServiceAPI(serviceAPI);
		serviceManagerService.replaceRoutingMethod(businessPlatformID, serviceID, uri, method);
		
		// Retrieve updated serviceAPI
		uri = serviceManagerService.getURIString(businessPlatformDomain, HttpMethod.GET, serviceName, serviceVersion, resource);
		method = (DirectMethod) serviceManagerService.getRoutingMethod(uri);
		ServiceAPI updatedServiceAPI = method.getServiceAPI();
		
		// Assert		
		Assert.assertEquals(transportTypeUpdate.toString(), updatedServiceAPI.getTransportType().toString());
		Assert.assertEquals(parametersUpdate, updatedServiceAPI.getParameters());
		Assert.assertEquals(isValidUpdate.toString(), updatedServiceAPI.getIsValid().toString());
		
		// Setting field from backup data		
		updatedServiceAPI.setTransportType(transportTypeBackup);
		updatedServiceAPI.setParameters(parametersBackup);
		updatedServiceAPI.setIsValid(isValidBackup);
		
		// Restore serviceAPI
		serviceManagerService.modifyServiceAPI(updatedServiceAPI);
		// Updating cache data in Hazelcast
		method.setServiceAPI(updatedServiceAPI);
		serviceManagerService.replaceRoutingMethod(businessPlatformID, serviceID, uri, method);
	}
	
	@FailedTest
	public void modifyServiceAPIFailed() throws ValidateException {
		ServiceVersion serviceVersion = new ServiceVersion(strServiceVersion);
		String uri = serviceManagerService.getURIString(businessPlatformDomain, HttpMethod.GET, serviceName, serviceVersion, resource);
		DirectMethod method = (DirectMethod) serviceManagerService.getRoutingMethod(uri);
		
		ServiceAPI serviceAPI = method.getServiceAPI();
		
		serviceAPI.setId(UUIDUtils.getBytes(UUID.randomUUID()));
		
		exception.expect(com.kthcorp.radix.domain.exception.ValidateException.class);
		
		serviceManagerService.modifyServiceAPI(serviceAPI);
	}
	
	@FailedTest
	public void modifyPartnerAPI() throws JSONException, ValidateException, NotSupportException {
		ServiceVersion serviceVersion = new ServiceVersion(strServiceVersion);
		String uri = serviceManagerService.getURIString(businessPlatformDomain, HttpMethod.GET, serviceName, serviceVersion, resource);
		DirectMethod method = (DirectMethod) serviceManagerService.getRoutingMethod(uri);
		
		PartnerAPI partnerAPI = method.getPartnerAPI();
		
		TransportType transportTypeBackup = partnerAPI.getTransportType();
		TransformType transformTypeBackup = partnerAPI.getDefaultTransformType();
		ProtocolType protocolTypeBackup = partnerAPI.getProtocolType();
		String protocolMetaBackup = partnerAPI.getProtocolMeta();
		String parametersBackup = partnerAPI.getParameters();
		YesOrNo isValidBackup = partnerAPI.getIsValid();
		/*
		 * -Setting mutable fields to be updated
		 *   [ transportType, transformType, protocolType, protocolMeta, parameters, isDeleted, isValid ]
		 * -Immutable fields
		 *   [ PartnerAPIName ]
		 */
		TransportType transportTypeUpdate = TransportType.DBMS;
		TransformType transformTypeUpdate = TransformType.XML;
		ProtocolType protocolTypeUpdate = ProtocolType.HTTP_v1_0;
		String protocolMetaUpdate = "{ \"uri\":\"218.234.32.207/zenny_test/appletree/v1/0/Bus/Lane/Search.asp\", \"method\":\"GET\", \"content-type\":\"application/xml\", \"user\":\"kth\", \"password\":\"123456789\", \"signature-encoding\":\"SHA256(timestamp+{password}+\\\"static string\\\")\" }";
		String parametersUpdate = "[ {name=\"PlaneNo\"} ]";
		YesOrNo isValidUpdate = YesOrNo.N;
		
		partnerAPI.setTransportType(transportTypeUpdate);
		partnerAPI.setDefaultTransformType(transformTypeUpdate);
		partnerAPI.setProtocolType(protocolTypeUpdate);
		partnerAPI.setProtocolMeta(protocolMetaUpdate);
		partnerAPI.setParameters(parametersUpdate);
		partnerAPI.setIsValid(isValidUpdate);
		
		// Updating
		serviceManagerService.modifyPartnerAPI(partnerAPI);
		// Updating cache data in Hazelcast
		method.setPartnerAPI(partnerAPI);
		serviceManagerService.replaceRoutingMethod(businessPlatformID, serviceID, uri, method);
		
		// Retrieve updated partnerAPI
		uri = serviceManagerService.getURIString(businessPlatformDomain, HttpMethod.GET, serviceName, serviceVersion, resource);
		// Updating cache data in Hazelcast
		method = (DirectMethod) serviceManagerService.getRoutingMethod(uri);
		PartnerAPI updatedPartnerAPI = method.getPartnerAPI();
		
		// Assert
		Assert.assertEquals(transportTypeUpdate.toString(), updatedPartnerAPI.getTransportType().toString());
		Assert.assertEquals(transformTypeUpdate.toString(), updatedPartnerAPI.getDefaultTransformType().toString());
		Assert.assertEquals(protocolTypeUpdate.toString(), updatedPartnerAPI.getProtocolType().toString());
		Assert.assertEquals(protocolMetaUpdate, updatedPartnerAPI.getProtocolMeta());
		Assert.assertEquals(parametersUpdate, updatedPartnerAPI.getParameters());
		Assert.assertEquals(isValidUpdate.toString(), updatedPartnerAPI.getIsValid().toString());
		
		// Setting field from backup data
		updatedPartnerAPI.setTransportType(transportTypeBackup);
		updatedPartnerAPI.setDefaultTransformType(transformTypeBackup);
		updatedPartnerAPI.setProtocolType(protocolTypeBackup);
		updatedPartnerAPI.setProtocolMeta(protocolMetaBackup);
		updatedPartnerAPI.setParameters(parametersBackup);
		updatedPartnerAPI.setIsValid(isValidBackup);
		
		// Restore serviceAPI
		serviceManagerService.modifyPartnerAPI(updatedPartnerAPI);
		// Updating cache data in Hazelcast		
		method.setPartnerAPI(updatedPartnerAPI);
		serviceManagerService.replaceRoutingMethod(businessPlatformID, serviceID, uri, method);
	}
	
	@FailedTest
	public void updatepartnerAPIFailed() throws ValidateException {
		ServiceVersion serviceVersion = new ServiceVersion(strServiceVersion);
		String uri = serviceManagerService.getURIString(businessPlatformDomain, HttpMethod.GET, serviceName, serviceVersion, resource);
		DirectMethod method = (DirectMethod) serviceManagerService.getRoutingMethod(uri);
		
		PartnerAPI partnerAPI = method.getPartnerAPI();
		
		partnerAPI.setId(UUIDUtils.getBytes(UUID.randomUUID()));
		
		exception.expect(com.kthcorp.radix.domain.exception.ValidateException.class);
		
		serviceManagerService.modifyPartnerAPI(partnerAPI);
	}
	
	@FailedTest
	public void updateMappingInfo() throws JSONException, ValidateException {
		ServiceVersion serviceVersion = new ServiceVersion(strServiceVersion);
		String uri = serviceManagerService.getURIString(businessPlatformDomain, HttpMethod.GET, serviceName, serviceVersion, resource);
		DirectMethod method = (DirectMethod) serviceManagerService.getRoutingMethod(uri);
		
		MappingInfo mappingInfo = method.getParameterMap();
		
		MappingType mappingTypeBackup = mappingInfo.getMappingType();
		String mappingBackup = mappingInfo.getMapping();
		YesOrNo isValidBackup = mappingInfo.getIsValid();
		/*
		 * -Setting mutable fields to be updated
		 *   [ mappintType, mapping, isDeleted, isValid ]
		 */
		MappingType mappingTypeUpdate = MappingType.RESULT;
		String mappingUpdate = "{\"BusNumber\":[\"busNambar\"]}";
		;
		YesOrNo isValidUpdate = YesOrNo.N;
		
		mappingInfo.setMappingType(mappingTypeUpdate);
		mappingInfo.setMapping(mappingUpdate);
		mappingInfo.setIsValid(isValidUpdate);
		
		serviceManagerService.modifyAPIMapping(mappingInfo);
		// Updating cache data in Hazelcast
		method.setParameterMap(mappingInfo);
		serviceManagerService.replaceRoutingMethod(businessPlatformID, serviceID, uri, method);
		
		// Retrieve updated MappingInfo
		uri = serviceManagerService.getURIString(businessPlatformDomain, HttpMethod.GET, serviceName, serviceVersion, resource);
		method = (DirectMethod) serviceManagerService.getRoutingMethod(uri);
		MappingInfo updatedMappingInfo = method.getParameterMap();
		
		// Assert
		Assert.assertEquals(mappingTypeUpdate.toString(), updatedMappingInfo.getMappingType().toString());
		Assert.assertEquals(mappingUpdate, updatedMappingInfo.getMapping());
		Assert.assertEquals(isValidUpdate.toString(), updatedMappingInfo.getIsValid().toString());
		
		// Setting field from backup data
		updatedMappingInfo.setMappingType(mappingTypeBackup);
		updatedMappingInfo.setMapping(mappingBackup);
		updatedMappingInfo.setIsValid(isValidBackup);
		
		serviceManagerService.modifyAPIMapping(updatedMappingInfo);
		// Updating cache data in Hazelcast
		method.setParameterMap(updatedMappingInfo);
		serviceManagerService.replaceRoutingMethod(businessPlatformID, serviceID, uri, method);
	}
	
	@FailedTest
	public void updateMappingInfoFailed() throws ValidateException {
		ServiceVersion serviceVersion = new ServiceVersion(strServiceVersion);
		String uri = serviceManagerService.getURIString(businessPlatformDomain, HttpMethod.GET, serviceName, serviceVersion, resource);
		DirectMethod method = (DirectMethod) serviceManagerService.getRoutingMethod(uri);
		
		MappingInfo mappingInfo = method.getParameterMap();
		
		mappingInfo.setId(UUIDUtils.getBytes(UUID.randomUUID()));
		
		exception.expect(com.kthcorp.radix.domain.exception.ValidateException.class);
		
		serviceManagerService.modifyAPIMapping(mappingInfo);
	}
	
	@FailedTest
	public void updateRoutingInfo() throws ValidateException {
		ServiceVersion serviceVersion = new ServiceVersion(strServiceVersion);
		String uri = serviceManagerService.getURIString(businessPlatformDomain, HttpMethod.GET, serviceName, serviceVersion, resource);
		DirectMethod method = (DirectMethod) serviceManagerService.getRoutingMethod(uri);
		
		YesOrNo isValidBackup = YesOrNo.Y;
		
		/*
		 * -Setting mutable fields to be updated
		 *   [ isDeleted, isValid, limitCount, limitDuration, cached, cacheCount, cacheEviction ]
		 */
		YesOrNo isValidUpdate = YesOrNo.N;
		
		method.setIsValid(isValidUpdate);
		
		serviceManagerService.modifyDirectRouting(method);
		// Updating cache data in Hazelcast
		serviceManagerService.replaceRoutingMethod(businessPlatformID, serviceID, uri, method);
		
		// Retrieve updated MappingInfo
		uri = serviceManagerService.getURIString(businessPlatformDomain, HttpMethod.GET, serviceName, serviceVersion, resource);
		DirectMethod updatedMethod = (DirectMethod) serviceManagerService.getRoutingMethod(uri);
		
		// Assert
		Assert.assertEquals(isValidUpdate.toString(), updatedMethod.getIsValid().toString());
		
		// Setting field from backup data
		updatedMethod.setIsValid(isValidBackup);
		
		serviceManagerService.modifyDirectRouting(updatedMethod);
		// Updating cache data in Hazelcast		
		serviceManagerService.replaceRoutingMethod(businessPlatformID, serviceID, uri, updatedMethod);
	}
	
	@FailedTest
	public void updateRoutingInfoFailed() throws ValidateException {
		ServiceVersion serviceVersion = new ServiceVersion(strServiceVersion);
		String uri = serviceManagerService.getURIString(businessPlatformDomain, HttpMethod.GET, serviceName, serviceVersion, resource);
		DirectMethod method = (DirectMethod) serviceManagerService.getRoutingMethod(uri);
		
		method.setId(UUIDUtils.getBytes(UUID.randomUUID()));
		
		exception.expect(com.kthcorp.radix.domain.exception.ValidateException.class);
		
		serviceManagerService.modifyDirectRouting(method);
	}
	
}
