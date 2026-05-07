package com.example.drone.repository.adapter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sqlite.SQLiteDataSource;

import javax.sql.DataSource;

/**
 * SQLite 数据库适配器实现.
 * <p>
 * 当配置文件中 database.type=sqlite 时，该适配器被激活。
 * 使用 SQLiteDataSource 作为数据源，适用于小规模部署和开发环境。
 * </p>
 *
 * @author Drone Management System
 * @version 1.0.0
 * @since 2024-01-15
 */
public class SQLiteAdapter implements DatabaseAdapter {

    private static final Logger logger = LoggerFactory.getLogger(SQLiteAdapter.class);

    /**
     * SQLite 数据库文件路径.
     * <p>
     * 从配置文件中读取 database.sqlite.path 属性。
     * 示例：./data/drone.db
     * </p>
     */
    private final String dbPath;

    /**
     * 构造器.
     *
     * @param dbPath SQLite 数据库文件路径
     */
    public SQLiteAdapter(String dbPath) {
        this.dbPath = dbPath;
    }

    /**
     * 获取 SQLite 数据源.
     * <p>
     * 创建并配置 SQLiteDataSource 对象，设置 JDBC URL。
     * URL 格式：jdbc:sqlite:数据库文件路径
     * </p>
     *
     * @return SQLite 数据源对象
     */
    @Override
    public DataSource getDataSource() {
        logger.info("初始化 SQLite 数据源，数据库路径: {}", dbPath);
        
        SQLiteDataSource dataSource = new SQLiteDataSource();
        dataSource.setUrl("jdbc:sqlite:" + dbPath);
        
        // 初始化数据库表结构
        initializeDatabase(dataSource);
        
        logger.info("SQLite 数据源初始化成功");
        return dataSource;
    }
    
    /**
     * 初始化数据库表结构.
     * 
     * @param dataSource 数据源
     */
    private void initializeDatabase(SQLiteDataSource dataSource) {
        try (java.sql.Connection conn = dataSource.getConnection();
             java.sql.Statement stmt = conn.createStatement()) {
            
            stmt.execute("CREATE TABLE IF NOT EXISTS t_drone (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "serial_number TEXT NOT NULL UNIQUE," +
                "model_name TEXT NOT NULL," +
                "manufacturer TEXT NOT NULL," +
                "purchase_date TEXT NOT NULL," +
                "status TEXT NOT NULL," +
                "max_flight_time INTEGER NOT NULL," +
                "max_flight_distance INTEGER NOT NULL," +
                "weight INTEGER NOT NULL," +
                "remarks TEXT," +
                "created_at TEXT NOT NULL DEFAULT (datetime('now', 'localtime'))," +
                "updated_at TEXT NOT NULL DEFAULT (datetime('now', 'localtime'))" +
                ")");
            
            stmt.execute("CREATE INDEX IF NOT EXISTS idx_serial_number ON t_drone(serial_number)");
            stmt.execute("CREATE INDEX IF NOT EXISTS idx_model_name ON t_drone(model_name)");
            stmt.execute("CREATE INDEX IF NOT EXISTS idx_manufacturer ON t_drone(manufacturer)");
            stmt.execute("CREATE INDEX IF NOT EXISTS idx_status ON t_drone(status)");
            
            logger.info("SQLite t_drone 表初始化成功");
        } catch (Exception e) {
            logger.error("SQLite t_drone 表初始化失败: {}", e.getMessage(), e);
        }

        try (java.sql.Connection conn = dataSource.getConnection();
             java.sql.Statement stmt = conn.createStatement()) {
            stmt.execute("CREATE TABLE IF NOT EXISTS t_flight_mission (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "mission_code TEXT NOT NULL UNIQUE," +
                "drone_id INTEGER NOT NULL REFERENCES t_drone(id)," +
                "mission_type TEXT NOT NULL," +
                "status TEXT NOT NULL DEFAULT 'PENDING'," +
                "pilot_name TEXT," +
                "planned_start_time TEXT," +
                "planned_end_time TEXT," +
                "actual_start_time TEXT," +
                "actual_end_time TEXT," +
                "flight_area TEXT," +
                "remarks TEXT," +
                "created_at TEXT NOT NULL," +
                "updated_at TEXT NOT NULL)");
            logger.info("SQLite t_flight_mission 表初始化成功");
        } catch (Exception e) {
            logger.error("SQLite t_flight_mission 表初始化失败: {}", e.getMessage(), e);
        }

        try (java.sql.Connection conn = dataSource.getConnection();
             java.sql.Statement stmt = conn.createStatement()) {
            stmt.execute("CREATE TABLE IF NOT EXISTS t_flight_log (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "drone_id INTEGER NOT NULL REFERENCES t_drone(id)," +
                "mission_id INTEGER REFERENCES t_flight_mission(id)," +
                "flight_duration INTEGER NOT NULL," +
                "flight_distance INTEGER," +
                "max_altitude INTEGER," +
                "takeoff_location TEXT," +
                "landing_location TEXT," +
                "weather_condition TEXT," +
                "wind_speed INTEGER," +
                "battery_consumed INTEGER," +
                "remarks TEXT," +
                "flight_date TEXT NOT NULL," +
                "created_at TEXT NOT NULL)");
            logger.info("SQLite t_flight_log 表初始化成功");
        } catch (Exception e) {
            logger.error("SQLite t_flight_log 表初始化失败: {}", e.getMessage(), e);
        }

        try (java.sql.Connection conn = dataSource.getConnection();
             java.sql.Statement stmt = conn.createStatement()) {
            stmt.execute("CREATE TABLE IF NOT EXISTS t_maintenance (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "drone_id INTEGER NOT NULL REFERENCES t_drone(id)," +
                "maintenance_type TEXT NOT NULL," +
                "status TEXT NOT NULL DEFAULT 'PENDING'," +
                "scheduled_date TEXT NOT NULL," +
                "completed_date TEXT," +
                "technician TEXT," +
                "parts_replaced TEXT," +
                "cost INTEGER DEFAULT 0," +
                "description TEXT," +
                "next_maintenance_date TEXT," +
                "created_at TEXT NOT NULL," +
                "updated_at TEXT NOT NULL)");
            logger.info("SQLite t_maintenance 表初始化成功");
        } catch (Exception e) {
            logger.error("SQLite t_maintenance 表初始化失败: {}", e.getMessage(), e);
        }

        try (java.sql.Connection conn = dataSource.getConnection();
             java.sql.Statement stmt = conn.createStatement()) {
            stmt.execute("CREATE TABLE IF NOT EXISTS sys_user (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "email TEXT NOT NULL UNIQUE," +
                "password_hash TEXT NOT NULL," +
                "status TEXT NOT NULL DEFAULT 'INACTIVE'," +
                "role TEXT NOT NULL DEFAULT 'user'," +
                "activation_token TEXT," +
                "created_at TEXT NOT NULL DEFAULT (datetime('now','localtime'))," +
                "updated_at TEXT NOT NULL DEFAULT (datetime('now','localtime')))");
            logger.info("SQLite sys_user 表初始化成功");
        } catch (Exception e) {
            logger.error("SQLite sys_user 表初始化失败: {}", e.getMessage(), e);
        }

        try (java.sql.Connection conn = dataSource.getConnection();
             java.sql.Statement stmt = conn.createStatement()) {
            stmt.execute("CREATE TABLE IF NOT EXISTS sys_role (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "role_code TEXT NOT NULL UNIQUE," +
                "role_name TEXT NOT NULL," +
                "description TEXT)");
            logger.info("SQLite sys_role 表初始化成功");
        } catch (Exception e) {
            logger.error("SQLite sys_role 表初始化失败: {}", e.getMessage(), e);
        }

        try (java.sql.Connection conn = dataSource.getConnection();
             java.sql.Statement stmt = conn.createStatement()) {
            stmt.execute("CREATE TABLE IF NOT EXISTS sys_permission (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "perm_code TEXT NOT NULL UNIQUE," +
                "perm_name TEXT NOT NULL," +
                "description TEXT)");
            logger.info("SQLite sys_permission 表初始化成功");
        } catch (Exception e) {
            logger.error("SQLite sys_permission 表初始化失败: {}", e.getMessage(), e);
        }

        try (java.sql.Connection conn = dataSource.getConnection();
             java.sql.Statement stmt = conn.createStatement()) {
            stmt.execute("CREATE TABLE IF NOT EXISTS sys_user_role (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "user_id INTEGER NOT NULL," +
                "role_id INTEGER NOT NULL," +
                "UNIQUE(user_id, role_id))");
            logger.info("SQLite sys_user_role 表初始化成功");
        } catch (Exception e) {
            logger.error("SQLite sys_user_role 表初始化失败: {}", e.getMessage(), e);
        }

        try (java.sql.Connection conn = dataSource.getConnection();
             java.sql.Statement stmt = conn.createStatement()) {
            stmt.execute("CREATE TABLE IF NOT EXISTS sys_role_permission (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "role_id INTEGER NOT NULL," +
                "permission_id INTEGER NOT NULL," +
                "UNIQUE(role_id, permission_id))");
            logger.info("SQLite sys_role_permission 表初始化成功");
        } catch (Exception e) {
            logger.error("SQLite sys_role_permission 表初始化失败: {}", e.getMessage(), e);
        }

        try (java.sql.Connection conn = dataSource.getConnection();
             java.sql.Statement stmt = conn.createStatement()) {
            stmt.execute("CREATE TABLE IF NOT EXISTS sys_login_log (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "user_id INTEGER," +
                "ip TEXT NOT NULL," +
                "device TEXT," +
                "login_time TEXT NOT NULL DEFAULT (datetime('now','localtime'))," +
                "result TEXT NOT NULL," +
                "fail_reason TEXT)");
            logger.info("SQLite sys_login_log 表初始化成功");
        } catch (Exception e) {
            logger.error("SQLite sys_login_log 表初始化失败: {}", e.getMessage(), e);
        }

        try (java.sql.Connection conn = dataSource.getConnection();
             java.sql.Statement stmt = conn.createStatement()) {
            stmt.execute("CREATE TABLE IF NOT EXISTS sys_operation_log (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "user_id INTEGER," +
                "operation TEXT NOT NULL," +
                "target TEXT," +
                "ip TEXT NOT NULL," +
                "op_time TEXT NOT NULL DEFAULT (datetime('now','localtime'))," +
                "result TEXT NOT NULL)");
            logger.info("SQLite sys_operation_log 表初始化成功");
        } catch (Exception e) {
            logger.error("SQLite sys_operation_log 表初始化失败: {}", e.getMessage(), e);
        }

        // 初始化种子数据
        try (java.sql.Connection conn = dataSource.getConnection();
             java.sql.Statement stmt = conn.createStatement()) {
            stmt.execute("INSERT OR IGNORE INTO sys_role (role_code, role_name, description) VALUES " +
                "('admin', '管理员', '系统管理员'), ('user', '普通用户', '普通用户，拥有所有权限')");
            stmt.execute("INSERT OR IGNORE INTO sys_permission (perm_code, perm_name) VALUES " +
                "('drone:list', '查看无人机'), ('drone:create', '创建无人机'), ('drone:update', '更新无人机'), ('drone:delete', '删除无人机')," +
                "('mission:list', '查看飞行任务'), ('mission:create', '创建飞行任务'), ('mission:update', '更新飞行任务'), ('mission:delete', '删除飞行任务')," +
                "('maintenance:list', '查看维护记录'), ('maintenance:create', '创建维护记录'), ('maintenance:update', '更新维护记录'), ('maintenance:delete', '删除维护记录')," +
                "('flightlog:list', '查看飞行日志'), ('flightlog:create', '创建飞行日志'), ('flightlog:delete', '删除飞行日志')");
            stmt.execute("INSERT OR IGNORE INTO sys_role_permission (role_id, permission_id) " +
                "SELECT r.id, p.id FROM sys_role r, sys_permission p WHERE r.role_code = 'admin'");
            stmt.execute("DELETE FROM sys_role_permission WHERE role_id = (SELECT id FROM sys_role WHERE role_code = 'user')");
            stmt.execute("INSERT OR IGNORE INTO sys_role_permission (role_id, permission_id) " +
                "SELECT r.id, p.id FROM sys_role r, sys_permission p WHERE r.role_code = 'user'");
            stmt.execute("INSERT OR IGNORE INTO sys_user_role (user_id, role_id) " +
                "SELECT u.id, r.id FROM sys_user u, sys_role r WHERE u.role = r.role_code AND NOT EXISTS (SELECT 1 FROM sys_user_role ur WHERE ur.user_id = u.id AND ur.role_id = r.id)");
            logger.info("SQLite 种子数据初始化成功");
        } catch (Exception e) {
            logger.warn("SQLite 种子数据初始化失败（可能已存在）: {}", e.getMessage());
        }
    }

    /**
     * 获取数据库类型标识.
     *
     * @return 返回 "sqlite"
     */
    @Override
    public String getDatabaseType() {
        return "sqlite";
    }
}
