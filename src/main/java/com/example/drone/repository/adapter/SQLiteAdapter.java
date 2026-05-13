package com.example.drone.repository.adapter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sqlite.SQLiteDataSource;

import javax.sql.DataSource;

public class SQLiteAdapter implements DatabaseAdapter {

    private static final Logger logger = LoggerFactory.getLogger(SQLiteAdapter.class);

    private final String dbPath;

    public SQLiteAdapter(String dbPath) {
        this.dbPath = dbPath;
    }

    @Override
    public DataSource getDataSource() {
        logger.info("Initializing SQLite datasource, path: {}", dbPath);

        SQLiteDataSource dataSource = new SQLiteDataSource();
        dataSource.setUrl("jdbc:sqlite:" + dbPath);

        initializeDatabase(dataSource);

        logger.info("SQLite datasource initialized successfully");
        return dataSource;
    }

    private void initializeDatabase(SQLiteDataSource dataSource) {
        DatabaseScriptRunner.runScript(dataSource, "sql/sqlite/schema.sql");
        DatabaseScriptRunner.runScript(dataSource, "sql/sqlite/seed.sql");
    }

    @Override
    public String getDatabaseType() {
        return "sqlite";
    }
}
