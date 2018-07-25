package com.sync.common;

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
	
}
