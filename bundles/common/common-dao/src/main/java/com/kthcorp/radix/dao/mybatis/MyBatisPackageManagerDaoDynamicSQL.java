package com.kthcorp.radix.dao.mybatis;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import com.kthcorp.radix.util.UuidViewableLoggerFactory;
import org.springframework.util.StringUtils;

import com.kthcorp.radix.domain.packages.Packages;

public class MyBatisPackageManagerDaoDynamicSQL {
	
	private static final Logger LOG = UuidViewableLoggerFactory.getLogger(MyBatisPackageManagerDaoDynamicSQL.class);
	
	public String updatePackageInDynamicSQL(Packages packages) {
		List<String> sql = new ArrayList<String>();
		sql.add("UPDATE");
		sql.add("radix_package");
		sql.add("SET");
		
		List<String> attrs = new ArrayList<String>();
		attrs.add("name");
		attrs.add("updateDate");
		
		if(packages.getIsValid() != null) {
			attrs.add("isValid");
		}
		if(packages.getInvalidStatus() != null) {
			attrs.add("invalidStatus");
		}
		return this.makeUpdateSQL(sql, attrs, "id=#{id}");
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
