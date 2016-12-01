package com.efeiyi.website.cache.redis;

import com.efeiyi.website.entity.Entity;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/7/20 0020.
 */

public interface Redis {

    void setDefaultExpireTime(int defaultExpireTime);

    void setHash(String key, Map<String, String> value, int seconds);

    void setHash(String key, Map<String, String> value);

    Map<String, String> getHash(String key);

    void close();

    String ping();

    void del(String key);

    void set(String key, String value, int seconds);

    void set(String key, String value);

    String get(String key);

    List<String> getList(String key);

    void setList(String key, Collection<String> stringList);

    List<Map<String, String>> getListHash(Collection<String> redisList) throws Exception;


    void setEntity(String key, Entity entity) throws Exception;
    void loadEntity(String key, Entity entity) throws Exception;


}
