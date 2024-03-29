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
package net.sf.log4jdbc;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Date;
import java.sql.NClob;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Map;

/**
 * Wraps a ResultSet and reports method calls, returns and exceptions.
 *
 * JDBC 4 version.
 *
 * @author Arthur Blake
 */
@SuppressWarnings({ "unchecked", "rawtypes" })
public class ResultSetSpy implements ResultSet, Spy
{
  private final SpyLogDelegator log;

  /**
   * Collects results from the result set spy method
   */
  private ResultSetCollector resultSetCollector = null;

  public ResultSetCollector getResultSetCollector()
  {
    return resultSetCollector;
  }

  public void setResultSetCollector(ResultSetCollector resultSetCollector)
  {
    this.resultSetCollector = resultSetCollector;
  }

  /**
   * Report an exception to be logged.
   *
   * @param methodCall description of method call and arguments passed to it that generated the exception.
   * @param exception exception that was generated
   */
  protected void reportException(String methodCall, SQLException exception)
  {
    log.exceptionOccured(this, methodCall, exception, null, -1L);
  }

  /**
   * Report (for logging) that a method returned.  All the other reportReturn methods are convenience methods that call
   * this method.
   *
   * @param methodCall description of method call and arguments passed to it that returned.
   * @param msg description of what the return value that was returned.  may be an empty String for void return types.
   */
  protected void reportAllReturns(String methodCall, Object returnValue, Object... methodParams)
  {
    if (resultSetCollector != null)
    {
      // Give the result set collector a chance to do its work
      boolean finished = resultSetCollector.methodReturned(this, methodCall, returnValue, realResultSet, methodParams);
      if (finished)
      {
        log.resultSetCollected(resultSetCollector);
        resultSetCollector.reset();
      }
    }
    log.methodReturned(this, methodCall, returnValue, realResultSet, methodParams);
  }

  // MODIFY : change modifiter from private to protected
  protected ResultSet realResultSet;

  /**
   * Get the real ResultSet that this ResultSetSpy wraps.
   *
   * @return the real ResultSet that this ResultSetSpy wraps.
   */
  public ResultSet getRealResultSet()
  {
    return realResultSet;
  }

  private StatementSpy parent;

  /**
   * Create a new ResultSetSpy that wraps another ResultSet object, that logs all method calls, expceptions, etc.
   *
   * @param parent Statement that generated this ResultSet.
   * @param realResultSet real underlying ResultSet that is being wrapped.
   */
  public ResultSetSpy(StatementSpy parent, ResultSet realResultSet, SpyLogDelegator log)
  {
    if (realResultSet == null)
    {
      throw new IllegalArgumentException("Must provide a non null real ResultSet");
    }
    this.realResultSet = realResultSet;
    this.parent = parent;
    this.log = log;
    if (log.isResultSetCollectionEnabled())
    {
      resultSetCollector = new DefaultResultSetCollector(log.isResultSetCollectionEnabledWithUnreadValueFillIn());
    }
    reportReturn("new ResultSet", "", realResultSet);
  }

  /**
   * Description for ResultSet class type.
   */
  public static final String classTypeDescription = "ResultSet";

  public String getClassType()
  {
    return classTypeDescription;
  }

  public Integer getConnectionNumber()
  {
    return parent.getConnectionNumber();
  }

  /**
   * Conveniance method to report (for logging) that a method returned a boolean value.
   *
   * @param methodCall description of method call and arguments passed to it that returned.
   * @param value boolean return value.
   * @return the boolean return value as passed in.
   */
  protected boolean reportReturn(String methodCall, Boolean value, Object... args)
  {
    reportAllReturns(methodCall, "" + value, args);
    return value;
  }

  /**
   * Conveniance method to report (for logging) that a method returned a byte value.
   *
   * @param methodCall description of method call and arguments passed to it that returned.
   * @param value byte return value.
   * @return the byte return value as passed in.
   */
  protected byte reportReturn(String methodCall, Byte value, Object... args)
  {
    reportAllReturns(methodCall, "" + value, args);
    return value;
  }

  /**
   * Conveniance method to report (for logging) that a method returned a int value.
   *
   * @param methodCall description of method call and arguments passed to it that returned.
   * @param value int return value.
   * @return the int return value as passed in.
   */
  protected int reportReturn(String methodCall, Integer value, Object... args)
  {
    reportAllReturns(methodCall, "" + value, args);
    return value;
  }

  /**
   * Conveniance method to report (for logging) that a method returned a double value.
   *
   * @param methodCall description of method call and arguments passed to it that returned.
   * @param value double return value.
   * @return the double return value as passed in.
   */
  protected double reportReturn(String methodCall, Double value, Object... args)
  {
    reportAllReturns(methodCall, "" + value, args);
    return value;
  }

  /**
   * Conveniance method to report (for logging) that a method returned a short value.
   *
   * @param methodCall description of method call and arguments passed to it that returned.
   * @param value short return value.
   * @return the short return value as passed in.
   */
  protected short reportReturn(String methodCall, Short value, Object... args)
  {
    reportAllReturns(methodCall, "" + value, args);
    return value;
  }

  /**
   * Conveniance method to report (for logging) that a method returned a long value.
   *
   * @param methodCall description of method call and arguments passed to it that returned.
   * @param value long return value.
   * @return the long return value as passed in.
   */
  protected long reportReturn(String methodCall, Long value, Object... args)
  {
    reportAllReturns(methodCall, "" + value, args);
    return value;
  }

  /**
   * Conveniance method to report (for logging) that a method returned a float value.
   *
   * @param methodCall description of method call and arguments passed to it that returned.
   * @param value float return value.
   * @return the float return value as passed in.
   */
  protected float reportReturn(String methodCall, Float value, Object... args)
  {
    reportAllReturns(methodCall, "" + value, args);
    return value;
  }

  protected Object reportReturn(String methodCall, Time value, Object... args)
  {
    reportAllReturns(methodCall, "" + value, args);
    return value;
  }

  protected BigDecimal reportReturn(String methodCall, BigDecimal value, Object... args)
  {
    reportAllReturns(methodCall, "" + value, args);
    return value;
  }

  protected Timestamp reportReturn(String methodCall, Timestamp value, Object... args)
  {
    reportAllReturns(methodCall, "" + value, args);
    return value;
  }

  protected String reportReturn(String methodCall, String value, Object... args)
  {
    reportAllReturns(methodCall, "" + value, args);
    return value;
  }

  /**
   * Conveniance method to report (for logging) that a method returned an Object.
   *
   * @param methodCall description of method call and arguments passed to it that returned.
   * @param value return Object.
   * @return the return Object as passed in.
   */
  protected Object reportReturn(String methodCall, Object returnValue, Object... args)
  {
    reportAllReturns(methodCall, returnValue, args);
    return returnValue;
  }

  protected Ref reportReturn(String methodCall, Ref value, Object... args)
  {
    reportAllReturns(methodCall, "" + value, args);
    return value;
  }

  protected Blob reportReturn(String methodCall, Blob value, Object... args)
  {
    reportAllReturns(methodCall, "" + value, args);
    return value;
  }

  protected Clob reportReturn(String methodCall, Clob value, Object... args)
  {
    reportAllReturns(methodCall, "" + value, args);
    return value;
  }

  protected Array reportReturn(String methodCall, Array value, Object... args)
  {
    reportAllReturns(methodCall, "" + value, args);
    return value;
  }

  protected RowId reportReturn(String methodCall, RowId value, Object... args)
  {
    reportAllReturns(methodCall, "" + value, args);
    return value;
  }

  protected SQLXML reportReturn(String methodCall, SQLXML value, Object... args)
  {
    reportAllReturns(methodCall, "" + value, args);
    return value;
  }

  protected Reader reportReturn(String methodCall, Reader value, Object... args)
  {
    reportAllReturns(methodCall, "" + value, args);
    return value;
  }

  protected Date reportReturn(String methodCall, Date value, Object... args)
  {
    reportAllReturns(methodCall, "" + value, args);
    return value;
  }

  protected InputStream reportReturn(String methodCall, InputStream value, Object... args)
  {
    reportAllReturns(methodCall, "" + value, args);
    return value;
  }

  protected URL reportReturn(String methodCall, URL value, Object... args)
  {
    reportAllReturns(methodCall, "" + value, args);
    return value;
  }

  protected byte[] reportReturn(String methodCall, byte[] value, Object... args)
  {
    reportAllReturns(methodCall, "" + value, args);
    return value;
  }

  // forwarding methods

  public void updateAsciiStream(int columnIndex, InputStream x, int length) throws SQLException
  {
    String methodCall = "updateAsciiStream(" + columnIndex + ", " + x + ", " + length + ")";
    try {
      realResultSet.updateAsciiStream(columnIndex, x, length);
    }
    catch (SQLException s)
    {
      reportException(methodCall, s);
      throw s;
    }
    reportReturn(methodCall, (Object[]) null);
  }

  public void updateAsciiStream(String columnName, InputStream x, int length) throws SQLException
  {
    String methodCall = "updateAsciiStream(" + columnName + ", " + x + ", " + length + ")";
    try
    {
      realResultSet.updateAsciiStream(columnName, x, length);
    }
    catch (SQLException s)
    {
      reportException(methodCall, s);
      throw s;
    }
    reportReturn(methodCall, (Object[]) null);
  }

  public int getRow() throws SQLException
  {
    String methodCall = "getRow()";
    try
    {
      return reportReturn(methodCall, realResultSet.getRow(), (Object[]) null);
    }
    catch (SQLException s)
    {
      reportException(methodCall, s);
      throw s;
    }
  }

  public void cancelRowUpdates() throws SQLException
  {
    String methodCall = "cancelRowUpdates()";
    try
    {
      realResultSet.cancelRowUpdates();
    }
    catch (SQLException s)
    {
      reportException(methodCall, s);
      throw s;
    }
    reportReturn(methodCall, (Object[]) null);
  }

  public Time getTime(int columnIndex) throws SQLException
  {
    String methodCall = "getTime(" + columnIndex + ")";
    try
    {
      return (Time) reportReturn(methodCall, realResultSet.getTime(columnIndex), columnIndex);
    }
    catch (SQLException s)
    {
      reportException(methodCall, s);
      throw s;
    }
  }

  public Time getTime(String columnName) throws SQLException
  {
    String methodCall = "getTime(" + columnName + ")";
    try
    {
      return (Time) reportReturn(methodCall, realResultSet.getTime(columnName), columnName);
    }
    catch (SQLException s)
    {
      reportException(methodCall, s);
      throw s;
    }
  }

  public Time getTime(int columnIndex, Calendar cal) throws SQLException
  {
    String methodCall = "getTime(" + columnIndex + ", " + cal + ")";
    try
    {
      return (Time) reportReturn(methodCall, realResultSet.getTime(columnIndex, cal), columnIndex, cal);
    }
    catch (SQLException s)
    {
      reportException(methodCall, s);
      throw s;
    }
  }

  public Time getTime(String columnName, Calendar cal) throws SQLException
  {
    String methodCall = "getTime(" + columnName + ", " + cal + ")";
    try
    {
      return (Time) reportReturn(methodCall, realResultSet.getTime(columnName, cal), columnName, cal);
    }
    catch (SQLException s)
    {
      reportException(methodCall, s);
      throw s;
    }
  }

  public boolean absolute(int row) throws SQLException
  {
    String methodCall = "absolute(" + row + ")";
    try
    {
      return reportReturn(methodCall, realResultSet.absolute(row), row);
    }
    catch (SQLException s)
    {
      reportException(methodCall, s);
      throw s;
    }
  }

  public Timestamp getTimestamp(int columnIndex) throws SQLException
  {
    String methodCall = "getTimestamp(" + columnIndex + ")";
    try
    {
      return (Timestamp) reportReturn(methodCall, realResultSet.getTimestamp(columnIndex), columnIndex);
    }
    catch (SQLException s)
    {
      reportException(methodCall, s);
      throw s;
    }
  }

  public Timestamp getTimestamp(String columnName) throws SQLException
  {
    String methodCall = "getTimestamp(" + columnName + ")";
    try
    {
      return (Timestamp) reportReturn(methodCall, realResultSet.getTimestamp(columnName), columnName);
    }
    catch (SQLException s)
    {
      reportException(methodCall, s);
      throw s;
    }

  }

  public Timestamp getTimestamp(int columnIndex, Calendar cal) throws SQLException
  {
    String methodCall = "getTimestamp(" + columnIndex + ", " + cal + ")";
    try
    {
      return (Timestamp) reportReturn(methodCall, realResultSet.getTimestamp(columnIndex, cal), columnIndex, cal);
    }
    catch (SQLException s)
    {
      reportException(methodCall, s);
      throw s;
    }

  }

  public Timestamp getTimestamp(String columnName, Calendar cal) throws SQLException
  {
    String methodCall = "getTimestamp(" + columnName + ", " + cal + ")";
    try
    {
      return (Timestamp) reportReturn(methodCall, realResultSet.getTimestamp(columnName, cal), cal);
    }
    catch (SQLException s)
    {
      reportException(methodCall, s);
      throw s;
    }
  }

  public void moveToInsertRow() throws SQLException
  {
    String methodCall = "moveToInsertRow()";
    try
    {
      realResultSet.moveToInsertRow();
    }
    catch (SQLException s)
    {
      reportException(methodCall, s);
      throw s;
    }
    reportReturn(methodCall, (Object[]) null);
  }

  public boolean relative(int rows) throws SQLException
  {
    String methodCall = "relative(" + rows + ")";
    try
    {
      return reportReturn(methodCall, realResultSet.relative(rows), rows);
    }
    catch (SQLException s)
    {
      reportException(methodCall, s);
      throw s;
    }
  }

  public boolean previous() throws SQLException
  {
    String methodCall = "previous()";
    try
    {
      return reportReturn(methodCall, realResultSet.previous(), (Object[]) null);
    }
    catch (SQLException s)
    {
      reportException(methodCall, s);
      throw s;
    }
  }

  public void moveToCurrentRow() throws SQLException
  {
    String methodCall = "moveToCurrentRow()";
    try
    {
      realResultSet.moveToCurrentRow();
    }
    catch (SQLException s)
    {
      reportException(methodCall, s);
      throw s;
    }
    reportReturn(methodCall, (Object[]) null);
  }

  public Ref getRef(int i) throws SQLException
  {
    String methodCall = "getRef(" + i + ")";
    try
    {
      return (Ref) reportReturn(methodCall, realResultSet.getRef(i), i);
    }
    catch (SQLException s)
    {
      reportException(methodCall, s);
      throw s;
    }
  }

  public void updateRef(int columnIndex, Ref x) throws SQLException
  {
    String methodCall = "updateRef(" + columnIndex + ", " + x + ")";
    try
    {
      realResultSet.updateRef(columnIndex, x);
    }
    catch (SQLException s)
    {
      reportException(methodCall, s);
      throw s;
    }
    reportReturn(methodCall, (Object[]) null);
  }

  public Ref getRef(String colName) throws SQLException
  {
    String methodCall = "getRef(" + colName + ")";
    try
    {
      return (Ref) reportReturn(methodCall, realResultSet.getRef(colName), colName);
    }
    catch (SQLException s)
    {
      reportException(methodCall, s);
      throw s;
    }
  }

  public void updateRef(String columnName, Ref x) throws SQLException
  {
    String methodCall = "updateRef(" + columnName + ", " + x + ")";
    try
    {
      realResultSet.updateRef(columnName, x);
    }
    catch (SQLException s)
    {
      reportException(methodCall, s);
      throw s;
    }
    reportReturn(methodCall, (Object[]) null);
  }

  public Blob getBlob(int i) throws SQLException
  {
    String methodCall = "getBlob(" + i + ")";
    try
    {
      return (Blob) reportReturn(methodCall, realResultSet.getBlob(i), i);
    }
    catch (SQLException s)
    {
      reportException(methodCall, s);
      throw s;
    }
  }

  public void updateBlob(int columnIndex, Blob x) throws SQLException
  {
    String methodCall = "updateBlob(" + columnIndex + ", " + x + ")";
    try
    {
      realResultSet.updateBlob(columnIndex, x);
    }
    catch (SQLException s)
    {
      reportException(methodCall, s);
      throw s;
    }
    reportReturn(methodCall, (Object[]) null);
  }

  public Blob getBlob(String colName) throws SQLException
  {
    String methodCall = "getBlob(" + colName + ")";
    try
    {
      return (Blob) reportReturn(methodCall, realResultSet.getBlob(colName), colName);
    }
    catch (SQLException s)
    {
      reportException(methodCall, s);
      throw s;
    }
  }

  public void updateBlob(String columnName, Blob x) throws SQLException
  {
    String methodCall = "updateBlob(" + columnName + ", " + x + ")";
    try
    {
      realResultSet.updateBlob(columnName, x);
    }
    catch (SQLException s)
    {
      reportException(methodCall, s);
      throw s;
    }
    reportReturn(methodCall, (Object[]) null);
  }

  public Clob getClob(int i) throws SQLException
  {
    String methodCall = "getClob(" + i + ")";
    try
    {
      return (Clob) reportReturn(methodCall, realResultSet.getClob(i), i);
    }
    catch (SQLException s)
    {
      reportException(methodCall, s);
      throw s;
    }
  }

  public void updateClob(int columnIndex, Clob x) throws SQLException
  {
    String methodCall = "updateClob(" + columnIndex + ", " + x + ")";
    try
    {
      realResultSet.updateClob(columnIndex, x);
    }
    catch (SQLException s)
    {
      reportException(methodCall, s);
      throw s;
    }
    reportReturn(methodCall, (Object[]) null);
  }

  public Clob getClob(String colName) throws SQLException
  {
    String methodCall = "getClob(" + colName + ")";
    try
    {
      return (Clob) reportReturn(methodCall, realResultSet.getClob(colName), colName);
    }
    catch (SQLException s)
    {
      reportException(methodCall, s);
      throw s;
    }
  }

  public void updateClob(String columnName, Clob x) throws SQLException
  {
    String methodCall = "updateClob(" + columnName + ", " + x + ")";
    try
    {
      realResultSet.updateClob(columnName, x);
    }
    catch (SQLException s)
    {
      reportException(methodCall, s);
      throw s;
    }
    reportReturn(methodCall, (Object[]) null);
  }

  public boolean getBoolean(int columnIndex) throws SQLException
  {
    String methodCall = "getBoolean(" + columnIndex + ")";
    try
    {
      return reportReturn(methodCall, realResultSet.getBoolean(columnIndex), columnIndex);
    }
    catch (SQLException s)
    {
      reportException(methodCall, s);
      throw s;
    }
  }

  public boolean getBoolean(String columnName) throws SQLException
  {
    String methodCall = "getBoolean(" + columnName + ")";
    try
    {
      return reportReturn(methodCall, realResultSet.getBoolean(columnName), columnName);
    }
    catch (SQLException s)
    {
      reportException(methodCall, s);
      throw s;
    }
  }

  public Array getArray(int i) throws SQLException
  {
    String methodCall = "getArray(" + i + ")";
    try
    {
      return (Array) reportReturn(methodCall, realResultSet.getArray(i), i);
    }
    catch (SQLException s)
    {
      reportException(methodCall, s);
      throw s;
    }
  }

  public void updateArray(int columnIndex, Array x) throws SQLException
  {
    String methodCall = "updateArray(" + columnIndex + ", " + x + ")";
    try
    {
      realResultSet.updateArray(columnIndex, x);
    }
    catch (SQLException s)
    {
      reportException(methodCall, s);
      throw s;
    }
    reportReturn(methodCall, (Object[]) null);
  }

  public Array getArray(String colName) throws SQLException
  {
    String methodCall = "getArray(" + colName + ")";
    try
    {
      return (Array) reportReturn(methodCall, realResultSet.getArray(colName), colName);
    }
    catch (SQLException s)
    {
      reportException(methodCall, s);
      throw s;
    }
  }

  public void updateArray(String columnName, Array x) throws SQLException
  {
    String methodCall = "updateArray(" + columnName + ", " + x + ")";
    try
    {
      realResultSet.updateArray(columnName, x);
    }
    catch (SQLException s)
    {
      reportException(methodCall, s);
      throw s;
    }
    reportReturn(methodCall, (Object[]) null);
  }

  public RowId getRowId(int columnIndex) throws SQLException {
    String methodCall = "getRowId(" + columnIndex + ")";
    try
    {
      return (RowId) reportReturn(methodCall, realResultSet.getRowId(columnIndex), columnIndex);
    }
    catch (SQLException s)
    {
      reportException(methodCall, s);
      throw s;
    }
  }

  public RowId getRowId(String columnLabel) throws SQLException {
    String methodCall = "getRowId(" + columnLabel + ")";
    try
    {
      return (RowId) reportReturn(methodCall, realResultSet.getRowId(columnLabel), columnLabel);
    }
    catch (SQLException s)
    {
      reportException(methodCall, s);
      throw s;
    }
  }

  public void updateRowId(int columnIndex, RowId x) throws SQLException {
    String methodCall = "updateRowId(" + columnIndex + ", " + x + ")";
    try
    {
      realResultSet.updateRowId(columnIndex, x);
    }
    catch (SQLException s)
    {
      reportException(methodCall, s);
      throw s;
    }
    reportReturn(methodCall, (Object[]) null);
  }

  public void updateRowId(String columnLabel, RowId x) throws SQLException {
    String methodCall = "updateRowId(" + columnLabel + ", " + x + ")";
    try
    {
      realResultSet.updateRowId(columnLabel, x);
    }
    catch (SQLException s)
    {
      reportException(methodCall, s);
      throw s;
    }
    reportReturn(methodCall, (Object[]) null);
  }

  public int getHoldability() throws SQLException {
    String methodCall = "getHoldability()";
    try
    {
      return reportReturn(methodCall, realResultSet.getHoldability(), (Object[]) null);
    }
    catch (SQLException s)
    {
      reportException(methodCall, s);
      throw s;
    }
  }

  public boolean isClosed() throws SQLException {
    String methodCall = "isClosed()";
    try
    {
      return reportReturn(methodCall, realResultSet.isClosed(), (Object[]) null);
    }
    catch (SQLException s)
    {
      reportException(methodCall, s);
      throw s;
    }
  }

  public void updateNString(int columnIndex, String nString) throws SQLException {
    String methodCall = "updateNString(" + columnIndex + ", " + nString + ")";
    try
    {
      realResultSet.updateNString(columnIndex, nString);
    }
    catch (SQLException s)
    {
      reportException(methodCall, s);
      throw s;
    }
    reportReturn(methodCall, (Object[]) null);
  }

  public void updateNString(String columnLabel, String nString) throws SQLException {
    String methodCall = "updateNString(" + columnLabel + ", " + nString + ")";
    try
    {
      realResultSet.updateNString(columnLabel, nString);
    }
    catch (SQLException s)
    {
      reportException(methodCall, s);
      throw s;
    }
    reportReturn(methodCall, (Object[]) null);
  }

  public void updateNClob(int columnIndex, NClob nClob) throws SQLException {
    String methodCall = "updateNClob(" + columnIndex + ", " + nClob + ")";
    try
    {
      realResultSet.updateNClob(columnIndex, nClob);
    }
    catch (SQLException s)
    {
      reportException(methodCall, s);
      throw s;
    }
    reportReturn(methodCall, (Object[]) null);
  }

  public void updateNClob(String columnLabel, NClob nClob) throws SQLException {
    String methodCall = "updateNClob(" + columnLabel + ", " + nClob + ")";
    try
    {
      realResultSet.updateNClob(columnLabel, nClob);
    }
    catch (SQLException s)
    {
      reportException(methodCall, s);
      throw s;
    }
    reportReturn(methodCall, (Object[]) null);
  }

  public NClob getNClob(int columnIndex) throws SQLException {
    String methodCall = "getNClob(" + columnIndex + ")";
    try
    {
      return (NClob) reportReturn(methodCall, realResultSet.getNClob(columnIndex), columnIndex);
    }
    catch (SQLException s)
    {
      reportException(methodCall, s);
      throw s;
    }
  }

  public NClob getNClob(String columnLabel) throws SQLException {
    String methodCall = "getNClob(" + columnLabel + ")";
    try
    {
      return (NClob) reportReturn(methodCall, realResultSet.getNClob(columnLabel), columnLabel);
    }
    catch (SQLException s)
    {
      reportException(methodCall, s);
      throw s;
    }
  }

  public SQLXML getSQLXML(int columnIndex) throws SQLException {
    String methodCall = "getSQLXML(" + columnIndex + ")";
    try
    {
      return (SQLXML) reportReturn(methodCall, realResultSet.getSQLXML(columnIndex), columnIndex);
    }
    catch (SQLException s)
    {
      reportException(methodCall, s);
      throw s;
    }
  }

  public SQLXML getSQLXML(String columnLabel) throws SQLException {
    String methodCall = "getSQLXML(" + columnLabel + ")";
    try
    {
      return (SQLXML) reportReturn(methodCall, realResultSet.getSQLXML(columnLabel), columnLabel);
    }
    catch (SQLException s)
    {
      reportException(methodCall, s);
      throw s;
    }
  }

  public void updateSQLXML(int columnIndex, SQLXML xmlObject) throws SQLException {
    String methodCall = "updateSQLXML(" + columnIndex + ", " + xmlObject + ")";
    try
    {
      realResultSet.updateSQLXML(columnIndex, xmlObject);
    }
    catch (SQLException s)
    {
      reportException(methodCall, s);
      throw s;
    }
    reportReturn(methodCall, (Object[]) null);
  }

  public void updateSQLXML(String columnLabel, SQLXML xmlObject) throws SQLException {
    String methodCall = "updateSQLXML(" + columnLabel + ", " + xmlObject + ")";
    try
    {
      realResultSet.updateSQLXML(columnLabel, xmlObject);
    }
    catch (SQLException s)
    {
      reportException(methodCall, s);
      throw s;
    }
    reportReturn(methodCall, (Object[]) null);
  }

  public String getNString(int columnIndex) throws SQLException {
    String methodCall = "getNString(" + columnIndex + ")";
    try
    {
      return (String) reportReturn(methodCall, realResultSet.getNString(columnIndex), columnIndex);
    }
    catch (SQLException s)
    {
      reportException(methodCall, s);
      throw s;
    }
  }

  public String getNString(String columnLabel) throws SQLException {
    String methodCall = "getNString(" + columnLabel + ")";
    try
    {
      return (String) reportReturn(methodCall, realResultSet.getNString(columnLabel), columnLabel);
    }
    catch (SQLException s)
    {
      reportException(methodCall, s);
      throw s;
    }
  }

  public Reader getNCharacterStream(int columnIndex) throws SQLException {
    String methodCall = "getNCharacterStream(" + columnIndex + ")";
    try
    {
      return (Reader) reportReturn(methodCall, realResultSet.getNCharacterStream(columnIndex), columnIndex);
    }
    catch (SQLException s)
    {
      reportException(methodCall, s);
      throw s;
    }
  }

  public Reader getNCharacterStream(String columnLabel) throws SQLException {
    String methodCall = "getNCharacterStream(" + columnLabel + ")";
    try
    {
      return (Reader) reportReturn(methodCall, realResultSet.getNCharacterStream(columnLabel), columnLabel);
    }
    catch (SQLException s)
    {
      reportException(methodCall, s);
      throw s;
    }
  }

  public void updateNCharacterStream(int columnIndex, Reader x, long length) throws SQLException {
    String methodCall = "updateNCharacterStream(" + columnIndex + ", " + x + ", " + length + ")";
    try
    {
      realResultSet.updateNCharacterStream(columnIndex, x, length);
    }
    catch (SQLException s)
    {
      reportException(methodCall, s);
      throw s;
    }
    reportReturn(methodCall, (Object[]) null);
  }

  public void updateNCharacterStream(String columnLabel, Reader reader, long length) throws SQLException {
    String methodCall = "updateNCharacterStream(" + columnLabel + ", " + reader + ", " + length + ")";
    try
    {
      realResultSet.updateNCharacterStream(columnLabel, reader, length);
    }
    catch (SQLException s)
    {
      reportException(methodCall, s);
      throw s;
    }
    reportReturn(methodCall, (Object[]) null);
  }

  public void updateAsciiStream(int columnIndex, InputStream x, long length) throws SQLException {
    String methodCall = "updateAsciiStream(" + columnIndex + ", " + x + ", " + length + ")";
    try
    {
      realResultSet.updateAsciiStream(columnIndex, x, length);
    }
    catch (SQLException s)
    {
      reportException(methodCall, s);
      throw s;
    }
    reportReturn(methodCall, (Object[]) null);
  }

  public void updateBinaryStream(int columnIndex, InputStream x, long length) throws SQLException {
    String methodCall = "updateBinaryStream(" + columnIndex + ", " + x + ", " + length + ")";
    try
    {
      realResultSet.updateBinaryStream(columnIndex, x, length);
    }
    catch (SQLException s)
    {
      reportException(methodCall, s);
      throw s;
    }
    reportReturn(methodCall, (Object[]) null);
  }

  public void updateCharacterStream(int columnIndex, Reader x, long length) throws SQLException {
    String methodCall = "updateCharacterStream(" + columnIndex + ", " + x + ", " + length + ")";
    try
    {
      realResultSet.updateCharacterStream(columnIndex, x, length);
    }
    catch (SQLException s)
    {
      reportException(methodCall, s);
      throw s;
    }
    reportReturn(methodCall, (Object[]) null);
  }

  public void updateAsciiStream(String columnLabel, InputStream x, long length) throws SQLException {
    String methodCall = "updateAsciiStream(" + columnLabel + ", " + x + ", " + length + ")";
    try
    {
      realResultSet.updateAsciiStream(columnLabel, x, length);
    }
    catch (SQLException s)
    {
      reportException(methodCall, s);
      throw s;
    }
    reportReturn(methodCall, (Object[]) null);
  }

  public void updateBinaryStream(String columnLabel, InputStream x, long length) throws SQLException {
    String methodCall = "updateBinaryStream(" + columnLabel + ", " + x + ", " + length + ")";
    try
    {
      realResultSet.updateBinaryStream(columnLabel, x, length);
    }
    catch (SQLException s)
    {
      reportException(methodCall, s);
      throw s;
    }
    reportReturn(methodCall, (Object[]) null);
  }

  public void updateCharacterStream(String columnLabel, Reader reader, long length) throws SQLException {
    String methodCall = "updateCharacterStream(" + columnLabel + ", " + reader + ", " + length + ")";
    try
    {
      realResultSet.updateCharacterStream(columnLabel, reader, length);
    }
    catch (SQLException s)
    {
      reportException(methodCall, s);
      throw s;
    }
    reportReturn(methodCall, (Object[]) null);
  }

  public void updateBlob(int columnIndex, InputStream inputStream, long length) throws SQLException {
    String methodCall = "updateBlob(" + columnIndex + ", " + inputStream + ", " + length + ")";
    try
    {
      realResultSet.updateBlob(columnIndex, inputStream, length);
    }
    catch (SQLException s)
    {
      reportException(methodCall, s);
      throw s;
    }
    reportReturn(methodCall, (Object[]) null);
  }

  public void updateBlob(String columnLabel, InputStream inputStream, long length) throws SQLException {
    String methodCall = "updateBlob(" + columnLabel + ", " + inputStream + ", " + length + ")";
    try
    {
      realResultSet.updateBlob(columnLabel, inputStream, length);
    }
    catch (SQLException s)
    {
      reportException(methodCall, s);
      throw s;
    }
    reportReturn(methodCall, (Object[]) null);
  }

  public void updateClob(int columnIndex, Reader reader, long length) throws SQLException {
    String methodCall = "updateClob(" + columnIndex + ", " + reader + ", " + length + ")";
    try
    {
      realResultSet.updateClob(columnIndex, reader, length);
    }
    catch (SQLException s)
    {
      reportException(methodCall, s);
      throw s;
    }
    reportReturn(methodCall, (Object[]) null);
  }

  public void updateClob(String columnLabel, Reader reader, long length) throws SQLException {
    String methodCall = "updateClob(" + columnLabel + ", " + reader + ", " + length + ")";
    try
    {
      realResultSet.updateClob(columnLabel, reader, length);
    }
    catch (SQLException s)
    {
      reportException(methodCall, s);
      throw s;
    }
    reportReturn(methodCall, (Object[]) null);
  }

  public void updateNClob(int columnIndex, Reader reader, long length) throws SQLException {
    String methodCall = "updateNClob(" + columnIndex + ", " + reader + ", " + length + ")";
    try
    {
      realResultSet.updateNClob(columnIndex, reader, length);
    }
    catch (SQLException s)
    {
      reportException(methodCall, s);
      throw s;
    }
    reportReturn(methodCall, (Object[]) null);
  }

  public void updateNClob(String columnLabel, Reader reader, long length) throws SQLException {
    String methodCall = "updateNClob(" + columnLabel + ", " + reader + ", " + length + ")";
    try
    {
      realResultSet.updateNClob(columnLabel, reader, length);
    }
    catch (SQLException s)
    {
      reportException(methodCall, s);
      throw s;
    }
    reportReturn(methodCall, (Object[]) null);
  }

  public void updateNCharacterStream(int columnIndex, Reader reader) throws SQLException {
    String methodCall = "updateNCharacterStream(" + columnIndex + ", " + reader + ")";
    try
    {
      realResultSet.updateNCharacterStream(columnIndex, reader);
    }
    catch (SQLException s)
    {
      reportException(methodCall, s);
      throw s;
    }
    reportReturn(methodCall, (Object[]) null);
  }

  public void updateNCharacterStream(String columnLabel, Reader reader) throws SQLException {
    String methodCall = "updateNCharacterStream(" + columnLabel + ", " + reader + ")";
    try
    {
      realResultSet.updateNCharacterStream(columnLabel, reader);
    }
    catch (SQLException s)
    {
      reportException(methodCall, s);
      throw s;
    }
    reportReturn(methodCall, (Object[]) null);
  }

  public void updateAsciiStream(int columnIndex, InputStream x) throws SQLException {
    String methodCall = "updateAsciiStream(" + columnIndex + ", " + x + ")";
    try
    {
      realResultSet.updateAsciiStream(columnIndex, x);
    }
    catch (SQLException s)
    {
      reportException(methodCall, s);
      throw s;
    }
    reportReturn(methodCall, (Object[]) null);
  }

  public void updateBinaryStream(int columnIndex, InputStream x) throws SQLException {
    String methodCall = "updateBinaryStream(" + columnIndex + ", " + x + ")";
    try
    {
      realResultSet.updateBinaryStream(columnIndex, x);
    }
    catch (SQLException s)
    {
      reportException(methodCall, s);
      throw s;
    }
    reportReturn(methodCall, (Object[]) null);
  }

  public void updateCharacterStream(int columnIndex, Reader x) throws SQLException {
    String methodCall = "updateCharacterStream(" + columnIndex + ", " + x + ")";
    try
    {
      realResultSet.updateCharacterStream(columnIndex, x);
    }
    catch (SQLException s)
    {
      reportException(methodCall, s);
      throw s;
    }
    reportReturn(methodCall, (Object[]) null);
  }

  public void updateAsciiStream(String columnLabel, InputStream x) throws SQLException {
    String methodCall = "updateAsciiStream(" + columnLabel + ", " + x + ")";
    try
    {
      realResultSet.updateAsciiStream(columnLabel, x);
    }
    catch (SQLException s)
    {
      reportException(methodCall, s);
      throw s;
    }
    reportReturn(methodCall, (Object[]) null);
  }

  public void updateBinaryStream(String columnLabel, InputStream x) throws SQLException {
    String methodCall = "updateBinaryStream(" + columnLabel + ", " + x + ")";
    try
    {
      realResultSet.updateBinaryStream(columnLabel, x);
    }
    catch (SQLException s)
    {
      reportException(methodCall, s);
      throw s;
    }
    reportReturn(methodCall, (Object[]) null);
  }

  public void updateCharacterStream(String columnLabel, Reader reader) throws SQLException {
    String methodCall = "updateCharacterStream(" + columnLabel + ", " + reader + ")";
    try
    {
      realResultSet.updateCharacterStream(columnLabel, reader);
    }
    catch (SQLException s)
    {
      reportException(methodCall, s);
      throw s;
    }
    reportReturn(methodCall, (Object[]) null);
  }

  public void updateBlob(int columnIndex, InputStream inputStream) throws SQLException {
    String methodCall = "updateBlob(" + columnIndex + ", " + inputStream + ")";
    try
    {
      realResultSet.updateBlob(columnIndex, inputStream);
    }
    catch (SQLException s)
    {
      reportException(methodCall, s);
      throw s;
    }
    reportReturn(methodCall, (Object[]) null);
  }

  public void updateBlob(String columnLabel, InputStream inputStream) throws SQLException {
    String methodCall = "updateBlob(" + columnLabel + ", " + inputStream + ")";
    try
    {
      realResultSet.updateBlob(columnLabel, inputStream);
    }
    catch (SQLException s)
    {
      reportException(methodCall, s);
      throw s;
    }
    reportReturn(methodCall, (Object[]) null);
  }

  public void updateClob(int columnIndex, Reader reader) throws SQLException {
    String methodCall = "updateClob(" + columnIndex + ", " + reader + ")";
    try
    {
      realResultSet.updateClob(columnIndex, reader);
    }
    catch (SQLException s)
    {
      reportException(methodCall, s);
      throw s;
    }
    reportReturn(methodCall, (Object[]) null);
  }

  public void updateClob(String columnLabel, Reader reader) throws SQLException {
    String methodCall = "updateClob(" + columnLabel + ", " + reader + ")";
    try
    {
      realResultSet.updateClob(columnLabel, reader);
    }
    catch (SQLException s)
    {
      reportException(methodCall, s);
      throw s;
    }
    reportReturn(methodCall, (Object[]) null);
  }

  public void updateNClob(int columnIndex, Reader reader) throws SQLException {
    String methodCall = "updateNClob(" + columnIndex + ", " + reader + ")";
    try
    {
      realResultSet.updateNClob(columnIndex, reader);
    }
    catch (SQLException s)
    {
      reportException(methodCall, s);
      throw s;
    }
    reportReturn(methodCall, (Object[]) null);
  }

  public void updateNClob(String columnLabel, Reader reader) throws SQLException {
    String methodCall = "updateNClob(" + columnLabel + ", " + reader + ")";
    try
    {
      realResultSet.updateNClob(columnLabel, reader);
    }
    catch (SQLException s)
    {
      reportException(methodCall, s);
      throw s;
    }
    reportReturn(methodCall, (Object[]) null);
  }

  public boolean isBeforeFirst() throws SQLException
  {
    String methodCall = "isBeforeFirst()";
    try
    {
      return reportReturn(methodCall, realResultSet.isBeforeFirst(), (Object[]) null);
    }
    catch (SQLException s)
    {
      reportException(methodCall, s);
      throw s;
    }
  }

  public short getShort(int columnIndex) throws SQLException
  {
    String methodCall = "getShort(" + columnIndex + ")";
    try
    {
      return reportReturn(methodCall, realResultSet.getShort(columnIndex), columnIndex);
    }
    catch (SQLException s)
    {
      reportException(methodCall, s);
      throw s;
    }
  }

  public short getShort(String columnName) throws SQLException
  {
    String methodCall = "getShort(" + columnName + ")";
    try
    {
      return reportReturn(methodCall, realResultSet.getShort(columnName), columnName);
    }
    catch (SQLException s)
    {
      reportException(methodCall, s);
      throw s;
    }
  }

  public int getInt(int columnIndex) throws SQLException
  {
    String methodCall = "getInt(" + columnIndex + ")";
    try
    {
      return reportReturn(methodCall, realResultSet.getInt(columnIndex), columnIndex);
    }
    catch (SQLException s)
    {
      reportException(methodCall, s);
      throw s;
    }
  }

  public int getInt(String columnName) throws SQLException
  {
    String methodCall = "getInt(" + columnName + ")";
    try
    {
      return reportReturn(methodCall, realResultSet.getInt(columnName), columnName);
    }
    catch (SQLException s)
    {
      reportException(methodCall, s);
      throw s;
    }
  }

  public void close() throws SQLException
  {
    String methodCall = "close()";
    try
    {
      realResultSet.close();
    }
    catch (SQLException s)
    {
      reportException(methodCall, s);
      throw s;
    }
    reportReturn(methodCall, (Object[]) null);
  }

  public ResultSetMetaData getMetaData() throws SQLException
  {
    String methodCall = "getMetaData()";
    try
    {
      return (ResultSetMetaData) reportReturn(methodCall, realResultSet.getMetaData(), (Object[]) null);
    }
    catch (SQLException s)
    {
      reportException(methodCall, s);
      throw s;
    }
  }

  public int getType() throws SQLException
  {
    String methodCall = "getType()";
    try
    {
      return reportReturn(methodCall, realResultSet.getType(), (Object[]) null);
    }
    catch (SQLException s)
    {
      reportException(methodCall, s);
      throw s;
    }
  }

  public double getDouble(int columnIndex) throws SQLException
  {
    String methodCall = "getDouble(" + columnIndex + ")";
    try
    {
      return reportReturn(methodCall, realResultSet.getDouble(columnIndex), columnIndex);
    }
    catch (SQLException s)
    {
      reportException(methodCall, s);
      throw s;
    }
  }

  public double getDouble(String columnName) throws SQLException
  {
    String methodCall = "getDouble(" + columnName + ")";
    try
    {
      return reportReturn(methodCall, realResultSet.getDouble(columnName), columnName);
    }
    catch (SQLException s)
    {
      reportException(methodCall, s);
      throw s;
    }
  }

  public void deleteRow() throws SQLException
  {
    String methodCall = "deleteRow()";
    try
    {
      realResultSet.deleteRow();
    }
    catch (SQLException s)
    {
      reportException(methodCall, s);
      throw s;
    }
    reportReturn(methodCall, (Object[]) null);
  }

  public int getConcurrency() throws SQLException
  {
    String methodCall = "getConcurrency()";
    try
    {
      return reportReturn(methodCall, realResultSet.getConcurrency(), (Object[]) null);
    }
    catch (SQLException s)
    {
      reportException(methodCall, s);
      throw s;
    }
  }

  public boolean rowUpdated() throws SQLException
  {
    String methodCall = "rowUpdated()";
    try
    {
      return reportReturn(methodCall, realResultSet.rowUpdated(), (Object[]) null);
    }
    catch (SQLException s)
    {
      reportException(methodCall, s);
      throw s;
    }
  }

  public Date getDate(int columnIndex) throws SQLException
  {
    String methodCall = "getDate(" + columnIndex + ")";
    try
    {
      return (Date) reportReturn(methodCall, realResultSet.getDate(columnIndex), columnIndex);
    }
    catch (SQLException s)
    {
      reportException(methodCall, s);
      throw s;
    }
  }

  public Date getDate(String columnName) throws SQLException
  {
    String methodCall = "getDate(" + columnName + ")";
    try
    {
      return (Date) reportReturn(methodCall, realResultSet.getDate(columnName), columnName);
    }
    catch (SQLException s)
    {
      reportException(methodCall, s);
      throw s;
    }
  }

  public Date getDate(int columnIndex, Calendar cal) throws SQLException
  {
    String methodCall = "getDate(" + columnIndex + ", " + cal + ")";
    try
    {
      return (Date) reportReturn(methodCall, realResultSet.getDate(columnIndex, cal), columnIndex, cal);
    }
    catch (SQLException s)
    {
      reportException(methodCall, s);
      throw s;
    }

  }

  public Date getDate(String columnName, Calendar cal) throws SQLException
  {
    String methodCall = "getDate(" + columnName + ", " + cal + ")";
    try
    {
      return (Date) reportReturn(methodCall, realResultSet.getDate(columnName, cal), columnName, cal);
    }
    catch (SQLException s)
    {
      reportException(methodCall, s);
      throw s;
    }
  }

  public boolean last() throws SQLException
  {
    String methodCall = "last()";
    try
    {
      return reportReturn(methodCall, realResultSet.last(), (Object[]) null);
    }
    catch (SQLException s)
    {
      reportException(methodCall, s);
      throw s;
    }
  }

  public boolean rowInserted() throws SQLException
  {
    String methodCall = "rowInserted()";
    try
    {
      return reportReturn(methodCall, realResultSet.rowInserted(), (Object[]) null);
    }
    catch (SQLException s)
    {
      reportException(methodCall, s);
      throw s;
    }
  }

  public boolean rowDeleted() throws SQLException
  {
    String methodCall = "rowDeleted()";
    try
    {
      return reportReturn(methodCall, realResultSet.rowDeleted(), (Object[]) null);
    }
    catch (SQLException s)
    {
      reportException(methodCall, s);
      throw s;
    }
  }

  public void updateNull(int columnIndex) throws SQLException
  {
    String methodCall = "updateNull(" + columnIndex + ")";
    try
    {
      realResultSet.updateNull(columnIndex);
    }
    catch (SQLException s)
    {
      reportException(methodCall, s);
      throw s;
    }
    reportReturn(methodCall, (Object[]) null);
  }

  public void updateNull(String columnName) throws SQLException
  {
    String methodCall = "updateNull(" + columnName + ")";
    try
    {
      realResultSet.updateNull(columnName);
    }
    catch (SQLException s)
    {
      reportException(methodCall, s);
      throw s;
    }
    reportReturn(methodCall, (Object[]) null);
  }

  public void updateShort(int columnIndex, short x) throws SQLException
  {
    String methodCall = "updateShort(" + columnIndex + ", " + x + ")";
    try
    {
      realResultSet.updateShort(columnIndex, x);
    }
    catch (SQLException s)
    {
      reportException(methodCall, s);
      throw s;
    }
    reportReturn(methodCall, (Object[]) null);
  }

  public void updateShort(String columnName, short x) throws SQLException
  {
    String methodCall = "updateShort(" + columnName + ", " + x + ")";
    try
    {
      realResultSet.updateShort(columnName, x);
    }
    catch (SQLException s)
    {
      reportException(methodCall, s);
      throw s;
    }
    reportReturn(methodCall, (Object[]) null);
  }

  public void updateBoolean(int columnIndex, boolean x) throws SQLException
  {
    String methodCall = "updateBoolean(" + columnIndex + ", " + x + ")";
    try
    {
      realResultSet.updateBoolean(columnIndex, x);
    }
    catch (SQLException s)
    {
      reportException(methodCall, s);
      throw s;
    }
    reportReturn(methodCall, (Object[]) null);
  }

  public void updateBoolean(String columnName, boolean x) throws SQLException
  {
    String methodCall = "updateBoolean(" + columnName + ", " + x + ")";
    try
    {
      realResultSet.updateBoolean(columnName, x);
    }
    catch (SQLException s)
    {
      reportException(methodCall, s);
      throw s;
    }
    reportReturn(methodCall, (Object[]) null);
  }

  public void updateByte(int columnIndex, byte x) throws SQLException
  {
    String methodCall = "updateByte(" + columnIndex + ", " + x + ")";
    try
    {
      realResultSet.updateByte(columnIndex, x);
    }
    catch (SQLException s)
    {
      reportException(methodCall, s);
      throw s;
    }
    reportReturn(methodCall, (Object[]) null);
  }

  public void updateByte(String columnName, byte x) throws SQLException
  {
    String methodCall = "updateByte(" + columnName + ", " + x + ")";
    try
    {
      realResultSet.updateByte(columnName, x);
    }
    catch (SQLException s)
    {
      reportException(methodCall, s);
      throw s;
    }
    reportReturn(methodCall, (Object[]) null);
  }

  public void updateInt(int columnIndex, int x) throws SQLException
  {
    String methodCall = "updateInt(" + columnIndex + ", " + x + ")";
    try
    {
      realResultSet.updateInt(columnIndex, x);
    }
    catch (SQLException s)
    {
      reportException(methodCall, s);
      throw s;
    }
    reportReturn(methodCall, (Object[]) null);
  }

  public void updateInt(String columnName, int x) throws SQLException
  {
    String methodCall = "updateInt(" + columnName + ", " + x + ")";
    try
    {
      realResultSet.updateInt(columnName, x);
    }
    catch (SQLException s)
    {
      reportException(methodCall, s);
      throw s;
    }
    reportReturn(methodCall, (Object[]) null);
  }

  public Object getObject(int columnIndex) throws SQLException
  {
    String methodCall = "getObject(" + columnIndex + ")";
    try
    {
      return reportReturn(methodCall, realResultSet.getObject(columnIndex), new Object[]
      { columnIndex });
    }
    catch (SQLException s)
    {
      reportException(methodCall, s);
      throw s;
    }
  }

  public Object getObject(String columnName) throws SQLException
  {
    String methodCall = "getObject(" + columnName + ")";
    try
    {
      return reportReturn(methodCall, realResultSet.getObject(columnName), new Object[]
      { columnName });
    }
    catch (SQLException s)
    {
      reportException(methodCall, s);
      throw s;
    }
  }

  public Object getObject(String colName, Map map) throws SQLException
  {
    String methodCall = "getObject(" + colName + ", " + map + ")";
    try
    {
      return reportReturn(methodCall, realResultSet.getObject(colName, map), new Object[]
      { colName, map });
    }
    catch (SQLException s)
    {
      reportException(methodCall, s);
      throw s;
    }
  }

  public boolean next() throws SQLException
  {
    String methodCall = "next()";
    try
    {
      if (resultSetCollector != null)
      {
        // Give the result set collector a chance to fill in unread values from the result set row if that option has been selected
        resultSetCollector.preMethod(this, methodCall, (Object[]) null);
      }
      return reportReturn(methodCall, realResultSet.next(), (Object[]) null);
    }
    catch (SQLException s)
    {
      reportException(methodCall, s);
      throw s;
    }
  }

  public void updateLong(int columnIndex, long x) throws SQLException
  {
    String methodCall = "updateLong(" + columnIndex + ", " + x + ")";
    try
    {
      realResultSet.updateLong(columnIndex, x);
    }
    catch (SQLException s)
    {
      reportException(methodCall, s);
      throw s;
    }
    reportReturn(methodCall, (Object[]) null);
  }

  public void updateLong(String columnName, long x) throws SQLException
  {
    String methodCall = "updateLong(" + columnName + ", " + x + ")";
    try
    {
      realResultSet.updateLong(columnName, x);
    }
    catch (SQLException s)
    {
      reportException(methodCall, s);
      throw s;
    }
    reportReturn(methodCall, (Object[]) null);
  }

  public void updateFloat(int columnIndex, float x) throws SQLException
  {
    String methodCall = "updateFloat(" + columnIndex + ", " + x + ")";
    try
    {
      realResultSet.updateFloat(columnIndex, x);
    }
    catch (SQLException s)
    {
      reportException(methodCall, s);
      throw s;
    }
    reportReturn(methodCall, (Object[]) null);

  }

  public void updateFloat(String columnName, float x) throws SQLException
  {
    String methodCall = "updateFloat(" + columnName + ", " + x + ")";
    try
    {
      realResultSet.updateFloat(columnName, x);
    }
    catch (SQLException s)
    {
      reportException(methodCall, s);
      throw s;
    }
    reportReturn(methodCall, (Object[]) null);
  }

  public void updateDouble(int columnIndex, double x) throws SQLException
  {
    String methodCall = "updateDouble(" + columnIndex + ", " + x + ")";
    try
    {
      realResultSet.updateDouble(columnIndex, x);
    }
    catch (SQLException s)
    {
      reportException(methodCall, s);
      throw s;
    }
    reportReturn(methodCall, (Object[]) null);
  }

  public void updateDouble(String columnName, double x) throws SQLException
  {
    String methodCall = "updateDouble(" + columnName + ", " + x + ")";
    try
    {
      realResultSet.updateDouble(columnName, x);
    }
    catch (SQLException s)
    {
      reportException(methodCall, s);
      throw s;
    }
    reportReturn(methodCall, (Object[]) null);
  }

  public Statement getStatement() throws SQLException
  {
    String methodCall = "getStatement()";
    try
    {
      Statement s = realResultSet.getStatement();
      if (s == null)
      {
        return (Statement) reportReturn(methodCall, s, (Object[]) null);
      }
      else
      {
        // todo: what's going on here?
        return (Statement) reportReturn(methodCall, new StatementSpy(new ConnectionSpy(s.getConnection(), this.log), s, this.log), (Object[]) null);
      }
    }
    catch (SQLException s)
    {
      reportException(methodCall, s);
      throw s;
    }
  }

  public Object getObject(int columnIndex, Map<String, Class<?>> map) throws SQLException
  {
    String methodCall = "getObject(" + columnIndex + ", " + map + ")";
    try
    {
      return reportReturn(methodCall, realResultSet.getObject(columnIndex, map), new Object[]
      { columnIndex, map });
    }
    catch (SQLException s)
    {
      reportException(methodCall, s);
      throw s;
    }
  }

  public void updateString(int columnIndex, String x) throws SQLException
  {
    String methodCall = "updateString(" + columnIndex + ", " + x + ")";
    try
    {
      realResultSet.updateString(columnIndex, x);
    }
    catch (SQLException s)
    {
      reportException(methodCall, s);
      throw s;
    }
    reportReturn(methodCall, (Object[]) null);
  }

  public void updateString(String columnName, String x) throws SQLException
  {
    String methodCall = "updateString(" + columnName + ", " + x + ")";
    try
    {
      realResultSet.updateString(columnName, x);
    }
    catch (SQLException s)
    {
      reportException(methodCall, s);
      throw s;
    }
    reportReturn(methodCall, (Object[]) null);
  }

  public InputStream getAsciiStream(int columnIndex) throws SQLException
  {
    String methodCall = "getAsciiStream(" + columnIndex + ")";
    try
    {
      return (InputStream) reportReturn(methodCall, realResultSet.getAsciiStream(columnIndex), columnIndex);
    }
    catch (SQLException s)
    {
      reportException(methodCall, s);
      throw s;
    }
  }

  public InputStream getAsciiStream(String columnName) throws SQLException
  {
    String methodCall = "getAsciiStream(" + columnName + ")";
    try
    {
      return (InputStream) reportReturn(methodCall, realResultSet.getAsciiStream(columnName), columnName);
    }
    catch (SQLException s)
    {
      reportException(methodCall, s);
      throw s;
    }
  }

  public void updateBigDecimal(int columnIndex, BigDecimal x) throws SQLException
  {
    String methodCall = "updateBigDecimal(" + columnIndex + ", " + x + ")";
    try
    {
      realResultSet.updateBigDecimal(columnIndex, x);
    }
    catch (SQLException s)
    {
      reportException(methodCall, s);
      throw s;
    }
    reportReturn(methodCall, (Object[]) null);
  }

  public URL getURL(int columnIndex) throws SQLException
  {
    String methodCall = "getURL(" + columnIndex + ")";
    try
    {
      return (URL) reportReturn(methodCall, realResultSet.getURL(columnIndex), columnIndex);
    }
    catch (SQLException s)
    {
      reportException(methodCall, s);
      throw s;
    }
  }

  public void updateBigDecimal(String columnName, BigDecimal x) throws SQLException
  {
    String methodCall = "updateBigDecimal(" + columnName + ", " + x + ")";
    try
    {
      realResultSet.updateBigDecimal(columnName, x);
    }
    catch (SQLException s)
    {
      reportException(methodCall, s);
      throw s;
    }
    reportReturn(methodCall, (Object[]) null);
  }

  public URL getURL(String columnName) throws SQLException
  {
    String methodCall = "getURL(" + columnName + ")";
    try
    {
      return (URL) reportReturn(methodCall, realResultSet.getURL(columnName), (Object[]) null);
    }
    catch (SQLException s)
    {
      reportException(methodCall, s);
      throw s;
    }
  }

  public void updateBytes(int columnIndex, byte[] x) throws SQLException
  {
    // todo: dump array?
    String methodCall = "updateBytes(" + columnIndex + ", " + x + ")";
    try
    {
      realResultSet.updateBytes(columnIndex, x);
    }
    catch (SQLException s)
    {
      reportException(methodCall, s);
      throw s;
    }
    reportReturn(methodCall, (Object[]) null);
  }

  public void updateBytes(String columnName, byte[] x) throws SQLException
  {
    // todo: dump array?
    String methodCall = "updateBytes(" + columnName + ", " + x + ")";
    try
    {
      realResultSet.updateBytes(columnName, x);
    }
    catch (SQLException s)
    {
      reportException(methodCall, s);
      throw s;
    }
    reportReturn(methodCall, (Object[]) null);
  }

  /**
   * @deprecated
   */
  public InputStream getUnicodeStream(int columnIndex) throws SQLException
  {
    String methodCall = "getUnicodeStream(" + columnIndex + ")";
    try
    {
      return (InputStream) reportReturn(methodCall, realResultSet.getUnicodeStream(columnIndex), columnIndex);
    }
    catch (SQLException s)
    {
      reportException(methodCall, s);
      throw s;
    }
  }

  /**
   * @deprecated
   */
  public InputStream getUnicodeStream(String columnName) throws SQLException
  {
    String methodCall = "getUnicodeStream(" + columnName + ")";
    try
    {
      return (InputStream) reportReturn(methodCall, realResultSet.getUnicodeStream(columnName), columnName);
    }
    catch (SQLException s)
    {
      reportException(methodCall, s);
      throw s;
    }
  }

  public void updateDate(int columnIndex, Date x) throws SQLException
  {
    String methodCall = "updateDate(" + columnIndex + ", " + x + ")";
    try
    {
      realResultSet.updateDate(columnIndex, x);
    }
    catch (SQLException s)
    {
      reportException(methodCall, s);
      throw s;
    }
  }

  public void updateDate(String columnName, Date x) throws SQLException
  {
    String methodCall = "updateDate(" + columnName + ", " + x + ")";
    try
    {
      realResultSet.updateDate(columnName, x);
    }
    catch (SQLException s)
    {
      reportException(methodCall, s);
      throw s;
    }
    reportReturn(methodCall, (Object[]) null);
  }

  public int getFetchSize() throws SQLException
  {
    String methodCall = "getFetchSize()";
    try
    {
      return reportReturn(methodCall, realResultSet.getFetchSize(), (Object[]) null);
    }
    catch (SQLException s)
    {
      reportException(methodCall, s);
      throw s;
    }
  }

  public SQLWarning getWarnings() throws SQLException
  {
    String methodCall = "getWarnings()";
    try
    {
      return (SQLWarning) reportReturn(methodCall, realResultSet.getWarnings(), (Object[]) null);
    }
    catch (SQLException s)
    {
      reportException(methodCall, s);
      throw s;
    }
  }

  public InputStream getBinaryStream(int columnIndex) throws SQLException
  {
    String methodCall = "getBinaryStream(" + columnIndex + ")";
    try
    {
      return (InputStream) reportReturn(methodCall, realResultSet.getBinaryStream(columnIndex), columnIndex);
    }
    catch (SQLException s)
    {
      reportException(methodCall, s);
      throw s;
    }
  }

  public InputStream getBinaryStream(String columnName) throws SQLException
  {
    String methodCall = "getBinaryStream(" + columnName + ")";
    try
    {
      return (InputStream) reportReturn(methodCall, realResultSet.getBinaryStream(columnName), columnName);
    }
    catch (SQLException s)
    {
      reportException(methodCall, s);
      throw s;
    }
  }

  public void clearWarnings() throws SQLException
  {
    String methodCall = "clearWarnings()";
    try
    {
      realResultSet.clearWarnings();
    }
    catch (SQLException s)
    {
      reportException(methodCall, s);
      throw s;
    }
    reportReturn(methodCall, (Object[]) null);
  }

  public void updateTimestamp(int columnIndex, Timestamp x) throws SQLException
  {
    String methodCall = "updateTimestamp(" + columnIndex + ", " + x + ")";
    try
    {
      realResultSet.updateTimestamp(columnIndex, x);
    }
    catch (SQLException s)
    {
      reportException(methodCall, s);
      throw s;
    }
    reportReturn(methodCall, (Object[]) null);
  }

  public void updateTimestamp(String columnName, Timestamp x) throws SQLException
  {
    String methodCall = "updateTimestamp(" + columnName + ", " + x + ")";
    try
    {
      realResultSet.updateTimestamp(columnName, x);
    }
    catch (SQLException s)
    {
      reportException(methodCall, s);
      throw s;
    }
    reportReturn(methodCall, (Object[]) null);
  }

  public boolean first() throws SQLException
  {
    String methodCall = "first()";
    try
    {
      return reportReturn(methodCall, realResultSet.first(), (Object[]) null);
    }
    catch (SQLException s)
    {
      reportException(methodCall, s);
      throw s;
    }
  }

  public String getCursorName() throws SQLException
  {
    String methodCall = "getCursorName()";
    try
    {
      return (String) reportReturn(methodCall, realResultSet.getCursorName(), (Object[]) null);
    }
    catch (SQLException s)
    {
      reportException(methodCall, s);
      throw s;
    }
  }

  public int findColumn(String columnName) throws SQLException
  {
    String methodCall = "findColumn(" + columnName + ")";
    try
    {
      return reportReturn(methodCall, realResultSet.findColumn(columnName), columnName);
    }
    catch (SQLException s)
    {
      reportException(methodCall, s);
      throw s;
    }
  }

  public boolean wasNull() throws SQLException
  {
    String methodCall = "wasNull()";
    try
    {
      return reportReturn(methodCall, realResultSet.wasNull(), (Object[]) null);
    }
    catch (SQLException s)
    {
      reportException(methodCall, s);
      throw s;
    }
  }

  public void updateBinaryStream(int columnIndex, InputStream x, int length) throws SQLException
  {
    String methodCall = "updateBinaryStream(" + columnIndex + ", " + x + ", " + length + ")";
    try
    {
      realResultSet.updateBinaryStream(columnIndex, x, length);
    }
    catch (SQLException s)
    {
      reportException(methodCall, s);
      throw s;
    }
    reportReturn(methodCall, (Object[]) null);
  }

  public void updateBinaryStream(String columnName, InputStream x, int length) throws SQLException
  {
    String methodCall = "updateBinaryStream(" + columnName + ", " + x + ", " + length + ")";
    try
    {
      realResultSet.updateBinaryStream(columnName, x, length);
    }
    catch (SQLException s)
    {
      reportException(methodCall, s);
      throw s;
    }
    reportReturn(methodCall, (Object[]) null);
  }

  public String getString(int columnIndex) throws SQLException
  {
    String methodCall = "getString(" + columnIndex + ")";
    try
    {
      return (String) reportReturn(methodCall, realResultSet.getString(columnIndex), columnIndex);
    }
    catch (SQLException s)
    {
      reportException(methodCall, s);
      throw s;
    }
  }

  public String getString(String columnName) throws SQLException
  {
    String methodCall = "getString(" + columnName + ")";
    try
    {
      return (String) reportReturn(methodCall, realResultSet.getString(columnName), columnName);
    }
    catch (SQLException s)
    {
      reportException(methodCall, s);
      throw s;
    }
  }

  public Reader getCharacterStream(int columnIndex) throws SQLException
  {
    String methodCall = "getCharacterStream(" + columnIndex + ")";
    try
    {
      return (Reader) reportReturn(methodCall, realResultSet.getCharacterStream(columnIndex), columnIndex);
    }
    catch (SQLException s)
    {
      reportException(methodCall, s);
      throw s;
    }
  }

  public Reader getCharacterStream(String columnName) throws SQLException
  {
    String methodCall = "getCharacterStream(" + columnName + ")";
    try
    {
      return (Reader) reportReturn(methodCall, realResultSet.getCharacterStream(columnName), columnName);
    }
    catch (SQLException s)
    {
      reportException(methodCall, s);
      throw s;
    }
  }

  public void setFetchDirection(int direction) throws SQLException
  {
    String methodCall = "setFetchDirection(" + direction + ")";
    try
    {
      realResultSet.setFetchDirection(direction);
    }
    catch (SQLException s)
    {
      reportException(methodCall, s);
      throw s;
    }
    reportReturn(methodCall, (Object[]) null);
  }

  public void updateCharacterStream(int columnIndex, Reader x, int length) throws SQLException
  {
    String methodCall = "updateCharacterStream(" + columnIndex + ", " + x + ", " + length + ")";
    try
    {
      realResultSet.updateCharacterStream(columnIndex, x, length);
    }
    catch (SQLException s)
    {
      reportException(methodCall, s);
      throw s;
    }
    reportReturn(methodCall, (Object[]) null);
  }

  public void updateCharacterStream(String columnName, Reader reader, int length) throws SQLException
  {
    String methodCall = "updateCharacterStream(" + columnName + ", " + reader + ", " + length + ")";
    try
    {
      realResultSet.updateCharacterStream(columnName, reader, length);
    }
    catch (SQLException s)
    {
      reportException(methodCall, s);
      throw s;
    }
    reportReturn(methodCall, (Object[]) null);
  }

  public byte getByte(int columnIndex) throws SQLException
  {
    String methodCall = "getByte(" + columnIndex + ")";
    try
    {
      return reportReturn(methodCall, realResultSet.getByte(columnIndex), columnIndex);
    }
    catch (SQLException s)
    {
      reportException(methodCall, s);
      throw s;
    }
  }

  public byte getByte(String columnName) throws SQLException
  {
    String methodCall = "getByte(" + columnName + ")";
    try
    {
      return reportReturn(methodCall, realResultSet.getByte(columnName), columnName);
    }
    catch (SQLException s)
    {
      reportException(methodCall, s);
      throw s;
    }
  }

  public void updateTime(int columnIndex, Time x) throws SQLException
  {
    String methodCall = "updateTime(" + columnIndex + ", " + x + ")";
    try
    {
      realResultSet.updateTime(columnIndex, x);
    }
    catch (SQLException s)
    {
      reportException(methodCall, s);
      throw s;
    }
    reportReturn(methodCall, (Object[]) null);
  }

  public void updateTime(String columnName, Time x) throws SQLException
  {
    String methodCall = "updateTime(" + columnName + ", " + x + ")";
    try
    {
      realResultSet.updateTime(columnName, x);
    }
    catch (SQLException s)
    {
      reportException(methodCall, s);
      throw s;
    }
    reportReturn(methodCall, (Object[]) null);
  }

  public byte[] getBytes(int columnIndex) throws SQLException
  {
    String methodCall = "getBytes(" + columnIndex + ")";
    try
    {
      return (byte[]) reportReturn(methodCall, realResultSet.getBytes(columnIndex), columnIndex);
    }
    catch (SQLException s)
    {
      reportException(methodCall, s);
      throw s;
    }
  }

  public byte[] getBytes(String columnName) throws SQLException
  {
    String methodCall = "getBytes(" + columnName + ")";
    try
    {
      return (byte[]) reportReturn(methodCall, realResultSet.getBytes(columnName), (Object[]) null);
    }
    catch (SQLException s)
    {
      reportException(methodCall, s);
      throw s;
    }
  }

  public boolean isAfterLast() throws SQLException
  {
    String methodCall = "isAfterLast()";
    try
    {
      return reportReturn(methodCall, realResultSet.isAfterLast(), (Object[]) null);
    }
    catch (SQLException s)
    {
      reportException(methodCall, s);
      throw s;
    }
  }

  public void updateObject(int columnIndex, Object x, int scale) throws SQLException
  {
    String methodCall = "updateObject(" + columnIndex + ", " + x + ", " + scale + ")";
    try
    {
      realResultSet.updateObject(columnIndex, x, scale);
    }
    catch (SQLException s)
    {
      reportException(methodCall, s);
      throw s;
    }
    reportReturn(methodCall, (Object[]) null);
  }

  public void updateObject(int columnIndex, Object x) throws SQLException
  {
    String methodCall = "updateObject(" + columnIndex + ", " + x + ")";
    try
    {
      realResultSet.updateObject(columnIndex, x);
    }
    catch (SQLException s)
    {
      reportException(methodCall, s);
      throw s;
    }
    reportReturn(methodCall, (Object[]) null);
  }

  public void updateObject(String columnName, Object x, int scale) throws SQLException
  {
    String methodCall = "updateObject(" + columnName + ", " + x + ", " + scale + ")";
    try
    {
      realResultSet.updateObject(columnName, x, scale);
    }
    catch (SQLException s)
    {
      reportException(methodCall, s);
      throw s;
    }
    reportReturn(methodCall, (Object[]) null);
  }

  public void updateObject(String columnName, Object x) throws SQLException
  {
    String methodCall = "updateObject(" + columnName + ", " + x + ")";
    try
    {
      realResultSet.updateObject(columnName, x);
    }
    catch (SQLException s)
    {
      reportException(methodCall, s);
      throw s;
    }
    reportReturn(methodCall, (Object[]) null);
  }

  public int getFetchDirection() throws SQLException
  {
    String methodCall = "getFetchDirection()";
    try
    {
      return reportReturn(methodCall, realResultSet.getFetchDirection(), (Object[]) null);
    }
    catch (SQLException s)
    {
      reportException(methodCall, s);
      throw s;
    }
  }

  public long getLong(int columnIndex) throws SQLException
  {
    String methodCall = "getLong(" + columnIndex + ")";
    try
    {
      return reportReturn(methodCall, realResultSet.getLong(columnIndex), columnIndex);
    }
    catch (SQLException s)
    {
      reportException(methodCall, s);
      throw s;
    }
  }

  public long getLong(String columnName) throws SQLException
  {
    String methodCall = "getLong(" + columnName + ")";
    try
    {
      return reportReturn(methodCall, realResultSet.getLong(columnName), columnName);
    }
    catch (SQLException s)
    {
      reportException(methodCall, s);
      throw s;
    }
  }

  public boolean isFirst() throws SQLException
  {
    String methodCall = "isFirst()";
    try
    {
      return reportReturn(methodCall, realResultSet.isFirst(), (Object[]) null);
    }
    catch (SQLException s)
    {
      reportException(methodCall, s);
      throw s;
    }
  }

  public void insertRow() throws SQLException
  {
    String methodCall = "insertRow()";
    try
    {
      realResultSet.insertRow();
    }
    catch (SQLException s)
    {
      reportException(methodCall, s);
      throw s;
    }
  }

  public float getFloat(int columnIndex) throws SQLException
  {
    String methodCall = "getFloat(" + columnIndex + ")";
    try
    {
      return reportReturn(methodCall, realResultSet.getFloat(columnIndex), columnIndex);
    }
    catch (SQLException s)
    {
      reportException(methodCall, s);
      throw s;
    }
  }

  public float getFloat(String columnName) throws SQLException
  {
    String methodCall = "getFloat(" + columnName + ")";
    try
    {
      return reportReturn(methodCall, realResultSet.getFloat(columnName), columnName);
    }
    catch (SQLException s)
    {
      reportException(methodCall, s);
      throw s;
    }
  }

  public boolean isLast() throws SQLException
  {
    String methodCall = "isLast()";
    try
    {
      return reportReturn(methodCall, realResultSet.isLast(), (Object[]) null);
    }
    catch (SQLException s)
    {
      reportException(methodCall, s);
      throw s;
    }
  }

  public void setFetchSize(int rows) throws SQLException
  {
    String methodCall = "setFetchSize(" + rows + ")";
    try
    {
      realResultSet.setFetchSize(rows);
    }
    catch (SQLException s)
    {
      reportException(methodCall, s);
      throw s;
    }
    reportReturn(methodCall, (Object[]) null);
  }

  public void updateRow() throws SQLException
  {
    String methodCall = "updateRow()";
    try
    {
      realResultSet.updateRow();
    }
    catch (SQLException s)
    {
      reportException(methodCall, s);
      throw s;
    }
    reportReturn(methodCall, (Object[]) null);
  }

  public void beforeFirst() throws SQLException
  {
    String methodCall = "beforeFirst()";
    try
    {
      realResultSet.beforeFirst();
    }
    catch (SQLException s)
    {
      reportException(methodCall, s);
      throw s;
    }
    reportReturn(methodCall, (Object[]) null);
  }

  /**
   * @deprecated
   */
  public BigDecimal getBigDecimal(int columnIndex, int scale) throws SQLException
  {
    String methodCall = "getBigDecimal(" + columnIndex + ", " + scale + ")";
    try
    {
      return (BigDecimal) reportReturn(methodCall, realResultSet.getBigDecimal(columnIndex, scale), columnIndex, scale);
    }
    catch (SQLException s)
    {
      reportException(methodCall, s);
      throw s;
    }
  }

  /**
   * @deprecated
   */
  public BigDecimal getBigDecimal(String columnName, int scale) throws SQLException
  {
    String methodCall = "getBigDecimal(" + columnName + ", " + scale + ")";
    try
    {
      return (BigDecimal) reportReturn(methodCall, realResultSet.getBigDecimal(columnName, scale), columnName, scale);
    }
    catch (SQLException s)
    {
      reportException(methodCall, s);
      throw s;
    }
  }

  public BigDecimal getBigDecimal(int columnIndex) throws SQLException
  {
    String methodCall = "getBigDecimal(" + columnIndex + ")";
    try
    {
      return (BigDecimal) reportReturn(methodCall, realResultSet.getBigDecimal(columnIndex), columnIndex);
    }
    catch (SQLException s)
    {
      reportException(methodCall, s);
      throw s;
    }
  }

  public BigDecimal getBigDecimal(String columnName) throws SQLException
  {
    String methodCall = "getBigDecimal(" + columnName + ")";
    try
    {
      return (BigDecimal) reportReturn(methodCall, realResultSet.getBigDecimal(columnName), columnName);
    }
    catch (SQLException s)
    {
      reportException(methodCall, s);
      throw s;
    }
  }

  public void afterLast() throws SQLException
  {
    String methodCall = "afterLast()";
    try
    {
      realResultSet.afterLast();
    }
    catch (SQLException s)
    {
      reportException(methodCall, s);
      throw s;
    }
    reportReturn(methodCall, (Object[]) null);
  }

  public void refreshRow() throws SQLException
  {
    String methodCall = "refreshRow()";
    try
    {
      realResultSet.refreshRow();
    }
    catch (SQLException s)
    {
      reportException(methodCall, s);
      throw s;
    }
  }

  public <T> T unwrap(Class<T> iface) throws SQLException {
    String methodCall = "unwrap(" + (iface==null?"null":iface.getName()) + ")";
    try
    {
      //todo: double check this logic
      return (T)reportReturn(methodCall, (iface != null && (iface == ResultSet.class || iface == Spy.class))?(T)this:realResultSet.unwrap(iface));
    }
    catch (SQLException s)
    {
      reportException(methodCall,s);
      throw s;
    }
  }

  public boolean isWrapperFor(Class<?> iface) throws SQLException
  {
    String methodCall = "isWrapperFor(" + (iface==null?"null":iface.getName()) + ")";
    try
    {
      return reportReturn(methodCall, (iface != null && (iface == ResultSet.class || iface == Spy.class)) ||
          realResultSet.isWrapperFor(iface));
    }
    catch (SQLException s)
    {
      reportException(methodCall,s);
      throw s;
    }
  }
}