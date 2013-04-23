package net.sf.log4jdbc.radix;

import java.sql.ResultSet;
import java.sql.SQLException;

import net.sf.log4jdbc.ResultSetSpy;
import net.sf.log4jdbc.SpyLogDelegator;
import net.sf.log4jdbc.StatementSpy;

/**
 * query 해온 값을 찍어줄 때 byte[] 타입의 id를 표시하기 위한 클래스.
 */
public class ResultSetSpyForRadix extends ResultSetSpy {

	public ResultSetSpyForRadix(StatementSpy parent, ResultSet realResultSet, SpyLogDelegator log) {
		super(parent, realResultSet, log);
	}

	@Override
	public byte[] getBytes(String columnName) throws SQLException
	{
		String methodCall = "getBytes(" + columnName + ")";
		try
		{
			// 원본은 columnName을 (Object[])null로 넘긴다.
			// 뒷단에서 null 여부를 가지고 실제 값 치환을 따지는데,
			// 일단 byte[]가 uuid인지 여부는 여기서 모르니까 일단 columnName을 넘기자.
			return (byte[]) reportReturn(methodCall, realResultSet.getBytes(columnName), columnName);
		}
		catch (SQLException s)
		{
			reportException(methodCall, s);
			throw s;
		}
	}

	@Override
	protected byte[] reportReturn(String methodCall, byte[] value, Object... args)
	{
		try {
			String stringedUuidValue = PreparedStatementSpyForRadix.getString(value);
			// 넘겨온 byte[]가 uuid였네. 출력하도록 String 값과 args를 넘기자. args를 넘기지 않으면 뒤에서 실제값으로 바꿔어주지 않는다.
			reportAllReturns(methodCall, stringedUuidValue, args);
		} catch(Exception e) {
			// 넘겨온 byte[]가 uuid가 아니었다. 원본과 같이 호출하자.
			reportAllReturns(methodCall, "" + value, (Object[])null);
		}
		return value;
	}

}
