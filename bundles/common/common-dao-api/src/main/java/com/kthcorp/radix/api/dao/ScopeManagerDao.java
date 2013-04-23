package com.kthcorp.radix.api.dao;

import com.kthcorp.radix.domain.scope.ScopePoliciesDAO;

public interface ScopeManagerDao {
	
	void insertScopePolicies(ScopePoliciesDAO scopePoliciesDAO);
	
	ScopePoliciesDAO selectScopePolicies(String apiKey, int clientID);
	
	String selectStatus(String apiKey, int clientID);
	
	void updateStatus(String apiKey, int clientID, String status);
	
	void deleteScopePolicies(int clientID);
	
	void deleteScopePolicies(int clientID, int packageID);
	
	void deleteScopePolicies(String apiKey, int clientID);
	
	void deleteScopePolicies(String apiKey, int clientID, int packageID);
	
}