/**
 * Copyright 2007-2010 Arthur Blake
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.sf.log4jdbc.radix;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import net.sf.log4jdbc.ConnectionSpy;
import net.sf.log4jdbc.PreparedStatementSpy;
import net.sf.log4jdbc.SpyLogDelegator;

/**
 * uuid byte[] 값을 표시하기 위한 클래스.
 */
public class PreparedStatementSpyForRadix extends PreparedStatementSpy {

	public PreparedStatementSpyForRadix(String sql, ConnectionSpy connectionSpy, PreparedStatement realPreparedStatement, SpyLogDelegator log)
	{
		super(sql, connectionSpy, realPreparedStatement, log);
	}

	/* copied from com.kthcorp.radix.domain.UUIDUtils */
	public static String getString(byte[] uuidInBytes) {
		if(uuidInBytes==null) {
			return null;
		}
		if(uuidInBytes.length!=16) {
			throw new IllegalArgumentException("uuid invalid(length->"+uuidInBytes.length+")");
		}
		return com.fasterxml.uuid.impl.UUIDUtil.uuid(uuidInBytes).toString();
	}

	@Override
	public void setBytes(int parameterIndex, byte[] x) throws SQLException
	{
		//todo: dump array?
		String methodCall = "setBytes(" + parameterIndex + ", " + x + ")";

		String viewingValue = "<byte[]>";
		try {
			viewingValue = getString(x);
		} catch(Exception e) {
			// uuid byte[]가 아니다. 무시한다.
		}
		argTraceSet(parameterIndex, "(byte[])", viewingValue);


		try
		{
			realPreparedStatement.setBytes(parameterIndex, x);
		}
		catch (SQLException s)
		{
			reportException(methodCall, s);
			throw s;
		}
		reportReturn(methodCall);
	}

}