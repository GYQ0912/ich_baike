package com.efeiyi.website.dao;

import com.efeiyi.website.entity.Entity;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.avalon.framework.parameters.ParameterException;

import java.sql.Connection;
import java.util.List;
import java.util.Map;

/**
 * Created by WangTao on 2016/7/25 0025.
 * dao abstract class
 */
@SuppressWarnings("unchecked")

public abstract class BaseDao<T extends Entity> implements Dao<T> {
    public static int USE_DATABASE = 0;
    public static int USE_REDIS = 1;
    public static int USE_BOTH_DATABASE_REDIS = 2;
    public enum DataSource {
        DATABASE_ONLY, REDIS_ONLY, DATABASE_AND_REDIS, DATABASE_PRIORITY, REDIS_PRIORITY
    }

    public BaseDao() {

    }

    protected abstract BaseRdDao getRdDao();

    protected abstract BaseDbDao getDbDao();

    public boolean execute(String procedure, Object[] params, Connection conn) throws Exception {
        return getDbDao().execute(procedure, params, conn);
    }

    public boolean execute(String procedure, Object[] params) throws Exception {
        return getDbDao().execute(procedure, params);
    }

    //tested
    public void update(T entity, DataSource dataSource) throws Exception {
        if (dataSource == DataSource.DATABASE_ONLY || dataSource == DataSource.DATABASE_AND_REDIS) {
            getDbDao().update(entity);
        } else if(dataSource == DataSource.REDIS_ONLY || dataSource == DataSource.DATABASE_AND_REDIS) {
            getDbDao().update(entity);
        } else {
            throw new ParameterException("dataSource值错误");
        }

    }

    public void update(T entity, Connection conn, DataSource dataSource) throws Exception {
        if (dataSource == DataSource.DATABASE_ONLY || dataSource == DataSource.DATABASE_AND_REDIS) {
            getDbDao().update(entity, conn);
        } else if(dataSource == DataSource.REDIS_ONLY || dataSource == DataSource.DATABASE_AND_REDIS) {
            getRdDao().update(entity);
        }else {
            throw new ParameterException("dataSource值错误");
        }
    }

    @Override
    public void update(T entity) throws Exception {
        getDbDao().update(entity);
    }

    public void update(T entity, Connection conn) throws Exception {
        boolean oriAutoCommit = conn.getAutoCommit();
        conn.setAutoCommit(false);
        try {
            getDbDao().update(entity, conn);
        } catch (Exception e) {
            conn.setAutoCommit(oriAutoCommit);
            throw e;
        }
        conn.setAutoCommit(oriAutoCommit);
    }

    public long getList(String procedureName, Object[] params, JSONArray result) throws Exception {
        return getList(procedureName, params, 0, Integer.MAX_VALUE, result);
    }

    public long getList(String procedureName, Object[] params,
                        DataSource dataSource, JSONArray result) throws Exception {
        Connection conn = ConnectionPool.get().getConnection();
        long sum;
        try {
            sum = getList(procedureName, params, conn, dataSource, result);
        } catch (Exception e) {
            throw  e;
        } finally {
            ConnectionPool.get().free(conn);
        }
        return  sum;
    }


    public long getList(String procedureName, Object[] params, Connection conn, JSONArray result) throws Exception {
        return getDbDao().getList(procedureName, params, conn, 0, Integer.MAX_VALUE, result);
    }

    //tested
    public long getList(String procedureName, Object[] params, Connection conn,
                        DataSource dataSource, JSONArray result) throws Exception {
        long sum;
        if(dataSource == DataSource.DATABASE_ONLY) {
            return getDbDao().getList(procedureName, params, conn, result);
        } else if (dataSource == DataSource.REDIS_ONLY) {
            return getRdDao().getList(procedureName,params, result);
        } else if(dataSource == DataSource.DATABASE_PRIORITY) {
            sum = getDbDao().getList(procedureName, params, conn, result);
            if(sum == 0) {
                sum = getRdDao().getList(procedureName, params, result);
            }
        } else if(dataSource == DataSource.REDIS_PRIORITY){
            sum = getRdDao().getList(procedureName, params, result);
            if(sum == 0) {
                sum = getDbDao().getList(procedureName, params,conn,  result);
            }
        } else {
            throw new ParameterException("dataSource 超出取值范围.");
        }
        return sum;
    }

    public long getList(String procedureName, Map<String, String[]> paramsMap, JSONArray result) throws Exception {
        return getDbDao().getList(procedureName, paramsMap, result);
    }

    public long getList(String procedureName, Map<String, String[]> paramsMap, DataSource dataSource,
                         JSONArray result) throws Exception {
        long size = 0;
        Connection conn = ConnectionPool.get().getConnection();
        try {
            size = getList(procedureName, paramsMap, conn, dataSource, result);
        } catch (Exception e) {
            throw e;
        } finally {
            ConnectionPool.get().free(conn);
        }
        return size;
    }

    public long getList(String procedureName, Map<String, String[]> paramMap, Connection conn, JSONArray result) throws Exception {
        return getDbDao().getList(procedureName, paramMap, conn, result);
    }


    public long getList(String procedureName, Map<String, String[]> paramsMap, Connection conn,
                        DataSource dataSource, JSONArray result) throws Exception {
        long size = 0;
        if(dataSource == DataSource.DATABASE_ONLY) {
            return getDbDao().getList(procedureName, paramsMap, result);
        } else if(dataSource == DataSource.REDIS_ONLY) {
            return getRdDao().getList(procedureName, paramsMap, result);
        } else if(dataSource == DataSource.DATABASE_PRIORITY) {
            size = getDbDao().getList(procedureName, paramsMap, result);
            if (size == 0) {
                size = getRdDao().getList(procedureName, paramsMap, result);
            }
        } else if(dataSource == DataSource.REDIS_PRIORITY) {
            size = getRdDao().getList(procedureName, paramsMap, result);
            if(size == 0) {
                size = getDbDao().getList(procedureName, paramsMap, result);
            }
        } else {
            throw new ParameterException("dataSource 超出取值范围.");
        }
        return size;
    }

    public long getList(String procedureName, Object[] params, int from, int to, JSONArray result) throws Exception {
        return getDbDao().getList(procedureName, params, from, to, result);
    }

    public long getList(String procedureName, Object[] params, int from, int to,
                        DataSource dataSource, JSONArray result) throws Exception {
        long total = 0;
        Connection conn = ConnectionPool.get().getConnection();
        try {
            total = getList(procedureName, params, conn,dataSource, from, to,  result);
        } catch (Exception e) {
            throw e;
        } finally {
            ConnectionPool.get().free(conn);
        }
        return total;

    }

    public long getList(String procedureName, Object[] params, Connection conn, int from, int to, JSONArray result) throws Exception {
        return getDbDao().getList(procedureName, params, conn, from, to, result);
    }

    public long getList(String procedureName, Object[] paramsMap, Connection conn,
                        DataSource dataSource, int from, int to, JSONArray result) throws Exception {
        long size = 0;
        if(dataSource == DataSource.DATABASE_ONLY) {
            return getDbDao().getList(procedureName, paramsMap,conn, from, to,  result);
        } else if(dataSource == DataSource.REDIS_ONLY) {
            return getRdDao().getList(procedureName, paramsMap, from, to, result);
        } else if(dataSource == DataSource.DATABASE_PRIORITY) {
            size = getDbDao().getList(procedureName, paramsMap, conn, from, to, result);
            if (size == 0) {
                size = getRdDao().getList(procedureName, paramsMap, from, to, result);
            }
        } else if(dataSource == DataSource.REDIS_PRIORITY) {
            size = getRdDao().getList(procedureName, paramsMap, from, to, result);
            if(size == 0) {
                size = getDbDao().getList(procedureName, paramsMap,conn, from, to, result);
            }
        } else {
            throw new ParameterException("dataSource 超出取值范围.");
        }
        return size;
    }

    public void getJSONObject(String procedureName, Object[] params, JSONObject result, Connection conn) throws Exception {
         getDbDao().getJSONObject(procedureName, params, result, conn);
    }

    public void getJSONObject(String procedureName, Object[] params, JSONObject result) throws Exception {
        getDbDao().getJSONObject(procedureName, params, result);
    }


    public void get(String procedureName, Object[] params, List<T> entities)
            throws Exception {
        getDbDao().get(procedureName, params, entities);
    }

    public void get(String procedureName, Object[] params, List<T> entities, DataSource dataSource)
            throws Exception {
        get(procedureName, params, entities, 0, Integer.MAX_VALUE, dataSource);
    }

    public void get(String procedureName, Object[] params, List<T> entities,
                    int from, int to) throws Exception {
        getDbDao().get(procedureName, params, entities, from, to);
    }

    public void get(String procedureName, Object[] params, List<T> entities,
                    int from, int to, DataSource dataSource) throws Exception {
        Connection conn = ConnectionPool.get().getConnection();
        try {
            get(procedureName, params, entities,conn, dataSource, from, to);
        } catch (Exception e) {
            throw  e;
        } finally {
            ConnectionPool.get().free(conn);
        }
    }

    public void get(String procedureName, Object[] params, List<T> entities,
                    Connection conn) throws Exception {
        getDbDao().get(procedureName, params, entities, conn, 0, Integer.MAX_VALUE);
    }


    public void get(String procedureName, Object[] params, List<T> entities,
                    Connection conn, DataSource dataSource) throws Exception {
        get(procedureName, params, entities, conn,dataSource, 0, Integer.MAX_VALUE);
    }


    public void get(String procedureName, Object[] params, List<T> entities,
                    Connection conn, int from, int to) throws Exception {
        getDbDao().get(procedureName, params, entities, conn, from, to);
    }


    public void get(String procedureName, Object[] params, List<T> entities,
                    Connection conn, DataSource dataSource, int from, int to) throws Exception {
        if (dataSource == DataSource.DATABASE_ONLY) {
            getDbDao().get(procedureName, params, entities, conn, from, to);
        } else if(dataSource == DataSource.REDIS_ONLY) {
            getRdDao().get(procedureName, params, entities, from, to);
        } else if(dataSource == DataSource.REDIS_PRIORITY) {
            try {getRdDao().get(procedureName, params, entities, from, to);} catch (Exception e){}
            if(entities.size() == 0) {
                getDbDao().get(procedureName, params, entities, conn, from, to);
            }
        } else if(dataSource == DataSource.REDIS_PRIORITY) {
            try {getDbDao().get(procedureName, params, entities, conn, from, to);} catch (Exception e){}
            if(entities.size() == 0) {
                getRdDao().get(procedureName, params, entities, from, to);
            }
        } else {
            throw new ParameterException("dataSource 超出范围.");
        }
    }

    public void get(String procedureName, Object[] params, T entity)
            throws Exception {
        getDbDao().get(procedureName, params, entity);
    }

    public void get(String procedureName, Object[] params, T entity, DataSource dataSource)
            throws Exception {
        Connection conn = ConnectionPool.get().getConnection();
        try {
            get(procedureName, params, entity, conn, dataSource);
        } catch (Exception e) {
            throw e;
        } finally {
            ConnectionPool.get().free(conn);
        }
    }

    public void get(String procedureName, Object[] params, T entity,
                    Connection conn) throws Exception {
        getDbDao().get(procedureName, params, entity, conn);
    }

    public void get(String procedureName, Object[] params, T entity,
                    Connection conn, DataSource dataSource) throws Exception {
       if(dataSource == DataSource.DATABASE_ONLY) {
           getDbDao().get(procedureName, params, entity, conn);
       } else if(dataSource == DataSource.REDIS_ONLY) {
           getRdDao().get(procedureName, params, entity);
       } else if(dataSource == DataSource.DATABASE_PRIORITY) {
           try {getDbDao().get(procedureName, params, entity, conn);}catch (Exception e){}
           if(entity.identity() == null) {
               getRdDao().get(procedureName, params, entity);
           }
       } else if(dataSource == DataSource.REDIS_PRIORITY) {
           try {getRdDao().get(procedureName, params, entity);}catch (Exception e){}
           if(entity.identity() == null) {
               getDbDao().get(procedureName, params, entity, conn);
           }
       } else {
           throw new ParameterException("dataSource超出范围.");
       }
    }

    public void get(String id, T entity) throws Exception {
        getDbDao().get(id, entity);
    }

    public void get(String id, T entity, DataSource dataSource) throws Exception {
        Connection conn = ConnectionPool.get().getConnection();
        try {
            get(id, dataSource, conn, entity);
        } catch (Exception e) {
            throw  e;
        } finally {
            ConnectionPool.get().free(conn);
        }
    }

    public void get(String id, Connection conn, T entity) throws Exception {
        getDbDao().get(id, conn, entity);
    }

    public void get(String id, DataSource dataSource, Connection conn, T entity ) throws Exception {
        if(dataSource == DataSource.DATABASE_ONLY) {
            getDbDao().get(id, conn, entity);
        } else if(dataSource == DataSource.REDIS_ONLY) {
            getRdDao().get(id, entity);
        } else if(dataSource == DataSource.DATABASE_PRIORITY) {
            try {getRdDao().get(id, entity);} catch (Exception e){}
            if(entity.identity()  == null) {
                getDbDao().get(id, conn, entity);
            }
        }
    }

    public void delete(T entity, DataSource dataSource) throws Exception {
        delete(entity.identity(), dataSource);
    }

    public void delete(T entity, Connection conn, DataSource dataSource) throws Exception {
        delete(entity.identity(), dataSource, conn);
    }

    @Override
    public void delete(T entity) throws Exception {
     delete(entity.identity());
    }

    public void delete(T entity, Connection conn) throws Exception {
       delete(entity.identity(), conn);
    }

    public void delete(String id, DataSource dataSource) throws Exception {
        Connection conn = ConnectionPool.get().getConnection();
        try {
            delete(id, dataSource, conn);
        } catch (Exception e) {
            throw  e;
        } finally {
            ConnectionPool.get().free(conn);
        }
    }

    public void delete(String id, DataSource dataSource, Connection conn) throws Exception {
        if(dataSource == DataSource.DATABASE_ONLY) {
            getDbDao().delete(id, conn);
        } else if(dataSource == DataSource.REDIS_ONLY) {
            getRdDao().delete(id);
        } else if(dataSource == DataSource.DATABASE_AND_REDIS) {
            getDbDao().delete(id, conn);
            getRdDao().delete(id);
        } else {
            throw new ParameterException("dataSource 超出范围");
        }
    }

    //tested
    public void delete(String id) throws Exception {
        Connection conn = ConnectionPool.get().getConnection();
        try {
            delete(id, conn);
        } catch (Exception e) {
            throw e;
        } finally {
            ConnectionPool.get().free(conn);
        }
    }

    public void delete(String id, Connection conn) throws Exception {
       getDbDao().delete(id, conn);
    }

    @Override
    public void add(T entity) throws Exception {
        getDbDao().add(entity);
    }

    public void add(T entity, Connection conn) throws Exception {
        getDbDao().add(entity, conn);
    }

    public void add(T entity, DataSource dataSource) throws Exception {
        Connection conn = ConnectionPool.get().getConnection();
        try {
            add(entity, dataSource, conn);
        } catch (Exception e) {
            throw e;
        } finally {
            ConnectionPool.get().free(conn);
        }
    }

    public void add(T entity, DataSource dataSource, Connection conn) throws Exception {
        if(dataSource == DataSource.DATABASE_ONLY) {
            getDbDao().add(entity);
        } else if(dataSource == DataSource.REDIS_ONLY) {
            getRdDao().add(entity);
        } else if(dataSource == DataSource.DATABASE_AND_REDIS) {
            boolean oriAutoCommit = conn.getAutoCommit();
            conn.setAutoCommit(false);
            try {
                getDbDao().add(entity, conn);
            } catch (Exception e) {
                conn.setAutoCommit(oriAutoCommit);
                throw e;
            }

            try {
                getDbDao().add(entity);
            } catch (Exception e) {
                conn.rollback();
                conn.setAutoCommit(oriAutoCommit);
                throw  e;
            }
            conn.commit();
            conn.setAutoCommit(oriAutoCommit);
        } else {
            throw new ParameterException("dataSource 超出范围.");
        }
    }

    public void save(T entity) throws Exception {
        getDbDao().save(entity);
    }

    public void save(T entity, DataSource dataSource) throws Exception {
        Connection conn = ConnectionPool.get().getConnection();
        try {
            save(entity, dataSource, conn);
        } catch (Exception e) {
            throw e;
        } finally {
            ConnectionPool.get().free(conn);
        }
    }

    public void save(T entity, Connection conn) throws Exception {
        getDbDao().save(entity, conn);
    }

    public void save(T entity, DataSource dataSource, Connection conn) throws Exception {
        if(dataSource == DataSource.DATABASE_ONLY){
            getDbDao().save(entity);
        } else if(dataSource == dataSource.REDIS_ONLY) {
            getRdDao().save(entity);
        } else if(dataSource == dataSource.DATABASE_AND_REDIS) {
            boolean oriAutoCommit = conn.getAutoCommit();
            conn.setAutoCommit(false);
            try {
                getDbDao().save(entity, conn);
            } catch (Exception e) {
                conn.setAutoCommit(oriAutoCommit);
                throw e;
            }

            try {
                getRdDao().save(entity);
            } catch (Exception e) {
                conn.rollback();
                conn.setAutoCommit(oriAutoCommit);
                throw  e;
            }

            conn.commit();
            conn.setAutoCommit(oriAutoCommit);
        } else {
            throw new ParameterException("dataSource超出范围");
        }
    }

}
