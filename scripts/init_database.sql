-- 智能家居控制系统数据库初始化脚本
-- 创建数据库（如果不存在）
CREATE DATABASE IF NOT EXISTS home_furnishings 
CHARACTER SET utf8mb4 
COLLATE utf8mb4_unicode_ci;

-- 使用数据库
USE home_furnishings;

-- 用户表
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '用户唯一标识',
    username VARCHAR(50) NOT NULL COMMENT '用户名',
    password VARCHAR(255) NOT NULL COMMENT '密码（加密存储）',
    phone VARCHAR(20) COMMENT '手机号',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    last_login_at DATETIME COMMENT '最后上线时间或最新使用时间'
) COMMENT='用户表';

-- 房屋区域表
CREATE TABLE IF NOT EXISTS house_areas (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '区域唯一标识',
    user_id BIGINT NOT NULL COMMENT '用户ID（外键，关联用户表）',
    area_name VARCHAR(50) NOT NULL COMMENT '区域名称（客厅、卧室、厨房、卫生间、阳台、通用区域等）',
    area_type VARCHAR(20) NOT NULL COMMENT '区域类型（fixed - 固定区域，general - 通用区域）',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) COMMENT='房屋区域表';

-- 设备表
CREATE TABLE IF NOT EXISTS devices (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '设备唯一标识',
    user_id BIGINT NOT NULL COMMENT '用户ID（外键，关联用户表）',
    area_id BIGINT NOT NULL COMMENT '区域ID（外键，关联房屋区域表）',
    device_id VARCHAR(100) NOT NULL COMMENT '设备唯一ID（由下位机提供）',
    device_type VARCHAR(50) NOT NULL COMMENT '设备类型（temperature_sensor, humidity_sensor, light_sensor, led, curtain等）',
    device_name VARCHAR(100) COMMENT '设备名称',
    status_name VARCHAR(100) COMMENT '状态名称（用于前端显示框架）',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (area_id) REFERENCES house_areas(id) ON DELETE CASCADE
) COMMENT='设备表';

-- 连接状态表
CREATE TABLE IF NOT EXISTS connection_status (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '记录唯一标识',
    device_id VARCHAR(100) NOT NULL COMMENT '设备ID',
    user_id VARCHAR(100) NOT NULL COMMENT '用户ID',
    connection_status VARCHAR(20) NOT NULL COMMENT '连接状态（connected, disconnected）',
    connection_time DATETIME COMMENT '连接时间戳',
    disconnection_time DATETIME COMMENT '断开连接时间戳',
    last_heartbeat DATETIME COMMENT '最后心跳时间',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) COMMENT='连接状态表';

-- 自动化规则表
CREATE TABLE IF NOT EXISTS automation_rules (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '规则唯一标识',
    user_id BIGINT NOT NULL COMMENT '用户ID（外键，关联用户表）',
    rule_name VARCHAR(100) NOT NULL COMMENT '规则名称',
    trigger_type VARCHAR(30) NOT NULL COMMENT '触发类型（device_status - 设备状态，time_based - 定时）',
    trigger_condition JSON COMMENT '触发条件（JSON格式存储）',
    target_device_id VARCHAR(100) COMMENT '目标设备ID（外键，关联设备表）',
    target_device_type VARCHAR(50) COMMENT '目标设备类型',
    command_type VARCHAR(50) COMMENT '命令类型',
    command_parameters JSON COMMENT '命令参数（JSON格式存储）',
    is_enabled TINYINT(1) DEFAULT 1 COMMENT '规则是否启用',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) COMMENT='自动化规则表';

-- 创建索引以提高查询性能
CREATE INDEX idx_users_phone ON users(phone);
CREATE INDEX idx_house_areas_user_id ON house_areas(user_id);
CREATE INDEX idx_devices_user_id ON devices(user_id);
CREATE INDEX idx_devices_area_id ON devices(area_id);
CREATE INDEX idx_devices_device_id ON devices(device_id);
CREATE INDEX idx_connection_status_device_id ON connection_status(device_id);
CREATE INDEX idx_automation_rules_user_id ON automation_rules(user_id);
CREATE INDEX idx_automation_rules_is_enabled ON automation_rules(is_enabled);

-- 插入默认的区域类型数据
-- INSERT INTO house_areas (user_id, area_name, area_type, created_at, updated_at) 
-- VALUES 
-- (1, '客厅', 'fixed', NOW(), NOW()),
-- (1, '卧室', 'fixed', NOW(), NOW()),
-- (1, '厨房', 'fixed', NOW(), NOW()),
-- (1, '卫生间', 'fixed', NOW(), NOW()),
-- (1, '阳台', 'fixed', NOW(), NOW()),
-- (1, '通用区域', 'general', NOW(), NOW());

-- 插入默认的设备类型（作为参考）
-- INSERT INTO devices (user_id, area_id, device_id, device_type, device_name, status_name, created_at, updated_at)
-- VALUES 
-- (1, 1, 'temp_sensor_001', 'temperature_sensor', '客厅温度传感器', '温度传感器', NOW(), NOW()),
-- (1, 1, 'led_001', 'led', '客厅LED灯', 'LED灯', NOW(), NOW());