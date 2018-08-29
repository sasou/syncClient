package com.sync.common;

import java.security.MessageDigest;

public class Tool {

	public static String makeTargetName(String canal_destination, String db, String table) {
	    int type = GetProperties.target.get(canal_destination).deep;
	    String ret = null;
		switch(type) {
		case 1:
			ret = "sync_" + canal_destination + "_" + db + "_" + table;
			break;
		case 2:
			ret = "sync_" + canal_destination + "_" + db;
			break;
		case 3:
			ret = "sync_" + canal_destination;
			break;
		case 4:
			ret = "sync_" + "_" + db + "_" + table;
			break;
		default:
			ret = "sync_" + canal_destination + "_" + db + "_" + table;
			break;
		}
		return ret;
	}
	
	public static String md5(String s) {
		char hexDigits[]={'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'};       
		try {
		    byte[] btInput = s.getBytes("UTF-8");
		    // 获得MD5摘要算法的 MessageDigest 对象
		    MessageDigest mdInst = MessageDigest.getInstance("MD5");
		    // 使用指定的字节更新摘要
		    mdInst.update(btInput);
		    // 获得密文
		    byte[] md = mdInst.digest();
		    // 把密文转换成十六进制的字符串形式
		    int j = md.length;
		    char str[] = new char[j * 2];
		    int k = 0;
		    for (int i = 0; i < j; i++) {
		        byte byte0 = md[i];
		        str[k++] = hexDigits[byte0 >>> 4 & 0xf];
		        str[k++] = hexDigits[byte0 & 0xf];
		    }
		    String rec = new String(str);
		    return rec.toLowerCase();
		} catch (Exception e) {
		    e.printStackTrace();
		    return null;
		}
	}
	
}
