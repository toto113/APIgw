package com.kthcorp.radix.util;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.UUID;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

import com.fasterxml.uuid.EthernetAddress;
import com.fasterxml.uuid.Generators;
import com.fasterxml.uuid.impl.TimeBasedGenerator;

public class JsonBuilderTest {

	
	private Object[] TEST_VALUES = { "value1", 1, true, 1.3f, 999l, null, "", "with\ttab", "with\nreturn", " ", "'with quote'", "\"with double quote\"" };

	
	private void assertIsJson(String value) {

		System.out.println(value);
		try {
			@SuppressWarnings("unused")
			JSONObject jsonObject = new JSONObject(value);
		} catch (JSONException e) {
			e.printStackTrace();
			fail("not json value. value="+value+", exception message="+e.getMessage());
		}
	}

	private void assertIsNotJson(String value) {

		System.out.println(value);
		try {
			@SuppressWarnings("unused")
			JSONObject jsonObject = new JSONObject(value);
			fail("expect json parsing failed, but success. value="+value);
		} catch (JSONException e) {
		}
	}



	@Test
	public void 다양한_값들로_해보자() {
		JsonBuilder jb = new JsonBuilder();
		for(int i=0; i<TEST_VALUES.length; i++) {
			jb.put("name"+i, TEST_VALUES[i]);
		}

		assertIsJson(jb.toString());
	}

	@Test
	public void 이름에_null을_입력해_보자() {
		JsonBuilder jb = new JsonBuilder();
		jb.put(null, "value");
		assertIsJson(jb.toString());
	}
	
	@Test
	public void 같은이름의_쌍을_입력해_보자() {
		JsonBuilder jb = new JsonBuilder();
		jb.put("name", "value1");
		jb.put("name", "value2");
		jb.put("name", "value3");
		assertIsNotJson(jb.toString());
	}

	
	@Test
	public void toString의_값이_json인_객체를_값으로_할때의_처리_확인() {
		JsonValuable jsonValuable = new JsonValuable();
		JsonBuilder jb = new JsonBuilder();
		jb.put("object", jsonValuable);
		// {"object": {"name1": "value1"}}
		assertIsJson(jb.toString());
	}
	
	@Test
	public void toString의_값이_json이_아닌_객체를_값으로_할때의_처리_확인() {
		NotJsonValuable notJsonValuable = new NotJsonValuable();
		JsonBuilder jb = new JsonBuilder();
		jb.put("object", notJsonValuable);
		// {"object": "name1-value1"}
		assertIsJson(jb.toString());
	}
	
	@Test
	public void Map_처리_확인() {
	
		Map<String, Object> map = new HashMap<String, Object>();
		for(int i=0; i<TEST_VALUES.length; i++) {
			map.put("name"+i, TEST_VALUES[i]);
		}
		JsonBuilder jb = new JsonBuilder();
		jb.put("map", map);
		
		assertIsJson(jb.toString());
		
		assertIsJson(JsonBuilder.getJsonString(map));
		
	}
	
	@Test
	public void List_처리_확인() {
		
		List<Object> list = new ArrayList<Object>();
		for(int i=0; i<TEST_VALUES.length; i++) {
			list.add(TEST_VALUES[i]);
		}
		JsonBuilder jb = new JsonBuilder();
		jb.put("list", list);
		
		assertIsJson(jb.toString());
		
	}
	
	@Test
	public void Set_처리_확인() {
		Set<Object> set = new HashSet<Object>();
		for(int i=0; i<TEST_VALUES.length; i++) {
			set.add(TEST_VALUES[i]);
		}
		JsonBuilder jb = new JsonBuilder();
		jb.put("set", set);
		
		assertIsJson(jb.toString());
	}
	
	@Test
	public void Properties_처리_확인() {
		Properties properties = new Properties();
		for(int i=0; i<TEST_VALUES.length; i++) {
			// Properties는 값으로 null을 담을 수 없다.
			if(TEST_VALUES[i]==null) { continue; }
			properties.put("name"+i, TEST_VALUES[i]);
		}
		JsonBuilder jb = new JsonBuilder();
		jb.put("properties", properties);
		
		assertIsJson(jb.toString());
		
		assertIsJson(JsonBuilder.getJsonString(properties));
		
	}
	
	
	@Test
	public void 값으로_class로_했을때_처리_확인() {
		JsonBuilder jb = new JsonBuilder();
		jb.put("class", this.getClass());
		
		assertIsJson(jb.toString());
	}
	
	
	@Test
	public void byte_array_타입의_uuid_처리_확인() {
		JsonBuilder jb = new JsonBuilder();
		
		// 다음 id 생성 코드는 generateID.generateID()에서 카피
		EthernetAddress ethernetAddress = EthernetAddress.fromInterface();
		TimeBasedGenerator generator = Generators.timeBasedGenerator(ethernetAddress);
		UUID newID = generator.generate();
		byte[] id = UUIDUtils.getBytes(newID);
		
		jb.put("id", id);
		
		assertIsJson(jb.toString());
		assertTrue("not converted into uuid. result="+jb.toString(), jb.toString().indexOf("[B@")==-1);
	}
	
	
	@Test
	 // com.kthcorp.radix.domain.platform.ap.ResponseMessageBodyList의 처리를 확인하기 위한.
	public void List를_상속받은_클래스의_처리를_확인() {
		ResponseMessageBodyList<String, String> list = new ResponseMessageBodyList<String, String>();
		{
			Map<String, String> map = new HashMap<String, String>();
			map.put("name0", "value0");
			map.put("name1", "value1");
			map.put("name2", "value2");
			list.add(map);
		}
		
		{
			Map<String, String> map = new HashMap<String, String>();
			map.put("name0", "value0");
			map.put("name1", "value1");
			map.put("name2", "value2");
			list.add(map);
		}
		JsonBuilder jb = new JsonBuilder();
		jb.put("list", list);
		assertIsJson(jb.toString());
		/*
		{"list": [
		    {
		        "name0": "value0",
		        "name1": "value1",
		        "name2": "value2"
		    },
		    {
		        "name0": "value0",
		        "name1": "value1",
		        "name2": "value2"
		    }
		]}
		 */
	}
	@SuppressWarnings("serial")
	public class ResponseMessageBodyList<E, V> extends ArrayList<Map<E, V>> {
		
		public String toString() {
			JsonBuilder jb = new JsonBuilder();
			jb.put("class", this.getClass());
			jb.put("site", this.size());
			jb.put("data", this);
			return jb.toString();
		}
		
	}
	
	
	private class JsonValuable {
		@Override
		public String toString() {
			return "{name1:value1}";
		}
	}
	
	private class NotJsonValuable {
		@Override
		public String toString() {
			return "name1-value1";
		}
	}
	
	
}
