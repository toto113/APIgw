package caesar.api.rest;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ParameterMap implements Serializable, Map<String, List<String>> {
	
	@SuppressWarnings("unused")
	private final Logger LOG = LoggerFactory.getLogger(this.getClass());
	
	private static final long serialVersionUID = 5280411656442196008L;
	
	private Map<String, List<String>> maps;
	
	public ParameterMap() {
		maps = new HashMap<String, List<String>>();
	}
	
	public String put(String key, String value) {
		if(key == null) {
			return null;
		}
		
		List<String> v = maps.get(key);
		if(v == null) {
			v = new ArrayList<String>();
		}
		v.add(value);
		maps.put(key, v);
		
		return value;
	}
	
	@Override
	public int size() {
		return maps.size();
	}
	
	@Override
	public boolean isEmpty() {
		return maps.isEmpty();
	}
	
	@Override
	public boolean containsKey(Object key) {
		return maps.containsKey(key);
	}
	
	@Override
	public boolean containsValue(Object value) {
		Collection<List<String>> vs = maps.values();
		for(List<String> ve : vs) {
			if(ve != null) {
				if(ve.contains(value)) {
					return true;
				}
			}
		}
		return false;
	}
	
	@Override
	public List<String> get(Object key) {
		List<String> rtnList = maps.get(key);
		if ( rtnList == null ) {
			return Collections.emptyList();
		} else {
			return rtnList;
		}
	}
	
	@Override
	public List<String> remove(Object key) {
		return maps.remove(key);
	}
	
	@Override
	public void clear() {
		maps.clear();
	}
	
	public Set<String> keys() {
		return maps.keySet();
	}
	
	@Override
	public Set<String> keySet() {
		return maps.keySet();
	}
	
	@Override
	public Collection<List<String>> values() {
		return maps.values();
	}
	
	@Override
	public Set<Entry<String, List<String>>> entrySet() {
		return maps.entrySet();
	}
	
	@Override
	public List<String> put(String key, List<String> value) {
		if(key == null) {
			return null;
		}
		maps.put(key, value);
		
		return value;
	}
	
	@Override
	public void putAll(Map<? extends String, ? extends List<String>> m) {
		if(m != null) {
			for(java.util.Map.Entry<? extends String, ? extends List<String>> e : m.entrySet()) {
				this.put(e.getKey(), e.getValue());
			}
		}
	}
	
}