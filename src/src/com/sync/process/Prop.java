package com.sync.process;

import com.sync.common.GetProperties;

public class Prop implements Runnable {
	
	public void run() {
		while (true) {
			GetProperties.update();
		    try {
				Thread.sleep(1000 * 10);
			} catch (InterruptedException e) {

			}
 		}
	}
	
}
