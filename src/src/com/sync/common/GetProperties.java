package com.sync.common;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import com.sync.common.ReadProperties;

/**
 * GetProperties
 * 
 * @author sasou <admin@php-gene.com> web:http://www.php-gene.com/
 * @version 1.0.0
 */

public class GetProperties {
	// debug
	public static int system_debug = 0;
	// canal
	public static CanalData canal = new CanalData();

	// target
	public static Map<String, TargetData> target = new HashMap<String, TargetData>();

	public GetProperties() {
		// read config
		ReadProperties readProperties = new ReadProperties();
		Properties p = readProperties.readProperties();
		String tmp = "";

		// debug
		tmp = String.valueOf(p.get("system.debug"));
		if (!"".equals(tmp)) {
			system_debug = Integer.parseInt(tmp);
		}
		
		// canal
		tmp = String.valueOf(p.get("canal.ip"));
		if (!"".equals(tmp)) {
			canal.setIp(tmp);
		}

		tmp = String.valueOf(p.get("canal.port"));
		if (!"".equals(tmp)) {
			canal.setPort(Integer.parseInt(tmp));
		}
		canal.setDestination(String.valueOf(p.get("canal.destination")));
		canal.setUsername(String.valueOf(p.get("canal.username")));
		canal.setPassword(String.valueOf(p.get("canal.password")));

		tmp = String.valueOf(p.get("canal.filter"));
		if (!"".equals(tmp)) {
			canal.setFilter(tmp);
		}

		// target
		if (canal.destination != null) {
			int num = canal.destination.length;
			if (num > 0) {
				for (int i = 0; i < num; i++) {
					TargetData target_tmp = new TargetData();

					tmp = String.valueOf(p.get(canal.destination[i] + ".target_type"));
					if (!"".equals(tmp)) {
						target_tmp.setType(tmp);
					}
					tmp = String.valueOf(p.get(canal.destination[i] + ".target_ip"));
					if (!"".equals(tmp)) {
						target_tmp.setIp(tmp);
					}
					if ("kafka".equals(target_tmp.type)) {
						target_tmp.setPort(9092);
					}
					if ("redis".equals(target_tmp.type)) {
						target_tmp.setPort(6379);
					}
					if ("elasticsearch".equals(target_tmp.type)) {
						target_tmp.setPort(9200);
					}
					tmp = String.valueOf(p.get(canal.destination[i] + ".target_port"));
					if (!"".equals(tmp)) {
						target_tmp.setPort(Integer.parseInt(tmp));
					}
					target.put(canal.destination[i], target_tmp);
				}
			}
		}
	}
}