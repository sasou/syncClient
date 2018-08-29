package com.sync.common;

import java.util.Date; 
import com.danga.MemCached.MemCachedClient;
import com.danga.MemCached.SockIOPool;

/**
 * MemApi
 * 
 * @author sasou <admin@php-gene.com> web:http://www.php-gene.com/
 * @version 1.0.0
 */

public class MemApi {

    /**
     * MemCachedClient
     */
    protected static MemCachedClient memCachedClient = null;

    /**
     * pool
     */
    static SockIOPool pool ;
 
    
   /**
    * 保护型构造方法，不允许实例化�
    */
   public MemApi(String name)
   {
       if (memCachedClient == null) {
		   // 服务器列表和其权重，个人memcached地址和端口号
		   String[] servers = {GetProperties.target.get(name).ip + ":" + GetProperties.target.get(name).port};
		   Integer[] weights = {3}; 
		
		   // 获取socke连接池的实例对象
		   pool = SockIOPool.getInstance(); 
		   
		   // 设置服务器信�?
		   pool.setServers(servers);
		   pool.setWeights(weights);
		
		   // 设置初始连接数�?�最小和�?大连接数以及�?大处理时�?
		   pool.setInitConn(10);
		   pool.setMinConn(50);
		   pool.setMaxConn(500);
		   pool.setMaxIdle(1000 * 60 * 60 * 6); 
		
		   // 设置主线程的睡眠时间
		   pool.setMaintSleep(30); 
		
		   // 设置TCP的参数，连接超时�?
		   pool.setNagle(false);
		   pool.setSocketTO(3000);
		   pool.setSocketConnectTO(0); 
		
		   // 初始化连接池
		   pool.initialize();  
		   
			memCachedClient = new MemCachedClient();
			memCachedClient.setPrimitiveAsString(true);
       }
   }
    
    /**
     * 取指定的key是否存在
     * @param key
     * @return boolean
     */
    public static boolean exists(String key) {
        return memCachedClient.keyExists(key);
    } 

    /**
     * 添加�?个指定的值到缓存�?.
     * @param key
     * @param value
     * @return boolean
     * @throws Exception 
     */
    public static boolean set(String key, Object value) throws Exception {
		try {
			return memCachedClient.set(key, value);
		} catch (Exception e) {
			throw new Exception(" memcached link fail", e);
		}
    }
    /**
     * 添加�?个指定的值到缓存�?.
     * @param key
     * @param value
     * @param expiry
     * @return boolean
     * @throws Exception 
     */
    public boolean set(String key, Object value, Date expiry) throws Exception{
		try {
			return memCachedClient.set(key, value, expiry);
		} catch (Exception e) {
			throw new Exception(" memcached link fail", e);
		}
    }

    /**
     * 向缓存添加键值对。注意：仅当缓存中不存在键时，才会添加成功�??
     * @param key
     * @param value
     * @return boolean
     * @throws Exception 
     */
	public static boolean add(String key, Object value) throws Exception {
		try {
			if (get(key) != null) {
				return false;
			} else {
				return memCachedClient.add(key, value);
			}
		} catch (Exception e) {
			throw new Exception(" memcached link fail", e);
		}
	}

    /**
     * 替换�?个指定的值到缓存�?.
     * @param key
     * @param value
     * @return boolean
     * @throws Exception 
     */
    public static boolean replace(String key, Object value) throws Exception {
		try {
			return memCachedClient.replace(key, value);
		} catch (Exception e) {
			throw new Exception(" memcached link fail", e);
		}
    }

	/**
	 * 根据键来替换Memcached内存缓存中已有的对应的�?�并设置逾期时间（即多长时间后该键�?�对从Memcached内存缓存中删除，比如�? new Date(1000*10)，则表示十秒之后从Memcached内存缓存中删除）�?
	 * 注意：只有该键存在时，才会替换键相应的�?��??
     * @param key
     * @param value
     * @param expiry
     * @return boolean
	 * @throws Exception 
	 */
    public static boolean replace(String key, Object value, Date expiry) throws Exception {
		try {
			return memCachedClient.replace(key, value, expiry);
		} catch (Exception e) {
			throw new Exception(" memcached link fail", e);
		}
     }     

    
	/**
	 * 根据键获取Memcached内存缓存管理系统中相应的�?
	 * 
     * @param key
     * @return boolean
	 * @throws Exception 
	 */
	public static String get(String key) throws Exception {
		try {
			return memCachedClient.get(key).toString();
		} catch (Exception e) {
			throw new Exception(" memcached link fail", e);
		}
	}
	
	/**
	 * incr
	 * 
     * @param key
     * @return boolean
	 * @throws Exception 
	 */
	public static long incr(String key) throws Exception {
		try {
			return memCachedClient.addOrIncr(key, 1);
		} catch (Exception e) {
			throw new Exception(" memcached link fail", e);
		}
	}
    
    /**
     * 删除�?个指定的值到缓存�?.
     * @param key
     * @param value
     * @return boolean
     * @throws Exception 
     */
    public static boolean delete(String key) throws Exception{
		try {
			return memCachedClient.delete(key);
		} catch (Exception e) {
			throw new Exception(" memcached link fail", e);
		}
    }

    /**
     * close
     * @return void
     */
    public static void close(){
    	pool.shutDown();
    }
    
}