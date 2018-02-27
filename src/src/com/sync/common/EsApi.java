package com.sync.common;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.entity.ContentType;
import org.apache.http.nio.entity.NStringEntity;
import org.apache.http.util.EntityUtils;
import org.elasticsearch.client.*;


/**
 * EsApi
 * 
 * @author sasou <admin@php-gene.com> web:http://www.php-gene.com/
 * @version 1.0.0
 */
public final class EsApi {

	private String canal_destination = null;
	private static RestClient rs = null;

	public EsApi(String name) throws Exception {
		canal_destination = name;
		try {
			rs = RestClient.builder(new HttpHost(GetProperties.target.get(canal_destination).ip, GetProperties.target.get(canal_destination).port, "http")).build();
		} catch (Exception e) {
			rs.close();
			throw new Exception("elasticsearch link fail", e);
		}
	}
	
	/**
	 * @param index
	 * @param content
	 * @throws Exception 
	 */
	public boolean sync(String index, String content) {
		System.out.println(index);
		System.out.println(content);
		return true;
	}

	/**
	 * @param index
	 * @param type
	 * @param id
	 * @param content
	 * @throws Exception 
	 */
	public boolean insert(String index, String type, String id, String content) throws Exception {
		System.out.println(index);
		System.out.println(content);
		Map<String, String> params = Collections.emptyMap();
		HttpEntity entity = new NStringEntity(content, ContentType.APPLICATION_JSON);
		Response response = rs.performRequest("POST", "/sync/" + index + "/" + type + "/" + id, params, entity); 
		System.out.println(EntityUtils.toString(response.getEntity()));
		return response.getStatusLine().getReasonPhrase().equals("true");
	}

	/**
	 * @param index
	 * @param type
	 * @param id
	 * @param content
	 * @throws Exception 
	 */
	public boolean update(String index, String type, String id, String content) throws Exception {
		System.out.println(index);
		System.out.println(content);
		Map<String, String> params = Collections.emptyMap();
		HttpEntity entity = new NStringEntity(content, ContentType.APPLICATION_JSON);
		Response response = rs.performRequest("PUT", "/sync/" + index + "/" + type + "/" + id, params, entity); 
		System.out.println(EntityUtils.toString(response.getEntity()));
		return response.getStatusLine().getReasonPhrase().equals("true");
	}

	/**
	 * @param index
	 * @param type
	 * @param id
	 * @throws Exception 
	 */
	public boolean delete(String index, String type, String id) throws Exception {
		System.out.println(index);
		System.out.println(id);
		Map<String, String> params = Collections.emptyMap();
		Response response = rs.performRequest("DELETE", "/sync/" + index + "/" + type + "/" + id, params); 
		System.out.println(EntityUtils.toString(response.getEntity()));
		return response.getStatusLine().getReasonPhrase().equals("true");
	}

	/**
	 * @param index
	 * @param type
	 * @param id
	 * @throws IOException 
	 */
	public String get(String index, String type, String id) throws Exception {
		try {
		   Response response = rs.performRequest("GET", "/sync/" + index + "/" + type + "/" + id, Collections.singletonMap("pretty", "true"));
	        return EntityUtils.toString(response.getEntity());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}
	
	/**
	 * @param index
	 * @throws IOException 
	 */
	public boolean index(String index) throws Exception {
		try {
			Response response = rs.performRequest("HEAD", index, Collections.<String, String>emptyMap());
	        return response.getStatusLine().getReasonPhrase().equals("OK");
		} catch (Exception e) {
			
		}
		return false;
	}

}
