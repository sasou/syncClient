package com.alibaba.otter.canal.common;

import java.util.Properties;

import com.alibaba.otter.canal.common.ReadProperties;

public class GetProperties {		
	    //client配置
		public static int debug = 0;
		public static String ip = "127.0.0.1";
		public static int port = 11111;
		public static String destination = "";
		public static String username = "";
		public static String password = "";
		public static String filter = "";
		
	    //kafak配置
		public static String kafkaIp = "127.0.0.1";
		public static int kafkaPort = 9092;


		public static void getProperties() {		
			//读取资源配置文件
			ReadProperties readProperties = new ReadProperties();		
			Properties p = readProperties.readProperties();	
			String tmp = "";
			
			//client配置
			
			tmp = String.valueOf(p.get("debug"));
			if (!"".equals(tmp)) {
				debug =  Integer.parseInt(tmp);
			}
			
			tmp = String.valueOf(p.get("ip"));
			if (!"".equals(tmp)) {
				ip = tmp;
			}

			tmp = String.valueOf(p.get("port"));
			if (!"".equals(tmp)) {
				port =  Integer.parseInt(tmp);
			}
			
			destination = String.valueOf(p.get("destination"));
			username = String.valueOf(p.get("username"));
			password = String.valueOf(p.get("password"));
			
			tmp = String.valueOf(p.get("filter"));
			if (!"".equals(tmp)) {
				filter = tmp;
			}
			
			//kafak配置
			tmp = String.valueOf(p.get("kafkaIp"));
			if (!"".equals(tmp)) {
				kafkaIp = tmp;
			}
			tmp = String.valueOf(p.get("kafkaPort"));
			if (!"".equals(tmp)) {
				kafkaPort =  Integer.parseInt(tmp);
			}
			
		}
}