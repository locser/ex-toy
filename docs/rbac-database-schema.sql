-- =====================================================
-- RBAC Database Schema for Toy Application
-- =====================================================

-- Bảng Roles - Lưu trữ các vai trò trong hệ thống
CREATE TABLE roles (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE COMMENT 'Tên vai trò (VD: ADMIN, USER, CREATOR)',
    description VARCHAR(255) COMMENT 'Mô tả vai trò',
    is_active BOOLEAN NOT NULL DEFAULT TRUE COMMENT 'Trạng thái hoạt động của vai trò',
    is_system_role BOOLEAN NOT NULL DEFAULT FALSE COMMENT 'Vai trò hệ thống không thể xóa',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Thời gian tạo',
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Thời gian cập nhật',
    INDEX idx_roles_name (name),
    INDEX idx_roles_active (is_active)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Bảng vai trò RBAC';

-- Bảng Permissions - Lưu trữ các quyền trong hệ thống
CREATE TABLE permissions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE COMMENT 'Tên quyền (VD: READ_toy, create_toy)',
    description VARCHAR(255) COMMENT 'Mô tả quyền',
    resource VARCHAR(50) NOT NULL COMMENT 'Tài nguyên (VD: TOY, USER, EVENT)',
    action VARCHAR(50) NOT NULL COMMENT 'Hành động (VD: READ, create, update, delete)',
    is_active BOOLEAN NOT NULL DEFAULT TRUE COMMENT 'Trạng thái hoạt động của quyền',
    is_system_permission BOOLEAN NOT NULL DEFAULT FALSE COMMENT 'Quyền hệ thống không thể xóa',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Thời gian tạo',
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Thời gian cập nhật',
    INDEX idx_permissions_name (name),
    INDEX idx_permissions_resource (resource),
    INDEX idx_permissions_action (action),
    INDEX idx_permissions_active (is_active),
    INDEX idx_permissions_resource_action (resource, action)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Bảng quyền RBAC';

-- Bảng User_Roles - Mapping giữa User và Role (Many-to-Many)
CREATE TABLE user_roles (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL COMMENT 'ID người dùng',
    role_id BIGINT NOT NULL COMMENT 'ID vai trò',
    assigned_by BIGINT NOT NULL COMMENT 'ID người gán vai trò',
    is_active BOOLEAN NOT NULL DEFAULT TRUE COMMENT 'Trạng thái hoạt động của gán vai trò',
    expires_at DATETIME NULL COMMENT 'Thời gian hết hạn vai trò (NULL = vĩnh viễn)',
    assigned_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Thời gian gán vai trò',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Thời gian tạo',
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Thời gian cập nhật',
    UNIQUE KEY uk_user_role (user_id, role_id),
    INDEX idx_user_roles_user_id (user_id),
    INDEX idx_user_roles_role_id (role_id),
    INDEX idx_user_roles_assigned_by (assigned_by),
    INDEX idx_user_roles_active (is_active),
    INDEX idx_user_roles_expires_at (expires_at),
    INDEX idx_user_roles_user_active (user_id, is_active)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Bảng mapping User-Role';

-- Bảng Role_Permissions - Mapping giữa Role và Permission (Many-to-Many)
CREATE TABLE role_permissions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    role_id BIGINT NOT NULL COMMENT 'ID vai trò',
    permission_id BIGINT NOT NULL COMMENT 'ID quyền',
    assigned_by BIGINT NOT NULL COMMENT 'ID người gán quyền',
    is_active BOOLEAN NOT NULL DEFAULT TRUE COMMENT 'Trạng thái hoạt động của gán quyền',
    assigned_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Thời gian gán quyền',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Thời gian tạo',
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Thời gian cập nhật',
    UNIQUE KEY uk_role_permission (role_id, permission_id),
    INDEX idx_role_permissions_role_id (role_id),
    INDEX idx_role_permissions_permission_id (permission_id),
    INDEX idx_role_permissions_assigned_by (assigned_by),
    INDEX idx_role_permissions_active (is_active),
    INDEX idx_role_permissions_role_active (role_id, is_active)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Bảng mapping Role-Permission';

-- =====================================================
-- DROP FOREIGN KEY CONSTRAINTS (if they exist in your database)
-- =====================================================

-- Check existing foreign keys first:
-- SELECT CONSTRAINT_NAME, TABLE_NAME, REFERENCED_TABLE_NAME
-- FROM information_schema.KEY_COLUMN_USAGE
-- WHERE REFERENCED_TABLE_SCHEMA = 'your_database_name'
-- AND TABLE_NAME IN ('user_roles', 'role_permissions');

-- Drop foreign key constraints from user_roles table
ALTER TABLE user_roles DROP FOREIGN KEY IF EXISTS user_roles_ibfk_1;
ALTER TABLE user_roles DROP FOREIGN KEY IF EXISTS user_roles_ibfk_2;
ALTER TABLE user_roles DROP FOREIGN KEY IF EXISTS user_roles_ibfk_3;
ALTER TABLE user_roles DROP FOREIGN KEY IF EXISTS fk_user_roles_user_id;
ALTER TABLE user_roles DROP FOREIGN KEY IF EXISTS fk_user_roles_role_id;
ALTER TABLE user_roles DROP FOREIGN KEY IF EXISTS fk_user_roles_assigned_by;

-- Drop foreign key constraints from role_permissions table
ALTER TABLE role_permissions DROP FOREIGN KEY IF EXISTS role_permissions_ibfk_1;
ALTER TABLE role_permissions DROP FOREIGN KEY IF EXISTS role_permissions_ibfk_2;
ALTER TABLE role_permissions DROP FOREIGN KEY IF EXISTS role_permissions_ibfk_3;
ALTER TABLE role_permissions DROP FOREIGN KEY IF EXISTS fk_role_permissions_role_id;
ALTER TABLE role_permissions DROP FOREIGN KEY IF EXISTS fk_role_permissions_permission_id;
ALTER TABLE role_permissions DROP FOREIGN KEY IF EXISTS fk_role_permissions_assigned_by;

-- =====================================================
-- DROP STATEMENTS (for cleanup if needed)
-- =====================================================

-- Drop tables in correct order
-- DROP TABLE IF EXISTS user_roles;
-- DROP TABLE IF EXISTS role_permissions;
-- DROP TABLE IF EXISTS permissions;
-- DROP TABLE IF EXISTS roles;

-- -- Drop views
-- DROP VIEW IF EXISTS user_permissions_view;
-- DROP VIEW IF EXISTS user_roles_view;

-- -- Drop stored procedures and functions
-- DROP PROCEDURE IF EXISTS AssignRoleToUser;
-- DROP FUNCTION IF EXISTS HasPermission;

-- =====================================================
-- Dữ liệu mẫu cho RBAC
-- =====================================================

-- Insert Permissions
INSERT INTO permissions (name, description, resource, action, is_system_permission) VALUES
-- Toy permissions
('READ_TOY', 'Đọc thông tin đồ chơi', 'TOY', 'READ', TRUE),
('CREATE_TOY', 'Tạo đồ chơi mới', 'TOY', 'CREATE', TRUE),
('UPDATE_TOY', 'Cập nhật thông tin đồ chơi', 'TOY', 'UPDATE', TRUE),
('DELETE_TOY', 'Xóa đồ chơi', 'TOY', 'DELETE', TRUE),
('MANAGE_TOY', 'Quản lý toàn bộ đồ chơi', 'TOY', 'MANAGE', TRUE),

-- User permissions
('READ_USER', 'Đọc thông tin người dùng', 'USER', 'READ', TRUE),
('UPDATE_USER', 'Cập nhật thông tin người dùng', 'USER', 'UPDATE', TRUE),
('DELETE_USER', 'Xóa người dùng', 'USER', 'DELETE', TRUE),
('MANAGE_USER', 'Quản lý toàn bộ người dùng', 'USER', 'MANAGE', TRUE),

-- Event permissions
('READ_EVENT', 'Đọc thông tin sự kiện', 'EVENT', 'READ', TRUE),
('CREATE_EVENT', 'Tạo sự kiện mới', 'EVENT', 'CREATE', TRUE),
('UPDATE_EVENT', 'Cập nhật sự kiện', 'EVENT', 'UPDATE', TRUE),
('DELETE_EVENT', 'Xóa sự kiện', 'EVENT', 'DELETE', TRUE),
('MANAGE_EVENT', 'Quản lý toàn bộ sự kiện', 'EVENT', 'MANAGE', TRUE),

-- Giveaway permissions
('READ_GIVEAWAY', 'Đọc thông tin giveaway', 'GIVEAWAY', 'READ', TRUE),
('CREATE_GIVEAWAY', 'Tạo giveaway mới', 'GIVEAWAY', 'CREATE', TRUE),
('UPDATE_GIVEAWAY', 'Cập nhật giveaway', 'GIVEAWAY', 'UPDATE', TRUE),
('DELETE_GIVEAWAY', 'Xóa giveaway', 'GIVEAWAY', 'DELETE', TRUE),
('MANAGE_GIVEAWAY', 'Quản lý toàn bộ giveaway', 'GIVEAWAY', 'MANAGE', TRUE),
('PARTICIPATE_GIVEAWAY', 'Tham gia giveaway', 'GIVEAWAY', 'PARTICIPATE', TRUE),

-- System permissions
('ADMIN_ACCESS', 'Truy cập trang quản trị', 'SYSTEM', 'ADMIN', TRUE),
('VIEW_REPORTS', 'Xem báo cáo', 'SYSTEM', 'REPORT', TRUE),
('EXPORT_DATA', 'Xuất dữ liệu', 'SYSTEM', 'EXPORT', TRUE),
('MANAGE_ROLES', 'Quản lý vai trò', 'SYSTEM', 'ROLE_MANAGE', TRUE),
('ASSIGN_PERMISSIONS', 'Gán quyền', 'SYSTEM', 'PERMISSION_ASSIGN', TRUE);

-- Insert Roles
INSERT INTO roles (name, description, is_system_role) VALUES
('ADMIN', 'Quản trị viên hệ thống - có toàn quyền', TRUE),
('MODERATOR', 'Người kiểm duyệt - quản lý nội dung', TRUE),
('CREATOR', 'Người tạo nội dung - có thể tạo và quản lý đồ chơi, sự kiện', TRUE),
('USER', 'Người dùng thông thường - chỉ có quyền cơ bản', TRUE),
('GUEST', 'Khách - chỉ có quyền xem', TRUE);

-- Gán quyền cho vai trò ADMIN (có tất cả quyền)
INSERT INTO role_permissions (role_id, permission_id, assigned_by)
SELECT
    (SELECT id FROM roles WHERE name = 'ADMIN'),
    p.id,
    1 -- Assume user ID 1 is system admin
FROM permissions p;

-- Gán quyền cho vai trò MODERATOR
INSERT INTO role_permissions (role_id, permission_id, assigned_by)
SELECT
    (SELECT id FROM roles WHERE name = 'MODERATOR'),
    p.id,
    1
FROM permissions p
WHERE p.name IN (
    'READ_TOY', 'UPDATE_TOY', 'DELETE_TOY', 'MANAGE_TOY',
    'READ_USER', 'UPDATE_USER',
    'READ_EVENT', 'UPDATE_EVENT', 'DELETE_EVENT', 'MANAGE_EVENT',
    'READ_GIVEAWAY', 'UPDATE_GIVEAWAY', 'DELETE_GIVEAWAY', 'MANAGE_GIVEAWAY',
    'VIEW_REPORTS'
);

-- Gán quyền cho vai trò CREATOR
INSERT INTO role_permissions (role_id, permission_id, assigned_by)
SELECT
    (SELECT id FROM roles WHERE name = 'CREATOR'),
    p.id,
    1
FROM permissions p
WHERE p.name IN (
    'READ_TOY', 'CREATE_TOY', 'UPDATE_TOY',
    'READ_USER',
    'READ_EVENT', 'CREATE_EVENT', 'UPDATE_EVENT',
    'READ_GIVEAWAY', 'CREATE_GIVEAWAY', 'UPDATE_GIVEAWAY', 'PARTICIPATE_GIVEAWAY'
);

-- Gán quyền cho vai trò USER
INSERT INTO role_permissions (role_id, permission_id, assigned_by)
SELECT
    (SELECT id FROM roles WHERE name = 'USER'),
    p.id,
    1
FROM permissions p
WHERE p.name IN (
    'READ_TOY',
    'READ_USER',
    'READ_EVENT',
    'READ_GIVEAWAY', 'PARTICIPATE_GIVEAWAY'
);

-- Gán quyền cho vai trò GUEST
INSERT INTO role_permissions (role_id, permission_id, assigned_by)
SELECT
    (SELECT id FROM roles WHERE name = 'GUEST'),
    p.id,
    1
FROM permissions p
WHERE p.name IN (
    'READ_TOY',
    'READ_EVENT',
    'READ_GIVEAWAY'
);

-- =====================================================
-- Các View hữu ích cho RBAC
-- =====================================================

-- View để xem tất cả quyền của user
CREATE VIEW user_permissions_view AS
SELECT
    u.id as user_id,
    u.email,
    u.name as user_name,
    r.name as role_name,
    p.name as permission_name,
    p.resource,
    p.action,
    ur.is_active as role_active,
    ur.expires_at,
    rp.is_active as permission_active
FROM users u
JOIN user_roles ur ON u.id = ur.user_id
JOIN roles r ON ur.role_id = r.id
JOIN role_permissions rp ON r.id = rp.role_id
JOIN permissions p ON rp.permission_id = p.id
WHERE ur.is_active = TRUE
  AND r.is_active = TRUE
  AND rp.is_active = TRUE
  AND p.is_active = TRUE
  AND (ur.expires_at IS NULL OR ur.expires_at > NOW());

-- View để xem roles của user
CREATE VIEW user_roles_view AS
SELECT
    u.id as user_id,
    u.email,
    u.name as user_name,
    r.id as role_id,
    r.name as role_name,
    r.description as role_description,
    ur.assigned_at,
    ur.expires_at,
    ur.is_active
FROM users u
JOIN user_roles ur ON u.id = ur.user_id
JOIN roles r ON ur.role_id = r.id
WHERE ur.is_active = TRUE
  AND r.is_active = TRUE
  AND (ur.expires_at IS NULL OR ur.expires_at > NOW());

-- =====================================================
-- Stored Procedures hữu ích
-- =====================================================

DELIMITER //

-- Procedure để gán role cho user
CREATE PROCEDURE AssignRoleToUser(
    IN p_user_id BIGINT,
    IN p_role_name VARCHAR(50),
    IN p_assigned_by BIGINT,
    IN p_expires_at DATETIME
)
BEGIN
    DECLARE v_role_id BIGINT;
    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        ROLLBACK;
        RESIGNAL;
    END;

    START TRANSACTION;

    -- Lấy role_id
    SELECT id INTO v_role_id FROM roles WHERE name = p_role_name AND is_active = TRUE;

    IF v_role_id IS NULL THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Role not found or inactive';
    END IF;

    -- Insert hoặc update user_role
    INSERT INTO user_roles (user_id, role_id, assigned_by, expires_at)
    VALUES (p_user_id, v_role_id, p_assigned_by, p_expires_at)
    ON DUPLICATE KEY UPDATE
        is_active = TRUE,
        assigned_by = p_assigned_by,
        expires_at = p_expires_at,
        assigned_at = NOW(),
        updated_at = NOW();

    COMMIT;
END //

-- Function để kiểm tra user có permission không
CREATE FUNCTION HasPermission(
    p_user_id BIGINT,
    p_permission_name VARCHAR(100)
) RETURNS BOOLEAN
READS SQL DATA
DETERMINISTIC
BEGIN
    DECLARE v_count INT DEFAULT 0;

    SELECT COUNT(*) INTO v_count
    FROM user_permissions_view
    WHERE user_id = p_user_id
      AND permission_name = p_permission_name;

    RETURN v_count > 0;
END //

DELIMITER ;

-- =====================================================
-- Indexes để tối ưu performance
-- =====================================================

-- Composite indexes cho các truy vấn thường dùng
CREATE INDEX idx_user_roles_user_role_active ON user_roles(user_id, role_id, is_active);
CREATE INDEX idx_role_permissions_role_permission_active ON role_permissions(role_id, permission_id, is_active);
CREATE INDEX idx_permissions_resource_action_active ON permissions(resource, action, is_active);

-- =====================================================
-- Comments và Documentation
-- =====================================================

/*
RBAC Schema Documentation:

1. Bảng roles: Lưu trữ các vai trò trong hệ thống
   - Mỗi role có thể có nhiều permissions
   - is_system_role = TRUE nghĩa là không thể xóa

2. Bảng permissions: Lưu trữ các quyền cụ thể
   - Format naming: ACTION_RESOURCE (VD: READ_TOY, CREATE_USER)
   - resource: Loại tài nguyên (TOY, USER, EVENT, etc.)
   - action: Hành động (READ, CREATE, UPDATE, DELETE, MANAGE)

3. Bảng user_roles: Mapping User ↔ Role
   - Hỗ trợ expires_at để tạo temporary roles
   - assigned_by để audit ai gán role

4. Bảng role_permissions: Mapping Role ↔ Permission
   - assigned_by để audit ai gán permission cho role

Performance Notes:
- Sử dụng composite indexes cho các truy vấn thường dùng
- View user_permissions_view để query nhanh permissions của user
- Function HasPermission() để check permission trong stored procedures

Security Notes:
- Tất cả mapping tables đều có is_active flag
- Foreign key constraints để đảm bảo data integrity
- Audit trail với assigned_by và timestamps
*/