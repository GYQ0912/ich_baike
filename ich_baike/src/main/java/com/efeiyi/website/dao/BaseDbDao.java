package com.efeiyi.website.dao;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.efeiyi.website.entity.Entity;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.efeiyi.website.util.Util;

import static com.efeiyi.website.util.Util.isBaseType;

public abstract class BaseDbDao<T extends Entity> implements Dao<T> {

    public boolean execute(String procedure, Object[] params, Connection conn)
            throws Exception {
        String sql = "CALL " + procedure + "( ";
        for (int i = 0; i < params.length; i++) {
            Object param = params[i];

            if (param.getClass().equals(Date.class)) {
                SimpleDateFormat sdf = new SimpleDateFormat(
                        "yyyy-MM-dd HH:mm:ss");
                param = sdf.format(((Date) param));
            } else if (param.getClass().equals(Boolean.class) && param.equals(true)) {
                param = 1;
            } else if (param.getClass().equals(Boolean.class) && param.equals(false)) {
                param = 0;
            }

            String text = param.toString();
            if (!param.getClass().equals(Integer.class)) {
                text = "'" + text + "'";
            }
            if (i == 0) {
                sql += text;
            } else {
                sql += "," + text;
            }
        }
        sql += ")";

        CallableStatement statement = conn.prepareCall(sql);
        return statement.execute();
    }

    public boolean execute(String procedure, Object[] params) throws Exception {
        Connection conn = ConnectionPool.get().getConnection();
        boolean result;
        try {
            result = execute(procedure, params, conn);
        } catch (Exception e) {
            ConnectionPool.get().free(conn);
            throw e;
        }
        ConnectionPool.get().free(conn);
        return result;
    }

    public void add(T entity) throws Exception {
        Connection conn = ConnectionPool.get().getConnection();
        try {
            add(entity, conn);
        } catch (Exception e) {
            ConnectionPool.get().free(conn);
            throw e;
        }
        ConnectionPool.get().free(conn);

    }

    public void add(T entity, Connection conn) throws Exception {
        String entityName = entity.getClass().getSimpleName();
        Field[] fields = entity.getClass().getDeclaredFields();
        List<String> fieldNameArray = new ArrayList<String>();
        List<Object> values = new ArrayList<Object>();
        String id = Util.getUUId();
        Method setIdMethod = entity.getClass().getMethod("setId", String.class);
        setIdMethod.invoke(entity, id);
        for (int i = 0; i < fields.length; i++) {
            Field field = fields[i];

            if (!isBaseType(field.getType())) {
                continue;
            }

            String fieldName = field.getName();
            String getMethodName = "get" + Util.firstUpperCase(fieldName);
            Method getMethod = entity.getClass().getMethod(getMethodName);
            Object obj = getMethod.invoke(entity);
            if (obj == null) {
                obj = null;
            }
            fieldNameArray.add(fieldName);
            values.add(obj);

        }
        String sql = buildSql(fieldNameArray, values, entityName, "insert");
        CallableStatement statement = conn.prepareCall(sql);
        statement.execute();
    }

    public void update(T entity) throws Exception {
        Connection conn = ConnectionPool.get().getConnection();
        try {
            update(entity, conn);
        } catch (Exception e) {
            ConnectionPool.get().free(conn);
            throw e;
        }
        ConnectionPool.get().free(conn);
    }

    public void update(T entity, Connection conn) throws Exception {
        String entityName = entity.getClass().getSimpleName();
        Field[] fields = entity.getClass().getDeclaredFields();
        List<String> fieldNameArray = new ArrayList<>();
        List<Object> valueArray = new ArrayList<>();
        for (Field field : fields) {
//        for (int i = 0; i < fields.length; i++) {
//            Field field = fields[i];

            if (!isBaseType(field.getType())) {
                continue;
            }


            String fieldName = field.getName();
            String getMethodName = "get" + Util.firstUpperCase(fieldName);
            Method getMethod = entity.getClass().getMethod(getMethodName);
            Object obj = getMethod.invoke(entity);
            if (obj == null) {
                obj = null;
            }
            fieldNameArray.add(fieldName);
            valueArray.add(obj);
        }
        String sql = buildSql(fieldNameArray, valueArray, entityName, "update");
        CallableStatement statement = conn.prepareCall(sql);
        statement.execute();
    }


    public long getList(String procedureName, Object[] params, JSONArray result) throws Exception {
        return getList(procedureName, params, 0, Integer.MAX_VALUE, result);
    }

    public long getList(String procedureName, Object[] params, Connection conn, JSONArray result) throws Exception {
        return getList(procedureName, params, conn, 0, Integer.MAX_VALUE, result);
    }

    public long getList(String procedureName, Map<String, String[]> paramsMap, JSONArray result) throws Exception {
        Connection conn = ConnectionPool.get().getConnection();
        long sum = 0;

        try {
            sum = getList(procedureName, paramsMap, conn, result);
        } catch (Exception e) {
            throw e;
        } finally {
            ConnectionPool.get().free(conn);
        }

        return sum;
    }

    public long getList(String procedureName, Map<String, String[]> paramMap, Connection conn, JSONArray result) throws Exception {
        ProcedureParams procedureParams = new ProcedureParams();
        String[] paramNameArray = procedureParams.get(procedureName);

        int from = 0;

        try {
            from = Integer.parseInt(paramMap.get("from")[0]);
        } catch (Exception e) {
            e.printStackTrace();
        }

        int to = Integer.MAX_VALUE;

        try {
            to = Integer.parseInt(paramMap.get("to")[0]);
        } catch (Exception e) {
            e.printStackTrace();
        }

        String[] paramValueArray = getParamArray(paramMap, paramNameArray);
        return getList(procedureName, paramValueArray, conn, from, to, result);
    }


    public String[] getParamArray(Map<String, String[]> paramMap, String[] paramNameArray) {
        String[] paramValueArray = new String[paramNameArray.length];
        for (int i = 0; i < paramNameArray.length; i++) {
            String paramName = paramNameArray[i];
            String paramValue = "";
//            try {
            paramValue = paramMap.get(paramName)[0];
//            } catch (Exception e) {
//            }
            paramValueArray[i] = paramValue;
        }
        return paramValueArray;
    }

    public long getList(String procedureName, Object[] params, int from, int to,
                        JSONArray result) throws Exception {
        Connection conn = ConnectionPool.get().getConnection();
        long sum;
        try {
            sum = getList(procedureName, params, conn, from, to, result);
        } catch (Exception e) {
            ConnectionPool.get().free(conn);
            throw e;
        }
        ConnectionPool.get().free(conn);
        return sum;
    }

    public long getList(String procedureName, Object[] params, Connection conn, int from, int to, JSONArray result)
            throws Exception {
        long sum = 0;
        String sql = "CALL " + procedureName + "(";
        for (int i = 0; i < params.length; i++) {
            params[i] = params[i].toString().replace("'", "''");
            if (i == 0) {
                sql += "'" + params[i] + "'";
            } else {
                sql += ",'" + params[i] + "'";
            }
        }
        sql += ")";

        CallableStatement statement = conn.prepareCall(sql);
        ResultSet rs = null;
        try {
            rs = statement.executeQuery();
        } catch (Exception e) {
            rs.close();
            throw e;
        }

        rs.last();
        sum = rs.getRow();

        rs.beforeFirst();
        ResultSetMetaData metaData = rs.getMetaData();
        int columnCount = metaData.getColumnCount();
        int rowIndex = 0;
        while (rs.next()) {

            if (rowIndex < from) {
                rowIndex++;
                continue;
            } else if (rowIndex >= to) {
                break;
            }

            JSONObject json = new JSONObject();
            for (int i = 1; i <= columnCount; i++) {
                String key = metaData.getColumnLabel(i);
                Object value = rs.getObject(i);
                if (value == null) {
                    value = "";
                }

                if (value instanceof java.sql.Date || value instanceof Date) {
                    value = ((Date) (value)).getTime();
                }


                json.element(key, value);
            }
            result.add(json);
            rowIndex++;
        }
        rs.close();
        return sum;
    }

    public void getJSONObject(String procedureName, Object[] params, JSONObject result, Connection conn)
            throws Exception {

        String sql = "CALL " + procedureName + "(";
        for (int i = 0; i < params.length; i++) {
            if (i == 0) {
                sql += "'" + params[i] + "'";
            } else {
                sql += ",'" + params[i] + "'";
            }
        }
        sql += ")";

        CallableStatement statement = conn.prepareCall(sql);
        ResultSet rs = null;
        try {
            rs = statement.executeQuery();
        } catch (Exception e) {
            rs.close();
            throw e;
        }

        ResultSetMetaData metaData = rs.getMetaData();
        int columnCount = metaData.getColumnCount();

        while (rs.next()) {
            for (int i = 1; i <= columnCount; i++) {
                String key = metaData.getColumnName(i);
                Object value = rs.getObject(i);
                if (value == null) {
                    continue;
                }

                if (value instanceof java.sql.Date || value instanceof Date) {
                    value = ((Date) (value)).getTime();
                }


                result.element(key, value);
            }
        }
        rs.close();
    }

    public void getJSONObject(String procedureName, Object[] params, JSONObject result)
            throws Exception {
        Connection conn = ConnectionPool.get().getConnection();
        try {
            getJSONObject(procedureName, params, result, conn);
        } catch (Exception e) {
            ConnectionPool.get().free(conn);
            throw e;
        }
        ConnectionPool.get().free(conn);
    }

    public void get(String procedureName, Object[] params, List<T> entities)
            throws Exception {
        Connection conn = ConnectionPool.get().getConnection();
        try {
            get(procedureName, params, entities, conn);
        } catch (Exception e) {
            ConnectionPool.get().free(conn);
            throw e;
        }
        ConnectionPool.get().free(conn);
    }

    public void get(String procedureName, Object[] params, List<T> entities,
                    int from, int to) throws Exception {
        Connection conn = ConnectionPool.get().getConnection();
        try {
            get(procedureName, params, entities, conn, from, to);
        } catch (Exception e) {
            ConnectionPool.get().free(conn);
            throw e;
        }
        ConnectionPool.get().free(conn);
    }

    public void get(String procedureName, Object[] params, List<T> entities,
                    Connection conn) throws Exception {
        String sql = "CALL " + procedureName + "(";
        for (int i = 0; i < params.length; i++) {
            if (i == 0) {
                sql += "'" + params[i] + "'";
            } else {
                sql += ",'" + params[i] + "'";
            }
        }
        sql += ")";
        CallableStatement statement = conn.prepareCall(sql);
        ResultSet rs = null;
        try {
            rs = statement.executeQuery();
        } catch (Exception e) {
            rs.close();
            throw e;
        }
        while (rs.next()) {
            @SuppressWarnings("unchecked")
            Class<T> clazz = (Class<T>) ((ParameterizedType) getClass()
                    .getGenericSuperclass()).getActualTypeArguments()[0];
            T entity = clazz.newInstance();
            fillEntityFromResultSet(entity, rs);
            entities.add(entity);
        }
        rs.close();
    }


    public void get(String procedureName, Object[] params, List<T> entities,
                    Connection conn, int from, int to) throws Exception {
        String sql = "CALL " + procedureName + "(";
        for (int i = 0; i < params.length; i++) {
            if (i == 0) {
                sql += "'" + params[i] + "'";
            } else {
                sql += ",'" + params[i] + "'";
            }
        }
        sql += ")";
        CallableStatement statement = conn.prepareCall(sql);
        ResultSet rs = null;
        try {
            rs = statement.executeQuery();
        } catch (Exception e) {
            rs.close();
            throw e;
        }
        int index = 0;
        while (rs.next() && index < to) {
            if (index < from) {
                continue;
            }
            @SuppressWarnings("unchecked")
            Class<T> clazz = (Class<T>) ((ParameterizedType) getClass()
                    .getGenericSuperclass()).getActualTypeArguments()[0];
            T entity = clazz.newInstance();
            fillEntityFromResultSet(entity, rs);
            entities.add(entity);
            index++;
        }
        rs.close();
    }

    public void get(String procedureName, Object[] params, T entity)
            throws Exception {
        Connection conn = ConnectionPool.get().getConnection();
        try {
            get(procedureName, params, entity, conn);
        } catch (Exception e) {
            throw e;
        } finally {
            ConnectionPool.get().free(conn);
        }
    }

    public void get(String procedureName, Object[] params, T entity,
                    Connection conn) throws Exception {
        String sql = "CALL " + procedureName + "(";
        for (int i = 0; i < params.length; i++) {
            String param = params[i].toString().replace("'", "''");
            if (i == 0) {
                sql += "'" + param + "'";
            } else {
                sql += ",'" + param + "'";
            }
        }
        sql += ")";

        CallableStatement statement = null;
        try {
            statement = conn.prepareCall(sql);
        } catch (Exception e) {
            //MYSQL 服务器休眠，重建连接池
            ConnectionPool.get().release();
            Util.getLogger(this.getClass()).debug("MYSQL 服务器休眠，重建连接池");
            throw e;
        }
        ResultSet rs = null;
        try {
            rs = statement.executeQuery();
        } catch (Exception e) {
            rs.close();
            throw e;
        }
        if (rs.next()) {
            fillEntityFromResultSet(entity, rs);
        }
        rs.close();
    }

    public void get(String id, T entity) throws Exception {
        Connection conn = ConnectionPool.get().getConnection();
        try {
            get(id, conn, entity);
        } catch (Exception e) {
            ConnectionPool.get().free(conn);
            throw e;
        }
        ConnectionPool.get().free(conn);
    }

    public void get(String id, Connection conn, T entity) throws Exception {
        @SuppressWarnings("unchecked")
        Class<T> clazz = (Class<T>) ((ParameterizedType) getClass()
                .getGenericSuperclass()).getActualTypeArguments()[0];// 获得一个类

        String entityName = clazz.getSimpleName();
        String tableName = Util.getTableNameFromEntityName(entityName);
        String sql = "{CALL get_" + tableName + "('" + id + "')" + "}";
        CallableStatement statement = conn.prepareCall(sql);
        ResultSet rs = null;
        try {
            rs = statement.executeQuery();
        } catch (Exception e) {
            rs.close();
            throw e;
        }
        if (rs.next()) {
            fillEntityFromResultSet(entity, rs);
        }
        rs.close();
    }

    public Object getValueFromRecordSet(String tableName, String columnName, ResultSet rs) throws  Exception{
        com.mysql.jdbc.ResultSetMetaData meta = (com.mysql.jdbc.ResultSetMetaData)rs.getMetaData();
        for(int i = 1; i <= meta.getColumnCount(); i++) {
            if(meta.getTableName(i).equals(tableName) && meta.getColumnLabel(i).equals(columnName)) {
                return  rs.getObject(i);
            }

        }
        return null;
    }

    public void fillEntityFromResultSet(T entity, ResultSet rs)
            throws Exception {

        com.mysql.jdbc.ResultSetMetaData meta = (com.mysql.jdbc.ResultSetMetaData)rs.getMetaData();
        String entityName = entity.getClass().getSimpleName();
        String tableName = Util.getTableNameFromEntityName(entityName);
        for (int i = 1; i <= meta.getColumnCount(); i++) {
            String columnName = meta.getColumnName(i);
            if(!meta.getTableName(i).equals(tableName)) {
                continue;
            }

            String fieldName = Util.getFieldNameFromColumnName(columnName);
            Field field = null;

            try {
                field = entity.getClass().getDeclaredField(fieldName);
            } catch (SecurityException e) {
                continue;
            } catch (NoSuchFieldException e) {
                continue;
            }

            String setMethodName = "set" + Util.firstUpperCase(fieldName);

            @SuppressWarnings("unchecked")
            Class<T> paramType = (Class<T>) field.getType();
            Method setMethod = entity.getClass().getMethod(setMethodName,
                    paramType);

            Object value = null;
            if (paramType.equals(boolean.class)) {
                value = rs.getBoolean(i);
            } else {
                value = rs.getObject(i);
            }

            if (value == null) {
                continue;
            }

            setMethod.invoke(entity, value);
        }
    }

    public void delete(T entity) throws Exception {
        Connection conn = ConnectionPool.get().getConnection();
        try {
            delete(entity, conn);
        } catch (Exception e) {
            ConnectionPool.get().free(conn);
            throw e;
        }
        ConnectionPool.get().free(conn);
    }

    public void delete(String id, Connection conn) throws Exception {
        @SuppressWarnings("unchecked")
        Class<?> clazz = (Class<T>) ((ParameterizedType) getClass()
                .getGenericSuperclass()).getActualTypeArguments()[0];
        String entityName = clazz.getSimpleName();
        String tableName = Util.getTableNameFromEntityName(entityName);
        String sql = "CALL delete_" + tableName + "('" + id + "')";
        CallableStatement statement = conn.prepareCall(sql);
        statement.execute();
    }

    public void delete(T entity, Connection conn) throws Exception {
        delete(entity.identity(), conn);
    }

    public void delete(String id) throws Exception{
        Connection conn = ConnectionPool.get().getConnection();
        try {
            delete(id, conn);
        } catch (Exception e) {
          throw e;
        } finally {
            ConnectionPool.get().free(conn);
        }
    }

    private String buildSql(List<String> fieldNameArray,
                            List<Object> valueArray, String entityName, String method) {

        String result = "{" + "CALL " + method + "_"
                + Util.getTableNameFromEntityName(entityName) + "(";
        for (int i = 0; i < fieldNameArray.size(); i++) {
            Object value = valueArray.get(i);
            String valueString = null;
            if (value != null) {
                if (value.getClass().equals(Date.class)) {
                    SimpleDateFormat sdf = new SimpleDateFormat(
                            "yyyy-MM-dd HH:mm:ss");
                    valueString = "'" + sdf.format(value) + "'";
                } else if (value.getClass().equals(Boolean.class)) {
                    if (value.equals(true)) {
                        valueString = "1";
                    } else {
                        valueString = "0";
                    }
                } else if (value instanceof String) {
                    valueString = "'" + ((String) value).replace("'", "''") + "'";
                } else {
                    valueString = "'" + value.toString() + "'";
                }

            }

            if (i == 0) {
                if (valueString != null) {
                    result += valueString;
                } else {
                    result += "null";
                }
            } else {
                if (valueString != null) {
                    result += "," + valueString;
                } else {
                    result += ",null";
                }
            }
        }

        result += ")";
        result += "}";
        return result;
    }

    @Override
    public void save(T entity) throws Exception {
        Connection conn = ConnectionPool.get().getConnection();
        save(entity, conn);
        ConnectionPool.get().free(conn);
    }

    public void save(T entity, Connection conn) throws Exception {
        String id = entity.identity();
        if (id == null) {
            add(entity, conn);
        } else if(id.endsWith(" d")) {
            id = id.replace(" d", "");
            delete(id, conn);
        } else {
            update(entity, conn);
        }
    }

    public void save(List<T> entities, Connection conn) throws  Exception{
        for (T entity:entities) {
            save(entity, conn);
        }
    }


    protected String[] splitEntityId(String id) {
        if (id == null) {
            return null;
        }
        return id.split(" ");
    }
}
