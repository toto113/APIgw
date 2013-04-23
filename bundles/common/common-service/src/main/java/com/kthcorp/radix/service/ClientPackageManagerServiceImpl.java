package com.kthcorp.radix.service;

import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.json.JSONException;
import org.slf4j.Logger;
import com.kthcorp.radix.util.UuidViewableLoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.kthcorp.radix.api.service.ClientPackageManagerService;
import com.kthcorp.radix.api.service.ScopeManagerService;
import com.kthcorp.radix.dao.mybatis.MyBatisClientKeyManagerDaoMapper;
import com.kthcorp.radix.dao.mybatis.MyBatisClientPackageManagerDaoMapper;
import com.kthcorp.radix.dao.mybatis.MyBatisPackageManagerDaoMapper;
import com.kthcorp.radix.domain.client.CPackages;
import com.kthcorp.radix.domain.exception.DataBaseProcessingException;
import com.kthcorp.radix.domain.exception.ValidateException;
import com.kthcorp.radix.domain.service.YesOrNo;
import com.kthcorp.radix.util.UUIDUtils;

public class ClientPackageManagerServiceImpl implements ClientPackageManagerService {
	
	private enum validateType {
		clientID, packageID
	};
	
	private static final Logger LOG = UuidViewableLoggerFactory.getLogger(ClientPackageManagerServiceImpl.class);
	
	@Autowired
	private MyBatisClientPackageManagerDaoMapper mapper;
	
	@Autowired
	private MyBatisClientKeyManagerDaoMapper clientKeyMapper;
	
	@Autowired
	private MyBatisPackageManagerDaoMapper packageMapper;
	
	@Autowired
	private ScopeManagerService scopeManagerService;
	
	@Override
	public boolean createClientPackage(byte[] clientID, List<CPackages> clientPackageList) throws ValidateException, DataBaseProcessingException, NoSuchAlgorithmException {
		if(clientPackageList == null || clientPackageList.size() < 1) {
			throw new ValidateException("packageList does not found");
		}
		
		for(CPackages packages : clientPackageList) {
			if(mapper.selectExistsCountWithCP(clientID, packages.getPackageID()) > 0) {
				throw new ValidateException("package->("+UUIDUtils.getString(packages.getPackageID()) + ") is already exists");
			}
			this.validate(packages.getPackageID(), validateType.packageID);
		}
		
		boolean isSuccess = true;
		for(CPackages packages : clientPackageList) {
			if(this.createClientPackage(clientID, packages.getPackageID(), packages.getParameters()) == false) {
				isSuccess = false;
			}
		}
		
		if(LOG.isDebugEnabled()) {
			LOG.debug("[createClientPackage] Created, count->" + clientPackageList.size() + ", isSuccess->" + isSuccess);
		}
		
		return isSuccess;
	}
	
	@Override
	public boolean createClientPackage(byte[] clientID, byte[] packageID, String parameters) throws ValidateException, DataBaseProcessingException, NoSuchAlgorithmException {
		this.validate(clientID, validateType.clientID);
		this.validate(packageID, validateType.packageID);
		
		CPackages packages = new CPackages();
		packages.generateID();
		packages.setClientID(clientID);
		packages.setPackageID(packageID);
		try {
			packages.setParameters(parameters);
		} catch(JSONException e) {
			throw new ValidateException("parameter is invalid," + e.getMessage());
		}
		
		int affectedRow = mapper.insertClientPackage(packages);
		
		if(LOG.isDebugEnabled()) {
			LOG.debug("[createClientPackage] clientID->" + UUIDUtils.getString(clientID) + ",packageID->" + UUIDUtils.getString(packageID) + ",parameters->" + parameters + ",affectedRow->" + affectedRow);
		}
		
		if(affectedRow > 0) {
			scopeManagerService.createPackagePolicies(clientID, packageID);
			return true;
		}
		return false;
	}
	
	@Override
	public List<CPackages> getClientPackageList(byte[] clientID) throws ValidateException {
		this.validate(clientID, validateType.clientID);
		
		List<CPackages> packageList = mapper.selectPackageListWithClientID(clientID);
		
		if(LOG.isDebugEnabled()) {
			if(packageList != null) {
				LOG.debug("[getClientPackageList] clientID->" + UUIDUtils.getString(clientID) + ",packageCount->" + packageList.size());
			}
		}
		
		return packageList;
	}
	
	@Override
	public CPackages getClientPackage(byte[] clientID, byte[] packageID) throws ValidateException {
		this.validate(clientID, validateType.clientID);
		this.validate(packageID, validateType.packageID);
		
		CPackages packages = mapper.selectPackageLastOneWithCP(clientID, packageID);
		
		if(LOG.isDebugEnabled()) {
			if(packages != null) {
				LOG.debug("[getClientPackage] clientID->" + UUIDUtils.getString(clientID) + ",packages->" + packages.toString());
			}
		}
		
		return packages;
	}
	
	@Override
	public boolean modifyClientPackage(byte[] clientID, List<CPackages> clientPackageList, boolean forceToDelete) throws ValidateException, DataBaseProcessingException, NoSuchAlgorithmException {
		
		if(clientPackageList == null || clientPackageList.size() < 1) {
			throw new ValidateException("packageList does not found");
		}
		
		this.validate(clientID, validateType.clientID);
		
		for(CPackages packages : clientPackageList) {
			this.validate(packages.getClientID(), validateType.clientID);
			this.validate(packages.getPackageID(), validateType.packageID);
		}
		
		List<CPackages> oldPackageList = mapper.selectPackageListWithClientID(clientID);
		
		Map<String, CPackages> oldPackageMap = this.getPackageMap(oldPackageList);
		Map<String, CPackages> newPackageMap = this.getPackageMap(clientPackageList);
		
		boolean isSuccess = true;
		
		for(Entry<String, CPackages> entry : newPackageMap.entrySet()) {
			if(oldPackageMap.containsKey(entry.getKey())) {
				
				System.err.println("Modify->Package->"+entry.getKey());
				
				// Exists
				if(!this.modifyClientPackage(clientID, entry.getValue().getPackageID(), entry.getValue().getParameters(), forceToDelete)) {
					isSuccess = false;
				}
			} else {
				System.err.println("New->Package->"+entry.getKey());
				// New
				if(!this.createClientPackage(clientID, entry.getValue().getPackageID(), entry.getValue().getParameters())) {
					isSuccess = false;
				}
			}
			oldPackageMap.remove(entry.getKey());
		}
		
		// Remain
		if(oldPackageMap.size() > 0) {
			for(Entry<String, CPackages> entry : oldPackageMap.entrySet()) {
				System.err.println("Remove->Package->"+entry.getKey());

				if(!this.removeClientPackage(clientID, entry.getValue().getPackageID(), forceToDelete)) {
					isSuccess = false;
				}
			}
		}
		
		return isSuccess;
	}
	
	private Map<String, CPackages> getPackageMap(List<CPackages> packageList) {
		if(packageList == null || packageList.size() < 1)
			return null;
		
		Map<String, CPackages> ret = new HashMap<String, CPackages>();
		for(CPackages packages : packageList) {
			ret.put(UUIDUtils.getString(packages.getPackageID()), packages);
		}
		return ret;
	}
	
	@Override
	public boolean modifyClientPackage(byte[] clientID, byte[] packageID, String parameters, boolean forceToDelete) throws ValidateException {
		this.validate(clientID, validateType.clientID);
		this.validate(packageID, validateType.packageID);
		
		CPackages packages = new CPackages();
		packages.setClientID(clientID);
		packages.setPackageID(packageID);
		try {
			packages.setParameters(parameters);
		} catch(JSONException e) {
			throw new ValidateException("parameter is invalid," + e.getMessage());
		}
		
		int affectedRow = mapper.updateClientPackageParameterWithCP(packages);
		
		if(LOG.isDebugEnabled()) {
			LOG.debug("[modifyClientPackage] clientID->" + UUIDUtils.getString(clientID) + ",packageID->" + UUIDUtils.getString(packageID) + ",parameters->" + parameters + ",affectedRow->" + affectedRow);
		}
		
		if(affectedRow > 0) {
			return true;
		}
		return false;
	}
	
	@Override
	public boolean modifyClientPackageValid(byte[] clientID, byte[] packageID, YesOrNo isValid, String invalidStatus, boolean forceToDelete) throws ValidateException {
		this.validate(clientID, validateType.clientID);
		this.validate(packageID, validateType.packageID);
		
		CPackages packages = new CPackages();
		packages.setClientID(clientID);
		packages.setPackageID(packageID);
		packages.setIsValid(isValid);
		packages.setInvalidStatus(invalidStatus);
		
		int affectedRow = mapper.updateClientPackageValidInfoWithCP(packages);
		
		if(LOG.isDebugEnabled()) {
			LOG.debug("[modifyClientPackageValid] clientID->" + UUIDUtils.getString(clientID)  + ",packageID->" + UUIDUtils.getString(packageID)  + ",isValid->" + isValid + ",invalidStatus->" + invalidStatus + ",affectedRow->" + affectedRow);
		}
		
		if(affectedRow > 0) {
			return true;
		}
		return false;
	}
	
	@Override
	public boolean removeClientPackage(byte[] clientID, byte[] packageID, boolean forceToDelete) throws ValidateException {
		this.validate(clientID, validateType.clientID);
		this.validate(packageID, validateType.packageID);
		
		if(mapper.selectExistsCountWithCP(clientID, packageID) < 1) {
			throw new ValidateException(UUIDUtils.getString(packageID) + " does not exists");
		}
		
		if(forceToDelete==false) {
			if(mapper.backupClientPackageWithCP(clientID, packageID)<1) {
				return false;
			}
		}
		int affectedRow = mapper.deleteClientPackageWithCP(clientID, packageID);
		
		if(LOG.isDebugEnabled()) {
			LOG.debug("[removeClientPackage] clientID->" + UUIDUtils.getString(clientID) + ",packageID->" + UUIDUtils.getString(packageID) + ",affectedRow->" + affectedRow);
		}
		
		if(affectedRow > 0) {
			scopeManagerService.removePackagePolicies(clientID, packageID);
			return true;
		}
		return false;
	}
	
	@Override
	public boolean removeClientPackage(byte[] clientID, boolean forceToDelete) throws ValidateException {
		this.validate(clientID, validateType.clientID);
		
		if(mapper.selectExistsCountWithClientID(clientID) < 1) {
			throw new ValidateException("packages does not exists");
		}
		
		if(forceToDelete==false) {
			if(mapper.backupClientPackagesWithClientID(clientID) < 1) {
				return false;
			}
		}
		int affectedRow = mapper.deletePackageWithClientID(clientID);
		
		if(LOG.isDebugEnabled()) {
			LOG.debug("[removeClientPackage] clientID->" + clientID + ",affectedRow->" + affectedRow);
		}
		
		if(affectedRow > 0) {
			return true;
		}
		return false;
	}
	
	@Override
	public boolean removeBackupClientPackage(byte[] clientID, byte[] packageID) throws ValidateException {
		this.validate(clientID, validateType.clientID);
		this.validate(packageID, validateType.packageID);
		
		int affectedRow = mapper.deleteBackupClientPackageWithCP(clientID, packageID);
		if(LOG.isDebugEnabled()) {
			LOG.debug("[removeBackupClientPackage] clientID->" + clientID + ",packageID->" + packageID + ",affectedRow->" + affectedRow);
		}
		
		if(affectedRow > 0) {
			return true;
		}
		return false;
	}
	
	@Override
	public boolean removeBackupClientPackage(byte[] clientID) throws ValidateException {
		this.validate(clientID, validateType.clientID);
		
		int affectedRow = mapper.deleteBackupClientPackagesWithClientID(clientID);
		if(LOG.isDebugEnabled()) {
			LOG.debug("[removeBackupClientPackage] clientID->" + clientID + ",affectedRow->" + affectedRow);
		}
		
		if(affectedRow > 0) {
			return true;
		}
		return false;
	}
	
	private void validate(byte[] id, validateType name) throws ValidateException {
		if(name == null) {
			return;
		}
		if(id == null) {
			throw new ValidateException(name + " does not found");
		}
		;
		if(name == validateType.clientID) {
			if(clientKeyMapper.isExistsWithID(id) <= 0) {
				throw new ValidateException(name + "(" + id + ") does not exists");
			}
		}
		if(name == validateType.packageID) {
			if(packageMapper.selectExistsPackageCount(id) <= 0) {
				throw new ValidateException(name + "(" + id + ") does not exists");
			}
		}
	}
}
