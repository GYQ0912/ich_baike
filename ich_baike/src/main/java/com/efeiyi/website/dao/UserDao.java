package com.efeiyi.website.dao;

import com.efeiyi.website.entity.Function;
import com.efeiyi.website.entity.User;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;


public class UserDao extends BaseDao<User> {

    @Override
    protected BaseRdDao getRdDao() {
        return new UserRdDao();
    }

    @Override
    protected BaseDbDao getDbDao() {
        return new UserDbDao();
    }

    @Override
    public void get(String id, User entity) throws Exception {
        Connection conn = ConnectionPool.get().getConnection();
        try {
            get(id, conn, entity);
        } catch (Exception e) {
            ConnectionPool.get().free(conn);
            throw e;
        }
        FunctionDao functionDao = new FunctionDao();
        List<Function> functionList = new ArrayList<>();
        try {
            //functionDao.getByRole(entity.getRoleId(), functionList, conn);
        } catch (Exception e) {
            throw  e;
        } finally {
            ConnectionPool.get().free(conn);
        }
    }

    public void login(String userName, String password, String ip, User entity) throws Exception {
        Connection connection = ConnectionPool.get().getConnection();
        Object[] params = {userName, password, ip};
        try {
            super.get("login", params, entity, connection);
        } catch (Exception e) {
            ConnectionPool.get().free(connection);
            throw e;
        }

        if (entity.identity() == null) {
            return;
        }

        FunctionDao functionDao = new FunctionDao();
        List<Function> functionList = new ArrayList<Function>();
        try {
            //functionDao.getByRole(entity.getRoleId(), functionList, connection);
        } catch (Exception e) {
            ConnectionPool.get().free(connection);
            throw e;
        }
        //entity.setFunctionList(functionList);
        ConnectionPool.get().free(connection);
        entity.setPassword(null);
    }

    private class UserRdDao extends BaseRdDao<User> {
    }

    private class UserDbDao extends BaseDbDao<User> {
    }

}
