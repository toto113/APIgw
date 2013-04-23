package com.kthcorp.radix.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.json.JSONException;
import org.json.JSONObject;

import com.fasterxml.uuid.impl.UUIDUtil;

/**
 * 거의 JSONObject와 유사하다. 별로 하는 것 없고 단지 다음 정도의 기능만.
 * - 예외를 던지지 않는다. catch할 필요 없다. 다만 json 파싱이 실패하면 최선의 값을 반환한다. 
 * - 값으로 들어온 null이나 빈문자열, white space들을 처리해 주고.
 * - 값으로 들어온 Map, Collection을 처리하고.
 * - 값으로 들어온 String이 json 포멧이라면 스트링이 하닌 json으로 처리해 주고.
 * - radix의 byte[] 타입의 uuid를 String으로 변경해 주고.
 * 
 * 그냥 JSONObject으로 처리하면 제일 간단했을 클래스인데, 
 * 굳이 Pair로 담아 두었다가 이러는 이유는 Json 파싱이 실패했을 때라도 들어올 값을 String으로 보여주기 위해서이다.
 * try catch 안하는 대신 무언가는 반드시 보여주어야 한다.
 * 
 * JSONObject 대비 불편한 것은, 같은 key로 중복 put()하면 처리 못한다는. 하지만 json 비스므래한 출력은 한다는.
 */
public class JsonBuilder {

	
	private List<Pair> pairList = new ArrayList<Pair>();
	private final int DEFAULT_INDENT_SIZE = 4;
	private Exception lastException = null;
	
	
	private class Pair {
		private String name = null;
		private Object value = null;
		public Pair(String name, Object value) {
			this.name = name;
			this.value = value;
		}
	}
	
	public JsonBuilder put(String name, Object value) {
		if(name==null) { name = "''"; }
		Pair pair = new Pair(name, value);
		pairList.add(pair);
		return this;
	}
	
	private static boolean isJsonString(String value) {
		if(value==null) { return false; }
		try {
			new JSONObject(value);
			return true;
		} catch (JSONException e) {
			return false;
		}
	}
	
	private static boolean isJsonValuable(String value) {
		if(value==null) { return false; }
		String trialJsonString = "{name:"+value+"}";
		try {
			@SuppressWarnings("unused")
			JSONObject jsonObject = new JSONObject(trialJsonString);
			// json으로 파싱할 수 있다면 따로 처리할 것 없다.
			return true;
		} catch (JSONException e) {
			return false;
		}
	}
	
	private static String quote(String value) {
		if(value==null) { return "''"; }
		if(value.equals("")) { return "''"; }
		if(value.startsWith("'")&&value.endsWith("'")) { return value; }
		if(value.startsWith("\"")&&value.endsWith("\"")) { return value; }
		if(isJsonString(value)) { return value; }
		return "'"+value+"'";
	}
	
	private static String escape(String value) {
		
		if(isJsonValuable(value)) { return value; }
		String trialJsonString = "{name:'"+value+"'}";
		if(isJsonValuable(trialJsonString)) { return value; }

		StringBuilder sb = new StringBuilder();
		String remain = value;
		int index = remain.indexOf("\n");
		if(index<0) { return value; }
		while(index>=0) {
			String previous = remain.substring(0, index);
			sb.append(previous);
			remain = remain.substring(index+1);
			if(previous.endsWith("\\")) {
				sb.append("\\n");
				// 이미 escape된 경우이다.
			}
			else {
				sb.append("\\\\n");
			}
			index = remain.indexOf("\n");
		}
		sb.append(remain);
		
		return sb.toString();
	}
	
	public String getJsonString() {
		return getJsonString(DEFAULT_INDENT_SIZE);
	}

	
	public String getJsonString(int indent) {
		StringBuilder sb = new StringBuilder();
		sb.append("{\n");
		boolean isFirst = true;
		for(Pair pair : pairList) {
			if(!isFirst) { sb.append(",\n"); }
			sb.append(pair.name);
			sb.append(":");
			String stringValue = getStirngValue(pair.value);
			sb.append(stringValue);
			isFirst = false;
		}
		sb.append("\n}");
		try {
			JSONObject jsonObject = new JSONObject(sb.toString());
			return jsonObject.toString(indent);
		} catch (JSONException e) {
			lastException = e;
		}
		
		// json으로 파싱할 수 없는 경우이다. 그저 String으로 붙여서 반환하자.
		return sb.toString();
		
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static String getStirngValue(Object value) {
		
		if(value==null) { return "''"; }
		if(value instanceof Map<?, ?>) {
			return JsonBuilder.getJsonableString((Map<String, Object>)value);
		}
		else if(value instanceof Collection<?>) {
			return JsonBuilder.getJsonableString((Collection<Object>)value);
		}
		else if(value instanceof Class) {
			return ((Class)value).getName();
		}
		
		
		if(value instanceof byte[]) {
			value = parseIntoUuidIfPossible((byte[]) value);
		}
		
		String stringValue = String.valueOf(value);
		if(isJsonValuable(stringValue)) { return stringValue; }
		if(isJsonString(stringValue)) { return stringValue; }
		
		// "\n"과 같은 특수문자가 포함된 경우이다. escape해보자.
		stringValue = escape(stringValue);
		if(isJsonString(stringValue)) { return stringValue; }
		
		// 값 앞뒤에 quotation을 달아보자.
		stringValue = quote(stringValue);

		// 그래도 실패한다면 어쩔 수 없다. 그냥 반환하자.
		return stringValue;
		
	}

	private static Object parseIntoUuidIfPossible(byte[] value) {
		if(value.length!=16) { return value; }
		try {
			return UUIDUtil.uuid(value).toString();
		} catch(Throwable e) {
			return value;
		}
	}

	public String toString(int indentSize) {
		return getJsonString(indentSize);
	}
	
	public String toString() {
		return getJsonString(DEFAULT_INDENT_SIZE);
	}
	
	public Exception getLastException() {
		return lastException;
	}

	@SuppressWarnings("unchecked")
	// 요 메소드를 호출하면, 다른 빈의  toString()과 달리 "class" 속성값을 설정할 수가 없네. 쓰지 말아야 하남...
	public static String getJsonString(Object mapLikes) {
		if(mapLikes instanceof Map) {
			return getJsonableString((Map<? extends Object, ? extends Object>)mapLikes);
		}
		else if(mapLikes instanceof Properties) {
			return getJsonableString((Properties)mapLikes);
		}
		return null;
	}
	
	public static String getJsonableString(Properties properties) {
		JsonBuilder jb = new JsonBuilder();
		for(Object name : properties.keySet()) {
			if(name==null) { continue; }
			jb.put(name.toString(), properties.get(name));
		}
		return jb.toString();
	}
	
	public static String getJsonableString(Map<? extends Object, ? extends Object> map) {
		JsonBuilder jb = new JsonBuilder();
		for(Object name : map.keySet()) {
			if(name==null) { continue; }
			jb.put(name.toString(), map.get(name));
		}
		return jb.toString();
	}
	
	public static String getJsonableString(Collection<? extends Object> collection) {
		StringBuilder sb = new StringBuilder();
		sb.append("[");
		boolean isCommaRequired = false;
		for(Object value : collection) {
			if(isCommaRequired) { sb.append(", "); }
			String stringValue = getStirngValue(value);
			sb.append(stringValue);
			isCommaRequired = true;
		}
		sb.append("]");
		
		return sb.toString();
	}

	
}