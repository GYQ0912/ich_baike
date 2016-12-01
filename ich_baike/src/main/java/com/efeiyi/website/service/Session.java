package com.efeiyi.website.service;

import com.efeiyi.website.cache.redis.Redis;
import com.efeiyi.website.cache.redis.RedisFactory;
import com.efeiyi.website.entity.Entity;
import com.efeiyi.website.util.Util;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
/**
 * Created by Administrator on 2016/9/7.
 */
public class Session  {

    private int maxInactiveInterval = 1800;
    private long createTime;
    private String sessionId;
    private long lastAccessedTime;
    private boolean isNew = true;

    public Session(String sessionId) {
        this.sessionId = sessionId;
        createTime = (new Date()).getTime();
        lastAccessedTime = createTime;
    }

    public Session(HttpServletRequest request) {
        this(request.getRequestedSessionId());
    }

    public long getCreationTime() {
        return createTime;
    }

    public String getId() {
        return sessionId;
    }

    public long getLastAccessedTime() {
        return lastAccessedTime;
    }

    public void setMaxInactiveInterval(int interval) {
        maxInactiveInterval = interval;
    }

    public int getMaxInactiveInterval() {
        return  maxInactiveInterval;
    }

    public Object getAttribute(String name) throws Exception{
        Redis redis = null;
        redis = RedisFactory.getInstance().getRedis();

        Map<String, String> map = redis.getHash(sessionId);
        String value = map.get(name);
        String type_name = map.get(name + "_type");

        Object obj = null;
        if(type_name == null) {
            return value;
        } else {
            Class<?> clazz = Class.forName(type_name);
            if (Entity.class.isAssignableFrom(clazz)) {
                Entity entity = (Entity) clazz.newInstance();
                redis.loadEntity(map.get(name), entity);
                return entity;
            }
        }
        isNew = false;
        return null;

    }

    public Enumeration<String> getAttributeNames() {
        return null;
    }
    public void updateState() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                Redis redis = null;
                try {
                    redis = RedisFactory.getInstance().getRedis();
                } catch (Exception e) {
                    Util.getLogger(getClass()).error(e.getMessage());
                }

                Map<String, String> map = redis.getHash(sessionId);
                redis.setHash(sessionId, map, maxInactiveInterval);
                redis.close();
            }
        });
        thread.run();
        lastAccessedTime = new Date().getTime();
        isNew = false;
    }

    public void setAttribute(String name, Object value) throws Exception{
        Redis redis = RedisFactory.getInstance().getRedis();
        Map<String,String> sessionMap = redis.getHash(sessionId);

        if(value instanceof Entity) {
            Entity entity = (Entity)value;
            redis.setEntity(entity.identity(), entity);
            sessionMap.put(name, entity.identity());
            sessionMap.put(name + "_type", value.getClass().getName());
            redis.setHash(sessionId, sessionMap, maxInactiveInterval);
        } else {
            sessionMap.put(name, value.getClass().getName() + "_" + value.toString());
        }
        redis.close();
    }

    public void removeAttribute(String name) throws Exception{
        Redis redis = RedisFactory.getInstance().getRedis();
        Map<String,String> sessionMap = redis.getHash(sessionId);
        sessionMap.remove(name);
        redis.setHash(sessionId, sessionMap, maxInactiveInterval);
        redis.close();
    }

    public void invalidate() {
        Redis redis = null;
        try {
            redis = RedisFactory.getInstance().getRedis();
        } catch (Exception e) {
            Util.getLogger(getClass()).error(e.getMessage());
        }
        redis.del(sessionId);
        redis.close();
    }

    public boolean isNew() {
        return isNew;
    }
}
