package com.sync.common;

public class HttpmqApi {
	
	private String canal_destination = null;
	private String url = null;

	public HttpmqApi(String name) {
		canal_destination = name;
		url = "http://" + GetProperties.target.get(canal_destination).ip + ":" + GetProperties.target.get(canal_destination).port + "?";
	}

	/**
	 * @param index
	 * @param content
	 * @throws Exception 
	 */
	public boolean put(String type, String content) throws Exception {
		String ret = null;
        ret = HttpClient.sendPost(url + "charset=utf-8&opt=put&name=" + type, content);
		if (ret.indexOf("HTTPMQ_PUT_OK") != -1) {
			return true;
		}
		return false;
	}
	
}
