package com.kthcorp.radix.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.helpers.FormattingTuple;

/**
 * org.slf4j.helpers.MessageFormatter 소스를 그대로 카피해 왔다.
 * convertUuidStringIfPossible() 메소드를 추가하였고,
 * arrayFormat() 내에서 convertUuidStringIfPossible()를 호출하도록 수정.
 * 
 */
@SuppressWarnings( {"rawtypes", "unchecked"} )
final public class UuidViewableMessageFormatter {
	static final char DELIM_START = '{';
	static final char DELIM_STOP = '}';
	static final String DELIM_STR = "{}";
	private static final char ESCAPE_CHAR = '\\';

	// 원 arg는 건드리지 않고, clone을 반환한다.
	private static Object convertUuidStringIfPossible(Object arg) {

		if(arg==null) { return arg; }

		if(arg instanceof Object[]) {
			Object[] array = (Object[])arg;
			Object[] newArray = new Object[array.length];
			for(int i=0; i<array.length; i++) {
				newArray[i] = convertUuidStringIfPossible(array[i]);
			}
			return newArray;
		}
		else if(arg instanceof Map) {
			Map<Object, Object> map = (Map<Object, Object>)arg;
			Map<Object, Object> newMap = new HashMap<Object, Object>();
			for(Object key : map.keySet()) {
				newMap.put(key, convertUuidStringIfPossible(map.get(key)));
			}
			return newMap;
		}
		else if(arg instanceof Collection) {
			Collection<Object> collection = (Collection<Object>)arg;
			Collection<Object> newCollection = new ArrayList<Object>();
			for(Object item : collection) {
				newCollection.add(convertUuidStringIfPossible(item));
			}
			return newCollection;
		}

		try {
			return UUIDUtils.getString((byte[])arg);
		} catch(Exception e) {
			return arg;
		}

	}

	/**
	 * Performs single argument substitution for the 'messagePattern' passed as
	 * parameter.
	 * <p>
	 * For example,
	 * 
	 * <pre>
	 * MessageFormatter.format(&quot;Hi {}.&quot;, &quot;there&quot;);
	 * </pre>
	 * 
	 * will return the string "Hi there.".
	 * <p>
	 * 
	 * @param messagePattern
	 *          The message pattern which will be parsed and formatted
	 * @param argument
	 *          The argument to be substituted in place of the formatting anchor
	 * @return The formatted message
	 */
	final public static FormattingTuple format(String messagePattern, Object arg) {
		return arrayFormat(messagePattern, new Object[] { arg });
	}

	/**
	 * 
	 * Performs a two argument substitution for the 'messagePattern' passed as
	 * parameter.
	 * <p>
	 * For example,
	 * 
	 * <pre>
	 * MessageFormatter.format(&quot;Hi {}. My name is {}.&quot;, &quot;Alice&quot;, &quot;Bob&quot;);
	 * </pre>
	 * 
	 * will return the string "Hi Alice. My name is Bob.".
	 * 
	 * @param messagePattern
	 *          The message pattern which will be parsed and formatted
	 * @param arg1
	 *          The argument to be substituted in place of the first formatting
	 *          anchor
	 * @param arg2
	 *          The argument to be substituted in place of the second formatting
	 *          anchor
	 * @return The formatted message
	 */
	final public static FormattingTuple format(final String messagePattern,
			Object arg1, Object arg2) {
		return arrayFormat(messagePattern, new Object[] { arg1, arg2 });
	}

	static final Throwable getThrowableCandidate(Object[] argArray) {
		if (argArray == null || argArray.length == 0) {
			return null;
		}

		final Object lastEntry = argArray[argArray.length - 1];
		if (lastEntry instanceof Throwable) {
			return (Throwable) lastEntry;
		}
		return null;
	}

	/**
	 * Same principle as the {@link #format(String, Object)} and
	 * {@link #format(String, Object, Object)} methods except that any number of
	 * arguments can be passed in an array.
	 * 
	 * @param messagePattern
	 *          The message pattern which will be parsed and formatted
	 * @param argArray
	 *          An array of arguments to be substituted in place of formatting
	 *          anchors
	 * @return The formatted message
	 */
	final public static FormattingTuple arrayFormat(final String messagePattern,
			final Object[] orgArgArray) {

		Throwable throwableCandidate = getThrowableCandidate(orgArgArray);

		if (messagePattern == null) {
			return new FormattingTuple(null, orgArgArray, throwableCandidate);
		}

		if (orgArgArray == null) {
			return new FormattingTuple(messagePattern);
		}

		int i = 0;
		int j;
		StringBuffer sbuf = new StringBuffer(messagePattern.length() + 50);

		// List, Map, Set, array내의 byte[] uuid를 String의 uuid로 바꾼다. 
		Object[] argArray = (Object[]) convertUuidStringIfPossible(orgArgArray);
		
		
		int L;
		for (L = 0; L < argArray.length; L++) {

			j = messagePattern.indexOf(DELIM_STR, i);

			if (j == -1) {
				// no more variables
				if (i == 0) { // this is a simple string
					return new FormattingTuple(messagePattern, argArray,
							throwableCandidate);
				} else { // add the tail string which contains no variables and return
					// the result.
					sbuf.append(messagePattern.substring(i, messagePattern.length()));
					return new FormattingTuple(sbuf.toString(), argArray,
							throwableCandidate);
				}
			} else {
				if (isEscapedDelimeter(messagePattern, j)) {
					if (!isDoubleEscaped(messagePattern, j)) {
						L--; // DELIM_START was escaped, thus should not be incremented
						sbuf.append(messagePattern.substring(i, j - 1));
						sbuf.append(DELIM_START);
						i = j + 1;
					} else {
						// The escape character preceding the delimiter start is
						// itself escaped: "abc x:\\{}"
						// we have to consume one backward slash
						sbuf.append(messagePattern.substring(i, j - 1));
						deeplyAppendParameter(sbuf, argArray[L], new HashMap());
						i = j + 2;
					}
				} else {
					// normal case
					sbuf.append(messagePattern.substring(i, j));
					deeplyAppendParameter(sbuf, argArray[L], new HashMap());
					i = j + 2;
				}
			}
		}
		// append the characters following the last {} pair.
		sbuf.append(messagePattern.substring(i, messagePattern.length()));
		if (L < argArray.length - 1) {
			return new FormattingTuple(sbuf.toString(), argArray, throwableCandidate);
		} else {
			return new FormattingTuple(sbuf.toString(), argArray, null);
		}
	}

	final static boolean isEscapedDelimeter(String messagePattern,
			int delimeterStartIndex) {

		if (delimeterStartIndex == 0) {
			return false;
		}
		char potentialEscape = messagePattern.charAt(delimeterStartIndex - 1);
		if (potentialEscape == ESCAPE_CHAR) {
			return true;
		} else {
			return false;
		}
	}

	final static boolean isDoubleEscaped(String messagePattern,
			int delimeterStartIndex) {
		if (delimeterStartIndex >= 2
				&& messagePattern.charAt(delimeterStartIndex - 2) == ESCAPE_CHAR) {
			return true;
		} else {
			return false;
		}
	}

	// special treatment of array values was suggested by 'lizongbo'
	private static void deeplyAppendParameter(StringBuffer sbuf, Object o,
			Map seenMap) {
		if (o == null) {
			sbuf.append("null");
			return;
		}
		if (!o.getClass().isArray()) {
			safeObjectAppend(sbuf, o);
		} else {
			// check for primitive array types because they
			// unfortunately cannot be cast to Object[]
			if (o instanceof boolean[]) {
				booleanArrayAppend(sbuf, (boolean[]) o);
			} else if (o instanceof byte[]) {
				byteArrayAppend(sbuf, (byte[]) o);
			} else if (o instanceof char[]) {
				charArrayAppend(sbuf, (char[]) o);
			} else if (o instanceof short[]) {
				shortArrayAppend(sbuf, (short[]) o);
			} else if (o instanceof int[]) {
				intArrayAppend(sbuf, (int[]) o);
			} else if (o instanceof long[]) {
				longArrayAppend(sbuf, (long[]) o);
			} else if (o instanceof float[]) {
				floatArrayAppend(sbuf, (float[]) o);
			} else if (o instanceof double[]) {
				doubleArrayAppend(sbuf, (double[]) o);
			} else {
				objectArrayAppend(sbuf, (Object[]) o, seenMap);
			}
		}
	}

	private static void safeObjectAppend(StringBuffer sbuf, Object o) {
		try {
			String oAsString = o.toString();
			sbuf.append(oAsString);
		} catch (Throwable t) {
			System.err
			.println("SLF4J: Failed toString() invocation on an object of type ["
					+ o.getClass().getName() + "]");
			t.printStackTrace();
			sbuf.append("[FAILED toString()]");
		}

	}


	private static void objectArrayAppend(StringBuffer sbuf, Object[] a,
			Map seenMap) {
		sbuf.append('[');
		if (!seenMap.containsKey(a)) {
			seenMap.put(a, null);
			final int len = a.length;
			for (int i = 0; i < len; i++) {
				deeplyAppendParameter(sbuf, a[i], seenMap);
				if (i != len - 1)
					sbuf.append(", ");
			}
			// allow repeats in siblings
			seenMap.remove(a);
		} else {
			sbuf.append("...");
		}
		sbuf.append(']');
	}

	private static void booleanArrayAppend(StringBuffer sbuf, boolean[] a) {
		sbuf.append('[');
		final int len = a.length;
		for (int i = 0; i < len; i++) {
			sbuf.append(a[i]);
			if (i != len - 1)
				sbuf.append(", ");
		}
		sbuf.append(']');
	}

	private static void byteArrayAppend(StringBuffer sbuf, byte[] a) {

		try {
			String uuidInString = UUIDUtils.getString(a);
			sbuf.append(uuidInString);
			return;
		} catch(Exception e) {
			// ignore
		}

		sbuf.append('[');
		final int len = a.length;
		for (int i = 0; i < len; i++) {
			sbuf.append(a[i]);
			if (i != len - 1)
				sbuf.append(", ");
		}
		sbuf.append(']');
	}

	private static void charArrayAppend(StringBuffer sbuf, char[] a) {
		sbuf.append('[');
		final int len = a.length;
		for (int i = 0; i < len; i++) {
			sbuf.append(a[i]);
			if (i != len - 1)
				sbuf.append(", ");
		}
		sbuf.append(']');
	}

	private static void shortArrayAppend(StringBuffer sbuf, short[] a) {
		sbuf.append('[');
		final int len = a.length;
		for (int i = 0; i < len; i++) {
			sbuf.append(a[i]);
			if (i != len - 1)
				sbuf.append(", ");
		}
		sbuf.append(']');
	}

	private static void intArrayAppend(StringBuffer sbuf, int[] a) {
		sbuf.append('[');
		final int len = a.length;
		for (int i = 0; i < len; i++) {
			sbuf.append(a[i]);
			if (i != len - 1)
				sbuf.append(", ");
		}
		sbuf.append(']');
	}

	private static void longArrayAppend(StringBuffer sbuf, long[] a) {
		sbuf.append('[');
		final int len = a.length;
		for (int i = 0; i < len; i++) {
			sbuf.append(a[i]);
			if (i != len - 1)
				sbuf.append(", ");
		}
		sbuf.append(']');
	}

	private static void floatArrayAppend(StringBuffer sbuf, float[] a) {
		sbuf.append('[');
		final int len = a.length;
		for (int i = 0; i < len; i++) {
			sbuf.append(a[i]);
			if (i != len - 1)
				sbuf.append(", ");
		}
		sbuf.append(']');
	}

	private static void doubleArrayAppend(StringBuffer sbuf, double[] a) {
		sbuf.append('[');
		final int len = a.length;
		for (int i = 0; i < len; i++) {
			sbuf.append(a[i]);
			if (i != len - 1)
				sbuf.append(", ");
		}
		sbuf.append(']');
	}
}
