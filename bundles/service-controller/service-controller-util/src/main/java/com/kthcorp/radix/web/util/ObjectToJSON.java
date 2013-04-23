package com.kthcorp.radix.web.util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.kthcorp.radix.domain.exception.ValidateException;
import com.kthcorp.radix.domain.packages.Packages;
import com.kthcorp.radix.domain.policy.Policy;
import com.kthcorp.radix.domain.token.AccessToken;
import com.kthcorp.radix.domain.service.ResourceOwner;
import com.kthcorp.radix.domain.service.Service;
import com.kthcorp.radix.domain.service.YesOrNo;
import com.kthcorp.radix.domain.service.api.partnerAPI.PartnerAPI;
import com.kthcorp.radix.domain.service.api.serviceAPI.ServiceAPI;
import com.kthcorp.radix.domain.service.mapping.MappingInfo;
import com.kthcorp.radix.domain.service.routing.RoutingMethod;
import com.kthcorp.radix.domain.service.routing.RoutingMethodType;
import com.kthcorp.radix.domain.service.routing.direct.DirectMethod;
import com.kthcorp.radix.util.UUIDUtils;

public class ObjectToJSON {
	
	public static JSONObject getAccessToken(AccessToken accessToken) throws JSONException, ValidateException {
		JSONObject obj = new JSONObject();
		
		if( null != accessToken ) {
			obj.put("access_token", accessToken.getTokenID());
//			obj.put("token", accessToken.getToken());
//			obj.put("authentication_id", accessToken.getAuthenticationID());
//			obj.put("authentication", accessToken.getAuthentication());
//			obj.put("refresh_token", accessToken.getRefreshToken());
//			obj.put("client_id", accessToken.getClientID());
//			obj.put("username", accessToken.getUsername());
			obj.put("scope", accessToken.getScope());
			obj.put("application_name", null == accessToken.getApplicationName() ? "" : accessToken.getApplicationName());
			obj.put("application_description", null == accessToken.getApplicationDescription() ? "" : accessToken.getApplicationDescription());
			obj.put("isValid", String.valueOf(accessToken.isValid()));
			obj.put("invalidStatus", null == accessToken.getInvalidStatus() ? "" : accessToken.getInvalidStatus());
			obj.put("expires_in", "");
			obj.put("create_date", null == accessToken.getCreateDate() ? "" : accessToken.getCreateDate());
		}
		
		return obj;
	}
	
	public static JSONObject getService(Service service) throws JSONException, ValidateException {
		JSONObject obj = new JSONObject();
		
		if(service!=null) {
			obj.put("id", UUIDUtils.getString(service.getId()));
			obj.put("businessPlatformID", UUIDUtils.getString(service.getBusinessPlatformID()));			
			obj.put("partnerID", service.getPartnerID());
			
			// Package Policy
			JSONArray apiList = new JSONArray();
			if(service.getApiList()!=null) {
				for(RoutingMethod method: service.getApiList()) {
					apiList.put(getRoutingMethod(method));
				}
			}
			
			obj.put("apiList", apiList);
			
			obj.put("name", service.getName());
			obj.put("version", service.getVersion());
			obj.put("resourceOwner", service.getResourceOwner());
			
			if(ResourceOwner.USER==service.getResourceOwner()) {
				obj.put("resourceAuthUrl", service.getResourceAuthUrl());
			}
			obj.put("isValid", service.getIsValid());
			if(YesOrNo.N==service.getIsValid()) {
				obj.put("invalidStatus", service.getInvalidStatus());
			}
			obj.put("createdDate", service.getRegDate());
			obj.put("lastModifiedDate", service.getUpdateDate());

		}
		
		return obj;
	}
	
	public static JSONObject getRoutingMethod(RoutingMethod methodOrg) throws JSONException, ValidateException {
		JSONObject obj = new JSONObject();
		
		if(methodOrg!=null) {
			
			if(RoutingMethodType.DIRECT == methodOrg.getRoutingMethodType()) {
				
				DirectMethod method = (DirectMethod) methodOrg;
				obj.put("id", UUIDUtils.getString(method.getId()));
				obj.put("businessPlatformID", UUIDUtils.getString(method.getBusinessPlatformID()));
				obj.put("serviceID", UUIDUtils.getString(method.getBusinessPlatformID()));
				
				obj.put("serviceAPIID", UUIDUtils.getString(method.getServiceAPIID()));
				obj.put("partnerAPIID", UUIDUtils.getString(method.getPartnerAPIID()));
				if(method.getParameterMappingID()!=null) {
					obj.put("parameterMappingID", UUIDUtils.getString(method.getParameterMappingID()));
				}
				if(method.getResultMappingID()!=null) {
					obj.put("resultMappingID", UUIDUtils.getString(method.getResultMappingID()));
				}
				
				if(method.getServiceAPI()!=null) {
					obj.put("serviceAPI", _getServiceAPI(method.getServiceAPI()));
				}

				if(method.getPartnerAPI()!=null) {
					obj.put("partnerAPI", _getPartnerAPI(method.getPartnerAPI()));
				}
				
				if(method.getParameterMap()!=null) {
					obj.put("parameterMap", _getMappingInfo(method.getParameterMap()));
				}

				if(method.getResultMap()!=null) {
					obj.put("resultMap", _getMappingInfo(method.getResultMap()));
				}

				obj.put("isValid", method.getIsValid());
				if(YesOrNo.N==method.getIsValid()) {
					obj.put("invalidStatus", method.getInvalidStatus());
				}
				obj.put("createdDate", method.getRegDate());
				obj.put("lastModifiedDate", method.getUpdateDate());
			} else {
				throw new ValidateException(methodOrg.getRoutingMethodType()+" does not support yet");
			}
		}
		
		return obj;
	}
	
	public static JSONObject getPackage(Packages packages) throws JSONException {
		JSONObject obj = new JSONObject();
		
		if(packages!=null) {
			obj.put("id", UUIDUtils.getString(packages.getId()));
			obj.put("businessPlatformID", UUIDUtils.getString(packages.getBusinessPlatformID()));
			obj.put("clientID", UUIDUtils.getString(packages.getClientID()));
			obj.put("partnerID", packages.getPartnerID());
			
			// Package Policy
			JSONArray policyList = new JSONArray();
			if(packages.getPolicyList()!=null) {
				for(Policy policy: packages.getPolicyList()) {
					policyList.put(_getPolicy(policy));
				}
			}
			
			obj.put("policyList", policyList);
			
			// Service APIs
			JSONArray serviceAPIList = new JSONArray();
			if(packages.getServiceApiList()!=null) {
				for(ServiceAPI serviceAPI: packages.getServiceApiList()) {
					serviceAPIList.put(_getServiceAPI(serviceAPI));
				}
			}
			
			obj.put("serviceAPIList", serviceAPIList);
			
			obj.put("name", packages.getName());
			if(packages.getParameters()!=null) {
				obj.put("parameters", new JSONObject(packages.getParameters()));
			}
			obj.put("isValid", packages.getIsValid());
			if(YesOrNo.N==packages.getIsValid()) {
				obj.put("invalidStatus", packages.getInvalidStatus());
			}
			obj.put("createdDate", packages.getRegDate());
			obj.put("lastModifiedDate", packages.getUpdateDate());

		}
		
		return obj;
	}
	
	public static JSONObject _getPartnerAPI(PartnerAPI partnerAPI) throws JSONException {
		JSONObject obj = new JSONObject();
		
		if(partnerAPI!=null) {
			obj.put("id", UUIDUtils.getString(partnerAPI.getId()));
			obj.put("serviceID", UUIDUtils.getString(partnerAPI.getServiceID()));
			
			obj.put("name", partnerAPI.getName());
			obj.put("transportType", partnerAPI.getTransportType());
			obj.put("protocolType", partnerAPI.getProtocolType());
			obj.put("defaultTransformType", partnerAPI.getDefaultTransformType());
			
			if(partnerAPI.getProtocolMeta()!=null) {
				obj.put("protocolMeta", new JSONObject(partnerAPI.getProtocolMeta()));
			}
			
			if(partnerAPI.getParameters()!=null) {
				obj.put("parameters", new JSONArray(partnerAPI.getParameters()));
			}

			obj.put("isValid", partnerAPI.getIsValid());
			if(YesOrNo.N==partnerAPI.getIsValid()) {
				obj.put("invalidStatus", partnerAPI.getInvalidStatus());
			}
			obj.put("createdDate", partnerAPI.getRegDate());
			obj.put("lastModifiedDate", partnerAPI.getUpdateDate());
		}
		
		return obj;
	}
	
	public static JSONObject _getMappingInfo(MappingInfo mappingInfo) throws JSONException {
		JSONObject obj = new JSONObject();
		
		if(mappingInfo!=null) {
			obj.put("id", UUIDUtils.getString(mappingInfo.getId()));
			obj.put("serviceID", UUIDUtils.getString(mappingInfo.getServiceID()));
			obj.put("serviceAPIID", UUIDUtils.getString(mappingInfo.getServiceAPIID()));
			obj.put("partnerAPIID", UUIDUtils.getString(mappingInfo.getPartnerAPIID()));
			
			obj.put("mappingType", mappingInfo.getMappingType());
			if(mappingInfo.getMapping()!=null) {
				obj.put("mapping", new JSONObject(mappingInfo.getMapping()));
			}

			obj.put("isValid", mappingInfo.getIsValid());
			if(YesOrNo.N==mappingInfo.getIsValid()) {
				obj.put("invalidStatus", mappingInfo.getInvalidStatus());
			}
			obj.put("createdDate", mappingInfo.getRegDate());
			obj.put("lastModifiedDate", mappingInfo.getUpdateDate());
		}
		
		return obj;
	}
	
	public static JSONObject _getServiceAPI(ServiceAPI serviceAPI) throws JSONException {
		JSONObject obj = new JSONObject();
		
		if(serviceAPI!=null) {
			obj.put("id", UUIDUtils.getString(serviceAPI.getId()));
			obj.put("serviceID", UUIDUtils.getString(serviceAPI.getServiceID()));
			if(serviceAPI.getApiKey()!=null) {
				obj.put("apiKey", serviceAPI.getApiKey());
			}
			
			obj.put("name", serviceAPI.getName());
			obj.put("routingMethodType", serviceAPI.getRoutingMethodType());
			obj.put("transportType", serviceAPI.getTransportType());
			obj.put("protocolType", serviceAPI.getProtocolType());
			obj.put("defaultTransformType", serviceAPI.getDefaultTransformType());
			
			if(serviceAPI.getProtocolMeta()!=null) {
				obj.put("protocolMeta", new JSONObject(serviceAPI.getProtocolMeta()));
			}
			
			if(serviceAPI.getParameters()!=null) {
				obj.put("parameters", new JSONArray(serviceAPI.getParameters()));
			}
			
			// Policy
			JSONArray policyList = new JSONArray();
			if(serviceAPI.getPolicyList()!=null) {
				for(Policy policy: serviceAPI.getPolicyList()) {
					policyList.put(_getPolicy(policy));
				}
			}
			obj.put("policyList", policyList);

			obj.put("isValid", serviceAPI.getIsValid());
			if(YesOrNo.N==serviceAPI.getIsValid()) {
				obj.put("invalidStatus", serviceAPI.getInvalidStatus());
			}
			obj.put("createdDate", serviceAPI.getRegDate());
			obj.put("lastModifiedDate", serviceAPI.getUpdateDate());
		}
		
		return obj;
	}
	
	public static JSONObject _getPolicy(Policy policy) throws JSONException {
		JSONObject obj = new JSONObject();
		
		if(policy!=null) {
			obj.put("id", UUIDUtils.getString(policy.getId()));
			obj.put("businessPlatformID", UUIDUtils.getString(policy.getBusinessPlatformID()));
			obj.put("packageID", UUIDUtils.getString(policy.getPackageID()));
			obj.put("partnerID", policy.getPartnerID());
			
			if(policy.getServiceAPIID()!=null) {
				obj.put("serviceAPIID", UUIDUtils.getString(policy.getServiceAPIID()));
			}
			obj.put("name", policy.getName());
			obj.put("policyTypeID", policy.getPolicyTypeID());
			if(policy.getProperties()!=null) {
				obj.put("properties", new JSONObject(policy.getProperties()));
			}
			obj.put("isValid", policy.getIsValid());
			if(YesOrNo.N==policy.getIsValid()) {
				obj.put("invalidStatus", policy.getInvalidStatus());
			}
			obj.put("createdDate", policy.getRegDate());
			obj.put("lastModifiedDate", policy.getUpdateDate());
		}
		
		return obj;
	}
}
