package com.efeiyi.website.dao;

import java.io.IOException;
import java.util.Properties;

public class ConnectionConfigurationReader {
    private ConnectionConfiguration config = null;

    public ConnectionConfigurationReader() {

    }

    public ConnectionConfiguration getConfiguration() throws IOException {
        if (config != null) {
            return config;
        }
        config = new ConnectionConfiguration();
        Properties props = new Properties();

        props.load(getClass()
                .getResourceAsStream("/database.properties"));
        config.setDriver(props.getProperty("jdbc.driverClassName"));
        config.setMaxConn(Integer.parseInt(props.getProperty("maxconn")));
        config.setPassword(props.getProperty("jdbc.password"));
        config.setUrl(props.getProperty("jdbc.url"));
        config.setUserName(props.getProperty("jdbc.username"));
        config.setCacheCanBeUseed(props.getProperty("cache.status").equals("true"));
        return config;
    }
}
