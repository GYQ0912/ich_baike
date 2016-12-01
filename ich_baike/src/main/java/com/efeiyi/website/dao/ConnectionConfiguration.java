package com.efeiyi.website.dao;

public class ConnectionConfiguration {

    private String driver = ""; //数据库驱动
    private String url = ""; //数据库url
    private String userName = ""; //用户名
    private String password = ""; //密码
    private int maxConn = 0; //最大连接数
    private boolean cacheCanBeUseed;  //true 缓存可用 false 缓存不可用

    public ConnectionConfiguration() {

    }

    public boolean isCacheCanBeUseed() {
        return cacheCanBeUseed;
    }

    public void setCacheCanBeUseed(boolean cacheCanBeUseed) {
        this.cacheCanBeUseed = cacheCanBeUseed;
    }

    public String getDriver() {
        return driver;
    }

    public void setDriver(String driver) {
        this.driver = driver;
    }

    public int getMaxConn() {
        return maxConn;
    }

    public void setMaxConn(int maxconn) {
        this.maxConn = maxconn;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }


    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
