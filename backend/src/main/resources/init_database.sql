-- 智能家居系统数据库初始化脚本
-- 包含用户表、房屋区域表、设备表、用户设备归属表、连接状态表、自动化规则表和设备状态表

-- 创建用户表
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    password VARCHAR(255) NOT NULL COMMENT '加密密码',
    phone VARCHAR(20) COMMENT '手机号',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    last_login_at DATETIME COMMENT '最后登录时间'
) COMMENT '用户表';

-- 创建设备表
CREATE TABLE IF NOT EXISTS devices (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    device_id VARCHAR(100) NOT NULL UNIQUE COMMENT '设备唯一ID',
    device_type VARCHAR(50) NOT NULL COMMENT '设备类型',
    device_name VARCHAR(100) NOT NULL COMMENT '设备名称',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) COMMENT '设备表';

-- 创建用户设备归属表
CREATE TABLE IF NOT EXISTS user_device_ownership (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL COMMENT '用户ID',
    device_id VARCHAR(100) NOT NULL COMMENT '设备ID',
    is_owner TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否为设备所有者',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (device_id) REFERENCES devices(device_id) ON DELETE CASCADE,
    INDEX idx_user_id (user_id),
    INDEX idx_device_id (device_id)
) COMMENT '用户设备归属表';

-- 创建房屋区域表
CREATE TABLE IF NOT EXISTS house_areas (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL COMMENT '创建者用户ID',
    area_name VARCHAR(50) NOT NULL COMMENT '区域名称',
    area_type VARCHAR(20) NOT NULL DEFAULT 'general' COMMENT '区域类型：fixed-固定区域，general-通用区域',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) COMMENT '房屋区域表';

-- 创建连接状态表
CREATE TABLE IF NOT EXISTS connection_status (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    device_id VARCHAR(100) NOT NULL COMMENT '设备ID',
    user_id VARCHAR(100) NOT NULL COMMENT '用户ID',
    connection_status VARCHAR(20) NOT NULL DEFAULT 'offline' COMMENT '连接状态：online-在线，offline-离线',
    connection_time DATETIME COMMENT '连接时间',
    disconnection_time DATETIME COMMENT '断开连接时间',
    last_heartbeat DATETIME COMMENT '最后心跳时间',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_device_id (device_id),
    INDEX idx_user_id (user_id)
) COMMENT '连接状态表';

-- 创建自动化规则表
CREATE TABLE IF NOT EXISTS automation_rules (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL COMMENT '创建者用户ID',
    rule_name VARCHAR(100) NOT NULL COMMENT '规则名称',
    trigger_type VARCHAR(30) NOT NULL COMMENT '触发类型：device_status-设备状态，time_based-定时',
    trigger_condition JSON COMMENT '触发条件',
    target_device_id VARCHAR(100) COMMENT '目标设备ID',
    target_device_type VARCHAR(50) COMMENT '目标设备类型',
    command_type VARCHAR(50) COMMENT '命令类型',
    command_parameters JSON COMMENT '命令参数',
    is_enabled TINYINT(1) NOT NULL DEFAULT 1 COMMENT '是否启用',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) COMMENT '自动化规则表';

-- 创建设备状态表
CREATE TABLE IF NOT EXISTS device_status (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    device_id VARCHAR(100) NOT NULL COMMENT '设备ID',
    status_data JSON COMMENT '状态数据（JSON格式存储）',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (device_id) REFERENCES devices(device_id) ON DELETE CASCADE,
    INDEX idx_device_id (device_id)
) COMMENT '设备状态表';