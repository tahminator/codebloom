package com.patina.codebloom.common.db;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "db.postgres")
public class DbProperties {

    private String connection;

    public String getConnection() {
        return this.connection;
    }

    public void setConnection(final String connection) {
        this.connection = connection;
    }
}
