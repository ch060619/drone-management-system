package com.example.drone.repository.adapter;

import javax.sql.DataSource;

/**
 * 数据库适配器接口.
 * <p>
 * 定义数据库适配器的通用行为，支持 SQLite 和 MySQL 数据库切换。
 * 使用策略模式，根据配置文件中的 database.type 选择具体的适配器实现。
 * </p>
 *
 * @author Drone Management System
 * @version 1.0.0
 * @since 2024-01-15
 */
public interface DatabaseAdapter {

    /**
     * 获取数据源.
     * <p>
     * 返回配置好的数据源对象，供 MyBatis 使用。
     * SQLite 使用 SQLiteDataSource，MySQL 使用 Druid 连接池。
     * </p>
     *
     * @return 数据源对象
     */
    DataSource getDataSource();

    /**
     * 获取数据库类型.
     * <p>
     * 返回数据库类型标识，用于日志记录和调试。
     * </p>
     *
     * @return 数据库类型（"sqlite" 或 "mysql"）
     */
    String getDatabaseType();
}
