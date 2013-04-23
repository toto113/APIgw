package com.kthcorp.radix.domain.service.routing;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.kthcorp.radix.domain.service.api.protocol.http.ServerProtocol;
import com.kthcorp.radix.domain.service.routing.direct.DirectMethod;
import com.kthcorp.radix.util.JsonBuilder;

public class RoutingMethodSelector {

	public static RoutingMethod selectRoutingMethod(List<RoutingMethod> routingMethodList, String resourcePath) {
		if(routingMethodList==null) { return null; }
		if(resourcePath==null) { return null; }
		
		Map<String, RoutingMethod> routingMethodMap = new HashMap<String, RoutingMethod>();
		List<String> pathTemplateList = new ArrayList<String>();
		for(RoutingMethod routingMethod : routingMethodList) {
			ServerProtocol serverProtocol = (ServerProtocol)((DirectMethod)routingMethod).getServiceAPI().getProtocolObj();
			String pathTemplate = serverProtocol.getPathTemplate();
			if(pathTemplate.contains("?")) {
				pathTemplate = pathTemplate.substring(0, pathTemplate.indexOf("?"));
			}
			pathTemplateList.add(pathTemplate);
			routingMethodMap.put(pathTemplate, routingMethod);
		}
		DynamicResourceIndicator indicator = new DynamicResourceIndicator(pathTemplateList);
		
		String selectedPathTemplate = indicator.parsePath(resourcePath);
		return routingMethodMap.get(selectedPathTemplate);
		
	}

	private static enum SeperatorType {
		Static, Variable
	}
	
	private static class UriSeperator<T> {

		private String api;
		private String name;
		private Map<String, UriSeperator<?>> childs;
		private SeperatorType seperatorType;
		private boolean hasVariableChild = false;
		private UriSeperator<?> variableChild;
		
		public UriSeperator(String name, SeperatorType seperatorType) {
			this.name = name;
			this.seperatorType = seperatorType;
			childs = new HashMap<String, UriSeperator<?>>();
		}
		
		public void setApi(String api) {
			this.api = api;
		}
		
		public String getApi() {
			return this.api;
		}
		
		@SuppressWarnings("unused")
		public void setName(String name) {
			this.name = name;
		}
		
		@SuppressWarnings("unused")
		public String getName() {
			return name;
		}
		
		@SuppressWarnings("unused")
		public void setSeperatorType(SeperatorType seperatorType) {
			this.seperatorType = seperatorType;
		}
		
		public SeperatorType getSeperatorType() {
			return this.seperatorType;
		}
		
		public boolean hasChild() {
			return childs.size() > 0;
		}
		
		public boolean hasChild(String key) {
			return childs.containsKey(key);
		}
		
		public boolean hasVariableChild() {
			return hasVariableChild;
		}
		
		public void putChild(String key, UriSeperator<?> child) {
			this.childs.put(key, child);
			if(child.getSeperatorType() == SeperatorType.Variable) {
				this.variableChild = child;
				this.hasVariableChild = Boolean.TRUE;
			}
		}
		
		public UriSeperator<?> getChild(String key) {
			return this.childs.get(key);
		}
		
		public UriSeperator<?> getVariableChild() {
			return variableChild;
		}
		
		public Map<String, UriSeperator<?>> getChilds() {
			return this.childs;
		}
		
		
		@Override
		public String toString() {
			JsonBuilder jb = new JsonBuilder();
			jb.put("class", this.getClass());
			jb.put("name", name);
			jb.put("api", api);
			jb.put("seperatorType", seperatorType);
			jb.put("childs", childs);
			jb.put("variableChild", variableChild);
			return jb.toString();
		}
		
	}


	// 테스트를 위해 접근제한자를 package private으로 설정한다.
	static class DynamicResourceIndicator {
		
		private List<String> apis;
		public void setApis(List<String> apis) {
			this.apis = apis;
		}
		
		private Map<String, UriSeperator<?>> map;
		
		public DynamicResourceIndicator() {		
			init();
		}
		
		public DynamicResourceIndicator(List<String> apis) {
			if(apis==null) { apis = new ArrayList<String>(); }
			this.apis = new ArrayList<String>();
			for(String api : apis) {
				if(api==null) { continue; }
				if(api.contains("?")) {
					api = api.substring(0, api.indexOf("?"));
				}
				this.apis.add(api);
			}
			init();
		}
		
		private void init() {
			map = new HashMap<String, UriSeperator<?>>();
			loadApis();
		}
		
		private void loadApis() {
			if(apis==null) { return; }
			for(String api : apis) {
				if(api==null) { continue; }
				LinkedList<String> list = new LinkedList<String>();
				list.addAll(Arrays.asList(api.split("/")));
				list.remove("");
				if(list.size()==0) { return; }
				String key = list.remove(0);
				UriSeperator<?> seperator;
				if(map.containsKey(key)) {
					seperator = map.get(key);
				} else {
					SeperatorType seperatorType = key.startsWith("{") ? SeperatorType.Variable : SeperatorType.Static;
					seperator = new UriSeperator<String>(key, seperatorType);
					map.put(key, seperator);
				}
				UriSeperator<?> child = getChild(seperator, list);
				child.setApi(api);
			}
			
			showApis();
		}
		
		private UriSeperator<?> getChild(UriSeperator<?> parent, LinkedList<String> list) {
			if(list.size()==0) return parent;
			String key = list.remove(0);
			if(parent.hasChild(key)) {
				return getChild(parent.getChild(key), list);
			} else {
				SeperatorType seperatorType = key.startsWith("{") ? SeperatorType.Variable : SeperatorType.Static;
				UriSeperator<?> child = new UriSeperator<String>(key, seperatorType);
				parent.putChild(key, child);
				return getChild(child, list);
			}
		}
		
		private void showApis() {
			for(String key : map.keySet()) {
				showChild(key, map.get(key));
			}
		}
		
		private void showChild(String path, UriSeperator<?> parent) {
			if(parent.hasChild()) {
				for(String key : parent.getChilds().keySet()) {
					showChild(String.format("%s->%s", path, key), parent.getChild(key));
				}			
			}
		}
		
		public String parsePath(String path) {
			if(path==null) { return null; }
			if(path.contains("?")) {
				path = path.substring(0, path.indexOf("?"));
			}
			LinkedList<String> list = new LinkedList<String>();
			list.addAll(Arrays.asList(path.split("/")));
			list.remove("");
			if(list.size()==0) { return null; }
			String key = list.remove(0);
			UriSeperator<?> parent = map.get(key);
			UriSeperator<?> seperator = findChild(parent, list);
			if(seperator == null) {
				return null;
			} else {
				return seperator.getApi();
			}
		}
		
		private UriSeperator<?> findChild(UriSeperator<?> parent, List<String> list) {
			if(list.size()==0 || parent==null) return parent;
			String key = list.remove(0);
			if(parent.hasChild(key)) {
				return findChild(parent.getChild(key), list);
			} else if(parent.hasVariableChild()) {
				return findChild(parent.getVariableChild(), list);
			} else {
				return null;
			}
		}
		
	}
}
