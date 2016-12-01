package com.efeiyi.website.dao;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;

import com.efeiyi.website.util.Util;

public class ConnectionPool {
    private Connection conn = null;
    private Vector<Connection> freeConns = new Vector<Connection>();//空闲连接
    private Vector<Connection> activeConns = new Vector<Connection>();//活动连接
    private int maxConn; // 最大连接
    private String password; // 密码
    private String url; // 数据库连接地址
    private String driver; // 驱动
    private String user; // 用户名

    private static ConnectionPool instance;

    static {
        ConnectionConfigurationReader reader = new ConnectionConfigurationReader();
        ConnectionConfiguration config = null;
        try {
            config = reader.getConfiguration();
        } catch (IOException e) {
            Util.getLogger(ConnectionPool.class).error(e.toString());
        }
        instance = new ConnectionPool(config);
    }

    public static ConnectionPool get() {
        return instance;
    }

    private ConnectionPool() {
    }

    private ConnectionPool(ConnectionConfiguration config) {
        this.driver = config.getDriver();
        this.url = config.getUrl();
        this.user = config.getUserName();
        this.password = config.getPassword();
        this.maxConn = config.getMaxConn();
    }

    public void free(Connection conn) {
        activeConns.remove(conn);

        boolean isAutoCommit = true;
        boolean clearTransFailed = false;

        try {
            isAutoCommit = conn.getAutoCommit();
        } catch (SQLException e) {
            Util.getLogger(ConnectionPool.class).error(e.toString());
            clearTransFailed = false;
        }

        if (!isAutoCommit) {
            try {
                conn.rollback();
                conn.setAutoCommit(true);
            } catch (SQLException e) {
                Util.getLogger(ConnectionPool.class).error(e.toString());
                clearTransFailed = false;
            }
        }
        if (!clearTransFailed) {
            freeConns.add(conn);// 添加到空闲连接的末尾
        }

        StackTraceElement[] temp = Thread.currentThread().getStackTrace();
        StackTraceElement a = (StackTraceElement) temp[3];
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Util.getLogger(this.getClass()).info("释放Connection：" + a.getMethodName() +
                "--释放时间：" + df.format(new Date()) +
                "空闲连接" + freeConns.size() +
                "活动连接" + activeConns.size() +
                "连接总数" + (freeConns.size() + activeConns.size()) +
                "最大连接数" + maxConn);
    }

    public synchronized Connection getConnection() throws Exception {
        Connection conn = null;
        if (this.freeConns.size() > 0) {
            conn = (Connection) this.freeConns.get(0);
            freeConns.remove(conn);
            if (conn.isClosed()) {
                this.release(conn);
                conn = null;
            } else {
                activeConns.add(conn);
            }
        }

        int connCount = freeConns.size() + activeConns.size();
        if (conn == null && connCount < maxConn) {
            conn = newConnection(); // 新建连接
            if (conn == null) {
                throw new SQLException();
            }
            activeConns.add(conn);
        } else if (conn == null && connCount >= maxConn) {
            Thread.sleep(50);
            Util.getLogger(this.getClass()).error("连接池已满，稍后分配连接...");
            return getConnection();
        }

        StackTraceElement[] temp = Thread.currentThread().getStackTrace();
        StackTraceElement a = (StackTraceElement) temp[3];
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Util.getLogger(this.getClass()).info("调用Connection：" + a.getMethodName() +
                "--调用时间：" + df.format(new Date()) +
                "空闲连接" + freeConns.size() +
                "活动连接" + activeConns.size() +
                "连接总数" + (freeConns.size() + activeConns.size()) +
                "最大连接数" + maxConn);
        return conn;
    }

    public void release() throws SQLException {
        for (int i = 0; i < freeConns.size(); i++) {
            Connection conn = freeConns.get(i);
            try {
                conn.close();
                freeConns.remove(conn);
            } catch (SQLException e) {
                throw e;
            }
        }
        for (int i = 0; i < activeConns.size(); i++) {
            Connection conn = activeConns.get(i);
            try {
                conn.close();
                activeConns.remove(conn);
            } catch (SQLException e) {
                throw e;
            }
        }
    }

    public void release(Connection conn) throws SQLException {
        try {
            conn.close();
        } catch (SQLException e) {
            Util.getLogger(this.getClass()).error(e.getMessage());
            throw e;
        }
    }

    private Connection newConnection() {
        try {
            Class.forName(driver);
            conn = DriverManager.getConnection(url, user, password);
            Util.getLogger(this.getClass()).info("数据库连接创建成功.");
        } catch (ClassNotFoundException e) {
            Util.getLogger(this.getClass()).error("找不到驱动");
        } catch (SQLException e) {
            Util.getLogger(this.getClass()).error("无法创建连接 :" + e.toString());
        }
        return conn;
    }
}

