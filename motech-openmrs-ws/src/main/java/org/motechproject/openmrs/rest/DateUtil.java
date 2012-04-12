package org.motechproject.openmrs.rest;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {

	private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
	
	public static String formatToOpenMrsDate(Date date) {
		return dateFormat.format(date);
	}
	
	public static Date parseOpenMrsDate(String date) throws ParseException {
		return dateFormat.parse(date);
	}
	
}
