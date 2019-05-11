package com.yihe.crawler;

import java.sql.Connection;
import java.sql.SQLException;

import org.h2.jdbcx.JdbcConnectionPool;

public class DB {
    private static DB cp = null;
    private JdbcConnectionPool jdbcCP = null;

    private DB() {
        String dbPath = "./config/data";
        jdbcCP = JdbcConnectionPool.create("jdbc:h2:" + dbPath, "sa", "");
        jdbcCP.setMaxConnections(50);
    }

    public static DB getInstance() {
        if (cp == null) {
            cp = new DB();
        }
        return cp;
    }

    public Connection getConnection() throws SQLException {
        return jdbcCP.getConnection();
    }
}