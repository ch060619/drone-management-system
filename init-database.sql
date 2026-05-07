-- ============================================
-- 无人机管理系统 — 数据库初始化脚本
-- 适用于 MySQL 8.x
-- ============================================

CREATE DATABASE IF NOT EXISTS drone_management
  DEFAULT CHARACTER SET utf8mb4
  DEFAULT COLLATE utf8mb4_unicode_ci;

USE drone_management;

-- 1. 无人机信息表
CREATE TABLE IF NOT EXISTS t_drone (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    serial_number VARCHAR(100) NOT NULL UNIQUE,
    model_name VARCHAR(200) NOT NULL,
    manufacturer VARCHAR(200) NOT NULL,
    purchase_date VARCHAR(50) NOT NULL,
    status VARCHAR(50) NOT NULL,
    max_flight_time INT NOT NULL,
    max_flight_distance INT NOT NULL,
    weight INT NOT NULL,
    remarks TEXT,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 2. 飞行任务表
CREATE TABLE IF NOT EXISTS t_flight_mission (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    mission_code VARCHAR(100) NOT NULL UNIQUE,
    drone_id BIGINT NOT NULL,
    mission_type VARCHAR(50) NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'PENDING',
    pilot_name VARCHAR(100),
    planned_start_time VARCHAR(50),
    planned_end_time VARCHAR(50),
    actual_start_time VARCHAR(50),
    actual_end_time VARCHAR(50),
    flight_area VARCHAR(200),
    remarks TEXT,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (drone_id) REFERENCES t_drone(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================
-- 认证授权系统 — 7张表
-- ============================================

-- 5. 用户表
CREATE TABLE IF NOT EXISTS sys_user (
    id               BIGINT AUTO_INCREMENT PRIMARY KEY,
    email            VARCHAR(200) NOT NULL UNIQUE,
    password_hash    VARCHAR(255) NOT NULL,
    status           VARCHAR(20) NOT NULL DEFAULT 'INACTIVE' COMMENT 'ACTIVE/INACTIVE/LOCKED',
    role             VARCHAR(50) NOT NULL DEFAULT 'user' COMMENT 'admin/user',
    activation_token VARCHAR(64) NULL,
    created_at       DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at       DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 6. 角色表
CREATE TABLE IF NOT EXISTS sys_role (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    role_code   VARCHAR(50) NOT NULL UNIQUE,
    role_name   VARCHAR(100) NOT NULL,
    description VARCHAR(255) NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 7. 权限表
CREATE TABLE IF NOT EXISTS sys_permission (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    perm_code   VARCHAR(100) NOT NULL UNIQUE,
    perm_name   VARCHAR(100) NOT NULL,
    description VARCHAR(255) NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 8. 用户角色关联表
CREATE TABLE IF NOT EXISTS sys_user_role (
    id      BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    UNIQUE KEY uk_user_role (user_id, role_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 9. 角色权限关联表
CREATE TABLE IF NOT EXISTS sys_role_permission (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    role_id       BIGINT NOT NULL,
    permission_id BIGINT NOT NULL,
    UNIQUE KEY uk_role_perm (role_id, permission_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 10. 登录日志表
CREATE TABLE IF NOT EXISTS sys_login_log (
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id    BIGINT NULL,
    ip         VARCHAR(50) NOT NULL,
    device     VARCHAR(255) NULL,
    login_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    result     VARCHAR(20) NOT NULL,
    fail_reason VARCHAR(255) NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 11. 操作日志表
CREATE TABLE IF NOT EXISTS sys_operation_log (
    id        BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id   BIGINT NULL,
    operation VARCHAR(100) NOT NULL,
    target    VARCHAR(200) NULL,
    ip        VARCHAR(50) NOT NULL,
    op_time   DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    result    VARCHAR(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 初始化角色数据
INSERT IGNORE INTO sys_role (role_code, role_name, description) VALUES ('admin', '管理员', '系统管理员，拥有所有权限');
INSERT IGNORE INTO sys_role (role_code, role_name, description) VALUES ('user', '普通用户', '普通用户，拥有所有权限');

-- 初始化权限数据
INSERT IGNORE INTO sys_permission (perm_code, perm_name, description) VALUES
('drone:list', '查看无人机', '分页查询无人机列表'),
('drone:create', '创建无人机', '新增无人机记录'),
('drone:update', '更新无人机', '修改无人机信息'),
('drone:delete', '删除无人机', '删除无人机记录'),
('mission:list', '查看飞行任务', '分页查询飞行任务列表'),
('mission:create', '创建飞行任务', '新增飞行任务记录'),
('mission:update', '更新飞行任务', '修改飞行任务信息'),
('mission:delete', '删除飞行任务', '删除飞行任务记录'),
('maintenance:list', '查看维护记录', '分页查询维护记录列表'),
('maintenance:create', '创建维护记录', '新增维护记录'),
('maintenance:update', '更新维护记录', '修改维护记录信息'),
('maintenance:delete', '删除维护记录', '删除维护记录'),
('flightlog:list', '查看飞行日志', '分页查询飞行日志列表'),
('flightlog:create', '创建飞行日志', '新增飞行日志记录'),
('flightlog:delete', '删除飞行日志', '删除飞行日志记录');

-- 为 admin 角色分配所有权限
INSERT IGNORE INTO sys_role_permission (role_id, permission_id)
SELECT (SELECT id FROM sys_role WHERE role_code = 'admin'), id FROM sys_permission;

-- 为 user 角色分配所有权限
INSERT IGNORE INTO sys_role_permission (role_id, permission_id)
SELECT (SELECT id FROM sys_role WHERE role_code = 'user'), id FROM sys_permission;
