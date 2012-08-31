package org.aksw.linkedqa.shared;

public class ParserUtils {
	public static Double tryParseDouble(Object o) {
		if(o == null) {
			return null;
		} else if(o instanceof Double) {
			return (Double)o;
		}
		
		
		try {
			return Double.valueOf(o.toString());
		} catch(Exception e) {
			return null;
		}
	}
	
	public static Long tryParseLong(Object o, Integer radix)
	{
		if(o == null) {
			return null;
		} else if(o instanceof Long) {
			return (Long)o;
		}
		
		try {
			return radix == null ? Long.valueOf(o.toString()) : Long.valueOf(o.toString(), radix);
		} catch(Exception e) {
			return null;
		}		
	}
}
