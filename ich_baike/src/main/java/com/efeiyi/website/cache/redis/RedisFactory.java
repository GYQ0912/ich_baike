package com.efeiyi.website.cache.redis;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import redis.clients.jedis.*;

import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.Exchanger;

/**
 * Created by Administrator on 2016/7/20 0020.
 */
public class RedisFactory {

    private static RedisFactory redisFactory;

    private JedisPool jedisPool;
    private Properties redisProps = new Properties();
    private JedisCluster jedisCluster;
    private boolean redisStatus = true;

    static {
        try {
            redisFactory = new RedisFactory();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private RedisFactory() throws Exception {
        //initJedisPool();
        initJedisCluster();
    }

    //@TODO 待深入测试
    private void initJedisCluster() throws Exception {
        redisProps.load(getClass().getResourceAsStream("/redis.properties"));
        Set<HostAndPort> jedisClusterNodes = new HashSet<>();
        int nodeNum = Integer.parseInt(redisProps.getProperty("redis.cluster.nodeNum"));
        for (int i = 0; i < nodeNum; i++) {
            String[] node = redisProps.getProperty("redis.cluster.node" + (i + 1)).split(":");
            jedisClusterNodes.add(new HostAndPort(node[0], Integer.parseInt(node[1])));
        }
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxTotal(Integer.parseInt(redisProps.getProperty("redis.maxTotal", "50")));
        config.setMaxIdle(Integer.parseInt(redisProps.getProperty("redis.maxIdle", "20")));
        config.setMaxWaitMillis(Long.parseLong(redisProps.getProperty("redis.maxWaitMillis", "1000000")));
        jedisCluster = new JedisCluster(jedisClusterNodes, Integer.parseInt(redisProps.getProperty("redis.timeout")), 2, config);
    }

    private void initJedisPool() throws Exception {
        redisProps.load(getClass().getResourceAsStream("/redis.properties"));
        if (jedisPool == null) {
            JedisPoolConfig config = new JedisPoolConfig();
            config.setMaxTotal(Integer.parseInt(redisProps.getProperty("redis.maxTotal", "50")));
            config.setMaxIdle(Integer.parseInt(redisProps.getProperty("redis.maxIdle", "20")));
            config.setMaxWaitMillis(Long.parseLong(redisProps.getProperty("redis.maxWaitMillis", "1000000")));
            config.setTestOnBorrow(true);
            jedisPool = new JedisPool(
                    config,
                    redisProps.getProperty("redis.hostName"/*, "localhost"*/),
                    Integer.parseInt(redisProps.getProperty("redis.port"/*, "7000"*/)),
                    Integer.parseInt(redisProps.getProperty("redis.timeout", "100000"))
                    /*redisProps.getProperty("redis.password")*/
            );
        }
    }

    public static RedisFactory getInstance() throws Exception {
        return redisFactory;
    }

    private JedisPool getJedisPool() throws Exception {
        return jedisPool;
    }


    public Redis getRedis() throws Exception {
        Jedis jedis = getJedisPool().getResource();
        return new JedisProxy(jedis, Integer.parseInt(redisProps.getProperty("redis.key.expire", "360000")));
//        return new JedisClusterProxy(jedisCluster, Integer.parseInt(redisProps.getProperty("redis.key.expire", "360000")));
    }


}
