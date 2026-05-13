CREATE TABLE IF NOT EXISTS t_drone (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    serial_number TEXT NOT NULL UNIQUE,
    model_name TEXT NOT NULL,
    manufacturer TEXT NOT NULL,
    purchase_date TEXT NOT NULL,
    status TEXT NOT NULL,
    max_flight_time INTEGER NOT NULL,
    max_flight_distance INTEGER NOT NULL,
    weight INTEGER NOT NULL,
    remarks TEXT,
    created_at TEXT NOT NULL DEFAULT (datetime('now', 'localtime')),
    updated_at TEXT NOT NULL DEFAULT (datetime('now', 'localtime'))
);

CREATE INDEX IF NOT EXISTS idx_serial_number ON t_drone(serial_number);
CREATE INDEX IF NOT EXISTS idx_model_name ON t_drone(model_name);
CREATE INDEX IF NOT EXISTS idx_manufacturer ON t_drone(manufacturer);
CREATE INDEX IF NOT EXISTS idx_status ON t_drone(status);

CREATE TABLE IF NOT EXISTS t_flight_mission (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    mission_code TEXT NOT NULL UNIQUE,
    drone_id INTEGER NOT NULL REFERENCES t_drone(id),
    mission_type TEXT NOT NULL,
    status TEXT NOT NULL DEFAULT 'PENDING',
    pilot_name TEXT,
    planned_start_time TEXT,
    planned_end_time TEXT,
    actual_start_time TEXT,
    actual_end_time TEXT,
    flight_area TEXT,
    remarks TEXT,
    created_at TEXT NOT NULL,
    updated_at TEXT NOT NULL
);

CREATE TABLE IF NOT EXISTS t_flight_log (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    drone_id INTEGER NOT NULL REFERENCES t_drone(id),
    mission_id INTEGER REFERENCES t_flight_mission(id),
    flight_duration INTEGER NOT NULL,
    flight_distance INTEGER,
    max_altitude INTEGER,
    takeoff_location TEXT,
    landing_location TEXT,
    weather_condition TEXT,
    wind_speed INTEGER,
    battery_consumed INTEGER,
    remarks TEXT,
    flight_date TEXT NOT NULL,
    created_at TEXT NOT NULL
);

CREATE TABLE IF NOT EXISTS t_maintenance (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    drone_id INTEGER NOT NULL REFERENCES t_drone(id),
    maintenance_type TEXT NOT NULL,
    status TEXT NOT NULL DEFAULT 'PENDING',
    scheduled_date TEXT NOT NULL,
    completed_date TEXT,
    technician TEXT,
    parts_replaced TEXT,
    cost INTEGER DEFAULT 0,
    description TEXT,
    next_maintenance_date TEXT,
    created_at TEXT NOT NULL,
    updated_at TEXT NOT NULL
);

CREATE TABLE IF NOT EXISTS sys_user (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    email TEXT NOT NULL UNIQUE,
    password_hash TEXT NOT NULL,
    status TEXT NOT NULL DEFAULT 'INACTIVE',
    role TEXT NOT NULL DEFAULT 'user',
    activation_token TEXT,
    created_at TEXT NOT NULL DEFAULT (datetime('now', 'localtime')),
    updated_at TEXT NOT NULL DEFAULT (datetime('now', 'localtime'))
);

CREATE TABLE IF NOT EXISTS sys_role (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    role_code TEXT NOT NULL UNIQUE,
    role_name TEXT NOT NULL,
    description TEXT
);

CREATE TABLE IF NOT EXISTS sys_permission (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    perm_code TEXT NOT NULL UNIQUE,
    perm_name TEXT NOT NULL,
    description TEXT
);

CREATE TABLE IF NOT EXISTS sys_user_role (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    user_id INTEGER NOT NULL,
    role_id INTEGER NOT NULL,
    UNIQUE(user_id, role_id)
);

CREATE TABLE IF NOT EXISTS sys_role_permission (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    role_id INTEGER NOT NULL,
    permission_id INTEGER NOT NULL,
    UNIQUE(role_id, permission_id)
);

CREATE TABLE IF NOT EXISTS sys_login_log (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    user_id INTEGER,
    ip TEXT NOT NULL,
    device TEXT,
    login_time TEXT NOT NULL DEFAULT (datetime('now', 'localtime')),
    result TEXT NOT NULL,
    fail_reason TEXT
);

CREATE TABLE IF NOT EXISTS sys_operation_log (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    user_id INTEGER,
    operation TEXT NOT NULL,
    target TEXT,
    ip TEXT NOT NULL,
    op_time TEXT NOT NULL DEFAULT (datetime('now', 'localtime')),
    result TEXT NOT NULL
);
