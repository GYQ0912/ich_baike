package com.efeiyi.website.dao;

import com.efeiyi.website.entity.Entity;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.sql.Connection;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/7/21 0021.
 */
public interface Dao<T extends Entity> {


    void update(T entity) throws Exception;

    long getList(String procedureName, Object[] params, JSONArray result) throws Exception;

    long getList(String procedureName, Map<String, String[]> paramsMap, JSONArray result) throws Exception;


    long getList(String procedureName, Object[] params, int from, int to,
                 JSONArray result) throws Exception;


    void getJSONObject(String procedureName, Object[] params, JSONObject result)
            throws Exception;

    void get(String procedureName, Object[] params, List<T> entities)
            throws Exception;

    void get(String procedureName, Object[] params, List<T> entities,
             int from, int to) throws Exception;


    void get(String procedureName, Object[] params, T entity)
            throws Exception;

    void get(String id, T entity) throws Exception;

    void delete(T entity) throws Exception;

    void add(T entity) throws Exception;

    void save(T entity) throws Exception;


}
