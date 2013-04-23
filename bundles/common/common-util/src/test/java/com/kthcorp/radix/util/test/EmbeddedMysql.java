package com.kthcorp.radix.util.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;

import com.kthcorp.radix.util.UuidViewableLoggerFactory;
import com.mysql.management.MysqldResource;
import com.mysql.management.MysqldResourceI;
import com.mysql.management.util.QueryUtil;

public class EmbeddedMysql {

	public static final String DB_DUMP_FILE = "radix.sql";
	public static final String HOST_NAME = "localhost";
	public static final int PORT = 13306;
	public static final String BASE_URL = "jdbc:mysql://"+HOST_NAME+":" + PORT; 
	public static final String DEFAULT_DB_NAME = "radix";
	private static final String ROOT_USER_NAME = "root";
	private static final String ROOT_PASSWORD = "";
	public static final String RADIX_USER_NAME = "radix";
	public static final String RADIX_PASSWORD = "radix";

	private static MysqldResource mysqld = null;
	
	private static final Logger LOG = UuidViewableLoggerFactory.getLogger(EmbeddedMysql.class);
	
	public static boolean isRunning() {
		if(mysqld!=null&&mysqld.isRunning()) { return true; }
		else { return false; }
	}

	// DBMS가 시작되었고, 유저 "root"/""로 접속할 수 있는 상태이다.
	public static void startupDbms() throws Exception {

		// 이미 기동 중이고 root 계정으로 접속 중이다. 따로 구동할 것 없다.
		if(mysqld!=null&&isConnectableWithRoot()) {
			return; 
		}

		File tempDir = new File(System.getProperty("java.io.tmpdir"));
		File databaseDir = new File(tempDir, "mysql-mxj");

		// root 계정으로 접속할 수는 없지만, mysqld가 실행되고 있는 경우이다.
		// 죽이자.
		try {
			mysqld = new MysqldResource(databaseDir);
			if(mysqld.isRunning()) {
				mysqld.shutdown();
			}
		} catch(Exception e) {
			// ignore : 따로 어떻게 할 바가 없다.
		}

		mysqld = new MysqldResource(databaseDir);
		
		Map<String, Object> database_options = new HashMap<String, Object>();
		database_options.put(MysqldResourceI.PORT, Integer.toString(PORT));

		String threadName = "embedded_mysql_thread_for_test";
		mysqld.start(threadName, database_options);

		if (!mysqld.isRunning()) {
			throw new RuntimeException("MySQL did not start.");
		}

		LOG.debug("Embedded Mysql started.");

	}


	public static void shutdownDbms() {

		try {
			mysqld.shutdown();
		} catch (Exception e) {
			// ignore
			LOG.warn("Embedded Mysql stopping failed.", e);
		}

		// 중지할 시간을 준다.
		// 제대로 중지하기 전에 다시 mysqld가 시작되며 문제가 될 수 있다.
		long waitingStart = System.currentTimeMillis();
		while(mysqld.isRunning()) {
			try { Thread.sleep(100); } catch(Exception e) {}
			if((System.currentTimeMillis()-waitingStart>30000)) {
				LOG.debug("Embedded Mysql shutdown waiting timeout. shutdown may not be completed");
				return;
			}
		}
		
		LOG.debug("Embedded Mysql shutdown completed.");
		
	}

	public static void prepareDataBase() throws Exception {
		prepareDataBase(DB_DUMP_FILE);
	}
	
	public static void prepareDataBase(String databaseDumpFileName) throws Exception {

		// 기존에 이미 디비가 있으면 drop한다. 없을 때 발생하는 예외는 무시하고.
		try { dropDataBase("radix"); } catch(Exception e) {}
		try { dropDataBase("backup"); } catch(Exception e) {}
		
		createDataBase("radix", RADIX_USER_NAME, RADIX_PASSWORD);
		createDataBase("backup", RADIX_USER_NAME, RADIX_PASSWORD);

		buildDataBaseWithSqlDump(databaseDumpFileName, RADIX_USER_NAME, RADIX_PASSWORD);

	}



	public static void clearDataBase() throws Exception {
		dropDataBase("radix");
		dropDataBase("backup");
	}

	private static void createDataBase(String dbName, String userName, String password) throws Exception {

		Connection rootCon = null;
		try {
			rootCon = getConnection(ROOT_USER_NAME, ROOT_PASSWORD);
			QueryUtil util = new QueryUtil(rootCon);
			util.execute("CREATE DATABASE "+dbName);
			util.execute("USE "+dbName);

			String sql = "GRANT ALL PRIVILEGES ON "+dbName+".*"
					+ " TO '"+userName+"'@'%' IDENTIFIED BY '"+password+"' WITH GRANT OPTION";
			util.execute(sql);

			sql = "GRANT ALL PRIVILEGES ON "+dbName+".*"
					+ " TO '"+userName+"'@'localhost' IDENTIFIED BY '"+password+"' WITH GRANT OPTION";
			util.execute(sql);

			util.execute("commit");
			
			LOG.debug("database '"+dbName+"' created with userName '"+userName+", password '"+password+"'");
		} catch (SQLException e) {
			throw new Exception("creating db failed.", e);
		} finally {
			if(rootCon!=null) {
				try { rootCon.close(); } catch(SQLException e) { }
			}
		}

		if(!isConnectable(dbName, userName, password)) {
			throw new RuntimeException("creating db and user success, but not connectable.");
		}

	}

	private static void dropDataBase(String dbName) throws Exception {
		
		Connection rootCon = null;
		try {
			rootCon = getConnection(ROOT_USER_NAME, ROOT_PASSWORD);
			QueryUtil util = new QueryUtil(rootCon);
			util.execute("DROP DATABASE "+dbName);
			LOG.debug("database "+dbName+" dropped.");
			util.execute("commit");
		} catch (SQLException e) {
			throw new Exception("dropping database "+dbName+" failed.", e);
		} finally {
			if(rootCon!=null) {
				try { rootCon.close(); } catch(SQLException e) { }
			}
		}



	}


	private static void buildDataBaseWithSqlDump(String sqlDumpFileName, String userName, String password) throws Exception {
		

		Connection con = null;
		try {
			// 실행할 sqlDumpFile이 radix와  backup 디비 두 개 전부 같이 가지고 있다.
			// 그래서 무조건 디비 radix의 것을 가져온다. 하드코딩 한다.
			con = getConnection("radix", userName, password);
			
			List<String> sqlList = loadDumpFileIntoSqlList(sqlDumpFileName);
			QueryUtil util = new QueryUtil(con);
			for(String sql : sqlList) {
				util.execute(sql);
			}
			
			util.execute("commit");
			
			LOG.debug("dump file "+sqlDumpFileName+" imported");
		} catch(Exception e) {
			throw new Exception("importing dump file "+sqlDumpFileName+" failed.", e);
		} finally {
			if(con!=null) {
				try { con.close(); } catch(SQLException e) { }
			}
		}
		
	}



	public static List<String> getTableList(String dbName) throws Exception {

		List<String> tableList = new ArrayList<String>();

		Connection con = null;
		try {
			// 실행할 sqlDumpFile이 radix와  backup 디비 두 개 전부 같이 가지고 있다.
			// 그래서 무조건 디비 radix의 것을 가져온다. 하드코딩 한다.
			con = getConnection(dbName, RADIX_USER_NAME, RADIX_USER_NAME);
			
			QueryUtil util = new QueryUtil(con);
			
			
			{
				@SuppressWarnings({ "unchecked", "rawtypes" })
				List<Map> resultList = util.executeQuery("show tables");
				for(@SuppressWarnings("rawtypes") Map result : resultList) {
					// 문자열 "TABLE_NAME"은 실제 값을 보고 알았다. 그냥 요기에 밖아 두자.
					String tableName = (String)result.get("TABLE_NAME");
					tableList.add(tableName);
				}
			}
			
			util.execute("commit");
		} catch(Exception e) {
			throw new Exception("buliding database failed.", e);
		} finally {
			if(con!=null) {
				try { con.close(); } catch(SQLException e) { }
			}
		}
		
		return tableList;
		
	}



	private static boolean isConnectableWithRoot() {
		// "test"는 mysql의 test를 위해 기본으로 생성되어 있는 디비 이름이다.
		String testDbName = "test";
		return isConnectable(testDbName, ROOT_USER_NAME, ROOT_PASSWORD);
	}


	private static boolean isConnectable(String dbName, String userName, String password) {
		Connection con = null;
		try {
			con = getConnection(dbName, userName, password);
			return true;
		} catch(Exception e) {
			return false;
		} finally {
			if(con!=null) {
				try { con.close(); } catch(Exception e) {}
			}
		}
	}

	private static Connection getConnection(String userName, String password) throws SQLException {
		String dbName = null;
		return getConnection(dbName, userName, password);
	}
	
	private static Connection getConnection(String dbName, String userName, String password) throws SQLException {
		String url = BASE_URL;
		if(dbName!=null) { 
			url = BASE_URL+"/"+dbName;
		}
		// TODO : driverClassName도 외부에서 받을 수 있겠다.
		String driverClassName = com.mysql.jdbc.Driver.class.getName();
		try {
			Class.forName(driverClassName).newInstance();
		} catch (Exception e) {
			// 컴파일이 된다는 건 com.mysql.jdbc.Driver가 제대로 로딩될 수 있다는 것이다. 따로 catch하지 않도록 RuntimeException으로 던진다.
			throw new RuntimeException("loading jdbc driver failed.");
		}

		try {
			return DriverManager.getConnection(url, userName, password);
		} catch(Exception e) {
			throw new SQLException("can't get connection. url="+url+", userName="+userName+", password="+password, e);
		}
	}


	
	private static List<String> loadDumpFileIntoSqlList(String fileName) throws IOException {

		
		String delimeter = ";";
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(Thread.currentThread().getContextClassLoader().getResourceAsStream(fileName)));
		} catch(NullPointerException e) {
			throw new IOException("unable to load file '"+fileName+"'");
		}

		String line = null;
		List<String> sqlList = new ArrayList<String>();

		StringBuilder sb = new StringBuilder();
		while((line = reader.readLine()) != null) {
			if(line.equals("END//") ) {
				sb.append("END");
				sqlList.add(sb.toString());
				sb.setLength(0);
				delimeter = ";";
				continue;
			}
			if(line.equals("DELIMITER //")) {
				delimeter = "//";
				sb.setLength(0);
				continue;
			}
			if(line.equals("DELIMITER ;")) {
				delimeter = ";";
				sb.setLength(0);
				continue;
			}
			
			sb.append(line).append("\n");
			if(line.endsWith(delimeter)) {
				if(line.contains(delimeter)) {
					line.trim();
					sqlList.add(sb.toString());
					sb.setLength(0);
				}
			}
			// delemiter를 바꾸는 특이한 케이스가 있다. 요놈들은 반드시 줄바꿈을 해주어야 한다.

		}

		return sqlList;
	}

	
}
