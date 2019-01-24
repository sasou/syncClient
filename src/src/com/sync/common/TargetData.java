package com.sync.common;

import java.util.HashMap;
import java.util.Map;

/**
 * TargetData
 * 
 * @author sasou <admin@php-gene.com> web:http://www.php-gene.com/
 * @version 1.0.0
 */
public final class TargetData {
	public String type = "";
	public String ip = "";
	public int port = 0;
	public int deep = 1;
	public String plugin = "";
	public String filter = "";
	public String sign = "";
	
	@SuppressWarnings("rawtypes")
	public Map filterMap = new HashMap();

	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * @param type
	 *            the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * @return the ip
	 */
	public String getIp() {
		return ip;
	}

	/**
	 * @param ip
	 *            the ip to set
	 */
	public void setIp(String ip) {
		this.ip = ip;
	}

	/**
	 * @return the port
	 */
	public int getPort() {
		return port;
	}

	/**
	 * @param port
	 *            the port to set
	 */
	public void setPort(int port) {
		this.port = port;
	}
	
	/**
	 * @return the deep
	 */
	public int getDeep() {
		return deep;
	}

	/**
	 * @param deep
	 *            the deep to set
	 */
	public void setDeep(int deep) {
		this.deep = deep;
	}
	
	/**
	 * @return the plugin
	 */
	public String getPlugin() {
		return plugin;
	}

	/**
	 * @param plugin
	 *            the plugin to set
	 */
	public void setPlugin(String plugin) {
		this.plugin = plugin;
	}
	
	/**
	 * @return the filter
	 */
	public String getFilter() {
		return filter;
	}

	/**
	 * @param filter
	 *            the filter to set
	 */
	public void setFilter(String filter) {
		this.filter = filter;
	}
	
	/**
	 * @return the sign
	 */
	public String getSign() {
		return sign;
	}

	/**
	 * @param sign
	 *            the sign to set
	 */
	public void setSign(String sign) {
		this.sign = sign;
	}
	
	/**
	 * @return the filterMap
	 */
	@SuppressWarnings("rawtypes")
	public Map getFilterMap() {
		return filterMap;
	}

	/**
	 * @param filterMap
	 *            the filterMap to set
	 */
	@SuppressWarnings("rawtypes")
	public void setFilterMap(Map filterMap) {
		this.filterMap = filterMap;
	}

}
