package com.kthcorp.radix.util;

public class ApiKeyUtil {

	// fullPath에서 앞의 두 개를 떨구고 반환한다.
	// "/MapAPIForTest/1" 			--> ""
	// "/MapAPIForTest/1/"	 		--> "/"
	// "/MapAPIForTest/1/map" 		--> "/map"
	// "/MapAPIForTest/1/map/"		--> "/map/"
	// "/MapAPIForTest/1/map/key" 	--> "/map/key"
	public static String getResourcePath(String fullPath) {
		int i=0;
		int slashCount = 0;
		i = fullPath.indexOf("/");
		while(i!=-1) {
			slashCount++;
			if(slashCount==3) { break; }
			i = fullPath.indexOf("/", i+1);

		}
		
		// i가 -1이란 건 slash가 3개도 안된다는 것이다.
		if(i==-1) { return ""; }
		
		// 현재 i는 3번째 slash를 가리키고 있다.
		return fullPath.substring(i);
	}
	
}
