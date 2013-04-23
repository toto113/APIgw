package com.kthcorp.radix.dao.mybatis;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.InsertProvider;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.apache.ibatis.annotations.UpdateProvider;

import com.kthcorp.radix.domain.service.Service;
import com.kthcorp.radix.domain.service.api.partnerAPI.PartnerAPI;
import com.kthcorp.radix.domain.service.api.serviceAPI.ServiceAPI;
import com.kthcorp.radix.domain.service.mapping.MappingInfo;
import com.kthcorp.radix.domain.service.mapping.MappingType;
import com.kthcorp.radix.domain.service.routing.direct.DirectMethod;

public interface MyBatisServiceManagerDaoMapper {
	
	/*
	 * +-------------------------------+ | Tables_in_radix |
	 * +-------------------------------+ | radix_service | |
	 * radix_service_apis_mapping | | radix_service_apis_partnerapi | |
	 * radix_service_apis_serviceapi | | radix_service_routing_direct |
	 * +-------------------------------+
	 */
	
	/* radix_service */
	@InsertProvider(method = "insertServiceInDynamicSQL", type = MyBatisServiceManagerDaoDynamicSQL.class)
	public int insertService(Service service);
	
	@Select({ "SELECT", "id, businessPlatformID, partnerID, name, version, resourceOwner, resourceAuthUrl,", "regDate, updateDate, isValid", "FROM", "radix_service", "WHERE", "businessPlatformID=#{businessPlatformID} and partnerID=#{partnerID}" })
	public List<Service> selectServiceListWithServiceInfo(@Param("businessPlatformID") byte[] businessPlatformID, @Param("partnerID") String partnerID);
	
	@Select({ "SELECT", "id, businessPlatformID, partnerID, name, version, resourceOwner, resourceAuthUrl,", "regDate, updateDate, isValid", "FROM", "radix_service" })
	public List<Service> selectAllServiceList();
	
	@Select({ "SELECT", "id, businessPlatformID, partnerID, name, version, resourceOwner, resourceAuthUrl,", "regDate, updateDate, isValid", "FROM", "radix_service", "WHERE", "businessPlatformID=#{businessPlatformID}" })
	public List<Service> selectAllBusinessPlatformServiceList(@Param("businessPlatformID") byte[] businessPlatformID);

	@Select({ "SELECT", "id, businessPlatformID, partnerID, name, version, resourceOwner, resourceAuthUrl,", "regDate, updateDate, isValid", "FROM", "radix_service", "WHERE", "partnerID=#{partnerID}" })
	public List<Service> selectAllServiceByPartnerID(@Param("partnerID") String partnerID);

	@Select({ "SELECT", "id, businessPlatformID, partnerID, name, version, resourceOwner, resourceAuthUrl,", "regDate, updateDate, isValid", "FROM", "radix_service", "WHERE", "id=#{serviceID}" })
	public Service selectService(@Param("serviceID") byte[] serviceID);
	
	@Select({ "SELECT", "id, businessPlatformID, partnerID, name, version, resourceOwner, resourceAuthUrl,", "regDate, updateDate, isValid", "FROM", "radix_service", "WHERE", "partnerID=#{partnerID} and id=#{serviceID}" })
	public Service selectServiceByPartnerID(@Param("partnerID") String partnerID, @Param("serviceID") byte[] serviceID);
	
	@Select ({ "SELECT IFNULL(count(1),0) FROM radix_service WHERE id=#{serviceID}"})
	public int selectExistingServiceCount(@Param("serviceID") byte[] serviceID);
	
	@Select ({ "SELECT IFNULL(count(1),0) FROM radix_service WHERE id=#{serviceID} and partnerID=#{partnerID}"})
	public int selectExistingServiceCountWithPartnerID(@Param("serviceID") byte[] serviceID, @Param("partnerID") String partnerID);
	
	@UpdateProvider(method = "updateServiceInDynamicSQL", type = MyBatisServiceManagerDaoDynamicSQL.class)
	public int updateService(Service updateService);
	
	@Insert( 
			{
				"INSERT",
				"INTO",
				"backup.radix_service",
				"(id, businessPlatformID, partnerID, name, version, resourceOwner, resourceAuthUrl, regDate, updateDate, isValid, removeDate)",
				"SELECT",
				"id, businessPlatformID, partnerID, name, version, resourceOwner, resourceAuthUrl, regDate, updateDate, isValid, NOW() as removeDate",
				"FROM",
				"radix_service",
				"WHERE",
				"id=#{serviceID}"
			}
		)
	public int backupService(@Param("serviceID") byte[] serviceID);
	
	@Delete({ "DELETE", "FROM", "radix_service", "WHERE", "businessPlatformID=#{businessPlatformID} and partnerID=#{partnerID}" })
	public int deleteServiceWithServiceInfo(@Param("businessPlatformID") byte[] businessPlatformID, @Param("partnerID") String partnerID);
	
	@Delete({ "DELETE", "FROM", "radix_service", "WHERE", "id=#{serviceID}" })
	public int deleteService(@Param("serviceID") byte[] serviceID);
	
	/* radix_service_apis_serviceapi */
	@InsertProvider(method = "insertServiceAPIInDynamicSQL", type = MyBatisServiceManagerDaoDynamicSQL.class)
	public int insertServiceAPI(ServiceAPI serviceAPI);
	
	@Select({ "SELECT", "id,serviceID,name,transportType,defaultTransformType,protocolType,protocolMeta,parameters,regDate,updateDate,isValid", "FROM", "radix_service_apis_serviceapi", "WHERE", "id=#{serviceID}" })
	public ServiceAPI selectServiceAPI(@Param("serviceID") byte[] serviceID);

	@Select(
			{
				"SELECT IFNULL(count(1),0)", 
				"FROM", 
				"radix_service_apis_serviceapi", 
				"WHERE", 
				"id=#{serviceAPIID}"
			}
	)
	public int selectExistsServiceAPICount(@Param("serviceAPIID") byte[] serviceAPIID);
	
	@Select({ "SELECT", "id,serviceID,apiKey,name,transportType,defaultTransformType,protocolType,protocolMeta,parameters,regDate,updateDate,isValid", "FROM", "radix_service_apis_serviceapi", "WHERE", "serviceID=#{serviceID}" })
	public List<ServiceAPI> selectAllServiceAPIList(@Param("serviceID") byte[] serviceID);
	
	@Select({ "SELECT", "id,serviceID,apiKey,name,transportType,defaultTransformType,protocolType,protocolMeta,parameters,regDate,updateDate,isValid", "FROM", "radix_service_apis_serviceapi", "WHERE", "serviceID=#{serviceID} and name=#{name}" })
	public ServiceAPI selectServiceAPIWithServiceInfo(@Param("serviceID") byte[] serviceID, @Param("name") String name);
	
	@UpdateProvider(method = "updateServiceAPIInDynamicSQL", type = MyBatisServiceManagerDaoDynamicSQL.class)
	public int updateServiceAPI(ServiceAPI updateServiceAPI);
	
	@Insert( 
			{
				"INSERT",
				"INTO",
				"backup.radix_service_apis_serviceapi",
				"(id,serviceID,apiKey,name,transportType,defaultTransformType,protocolType,protocolMeta,parameters,regDate,updateDate,isValid,removeDate)",
				"SELECT",
				"id,serviceID,apiKey,name,transportType,defaultTransformType,protocolType,protocolMeta,parameters,regDate,updateDate,isValid,NOW() as removeDate",
				"FROM",
				"radix_service_apis_serviceapi",
				"WHERE",
				"serviceID=#{serviceID}"
			}
		)
	public int backupServiceAPIWithServiceID(@Param("serviceID") byte[] serviceID);
	
	@Delete({ "DELETE", "FROM", "radix_service_apis_serviceapi", "WHERE", "serviceID=#{serviceID}" })
	public int deleteServiceAPIWithServiceID(@Param("serviceID") byte[] serviceID);
	
	@Delete({ "DELETE", "FROM", "radix_service_apis_serviceapi", "WHERE", "id=#{id}" })
	public int deleteServiceAPI(@Param("id") byte[] id);
	
	/* radix_service_apis_partnerapi */
	@InsertProvider(method = "insertPartnerAPIInDynamicSQL", type = MyBatisServiceManagerDaoDynamicSQL.class)
	public int insertPartnerAPI(PartnerAPI partnerAPI);
	
	@Select({ "SELECT", "id,serviceID,name,transportType,defaultTransformType,protocolType,protocolMeta,parameters,regDate,updateDate,isValid", "FROM", "radix_service_apis_partnerapi", "WHERE", "id=#{partnerAPIID}" })
	public PartnerAPI selectPartnerAPI(@Param("partnerAPIID") byte[] partnerAPIID);
	
	@Select({ "SELECT", "id,serviceID,name,transportType,defaultTransformType,protocolType,protocolMeta,parameters,regDate,updateDate,isValid", "FROM", "radix_service_apis_partnerapi", "WHERE", "serviceID=#{serviceID} and name=#{name}" })
	public PartnerAPI selectPartnerAPIWithServiceInfo(@Param("serviceID") byte[] serviceID, @Param("name") String name);
	
	@UpdateProvider(method = "updatePartnerAPIInDynamicSQL", type = MyBatisServiceManagerDaoDynamicSQL.class)
	public int updatePartnerAPI(PartnerAPI updatePartnerAPI);
	
	@Insert( 
			{
				"INSERT",
				"INTO",
				"backup.radix_service_apis_partnerapi",
				"(id,serviceID,name,transportType,defaultTransformType,protocolType,protocolMeta,parameters,regDate,updateDate,isValid,removeDate)",
				"SELECT",
				"id,serviceID,name,transportType,defaultTransformType,protocolType,protocolMeta,parameters,regDate,updateDate,isValid,NOW() as removeDate",
				"FROM",
				"radix_service_apis_partnerapi",
				"WHERE",
				"serviceID=#{serviceID}"
			}
		)
	public int backupPartnerAPIWithServiceID(@Param("serviceID") byte[] serviceID);
	
	@Delete({ "DELETE", "FROM", "radix_service_apis_partnerapi", "WHERE", "serviceID=#{serviceID}" })
	public int deletePartnerAPIWithServiceID(@Param("serviceID") byte[] serviceID);
	
	@Delete({ "DELETE", "FROM", "radix_service_apis_partnerapi", "WHERE", "id=#{partnerAPIID}" })
	public int deletePartnerAPI(@Param("partnerAPIID") byte[] partnerAPIID);
	
	/* radix_service_apis_mapping */
	@Insert({ "INSERT INTO", "radix_service_apis_mapping", "(id,serviceID,serviceAPIID,partnerAPIID,mappingType,mapping,regDate,updateDate)", "VALUES", "(#{id},#{serviceID},#{serviceAPIID},#{partnerAPIID},#{mappingType},#{mapping},NOW(),NOW())" })
	public int insertAPIMapping(MappingInfo mappingInfo);
	
	@Select({ "SELECT", "id,serviceID,serviceAPIID,partnerAPIID,mappingType,mapping,regDate,updateDate,isValid", "FROM", "radix_service_apis_mapping", "WHERE", "id=#{mappindID}" })
	public MappingInfo selectAPIMapping(@Param("mappindID") byte[] mappindID);
	
	@Select({ "SELECT", "id,serviceID,serviceAPIID,partnerAPIID,mappingType,mapping,regDate,updateDate,isValid", "FROM", "radix_service_apis_mapping", "WHERE", "serviceID=#{serviceID} and serviceAPIID=#{serviceAPIID} and partnerAPIID=#{partnerAPIID} and mappingType=#{mappingType}" })
	public MappingInfo selectAPIMappingWithAPIInfo(@Param("serviceID") byte[] serviceID, @Param("serviceAPIID") byte[] serviceAPIID, @Param("partnerAPIID") byte[] partnerAPIID, @Param("mappingType") MappingType mappingType);

	@UpdateProvider(method = "updateAPIMappingInDynamicSQL", type = MyBatisServiceManagerDaoDynamicSQL.class)
	public int updateAPIMappingWithAPIInfo(MappingInfo updateMappingInfo);
	
	@Update({ "UPDATE", "radix_service_apis_mapping", "SET", "mappingType=#{mappingType},mapping=#{mapping},updateDate=NOW(),isValid=#{isValid}", "WHERE", "id=#{id}" })
	public int updateAPIMapping(MappingInfo updateMappingInfo);

	@Update({ "UPDATE", "radix_service_apis_serviceapi", "SET", "apiKey=#{apiKey}", "WHERE", "serviceID=#{serviceID} and id=#{serviceAPIID}" })
	public int updateAPIKeyOfServiceAPI(@Param("serviceID") byte[] serviceID, @Param("serviceAPIID") byte[] serviceAPIID, @Param("apiKey") String apiKey);
	
	@Insert( 
			{
				"INSERT",
				"INTO",
				"backup.radix_service_apis_mapping",
				"(id,serviceID,serviceAPIID,partnerAPIID,mappingType,mapping,regDate,updateDate,removeDate,isValid)",
				"SELECT",
				"id,serviceID,serviceAPIID,partnerAPIID,mappingType,mapping,regDate,updateDate,NOW() as removeDate, isValid",
				"FROM",
				"radix_service_apis_mapping",
				"WHERE",
				"serviceID=#{serviceID}"
			}
		)
	public int backupAPIMapping(@Param("serviceID") byte[] serviceID);
	
	@Delete(
			{ 
				"DELETE", 
				"FROM",
				"radix_service_apis_mapping", 
				"WHERE", "serviceID=#{serviceID}" 
			}
	)
	public int deleteAPIMapping(@Param("serviceID") byte[] serviceID);
	
	@Insert( 
			{
				"INSERT",
				"INTO",
				"backup.radix_service_apis_mapping",
				"(id,serviceID,serviceAPIID,partnerAPIID,mappingType,mapping,regDate,updateDate,removeDate,isValid)",
				"SELECT",
				"id,serviceID,serviceAPIID,partnerAPIID,mappingType,mapping,regDate,updateDate,NOW() as removeDate, isValid",
				"FROM",
				"radix_service_apis_mapping",
				"WHERE",
				"id=#{id}"
			}
		)
	public int backupAPIMappingWithID(@Param("id") byte[] id);
	
	@Delete(
			{ 
				"DELETE", 
				"FROM",
				"radix_service_apis_mapping", 
				"WHERE",
				"id=#{id}"
			}
	)
	public int deleteAPIMappingWithID(@Param("id") byte[] id);
	
	@Delete({ "DELETE", "FROM", "backup.radix_service_apis_mapping", "WHERE", "id=#{id}" })
	public int deleteBackupAPIMappingWithID(@Param("id") byte[] id);
	
	@Delete({ "DELETE", "FROM", "backup.radix_service_apis_mapping", "WHERE", "serviceID=#{serviceID} and serviceAPIID=#{serviceAPIID} and partnerAPIID=#{partnerAPIID}" })
	public int deleteBackupAPIMappingWithAPIInfo(@Param("serviceID") byte[] serviceID, @Param("serviceAPIID") byte[] serviceAPIID, @Param("partnerAPIID") byte[] partnerAPIID, @Param("mappingType") MappingType mappingType);
	
	/* radix_service_routing_direct */
	@InsertProvider(method = "insertDirectRoutingInDynamicSQL", type = MyBatisServiceManagerDaoDynamicSQL.class)
	public int insertDirectRouting(DirectMethod directMethod);
	
	@Select({ "SELECT", "id,serviceID,serviceAPIID,partnerAPIID,parameterMappingID,resultMappingID,regDate,updateDate,isValid", "FROM", "radix_service_routing_direct", "WHERE", "id=#{id}" })
	public PartnerAPI selectDirectRouting(@Param("id") byte[] id);
	
	@Select({ "SELECT", "id,serviceID,serviceAPIID,partnerAPIID,parameterMappingID,resultMappingID,regDate,updateDate,isValid", "FROM", "radix_service_routing_direct", "WHERE", "serviceID=#{serviceID} and serviceAPIID=#{serviceAPIID}" })
	public DirectMethod selectDirectRoutingWithAPIInfo(@Param("serviceID") byte[] serviceID, @Param("serviceAPIID") byte[] serviceAPIID);
	
	@Select({ "SELECT", "id,serviceID,serviceAPIID,partnerAPIID,parameterMappingID,resultMappingID,regDate,updateDate,isValid", "FROM", "radix_service_routing_direct", "WHERE", "serviceID=#{serviceID} and serviceAPIID=#{serviceAPIID} and partnerAPIID=#{partnerAPIID}" })
	public DirectMethod selectDirectRoutingWithAPIInfo2(@Param("serviceID") byte[] serviceID, @Param("serviceAPIID") byte[] serviceAPIID, @Param("partnerAPIID") byte[] partnerAPIID);
	
	@UpdateProvider(method = "updateDirectRoutingInDynamicSQL", type = MyBatisServiceManagerDaoDynamicSQL.class)
	public int updateDirectRoutingWithAPIInfo(DirectMethod updateDirectMethod);
	
	@Update({ "UPDATE", "radix_service_routing_direct", "SET", "updateDate=NOW(),isValid=#{isValid}", "WHERE", "id=#{id}" })
	public int updateDirectRouting(DirectMethod updateDirectMethod);
	
	@Insert( 
			{
				"INSERT",
				"INTO",
				"backup.radix_service_routing_direct",
				"(id,serviceID,serviceAPIID,partnerAPIID,parameterMappingID,resultMappingID,regDate,updateDate,removeDate,isValid)",
				"SELECT",
				"id,serviceID,serviceAPIID,partnerAPIID,parameterMappingID,resultMappingID,regDate,updateDate,NOW() as removeDate, isValid",
				"FROM",
				"radix_service_routing_direct",
				"WHERE",
				"serviceID=#{serviceID}"
			}
		)
	public int backupDirectRouting(@Param("serviceID") byte[] serviceID);
	
	@Delete(
			{ 
				"DELETE", 
				"FROM",
				"radix_service_routing_direct", 
				"WHERE", 
				"serviceID=#{serviceID}" 
			}
	)
	public int deleteDirectRouting(@Param("serviceID") byte[] serviceID);
	
	@Insert( 
			{
				"INSERT",
				"INTO",
				"backup.radix_service_routing_direct",
				"(id,serviceID,serviceAPIID,partnerAPIID,parameterMappingID,resultMappingID,regDate,updateDate,removeDate,isValid)",
				"SELECT",
				"id,serviceID,serviceAPIID,partnerAPIID,parameterMappingID,resultMappingID,regDate,updateDate,NOW() as removeDate, isValid",
				"FROM",
				"radix_service_routing_direct",
				"WHERE",
				"serviceID=#{serviceID} and serviceAPIID=#{serviceAPIID} and partnerAPIID=#{partnerAPIID}"
			}
		)
	public int backupDirectRoutingWithAPIInfo(@Param("serviceID") byte[] serviceID, @Param("serviceAPIID") byte[] serviceAPIID, @Param("partnerAPIID") byte[] partnerAPIID);
	
	@Delete(
			{ 
				"DELETE", 
				"FROM",
				"radix_service_routing_direct", 
				"WHERE", 
				"serviceID=#{serviceID} and serviceAPIID=#{serviceAPIID} and partnerAPIID=#{partnerAPIID}"
			}
	)
	public int deleteDirectRoutingWithAPIInfo(@Param("serviceID") byte[] serviceID, @Param("serviceAPIID") byte[] serviceAPIID, @Param("partnerAPIID") byte[] partnerAPIID);
	
	@Delete({ "DELETE", "FROM", "backup.radix_service_routing_direct", "WHERE", "id=#{id}" })
	public int deleteBackupDirectRouting(@Param("id") byte[] id);
	
	@Delete({ "DELETE", "FROM", "backup.radix_service_routing_direct", "WHERE", "serviceID=#{serviceID} and serviceAPIID=#{serviceAPIID} and partnerAPIID=#{partnerAPIID}" })
	public int deleteBackupDirectRoutingWithAPIInfo(@Param("serviceID") byte[] serviceID, @Param("serviceAPIID") byte[] serviceAPIID, @Param("partnerAPIID") byte[] partnerAPIID);
	
	@Select(
			{
				"SELECT IFNULL(count(1),0)", 
				"FROM", 
				"radix_service", 
				"WHERE", 
				"businessPlatformID=#{businessPlatformID} and name=#{name} and version=#{version}"
			}
	)
	public int selectExistsServiceCount(@Param("businessPlatformID") byte[] businessPlatformID, @Param("name") String name, @Param("version") String version);
	
	
}
