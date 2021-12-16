package com.sync.common;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import net.rubyeye.xmemcached.MemcachedClient;
import net.rubyeye.xmemcached.MemcachedClientBuilder;
import net.rubyeye.xmemcached.XMemcachedClientBuilder;
import net.rubyeye.xmemcached.exception.MemcachedException;
import net.rubyeye.xmemcached.utils.AddrUtil;

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
    protected static MemcachedClient memCachedClient = null;

 
    
   /**
    *  MemApi
    */
   public MemApi(String name)
   {
       if (memCachedClient == null) {
    	   MemcachedClientBuilder  builder = new XMemcachedClientBuilder(AddrUtil.getAddresses(GetProperties.target.get(name).ip));
    	   try {
				memCachedClient = builder.build();
				builder.setConnectionPoolSize(5);
				memCachedClient.setEnableHeartBeat(false);
			} catch (IOException e) {
				
			}
       }
   }
    
    /**
     * @param key
     * @param value
     * @return boolean
     * @throws Exception 
     */
    public boolean set(String key, Object value) throws Exception {
    	boolean blag = false;
		try {
			blag =  memCachedClient.set(key, 0, value);
		} catch (MemcachedException e) {
			throw new Exception("MemcachedClient operation fail");
	    } catch (TimeoutException e) {
	    	throw new Exception("MemcachedClient operation timeout");
	    } catch (InterruptedException e) {
			// ignore
	    }
		return blag;
    }
    
    /**
     * @param key
     * @param value
     * @param expiry
     * @return boolean
     * @throws Exception 
     */
    public boolean set(String key, Object value, int expiry) throws Exception{
    	boolean blag = false;
		try {
			blag =  memCachedClient.set(key, expiry, value);
		} catch (MemcachedException e) {
			throw new Exception("MemcachedClient operation fail");
	    } catch (TimeoutException e) {
	    	throw new Exception("MemcachedClient operation timeout");
	    } catch (InterruptedException e) {
			// ignore
	    }
		return blag;
    }

    /**
     * @param key
     * @param value
     * @return boolean
     * @throws Exception 
     */
	public boolean add(String key, Object value) throws Exception {
		boolean blag = false;
		try {
			if (get(key) != null) {
				return blag;
			} else {
				blag = memCachedClient.add(key, 0, value);
			}
		} catch (MemcachedException e) {
			throw new Exception("MemcachedClient operation fail");
	    } catch (TimeoutException e) {
	    	throw new Exception("MemcachedClient operation timeout");
	    } catch (InterruptedException e) {
			// ignore
	    }
		return blag;
	}

    /**
     * @param key
     * @param value
     * @return boolean
     * @throws Exception 
     */
    public boolean replace(String key, Object value) throws Exception {
    	boolean blag = false;
		try {
			blag = memCachedClient.replace(key, 0, value);
		} catch (MemcachedException e) {
			throw new Exception("MemcachedClient operation fail");
	    } catch (TimeoutException e) {
	    	throw new Exception("MemcachedClient operation timeout");
	    } catch (InterruptedException e) {
			// ignore
	    }
		return blag;
    }

	/**
     * @param key
     * @param value
     * @param expiry
     * @return boolean
	 * @throws Exception 
	 */
    public boolean replace(String key, Object value, int expiry) throws Exception {
    	boolean blag = false;
		try {		
			blag = memCachedClient.replace(key, expiry, value);
		} catch (MemcachedException e) {
			throw new Exception("MemcachedClient operation fail");
	    } catch (TimeoutException e) {
	    	throw new Exception("MemcachedClient operation timeout");
	    } catch (InterruptedException e) {
			// ignore
	    }
		return blag;
     }     

    
	/**
	 * 
     * @param key
     * @return boolean
	 * @throws Exception 
	 */
	public String get(String key) throws Exception {
		String blag = "";
		try {
			blag = memCachedClient.get(key).toString();
		} catch (MemcachedException e) {
			throw new Exception("MemcachedClient operation fail");
	    } catch (TimeoutException e) {
	    	throw new Exception("MemcachedClient operation timeout");
	    } catch (InterruptedException e) {
			// ignore
	    }
		return blag;
	}
	
	/**
	 * incr
	 * 
     * @param key
     * @return boolean
	 * @throws Exception 
	 */
	public boolean incr(String key) throws Exception {
		boolean blag = false;
		try {
			long ret = memCachedClient.incr(key, 1, 1);
			if (ret > 0) {
				blag = true;
			}
		} catch (MemcachedException e) {
			throw new Exception("MemcachedClient operation fail");
	    } catch (TimeoutException e) {
	    	throw new Exception("MemcachedClient operation timeout");
	    } catch (InterruptedException e) {
			// ignore
	    }
		return blag;
	}
    
    /**
     * @param key
     * @param value
     * @return boolean
     * @throws Exception 
     */
    public boolean delete(String key) throws Exception{
    	boolean blag = false;
		try {
			blag = memCachedClient.delete(key);
		} catch (MemcachedException e) {
			throw new Exception("MemcachedClient operation fail");
	    } catch (TimeoutException e) {
	    	throw new Exception("MemcachedClient operation timeout");
	    } catch (InterruptedException e) {
			// ignore
	    }
		return blag;
    }
    
}