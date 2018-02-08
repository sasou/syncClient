package com.alibaba.otter.canal.process;

import com.alibaba.otter.canal.common.GetProperties;

/**
 * config file read
 * 
 * @author sasou <admin@php-gene.com> web:http://www.php-gene.com/
 * @version 1.0.0
 */
public final class task {

	public static void main(String[] args) {
		// init
		GetProperties.getProperties();
		int num = GetProperties.canal_destination.length;
		if (num > 0) {
			for (int i = 0; i < num; i++) {
				if (!"".equals(GetProperties.canal_destination[i])) {
					switch (GetProperties.target_type) {
					case "kafka":
						new Thread(new Kafka(GetProperties.canal_destination[i])).start();
						break;
					case "redis":
						System.out.println("error:not support type!");
						break;
					default:
						System.out.println("error:not support type!");
						break;
					}

				}
			}
		}
	}

}
