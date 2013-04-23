package com.kthcorp.radix.dao.hazelcast;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import com.kthcorp.radix.util.test.junit.MysqlEmbeddableSpringJUnit4ClassRunner;

import com.kthcorp.radix.api.dao.RoutingResourceManagerDao;
import com.kthcorp.radix.domain.service.Service;
import com.kthcorp.radix.domain.service.api.serviceAPI.ServiceAPI;
import com.kthcorp.radix.domain.service.routing.RoutingMethod;
import com.kthcorp.radix.domain.service.routing.direct.DirectMethod;
import com.kthcorp.radix.util.UUIDUtils;

@RunWith(MysqlEmbeddableSpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { 
		"classpath:/META-INF/spring/spring-application-context-test.xml", 
		"classpath:/META-INF/spring/spring-dao-hazelcast-context.xml" })
public class HazelcastRoutingResourceManagerDaoTest extends AbstractJUnit4SpringContextTests {
	
	@Autowired
	private RoutingResourceManagerDao resourceManager;

	
	@Test
	public void insertRoutingMethod() {

		DirectMethod method = new DirectMethod();
		ServiceAPI serviceAPI = new ServiceAPI();
		serviceAPI.setName("testAPI");
		method.setServiceAPI(serviceAPI);
		String objectKey = "testKey";
		
		UUID bpID = UUID.randomUUID();
		UUID sID = UUID.randomUUID();
		
		Service service = new Service();
		service.setId(UUIDUtils.getBytes(sID));
		
		resourceManager.insertService(service);
		List<RoutingMethod> routingMethodList = new ArrayList<RoutingMethod>();
		routingMethodList.add(method);
		resourceManager.updateRoutingMethods(bpID.toString(), sID.toString(), objectKey, routingMethodList);
		
		List<RoutingMethod> actualRoutingMethodList = resourceManager.selectRoutingMethods(objectKey);
		Assert.assertNotNull(actualRoutingMethodList);
		Assert.assertTrue(actualRoutingMethodList.size()==1);
		DirectMethod methodGot = (DirectMethod) actualRoutingMethodList.get(0);
		
		Assert.assertNotNull(methodGot);
		
		resourceManager.deleteRoutingMethod(bpID.toString(), sID.toString(), objectKey);
		resourceManager.deleteService(bpID.toString(), sID.toString());
	}
}
