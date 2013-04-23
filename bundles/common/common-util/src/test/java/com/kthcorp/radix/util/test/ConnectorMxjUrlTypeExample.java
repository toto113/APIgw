package com.kthcorp.radix.util.test;

import java.io.File;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.DriverManager;

import org.junit.Test;

import com.mysql.management.driverlaunched.ServerLauncherSocketFactory;
import com.mysql.management.util.Platform;
import com.mysql.management.util.QueryUtil;



/**
 * 다운로드 받은 mysql-connector-mxj에 포함된 ConnectorMXJUrlTestExample.java의 내용을 거의 그대로 가져왔다.
 * 이를 토대로 EmbeddedMysql.java를 짜집기 했다.
 * 
 * 요 클래스는 테스트 케이스도 아니고, 다른 곳에서도 사용하지 않는다.
 * 사실 필요 없고, 삭제하여할 클래스 이지만, 냄겨 둔다.
 * EmbeddedMysql이 문제가 없다면 삭제하자.
 *
 * TODO : 필요없는 클래스이다. 삭제하자.
 * 
 */
public class ConnectorMxjUrlTypeExample {
	
	public static String DRIVER = "com.mysql.jdbc.Driver";
	public static String JAVA_IO_TMPDIR = "java.io.tmpdir";
	
	@Test
	public void 기동과_중지_확인() {
		
	}
	
	
	public static void main(String[] args) throws Exception {
		
		System.setProperty(Platform.OS_ARCH, "x86");
		
		File ourAppDir = new File(System.getProperty("java.io.tmpdir"));
        File databaseDir = new File(ourAppDir, "test-show-users-mxj");
        String databasePath = URLEncoder.encode(databaseDir.getPath(), "UTF-8");
        
		int port = Integer.parseInt(System.getProperty("c-mxj_test_port", "3336"));
		String dbName = "our_test_app";
		String url = "jdbc:mysql:mxj://localhost:" + port + "/" + dbName //
				+ "?" + "server.basedir=" + databasePath //
				+ "&" + "createDatabaseIfNotExist=true"//
				+ "&" + "server.initialize-user=true" //
				;
		System.out.println(url);
		String userName = "alice";
		String password = "q93uti0opwhkd";
		Class.forName(DRIVER);
		Connection conn = null;
		try {
			conn = DriverManager.getConnection(url, userName, password);
			QueryUtil queryUtil = new QueryUtil(conn);
			
			
			String sql = "SELECT VERSION()";
			String queryForString = queryUtil.queryForString(sql);
			System.out.println("------------------------");
			System.out.println(sql);
			System.out.println("------------------------");
			System.out.println(queryForString);
			System.out.println("------------------------");
			System.out.flush();
			Thread.sleep(100); // wait for System.out to finish flush
			
			sql = "create database backup";
			queryUtil.execute(sql);
			
		} finally {
			try {
				if (conn != null)
					conn.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			ServerLauncherSocketFactory.shutdown(databaseDir, null);
		}
	}
}
