package com.sync.process;

import com.sync.common.GetProperties;

/**
 * config file read
 * 
 * @author sasou <admin@php-gene.com> web:http://www.php-gene.com/
 * @version 1.0.0
 */
public final class task {

	public static void main(String[] args) {
		// init
		new GetProperties();
		if (GetProperties.canal.destination == null) {
			System.out.println("error:canal destination is null!");
			return;
		}
		int num = GetProperties.canal.destination.length;
		if (num > 0) {
			for (int i = 0; i < num; i++) {
				if (!"".equals(GetProperties.canal.destination[i])) {
					String type = GetProperties.target.get(GetProperties.canal.destination[i]).type;
					switch (type) {
					case "kafka":
						new Thread(new Kafka(GetProperties.canal.destination[i])).start();
						break;
					case "redis":
						new Thread(new Redis(GetProperties.canal.destination[i])).start();
						break;
					case "elasticsearch":
						new Thread(new ElasticSearch(GetProperties.canal.destination[i])).start();
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
