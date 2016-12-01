package com.efeiyi.website.dao;

import com.efeiyi.website.cache.redis.Redis;
import com.efeiyi.website.cache.redis.RedisFactory;
import com.efeiyi.website.entity.Entity;
import com.efeiyi.website.util.Util;

import static com.efeiyi.website.util.Util.firstLowerCase;
import static com.efeiyi.website.util.Util.isBaseType;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import redis.clients.jedis.exceptions.JedisDataException;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;


/**
 * Created by WangTao on 2016/7/21 0021.
 * rdDao abstract class
 * 提供同步和异步两种操作方法
 * 1.同步方法默认会抛出异常
 * 2.异步方法是不抛出异常的
 */

public abstract class BaseRdDao<T extends Entity> implements Dao<T> {

    private final BaseRdDao<T> baseRdDao = this;

    public static final Executor redisExecutor = Executors.newFixedThreadPool(100, (Runnable r) -> {
        Thread t = new Thread(r);
        t.setDaemon(true);
        return t;
    });


    public CompletableFuture<T> addAsync(T entity) {
        return CompletableFuture.supplyAsync(new Supplier<T>() {
            @Override
            public T get() {
                try {
                    add(entity);
                } catch (Exception e) {
                    Util.getLogger(getClass()).error("redis操作失败:" + e.getMessage(), e);
                    return null;
                }
                return entity;
            }
        }, redisExecutor);
    }

    public CompletableFuture<T> addAsync(T entity, int expire) {
        return CompletableFuture.supplyAsync(new Supplier<T>() {
            @Override
            public T get() {
                try {
                    add(entity, expire);
                } catch (Exception e) {
                    Util.getLogger(getClass()).error("redis操作失败:" + e.getMessage(), e);
                    return null;
                }
                return entity;
            }
        }, redisExecutor);
    }

    public CompletableFuture<List<T>> addAsync(String key, List<T> entities) {
        return CompletableFuture.supplyAsync(new Supplier<List<T>>() {
            @Override
            public List<T> get() {
                try {
                    add(key, entities);
                } catch (Exception e) {
                    Util.getLogger(getClass()).error("redis操作失败:" + e.getMessage(), e);
                    return null;
                }
                return entities;
            }
        }, redisExecutor);
    }

    public CompletableFuture<String> addAsync(String key, String json) {
        return CompletableFuture.supplyAsync(new Supplier<String>() {
            @Override
            public String get() {
                try {
                    add(key, json);
                } catch (Exception e) {
                    Util.getLogger(getClass()).error("redis操作失败:" + e.getMessage(), e);
                    return null;
                }
                return json;
            }
        }, redisExecutor);
    }

    public CompletableFuture<T> addAsync(String key, T entity) {
        return CompletableFuture.supplyAsync(new Supplier<T>() {
            @Override
            public T get() {
                try {
                    add(key, entity);
                } catch (Exception e) {
                    Util.getLogger(getClass()).error("redis操作失败:" + e.getMessage(), e);
                    return null;
                }
                return entity;
            }
        }, redisExecutor);
    }

    public CompletableFuture<T> addAsync(String key, T entity, int expire) {
        return CompletableFuture.supplyAsync(new Supplier<T>() {
            @Override
            public T get() {
                try {
                    add(key, entity, expire);
                } catch (Exception e) {
                    Util.getLogger(getClass()).error("redis操作失败:" + e.getMessage(), e);
                    return null;
                }
                return entity;
            }
        }, redisExecutor);
    }

    @Override
    public void add(T entity) throws Exception {
        Redis redis;
        try {
            redis = RedisFactory.getInstance().getRedis();
        } catch (Exception e) {
            Util.getLogger(getClass()).error("redis初始化失败:" + e.getMessage(), e);
            throw e;
        }
        try {
            add(entity, redis);
        } catch (Exception e) {
            Util.getLogger(getClass()).error("redis操作失败:" + e.getMessage(), e);
            redis.close();
            throw e;
        }
        redis.close();
    }

    public void add(T entity, int expire) throws Exception {
        Redis redis;
        try {
            redis = RedisFactory.getInstance().getRedis();
            redis.setDefaultExpireTime(expire);
        } catch (Exception e) {
            Util.getLogger(getClass()).error("redis初始化失败:" + e.getMessage(), e);
            throw e;
        }
        try {
            add(entity, redis);
        } catch (Exception e) {
            Util.getLogger(getClass()).error("redis操作失败:" + e.getMessage(), e);
            redis.close();
            throw e;
        }
        redis.close();
    }

    public void add(String key, List<T> entities) throws Exception {
        Redis redis;
        try {
            redis = RedisFactory.getInstance().getRedis();
        } catch (Exception e) {
            Util.getLogger(getClass()).error("redis初始化失败:" + e.getMessage(), e);
            throw e;
        }
        try {
            add(key, entities, redis);
            System.out.println("保存数据成功");
        } catch (Exception e) {
            Util.getLogger(getClass()).error("redis操作失败:" + e.getMessage(), e);
            redis.close();
            throw e;
        }
        redis.close();
    }

    public void add(String key, String json) throws Exception {
        Redis redis;
        try {
            redis = RedisFactory.getInstance().getRedis();
        } catch (Exception e) {
            Util.getLogger(getClass()).error("redis初始化失败:" + e.getMessage(), e);
            throw e;
        }
        try {
            redis.set(key, json);
            System.out.println("保存数据成功");
        } catch (Exception e) {
            Util.getLogger(getClass()).error("redis操作失败:" + e.getMessage(), e);
            redis.close();
            throw e;
        }
        redis.close();
    }

    public void add(String key, T entity) throws Exception {
        Redis redis = RedisFactory.getInstance().getRedis();
        Map<String, String> redisHash = entity.toHashMap();
        try {
            redis.setHash(key, redisHash);
        } catch (Exception e) {
            Util.getLogger(getClass()).error("redis操作失败:" + e.getMessage(), e);
            throw e;
        } finally {
            redis.close();
        }
    }

    public void add(String key, T entity, int expire) throws Exception {
        Redis redis = RedisFactory.getInstance().getRedis();
        redis.setDefaultExpireTime(expire);
        Map<String, String> redisHash = entity.toHashMap();
        try {
            redis.setHash(key, redisHash);
        } catch (Exception e) {
            Util.getLogger(getClass()).error("redis操作失败:" + e.getMessage(), e);
            throw e;
        } finally {
            redis.close();
        }
    }

    public void add(String key, JSONArray jsonArray) throws Exception {
        Redis redis = RedisFactory.getInstance().getRedis();
        try {
            redis.set(key, jsonArray.toString());
        } catch (Exception e) {
            Util.getLogger(getClass()).error("redis操作失败:" + e.getMessage(), e);
            redis.close();
            throw e;
        }
        redis.close();
    }

    public void add(T entity, Redis redis) throws Exception {
        addEntity(entity, redis);
    }

    public void add(String key, List<T> entityList, Redis redis) throws Exception {
        addEntityList(key, (List<Entity>) entityList, redis);
    }

    public void addEntity(Entity entity, Redis redis) throws Exception {
        addEntity(entity.identity(), entity, redis);
    }

    public void addEntity(String id, Entity entity, Redis redis) throws Exception {
        if (entity.identity() == null) {
            return;
        }
        HashMap<String, String> map = entity.toHashMap();
        //存储基本类型字段
        redis.setHash(id, map);
        //寻找复杂类型字段,然后保存；
        Field[] fields = entity.getClass().getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            if (!isBaseType(field.getType())) {
                Object value;
                value = field.get(entity);
                if (value instanceof List) {
                    String redisKey = id + "_" + field.getName();
                    List<Entity> entities = (List<Entity>) value;
                    addEntityList(redisKey, entities, redis);
                    continue;
                }

                if (value instanceof Map) {
                    String redisKey = id + "_" + field.getName();
                    Map<String, Object> mapTemp = (Map<String, Object>) value;
                    addObjectMap(redisKey, mapTemp, redis);
                    continue;
                }

                if (value instanceof Entity && ((Entity) value).identity() != null) {
                    addEntity((Entity) value, redis);
                }


            }
        }
    }


    public void addEntityList(String key, List<? extends Entity> entityList, Redis redis) throws Exception {
        ArrayList<String> redisList = new ArrayList<>();
        for (Entity entity : entityList) {
            if (entity.identity() == null) {
                continue;
            }
            redisList.add(entity.identity());
            addEntity(entity, redis);
        }
        redis.setList(key, redisList);
    }

    public void addObjectMap(String key, Map<String, Object> objectMap, Redis redis) throws Exception {
        Set<String> redisList = objectMap.keySet();
        for (String keyTemp : redisList) {
            Object mapValue = objectMap.get(keyTemp);
            if (isBaseType(mapValue.getClass())) {
                redis.set(key + "_" + keyTemp, mapValue.toString());
                continue;
            }

            if (mapValue instanceof Entity) {
                addEntity(key + "_" + keyTemp, (Entity) mapValue, redis);
                continue;
            }

            if (mapValue instanceof List) {
                addEntityList(key + "_" + keyTemp, (List) mapValue, redis);
            }

        }
        redis.setList(key, redisList);
    }

    @Override
    public void update(T entity) throws Exception {
        add(entity);
    }

    @SuppressWarnings("unchecked")
    public void get(String key, List<T> entities) throws Exception {
        Redis redis = RedisFactory.getInstance().getRedis();
        try {
            get(key, entities, redis);
        } catch (Exception e) {
            Util.getLogger(getClass()).error("redis操作失败:" + e.getMessage(), e);
            redis.close();
            throw e;
        }
        redis.close();
    }

    public void get(String id, Map<String, Object> objectMap) throws Exception {
        Redis redis = RedisFactory.getInstance().getRedis();
        try {
            get(id, objectMap, redis);
        } catch (Exception e) {
            Util.getLogger(getClass()).error("redis操作失败:" + e.getMessage(), e);
            redis.close();
            throw e;
        }
        redis.close();
    }

    public void get(String id, Map<String, Object> objectMap, Redis redis) throws Exception {
        List<String> attributesKeyList = redis.getList(id);
        for (String attributeKey : attributesKeyList) {
            Map<String, String> objectToMap;
            String objectToString;
            try {
                objectToMap = redis.getHash(id + "_" + attributeKey);
                objectToString = null;
            } catch (JedisDataException e) {
                objectToMap = null;
                objectToString = redis.get(id + "_" + attributeKey);
            }
            if (objectToMap == null) {
                objectMap.put(attributeKey, objectToString);
            } else {
                String clazz = objectToMap.get("ClassName");
                Object instance = Class.forName(clazz).newInstance();
                parseEntity(instance, objectToMap);
                objectMap.put(attributeKey, instance);
            }
        }
    }

    public void get(String key, List<T> entities, Redis redis) throws Exception {
        List<String> redisList = redis.getList(key);
        for (String redisKey : redisList) {
            Map<String, String> redisHash = redis.getHash(redisKey);
            String classType = redisHash.get("ClassName");
            T entity;
            try {
                entity = newEntity();
            } catch (Exception e) {
                Util.getLogger(this.getClass()).error("初始化对象失败:" + classType + "\n" + e.getMessage());
                throw e;
            }
            parseEntity(entity, redisHash);
            entities.add(entity);
        }
    }

    @Override
    public void get(String id, T entity) throws Exception {
        Redis redis = RedisFactory.getInstance().getRedis();
        try {
            get(id, entity, redis);
        } catch (Exception e) {
            Util.getLogger(getClass()).error("redis操作失败:" + e.getMessage(), e);
            redis.close();
            throw e;
        }
        redis.close();
    }

    public void get(String id, T entity, Redis redis) throws Exception {
        Map<String, String> redisResult = redis.getHash(id); //获取redis中指定的hash
        parseEntity(entity, redisResult); //反序列化
    }

    @Override
    public void get(String procedureName, Object[] params, List<T> entities) throws Exception {
        get(procedureName, params, entities, 0, Integer.MAX_VALUE);
    }

    @Override
    public void get(String procedureName, Object[] params, List<T> entities, int from, int to) throws Exception {
        Redis redis = RedisFactory.getInstance().getRedis();
        StringBuilder redisKey = buildRedisKey(procedureName, params, from, to);
        List<String> redisList;
        try {
            redisList = redis.getList(redisKey.toString());
        } catch (Exception e) {
            Util.getLogger(getClass()).error("redis操作失败:" + e.getMessage(), e);
            redis.close();
            throw e;
        }

        try {
            getList(redisList, entities, redis);
        } catch (Exception e) {
            Util.getLogger(getClass()).error("redis操作失败:" + e.getMessage(), e);
            redis.close();
            throw e;
        }
        redis.close();
    }

    @Override
    public void get(String procedureName, Object[] params, T entity) throws Exception {
        Redis redis = RedisFactory.getInstance().getRedis();
        StringBuilder redisKey = buildRedisKey(procedureName, params);
        Map<String, String> redisHash;
        try {
            redisHash = redis.getHash(redisKey.toString());
            parseEntity(entity, redisHash);
        } catch (Exception e) {
            redis.close();
            throw e;
        }
        redis.close();
    }


    public void getList(List<String> redisList, List<T> entities, Redis redis) throws Exception {
        List<Map<String, String>> entityMapList = redis.getListHash(redisList);
        for (Map<String, String> entityMap : entityMapList) {
            T entity = newEntity();
            parseEntity(entity, entityMap);
            entities.add(entity);
        }

    }

    @Override
    public long getList(String procedureName, Object[] params, JSONArray result) throws Exception {
        return 0;
    }

    @Override
    public long getList(String procedureName, Map<String, String[]> paramsMap, JSONArray result) throws Exception {
        Redis redis = RedisFactory.getInstance().getRedis();
        StringBuilder redisKey = buildRedisJsonKey(procedureName, paramsMap);
        try {
            result = JSONArray.fromObject(redis.get(redisKey.toString()));
        } catch (Exception e) {
            Util.getLogger(getClass()).error("redis操作失败:" + e.getMessage(), e);
            redis.close();
            throw e;
        }
        redis.close();
        return result.size();
    }

    @Override
    public long getList(String procedureName, Object[] params, int from, int to, JSONArray result) throws Exception {
        Redis redis = RedisFactory.getInstance().getRedis();
        StringBuilder redisKey = buildRedisJsonKey(procedureName, params, from, to);
        String resultStr;
        JSONArray resultTemp;
        try {

            resultStr = redis.get(redisKey.toString());
        } catch (Exception e) {
            Util.getLogger(getClass()).error("redis操作失败:" + e.getMessage(), e);
            redis.close();
            throw e;
        }
        redis.close();
        if (resultStr != null) {
            resultTemp = JSONArray.fromObject(resultStr);
            for (Object object : resultTemp) {
                result.add(object);
            }
        }
        return result.size();
    }

    //@TODO 未实现
    @Override
    public void getJSONObject(String procedureName, Object[] params, JSONObject result) throws Exception {

    }

    @Override
    public void delete(T entity) throws Exception {
        Redis redis = RedisFactory.getInstance().getRedis();
        redis.del(entity.identity());
    }

    public void delete(String id) throws Exception {
        Redis redis = RedisFactory.getInstance().getRedis();
        redis.del(id);
    }

    @Override
    public void save(T entity) throws Exception {
        String id = entity.identity();
        if (entity.identity() == null) {
            id = Util.getUUId();
            entity.setIdentify(id);
            add(entity);
        } else if (id.endsWith(" d")) {
            id = id.replace(" d", "");
            delete(entity);
        } else {
            update(entity);
        }
    }

    public static StringBuilder buildRedisKey(String procedureName, Object[] params, int from, int to) {
        StringBuilder redisKey = new StringBuilder();
        redisKey.append(procedureName);

        for (Object str : params) {
            redisKey.append(str != null ? str.toString() : "");
        }
        redisKey.append(from);
        redisKey.append("-");
        redisKey.append(to);
        return redisKey;
    }

    public static StringBuilder buildRedisJsonKey(String procedureName, Object[] params, int from, int to) {
        StringBuilder redisKey = buildRedisKey(procedureName, params, from, to);
        redisKey.append("json");
        return redisKey;
    }

    public static StringBuilder buildRedisKey(String procedureName, Object[] params) {
        StringBuilder redisKey = new StringBuilder();
        redisKey.append(procedureName);
        for (Object str : params) {
            redisKey.append(str != null ? str.toString() : "");
        }
        return redisKey;
    }

    public static StringBuilder buildRedisJsonKey(String procedureName, Object[] params) {
        StringBuilder redisKey = buildRedisKey(procedureName, params);
        redisKey.append("json");
        return redisKey;
    }

    public static StringBuilder buildRedisKey(String procedureName, Map<String, String[]> params) {
        StringBuilder redisKey = new StringBuilder();
        redisKey.append(procedureName);
        for (String key : params.keySet()) {
            redisKey.append(key);
            for (String str : params.get(key)) {
                redisKey.append(str != null ? str : "");
            }
        }
        return redisKey;
    }

    public static StringBuilder buildRedisJsonKey(String procedureName, Map<String, String[]> params) {
        StringBuilder redisKey = buildRedisKey(procedureName, params);
        redisKey.append("redis");
        return redisKey;
    }

    @SuppressWarnings("unchecked")
    private T newEntity() throws Exception {
        Class<T> clazz = (Class<T>) ((ParameterizedType) getClass()
                .getGenericSuperclass()).getActualTypeArguments()[0];
        return clazz.newInstance();
    }

    public void parseEntity(Object entity, Map<String, String> redisResult) throws Exception {
        for (Field field : entity.getClass().getDeclaredFields()) {
            String setMethodName = "set" + Util.firstUpperCase(field.getName());
            Method setMethod = entity.getClass().getMethod(setMethodName, field.getType());
            Object value;

            if (!isBaseType(field.getType())) {
                continue;
            } else if (redisResult.get(field.getName()) == null) {
                continue;
            } else if (field.getType().getName().equals(Date.class.getName())) {
                value = new Date(Long.parseLong(redisResult.get(field.getName())));
            } else if (field.getType().getName().equals(Integer.class.getName())) {
                value = Integer.parseInt(redisResult.get(field.getName()));
            } else if (field.getType().getName().equals(Long.class.getName())) {
                value = Long.parseLong(redisResult.get(field.getName()));
            } else if (field.getType().getName().equals(Boolean.class.getName())) {
                value = Boolean.parseBoolean(redisResult.get(field.getName()));
            } else {
                value = redisResult.get(field.getName());
            }
            setMethod.invoke(entity, value);
        }
    }


}
