package com.alibaba.otter.canal.common;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * 读取资源配置文件
 * @author jacob
 *
 */
public class ReadProperties {
	static final String propertiesFilename = "SysConfig.properties";
	Properties p = new Properties();
	public Properties readProperties() {		
		try {
			InputStream inputStream = new BufferedInputStream(new FileInputStream(System.getProperty("user.dir")+ "/" + propertiesFilename));
			try {
				p.load(inputStream);
			} catch (IOException e) {
				
			}
		} catch (FileNotFoundException e) {
			
		}
		return p;
	}
}
