package foo;

import java.util.HashMap;
import java.util.Map;

public class UriSeperator<T> {

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
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("name=").append(name);
		sb.append(",api=").append(api);
		sb.append(",seperatorType=").append(seperatorType);
		sb.append(",childs=").append(childs);
		sb.append(",variableChild=").append(variableChild);
		return sb.toString();
	}
	
	public void setApi(String api) {
		this.api = api;
	}
	
	public String getApi() {
		return this.api;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
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
}
