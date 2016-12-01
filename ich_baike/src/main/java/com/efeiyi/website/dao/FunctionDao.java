package com.efeiyi.website.dao;

import com.efeiyi.website.entity.Function;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/7/14 0014.
 */
public class FunctionDao extends BaseDao<Function> {


    public void getByRole(String roleId, List<Function> functionList, Connection conn) throws Exception{
        super.get("select_function_by_role", new Object[]{roleId}, functionList, conn);
    }

    public Function getByCode(String code, boolean isDbOnly) throws Exception{
        List<Function> functionList = new ArrayList<Function>();
        getAll(functionList, isDbOnly);
        for (Function function:functionList) {
            if(function.getCode() != null && function.getCode().equals(code)) {
                return function;
            }
        }
        return null;
    }

    public  void save(Function function, Connection conn) throws Exception {
        boolean oriAutoCommit = conn.getAutoCommit();
        conn.setAutoCommit(false);
        super.save(function, conn);

        //重新从数据库加载
        List<Function> functionList = new ArrayList<Function>();
        try {
            get("select_all_function", new Object[]{}, functionList, conn);
        } catch(Exception e) {
            conn.rollback();
            throw e;
        }
        conn.commit();
        conn.setAutoCommit(oriAutoCommit);
    }

    public void getAll(List<Function> functionList, boolean isDbOnly) throws Exception{
        get("select_all_function", new Object[]{}, functionList);
    }

    @Override
    protected BaseRdDao getRdDao() {
        return new FunctionRdDao();
    }

    @Override
    protected BaseDbDao getDbDao() {
        return new FunctionDbDao();
    }

    private class FunctionRdDao extends BaseRdDao<Function> {
    }

    private class FunctionDbDao extends BaseDbDao<Function> {
    }

}
