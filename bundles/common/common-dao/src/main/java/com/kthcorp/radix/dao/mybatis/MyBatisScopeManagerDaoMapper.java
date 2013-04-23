package com.kthcorp.radix.dao.mybatis;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.kthcorp.radix.domain.scope.ScopePoliciesDAO;

public interface MyBatisScopeManagerDaoMapper {

	@Insert(
			{
				"INSERT INTO",
				"radix_scope_repository",
				"(serviceAPIID,clientID,packageID,apiKey,packageParams,policyList,status,regDate,updateDate)",
				"VALUES",
				"(#{serviceAPIID},#{clientID},#{packageID},#{apiKey},#{packageParams},#{policyList},#{status},NOW(),NOW())"
			}
	)
	public int insertScopePolicies(ScopePoliciesDAO scopePoliciesDAO);

	@Select(
			{
				"SELECT",
				"serviceAPIID,clientID,packageID,apiKey,packageParams,policyList,status,regDate,updateDate",
				"FROM",
				"radix_scope_repository",
				"WHERE",
				"apiKey=#{apiKey} and clientID=#{clientID}"
			}
	)
	public List<ScopePoliciesDAO> selectScopePoliciesDAOListWithAPIKey(@Param("apiKey") String apiKey, @Param("clientID") byte[] clientID);
	
	@Select(
			{
                "SELECT",
                "s.serviceAPIID,s.clientID,s.packageID,s.apiKey,s.packageParams,s.policyList,s.status,s.regDate,s.updateDate",
                "FROM",
                "radix_scope_repository s, radix_client c",
                "WHERE",
                "s.serviceAPIID=#{serviceAPIID} and s.clientID=#{clientID} and c.id=#{clientID} and c.isValid='Y'"
			}
	)
	public ScopePoliciesDAO selectScopePoliciesDAOWithServiceAPIID(@Param("serviceAPIID") byte[] serviceAPIID, @Param("clientID") byte[] clientID);

	@Deprecated // serviceAPIID와 clientID로 키로 찾아야 한다. apiKey는 유니크하지 않다. 기존 테스트 케이스 관련 남겨 둔다.
	@Select(
			{
				"SELECT",
				"status",
				"FROM",
				"radix_scope_repository",
				"WHERE",
				"apiKey=#{apiKey} and clientID=#{clientID}"
			}
	)
	public String selectStatus(@Param("apiKey") String apiKey, @Param("clientID") byte[] clientID);

	@Update(
			{
				"UPDATE",
				"radix_scope_repository",
				"SET",
				"status=#{status}",
				"WHERE",
				"apiKey=#{apiKey} and clientID=#{clientID}"
			}
	)
	public int updateStatus(@Param("apiKey") String apiKey, @Param("clientID") byte[] clientID, @Param("status") String status);

	@Delete(
			{
				"DELETE",
				"FROM",
				"radix_scope_repository",
				"WHERE",
				"clientID=#{clientID}"
			}
	)
	public int deleteScopePoliciesWithClientID(@Param("clientID") byte[] clientID);

	@Delete(
			{
				"DELETE",
				"FROM",
				"radix_scope_repository",
				"WHERE",
				"clientID=#{clientID} and packageID=#{packageID}"
			}
	)
	public int deleteScopePoliciesWithCP(@Param("clientID") byte[] clientID, @Param("packageID") byte[] packageID);

	@Delete(
			{
				"DELETE",
				"FROM",
				"radix_scope_repository",
				"WHERE",
				"apiKey=#{apiKey} and clientID=#{clientID}"
			}
	)
	public int deleteScopePoliciesWithAPIKey(@Param("apiKey") String apiKey, @Param("clientID") byte[] clientID);
	
	@Delete(
			{
				"DELETE",
				"FROM",
				"radix_scope_repository",
				"WHERE",
				"serviceAPIID=#{serviceAPIID} and clientID=#{clientID}"
			}
	)
	public int deleteScopePoliciesWithServiceAPIID(@Param("serviceAPIID") byte[] serviceAPIID, @Param("clientID") byte[] clientID);

	@Delete(
			{
				"DELETE",
				"FROM",
				"radix_scope_repository",
				"WHERE",
				"apiKey=#{apiKey} and clientID=#{clientID} and packageID=#{packageID}"
			}
	)
	public int deleteScopePolicies(@Param("apiKey") String apiKey, @Param("clientID") byte[] clientID, @Param("packageID") byte[] packageID);

	@Select(
			{
				"SELECT",
				"distinct rsas.name",
				"FROM",
				"radix_scope_repository rsr, radix_service_apis_serviceapi rsas",
				"WHERE",
				"clientID=#{clientID} and rsr.serviceAPIID = rsas.id"
				
			}
	)
	public List<String> selectScopeList(@Param("clientID") byte[] clientID);
}
