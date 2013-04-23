package com.kthcorp.radix.component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import com.kthcorp.radix.util.UuidViewableLoggerFactory;
import org.springframework.util.MultiValueMap;

import com.kthcorp.radix.api.service.ServiceManagerService;
import com.kthcorp.radix.domain.canonical.CanonicalMessage;
import com.kthcorp.radix.domain.canonical.CanonicalMessagePropertyName;
import com.kthcorp.radix.domain.canonical.request.OrchestratorRestRequest;
import com.kthcorp.radix.domain.canonical.request.ServiceControllerRestRequest;
import com.kthcorp.radix.domain.exception.RadixException;
import com.kthcorp.radix.domain.service.api.protocol.http.ClientProtocol;
import com.kthcorp.radix.domain.service.api.protocol.http.HttpMethod;
import com.kthcorp.radix.domain.service.api.protocol.http.ParameterMap;
import com.kthcorp.radix.domain.service.api.protocol.http.ServerProtocol;
import com.kthcorp.radix.domain.service.api.serviceAPI.ServiceAPI;
import com.kthcorp.radix.domain.service.routing.RoutingMethod;
import com.kthcorp.radix.domain.service.routing.RoutingMethodType;
import com.kthcorp.radix.domain.service.routing.direct.DirectMethod;
import com.kthcorp.radix.domain.service.routing.RoutingMethodSelector;

public class MessageConstructorComponent {

	private final Logger LOG = UuidViewableLoggerFactory.getLogger(this.getClass());

	private ServiceManagerService serviceManagerService;

	public void setServiceManagerService(ServiceManagerService serviceManagerService) {
		this.serviceManagerService = serviceManagerService;
	}

	public CanonicalMessage construct(CanonicalMessage canonicalMessage) throws RadixException {

		LOG.trace(">>> MessageConstructorComponent.contruct({})", canonicalMessage);

		ServiceControllerRestRequest request = (ServiceControllerRestRequest) canonicalMessage.getPayload().getRequest();
		String key = request.getKey();
		ParameterMap parameters = request.getParameters();
		String resourcePath = request.getResroucePath();
		ParameterMap headers = request.getHeaders();
		MultiValueMap<String, Object> body = request.getBody();
		String requestedUri = request.getResroucePath();

		LOG.debug(">>> ServiceControllerRequest={}", request);
		LOG.debug("key={}", key);
		LOG.debug("parameters={}", parameters);
		LOG.debug("resourcePath={}", resourcePath);
		if(parameters != null) {
			for(String parameterKey : parameters.keys())
				LOG.debug("{}={}", parameterKey, parameters.get(parameterKey));
		}
		LOG.debug("headers={}", headers);
		if(headers != null) {
			for(String headerKey : headers.keys())
				LOG.debug("{}={}", headerKey, headers.get(headerKey));
		}


		String uri = null;
		String contentType = null;
		HttpMethod method = null;
		String protocolType = null;

		List<RoutingMethod> routingMethods = serviceManagerService.getRoutingMethods(key);
		RoutingMethod routingMethod = RoutingMethodSelector.selectRoutingMethod(routingMethods, request.getResroucePath());
		
		if(routingMethod == null) {
			throw new RadixException("RoutingMethod not found. key={}", key);
		}
		LOG.trace("found routingMethod. key={}, routingMethod={}", key, routingMethod);

		// path parameter를 여기서 구하는 이유는 등록된 서비스에 포함된 pathTemplate이 필요하기 때문.
		// pathTemplate 예 : "/MapAPI/1/map/abc/{arg1}/A{arg2}"
		// 요 값을 알아야 실제 uri에서 path parameter를 뽑아낼 수 있다.
		String pathTemplate = getPathTemplate(routingMethod);
		Map<? extends String, ? extends List<String>> pathParameterMap = InnerUtil.getPathParameterMap(pathTemplate, request.getResroucePath());
		parameters.putAll(pathParameterMap);
		
		
		LOG.debug("routingMethodType={}", routingMethod.getRoutingMethodType());
		if(routingMethod.getRoutingMethodType() == RoutingMethodType.DIRECT) {
			DirectMethod directMethod = (DirectMethod) routingMethod;
			LOG.debug("directMethod={}", directMethod);
			ClientProtocol protocol = (ClientProtocol) directMethod.getPartnerAPI().getProtocolObj();

			protocolType = protocol.getProtocolType().name();
			contentType = protocol.getContentType();
			uri = protocol.getUri();
			method = protocol.getMethod();

			LOG.debug("protocolType={}", protocolType);
			LOG.debug("uri={}", uri);
			LOG.debug("method={}", method);
			LOG.debug("contentType={}", contentType);
		} else {
			throw new RadixException(String.format("ReoutingMethodType %s is not supported", routingMethod.getRoutingMethodType()));
		}

		canonicalMessage.getHeader().getHeaderProperties().put(CanonicalMessagePropertyName.PROTOCOL, protocolType);

		OrchestratorRestRequest orchestratorRequest = new OrchestratorRestRequest();
		orchestratorRequest.setRequestedUri(requestedUri);
		orchestratorRequest.setKey(key);
		orchestratorRequest.setContentType(contentType);
		orchestratorRequest.setUri(uri);
		orchestratorRequest.setHttpMethod(method);
		orchestratorRequest.setParameters(parameters);
		orchestratorRequest.setHeaders(headers);
		orchestratorRequest.setBody(body);
		canonicalMessage.getPayload().setOrchestratorRequest(orchestratorRequest);

		return canonicalMessage;
	}


	// 테스트를 위해 접근 제한자를 package로 한다.
	static class InnerUtil {

		// 입출력 예
		// 	pathTemplate : "/some/path/arg1/{name1}/arg2/{name2}/pre{name3}"
		// 	resourcePath : "/some/path/arg1/VALUE1/arg2/VALUE2/preVALUE3"
		// 	return : "{ "name1":"VALUE1", "name2":"VALUE2", "name3":"VALUE3" }
		//
		// 반환타입이 이렇게 특이한 것은 ParameterMap로 선언된 다른 map의 putAll()을 사용하려고.
		public static Map<? extends String, ? extends List<String>> getPathParameterMap(String pathTemplate, String resourcePath) throws RadixException {
			
			Map<String, List<String>> parameterMap = new HashMap<String, List<String>>();
			if(pathTemplate==null) { return parameterMap; }
			if(resourcePath==null) { return parameterMap; }

			// query String은 취급하지 않는다. 오직 path만 따지자.
			if(pathTemplate.contains("?")) {
				pathTemplate = pathTemplate.substring(0, pathTemplate.indexOf("?"));
			}
			
			// uri 마지막의 "/"는 무시한다.
			if(pathTemplate.endsWith("/")) { pathTemplate = pathTemplate.substring(0, pathTemplate.length()-1); }
			if(resourcePath.endsWith("/")) { resourcePath = resourcePath.substring(0, resourcePath.length()-1); }
			
			try {

				// pathConstants : [ "/some/path/arg1/", "/arg2/", "pre" ]
				List<String> pathConstants = InnerUtil.findPathConstances(pathTemplate);
				// names : [ "name1", "name2", "name3" ]
				List<String> names = InnerUtil.findParameterNames(pathTemplate);
				// values : [ "VALUE1", "VALUE2", "VALUE3" ]
				List<String> values = InnerUtil.findParameterValues(resourcePath, pathConstants);

				for(int i=0; i<names.size(); i++) {
					List<String> valueList = new ArrayList<String>();
					valueList.add(values.get(i));
					parameterMap.put(names.get(i), valueList);

				}

			} catch(RadixException e) {
				// 요 예외가 던져진 것은 유효하지 않은 값들을 의미한다. cause를 설정할 필요 없다.
				throw new RadixException("invalid path. pathTemplate="+pathTemplate+", resourcePath="+resourcePath);
			} catch(Throwable e) {
				throw new RadixException("invalid path. pathTemplate="+pathTemplate+", resourcePath="+resourcePath, e);
			}

			return parameterMap;
		}

		private static List<String> findPathConstances(String template) throws RadixException {
			List<String> list = new ArrayList<String>();
			if(template==null) { return list; }
			int s, e;
			s = template.indexOf("{");
			if(s==-1) {
				// 파라매터가 없는 경우이다.
				list.add(template);
				return list;
			}
			String constant;
			while(s!=-1) {
				constant = template.substring(0, s);
				list.add(constant);
				e = template.indexOf("}", s);
				if(e==-1) { throw new RadixException(""); }
				template = template.substring(e+1);
				s = template.indexOf("{");
			}
			if(template.length()>0) {
				list.add(template);
			}

			return list;
		}

		private static List<String> findParameterNames(String template) throws RadixException {
			List<String> list = new ArrayList<String>();
			if(template==null) { return list; }
			int s, e;
			s = template.indexOf("{");
			if(s==-1) {
				// 파라매터가 없는 경우이다.
				return list;
			}

			String name;
			while(s!=-1) {
				e = template.indexOf("}", s);
				if(e==-1) { throw new RadixException(""); }
				name = template.substring(s+1, e);
				list.add(name);
				template = template.substring(e+1);
				s = template.indexOf("{");
			}
			return list;
		}


		private static List<String> findParameterValues(String path, List<String> pathConstants) throws RadixException {
			List<String> list = new ArrayList<String>();
			if(path==null) { throw new RadixException(""); }
			int s, e;
			String value;
			boolean isFirst = true;
			for(String constant : pathConstants) {
				s = path.indexOf(constant);
				if(s==-1) { throw new RadixException(""); }
				if((!isFirst&&s==0) || s>0) {
					// isFirst를 따지는 이유는 특이하게 값이 ""인 경우이다. tempalte : "some/{arg1}/other", path : "some//other"
					value = path.substring(0, s);
					list.add(value);
				}

				isFirst = false;
				e = s+constant.length();
				if(e>path.length()) { 
					path=null;
					break; 
				}
				path = path.substring(e);
			}
			if(path!=null) { 
				list.add(path); 
			}

			return list;
		}


	}


	private String getPathTemplate(RoutingMethod routingMethod) throws RadixException {
		if(!(routingMethod instanceof DirectMethod)) {
			throw new RadixException("can't get pathTemplate. the routingMethod is not instance of DirectMethod. routingMethod="+routingMethod);
		}
		ServiceAPI serviceAPI = ((DirectMethod)routingMethod).getServiceAPI();
		ServerProtocol protocol = (ServerProtocol) serviceAPI.getProtocolObj();
		return protocol.getPathTemplate();
	}

}
