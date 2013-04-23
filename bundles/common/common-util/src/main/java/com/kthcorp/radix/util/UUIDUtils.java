package com.kthcorp.radix.util;

import java.util.Formatter;
import java.util.UUID;

import com.fasterxml.uuid.impl.UUIDUtil;

public class UUIDUtils {
	public static byte[] getBytes(UUID uuid) {
		if(uuid==null) {
			return null;
		}
		long ls = uuid.getLeastSignificantBits();
		long ms = uuid.getMostSignificantBits();
		
		byte[] uuidInBytes = new byte[16];
		for(int i=0;i<8; ++i) {
			uuidInBytes[i+8] = (byte) (ls >> (7 - i << 3));
			uuidInBytes[i] = (byte) (ms >> (7 - i << 3)); 
		}
		return uuidInBytes;
	}
	
	public static byte[] getBytes(String uuidInString) {
		if(uuidInString==null) {
			return null;
		}
		
		UUID uuid = UUID.fromString(uuidInString);
		return getBytes(uuid);
	}
	
	public static String byteArray2Hex(final byte[] hash) {
		Formatter formatter = new Formatter();
		for(byte b : hash) {
			formatter.format("%02x", b);
		}
		return formatter.toString();
	}
	
	public static String getString(UUID uuid) {
		if(uuid==null) {
			return null;
		}
		return uuid.toString();
	}
	
	public static String getString(byte[] uuidInBytes) {
		if(uuidInBytes==null) {
			return null;
		}
		if(uuidInBytes.length!=16) {
			throw new IllegalArgumentException("uuid invalid(length->"+uuidInBytes.length+")");
		}
		return UUIDUtil.uuid(uuidInBytes).toString();
	}
	
	public static boolean compare(byte[] uuid1, byte[] uuid2) {
		if(uuid1==null||uuid2==null) {
			return false;
		}
		if(uuid1.length!=uuid2.length) {
			return false;
		}
		for(int i=0,l=uuid1.length;i<l;i++) {
			if(uuid2[i]!=uuid1[i]) {
				return false;
			}
		}
		return true;
	}
	
	public static UUID fromByte(byte[] uuidInBytes) {
		if(uuidInBytes==null) {
			return null;
		}
		return UUIDUtil.uuid(uuidInBytes);
	}
}
