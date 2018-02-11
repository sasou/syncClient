package com.sync.common;

import java.util.List;
import java.util.Set;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.exceptions.JedisConnectionException;

/**
 * config
 * 
 * @author sasou <admin@php-gene.com> web:http://www.php-gene.com/
 * @version 1.0.0
 */

public class RedisApi {
	private String canal_destination = null;
	private static JedisPool pool = null;

	public RedisApi(String name) {
		canal_destination = name;
		JedisPoolConfig config = new JedisPoolConfig();
		// 控制一个pool可分配多少个jedis实例，通过pool.getResource()来获取；
		// 如果赋值为-1，则表示不限制；如果pool已经分配了maxActive个jedis实例，则此时pool的状态为exhausted(耗尽)。
		config.setMaxTotal(1000);
		// 控制一个pool最多有多少个状态为idle(空闲的)的jedis实例。
		config.setMaxIdle(50);
		// 表示当borrow(引入)一个jedis实例时，最大的等待时间，如果超过等待时间，则直接抛出JedisConnectionException；
		config.setMaxWaitMillis(1000 * 10);

		// 在获取连接的时候检查有效性, 默认false
		config.setTestOnBorrow(false);
		// 在空闲时检查有效性, 默认false
		config.setTestWhileIdle(false);

		// 连接耗尽时是否阻塞, false报异常,ture阻塞直到超时, 默认true
		config.setBlockWhenExhausted(true);

		config.setMinEvictableIdleTimeMillis(300000);
		pool = new JedisPool(config, GetProperties.target.get(canal_destination).ip, GetProperties.target.get(canal_destination).port, 1000 * 10);
	}

	/**
	 * 返还到连接池
	 * 
	 * @param pool
	 * @param redis
	 */
	public void returnResource(JedisPool pool, Jedis redis) {
		if (redis != null) {
			redis.close();
		}
	}

	/**
	 * 获取数据
	 * 
	 * @param key
	 * @return
	 */
	public String get(String key) throws Exception {
		String value = null;
		Jedis jedis = null;
		try {
			jedis = pool.getResource();
			value = jedis.get(key);
		} catch (JedisConnectionException e) {
			returnResource(pool, jedis);
			throw new Exception(" redis link fail", e);
		}
		return value;
	}

	/**
	 * 获取Set数据
	 * 
	 * @param key
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public Set zrange(String key) throws Exception {
		Set value = null;
		Jedis jedis = null;
		try {
			jedis = pool.getResource();
			value = jedis.zrange(key, 0, -1);
		} catch (JedisConnectionException e) {
			returnResource(pool, jedis);
			throw new Exception(" redis link fail", e);
		}
		return value;
	}

	/**
	 * 获取数据
	 * 
	 * @param key
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public List lrange(String key) throws Exception {
		List value = null;
		Jedis jedis = null;
		try {
			jedis = pool.getResource();
			value = jedis.lrange(key, 0, -1);
		} catch (JedisConnectionException e) {
			returnResource(pool, jedis);
			throw new Exception(" redis link fail", e);
		}
		return value;
	}

	/**
	 * 写String数据
	 * 
	 * @param key
	 * @return
	 * @throws Exception
	 */
	public void set(String key, String value) throws Exception {
		Jedis jedis = null;
		try {
			jedis = pool.getResource();
			jedis.set(key, value);
		} catch (JedisConnectionException e) {
			returnResource(pool, jedis);
			throw new Exception(" redis link fail", e);
		}
	}

	/**
	 * 写set数据
	 * 
	 * @param key
	 * @return
	 */
	public void zadd(String key, String member) throws Exception {
		Jedis jedis = null;
		try {
			jedis = pool.getResource();
			long score = (jedis.exists(key)) ? (jedis.zcard(key)) : 0;
			jedis.zadd(key, score, member);
		} catch (JedisConnectionException e) {
			returnResource(pool, jedis);
			throw new Exception(" redis link fail", e);
		}
	}

	/**
	 * 写List数据 左添加
	 * 
	 * @param key
	 * @return
	 */
	public void lpush(String key, String member) throws Exception {
		Jedis jedis = null;
		try {
			jedis = pool.getResource();
			jedis.lpush(key, member);
		} catch (JedisConnectionException e) {
			returnResource(pool, jedis);
			throw new Exception(" redis link fail", e);
		}
	}

	/**
	 * 写List数据 右添加
	 * 
	 * @param key
	 * @return
	 */
	public void rpush(String key, String member) throws Exception {
		Jedis jedis = null;
		try {
			jedis = pool.getResource();
			jedis.rpush(key, member);
		} catch (JedisConnectionException e) {
			returnResource(pool, jedis);
			throw new Exception(" redis link fail", e);
		}
	}

	/**
	 * 写数据
	 * 
	 * @param key
	 * @return
	 */
	public boolean exists(String key) throws Exception {

		Jedis jedis = null;
		boolean blag = false;
		try {
			jedis = pool.getResource();
			blag = jedis.exists(key);
		} catch (JedisConnectionException e) {
			returnResource(pool, jedis);
			throw new Exception(" redis link fail", e);
		}
		return blag;
	}

	/**
	 * 写数据
	 * 
	 * @param key
	 * @return
	 */
	public void del(String key) throws Exception {
		Jedis jedis = null;
		try {
			jedis = pool.getResource();
			jedis.del(key);
		} catch (JedisConnectionException e) {
			returnResource(pool, jedis);
			throw new Exception(" redis link fail", e);
		}
	}

	/**
	 * 删除List元素
	 * 
	 * @param key
	 * @return
	 */
	public void lrem(String key, String member) throws Exception {
		Jedis jedis = null;
		try {
			jedis = pool.getResource();
			jedis.lrem(key, 1, member);
		} catch (JedisConnectionException e) {
			returnResource(pool, jedis);
			throw new Exception(" redis link fail", e);
		}
	}

	/**
	 * 删除set元素
	 * 
	 * @param key
	 * @return
	 */
	public void zrem(String key, String member) throws Exception {
		Jedis jedis = null;
		try {
			jedis = pool.getResource();
			jedis.zrem(key, member);
		} catch (JedisConnectionException e) {
			returnResource(pool, jedis);
			throw new Exception(" redis link fail", e);
		}
	}

	/**
	 * 设置过期时间
	 * 
	 * @param key
	 * @param num
	 *            过期时间 分钟
	 */
	public void expire(String key, int num) throws Exception {
		Jedis jedis = null;
		try {
			jedis = pool.getResource();
			jedis.expire(key, num * 60);
		} catch (JedisConnectionException e) {
			returnResource(pool, jedis);
			throw new Exception(" redis link fail", e);
		}
	}

	/**
	 * 执行+1操作
	 * 
	 * @param key
	 */
	public void incr(String key) throws Exception {
		Jedis jedis = null;
		try {
			jedis = pool.getResource();
			jedis.incr(key);
		} catch (JedisConnectionException e) {
			returnResource(pool, jedis);
			throw new Exception(" redis link fail", e);
		}
	}

	/**
	 * 清空
	 * 
	 * @param num
	 *            过期时间 分钟
	 */
	public void clear() throws Exception {
		Jedis jedis = null;
		try {
			jedis = pool.getResource();
			jedis.flushDB();
		} catch (JedisConnectionException e) {
			returnResource(pool, jedis);
			throw new Exception(" redis link fail", e);
		}
	}
}
