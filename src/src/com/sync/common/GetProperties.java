package com.sync.common;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import com.alibaba.fastjson.JSONObject;
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

	@SuppressWarnings("unchecked")
	public static boolean update() {
		// read config
		Properties prop = ReadProperties.readProperties();
		String tmp = "";

		// debug
		tmp = prop.getProperty("system.debug", "");
		if (!"".equals(tmp)) {
			system_debug = Integer.parseInt(tmp);
		}
		
		// canal
		tmp = prop.getProperty("canal.ip", "");
		if (!"".equals(tmp)) {
			canal.setIp(tmp);
		}

		tmp = prop.getProperty("canal.port", "");
		if (!"".equals(tmp)) {
			canal.setPort(Integer.parseInt(tmp));
		}
		canal.setDestination(prop.getProperty("canal.destination", ""));
		canal.setUsername(prop.getProperty("canal.username", ""));
		canal.setPassword(prop.getProperty("canal.password", ""));

		// target
		if (canal.destination != null) {
			TargetData target_tmp = null;
			int num = canal.destination.length;
			if (num > 0) {
				for (int i = 0; i < num; i++) {
					if (target.containsKey(canal.destination[i])) {
						target_tmp = target.get(canal.destination[i]);
					} else {
						target_tmp = new TargetData();
					}
					
					tmp = prop.getProperty(canal.destination[i] + ".target_type", "");
					if (!"".equals(tmp)) {
						target_tmp.setType(tmp);
					}
					tmp = prop.getProperty(canal.destination[i] + ".target_ip", "");
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
					if ("httpmq".equals(target_tmp.type)) {
						target_tmp.setPort(1218);
					}
					tmp = prop.getProperty(canal.destination[i] + ".target_port", "");
					if (!"".equals(tmp)) {
						target_tmp.setPort(Integer.parseInt(tmp));
					}
					tmp = prop.getProperty(canal.destination[i] + ".target_deep", "");
					if (!"".equals(tmp)) {
						target_tmp.setDeep(Integer.parseInt(tmp));
					}
					tmp = prop.getProperty(canal.destination[i] + ".target_filter_api", "");
					target_tmp.setFilter(tmp);
					if (!"".equals(tmp)) {
						try {
							String json = HttpClient.sendGet(tmp, "");
							@SuppressWarnings("rawtypes")
							Map filterMap = new HashMap();
							syncCache(json, filterMap);
							target_tmp.setFilterMap(filterMap);
						} catch (Exception e) {

						}
					}
					
					if ("cache".equals(target_tmp.type)) {
						target_tmp.setPlugin(prop.getProperty(canal.destination[i] + ".target_plugin", ""));
						target_tmp.setSign(prop.getProperty(canal.destination[i] + ".target_version_sign", ""));
					}
					target.put(canal.destination[i], target_tmp);
				}
			}
		}
		prop.clear();
		return true;
	}
	
	public static String get(String key) {
		// read config
		Properties prop = ReadProperties.readProperties();
		return prop.getProperty(key, "");
	}
	
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static void syncCache(String jsonStr, Map<String, Map> cacheMap) {
        JSONObject jsonobject = JSONObject.parseObject(jsonStr);
        JSONObject db = (JSONObject) jsonobject.get("data");
        for (String dbName : db.keySet()){   
	        JSONObject table = (JSONObject) db.get(dbName);
	        for (String tableName : table.keySet()){    
	        	String key = dbName + "." + tableName;
		        Map tableMap = null;
		        if (cacheMap.containsKey(key)) {
		        	tableMap = (Map) cacheMap.get(key);
		        } else {
		        	tableMap = new HashMap();
		        	cacheMap.put(key, tableMap);
		        }
		        JSONObject field = (JSONObject) table.get(tableName);
		        for (String fieldName : field.keySet()){  
			        if (!tableMap.containsKey(fieldName)) {
			        	tableMap.put(fieldName, "");
			        }
		        }
	        }
        }
	}
	
}