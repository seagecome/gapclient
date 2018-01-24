package com.ethan.gapclient.biz.util;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.BitSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.Transaction;
import redis.clients.jedis.Tuple;
import redis.clients.jedis.exceptions.JedisException;


public class RedisUtil {

    public static final Integer DEFAULT_DB = Integer.valueOf(ConfigUtil.getProperties().getProperty("redis.default.db"));

    private static final Integer EXPIRE_SECONDS = Integer.valueOf(ConfigUtil.getProperties().getProperty("redis.key.expired.time"));

    private static Logger logger = LoggerFactory.getLogger(RedisUtil.class);

    private static final Map<Integer, JedisPool> jedisPools = new HashMap<>();

    static {
        try {
        	Properties properties = ConfigUtil.getProperties();
        	
            JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
            jedisPoolConfig.setMaxIdle(Integer.valueOf(properties.getProperty("jedis.pool.maxIdle")));
            jedisPoolConfig.setMaxWaitMillis(Integer.valueOf(properties.getProperty("jedis.pool.maxWaitMillis")));

            String host = properties.getProperty("redis.host");
            int port = Integer.valueOf(properties.getProperty("redis.port"));
            int timeout = Integer.valueOf(properties.getProperty("redis.timeout"));

            JedisPool jedisPool = new JedisPool(jedisPoolConfig, host, port, timeout);
            jedisPools.put(DEFAULT_DB, jedisPool);

        } catch (Exception ex) {
            logger.error("Redis Util : initialize jedis pool fail , " + ex);
        }
    }

    public static void setObject(Integer db, String key, Object object) {
        if (key == null || key.isEmpty() || object == null)
            return;
        JedisPool jedisPool = jedisPools.get(db);
        Jedis jedis = null;
        Boolean success = true;
        try {
            jedis = jedisPool.getResource();
            byte[] objBytes = serialize(object);
            jedis.set(key.getBytes(), objBytes);
            jedis.expire(key, EXPIRE_SECONDS);
        } catch (Exception ex) {
            logger.error("Redis Util : set object fail , " + ex);
            if(ex instanceof JedisException)
                success = false;
        }finally {
            returnJedisResource(jedisPool,jedis,success);
        }
    }

    public static Object getObject(Integer db, String key) {
        if (key == null || key.isEmpty())
            return null;
        JedisPool jedisPool = jedisPools.get(db);
        Jedis jedis = null;
        Boolean success = true;
        Object result = null;
        try {
            jedis = jedisPool.getResource();
            byte[] objBytes = jedis.get(key.getBytes());
            result = unserialize(objBytes);
        } catch (Exception ex) {
            logger.error("Redis Util : get object fail , " + ex);
            if(ex instanceof JedisException)
                success = false;
        }finally {
            returnJedisResource(jedisPool,jedis,success);
        }

        return result;
    }

    public static void delete(Integer db, String key) {
        if (key == null || key.isEmpty())
            return;
        JedisPool jedisPool = jedisPools.get(db);
        Jedis jedis = null;
        Boolean success = true;
        try {
            jedis = jedisPool.getResource();
            jedis.del(key);
        } catch (Exception ex) {
            logger.error("Redis Util : delete fail , " + ex);
            if(ex instanceof JedisException)
                success = false;
        }finally {
            returnJedisResource(jedisPool,jedis,success);
        }
    }

    public static void setString(Integer db, String key, String value) {
        if (key == null || key.isEmpty() || value == null)
            return;
        JedisPool jedisPool = jedisPools.get(db);
        Jedis jedis = null;
        Boolean success = true;
        try {
            jedis = jedisPool.getResource();
            jedis.set(key, value);
            jedis.expire(key, EXPIRE_SECONDS);
        } catch (Exception ex) {
            logger.error("Redis Util : set string fail , " + ex);
            if(ex instanceof JedisException)
                success = false;
        }finally {
            returnJedisResource(jedisPool,jedis,success);
        }
    }

    public static void setBit(Integer db, String key, long offset, String value) {
        if (key == null || key.isEmpty() || value == null || offset < 0) {
        	return;
        }

        JedisPool jedisPool = jedisPools.get(db);
        Jedis jedis = null;
        Boolean success = true;
        try {
            jedis = jedisPool.getResource();
            jedis.setbit(key, offset, value);
            jedis.expire(key, EXPIRE_SECONDS);
        } catch (Exception ex) {
            logger.error("Redis Util : set bit fail , " + ex);
            if(ex instanceof JedisException)
                success = false;
        } finally {
            returnJedisResource(jedisPool,jedis,success);
        }
    }

    public static void setHash(Integer db, String key, Map<String, String> map) {
        if (key == null || key.isEmpty() || map == null || map.size() == 0)
            return;
        JedisPool jedisPool = jedisPools.get(db);
        Jedis jedis = null;
        Boolean success = true;
        try {
            jedis = jedisPool.getResource();
            for (String field : map.keySet()) {
                jedis.hset(key, field, map.get(field));
                jedis.expire(key, EXPIRE_SECONDS);
            }
        } catch (Exception ex) {
            logger.error("Redis Util : set hash fail , " + ex);
            if(ex instanceof JedisException)
                success = false;
        }finally {
            returnJedisResource(jedisPool,jedis,success);
        }
    }

    public static void setList(Integer db, String key, List<String> list) {
        if (key == null || key.isEmpty() || list == null || list.size() == 0)
            return;
        JedisPool jedisPool = jedisPools.get(db);
        Jedis jedis = null;
        Boolean success = true;
        try {
            jedis = jedisPool.getResource();
            for (String value : list) {
                jedis.rpush(key, value);
                jedis.expire(key, EXPIRE_SECONDS);
            }
        } catch (Exception ex) {
            logger.error("Redis Util : set list fail , " + ex);
            if(ex instanceof JedisException)
                success = false;
        }finally {
            returnJedisResource(jedisPool,jedis,success);
        }
    }
    
    public static void rpush(Integer db, String key, String value) {
        if (key == null || key.isEmpty() || value == null)
            return;
        JedisPool jedisPool = jedisPools.get(db);
        Jedis jedis = null;
        Boolean success = true;
        try {
            jedis = jedisPool.getResource();
            jedis.rpush(key, value);
            jedis.expire(key, EXPIRE_SECONDS);
        } catch (Exception ex) {
            logger.error("Redis Util : rpush fail , " + ex);
            if(ex instanceof JedisException)
                success = false;
        }finally {
            returnJedisResource(jedisPool,jedis,success);
        }
    }
    
    public static void delItem(Integer db, String key, String value) {
        if (key == null || key.isEmpty() || value == null)
            return;
        JedisPool jedisPool = jedisPools.get(db);
        Jedis jedis = null;
        Boolean success = true;
        try {
            jedis = jedisPool.getResource();
            jedis.lrem(key, 1, value);
        } catch (Exception ex) {
            logger.error("Redis Util : lrem fail , " + ex);
            if(ex instanceof JedisException)
                success = false;
        }finally {
            returnJedisResource(jedisPool,jedis,success);
        }
    }

    public static void setList(int db, String key, String[] values) {
        JedisPool jedisPool = jedisPools.get(db);
        Jedis jedis = null;
        Boolean success = true;
        try {
            jedis = jedisPool.getResource();
            Transaction tx = jedis.multi();
            tx.del(key);
            tx.rpush(key, values);
            tx.exec();
        } catch (Exception ex) {
            logger.error("Redis Util : set list fail , " + ex);
            if(ex instanceof JedisException)
                success = false;
        } finally {
            returnJedisResource(jedisPool,jedis,success);
        }
    }

    public static String getString(Integer db, String key) {
        if (key == null || key.isEmpty())
            return null;
        JedisPool jedisPool = jedisPools.get(db);
        Jedis jedis = null;
        String result = null;
        Boolean success = true;
        try {
            jedis = jedisPool.getResource();
            result = jedis.get(key);
        } catch (Exception ex) {
            logger.error("Redis Util : set string fail , " + ex);
            if(ex instanceof JedisException)
                success = false;
        }finally {
            returnJedisResource(jedisPool,jedis,success);
        }

        return result;
    }
    
    public static BitSet getBitSet(Integer db, String key) {
        if (key == null || key.isEmpty())
            return null;
        JedisPool jedisPool = jedisPools.get(db);
        Jedis jedis = null;
        BitSet result = null;
        Boolean success = true;
        try {
            jedis = jedisPool.getResource();
            byte[] binaryBulkReply = jedis.get(key.getBytes());
            if (null == binaryBulkReply) {
            	return null;
            }
            result = BitSet.valueOf(binaryBulkReply);
        } catch (Exception ex) {
            logger.error("Redis Util : get bit set fail , " + ex);
            if(ex instanceof JedisException)
                success = false;
        }finally {
            returnJedisResource(jedisPool,jedis,success);
        }

        return result;
    }

    public static List<String> getList(Integer db, String key) {
        if (key == null || key.isEmpty())
            return null;
        JedisPool jedisPool = jedisPools.get(db);
        Jedis jedis = null;
        List<String> result = null;
        Boolean success = true;
        try {
            jedis = jedisPool.getResource();
            result = jedis.lrange(key, 0, -1);
        } catch (Exception ex) {
            logger.error("Redis Util : set string fail , " + ex);
            if(ex instanceof JedisException)
                success = false;
        }finally {
            returnJedisResource(jedisPool,jedis,success);
        }

        return result;
    }

    public static Map<String, String> getHash(Integer db, String key) {
        if (key == null || key.isEmpty())
            return null;
        JedisPool jedisPool = jedisPools.get(db);
        Jedis jedis = null;
        Boolean success = true;
        Map<String, String> result = null;
        try {
            jedis = jedisPool.getResource();
            result = jedis.hgetAll(key);
        } catch (Exception ex) {
            logger.error("Redis Util : set string fail , " + ex);
            if(ex instanceof JedisException)
                success = false;
        }finally {
            returnJedisResource(jedisPool,jedis,success);
        }

        return result;
    }

    public static boolean connectRedis(Integer db) {
        JedisPool jedisPool = jedisPools.get(db);
        Jedis jedis = null;
        Boolean success = true;
        try {
            jedis = jedisPool.getResource();
            jedis.get("");
            return true;
        } catch (Exception ex) {
            logger.error("Redis Util : connection fail , " + ex);
            if(ex instanceof JedisException)
                success = false;
            throw ex;
        }finally {
            returnJedisResource(jedisPool,jedis,success);
        }

    }

    public static Set<String> zrevrange(int db, String key, int start, int stop) {
        JedisPool jedisPool = jedisPools.get(db);
        Jedis jedis = null;
        Boolean success = true;
        Set<String> result = null;
        try {
            jedis = jedisPool.getResource();
            result = jedis.zrevrange(key, start, stop);
        } catch (Exception ex) {
            logger.error("Redis Util : set string fail , " + ex);
            if(ex instanceof JedisException)
                success = false;
        }finally {
            returnJedisResource(jedisPool,jedis,success);
        }

        return result;
    }

    public static void zadd(int db, String key, long score, String member) {
        JedisPool jedisPool = jedisPools.get(db);
        Jedis jedis = null;
        Boolean success = true;
        try {
            jedis = jedisPool.getResource();
            jedis.zadd(key, score, member);
        } catch (Exception ex) {
            logger.error("Redis Util : set string fail , " + ex);
            if(ex instanceof JedisException)
                success = false;
        }finally {
            returnJedisResource(jedisPool,jedis,success);
        }
    }

    public static void delete(int db, String... key) {
        JedisPool jedisPool = jedisPools.get(db);
        Jedis jedis = null;
        Boolean success = true;
        try {
            jedis = jedisPool.getResource();
            jedis.del(key);
        } catch (Exception ex) {
            logger.error("Redis Util : set string fail , " + ex);
            if(ex instanceof JedisException)
                success = false;
        }finally {
            returnJedisResource(jedisPool,jedis,success);
        }
    }

    public static void setValueToMap(int db, String key, String field, String value) {
        JedisPool jedisPool = jedisPools.get(db);
        Jedis jedis = null;
        Boolean success = true;
        try {
            jedis = jedisPool.getResource();
            jedis.hset(key, field, value);
        } catch (Exception ex) {
            logger.error("Redis Util : set string fail , " + ex);
            if(ex instanceof JedisException)
                success = false;
        }finally {
            returnJedisResource(jedisPool,jedis,success);
        }

    }

    public static String hget(int db, String key, String field) {
        JedisPool jedisPool = jedisPools.get(db);
        Jedis jedis = null;
        Boolean success = true;
        String result = null;
        try {
            jedis = jedisPool.getResource();
            result = jedis.hget(key, field);
        } catch (Exception ex) {
            logger.error("Redis Util : set string fail , " + ex);
            if(ex instanceof JedisException)
                success = false;
        }finally {
            returnJedisResource(jedisPool,jedis,success);
        }
        return result;
    }
    
    public static void hset(int db, String key, String field, String value) {
        JedisPool jedisPool = jedisPools.get(db);
        Jedis jedis = null;
        Boolean success = true;
        try {
            jedis = jedisPool.getResource();
            jedis.hset(key, field, value);
        } catch (Exception ex) {
            logger.error("Redis Util : hset fail , " + ex);
            if (ex instanceof JedisException) {
            	success = false;
            }
        } finally {
            returnJedisResource(jedisPool, jedis, success);
        }
    }

    public static Long hlen(int db, String key) {
    	JedisPool jedisPool = jedisPools.get(db);
        Jedis jedis = null;
        Boolean success = true;
        Long result = null;
        try {
        	jedis = jedisPool.getResource();
        	result = jedis.hlen(key);
        } catch (Exception ex) {
        	logger.error("Redis Util : hlen fail , " + ex);
            if (ex instanceof JedisException) {
            	success = false;
            }
        } finally {
            returnJedisResource(jedisPool, jedis, success);
        }
        return result;
    }

    public static long incr(int db, String key) {
        JedisPool jedisPool = jedisPools.get(db);
        Jedis jedis = null;
        Boolean success = true;
        Long result = null;
        try {
            jedis = jedisPool.getResource();
            result = jedis.incr(key);
        } catch (Exception ex) {
            logger.error("Redis Util : set string fail , " + ex);
            if(ex instanceof JedisException)
                success = false;
        } finally {
            returnJedisResource(jedisPool, jedis, success);
        }
        return null == result ? -1 : result.longValue();
    }
    
    public static long incrBy(int db, String key, long incrNum) {
        JedisPool jedisPool = jedisPools.get(db);
        Jedis jedis = null;
        Boolean success = true;
        Long result = null;
        try {
            jedis = jedisPool.getResource();
            result = jedis.incrBy(key, incrNum);
        } catch (Exception ex) {
            logger.error("Redis Util : incrBy fail , " + ex);
            if(ex instanceof JedisException)
                success = false;
        } finally {
            returnJedisResource(jedisPool, jedis, success);
        }
        return null == result ? -1 : result.longValue();
    }

    public static void zincrBy(int db, String key, int score, String member) {
        JedisPool jedisPool = jedisPools.get(db);
        Jedis jedis = null;
        Boolean success = true;
        try {
            jedis = jedisPool.getResource();
            Transaction tx = jedis.multi();
            tx.zincrby(key, score, member);
            tx.zremrangeByScore(key, Integer.MIN_VALUE, 0);
            tx.exec();
        } catch (Exception ex) {
            logger.error("Redis Util : set string fail , " + ex);
            if(ex instanceof JedisException)
                success = false;
        }finally {
            returnJedisResource(jedisPool,jedis,success);
        }
    }

    public static void expire(int db, String key, int expire) {
        JedisPool jedisPool = jedisPools.get(db);
        Jedis jedis = null;
        Boolean success = true;
        try {
            jedis = jedisPool.getResource();
            jedis.expire(key, expire);
        } catch (Exception ex) {
            logger.error("Redis Util : set string fail , " + ex);
            if(ex instanceof JedisException)
                success = false;
        } finally {
            returnJedisResource(jedisPool, jedis, success);
        }
    }

    public static void setValue(int db, String key, String value, long expiry) {
        JedisPool jedisPool = jedisPools.get(db);
        Jedis jedis = null;
        Boolean success = true;
        try {
            jedis = jedisPool.getResource();
            jedis.psetex(key, (int) expiry, value);
        } catch (Exception ex) {
            logger.error("Redis Util : set string fail , " + ex);
            if(ex instanceof JedisException)
                success = false;
        } finally {
            returnJedisResource(jedisPool, jedis, success);
        }
    }

    public static Set<Tuple> zrevrangeByScoreWithScores(int db, String key, double min, double max) {
        JedisPool jedisPool = jedisPools.get(db);
        Jedis jedis = null;
        Boolean success = true;
        try {
            jedis = jedisPool.getResource();
            return jedis.zrevrangeByScoreWithScores(key, min, max);
        } catch (Exception ex) {
            logger.error("Redis Util : set string fail , " + ex);
            if(ex instanceof JedisException)
                success = false;
            return null;
        } finally {
            returnJedisResource(jedisPool, jedis, success);
        }
    }

    private static void returnJedisResource(JedisPool jedisPool, Jedis jedis, Boolean success) {
        if(jedisPool != null && jedis != null){
            String message = "error : return %s ,";
            try {
                if(success){
                    jedisPool.returnResource(jedis);
                    message = String.format(message,"resource");
                }
                else {
                    jedisPool.returnBrokenResource(jedis);
                    message = String.format(message,"broken resource");
                }
            }catch (Exception ex){
                logger.error(message + ex);
            }
        }
    }

    private static byte[] serialize(Object object) {
        ObjectOutputStream outputStream = null;
        ByteArrayOutputStream baos = null;
        try {
            baos = new ByteArrayOutputStream();
            outputStream = new ObjectOutputStream(baos);
            outputStream.writeObject(object);
            byte[] bytes = baos.toByteArray();
            return bytes;
        } catch (Exception e) {
            logger.error("Redis Util : serialize fail , " + e);
        }
        return null;
    }

    private static Object unserialize(byte[] bytes) {
        ByteArrayInputStream bais = null;
        try {
            bais = new ByteArrayInputStream(bytes);
            ObjectInputStream ois = new ObjectInputStream(bais);
            return ois.readObject();
        } catch (Exception e) {
            logger.error("Redis Util : unserialize fail , " + e);
        }
        return null;
    }

}
