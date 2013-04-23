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

import com.kthcorp.radix.api.service.PackageManagerService;
import com.kthcorp.radix.api.service.PolicyManagerService;
import com.kthcorp.radix.dao.mybatis.MyBatisPackageManagerDaoMapper;
import com.kthcorp.radix.dao.mybatis.MyBatisPolicyManagerDaoMapper;
import com.kthcorp.radix.dao.mybatis.MyBatisServiceManagerDaoMapper;
import com.kthcorp.radix.domain.exception.ValidateException;
import com.kthcorp.radix.domain.packages.Packages;
import com.kthcorp.radix.domain.policy.Policy;
import com.kthcorp.radix.domain.service.api.serviceAPI.ServiceAPI;
import com.kthcorp.radix.util.UUIDUtils;

public class PackageManagerServiceImpl implements PackageManagerService {
	
	private static final Logger LOG = UuidViewableLoggerFactory.getLogger(PackageManagerServiceImpl.class);
	
	@Autowired
	private MyBatisPackageManagerDaoMapper myBatisPackageManagerDao;
	
	@Autowired
	private MyBatisPolicyManagerDaoMapper myBatisPolicyManagerDao;
	
	@Autowired
	private MyBatisServiceManagerDaoMapper myBatisServiceManagerDao;
	
	@Autowired
	private PolicyManagerService policyManagerService;
	
	@Override
	public byte[] createPackage(Packages packages) throws ValidateException, NoSuchAlgorithmException {
		this.validate(packages, false);
		
		// RuntimeException throw if error
		packages.generateID();
		this.myBatisPackageManagerDao.insertPackage(packages);
		for(ServiceAPI serviceAPI : packages.getServiceApiList()) {
			this.myBatisPackageManagerDao.insertPackageServiceAPI(packages.getId(), serviceAPI.getId());
			if(serviceAPI.getPolicyList() != null) {
				for(Policy policy : serviceAPI.getPolicyList()) {
					policy.generateID();
					policy.setPackageID(packages.getId());
					policy.setServiceAPIID(serviceAPI.getId());
					
					this.myBatisPolicyManagerDao.insertPackageServiceAPIPolicy(policy);
				}
			}
		}
		for(Policy policy : packages.getPolicyList()) {
			policy.generateID();
			policy.setPackageID(packages.getId());
			
			this.myBatisPolicyManagerDao.insertPackagePolicy(policy);
		}
		return packages.getId();
	}
	
	@Override
	public void createServiceAPI(byte[] packageID, ServiceAPI serviceAPI) throws ValidateException, NoSuchAlgorithmException {
		this.validate(serviceAPI);
		
		if(this.myBatisPackageManagerDao.selectExistsPackageCount(packageID) < 1) {
			throw new ValidateException("packageID is invalid");
		}
		
		if(LOG.isDebugEnabled()) {
			LOG.debug("add service api(packageID->" + packageID + ",serviceAPIID->" + serviceAPI.getId() + ")");
		}
		
		this.myBatisPackageManagerDao.insertPackageServiceAPI(packageID, serviceAPI.getId());
		if(serviceAPI.getPolicyList() != null) {
			for(Policy policy : serviceAPI.getPolicyList()) {
				policy.generateID();
				policy.setPackageID(packageID);
				policy.setServiceAPIID(serviceAPI.getId());
				
				policyManagerService.createPackageServiceAPIPolicy(policy);
			}
		}
	}
	
	@Override
	public Packages getPackage(final String partnerID, final byte[] packageID) throws ValidateException {
		
		if(partnerID == null || partnerID.length() == 0) {
			throw new ValidateException("input partnerID is null or size zero");
		}
		if(packageID == null) {
			throw new ValidateException("input packageID is invalid");
		}
		
		// if(this.myBatisPackageManagerDao.selectExistsPackageCount(packageID) < 1) {
		if(this.myBatisPackageManagerDao.selectExistsPackageCountWithPartnerID(packageID, partnerID) < 1) {
			throw new ValidateException("packageID with partnerID is invalid");
		}
		
		// Packages packages = this.myBatisPackageManagerDao.selectPackage(packageID);
		Packages packages = this.myBatisPackageManagerDao.selectPackageWithPartnerID(partnerID, packageID);
		if(packages != null) {
			if(packages.getId() != null) {
				extractServiceAPIAndPolicyList(packages);
			}
		}
		return packages;
	}
	
	private void extractServiceAPIAndPolicyList(Packages packages) {
		List<ServiceAPI> serviceAPIs = this.myBatisPackageManagerDao.selectServiceAPIList(packages.getId());
		
		if(serviceAPIs != null) {
			if(serviceAPIs.size() > 0) {
				for(int i = 0, l = serviceAPIs.size(); i < l; i++) {
					ServiceAPI serviceAPI = serviceAPIs.get(i);
					if(serviceAPI != null) {
						if(serviceAPI.getId() != null) {
							serviceAPI.setPolicyList(this.myBatisPolicyManagerDao.selectPackageServiceAPIPolicyList(packages.getId(), serviceAPI.getId()));
							if(LOG.isDebugEnabled()) {
								LOG.debug("serviceAPI("+UUIDUtils.getString(serviceAPI.getId())+")->getPolicies("+((serviceAPI.getPolicyList()!=null) ? serviceAPI.getPolicyList().size():"0")+")");
							}
						}
					}
					serviceAPIs.set(i, serviceAPI);
				}
			}
			
			packages.setServiceApiList(serviceAPIs);
		}
		packages.setPolicyList(this.myBatisPolicyManagerDao.selectPackagePolicy(packages.getId()));
	}
	
	@Override
	public List<Packages> getPackageList(byte[] businessPlatformID, String partnerID) {
		
		List<Packages> packagesList = this.myBatisPackageManagerDao.selectPartnerPackageList(businessPlatformID, partnerID);
		
		for(Packages packages : packagesList) {
			if(packages.getId() != null) {
				extractServiceAPIAndPolicyList(packages);
			}
		}
		
		return packagesList;
	}
	
	@Override
	public void modifyPackage(Packages packages, boolean forceToDelete) throws ValidateException, NoSuchAlgorithmException {
		this.validate(packages, true);
		
		byte[] packageID = packages.getId();
		
		// Package
		this.myBatisPackageManagerDao.updatePackage(packages);
		
		// Service API
		List<ServiceAPI> oldServiceAPIs = this.myBatisPackageManagerDao.selectServiceAPIList(packageID);
		List<ServiceAPI> serviceAPIs = packages.getServiceApiList();
		
		Map<String, ServiceAPI> oldServiceAPIMap = this.getServiceAPIMap(oldServiceAPIs);
		Map<String, ServiceAPI> serviceAPIMap = this.getServiceAPIMap(serviceAPIs);
		
		for(Entry<String, ServiceAPI> entry : serviceAPIMap.entrySet()) {
			if(oldServiceAPIMap.containsKey(entry.getKey())) {
				// Exists
				policyManagerService.modifyPackageServiceAPIPolicy(packageID, UUIDUtils.getBytes(entry.getKey()), entry.getValue().getPolicyList(), forceToDelete);
			} else {
				// New Service API
				this.createServiceAPI(packageID, entry.getValue());
			}
			oldServiceAPIMap.remove(entry.getKey());
		}
		
		// Remain Entry
		if(oldServiceAPIMap.size() > 0) {
			for(Entry<String, ServiceAPI> entry : oldServiceAPIMap.entrySet()) {
				this.removePackageServiceAPI(packageID, UUIDUtils.getBytes(entry.getKey()), forceToDelete);
			}
		}
		
		// Package Policy
		policyManagerService.modifyPackagePolicy(packageID, packages.getPolicyList(), forceToDelete);
	}
	
	private Map<String, ServiceAPI> getServiceAPIMap(List<ServiceAPI> serviceAPIs) {
		if(serviceAPIs == null || serviceAPIs.size() <= 0)
			return null;
		
		Map<String, ServiceAPI> ret = new HashMap<String, ServiceAPI>();
		for(ServiceAPI serviceAPI : serviceAPIs) {
			ret.put(UUIDUtils.getString(serviceAPI.getId()), serviceAPI);
		}
		return ret;
	}
	
	@Override
	public boolean removePackage(byte[] businessPlatformID, String partnerID, byte[] packageID, boolean forceToDelete) throws ValidateException {
		if(businessPlatformID == null) {
			throw new ValidateException("businessPlatformIDdoes not found");
		}
		if(StringUtils.hasText(partnerID) == false) {
			throw new ValidateException("partnerID does not found");
		}
		if(packageID == null) {
			throw new ValidateException("packageID does not found");
		}
		if(forceToDelete) {
			if(this.myBatisPackageManagerDao.selectExistsPackageWithHiddenCount(packageID) < 1) {
				throw new ValidateException("packageID is invalid");
			}
			if(this.myBatisPackageManagerDao.deletePackage(businessPlatformID, partnerID, packageID) > 0) {
				return true;
			} else {
				return false;
			}
		} else {
			if(this.myBatisPackageManagerDao.selectExistsPackageCount(packageID) < 1) {
				throw new ValidateException("packageID is invalid");
			}
			
			List<ServiceAPI> serviceAPIs = this.myBatisPackageManagerDao.selectServiceAPIList(packageID);
			if(serviceAPIs != null) {
				if(serviceAPIs.size() > 0) {
					for(ServiceAPI serviceAPI : serviceAPIs) {
						this.removePackageServiceAPI(packageID, serviceAPI.getId(), forceToDelete);
					}
				}
			}
			
			this.myBatisPolicyManagerDao.backupAllPackagePolicy(packageID);
			this.myBatisPolicyManagerDao.deleteAllPackagePolicy(packageID);
			
			if(this.myBatisPackageManagerDao.backupPackage(businessPlatformID, partnerID, packageID) > 0) {
				if(this.myBatisPackageManagerDao.deletePackage(businessPlatformID, partnerID, packageID) > 0) {
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
	public boolean removePackageServiceAPI(byte[] packageID, byte[] serviceAPIID, boolean forceToDelete) throws ValidateException {
		
		if(packageID == null) {
			throw new ValidateException("packageID does not found");
		}
		if(serviceAPIID == null) {
			throw new ValidateException("serviceAPIID does not found");
		}
		if(this.myBatisPackageManagerDao.selectExistsPackageCount(packageID) < 1) {
			throw new ValidateException("packageID is invalid");
		}
		int ret = 0;
		if(forceToDelete) {
			this.myBatisPolicyManagerDao.deletePackageServiceAPIPolicys(packageID, serviceAPIID);
			ret = this.myBatisPackageManagerDao.deletePackageServiceAPI(packageID, serviceAPIID);
		} else {
			this.myBatisPolicyManagerDao.backupPackageServiceAPIPolicys(packageID, serviceAPIID);
			this.myBatisPolicyManagerDao.deletePackageServiceAPIPolicys(packageID, serviceAPIID);
			this.myBatisPackageManagerDao.backupPackageServiceAPI(packageID, serviceAPIID);
			ret = this.myBatisPackageManagerDao.deletePackageServiceAPI(packageID, serviceAPIID);
		}
		
		if(LOG.isDebugEnabled()) {
			LOG.debug("remove serviceAPI(packageID->" + packageID + ",serviceAPIID->" + serviceAPIID + ",forceToDelete->" + forceToDelete + ",affectRecord->" + ret);
		}
		
		if(ret > 0) {
			return true;
		} else {
			return false;
		}
		
	}
	
	private void validate(Packages packages, boolean checkIfExists) throws ValidateException {
		
		if(packages == null) {
			throw new ValidateException("package information does not found");
		}
		if(packages.getBusinessPlatformID() == null) {
			throw new ValidateException("business Platform ID does not found");
		}
		if(checkIfExists == true) {
			if(packages.getId() == null || packages.getId() == null) {
				throw new ValidateException("packageID does not found");
			}
			if(this.myBatisPackageManagerDao.selectExistsPackageCount(packages.getId()) < 1) {
				throw new ValidateException("packageID is invalid");
			}
		}
		if(!StringUtils.hasText(packages.getName())) {
			throw new ValidateException("name does not found");
		}
		if(!StringUtils.hasText(packages.getPartnerID())) {
			throw new ValidateException("partner ID does not found");
		}
		if(packages.getServiceApiList() == null || packages.getServiceApiList().size() < 1) {
			throw new ValidateException("service apis does not found");
		}
		
		for(ServiceAPI serviceAPI : packages.getServiceApiList()) {
			this.validate(serviceAPI);
		}
		
		if(packages.getPolicyList() == null || packages.getPolicyList().size() < 1) {
			throw new ValidateException("package's policy not found");
		}
		
		for(Policy policy : packages.getPolicyList()) {
			if(!StringUtils.hasText(policy.getName())) {
				throw new ValidateException("policy(package->"+policy.getPolicyTypeID()+")->name does not found");
			}
			if(policy.getPolicyTypeID() == null) {
				throw new ValidateException("policyTypeID does not found");
			}
			if(this.myBatisPolicyManagerDao.selectExistsPolicyTypeCount(policy.getPolicyTypeID()) < 1) {
				throw new ValidateException("policyTypeID(" + policy.getPolicyTypeID() + ") is invalid");
			}
		}
	}
	
	private void validate(ServiceAPI serviceAPI) throws ValidateException {
		if(serviceAPI == null) {
			throw new ValidateException("serviceAPI is null");
		}
		if(serviceAPI.getId() == null || serviceAPI.getId() == null) {
			throw new ValidateException("serviceAPI ID does not found. serviceAPI="+serviceAPI);
		}
		if(myBatisServiceManagerDao.selectExistsServiceAPICount(serviceAPI.getId()) < 1) {
			throw new ValidateException("service not exist in this service. serviceAPI="+serviceAPI);
		}
		if(serviceAPI.getPolicyList() != null) {
			for(Policy policy : serviceAPI.getPolicyList()) {
				if(!StringUtils.hasText(policy.getName())) {
					throw new ValidateException("policy(serviceAPI:"+UUIDUtils.getString(serviceAPI.getId())+"->"+policy.getPolicyTypeID()+")->name does not found. serviceAPI="+serviceAPI);
				}
				if(policy.getPolicyTypeID() == null) {
					throw new ValidateException("policyTypeID does not found. serviceAPI="+serviceAPI);
				}
				if(this.myBatisPolicyManagerDao.selectExistsPolicyTypeCount(policy.getPolicyTypeID()) < 1) {
					throw new ValidateException("policyTypeID(" + policy.getPolicyTypeID() + ") is invalid. serviceAPI="+serviceAPI);
				}
			}
		}
		if(serviceAPI.getPolicyList() == null || serviceAPI.getPolicyList().size() < 1) {
			throw new ValidateException("serviceAPI's policy not found. serviceAPI="+serviceAPI);
		}
	}
	
}
