package com.kthcorp.radix.util.test;

import java.io.File;

import org.junit.Ignore;

@Ignore
public class TestResourceFinder {

	
	/**
	 * 전달된 fileName이 절대경로인데 읽을 수 있거나,
	 * 혹은 classPath에 있으면 받은 fileName을 반환한다.
	 * 또는 호출한 클래스의 위치와 이름을 가지고 파일을 찾아본다.
	 * 호출한 클래스가 com.some.MyTest이면 com/some/Mytest_resouce에서 찾아 본다.
	 */
	public static String getResourceFileNameOnClasspath(String fileName) {
		
		File file = new File(fileName);
		if(file.exists()) { return file.getAbsolutePath(); }
		
		if(Thread.currentThread().getContextClassLoader().getResourceAsStream(fileName)!=null) { 
			return fileName; 
		}
		
		// callerClassName = "com.some.MyTest"
		String callerClassName = getCallerClassName();
		// path = "com/some/MyTest"
		String path = callerClassName.replace(".", "/");
		// resourcePath = "com/some/MyTest/MyTest_resource"
		String resourcePath = path+"_resource";
		String resourceFileName = resourcePath+"/"+fileName;
		
		return resourceFileName;
		
	}
	
	
	private static String getCallerClassName() {
		try {
			throw new Exception("HI");
		} catch(Exception e) {
			StackTraceElement[] stackTraces = e.getStackTrace();
			for(StackTraceElement aStack : stackTraces) {
				String fullClassName = aStack.getClassName();
				if(fullClassName.startsWith(TestResourceFinder.class.getName()) ) { continue; }
				// inner class 일수도 있다.
				if(fullClassName.indexOf("$")>0) {
					fullClassName = fullClassName.substring(0, fullClassName.indexOf("$"));
				}
				return fullClassName;
			}
		}
		return null;
	}
	
}
