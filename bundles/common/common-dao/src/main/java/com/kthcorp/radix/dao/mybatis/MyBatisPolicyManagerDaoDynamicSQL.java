package com.kthcorp.radix.dao.mybatis;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import com.kthcorp.radix.util.UuidViewableLoggerFactory;
import org.springframework.util.StringUtils;

import com.kthcorp.radix.domain.policy.Policy;

public class MyBatisPolicyManagerDaoDynamicSQL {
	
	private static final Logger LOG = UuidViewableLoggerFactory.getLogger(MyBatisPolicyManagerDaoDynamicSQL.class);
	
	public String updatePackagePolicyInDynamicSQL(Policy policy) {
		List<String> sql = new ArrayList<String>();
		sql.add("UPDATE");
		sql.add("radix_package_policy");
		sql.add("SET");
		
		List<String> attrs = new ArrayList<String>();
		attrs.add("properties");
		attrs.add("updateDate");
		
		if(policy.getPolicyTypeID()!=null) {
			attrs.add("policyTypeID");
		}
		if(policy.getName() != null) {
			attrs.add("name");
		}
		if(policy.getIsValid() != null) {
			attrs.add("isValid");
		}
		if(policy.getIsValid() != null) {
			attrs.add("isValid");
		}
		if(policy.getInvalidStatus() != null) {
			attrs.add("invalidStatus");
		}
		return this.makeUpdateSQL(sql, attrs, "packageID=#{packageID} and id=#{id}");
	}
	
	public String updatePackageServiceAPIPolicyListInDynamicSQL(Policy policy) {
		List<String> sql = new ArrayList<String>();
		sql.add("UPDATE");
		sql.add("radix_package_serviceapi_policy");
		sql.add("SET");
		
		List<String> attrs = new ArrayList<String>();
		attrs.add("properties");
		attrs.add("updateDate");
		
		if(policy.getPolicyTypeID()!=null) {
			attrs.add("policyTypeID");
		}
		if(policy.getName() != null) {
			attrs.add("name");
		}
		if(policy.getIsValid() != null) {
			attrs.add("isValid");
		}
		if(policy.getInvalidStatus() != null) {
			attrs.add("invalidStatus");
		}
		return this.makeUpdateSQL(sql, attrs, "packageID=#{packageID} and serviceAPIID=#{serviceAPIID} and id=#{id}");
	}
	
	private String makeUpdateSQL(List<String> sql, List<String> attrs, String whereClause) {
		List<String> vars = new ArrayList<String>();
		for(String attr : attrs) {
			if(attr.equals("regDate")||attr.equals("updateDate")) {
				vars.add(attr + "=NOW()");
			}
			else {
				vars.add(attr + "=#{" + attr + "}");
			}
		}
		sql.add(StringUtils.arrayToCommaDelimitedString(vars.toArray()));
		
		if(whereClause!=null) {
			sql.add("WHERE");
			sql.add(whereClause);
		}
		
		if(LOG.isDebugEnabled()) {
			LOG.debug(StringUtils.arrayToDelimitedString(sql.toArray(), " "));
		}
		
		return StringUtils.arrayToDelimitedString(sql.toArray(), " ");
	}
}
