package com.sync.common;

/**
 * EsApi
 * 
 * @author sasou <admin@php-gene.com> web:http://www.php-gene.com/
 * @version 1.0.0
 */
public final class EsApi {

	@SuppressWarnings("unused")
	private String canal_destination = null;

	public EsApi(String name) {
		canal_destination = name;

	}

	/**
	 * @param index
	 * @param content
	 */
	public void insert(String index, String content) {
		System.out.println(index);
		System.out.println(content);
	}

	/**
	 * @param index
	 * @param content
	 */
	public void update(String index, String content) {
		System.out.println(index);
		System.out.println(content);
	}

	/**
	 * @param index
	 * @param content
	 */
	public void delete(String index, String content) {
		System.out.println(index);
		System.out.println(content);
	}

	/**
	 * @param index
	 * @param content
	 */
	public void get(String index, String content) {

	}

}
