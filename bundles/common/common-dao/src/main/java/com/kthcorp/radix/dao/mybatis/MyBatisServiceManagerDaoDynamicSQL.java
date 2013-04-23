package com.kthcorp.radix.dao.mybatis;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import com.kthcorp.radix.util.UuidViewableLoggerFactory;
import org.springframework.util.StringUtils;

import com.kthcorp.radix.domain.service.Service;
import com.kthcorp.radix.domain.service.api.partnerAPI.PartnerAPI;
import com.kthcorp.radix.domain.service.api.serviceAPI.ServiceAPI;
import com.kthcorp.radix.domain.service.mapping.MappingInfo;
import com.kthcorp.radix.domain.service.routing.direct.DirectMethod;

public class MyBatisServiceManagerDaoDynamicSQL {
	
	private static final Logger LOG = UuidViewableLoggerFactory.getLogger(MyBatisServiceManagerDaoDynamicSQL.class);
	
	/*
	 * +-------------------------------+ | Tables_in_radix |
	 * +-------------------------------+ | radix_service | |
	 * radix_service_apis_mapping | | radix_service_apis_partnerapi | |
	 * radix_service_apis_serviceapi | | radix_service_routing_direct |
	 * +-------------------------------+
	 */
	
	/* radix_service */
	public String insertServiceInDynamicSQL(Service service) {
		
		List<String> sql = new ArrayList<String>();
		sql.add("INSERT INTO");
		sql.add("radix_service");
		sql.add("(");
		
		List<String> attrs = new ArrayList<String>();
		attrs.add("id");
		attrs.add("businessPlatformID");
		attrs.add("partnerID");
		attrs.add("name");
		attrs.add("version");
		attrs.add("resourceOwner");
		attrs.add("regDate");
		attrs.add("updateDate");
		
		if(StringUtils.hasText(service.getResourceAuthUrl())) {
			attrs.add("resourceAuthUrl");
		}
		if(service.getIsValid() != null) {
			attrs.add("isValid");
		}
		
		sql.add(StringUtils.arrayToCommaDelimitedString(attrs.toArray()));
		sql.add(")");
		sql.add("VALUES");
		sql.add("(");
		
		List<String> vars = new ArrayList<String>();
		for(String attr : attrs) {
			if(attr.equals("regDate")||attr.equals("updateDate")) {
				vars.add("NOW()");
			}
			else {
				vars.add("#{" + attr + "}");
			}
		}
		sql.add(StringUtils.arrayToCommaDelimitedString(vars.toArray()));
		
		sql.add(")");
		
		String sqlString = StringUtils.arrayToDelimitedString(sql.toArray(), " ");
		if(LOG.isDebugEnabled()) {
			LOG.debug("{} : {}", sqlString, service);
		}
		
		return sqlString;
	}
	
	public String insertServiceAPIInDynamicSQL(ServiceAPI serviceAPI) {
		
		List<String> sql = new ArrayList<String>();
		sql.add("INSERT INTO");
		sql.add("radix_service_apis_serviceapi");
		sql.add("(");
		
		List<String> attrs = new ArrayList<String>();
		attrs.add("id");
		attrs.add("serviceID");
		attrs.add("name");
		attrs.add("transportType");
		attrs.add("regDate");
		attrs.add("updateDate");
		
		if(serviceAPI.getDefaultTransformType() != null) {
			attrs.add("defaultTransformType");
		}
		if(serviceAPI.getProtocolType()!=null) {
			attrs.add("protocolType");
		}
		if(serviceAPI.getProtocolMeta()!=null) {
			attrs.add("protocolMeta");
		}
		if(serviceAPI.getParameters() != null) {
			attrs.add("parameters");
		}
		if(serviceAPI.getIsValid() != null) {
			attrs.add("isValid");
		}
		
		sql.add(StringUtils.arrayToCommaDelimitedString(attrs.toArray()));
		sql.add(")");
		sql.add("VALUES");
		sql.add("(");
		
		List<String> vars = new ArrayList<String>();
		for(String attr : attrs) {
			if(attr.equals("regDate")||attr.equals("updateDate")) {
				vars.add("NOW()");
			}
			else {
				vars.add("#{" + attr + "}");
			}
		}
		sql.add(StringUtils.arrayToCommaDelimitedString(vars.toArray()));
		
		sql.add(")");
		
		String sqlString = StringUtils.arrayToDelimitedString(sql.toArray(), " ");
		if(LOG.isDebugEnabled()) {
			LOG.debug("{} : {}", sqlString, serviceAPI);
		}
		
		return sqlString;
	}
	
	public String insertPartnerAPIInDynamicSQL(PartnerAPI partnerAPI) {
		
		List<String> sql = new ArrayList<String>();
		sql.add("INSERT INTO");
		sql.add("radix_service_apis_partnerapi");
		sql.add("(");
		
		List<String> attrs = new ArrayList<String>();
		attrs.add("id");
		attrs.add("serviceID");
		attrs.add("name");
		attrs.add("transportType");
		attrs.add("regDate");
		attrs.add("updateDate");
		
		if(partnerAPI.getDefaultTransformType() != null) {
			attrs.add("defaultTransformType");
		}
		if(partnerAPI.getProtocolType()!=null) {
			attrs.add("protocolType");
		}
		if(partnerAPI.getProtocolMeta()!=null) {
			attrs.add("protocolMeta");
		}
		if(partnerAPI.getParameters() != null) {
			attrs.add("parameters");
		}
		if(partnerAPI.getIsValid() != null) {
			attrs.add("isValid");
		}
		
		sql.add(StringUtils.arrayToCommaDelimitedString(attrs.toArray()));
		sql.add(")");
		sql.add("VALUES");
		sql.add("(");
		
		List<String> vars = new ArrayList<String>();
		for(String attr : attrs) {
			if(attr.equals("regDate")||attr.equals("updateDate")) {
				vars.add("NOW()");
			}
			else {
				vars.add("#{" + attr + "}");
			}
		}
		sql.add(StringUtils.arrayToCommaDelimitedString(vars.toArray()));
		
		sql.add(")");
		
		String sqlString = StringUtils.arrayToDelimitedString(sql.toArray(), " ");
		if(LOG.isDebugEnabled()) {
			LOG.debug("{} : {}", sqlString, partnerAPI);
		}
		
		return sqlString;
		
	}
	
	public String insertDirectRoutingInDynamicSQL(DirectMethod directMethod) {
		
		List<String> sql = new ArrayList<String>();
		sql.add("INSERT INTO");
		sql.add("radix_service_routing_direct");
		sql.add("(");
		
		List<String> attrs = new ArrayList<String>();
		attrs.add("id");
		attrs.add("serviceID");
		attrs.add("serviceAPIID");
		attrs.add("partnerAPIID");
		attrs.add("regDate");
		attrs.add("updateDate");
		
		if(directMethod.getParameterMappingID() != null) {
			attrs.add("parameterMappingID");
		}
		if(directMethod.getResultMappingID()!=null) {
			attrs.add("resultMappingID");
		}
		if(directMethod.getIsValid() != null) {
			attrs.add("isValid");
		}
		
		sql.add(StringUtils.arrayToCommaDelimitedString(attrs.toArray()));
		sql.add(")");
		sql.add("VALUES");
		sql.add("(");
		
		List<String> vars = new ArrayList<String>();
		for(String attr : attrs) {
			if(attr.equals("regDate")||attr.equals("updateDate")) {
				vars.add("NOW()");
			}
			else {
				vars.add("#{" + attr + "}");
			}
		}
		sql.add(StringUtils.arrayToCommaDelimitedString(vars.toArray()));
		
		sql.add(")");
		
		String sqlString = StringUtils.arrayToDelimitedString(sql.toArray(), " ");
		if(LOG.isDebugEnabled()) {
			LOG.debug("{} : {}", sqlString, directMethod);
		}
		
		return sqlString;
		
	}
	
	public String updateServiceInDynamicSQL(Service service) {
		List<String> sql = new ArrayList<String>();
		sql.add("UPDATE");
		sql.add("radix_service");
		sql.add("SET");
		
		List<String> attrs = new ArrayList<String>();
		attrs.add("businessPlatformID");
		attrs.add("updateDate");
		
		if(service.getPartnerID() != null) {
			attrs.add("partnerID");
		}
		if(service.getName() != null) {
			attrs.add("name");
		}
		if(service.getVersion() != null) {
			attrs.add("version");
		}
		if(service.getResourceOwner() != null) {
			attrs.add("resourceOwner");
		}
		if(StringUtils.hasText(service.getResourceAuthUrl())) {
			attrs.add("resourceAuthUrl");
		}
		if(service.getIsValid() != null) {
			attrs.add("isValid");
		}
		
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
		
		sql.add("WHERE");
		
		sql.add("id=#{id}");
		
		
		String sqlString = StringUtils.arrayToDelimitedString(sql.toArray(), " ");
		if(LOG.isDebugEnabled()) {
			LOG.debug("{} : {}", sqlString, service);
		}
		
		return sqlString;
		
	}
	
	/* radix_service_apis_serviceapi */
	public String updateServiceAPIInDynamicSQL(ServiceAPI serviceAPI) {
		
		List<String> sql = new ArrayList<String>();
		sql.add("UPDATE");
		sql.add("radix_service_apis_serviceapi");
		sql.add("SET");
		
		List<String> attrs = new ArrayList<String>();
		attrs.add("updateDate");
		
		if(serviceAPI.getTransportType() != null) {
			attrs.add("transportType");
		}
		if(serviceAPI.getDefaultTransformType() != null) {
			attrs.add("defaultTransformType");
		}
		if(serviceAPI.getProtocolType() != null) {
			attrs.add("protocolType");
		}
		if(serviceAPI.getParameters() != null) {
			attrs.add("parameters");
		}
		if(StringUtils.hasText(serviceAPI.getProtocolMeta())) {
			attrs.add("protocolMeta");
		}
		if(serviceAPI.getIsValid() != null) {
			attrs.add("isValid");
		}
		
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
		
		sql.add("WHERE");
		
		if(serviceAPI.getId() != null) {
			sql.add("id=#{id}");
		} else {
			// sql.add("serviceID=#{serviceID} and name=#{name}");
			sql.add("serviceID=#{serviceID}");
		}
		
		String sqlString = StringUtils.arrayToDelimitedString(sql.toArray(), " ");
		if(LOG.isDebugEnabled()) {
			LOG.debug("{} : {}", sqlString, serviceAPI);
		}
		
		return sqlString;
		
	}
	
	/* radix_service_apis_partnerapi */
	public String updatePartnerAPIInDynamicSQL(PartnerAPI partnerAPI) {
		List<String> sql = new ArrayList<String>();
		sql.add("UPDATE");
		sql.add("radix_service_apis_partnerapi");
		sql.add("SET");
		
		List<String> attrs = new ArrayList<String>();
		attrs.add("updateDate");
		
		if(partnerAPI.getTransportType() != null) {
			attrs.add("transportType");
		}
		if(partnerAPI.getDefaultTransformType() != null) {
			attrs.add("defaultTransformType");
		}
		if(partnerAPI.getProtocolType() != null) {
			attrs.add("protocolType");
		}
		if(partnerAPI.getParameters() != null) {
			attrs.add("parameters");
		}
		if(StringUtils.hasText(partnerAPI.getProtocolMeta())) {
			attrs.add("protocolMeta");
		}
		if(partnerAPI.getIsValid() != null) {
			attrs.add("isValid");
		}
		
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
		
		sql.add("WHERE");
		
		if(partnerAPI.getId() != null) {
			sql.add("id=#{id}");
		} else {
			// sql.add("serviceID=#{serviceID} and name=#{name}");
			sql.add("serviceID=#{serviceID}");
		}
		
		String sqlString = StringUtils.arrayToDelimitedString(sql.toArray(), " ");
		if(LOG.isDebugEnabled()) {
			LOG.debug("{} : {}", sqlString, partnerAPI);
		}
		
		return sqlString;
		
	}
	
	public String updateDirectRoutingInDynamicSQL(DirectMethod directMethod) {
		
		List<String> sql = new ArrayList<String>();
		sql.add("UPDATE");
		sql.add("radix_service_routing_direct");
		sql.add("SET");
		
		List<String> attrs = new ArrayList<String>();
		attrs.add("updateDate");

		if(directMethod.getIsValid() != null) {
			attrs.add("isValid");
		}
		
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
		
		sql.add("WHERE");
		
		if(directMethod.getId() != null) {
			sql.add("id=#{id}");
		} else {
			sql.add("serviceID=#{serviceID} and serviceAPIID=#{serviceAPIID} and partnerAPIID=#{partnerAPIID}");
		}
		
		String sqlString = StringUtils.arrayToDelimitedString(sql.toArray(), " ");
		if(LOG.isDebugEnabled()) {
			LOG.debug("{} : {}", sqlString, directMethod);
		}
		
		return sqlString;
		
	}
	
	public String updateAPIMappingInDynamicSQL(MappingInfo updateMappingInfo) {
		List<String> sql = new ArrayList<String>();
		sql.add("UPDATE");
		sql.add("radix_service_apis_mapping");
		sql.add("SET");
		
		List<String> attrs = new ArrayList<String>();
		attrs.add("updateDate");
		
		if(updateMappingInfo.getMappingType() != null) {
			attrs.add("mappingType");
		}
		if(updateMappingInfo.getMapping() != null) {
			attrs.add("mapping");
		}
		if(updateMappingInfo.getIsValid() != null) {
			attrs.add("isValid");
		}
		
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
		
		sql.add("WHERE");
		
		if(updateMappingInfo.getId() != null) {
			sql.add("id=#{id}");
		} else {
			sql.add("serviceID=#{serviceID} and serviceAPIID=#{serviceAPIID} and partnerAPIID=#{partnerAPIID}");
		}
		
		String sqlString = StringUtils.arrayToDelimitedString(sql.toArray(), " ");
		if(LOG.isDebugEnabled()) {
			LOG.debug("{} : {}", sqlString, updateMappingInfo);
		}
		
		return sqlString;
		
	}
}
