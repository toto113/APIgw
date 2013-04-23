package com.kthcorp.radix.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Time {
	
	private static String defaultDateFormat = "yyyy-MM-dd HH:mm:ss";
	
	public static String getDefaultDateFormat() {
		return defaultDateFormat;
	}
	
	public static void setDefaultDateFormat(String defaultDateFormat) {
		Time.defaultDateFormat = defaultDateFormat;
	}
	
	public static Date now() {
		return Calendar.getInstance().getTime();
	}
	
	public static String strNow() {
		try {
			SimpleDateFormat sdf = new SimpleDateFormat(Time.getDefaultDateFormat());
			String formatDate = sdf.format(Time.now());
			
			return formatDate;
		} catch(Exception e) {
			return null;
		}
	}
	
	public static Date getDateFromString(String date) {
		try {
			SimpleDateFormat sdf = new SimpleDateFormat(Time.getDefaultDateFormat());
			Date result = sdf.parse(date);
			return result;
		} catch(ParseException e) {
			return null;
		}
	}
	
	public static Date dateAdd(int field, int amount, Date date) {
		Calendar cal = Calendar.getInstance();
		
		cal.setTime(date);
		cal.add(field, amount);
		
		return cal.getTime();
	}
	
	public static Date dateAdd(int field, int amount, String date) {
		try {
			SimpleDateFormat sdf = new SimpleDateFormat(Time.getDefaultDateFormat());
			Date result = sdf.parse(date);
			result = Time.dateAdd(field, amount, result);
			return result;
		} catch(ParseException e) {
			return null;
		}
	}
	
	public static String dateAddFormat(int field, int amount, Date date) {
		try {
			SimpleDateFormat sdf = new SimpleDateFormat(Time.getDefaultDateFormat());
			String formatDate = sdf.format(Time.dateAdd(field, amount, date));
			
			return formatDate;
		} catch(Exception e) {
			return null;
		}
	}
	
	public static int getTimeUnit(String strTimeUnit) {
		if("Y".equals(strTimeUnit)) {
			return Calendar.YEAR;
		} else if("M".equals(strTimeUnit)) {
			return Calendar.MONTH;
		} else if("W".equals(strTimeUnit)) {
			return Calendar.WEEK_OF_YEAR;
		} else if("D".equals(strTimeUnit)) {
			return Calendar.DAY_OF_YEAR;
		} else if("H".equals(strTimeUnit)) {
			return Calendar.HOUR_OF_DAY;
		}
		return 0;
	}
}
