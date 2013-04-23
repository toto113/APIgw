package com.kthcorp.radix.puddingto;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.codec.Base64;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;



public class ServiceRegistrator {

	// 아래의 두 값은 DB를 생성할 때 넣은 값이다. 한번 생성한 후에 계속 사용하고 있다. 그냥 사용한다.
	private final String BUSINESS_PLATFORM_ID = "59ffa6f4-0901-4ffc-82ad-44687540ab4b";
	private final String BUSINESS_PLATFORM_SECRET = "21334d4ffb7994f5094eb41b5a70dd3a165780f9";
	
	// 위 2값을 가지고 생성한다. 인증하기 위한 값.
	private final String BASIC_AUTH_VALUE = new String(Base64.encode((BUSINESS_PLATFORM_ID+":"+BUSINESS_PLATFORM_SECRET).getBytes()));
	
	// 요 값은 등록할 때 정해도 된다. 그리고 정한 값을 가지고 다른 곳에서 사용한다.
	// 그런데 2012/08/03 현재 기존에 개발된 것들이 "puddingto_admin"을 사용하고 있다.
	private final String PARTNER_ID = "puddingto_admin";
	
	// 도메인 이름이고.
	private final String URL_BASE = "https://api.pudding.to";
	
	
	private String getServiceId(String basicAuthValue, String partnerId) throws Exception {
		
		JSONObject serviceInJson = getJSONObjectOfService(basicAuthValue, partnerId);
		if(serviceInJson==null) { 
			return null;
		}

		/*
			{
		    	"body": {"service_0": {
		        	"businessPlatformID": "59ffa6f4-0901-4ffc-82ad-44687540ab4b",
		        	"createdDate": "Fri Jul 06 10:00:10 UTC 2012",
		        	"id": "5f2453c4-c751-11e1-ade2-12313f062e84",			
		        	"apiList": [ ]
		 */ 			
		JSONObject body = serviceInJson.getJSONObject("body");
		if(!body.has("service_0")) {
			return null;
		}
		String serviceId = body.getJSONObject("service_0").getString("id");

		return serviceId;
		
	}
	
	
	private void unregisterOldService(String basicAuthValue, String partnerId) throws Exception {
		
		String oldServiceId = null;
		try {
			System.out.println("trying to get old serviceId");
			oldServiceId = getServiceId(basicAuthValue, partnerId);
			if(oldServiceId==null) {
				System.out.println("no old service. nothing to unregister");
				return;
			}
			System.out.println("got old serviceId. serviceId="+oldServiceId);
		} catch (Exception e) {
			throw new Exception("failed to get old serviceId", e);
		}

		
		RestTemplate restTemplate = new RestTemplate();
		
		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.add("Authorization", "Basic "+basicAuthValue);
		
		Map<String, String> uriVariables = new Hashtable<String, String>();
		MultiValueMap<String, Object> requestBody = new LinkedMultiValueMap<String, Object>();
		
		HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<MultiValueMap<String, Object>>(requestBody, requestHeaders);
		String uri = URL_BASE + "/platform/services/"+oldServiceId;
		ResponseEntity<byte[]> responseEntity = restTemplate.exchange(uri , HttpMethod.DELETE, requestEntity, byte[].class, uriVariables);
		JSONObject jsonObject = InnerUtil.parseIntoJsonObject(responseEntity);
		System.out.println(jsonObject.toString(4));
		
	}
	
	
	private void regsiterService(String basicAuthValue, String partnerId, String serviceDescription) throws Exception {
		
		RestTemplate restTemplate = new RestTemplate();
		
		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.add("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
		requestHeaders.add("Authorization", "Basic "+basicAuthValue);
		
		Map<String, String> uriVariables = new Hashtable<String, String>();
		MultiValueMap<String, Object> requestBody = new LinkedMultiValueMap<String, Object>();
		requestBody.add("service_description", serviceDescription);
		requestBody.add("partner_id", partnerId);
		
		HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<MultiValueMap<String, Object>>(requestBody, requestHeaders);
		String uri = URL_BASE + "/platform/services";
		ResponseEntity<byte[]> responseEntity = restTemplate.exchange(uri , HttpMethod.POST, requestEntity, byte[].class, uriVariables);
		JSONObject jsonObject = InnerUtil.parseIntoJsonObject(responseEntity);
		System.out.println(jsonObject.toString(4));
		
	}
	
	
	private List<String> getPackageIdList(String basicAuthValue, String partnerId) throws Exception {
		
		JSONObject packageInJson = getJSONObjectOfPackage(basicAuthValue, partnerId);
		if(packageInJson==null) { 
			return null;
		}

		/*
			{
			    "body": {"package_0": {
			        "businessPlatformID": "59ffa6f4-0901-4ffc-82ad-44687540ab4b",
			        "createdDate": "Fri Jul 06 10:09:44 UTC 2012",
			        "id": "b57a9274-c752-11e1-ade2-12313f062e84",
		 */ 			
		JSONObject body = packageInJson.getJSONObject("body");
		
		List<String> packageIdList = new ArrayList<String>();
		int i=0;
		while(true) {
			String pacakgeIndexingName = "package_"+i;
			if(!body.has(pacakgeIndexingName)) {
				break;
			}
			String packageId = body.getJSONObject(pacakgeIndexingName).getString("id");
			packageIdList.add(packageId);
			i++;
		}

		return packageIdList;
		
	}


	private void removeOldPackage(String basicAuthValue, String partnerId) throws Exception {
		
		List<String> oldPacakgeIdList = null;
		try {
			System.out.println("trying to get old packageId");
			oldPacakgeIdList = getPackageIdList(basicAuthValue, partnerId);
			if(oldPacakgeIdList==null||oldPacakgeIdList.isEmpty()) {
				System.out.println("no old package. nothing to remove");
				return;
			}
			System.out.println("got old packageId. packageId="+oldPacakgeIdList);
		} catch (Exception e) {
			throw new Exception("failed to get old packageId", e);
		}
		
		for(String packageId : oldPacakgeIdList) {
			removeOldPackage(basicAuthValue, partnerId, packageId);
		}
		

		
		
	}
	


	private void removeOldPackage(String basicAuthValue, String partnerId, String packageId) throws Exception {

		RestTemplate restTemplate = new RestTemplate();
		
		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.add("Authorization", "Basic "+basicAuthValue);
		
		Map<String, String> uriVariables = new Hashtable<String, String>();
		MultiValueMap<String, Object> requestBody = new LinkedMultiValueMap<String, Object>();
		
		HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<MultiValueMap<String, Object>>(requestBody, requestHeaders);
		String uri = URL_BASE + "/platform/packages/"+packageId+"?partner_id="+partnerId;
		ResponseEntity<byte[]> responseEntity = restTemplate.exchange(uri , HttpMethod.DELETE, requestEntity, byte[].class, uriVariables);
		JSONObject jsonObject = InnerUtil.parseIntoJsonObject(responseEntity);
		System.out.println(jsonObject.toString(4));
		
	}


	private void createPackage(String basicAuthValue, String partnerId) throws Exception {
		
		String[] apiIds = null;
		try {
			apiIds = getApiIds(basicAuthValue, partnerId);
		} catch(Exception e) {
			throw new Exception("getting apiIds failed.", e);
		}
		
		if(apiIds==null) {
			System.out.println("no registered service.");
			return;
		}
		
		System.out.println("DEV : apiIds="+apiIds);
		
		Policy[] policies = new Policy[3];
		policies[0] = new Policy("puddingto_for_internal", 5000, 100000);
		policies[1] = new Policy("puddingto_for_normal", 1000, 20000);
		policies[2] = new Policy("puddingto_for_preminum", 2000, 40000);

		for(Policy policy : policies) {
			String packageDescription = generatePackageDescription(policy, apiIds);
			System.out.println(packageDescription);
			createPackage(basicAuthValue, partnerId, packageDescription);
		}
		
	}
	

	private void createPackage(String basicAuthValue, String partnerId, String packageDescription) throws Exception {
		
		RestTemplate restTemplate = new RestTemplate();
		
		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.add("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
		requestHeaders.add("Authorization", "Basic "+basicAuthValue);
		
		Map<String, String> uriVariables = new Hashtable<String, String>();
		MultiValueMap<String, Object> requestBody = new LinkedMultiValueMap<String, Object>();
		requestBody.add("package_description", packageDescription);
		requestBody.add("partner_id", partnerId);
		
		HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<MultiValueMap<String, Object>>(requestBody, requestHeaders);
		String uri = URL_BASE + "/platform/packages";
		ResponseEntity<byte[]> responseEntity = restTemplate.exchange(uri , HttpMethod.POST, requestEntity, byte[].class, uriVariables);
		JSONObject jsonObject = InnerUtil.parseIntoJsonObject(responseEntity);
		System.out.println(jsonObject.toString(4));
		
	}


	private class Policy {
		String PackgageName;
		int limitPerApi;
		int limitPerPackage;
		public Policy(String PackgageName, int limitPerApi, int limitPerPackage) {
			this.PackgageName = PackgageName;
			this.limitPerApi = limitPerApi;
			this.limitPerPackage = limitPerPackage;
		}
	}
	
	private String generatePackageDescription(Policy policy, String[] apiIds) {
	
		String packageDescriptionTemplate 
			= "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
			+ "<package name=\"PACKAGE_NAME\">\n"
			+ "\n"
			+ "	<apis>\n"
			+ "API_POLICIES"
			+ "	</apis>\n"
			+ "\n"
			+ "	<policies>\n"
			+ "		<usageLimit name=\"MONTYLY\">\n"
			+ "		<duration>1M</duration>\n"
			+ "		<maxCount>LIMIT_PER_PACKAGE</maxCount>\n"
			+ "		<condition>absolute</condition>\n"
			+ "		</usageLimit>\n"  
			+ "	</policies>\n"
			+ ""
			+ "</package>\n";

		String apiPolicyTemplate
			= "		<api id=\"API_ID\">\n"
			+ "			<policies>\n"
			+ "				<usageLimit name=\"DAILY\">\n"
			+ "					<duration>1D</duration>\n"
			+ "					<maxCount>LIMIT_PER_API</maxCount>\n"
			+ "					<condition>absolute</condition>\n"
			+ "				</usageLimit>\n"
			+ "			</policies>\n"
			+ "		</api>\n";
		
		StringBuilder sb = new StringBuilder();
		for(String apiId : apiIds) {
			String apiPolicy = apiPolicyTemplate;
			apiPolicy = apiPolicy.replace("API_ID", apiId);
			apiPolicy = apiPolicy.replace("LIMIT_PER_API", ""+policy.limitPerApi);
			sb.append(apiPolicy);
		}
		String apiPolcies = sb.toString();
		
		String packageDescription = packageDescriptionTemplate.replace("API_POLICIES", apiPolcies);
		packageDescription = packageDescription.replace("PACKAGE_NAME", ""+policy.PackgageName);
		packageDescription = packageDescription.replace("LIMIT_PER_PACKAGE", ""+policy.limitPerPackage);
		
		return packageDescription;
	}
	

	private JSONObject getJSONObjectOfService(String basicAuthValue, String partnerId) throws Exception {
		
		RestTemplate restTemplate = new RestTemplate();
		
		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.add("Authorization", "Basic "+basicAuthValue);
		
		Map<String, String> uriVariables = new Hashtable<String, String>();
		MultiValueMap<String, Object> requestBody = new LinkedMultiValueMap<String, Object>();
		
		HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<MultiValueMap<String, Object>>(requestBody, requestHeaders);
		String uri = URL_BASE + "/platform/services?partner_id="+partnerId;
		JSONObject jsonObject = null;
		try {
			ResponseEntity<byte[]> responseEntity = restTemplate.exchange(uri , HttpMethod.GET, requestEntity, byte[].class, uriVariables);
			jsonObject = InnerUtil.parseIntoJsonObject(responseEntity);
			System.out.println(jsonObject.toString(4));
		} catch (HttpClientErrorException e) {
			// 등록된 service가 없는 경우이다. 예외가 아닌 경우로 처리한다.
			return null;
		} catch (Exception e) {
			throw new Exception("getting service info failed.", e);
		}
		
		return jsonObject;
		
	}
	
	
	
	// TODO : getJSONObjectOfService와 크게 중복된다. 중복 제거
	private JSONObject getJSONObjectOfPackage(String basicAuthValue, String partnerId) throws Exception {
		
		RestTemplate restTemplate = new RestTemplate();
		
		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.add("Authorization", "Basic "+basicAuthValue);
		
		Map<String, String> uriVariables = new Hashtable<String, String>();
		MultiValueMap<String, Object> requestBody = new LinkedMultiValueMap<String, Object>();
		
		HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<MultiValueMap<String, Object>>(requestBody, requestHeaders);
		String uri = URL_BASE + "/platform/packages?partner_id="+partnerId;
		JSONObject jsonObject = null;
		try {
			ResponseEntity<byte[]> responseEntity = restTemplate.exchange(uri , HttpMethod.GET, requestEntity, byte[].class, uriVariables);
			jsonObject = InnerUtil.parseIntoJsonObject(responseEntity);
			System.out.println(jsonObject.toString(4));
		} catch (HttpClientErrorException e) {
			// 등록된 package가 없는 경우이다. 예외가 아닌 경우로 처리한다.
			return null;
		} catch (Exception e) {
			throw new Exception("getting package info failed.", e);
		}
		
		return jsonObject;
		
	}
	
	
	private String[] getApiIds(String basicAuthValue, String partnerId) throws Exception {
		
		JSONObject serviceInJson = getJSONObjectOfService(basicAuthValue, partnerId);
		if(serviceInJson==null) {
			return null;
		}
		
		/*
			{
		    	"body": {"service_0": {
		        	"apiList": [
		        		{ 
		        			"id": "5f29f915-c751-11e1-ade2-12313f062e84",	// 요놈이 아니네. 그럼 이놈은 어디 테이블의 id일까
		        			"serviceAPI" : {
		        				"id""a7227d6c-df6d-11e1-a8aa-12313f062e84",
		        				...
		        			},
		        			"partnerAPI": { ... },
		        			"parameterMap": { ... },
		        			"resultMap": { ... },
		        			...
		        		
		        		},
		        		...
		        	]
		 */ 			
		String[] apiIds = null;
		try {
			// TODO : what if multiple service "service_0", "service_1", ...
			JSONArray apiArray = serviceInJson.getJSONObject("body").getJSONObject("service_0").getJSONArray("apiList");
			apiIds = new String[apiArray.length()];
			for(int i=0; i<apiArray.length(); i++) {
				JSONObject api = apiArray.getJSONObject(i);
				apiIds[i] = api.getJSONObject("serviceAPI").getString("id");
			}
		} catch(JSONException e) {
			throw new Exception("parsing api list json failed.", e);
		}

		return apiIds;
		
	}
	
	
	private void confirmNewService() {
		
	}
	
	
	public void processAll() {
		
		String serviceDescription = null;
		try {
			serviceDescription = InnerUtil.loadFile("service_description.xml");
		} catch (IOException e) {
			System.out.println("reading service description file failed.");
			e.printStackTrace();
			return;
		}
		
		
		try {
			System.out.println("trying to unregister old service");
			unregisterOldService(BASIC_AUTH_VALUE, PARTNER_ID);
			System.out.println("unregistering old service success.");
		} catch (Exception e) {
			System.out.println("unregistering old service failed.");
			e.printStackTrace();
			return;
		}

		
		try {
			System.out.println("trying to register new service");
			System.out.println("serviceDescription="+serviceDescription);
			regsiterService(BASIC_AUTH_VALUE, PARTNER_ID, serviceDescription);
			System.out.println("registering new service success.");
		} catch (Exception e) {
			System.out.println("registering new service failed.");
			e.printStackTrace();
			return;
		}
		

		try {
			System.out.println("trying to remove old package");
			removeOldPackage(BASIC_AUTH_VALUE, PARTNER_ID);
			System.out.println("removing old package success.");
		} catch (Exception e) {
			System.out.println("removing old package failed.");
			e.printStackTrace();
			return;
		}
		
		try {
			System.out.println("trying to create package");
			createPackage(BASIC_AUTH_VALUE, PARTNER_ID);
			System.out.println("creating package success.");
		} catch (Exception e) {
			System.out.println("creating package failed.");
			e.printStackTrace();
			return;
		}
		
		confirmNewService();
		
	}
	
	
	
	public static void main(String[] args) {
		ServiceRegistrator serviceRegistrator = new ServiceRegistrator();
		serviceRegistrator.processAll();
		serviceRegistrator.confirmNewService();
	}
	

	
	
	
	private static class InnerUtil {
		
		private static String loadFile(String fileName) throws IOException {
			
			fileName = getFileNameOnClasspath(fileName);
			
			BufferedReader reader = null;
			try {
				reader = new BufferedReader(new InputStreamReader(Thread.currentThread().getContextClassLoader().getResourceAsStream(fileName)));
			} catch(NullPointerException e) {
				throw new IOException("unable to load description file '"+fileName+"'");
			}
			
			StringBuilder sb = new StringBuilder();
			
			String line = null;
			
			while((line = reader.readLine()) != null) {
				sb.append(line).append("\n");
			}
			
			return sb.toString();
		}
		
		public static JSONObject parseIntoJsonObject(ResponseEntity<byte[]> responseEntity) throws Exception {
			String bodyInJsonFormat = null;
			try {
				bodyInJsonFormat = new String(responseEntity.getBody(), "utf-8");
				JSONObject jsonObject = new JSONObject(bodyInJsonFormat);
				HttpStatus httpStatus = responseEntity.getStatusCode();
				if(isFailedStatus(httpStatus)) {
					throw new Exception("execution failed. statusCode="+httpStatus+", message="+jsonObject.toString(4));
				}
				return jsonObject;
			} catch (Exception e) {
				throw new Exception("parsing response body into json failed. body="+bodyInJsonFormat, e);
			}
		}

		private static boolean isFailedStatus(HttpStatus httpStatus) {
			if(HttpStatus.OK==httpStatus) { return false; }
			if(HttpStatus.CREATED==httpStatus) { return false; }
			if(HttpStatus.ACCEPTED==httpStatus) { return false; }
			return true;
		}

	}
	
	
	public static String getFileNameOnClasspath(String fileName) {
		
		File file = new File(fileName);
		if(file.exists()) { return file.getAbsolutePath(); }
		
		if(Thread.currentThread().getContextClassLoader().getResourceAsStream(fileName)!=null) { return fileName; }
		
		String packageName = ServiceRegistrator.class.getPackage().getName();
		String packagePath = packageName.replace(".", "/");
		return packagePath+"/"+fileName;
		
	}
	
	
}
