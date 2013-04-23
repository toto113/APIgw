package com.kthcorp.radix.dao.mybatis;

import java.security.NoSuchAlgorithmException;
import java.util.List;

import junit.framework.Assert;

import org.json.JSONException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import com.kthcorp.radix.util.test.junit.MysqlEmbeddableSpringJUnit4ClassRunner;

import com.kthcorp.radix.domain.exception.NotSupportException;
import com.kthcorp.radix.domain.service.ResourceOwner;
import com.kthcorp.radix.domain.service.Service;
import com.kthcorp.radix.domain.service.YesOrNo;
import com.kthcorp.radix.domain.service.api.partnerAPI.PartnerAPI;
import com.kthcorp.radix.domain.service.api.protocol.ProtocolType;
import com.kthcorp.radix.domain.service.api.serviceAPI.ServiceAPI;
import com.kthcorp.radix.domain.service.api.transform.TransformType;
import com.kthcorp.radix.domain.service.api.transport.TransportType;
import com.kthcorp.radix.domain.service.mapping.MappingInfo;
import com.kthcorp.radix.domain.service.mapping.MappingType;
import com.kthcorp.radix.domain.service.routing.direct.DirectMethod;
import com.kthcorp.radix.util.UUIDUtils;

@RunWith(MysqlEmbeddableSpringJUnit4ClassRunner.class)
@ContextConfiguration(
		locations = { "classpath:/META-INF/spring/spring-application-context-test.xml", "classpath:/META-INF/spring/spring-dao-mysql-context.xml", "classpath:/META-INF/spring/spring-dao-mybatis-context-test.xml" })
public class MybatisServiceManagerDaoTest extends AbstractJUnit4SpringContextTests {
	
	@Autowired
	private MyBatisServiceManagerDaoMapper myBatisServiceAPIManagerDao;
	
	@Test
	public void directRoutingTest() throws JSONException, NotSupportException, NoSuchAlgorithmException {
		
		String businessPlatformKey = "59ffa6f4-0901-4ffc-82ad-44687540ab4b";
		byte[] bpInBytes = UUIDUtils.getBytes(businessPlatformKey);
		
		Service service = new Service();
		service.generateID();
		service.setIsValid(YesOrNo.N);
		service.setName("testService"+System.currentTimeMillis());
		service.setPartnerID("partner1");
		service.setResourceAuthUrl("http://auth.container.com/doAuth");
		service.setResourceOwner(ResourceOwner.USER);
		service.setVersion("1");
		
		// Get Business Platform Information
		service.setBusinessPlatformID(bpInBytes);
		
		// Here we go
		
		// Get List
		byte[] lastId = null;

		// Create
		int createRet = myBatisServiceAPIManagerDao.insertService(service);
		Assert.assertEquals(createRet, 1);
			
		List<Service> retList = myBatisServiceAPIManagerDao.selectServiceListWithServiceInfo(service.getBusinessPlatformID(), service.getPartnerID());
			
		for(Service s : retList) {
			// Get Each Item
			Service obj = myBatisServiceAPIManagerDao.selectService(s.getId());
			Assert.assertTrue(obj != null);
			lastId = s.getId();
		}
		
		String mappingString = "{\"name1\":[\"name2\"],\"name.lastname\":[\"name.name2\"]}";
		
		String partnerProtocolMetaInString = "{ \"uri\":\"api.koreatour.com:80/tourinfo/hotel.jsp\", \"method\":\"GET\", \"content-type\":\"application/xml\", \"user\":\"kth\", \"password\":\"123456789\", \"signature-encoding\":\"SHA256(timestamp+{password}+\\\"static string\\\")\" }";
		String parametersInString = "[  {name=\"region\"},  {name=\"hotelId\"},  {name=\"reference\",valueGenerator=\"SCRIPT\",resource=\"random.string(16)\"},  {name=\"reference\",valueGenerator=\"LITERAL\",resource=\"value1\"} ]";
		String serviceProtocolMetaInString = "{  \"pathTemplate\":\"/busLane/{attr1}/{attr2}\", \"method\":\"GET\" }";
		
		ServiceAPI serviceAPI = new ServiceAPI();
		serviceAPI.generateID();
		serviceAPI.setName("testServiceAPI"+System.currentTimeMillis());
		serviceAPI.setServiceID(lastId);
		serviceAPI.setTransportType(TransportType.REST);
		serviceAPI.setProtocolType(ProtocolType.HTTP_v1_1);
		serviceAPI.setProtocolMeta(serviceProtocolMetaInString);
		serviceAPI.setParameters(parametersInString);
		
		// Create
		int createSRet = myBatisServiceAPIManagerDao.insertServiceAPI(serviceAPI);
		Assert.assertEquals(createSRet, 1);
		
		// Read
		ServiceAPI serviceAPIRetrieved = myBatisServiceAPIManagerDao.selectServiceAPIWithServiceInfo(serviceAPI.getServiceID(), serviceAPI.getName());
		Assert.assertTrue(serviceAPIRetrieved != null);
		Assert.assertTrue(serviceAPIRetrieved.getId() != null);
		Assert.assertEquals(serviceAPIRetrieved.getTransportType(), TransportType.REST);
		byte[] serviceAPIID = serviceAPIRetrieved.getId();
		
		PartnerAPI partnerAPI = new PartnerAPI();
		partnerAPI.generateID();
		partnerAPI.setName("testPartnerAPI"+System.currentTimeMillis());
		partnerAPI.setServiceID(lastId);
		partnerAPI.setTransportType(TransportType.REST);
		partnerAPI.setDefaultTransformType(TransformType.JSON);
		partnerAPI.setProtocolType(ProtocolType.HTTP_v1_1);
		partnerAPI.setProtocolMeta(partnerProtocolMetaInString);
		partnerAPI.setParameters(parametersInString);
		
		// Create
		int createPRet = myBatisServiceAPIManagerDao.insertPartnerAPI(partnerAPI);
		Assert.assertEquals(createPRet, 1);
		
		// Read
		PartnerAPI partnerAPIRetrieved = myBatisServiceAPIManagerDao.selectPartnerAPIWithServiceInfo(partnerAPI.getServiceID(), partnerAPI.getName());
		Assert.assertTrue(partnerAPIRetrieved != null);
		Assert.assertTrue(partnerAPIRetrieved.getId() != null);
		Assert.assertEquals(partnerAPIRetrieved.getTransportType(), TransportType.REST);
		Assert.assertEquals(partnerAPIRetrieved.getDefaultTransformType(), TransformType.JSON);
		byte[] partnerAPIID = partnerAPIRetrieved.getId();
		
		MappingInfo mappingInfo = new MappingInfo();
		mappingInfo.generateID();
		mappingInfo.setServiceID(lastId);
		mappingInfo.setServiceAPIID(serviceAPIID);
		mappingInfo.setPartnerAPIID(partnerAPIID);
		mappingInfo.setMappingType(MappingType.PARAMETER);
		mappingInfo.setMapping(mappingString);
		
		/* Mapping */
		
		// Create
		int createMRet = myBatisServiceAPIManagerDao.insertAPIMapping(mappingInfo);
		Assert.assertEquals(createMRet, 1);
		
		// Read
		MappingInfo mappingInfoRetrieved = myBatisServiceAPIManagerDao.selectAPIMappingWithAPIInfo(mappingInfo.getServiceID(), mappingInfo.getServiceAPIID(), mappingInfo.getPartnerAPIID(), mappingInfo.getMappingType());
		Assert.assertTrue(mappingInfoRetrieved != null);
		Assert.assertTrue(mappingInfoRetrieved.getId() != null);
		Assert.assertEquals(mappingInfoRetrieved.getMappingType(), MappingType.PARAMETER);
		byte[] parameterMappingID = mappingInfoRetrieved.getId();
		
		/* Direct Routing ---------------------------------------------- */
		DirectMethod directMethod = new DirectMethod();
		directMethod.generateID();
		directMethod.setServiceID(lastId);
		directMethod.setServiceAPIID(serviceAPIID);
		directMethod.setPartnerAPIID(partnerAPIID);
		directMethod.setParameterMappingID(parameterMappingID);
		
		int createRet2 = myBatisServiceAPIManagerDao.insertDirectRouting(directMethod);
		Assert.assertEquals(createRet2, 1);
		
		DirectMethod directMethodRetrieved = myBatisServiceAPIManagerDao.selectDirectRoutingWithAPIInfo2(directMethod.getServiceID(), directMethod.getServiceAPIID(), directMethod.getPartnerAPIID());
		Assert.assertTrue(directMethodRetrieved != null);
		
		int removeRet = myBatisServiceAPIManagerDao.deleteDirectRoutingWithAPIInfo(directMethod.getServiceID(), directMethod.getServiceAPIID(), directMethod.getPartnerAPIID());
		Assert.assertEquals(removeRet, 1);
		
		/* Direct Routing ---------------------------------------------- */
		
		// Remove
		int removeMRet = myBatisServiceAPIManagerDao.deleteAPIMappingWithID(mappingInfoRetrieved.getId());
		Assert.assertEquals(removeMRet, 1);
		
		int removeSRet = myBatisServiceAPIManagerDao.deleteServiceAPI(serviceAPIID);
		Assert.assertEquals(removeSRet, 1);
		
		int removePRet = myBatisServiceAPIManagerDao.deletePartnerAPI(partnerAPIID);
		Assert.assertEquals(removePRet, 1);
		
		// Delete
		int removeSERet = myBatisServiceAPIManagerDao.deleteService(lastId);
		Assert.assertEquals(removeSERet, 1);
		
	}
}
