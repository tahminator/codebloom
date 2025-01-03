package com.patina.codebloom.common.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.springframework.boot.context.properties.EnableConfigurationProperties;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

@EnableConfigurationProperties(DbProperties.class)
public class DbConnection {
    final DbProperties dbProperties;

    public Connection conn = null;

    public DbConnection(DbProperties dbProperties) {
        this.dbProperties = dbProperties;
    }

    public Connection getConn() {
        return conn;
    }

    @PostConstruct
    private void populateConn() {
        String url = dbProperties.getConnection();
        try {
            conn = DriverManager.getConnection(url);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @PreDestroy
    private void closeConn() {
        if (conn != null) {
            try {
                if (!conn.getAutoCommit()) {
                    conn.rollback();
                }
                conn.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
