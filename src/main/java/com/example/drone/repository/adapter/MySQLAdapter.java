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
        logger.info("初始化 MySQL 数据源，URL: {}", url);

        // 1. 先确保数据库存在
        ensureDatabaseExists();

        // 2. 建表
        ensureTablesExist();

        // 3. 创建连接池
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
            logger.info("MySQL 数据源初始化成功");
        } catch (SQLException e) {
            logger.error("MySQL 数据源初始化失败", e);
            throw new RuntimeException("无法初始化 MySQL 数据源", e);
        }
        return dataSource;
    }

    private void ensureDatabaseExists() {
        String dbName = extractDatabaseName(url);
        String baseUrl = url.substring(0, url.indexOf(dbName)) + "?useSSL=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true";
        try {
            Class.forName(driverClassName);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("MySQL 驱动未找到: " + driverClassName);
        }
        try (Connection conn = DriverManager.getConnection(baseUrl, username, password);
             Statement stmt = conn.createStatement()) {
            stmt.execute("CREATE DATABASE IF NOT EXISTS " + dbName
                    + " DEFAULT CHARACTER SET utf8mb4 DEFAULT COLLATE utf8mb4_unicode_ci");
            logger.info("数据库 {} 已就绪", dbName);
        } catch (SQLException e) {
            logger.warn("数据库创建尝试: {}", e.getMessage());
        }
    }

    private void ensureTablesExist() {
        try {
            Class.forName(driverClassName);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("MySQL 驱动未找到");
        }
        try (Connection conn = DriverManager.getConnection(url, username, password);
             Statement stmt = conn.createStatement()) {

            stmt.execute("CREATE TABLE IF NOT EXISTS t_drone (" +
                "id BIGINT AUTO_INCREMENT PRIMARY KEY," +
                "serial_number VARCHAR(100) NOT NULL UNIQUE," +
                "model_name VARCHAR(200) NOT NULL," +
                "manufacturer VARCHAR(200) NOT NULL," +
                "purchase_date VARCHAR(50) NOT NULL," +
                "status VARCHAR(50) NOT NULL," +
                "max_flight_time INT NOT NULL," +
                "max_flight_distance INT NOT NULL," +
                "weight INT NOT NULL," +
                "remarks TEXT," +
                "created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP," +
                "updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4");
            logger.info("MySQL t_drone 表已就绪");

            stmt.execute("CREATE TABLE IF NOT EXISTS t_flight_mission (" +
                "id BIGINT AUTO_INCREMENT PRIMARY KEY," +
                "mission_code VARCHAR(100) NOT NULL UNIQUE," +
                "drone_id BIGINT NOT NULL," +
                "mission_type VARCHAR(50) NOT NULL," +
                "status VARCHAR(50) NOT NULL DEFAULT 'PENDING'," +
                "pilot_name VARCHAR(100)," +
                "planned_start_time VARCHAR(50)," +
                "planned_end_time VARCHAR(50)," +
                "actual_start_time VARCHAR(50)," +
                "actual_end_time VARCHAR(50)," +
                "flight_area VARCHAR(200)," +
                "remarks TEXT," +
                "created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP," +
                "updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP," +
                "FOREIGN KEY (drone_id) REFERENCES t_drone(id) ON DELETE CASCADE" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4");
            logger.info("MySQL t_flight_mission 表已就绪");

            stmt.execute("CREATE TABLE IF NOT EXISTS t_flight_log (" +
                "id BIGINT AUTO_INCREMENT PRIMARY KEY," +
                "drone_id BIGINT NOT NULL," +
                "mission_id BIGINT," +
                "flight_duration INT NOT NULL," +
                "flight_distance INT," +
                "max_altitude INT," +
                "takeoff_location VARCHAR(200)," +
                "landing_location VARCHAR(200)," +
                "weather_condition VARCHAR(100)," +
                "wind_speed INT," +
                "battery_consumed INT," +
                "remarks TEXT," +
                "flight_date VARCHAR(50) NOT NULL," +
                "created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP," +
                "FOREIGN KEY (drone_id) REFERENCES t_drone(id) ON DELETE CASCADE" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4");
            logger.info("MySQL t_flight_log 表已就绪");

            stmt.execute("CREATE TABLE IF NOT EXISTS t_maintenance (" +
                "id BIGINT AUTO_INCREMENT PRIMARY KEY," +
                "drone_id BIGINT NOT NULL," +
                "maintenance_type VARCHAR(50) NOT NULL," +
                "status VARCHAR(50) NOT NULL DEFAULT 'PENDING'," +
                "scheduled_date VARCHAR(50) NOT NULL," +
                "completed_date VARCHAR(50)," +
                "technician VARCHAR(100)," +
                "parts_replaced TEXT," +
                "cost INT DEFAULT 0," +
                "description TEXT," +
                "next_maintenance_date VARCHAR(50)," +
                "created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP," +
                "updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP," +
                "FOREIGN KEY (drone_id) REFERENCES t_drone(id) ON DELETE CASCADE" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4");
            logger.info("MySQL t_maintenance 表已就绪");

            // 认证授权系统 — 7 张表
            stmt.execute("CREATE TABLE IF NOT EXISTS sys_user (" +
                "id BIGINT AUTO_INCREMENT PRIMARY KEY," +
                "email VARCHAR(200) NOT NULL UNIQUE," +
                "password_hash VARCHAR(255) NOT NULL," +
                "status VARCHAR(20) NOT NULL DEFAULT 'INACTIVE'," +
                "role VARCHAR(50) NOT NULL DEFAULT 'user'," +
                "activation_token VARCHAR(64)," +
                "created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP," +
                "updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4");
            logger.info("MySQL sys_user 表已就绪");

            stmt.execute("CREATE TABLE IF NOT EXISTS sys_role (" +
                "id BIGINT AUTO_INCREMENT PRIMARY KEY," +
                "role_code VARCHAR(50) NOT NULL UNIQUE," +
                "role_name VARCHAR(100) NOT NULL," +
                "description VARCHAR(255)" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4");
            logger.info("MySQL sys_role 表已就绪");

            stmt.execute("CREATE TABLE IF NOT EXISTS sys_permission (" +
                "id BIGINT AUTO_INCREMENT PRIMARY KEY," +
                "perm_code VARCHAR(100) NOT NULL UNIQUE," +
                "perm_name VARCHAR(100) NOT NULL," +
                "description VARCHAR(255)" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4");
            logger.info("MySQL sys_permission 表已就绪");

            stmt.execute("CREATE TABLE IF NOT EXISTS sys_user_role (" +
                "id BIGINT AUTO_INCREMENT PRIMARY KEY," +
                "user_id BIGINT NOT NULL," +
                "role_id BIGINT NOT NULL," +
                "UNIQUE KEY uk_user_role (user_id, role_id)" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4");
            logger.info("MySQL sys_user_role 表已就绪");

            stmt.execute("CREATE TABLE IF NOT EXISTS sys_role_permission (" +
                "id BIGINT AUTO_INCREMENT PRIMARY KEY," +
                "role_id BIGINT NOT NULL," +
                "permission_id BIGINT NOT NULL," +
                "UNIQUE KEY uk_role_perm (role_id, permission_id)" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4");
            logger.info("MySQL sys_role_permission 表已就绪");

            stmt.execute("CREATE TABLE IF NOT EXISTS sys_login_log (" +
                "id BIGINT AUTO_INCREMENT PRIMARY KEY," +
                "user_id BIGINT," +
                "ip VARCHAR(50) NOT NULL," +
                "device VARCHAR(255)," +
                "login_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP," +
                "result VARCHAR(20) NOT NULL," +
                "fail_reason VARCHAR(255)" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4");
            logger.info("MySQL sys_login_log 表已就绪");

            stmt.execute("CREATE TABLE IF NOT EXISTS sys_operation_log (" +
                "id BIGINT AUTO_INCREMENT PRIMARY KEY," +
                "user_id BIGINT," +
                "operation VARCHAR(100) NOT NULL," +
                "target VARCHAR(200)," +
                "ip VARCHAR(50) NOT NULL," +
                "op_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP," +
                "result VARCHAR(20) NOT NULL" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4");
            logger.info("MySQL sys_operation_log 表已就绪");

            // 初始化角色和权限种子数据
            stmt.execute("INSERT IGNORE INTO sys_role (role_code, role_name, description) VALUES " +
                "('admin', '管理员', '系统管理员，拥有所有权限')," +
                "('user', '普通用户', '普通用户，拥有所有权限')");
            stmt.execute("UPDATE sys_role SET description = '普通用户，拥有所有权限' WHERE role_code = 'user'");
            stmt.execute("INSERT IGNORE INTO sys_permission (perm_code, perm_name, description) VALUES " +
                "('drone:list', '查看无人机', '分页查询无人机列表')," +
                "('drone:create', '创建无人机', '新增无人机记录')," +
                "('drone:update', '更新无人机', '修改无人机信息')," +
                "('drone:delete', '删除无人机', '删除无人机记录')," +
                "('mission:list', '查看飞行任务', '分页查询飞行任务列表')," +
                "('mission:create', '创建飞行任务', '新增飞行任务记录')," +
                "('mission:update', '更新飞行任务', '修改飞行任务信息')," +
                "('mission:delete', '删除飞行任务', '删除飞行任务记录')," +
                "('maintenance:list', '查看维护记录', '分页查询维护记录列表')," +
                "('maintenance:create', '创建维护记录', '新增维护记录')," +
                "('maintenance:update', '更新维护记录', '修改维护记录信息')," +
                "('maintenance:delete', '删除维护记录', '删除维护记录')," +
                "('flightlog:list', '查看飞行日志', '分页查询飞行日志列表')," +
                "('flightlog:create', '创建飞行日志', '新增飞行日志记录')," +
                "('flightlog:delete', '删除飞行日志', '删除飞行日志记录')");
            stmt.execute("INSERT IGNORE INTO sys_role_permission (role_id, permission_id) " +
                "SELECT r.id, p.id FROM sys_role r CROSS JOIN sys_permission p WHERE r.role_code = 'admin'");
            stmt.execute("DELETE FROM sys_role_permission WHERE role_id = (SELECT id FROM sys_role WHERE role_code = 'user')");
            stmt.execute("INSERT INTO sys_role_permission (role_id, permission_id) " +
                "SELECT r.id, p.id FROM sys_role r CROSS JOIN sys_permission p WHERE r.role_code = 'user'");
            stmt.execute("INSERT IGNORE INTO sys_user_role (user_id, role_id) " +
                "SELECT u.id, r.id FROM sys_user u CROSS JOIN sys_role r WHERE u.role = r.role_code AND NOT EXISTS (SELECT 1 FROM sys_user_role ur WHERE ur.user_id = u.id AND ur.role_id = r.id)");

        } catch (SQLException e) {
            logger.warn("初始化 MySQL 表失败（可能已存在）: {}", e.getMessage());
        }
    }

    private String extractDatabaseName(String jdbcUrl) {
        // jdbc:mysql://localhost:3306/drone_management?params...
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
