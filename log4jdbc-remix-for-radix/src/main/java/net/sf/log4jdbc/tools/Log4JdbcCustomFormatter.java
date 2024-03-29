/**
 * Copyright 2010 Tim Azzopardi
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
 *
 */
package net.sf.log4jdbc.tools;

import net.sf.log4jdbc.Slf4jSpyLogDelegator;
import net.sf.log4jdbc.Spy;

public class Log4JdbcCustomFormatter extends Slf4jSpyLogDelegator {

	private static String INDENT = "    ";
	
    private LoggingType loggingType = LoggingType.DISABLED;

    private String margin = "";

    private String sqlPrefix = "";

    public int getMargin() {
        return margin.length();
    }

    public void setMargin(int n) {
        margin = String.format("%1$#" + n + "s", "");
    }

    private int callerStackCount = 1;
    
    public void setCallerStackCount(int callerStackCount) {
    	this.callerStackCount = callerStackCount;
    }

    public void setIndentSize(int indentSize) {
    	INDENT = String.format("%1$#"+indentSize+"s", "");
    }

    
    public Log4JdbcCustomFormatter() {
    }


    
    @Override
    public String sqlOccured(Spy spy, String methodCall, String rawSql) {
        if (loggingType == LoggingType.DISABLED) {
            return "";
        }

        // Remove all existing cr lf, unless MULTI_LINE
        if (loggingType != LoggingType.MULTI_LINE) {
            rawSql = rawSql.replaceAll("\r", "");
            rawSql = rawSql.replaceAll("\n", "");
        }
        final String fromClause = " from ";
        String sql = rawSql;
        if (loggingType == LoggingType.MULTI_LINE) {
            final String whereClause = "where ";
            final String andClause = "and ";
            final String subSelectClauseS = "\\(select";
            final String subSelectClauseR = " (select";
            sql = sql.replaceAll(fromClause, "\n" + margin + fromClause);
            sql = sql.replaceAll(whereClause, "\n" + margin + whereClause);
            sql = sql.replaceAll(andClause, "\n" + margin + andClause);
            sql = sql.replaceAll(subSelectClauseS, "\n" + margin + subSelectClauseR);
        }
        if (loggingType == LoggingType.SINGLE_LINE_TWO_COLUMNS) {
            if (sql.startsWith("select")) {
                String from = sql.substring(sql.indexOf(fromClause) + fromClause.length());
                sql = from + "\t" + sql;
            }
        }
        String callerInfo = getCallerInfo();
        getSqlOnlyLogger().info("\n"+INDENT+sqlPrefix + sql + callerInfo);
        return sql;
    }

    private String getCallerInfo() {
    	
    	if(callerStackCount<1) {
    		return "";
    	}
    	
    	Throwable t = new Throwable();
    	t.fillInStackTrace();
    	StackTraceElement[] stackTraceElements = t.getStackTrace();
    	int stackedCount = 0;
    	
    	StringBuilder sb = new StringBuilder();
    	sb.append("\n"+INDENT+"called by");
    	
    	for(StackTraceElement element : stackTraceElements) {
    		if(stackedCount==callerStackCount) { break; }
    		String className = element.getClassName();
    		if(className.startsWith("com.kthcorp.radix")) {
    			sb.append("\n").append(INDENT);
    			sb.append(className).append(".").append(element.getMethodName()).append("()");
    			sb.append(" (").append(element.getFileName()).append(":").append(element.getLineNumber()).append(")");
        		stackedCount++;
    		}
    	}
    	
    	if(stackedCount>0) { 
    		sb.append("\n");
    	}
    	
		return sb.toString();
	}

	@Override
    public String sqlOccured(Spy spy, String methodCall, String[] sqls) {
        String s = "";
        for (int i = 0; i < sqls.length; i++) {
            s += sqlOccured(spy, methodCall, sqls[i]) + String.format("%n");
        }
        return s;
    }

    public LoggingType getLoggingType() {
        return loggingType;
    }

    public void setLoggingType(LoggingType loggingType) {
        this.loggingType = loggingType;
    }

    public String getSqlPrefix()
    {
        return sqlPrefix;
    }

    public void setSqlPrefix(String sqlPrefix)
    {
        this.sqlPrefix = sqlPrefix;
    }

}