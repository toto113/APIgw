package com.kthcorp.radix.service;

import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import com.kthcorp.radix.util.UuidViewableLoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import com.kthcorp.radix.api.service.PolicyManagerService;
import com.kthcorp.radix.dao.mybatis.MyBatisPackageManagerDaoMapper;
import com.kthcorp.radix.dao.mybatis.MyBatisPolicyManagerDaoMapper;
import com.kthcorp.radix.dao.mybatis.MyBatisServiceManagerDaoMapper;
import com.kthcorp.radix.domain.exception.ValidateException;
import com.kthcorp.radix.domain.policy.Policy;
import com.kthcorp.radix.domain.policy.PolicyType;
import com.kthcorp.radix.util.UUIDUtils;

public class PolicyManagerServiceImpl implements PolicyManagerService {
	
	private static final Logger LOG = UuidViewableLoggerFactory.getLogger(PolicyManagerServiceImpl.class);
	
	@Autowired
	private MyBatisPolicyManagerDaoMapper myBatisPolicyManagerDao;
	
	@Autowired
	private MyBatisPackageManagerDaoMapper myBatisPackageManagerDao;
	
	@Autowired
	private MyBatisServiceManagerDaoMapper myBatisServiceManagerDao;
	
	@Override
	public String createPolicy(PolicyType policyType) throws ValidateException {
		this.validate(policyType, false);
		myBatisPolicyManagerDao.insertPolicyType(policyType);
		return policyType.getId();
	}
	
	@Override
	public void createPackagePolicy(final Policy policy) throws ValidateException, NoSuchAlgorithmException {
		if(policy == null) {
			throw new ValidateException("policy does not setted");
		}
		if(policy.getId() == null) {
			policy.generateID();
		}
		if(policy.getPackageID() == null || policy.getPolicyTypeID() == null) {
			throw new ValidateException("packageID or policyID does not found");
		}
		if(this.myBatisPackageManagerDao.selectExistsPackageCount(policy.getPackageID()) < 1) {
			throw new ValidateException("packageID is invalid");
		}
		if(this.myBatisPolicyManagerDao.selectExistsPolicyTypeCount(policy.getPolicyTypeID()) < 1) {
			throw new ValidateException("policyID is invalid");
		}
		if(LOG.isDebugEnabled()) {
			LOG.debug("add package policy::"+policy.toString());
		}
		this.myBatisPolicyManagerDao.insertPackagePolicy(policy);
	}
	
	@Override
	public void createPackageServiceAPIPolicy(Policy policy) throws ValidateException, NoSuchAlgorithmException {
		if(policy == null) {
			throw new ValidateException("policy does not setted");
		}
		if(policy.getId() == null) {
			policy.generateID();
		}
		if(policy.getPackageID() == null || policy.getPolicyTypeID() == null) {
			throw new ValidateException("policy->packageID or policyID does not found");
		}
		if(policy.getServiceAPIID() == null) {
			throw new ValidateException("policy->serviceAPIId does not found");
		}
		if(this.myBatisPackageManagerDao.selectExistsPackageCount(policy.getPackageID()) < 1) {
			throw new ValidateException("policy->packageID is invalid");
		}
		if(this.myBatisPolicyManagerDao.selectExistsPolicyTypeCount(policy.getPolicyTypeID()) < 1) {
			throw new ValidateException("policy->policyID is invalid");
		}
		if(this.myBatisServiceManagerDao.selectExistsServiceAPICount(policy.getServiceAPIID()) < 1) {
			throw new ValidateException("policy->serviceAPIID is invalid");
		}
		if(LOG.isDebugEnabled()) {
			LOG.debug("add serviceAPI policy::"+policy);
		}
		this.myBatisPolicyManagerDao.insertPackageServiceAPIPolicy(policy);
	}
	
	@Override
	public PolicyType getPolicyType(String policyTypeID)
			throws ValidateException {
		if(!StringUtils.hasText(policyTypeID)) {
			throw new ValidateException("policyTypeID does not found");
		}
		
		return this.myBatisPolicyManagerDao.selectPolicyType(policyTypeID);
	}
	
	@Override
	public boolean modifyPolicy(PolicyType policyType) throws ValidateException {
		if(policyType == null) {
			throw new ValidateException("policyType does not found");
		}
		if(policyType.getId() == null) {
			throw new ValidateException("policyTypeID does not found");
		}
		this.validate(policyType, true);
		
		if(myBatisPolicyManagerDao.modifyPolicyType(policyType) > 0) {
			return true;
		} else {
			return false;
		}
	}
	
	@Override
	public boolean modifyPackageServiceAPIPolicy(byte[] packageID, byte[] serviceAPIID, List<Policy> policyList, boolean forceToDelete) throws ValidateException, NoSuchAlgorithmException {
		if(policyList==null||policyList.size() == 0) {
			int ret = this.myBatisPolicyManagerDao.deletePackageServiceAPIPolicys(packageID, serviceAPIID);
			if(ret>0) {
				return true;
			} else {
				return false;
			}
		}
		List<Policy> oldPolicyList = this.myBatisPolicyManagerDao.selectPackageServiceAPIPolicyList(packageID, serviceAPIID);
		
		Map<String,Policy> oldPolicyMap = this.getPolicyMap(oldPolicyList);
		Map<String,Policy> policyMap = this.getPolicyMap(policyList);
		
		for(Entry<String,Policy> entry: policyMap.entrySet()) {
			
			Policy policy = entry.getValue();
			if(policy.getPackageID()==null) {
				policy.setPackageID(packageID);
			}
			if(policy.getServiceAPIID()==null) {
				policy.setServiceAPIID(serviceAPIID);
			}
			
			if(oldPolicyMap.containsKey(entry.getKey())) {
				// Exists Entry
				this.modifyPackageServiceAPIPolicy(policy);
			} else {
				// New Entry
				this.createPackageServiceAPIPolicy(policy);
			}
			oldPolicyMap.remove(entry.getKey());
		}
		
		// Remain Entry
		if(oldPolicyMap.size()>0) {
			for(Entry<String,Policy> entry: oldPolicyMap.entrySet()) {
				this.removePackageServiceAPIPolicy(packageID, serviceAPIID, UUIDUtils.getBytes(entry.getKey()), forceToDelete);
			}
		}
		
		return true;
	}
	
	@Override
	public boolean modifyPackageServiceAPIPolicy(Policy policy) throws ValidateException {
		if(policy == null) {
			throw new ValidateException("policy does not found");
		}
		if(policy.getPackageID() == null || policy.getPolicyTypeID() == null) {
			throw new ValidateException("packageID or policyTypeID does not found");
		}
		if(policy.getServiceAPIID() == null) {
			throw new ValidateException("serviceAPIId does not found");
		}
		if(policy.getId() == null) {
			throw new ValidateException("policy->id does not found");
		}
		if(this.myBatisPackageManagerDao.selectExistsPackageCount(policy.getPackageID()) < 1) {
			throw new ValidateException("packageID is invalid");
		}
		if(this.myBatisPolicyManagerDao.selectExistsPolicyTypeCount(policy.getPolicyTypeID()) < 1) {
			throw new ValidateException("policyID is invalid");
		}
		if(this.myBatisPolicyManagerDao.selectExistsPackageServiceAPIPolicyCount(policy.getPackageID(), policy.getServiceAPIID(), policy.getId()) == null) {
			throw new ValidateException("package->serviceAPI->policy does not exists");
		}
		
		int ret = this.myBatisPolicyManagerDao.modifyPackageServiceAPIPolicy(policy);
		
		if(LOG.isDebugEnabled()) {
			LOG.debug("modify serviceAPI policy::"+policy.toString()+", affectedRecord->"+ret);
		}
		
		if(ret>0) {
			return true;
		} else {
			return false;
		}
	}
	
	@Override
	public boolean modifyPackagePolicy(byte[] packageID, List<Policy> policyList, boolean forceToDelete) throws ValidateException, NoSuchAlgorithmException {
		if(policyList==null||policyList.size() == 0) {
			int ret = this.myBatisPolicyManagerDao.deleteAllPackagePolicy(packageID);
			if(ret>0) {
				return true;
			} else {
				return false;
			}
		}
		List<Policy> oldPolicyList = this.myBatisPolicyManagerDao.selectPackagePolicy(packageID);
		
		Map<String,Policy> oldPolicyMap = this.getPolicyMap(oldPolicyList);
		Map<String,Policy> policyMap = this.getPolicyMap(policyList);
		
		for(Entry<String,Policy> entry: policyMap.entrySet()) {
			
			Policy policy = entry.getValue();
			if(policy.getPackageID()==null) {
				policy.setPackageID(packageID);
			}
			
			if(oldPolicyMap.containsKey(entry.getKey())) {
				// Exists Entry
				this.modifyPackagePolicy(entry.getValue());
			} else {
				// New Entry
				this.createPackagePolicy(entry.getValue());
			}
			oldPolicyMap.remove(entry.getKey());
		}
		
		// Remain Entry
		if(oldPolicyMap.size()>0) {
			for(Entry<String,Policy> entry: oldPolicyMap.entrySet()) {
				this.removePackagePolicy(packageID, UUIDUtils.getBytes(entry.getKey()), forceToDelete);
			}
		}
		
		return true;
	}
	
	@Override
	public boolean modifyPackagePolicy(Policy policy) throws ValidateException {
		if(policy.getPackageID() == null || policy.getId() == null) {
			throw new ValidateException("packageID or policy->ID does not found");
		}
		if(policy.getPolicyTypeID() == null) {
			throw new ValidateException("policyTypeID does not found");
		}
		if(this.myBatisPackageManagerDao.selectExistsPackageCount(policy.getPackageID()) < 1) {
			throw new ValidateException("packageID is invalid");
		}
		if(this.myBatisPolicyManagerDao.selectExistsPolicyTypeCount(policy.getPolicyTypeID()) < 1) {
			throw new ValidateException("policyID is invalid");
		}
		if(this.myBatisPolicyManagerDao.selectExistsPackagePolicyCount(policy.getPackageID(), policy.getId()) < 1) {
			throw new ValidateException("package->policy does not exists");
		}

		int ret = this.myBatisPolicyManagerDao.modifyPackagePolicy(policy);

		if(LOG.isDebugEnabled()) {
			LOG.debug("modify Package policy::"+policy.toString()+", affectedRecord->"+ret);
		}
		
		if(ret>0) {
			return true;
		} else {
			return false;
		}
		
	}
	
	@Override
	public boolean removePolicy(String policyTypeID, boolean force) throws ValidateException {
		if(policyTypeID == null) {
			throw new ValidateException("businessPlatformID or policyTypeID does not found");
		}
		if(force) {
			if(myBatisPolicyManagerDao.selectExistsPolicyTypeCount(policyTypeID) < 1) {
				throw new ValidateException("policy ID is invalid");
			}
			if(myBatisPolicyManagerDao.deletePolicyType(policyTypeID) > 0) {
				return true;
			} else {
				return false;
			}
		} else {
			if(myBatisPolicyManagerDao.selectExistsPolicyTypeCount(policyTypeID) < 1) {
				throw new ValidateException("policy ID is invalid");
			}
			if(myBatisPolicyManagerDao.backupPolicyType(policyTypeID) > 0) {
				if(myBatisPolicyManagerDao.deletePolicyType(policyTypeID) > 0) {
					return true;
				} else {
					return false;
				}
			} else {
				return false;
			}
		}
	}
	
	@Override
	public boolean removePackagePolicy(byte[] packageID, byte[] policyID, boolean force) throws ValidateException {

		if(packageID == null) {
			throw new ValidateException("packageID does not found");
		}
		if(policyID == null) {
			throw new ValidateException("policyID does not found");
		}
		if(this.myBatisPackageManagerDao.selectExistsPackageCount(packageID) < 1) {
			throw new ValidateException("packageID is invalid");
		}
		if(this.myBatisPolicyManagerDao.selectExistsPackagePolicyCount(packageID, policyID) < 1) {
			throw new ValidateException("policyID is invalid");
		}

		int ret = 0;
		if(force) {
			ret = this.myBatisPolicyManagerDao.deletePackagePolicy(packageID, policyID);
		} else {
			this.myBatisPolicyManagerDao.backupPackagePolicy(packageID, policyID);
			ret = this.myBatisPolicyManagerDao.deletePackagePolicy(packageID, policyID);
		}
		
		if(LOG.isDebugEnabled()) {
			LOG.debug("remove package policy(packageID->"+packageID+",policyID->"+policyID+",force->"+force+"),affectRecord->"+ret);
		}
		
		if(ret>0) {
			return true;
		} else {
			return false;
		}

	}
	
	@Override
	public boolean removePackageServiceAPIPolicy(byte[] packageID, byte[] serviceAPIID, byte[] policyID, boolean force) throws ValidateException {

		if(packageID == null) {
			throw new ValidateException("packageID does not found");
		}
		if(serviceAPIID == null) {
			throw new ValidateException("serviceAPIID does not found");
		}
		if(policyID == null) {
			throw new ValidateException("policyID does not found");
		}
		if(this.myBatisPackageManagerDao.selectExistsPackageCount(packageID) < 1) {
			throw new ValidateException("packageID is invalid");
		}
		if(this.myBatisPolicyManagerDao.selectExistsPackageServiceAPIPolicyCount(packageID, serviceAPIID, policyID) < 1) {
			throw new ValidateException("policyID is invalid");
		}

		int ret = 0;
		if(force) {
			ret = this.myBatisPolicyManagerDao.deletePackageServiceAPIPolicy(packageID, serviceAPIID, policyID);
		} else {
			this.myBatisPolicyManagerDao.backupPackageServiceAPIPolicy(packageID, serviceAPIID, policyID);
			ret = this.myBatisPolicyManagerDao.deletePackageServiceAPIPolicy(packageID, serviceAPIID, policyID);
		}
		
		if(LOG.isDebugEnabled()) {
			LOG.debug("remove serviceAPI policy(packageID->"+packageID+",serviceAPIID->"+serviceAPIID+",policyID->"+policyID+",force->"+force+"),affectRecord->"+ret);
		}
		
		if(ret>0) {
			return true;
		} else {
			return false;
		}

	}
	
	private Map<String,Policy> getPolicyMap(List<Policy> policies) {
		if(policies==null||policies.size()<=0) return null;
		
		Map<String,Policy> ret = new HashMap<String,Policy>();
		for(Policy policy: policies) {
			ret.put(UUIDUtils.getString(policy.getId()), policy);
		}
		return ret;
	}
	
	private void validate(PolicyType policyType, boolean checkIfExists) throws ValidateException {
		if(policyType == null) {
			throw new ValidateException("policy does not found");
		}
		if(!StringUtils.hasText(policyType.getName())) {
			throw new ValidateException("policy's name does not found");
		}
		if(checkIfExists == true) {
			if(policyType.getId() == null || policyType.getId() == null) {
				throw new ValidateException("policy ID does not found");
			}
		}
	}
	
	@SuppressWarnings("unused")
	private void validate(Policy policy, boolean checkIfExists) throws ValidateException {
		if(policy == null) {
			throw new ValidateException("policy does not found");
		}
		if(!StringUtils.hasText(policy.getName())) {
			throw new ValidateException("policy's name does not found");
		}
		if(checkIfExists == true) {
			if(policy.getId() == null || policy.getId() == null) {
				throw new ValidateException("policy ID does not found");
			}
			if(myBatisPolicyManagerDao.selectExistsPolicyTypeCount(policy.getPolicyTypeID()) < 1) {
				throw new ValidateException("policy ID is invalid");
			}
		}
		if(policy.getPolicyTypeID() == null) {
			throw new ValidateException("policy's type does not found");
		}
	}
}
