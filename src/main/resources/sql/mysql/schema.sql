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

CREATE TABLE IF NOT EXISTS t_flight_log (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    drone_id BIGINT NOT NULL,
    mission_id BIGINT,
    flight_duration INT NOT NULL,
    flight_distance INT,
    max_altitude INT,
    takeoff_location VARCHAR(200),
    landing_location VARCHAR(200),
    weather_condition VARCHAR(100),
    wind_speed INT,
    battery_consumed INT,
    remarks TEXT,
    flight_date VARCHAR(50) NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (drone_id) REFERENCES t_drone(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS t_maintenance (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    drone_id BIGINT NOT NULL,
    maintenance_type VARCHAR(50) NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'PENDING',
    scheduled_date VARCHAR(50) NOT NULL,
    completed_date VARCHAR(50),
    technician VARCHAR(100),
    parts_replaced TEXT,
    cost INT DEFAULT 0,
    description TEXT,
    next_maintenance_date VARCHAR(50),
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (drone_id) REFERENCES t_drone(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS sys_user (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(200) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'INACTIVE',
    role VARCHAR(50) NOT NULL DEFAULT 'user',
    activation_token VARCHAR(64),
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS sys_role (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    role_code VARCHAR(50) NOT NULL UNIQUE,
    role_name VARCHAR(100) NOT NULL,
    description VARCHAR(255)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS sys_permission (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    perm_code VARCHAR(100) NOT NULL UNIQUE,
    perm_name VARCHAR(100) NOT NULL,
    description VARCHAR(255)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS sys_user_role (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    UNIQUE KEY uk_user_role (user_id, role_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS sys_role_permission (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    role_id BIGINT NOT NULL,
    permission_id BIGINT NOT NULL,
    UNIQUE KEY uk_role_perm (role_id, permission_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS sys_login_log (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT,
    ip VARCHAR(50) NOT NULL,
    device VARCHAR(255),
    login_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    result VARCHAR(20) NOT NULL,
    fail_reason VARCHAR(255)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS sys_operation_log (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT,
    operation VARCHAR(100) NOT NULL,
    target VARCHAR(200),
    ip VARCHAR(50) NOT NULL,
    op_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    result VARCHAR(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
