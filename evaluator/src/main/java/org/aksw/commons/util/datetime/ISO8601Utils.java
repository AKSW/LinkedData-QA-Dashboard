package org.aksw.commons.util.datetime;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 * 
 * Adapted from http://developer.marklogic.com/learn/2004-09-dates
 * 
 * 
 * @author Claus Stadler <cstadler@informatik.uni-leipzig.de>
 *
 */
public class ISO8601Utils {
	public static DateFormat createDateFormat() {
		return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");	
	}
}
