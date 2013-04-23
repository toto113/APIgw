package com.kthcorp.radix.util.test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

public class EmbeddedMysqlTest {

	@Test
	public void 서버_시작과_DB생성_그리고_종료_확인() throws Exception {

		EmbeddedMysql.startupDbms();

		assertTrue("mysql not running.", EmbeddedMysql.isRunning());

		EmbeddedMysql.prepareDataBase();

		List<String> tableList = EmbeddedMysql.getTableList("radix");
		assertNotNull("queryed tableList is null", tableList);
		assertTrue("no table in radix db", tableList.size()>0);
		System.out.println("created table in radix db : "+tableList);

		EmbeddedMysql.clearDataBase();
		EmbeddedMysql.shutdownDbms();

	}


}
