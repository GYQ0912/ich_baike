package com.efeiyi.website.entity;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.*;

import com.efeiyi.website.util.Util;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import static com.efeiyi.website.util.Util.isBaseType;


public abstract class Entity {
    public Entity() {

    }

    public HashMap<String, String> toHashMap() throws Exception {
        Field[] fields = getClass().getDeclaredFields();
        HashMap<String, String> map = new HashMap<>();
        for (Field field : fields) {
            if (!isBaseType(field.getType())) {
                continue;
            }
            String fieldName = field.getName();
            String getMethodName = "get" + Util.firstUpperCase(fieldName);
            Method fieldMethod = getClass().getMethod(getMethodName);
            Object object = fieldMethod.invoke(this);
            String s = Util.baseTypeToString(object);
            map.put(fieldName, s);
        }
        return map;
    }

    public JSONObject toJson() {
        JSONObject json = new JSONObject();

        Field[] fields = getClass().getDeclaredFields();
        for (int i = 0; i < fields.length; i++) {
            Field field = fields[i];
            String fieldName = field.getName();
            Object value = getFieldValue(fieldName);
            if (value == null) {
                continue;
            }
            if (value instanceof Entity) {
                JSONObject jsonObject = ((Entity) value).toJson();
                json.put(fieldName, jsonObject);
                continue;
            }

            if (value instanceof java.sql.Date || value instanceof Date) {
                value = ((Date) (value)).getTime();
            }

            if (value.getClass().getName().equals(JSONArray.class.getName())) {
                JSONArray entities = (JSONArray) value;
                if (entities.size() != 0) {
                    json.put(fieldName, entities);
                }
            } else if (value instanceof List) {
                List<Entity> entities = (List<Entity>) value;
                if (entities.size() != 0) {
                    JSONArray jsonObject = Entity.toJson(entities);
                    json.put(fieldName, jsonObject);
                }
            } else {
                json.put(fieldName, value);
            }
        }
        return json;
    }

    public void parseHashMap(Map<String, String> map) throws  Exception{
        String className = (String) map.get("ClassName");
        Iterator iter = map.keySet().iterator();
        while(iter.hasNext()) {
            String key = (String) iter.next();
            if(key.equals("ClassName")) {
                continue;
            }
            String val = (String) map.get(key);
            Field field = null;
            try {
                field = getClass().getDeclaredField(key);
            } catch (Exception e) {
                continue;
            }
            field.setAccessible(true);
            Type type = field.getGenericType();
            if(type.equals(int.class)) {
                field.setInt(this, Integer.parseInt(val));
            } else if(type.equals(long.class)) {
                field.setLong(this, Long.parseLong(val));
            } else if(type.equals(Short.class)) {
                field.setShort(this, Short.parseShort(val));
            } else if(type.equals(String.class)) {
                field.set(this, val);
            } else if(type.equals(Date.class)) {
                field.set(this, new Date(Long.parseLong(val)));
            } else if(type.equals(float.class)) {
                field.set(this, Float.parseFloat(val));
            } else if(type.equals(double.class)) {
                field.set(this, Double.parseDouble(val));
            }
        }
    }

    public static JSONArray toJson(List<Entity> entities) {
        JSONArray jsonArray = new JSONArray();
        for (int i = 0; i < entities.size(); i++) {
            Entity entity = entities.get(i);
            JSONObject jsonObject = entity.toJson();
            jsonArray.add(jsonObject);
        }
        return jsonArray;
    }


    public Object getFieldValue(String fieldName) {
        String getMethodName = "get" + Util.firstUpperCase(fieldName);
        try {
            Method getMethod = getClass().getMethod(getMethodName);
            return getMethod.invoke(this);
        } catch (Exception e) {
            return null;
        }
    }

    public void setIdentify(String id) throws Exception {
        String methodName = "setId";
        Method method = getClass().getMethod(methodName, String.class);
        method.invoke(this, id);
    }

    public String identity() {
        String getMethodName = "getId";
        try {
            Method getMethod = getClass().getMethod(getMethodName);
            return (String) getMethod.invoke(this);
        } catch (Exception e) {
            return null;
        }
    }

}
