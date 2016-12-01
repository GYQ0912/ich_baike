package com.efeiyi.website.dao;

import com.efeiyi.website.cache.redis.Redis;
import com.efeiyi.website.cache.redis.RedisFactory;
import com.efeiyi.website.entity.Entity;
import com.efeiyi.website.util.Util;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.sf.json.JSONObject;

import javax.servlet.http.HttpSession;
import java.lang.reflect.Field;
import java.util.Map;

/**
 * Created by Administrator on 2016/7/21 0021.
 */
public class SessionRdDao extends BaseRdDao {

    public void save(HttpSession entity) throws Exception {
        JSONObject httpSessionToJson = parseJson(entity);
        Redis redis = RedisFactory.getInstance().getRedis();
        try {
            redis.set(httpSessionToJson.getJSONObject("session").getString("id"), httpSessionToJson.toString());
        } catch (Exception e) {
            redis.close();
            throw e;
        }
    }

    public JSONObject get(String id) throws Exception {
        Redis redis = RedisFactory.getInstance().getRedis();
        String jsonObj = "";
        try {
            jsonObj = redis.get(id);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return JSONObject.fromObject(jsonObj);
    }

    private JSONObject parseJson(Object entity) throws Exception {
        JSONObject json = new JSONObject();

        Field[] fields = entity.getClass().getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            String fieldName = field.getName();
            Object value = field.get(entity);

            if (value != null && fieldName.equals("session")) {
                json.put(fieldName, parseJson(value));
                continue;
            } else if (value == null) {
                continue;
            }

            if (fieldName.equals("attributes")) {
                Map<String, Object> attributes = (Map<String, Object>) value;
                JSONObject attrJson = new JSONObject();
                for (String key : attributes.keySet()) {
                    Object attrValue = attributes.get(key);
                    if (attrValue instanceof Entity) {
                        JSONObject jsonObject = ((Entity) attrValue).toJson();
                        attrJson.put(key, jsonObject);
                    } else {
                        JSONObject jsonObject = JSONObject.fromObject(attrValue);
                        attrJson.put(key, jsonObject);
                    }
                }
                json.put(fieldName, attrJson);
                continue;
            }

            json.put(fieldName, value.toString());
        }
        return json;
    }

    private Object parseHttpSession(String jsonObjString, Class clazz) {
        Object httpSession = null;
        try {
            JSONObject jobObj = JSONObject.fromObject(jsonObjString);
            ObjectMapper mapper = new ObjectMapper();
            httpSession = mapper.readValue(jobObj.toString(), clazz);
        } catch (Exception e) {
            Util.getLogger(this.getClass()).debug(e.getMessage());
        }
        return httpSession;
    }


}
