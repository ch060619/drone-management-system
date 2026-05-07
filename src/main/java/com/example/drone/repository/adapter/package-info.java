/**
 * 数据库适配器包.
 * <p>
 * 提供数据库适配器接口和实现类，支持 SQLite 和 MySQL 数据库切换。
 * 使用策略模式 + 工厂模式，根据配置文件自动选择对应的数据库适配器。
 * </p>
 * <p>
 * 主要组件：
 * <ul>
 *   <li>{@link com.example.drone.repository.adapter.DatabaseAdapter} - 数据库适配器接口</li>
 *   <li>{@link com.example.drone.repository.adapter.SQLiteAdapter} - SQLite 适配器实现</li>
 *   <li>{@link com.example.drone.repository.adapter.MySQLAdapter} - MySQL 适配器实现</li>
 * </ul>
 * </p>
 * <p>
 * 使用方式：
 * <pre>
 * # 在 application.yml 中配置数据库类型
 * database:
 *   type: sqlite  # 或 mysql
 * </pre>
 * </p>
 *
 * @author Drone Management System
 * @version 1.0.0
 * @since 2024-01-15
 */
package com.example.drone.repository.adapter;
