package com.sync.common;


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

	public SsdbApi(String name) {
		canal_destination = name;
	}
	
	public SSDB instance() throws Exception {
		if (ssdb == null) {
			ssdb = new SSDB(GetProperties.target.get(canal_destination).ip ,GetProperties.target.get(canal_destination).port, 1000 * 10);
		}
		return ssdb;
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
			instance();
			if (ssdb != null) {
				resp = ssdb.get(key);
			}
		} catch (Exception e) {
			if (ssdb != null) {
				ssdb.close();
				ssdb = null;
			}
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
			instance();
			if (ssdb != null) {
				ssdb.request("qpush", key, member);
			}
		} catch (Exception e) {
			if (ssdb != null) {
				ssdb.close();
				ssdb = null;
			}
			throw new Exception(" ssdb link fail", e);
		}
	}

}
