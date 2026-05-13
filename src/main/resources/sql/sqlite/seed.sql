INSERT OR IGNORE INTO sys_role (role_code, role_name, description) VALUES
('admin', 'Administrator', 'System administrator'),
('user', 'User', 'Standard user');

INSERT OR IGNORE INTO sys_permission (perm_code, perm_name, description) VALUES
('drone:list', 'View drones', 'List drones'),
('drone:create', 'Create drone', 'Create a drone'),
('drone:update', 'Update drone', 'Update a drone'),
('drone:delete', 'Delete drone', 'Delete a drone'),
('mission:list', 'View missions', 'List missions'),
('mission:create', 'Create mission', 'Create a mission'),
('mission:update', 'Update mission', 'Update a mission'),
('mission:delete', 'Delete mission', 'Delete a mission'),
('maintenance:list', 'View maintenance', 'List maintenance records'),
('maintenance:create', 'Create maintenance', 'Create a maintenance record'),
('maintenance:update', 'Update maintenance', 'Update a maintenance record'),
('maintenance:delete', 'Delete maintenance', 'Delete a maintenance record'),
('flightlog:list', 'View flight logs', 'List flight logs'),
('flightlog:create', 'Create flight log', 'Create a flight log'),
('flightlog:delete', 'Delete flight log', 'Delete a flight log');

INSERT OR IGNORE INTO sys_role_permission (role_id, permission_id)
SELECT r.id, p.id
FROM sys_role r, sys_permission p
WHERE r.role_code = 'admin';

DELETE FROM sys_role_permission
WHERE role_id = (SELECT id FROM sys_role WHERE role_code = 'user');

INSERT OR IGNORE INTO sys_role_permission (role_id, permission_id)
SELECT r.id, p.id
FROM sys_role r, sys_permission p
WHERE r.role_code = 'user';

INSERT OR IGNORE INTO sys_user_role (user_id, role_id)
SELECT u.id, r.id
FROM sys_user u, sys_role r
WHERE u.role = r.role_code
  AND NOT EXISTS (
      SELECT 1
      FROM sys_user_role ur
      WHERE ur.user_id = u.id
        AND ur.role_id = r.id
  );
