package foo;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DynamicResourceIndicator {

	private final Logger LOG = LoggerFactory.getLogger(this.getClass());
	
	private List<String> apis;
	public void setApis(List<String> apis) {
		this.apis = apis;
	}
	
	private Map<String, UriSeperator<?>> map;
	
	public DynamicResourceIndicator() {		
		init();
	}
	
	public DynamicResourceIndicator(List<String> apis) {
		this.apis = apis;
		init();
	}
	
	private void init() {
		map = new HashMap<String, UriSeperator<?>>();
		loadApis();
	}
	
	private void loadApis() {
		for(String api : apis) {
			LinkedList<String> list = new LinkedList<String>();
			list.addAll(Arrays.asList(api.split("/")));
			list.remove("");
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
		
		LOG.info("loadApis done...");
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
		if(parent.getApi() != null) LOG.info("path={}, api={}", path, parent.getApi());
		if(parent.hasChild()) {
			for(String key : parent.getChilds().keySet()) {
				showChild(String.format("%s->%s", path, key), parent.getChild(key));
			}			
		}
	}
	
	public String parsePath(String path) {
		LinkedList<String> list = new LinkedList<String>();
		list.addAll(Arrays.asList(path.split("/")));
		list.remove("");
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
