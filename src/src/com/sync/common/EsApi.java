package com.sync.common;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.entity.ContentType;
import org.apache.http.nio.entity.NStringEntity;
import org.apache.http.util.EntityUtils;
import org.elasticsearch.client.*;

import com.alibaba.fastjson.JSON;


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
	public boolean sync(String index, String content) throws Exception {
		Map<String,Object> data = jsonToMap(content);
		Map<String,Object> head = jsonToMap((String) data.get("head").toString());
	    String type = (String) head.get("type").toString();
	    String db = (String) head.get("db").toString();
	    String table = (String) head.get("table").toString();
	    String id = (String) head.get("id").toString();
	    String text = "";
	    switch(type) {
	    case "INSERT":
	    	text = (String) data.get("after").toString();
		    if (!"".equals(text)) {
		    	try {
					return insert("sync" + "-" + db + "-"+ table, "default", id, text);
				} catch (Exception e) {
					throw new Exception("elasticsearch insert fail", e);
				}
		    }
	    	break;
	    case "UPDATE":
	    	text = (String) data.get("after").toString();
		    if (!"".equals(id)) {
		    	try {
					return update("sync" + "-" + db + "-"+ table, "default", id, text);
				} catch (Exception e) {
					throw new Exception("elasticsearch update fail", e);
				}
		    }
	    	break;
	    case "DELETE":
		    if (!"".equals(id)) {
		    	try {
					return delete("sync" + "-" + db + "-"+ table, "default", id);
				} catch (Exception e) {
					
				}
		    }
	    	break;
	    }
		return true;
	}
	
    /**
     * json string to map
     * @param jsonObj
     * @return
     */
    public static Map<String,Object> jsonToMap(String jsonObj) {
    	@SuppressWarnings("unchecked")
		Map<String,Object> maps = (Map<String,Object>) JSON.parse((String) jsonObj);
        return maps;
    }

	/**
	 * @param index
	 * @param type
	 * @param id
	 * @param content
	 * @throws Exception 
	 */
	public boolean insert(String index, String type, String id, String content) throws Exception {
		if (!index("sync-sdsw-sys_log")) {
			System.out.println(setMappings("sync-sdsw-sys_log"));
		}
		Map<String, String> params = Collections.emptyMap();
		HttpEntity entity = new NStringEntity(content, ContentType.APPLICATION_JSON);
		Response response = rs.performRequest("PUT", "/" + index + "/" + type + "/" + id, params, entity); 
		String ret = (String) EntityUtils.toString(response.getEntity());
		return ret.contains("created") || ret.contains("updated");
	}

	/**
	 * @param index
	 * @param type
	 * @param id
	 * @param content
	 * @throws Exception 
	 */
	public boolean update(String index, String type, String id, String content) throws Exception {
		if (!index("sync-sdsw-sys_log")) {
			System.out.println(setMappings("sync-sdsw-sys_log"));
		}
		Map<String, String> params = Collections.emptyMap();
		HttpEntity entity = new NStringEntity(content, ContentType.APPLICATION_JSON);
		Response response = rs.performRequest("PUT", "/" + index + "/" + type + "/" + id, params, entity); 
		String ret = (String) EntityUtils.toString(response.getEntity());
		return ret.contains("created") || ret.contains("updated");
	}

	/**
	 * @param index
	 * @param type
	 * @param id
	 * @throws Exception 
	 */
	public boolean delete(String index, String type, String id) throws Exception {
		Map<String, String> params = Collections.emptyMap();
		Response response = rs.performRequest("DELETE", "/"+ index + "/" + type + "/" + id, params); 
		String ret = (String) EntityUtils.toString(response.getEntity());
		return ret.contains("not_found") || ret.contains("deleted");
	}

	/**
	 * @param index
	 * @param type
	 * @param id
	 * @throws IOException 
	 */
	public String get(String index, String type, String id) throws Exception {
		try {
		   Response response = rs.performRequest("GET", "/" + index + "/" + type + "/" + id, Collections.singletonMap("pretty", "true"));
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
	
	/**
	 * @param index
	 * @throws IOException 
	 */
	public String getMappings(String index) throws Exception {
		try {
			Response response = rs.performRequest("GET", "/" + index + "/_mappings", Collections.<String, String>emptyMap());
	        return EntityUtils.toString(response.getEntity());
		} catch (Exception e) {
			
		}
		return "";
	}

	/**
	 * @param index
	 * @throws IOException 
	 */
	public String setMappings(String index) throws Exception {
		try {
			HttpEntity entity = new NStringEntity("{\"mappings\":{\"default\" :{\"properties\":{\"@timestamp\":{\"type\" : \"date\"}}}}}", ContentType.APPLICATION_JSON);
			Response response = rs.performRequest("PUT", "/" + index, Collections.emptyMap(), entity);
	        return EntityUtils.toString(response.getEntity());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}
	
}
