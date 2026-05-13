package com.example.drone.repository.adapter;

import com.alibaba.druid.pool.DruidDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class MySQLAdapter implements DatabaseAdapter {

    private static final Logger logger = LoggerFactory.getLogger(MySQLAdapter.class);

    private final String url;
    private final String username;
    private final String password;
    private final String driverClassName;

    public MySQLAdapter(String url, String username, String password, String driverClassName) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.driverClassName = driverClassName;
    }

    @Override
    public DataSource getDataSource() {
        logger.info("Initializing MySQL datasource, url: {}", url);

        ensureDatabaseExists();

        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setUrl(url);
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        dataSource.setDriverClassName(driverClassName);
        dataSource.setInitialSize(5);
        dataSource.setMinIdle(5);
        dataSource.setMaxActive(20);
        dataSource.setMaxWait(60000);
        dataSource.setTimeBetweenEvictionRunsMillis(60000);
        dataSource.setMinEvictableIdleTimeMillis(300000);
        dataSource.setValidationQuery("SELECT 1");
        dataSource.setTestWhileIdle(true);
        dataSource.setTestOnBorrow(false);
        dataSource.setTestOnReturn(false);

        try {
            dataSource.init();
            initializeDatabase(dataSource);
            logger.info("MySQL datasource initialized successfully");
        } catch (SQLException e) {
            logger.error("Failed to initialize MySQL datasource", e);
            throw new RuntimeException("Unable to initialize MySQL datasource", e);
        }
        return dataSource;
    }

    private void ensureDatabaseExists() {
        String dbName = extractDatabaseName(url);
        String baseUrl = url.substring(0, url.indexOf(dbName))
                + "?useSSL=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true";
        try {
            Class.forName(driverClassName);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("MySQL driver not found: " + driverClassName, e);
        }
        try (Connection conn = DriverManager.getConnection(baseUrl, username, password);
             Statement stmt = conn.createStatement()) {
            stmt.execute("CREATE DATABASE IF NOT EXISTS " + dbName
                    + " DEFAULT CHARACTER SET utf8mb4 DEFAULT COLLATE utf8mb4_unicode_ci");
            logger.info("Database {} is ready", dbName);
        } catch (SQLException e) {
            logger.warn("Database creation skipped: {}", e.getMessage());
        }
    }

    private void initializeDatabase(DataSource dataSource) {
        DatabaseScriptRunner.runScript(dataSource, "sql/mysql/schema.sql");
        DatabaseScriptRunner.runScript(dataSource, "sql/mysql/seed.sql");
    }

    private String extractDatabaseName(String jdbcUrl) {
        int slashAfterPort = jdbcUrl.lastIndexOf('/');
        String dbPart = jdbcUrl.substring(slashAfterPort + 1);
        int qm = dbPart.indexOf('?');
        return qm > 0 ? dbPart.substring(0, qm) : dbPart;
    }

    @Override
    public String getDatabaseType() {
        return "mysql";
    }
}
