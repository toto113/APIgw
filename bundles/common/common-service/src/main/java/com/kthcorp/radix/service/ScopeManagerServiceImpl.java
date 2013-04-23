package com.kthcorp.radix.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import com.kthcorp.radix.util.UuidViewableLoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.kthcorp.radix.api.service.ScopeManagerService;
import com.kthcorp.radix.api.service.ServiceManagerService;
import com.kthcorp.radix.dao.mybatis.MyBatisClientPackageManagerDaoMapper;
import com.kthcorp.radix.dao.mybatis.MyBatisPackageManagerDaoMapper;
import com.kthcorp.radix.dao.mybatis.MyBatisPolicyManagerDaoMapper;
import com.kthcorp.radix.dao.mybatis.MyBatisScopeManagerDaoMapper;
import com.kthcorp.radix.domain.client.CPackages;
import com.kthcorp.radix.domain.exception.DataBaseProcessingException;
import com.kthcorp.radix.domain.exception.NotFoundException;
import com.kthcorp.radix.domain.exception.ValidateException;
import com.kthcorp.radix.domain.scope.Policy;
import com.kthcorp.radix.domain.scope.ScopePolicies;
import com.kthcorp.radix.domain.scope.ScopePoliciesDAO;
import com.kthcorp.radix.domain.service.api.serviceAPI.ServiceAPI;
import com.kthcorp.radix.domain.service.routing.RoutingMethod;
import com.kthcorp.radix.domain.service.routing.direct.DirectMethod;
import com.kthcorp.radix.domain.service.routing.RoutingMethodSelector;
import com.kthcorp.radix.util.UUIDUtils;

public class ScopeManagerServiceImpl implements ScopeManagerService {

	private static final Logger LOG = UuidViewableLoggerFactory.getLogger(ScopeManagerServiceImpl.class);

	@Autowired
	private ServiceManagerService serviceManagerService;
	
	@Autowired
	private MyBatisClientPackageManagerDaoMapper myBatisClientPackageManagerDaoMapper;

	@Autowired
	private MyBatisScopeManagerDaoMapper myBatisScopeManagerDaoMapper;

	@Autowired
	private MyBatisPolicyManagerDaoMapper myBatisPolicyManagerDao;

	@Autowired
	private MyBatisPackageManagerDaoMapper myBatisPackageManagerDao;

	/**
	 * High Level Interface
	 */

	@Override
	public void createPackagePolicies(byte[] clientID, byte[] packageID) throws NotFoundException, ValidateException, DataBaseProcessingException {

		// Check Package
		if(myBatisPackageManagerDao.selectExistsPackageCount(packageID) < 1) {
			throw new NotFoundException("package does not exists");
		}

		CPackages packages = myBatisClientPackageManagerDaoMapper.selectPackageLastOneWithCP(clientID, packageID);
		if(packages == null) {
			throw new NotFoundException("the clientID(" + UUIDUtils.getString(clientID) + ") has not package(" + UUIDUtils.getString(packageID) + ")");
		}

		// Package 내의 Service API 를 읽어 낸다.
		List<ServiceAPI> serviceAPIList = myBatisPackageManagerDao.selectServiceAPIDetailList(packageID);
		if(serviceAPIList.size() < 1) {
			throw new NotFoundException("ServiceAPI does not found:packageID->" + UUIDUtils.getString(packageID));
		}

		// Package Policy 를 읽어 낸다.
		List<Policy> packagePolicyList = this.convertToScopePolicy(clientID, packageID, myBatisPolicyManagerDao.selectPackagePolicy(packageID));

		if(LOG.isDebugEnabled()) {
			if(packagePolicyList != null) {
				LOG.debug("Get package policies, clientID->" + UUIDUtils.getString(clientID) + ", packageID->" + UUIDUtils.getString(packageID) + ", policyCount->" + packagePolicyList.size());
			} else {
				LOG.debug("Get package policies, clientID->" + UUIDUtils.getString(clientID) + ", packageID->" + UUIDUtils.getString(packageID) + ", policyCount->0");
			}
			if(LOG.isTraceEnabled()) {
				for(Policy policy : packagePolicyList) {
					LOG.debug("\t\t<< Package(" + UUIDUtils.getString(packageID) + ")->Policy(" + policy.toString() + ")");
				}
			}
		}

		// 소속된 Service API 에 대해 Policy 생성
		if(serviceAPIList != null && serviceAPIList.size() > 0) {
			for(ServiceAPI serviceAPI : serviceAPIList) {

				ScopePolicies scopePolicies = new ScopePolicies();
				List<Policy> policies = new ArrayList<Policy>();

				// Client 에 붙어 있는 Package 의 Parameters 를 저장 (ex: startTimestamp, endTimestamp)
				policies.addAll(packagePolicyList);

				if(serviceAPI.getApiKey() == null) {
					if(LOG.isWarnEnabled()) {
						LOG.warn("apiKey does not found (clientID->" + UUIDUtils.getString(clientID) + ", packageID->" + UUIDUtils.getString(packageID) + ", serviceAPIID->" + UUIDUtils.getString(serviceAPI.getId()) + "), it may be not loaded");
					}
					continue;
				}
				List<Policy> policyList = this.convertToScopePolicy(clientID, packageID, myBatisPolicyManagerDao.selectPackageServiceAPIPolicyList(packageID, serviceAPI.getId()));
				if(policyList != null && policyList.size() > 0) {

					/* ServiceAPI 에 대한 Policy 를 추가 */
					/* TODO: 현재는 Package 에 따른 중복을 생각하지 않음 하지만 이에 대해 생각하고 나중에 정리 할 필요가 있음 */
					policies.addAll(policyList);
				}

				scopePolicies.setPackageParams(packages.getParametersObj());
				scopePolicies.setPolicyList(policies);

				this.createScopePolicies(serviceAPI.getApiKey(), clientID, packageID, serviceAPI.getId(), scopePolicies);

				if(LOG.isDebugEnabled()) {
					LOG.debug("[createPackagePolicies] created scope policy (apiKey->" + serviceAPI.getApiKey() + ", packageParameters->" + packages.getParametersObj().toString() + ", policyList->" + policies + ")");
				}
			}
		}
	}

	@Override
	public void modifyPackagePolicies(byte[] clientID, byte[] packageID) throws NotFoundException, ValidateException, DataBaseProcessingException {

		this.removePackagePolicies(clientID, packageID);
		this.createPackagePolicies(clientID, packageID);
	}

	@Override
	public void modifyStatus(byte[] clientID, String apiKey, Map<String, String> status) throws NotFoundException, ValidateException, DataBaseProcessingException {

		this.modifyStatus(apiKey, clientID, status);
	}

	@Override
	public void removePackagePolicies(byte[] clientID, byte[] packageID) throws NotFoundException, ValidateException {

		// Check Package
		if(myBatisPackageManagerDao.selectExistsPackageCount(packageID) < 1) {
			throw new NotFoundException("package does not exists");
		}

		myBatisScopeManagerDaoMapper.deleteScopePoliciesWithCP(clientID, packageID);
	}

	private List<Policy> convertToScopePolicy(byte[] clientID, byte[] packageID, List<com.kthcorp.radix.domain.policy.Policy> policyList) {
		return this.convertToScopePolicy(clientID, packageID, null, policyList);
	}

	private List<Policy> convertToScopePolicy(byte[] clientID, byte[] packageID, byte[] serviceAPIID, List<com.kthcorp.radix.domain.policy.Policy> policyList) {
		if(policyList == null || policyList.size() < 1) {
			return null;
		}
		List<Policy> ret = new ArrayList<Policy>();
		for(com.kthcorp.radix.domain.policy.Policy policy : policyList) {
			ret.add(this.convertToScopePolicy(clientID, packageID, serviceAPIID, policy));
		}
		return ret;
	}

	private Policy convertToScopePolicy(byte[] clientID, byte[] packageID, byte[] serviceAPIID, com.kthcorp.radix.domain.policy.Policy policy) {
		if(policy == null) {
			return null;
		}
		Policy ret = new Policy();
		ret.setClientID(clientID);
		ret.setId(policy.getId());
		ret.setPackageID(packageID);
		ret.setPolicyTypeID(policy.getPolicyTypeID());

		if(serviceAPIID == null) {
			ret.setPackagePolicy(true);
		} else {
			ret.setPackagePolicy(false);
			ret.setServiceAPIID(serviceAPIID);
		}

		ret.setProperties(policy.getPropertiesObj());

		return ret;
	}

	/**
	 * Low Level Interface
	 * 
	 * @throws DataBaseProcessingException
	 */

	@Override
	public void createScopePolicies(String apiKey, byte[] clientID, byte[] packageID, byte[] serviceAPIID, ScopePolicies scopePolicies) throws ValidateException, DataBaseProcessingException {

		ScopePoliciesDAO scopePoliciesDAO = new ScopePoliciesDAO();
		scopePoliciesDAO.setApiKey(apiKey);
		scopePoliciesDAO.setClientID(clientID);
		scopePoliciesDAO.setPackageID(packageID);
		scopePoliciesDAO.setServiceAPIID(serviceAPIID);

		try {
			scopePoliciesDAO.setPackageParamsObj(scopePolicies.getPackageParams());
			scopePoliciesDAO.setPolicyList(scopePolicies.getPolicyList());
			scopePoliciesDAO.setStatusObj(scopePolicies.getStatus());

			myBatisScopeManagerDaoMapper.insertScopePolicies(scopePoliciesDAO);
		} catch(JSONException e) {
			throw new DataBaseProcessingException("DB Object generating failed," + e.getMessage());
		}
	}

	@Override
	public ScopePolicies getScopePolicies(String apiKey, String resourcePath, byte[] clientID) throws ValidateException, DataBaseProcessingException {
		
		List<RoutingMethod> routingMethodList = serviceManagerService.getRoutingMethods(apiKey);
		RoutingMethod routingMethod = RoutingMethodSelector.selectRoutingMethod(routingMethodList, resourcePath);
		if(routingMethod==null) {
			throw new ValidateException("routingMethod not found. apiKey="+apiKey+", resourcePath="+resourcePath+", clientID="+UUIDUtils.getString(clientID));
		}
		byte[] serviceAPIID = ((DirectMethod)routingMethod).getServiceAPI().getId();
		
		/* FIXME: Package 가 여러개가 있을 수 있음 이때 에러가 날 것임 */

		ScopePoliciesDAO dao = myBatisScopeManagerDaoMapper.selectScopePoliciesDAOWithServiceAPIID(serviceAPIID, clientID);
		if(dao==null) {
			throw new DataBaseProcessingException("not found scopePolicies serviceAPIID="+UUIDUtils.getString(serviceAPIID)+", clientID="+UUIDUtils.getString(clientID));
		}
		ScopePolicies scopePolicies = new ScopePolicies();
		try {
			scopePolicies.setApiKey(dao.getApiKey());
			scopePolicies.setClientID(dao.getClientID());
			scopePolicies.setPackageID(dao.getPackageID());
			scopePolicies.setServiceAPIID(dao.getServiceAPIID());
			scopePolicies.setPackageParams(dao.getPackageParamsMap());
			scopePolicies.setPolicyList(dao.getPolicyListReal());
			scopePolicies.setStatus(dao.getStatusMap());
		} catch(JSONException e) {
			throw new DataBaseProcessingException("DB Object(From DBMS) has invalid attributes, " + e.getMessage());
		}

		return scopePolicies;
	}

	@Deprecated // cliendID와  apiKey가 아닌 clientID와 serviceAPIID로 찾아야 한다. 
	@Override
	public Map<String, String> getStatus(byte[] clientID, String apiKey) throws ValidateException, DataBaseProcessingException {

		String status = myBatisScopeManagerDaoMapper.selectStatus(apiKey, clientID);

		if(status != null) {
			try {
				JSONObject obj = new JSONObject(status);
				Map<String, String> ret = new HashMap<String, String>();
				@SuppressWarnings("unchecked")
				Iterator<Object> keys = obj.keys();
				while(keys.hasNext()) {
					Object key = keys.next();
					if(key != null) {
						if(key instanceof java.lang.String) {
							String keyInString = key.toString();
							ret.put(keyInString, obj.getString(keyInString));
						}
					}
				}
				return ret;
			} catch(JSONException e) {
				throw new DataBaseProcessingException("DB Object(From DBMS) has invalid attributes, value->" + status + ", " + e.getMessage());
			}
		}

		return null;
	}

	@Override
	public void modifyStatus(String apiKey, byte[] clientID, Map<String, String> status) throws ValidateException, DataBaseProcessingException {
		if(status == null) {
			return;
		}

		JSONObject statusInJSON = new JSONObject();
		try {
			for(Entry<String, String> entry : status.entrySet()) {
				statusInJSON.put(entry.getKey(), entry.getValue());
			}
			myBatisScopeManagerDaoMapper.updateStatus(apiKey, clientID, statusInJSON.toString());
		} catch(JSONException e) {
			throw new DataBaseProcessingException("DB Object generating failed," + e.getMessage());
		}
	}

	@Override
	public List<String> getScopeList(byte[] clientID) {
		
		List<String> list = myBatisScopeManagerDaoMapper.selectScopeList(clientID);
		return list;
	}
}
