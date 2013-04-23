package com.kthcorp.radix.dao.hazelcast;
import java.util.Map.Entry;

import com.hazelcast.client.ClientConfig;
import com.hazelcast.client.HazelcastClient;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.kthcorp.radix.domain.service.Service;
import com.kthcorp.radix.domain.service.routing.RoutingMethod;
import com.kthcorp.radix.domain.service.routing.RoutingMethodType;
import com.kthcorp.radix.domain.service.routing.direct.DirectMethod;
import com.kthcorp.radix.util.UUIDUtils;


public class HazelcastTestSkip {

	public static void main(String[] args) {
		final String clusterName = "radix";
		final String password = "radix";
		final ClientConfig config = new ClientConfig();
		final String server = "211.42.137.122:35701";
		
		config.addAddress(server);
		
		config.getGroupConfig().setName(clusterName).setPassword(password);
		
		System.out.println("[Hazelcast-Client] Trying to connect cluster: "+server+" [" + clusterName + "]");
		HazelcastInstance client = HazelcastClient.newHazelcastClient(config);
		
		System.out.println("[Hazelcast-Client] Connected to cluster group: [" + clusterName + "]");
		
		IMap<String,RoutingMethod> routingResource = client.getMap("routingResource");
		System.out.println("routingResource::");

		for(Entry<String,RoutingMethod> entry : routingResource.entrySet()) {
			RoutingMethod methodObj = entry.getValue();
			if(methodObj.getRoutingMethodType()==RoutingMethodType.DIRECT) {
				DirectMethod method = (DirectMethod) methodObj;
				System.out.println("\tKey->"+entry.getKey()+",Value->"+method);
			}
		}
		
		IMap<byte[], Service> routingResourceSList = client.getMap("routingResource.SList");
		System.out.println("routingResourceSList::");
		for(Entry<byte[],Service> entry : routingResourceSList.entrySet()) {
			System.out.println("\tKey->"+UUIDUtils.getString(entry.getKey())+"Value->"+entry.getValue());
		}
		
	}
}
