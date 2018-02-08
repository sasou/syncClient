package com.sync.common;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * config file read
 * 
 * @author sasou <admin@php-gene.com> web:http://www.php-gene.com/
 * @version 1.0.0
 */
public class ReadProperties {
	static final String propertiesFilename = "SysConfig.properties";
	Properties p = new Properties();

	public Properties readProperties() {
		try {
			InputStream inputStream = new BufferedInputStream(
					new FileInputStream(System.getProperty("user.dir") + "/" + propertiesFilename));
			try {
				p.load(inputStream);
			} catch (IOException e) {

			}
		} catch (FileNotFoundException e) {

		}
		return p;
	}
}
