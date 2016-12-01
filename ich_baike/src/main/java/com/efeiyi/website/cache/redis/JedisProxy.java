package com.efeiyi.website.cache.redis;

import com.efeiyi.website.entity.Entity;
import com.efeiyi.website.util.Util;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.Response;

import java.awt.*;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.sql.Timestamp;
import java.util.*;
import java.util.List;

import static com.efeiyi.website.util.Util.isBaseType;

/**
 * Created by Administrator on 2016/7/20 0020.
 */
public class JedisProxy implements Redis {

    private Jedis jedis;
    private int defaultExpireTime; //单位: 秒

    public JedisProxy(Jedis jedis, int defaultExpireTime) {
        this.jedis = jedis;
        this.defaultExpireTime = defaultExpireTime;
    }

    public int getDefaultExpireTime() {
        return defaultExpireTime;
    }

    public void setDefaultExpireTime(int defaultExpireTime) {
        this.defaultExpireTime = defaultExpireTime;
    }

    @Override
    public void setHash(String key, Map<String, String> value, int seconds) {
        for (String vk : value.keySet()) {
            jedis.hset(key, vk, value.get(vk));
        }
        jedis.expire(key, seconds);
    }

    @Override
    public void setHash(String key, Map<String, String> value) {
        setHash(key, value, defaultExpireTime);
    }


    @Override
    public Map<String, String> getHash(String key) {
        return jedis.hgetAll(key);
    }

    @Override
    public void set(String key, String value, int seconds) {
        jedis.set(key, value);
        jedis.expire(key, seconds);
    }

    @Override
    public void set(String key, String value) {
        set(key, value, defaultExpireTime);
    }

    @Override
    public String get(String key) {
        return jedis.get(key);
    }


    @Override
    public void del(String key) {
        jedis.del(key);
    }


    @Override
    public void close() {
        jedis.close();
    }

    @Override
    public String ping() {
        return jedis.ping();
    }


    @Override
    public List<String> getList(String key) {
        return jedis.lrange(key, 0, -1);
    }


    @Override
    public void setList(String key, Collection<String> stringList) {
        if (stringList.isEmpty()) {
            Util.getLogger(getClass()).info("redis不能存储空列表");
            return;
        }
        String[] stringArray = new String[stringList.size()];
        jedis.del(key);
        jedis.lpush(key, stringList.toArray(stringArray));
    }

    public List<Map<String, String>> getListHash(Collection<String> redisList) throws Exception {
        Pipeline pipeline = jedis.pipelined();
        ArrayList<Map<String, String>> hashList = new ArrayList<>();
        for (String key : redisList) {
            pipeline.hgetAll(key);
        }
        pipeline.multi();
        List<Object> result = pipeline.syncAndReturnAll();
        for (Object object : result) {
            if (!(object instanceof String)) {
                HashMap<String, String> hash = (HashMap<String, String>) object;
                if (hash.keySet().size() > 0) {
                    hashList.add(hash);
                }
            }
        }
        try {
            pipeline.close();
        } catch (Exception e) {
            throw e;
        }
        return hashList;
    }

    public void setEntity(String key, Entity entity) throws Exception {
        HashMap<String, String> map = new HashMap<String, String>();
        Field[] fields = entity.getClass().getDeclaredFields();
        for (Field field: fields) {
            field.setAccessible(true);
            Object obj = field.get(entity);
            if(obj == null) {
              continue;
            } else if(obj instanceof  Entity) {
                setEntity(((Entity) obj).identity(), (Entity) obj);
                map.put(field.getName(), ((Entity) obj).identity());

            } else if(obj instanceof List) {
                List<String> list = new ArrayList<String>();
                for (Object value: (List)obj) {
                    if(value instanceof Entity) {
                        list.add(((Entity) value).identity());
                        setEntity(((Entity) value).identity(), (Entity) value);
                    }
                }
                setList(key + "." + field.getName(), list);
                map.put(field.getName(), key + "." + field.getName());

            } else {
                String s = Util.baseTypeToString(obj);
                map.put(field.getName(), s);

            }
        }
        setHash(key, map);

    }

    public void loadEntity(String key, Entity entity) throws  Exception{
        Map<String, String> map = getHash(key);
        Field[] fields = entity.getClass().getDeclaredFields();

        for(Field field: fields) {
            String s = map.get(field.getName());

            Class<?> fieldClass = field.getType();
            if(s == null) {
                continue;
            }
            if(Entity.class.isAssignableFrom(fieldClass)) {
                Entity child  = (Entity) field.getType().newInstance();
                loadEntity(s, child);
                field.setAccessible(true);
                field.set(entity, child);
            } else if(List.class.isAssignableFrom(fieldClass)) {
                List<String> children = getList(s);
                field.setAccessible(true);
                field.set(entity, new ArrayList<>());
                ArrayList<Entity> entityList = (ArrayList<Entity>) field.get(entity);
                Type type = ((ParameterizedType)field.getGenericType()).getActualTypeArguments()[0];
                for (String string: children) {
                    Entity child = (Entity) ((Class<?>)type).newInstance();
                    loadEntity(string, child);
                    entityList.add(child);
                }
            } else {
                Object obj = Util.parseObject(s, fieldClass);
                field.setAccessible(true);
                field.set(entity, obj);
            }
        }
    }
}
