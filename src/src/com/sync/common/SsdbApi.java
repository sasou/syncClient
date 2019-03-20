package com.sync.common;


import java.net.SocketException;

import com.udpwork.ssdb.*;


/**
 * SsdbApi
 * 
 * @author sasou <admin@php-gene.com> web:http://www.php-gene.com/
 * @version 1.0.0
 */

public class SsdbApi {
	private String canal_destination = null;
	private static SSDB ssdb = null;

	public SsdbApi(String name) throws Exception {
		canal_destination = name;
		if (ssdb == null) {
			ssdb = new SSDB(GetProperties.target.get(canal_destination).ip ,GetProperties.target.get(canal_destination).port, 1000 * 10);
		}
	}


	/**
	 * get data
	 * 
	 * @param key
	 * @return
	 * @throws Exception 
	 */
	public String get(String key) throws Exception {
		byte[] resp = null;
		try {
			resp = ssdb.get(key);
		} catch (SocketException e) {
			throw new Exception(" ssdb link fail", e);
		}
		return resp.toString();
	}
	
	
	/**
	 * push list in right
	 * 
	 * @param key
	 * @return 
	 * @return
	 */
	public void rpush(String key, String member) throws Exception {
		try {
			ssdb.request("qpush", key, member);
		} catch (SocketException e) {
			throw new Exception(" ssdb link fail", e);
		}
	}

}
