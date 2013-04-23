package com.kthcorp.radix.component;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import junit.framework.TestCase;

import org.slf4j.Logger;
import com.kthcorp.radix.util.UuidViewableLoggerFactory;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.serialize.MapSerializer;
import com.kthcorp.radix.domain.service.api.protocol.http.ParameterMap;

public class MultiMapTest extends TestCase {
	
	private Kryo kryo = new Kryo();
	/*private Kryo kryo = new Kryo() {
		protected Serializer newDefaultSerializer(Class type) {
			return new CompatibleFieldSerializer(this, type);
		}
	};*/
	
	private static final Logger LOG = UuidViewableLoggerFactory.getLogger(MultiMapTest.class);
	
	private static final int KRYO_BUFFER = 1024;
	
	public void testMultimapSerializer() {
		
		// CollectionSerializer collectionSerializer = new CollectionSerializer(kryo);
		MapSerializer serializer = new MapSerializer(kryo);
		
		/*		kryo.register( Map.class, new MapSerializer( kryo ) );
		        kryo.register( HashMap.class, new MapSerializer( kryo ) );
		        kryo.register( Collection.class, new CollectionSerializer( kryo ) );
		        kryo.register( List.class, new CollectionSerializer( kryo ) );
		        kryo.register( ArrayList.class, new CollectionSerializer( kryo ) );*/
		// kryo.register( Map.class );
		kryo.register(HashMap.class);
		kryo.register(Collection.class);
		// kryo.register( List.class );
		kryo.register(ArrayList.class);
		
		// kryo.register(java.util.Collection.class, collectionSerializer);
		kryo.register(com.kthcorp.radix.domain.service.api.protocol.http.ParameterMap.class);
		
		serializer.setValueClass(Collection.class);
		
		ParameterMap multiMap = new ParameterMap();
		multiMap.put("multi1", "value1");
		multiMap.put("multi2", "value2");
		multiMap.put("multi2", "value3");
		LOG.debug(multiMap.toString());
		LOG.debug(multiMap.toString());
		for(String key : multiMap.keys()) {
			LOG.debug(key + ":" + multiMap.get(key));
		}
		
		ByteBuffer buffer = ByteBuffer.allocateDirect(KRYO_BUFFER);
		
		int startPos = buffer.position();
		kryo.writeObject(buffer, multiMap);
		int endPos = buffer.position();
		
		byte[] kryoBytes = new byte[endPos - startPos + 1];
		buffer.position(startPos);
		buffer.get(kryoBytes);
		buffer.clear();
		
		ByteBuffer readBuffer = ByteBuffer.allocateDirect(kryoBytes.length);
		readBuffer.put(kryoBytes);
		readBuffer.flip();
		
		ParameterMap multiMapRtn = kryo.readObject(readBuffer, ParameterMap.class);
		readBuffer.clear();
		
		LOG.debug(multiMapRtn.toString());
		for(String key : multiMapRtn.keys())
			LOG.debug(key + ":" + multiMapRtn.get(key));
	}
}
