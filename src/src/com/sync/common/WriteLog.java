package com.sync.common;

/**
 * WriteLog
 * 
 * @author sasou <admin@php-gene.com> web:http://www.php-gene.com/
 * @version 1.0.0
 */
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Calendar;

/**
 * write logString
 * 
 * @param logString
 */
public class WriteLog {
	public static String base = null;

	public static void write(String type, String logString) {
		if (base == null) {
			base = System.getProperty("user.dir");
		}

		String current = base + "/logs/";
		try {
			String logFilePathName = null;
			Calendar cd = Calendar.getInstance();
			int year = cd.get(Calendar.YEAR);
			String month = addZero(cd.get(Calendar.MONTH) + 1);
			String day = addZero(cd.get(Calendar.DAY_OF_MONTH));
			String hour = addZero(cd.get(Calendar.HOUR_OF_DAY));
			String min = addZero(cd.get(Calendar.MINUTE));
			String sec = addZero(cd.get(Calendar.SECOND));
			current += year + "-" + month + "-" + day + "/";

			File fileParentDir = new File(current);
			if (!fileParentDir.exists()) {
				fileParentDir.mkdirs();
			}

			logFilePathName = current + type + ".log";

			FileOutputStream fos = new FileOutputStream(logFilePathName, true);
			String time = "[" + year + "-" + month + "-" + day + " " + hour + ":" + min + ":" + sec + "] ";
			String content = time + logString + "\r\n";
			fos.write(content.getBytes());
			fos.flush();
			fos.close();
			if (GetProperties.system_debug > 0) {
				System.out.println(logString);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * add 0
	 * 
	 * @param i
	 * @return
	 */
	public static String addZero(int i) {
		if (i < 10) {
			String tmpString = "0" + i;
			return tmpString;
		} else {
			return String.valueOf(i);
		}
	}
	
	
	/**
	 * eString
	 * 
	 * @param object e
	 * @return string
	 */
	public static String eString(Exception e) {
		StringWriter sw = null;
	    PrintWriter pw = null;
	    try {
	        sw = new StringWriter();
	        pw =  new PrintWriter(sw);
	        e.printStackTrace(pw);
	        pw.flush();
	        sw.flush();
	    } finally {
	        if (sw != null) {
	            try {
	                sw.close();
	            } catch (IOException e1) {
	                e1.printStackTrace();
	            }
	        }
	        if (pw != null) {
	            pw.close();
	        }
	    }
	 	return sw.toString();
	}

}