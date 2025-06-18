package com.currencyapi.repository;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;

public class DatabaseConnectionManager {

    private static final HikariDataSource dataSource;

    static {
        HikariConfig config = new HikariConfig();


        config.setJdbcUrl("jdbc:postgresql://localhost:5432/currencydb");
        config.setUsername("pguser");
        config.setPassword("password");
        config.setDriverClassName("org.postgresql.Driver");


        config.setMaximumPoolSize(10);
        config.setIdleTimeout(60000);
        config.setConnectionTimeout(30000);


        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");

        dataSource = new HikariDataSource(config);
    }


    public static Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }
}

