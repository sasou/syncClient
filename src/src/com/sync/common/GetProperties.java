package com.sync.common;

import java.util.Properties;

import com.sync.common.ReadProperties;

/**
 * config
 * 
 * @author sasou <admin@php-gene.com> web:http://www.php-gene.com/
 * @version 1.0.0
 */

public class GetProperties {
	// debug
	public static int system_debug = 0;
	// canal
	public static String canal_ip = "127.0.0.1";
	public static int canal_port = 11111;
	public static String[] canal_destination = null;
	public static String canal_username = "";
	public static String canal_password = "";
	public static String canal_filter = "";

	// target
	public static String target_type = "kafka";
	public static String target_ip = "127.0.0.1";
	public static int target_port = 9092;

	public static void getProperties() {
		// read config
		ReadProperties readProperties = new ReadProperties();
		Properties p = readProperties.readProperties();
		String tmp = "";

		// debug
		tmp = String.valueOf(p.get("system_debug"));
		if (!"".equals(tmp)) {
			system_debug = Integer.parseInt(tmp);
		}

		// canal
		tmp = String.valueOf(p.get("canal_ip"));
		if (!"".equals(tmp)) {
			canal_ip = tmp;
		}

		tmp = String.valueOf(p.get("canal_port"));
		if (!"".equals(tmp)) {
			canal_port = Integer.parseInt(tmp);
		}

		canal_destination = String.valueOf(p.get("canal_destination")).split(",");
		canal_username = String.valueOf(p.get("canal_username"));
		canal_password = String.valueOf(p.get("canal_password"));

		tmp = String.valueOf(p.get("canal_filter"));
		if (!"".equals(tmp)) {
			canal_filter = tmp;
		}

		// target
		tmp = String.valueOf(p.get("target_type"));
		if (!"".equals(tmp)) {
			target_type = tmp;
		}
		tmp = String.valueOf(p.get("target_ip"));
		if (!"".equals(tmp)) {
			target_ip = tmp;
		}
		tmp = String.valueOf(p.get("target_port"));
		if (!"".equals(tmp)) {
			target_port = Integer.parseInt(tmp);
		}

	}
}