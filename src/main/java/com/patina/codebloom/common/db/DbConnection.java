package com.patina.codebloom.common.db;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@EnableConfigurationProperties(DbProperties.class)
public class DbConnection {

    private final DbProperties dbProperties;

    private Connection conn = null;

    public DbConnection(final DbProperties dbProperties) {
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
