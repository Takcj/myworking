# 智能家居控制系统 - 数据库操作文档

## 1. 数据库架构概述

智能家居控制系统使用 MySQL 8.0 作为主要数据存储，Redis 7 作为缓存服务。MySQL 负责持久化存储用户信息、设备信息、区域信息和自动化规则等核心数据，Redis 用于缓存热点数据和会话信息。

## 2. 数据库连接配置

### 2.1 连接参数
```properties
# MySQL Database Configuration (Docker)
spring.datasource.url=jdbc:mysql://localhost:3306/smart_home?useSSL=false&serverTimezone=UTC&characterEncoding=utf8&allowPublicKeyRetrieval=true
spring.datasource.username=user
spring.datasource.password=password
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# HikariCP Connection Pool Configuration
spring.datasource.hikari.connection-timeout=20000
spring.datasource.hikari.validation-timeout=5000
spring.datasource.hikari.maximum-pool-size=5
spring.datasource.hikari.minimum-idle=2
spring.datasource.hikari.connection-test-query=SELECT 1
spring.datasource.hikari.initialization-fail-timeout=1
```

### 2.2 JPA 配置
```properties
# 解决数据库驱动信息显示为undefined/unknown的问题
spring.jpa.properties.hibernate.temp.use_jdbc_metadata_defaults=false

# JPA/Hibernate配置
spring.jpa.database-platform=org.hibernate.dialect.MySQL8Dialect
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.show-sql=true
```

## 3. 数据表设计

### 3.1 用户表 (users)

| 字段名 | 类型 | 约束 | 说明 |
|--------|------|------|------|
| id | BIGINT | PRIMARY KEY, AUTO_INCREMENT | 用户ID |
| username | VARCHAR(50) | NOT NULL, UNIQUE | 用户名 |
| password | VARCHAR(255) | NOT NULL | 加密密码 |
| phone | VARCHAR(20) | | 手机号 |
| created_at | DATETIME | | 创建时间 |
| last_login_at | DATETIME | | 最后登录时间 |

```sql
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    phone VARCHAR(20),
    created_at DATETIME,
    last_login_at DATETIME
);
```

### 3.2 房屋区域表 (house_areas)

| 字段名 | 类型 | 约束 | 说明 |
|--------|------|------|------|
| id | BIGINT | PRIMARY KEY, AUTO_INCREMENT | 区域ID |
| user_id | BIGINT | NOT NULL, FOREIGN KEY | 用户ID |
| area_name | VARCHAR(50) | NOT NULL | 区域名称 |
| area_type | VARCHAR(20) | | 区域类型 |
| created_at | DATETIME | | 创建时间 |
| updated_at | DATETIME | | 更新时间 |

```sql
CREATE TABLE house_areas (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    area_name VARCHAR(50) NOT NULL,
    area_type VARCHAR(20),
    created_at DATETIME,
    updated_at DATETIME,
    FOREIGN KEY (user_id) REFERENCES users(id)
);
```

### 3.3 设备表 (devices)

| 字段名 | 类型 | 约束 | 说明 |
|--------|------|------|------|
| id | BIGINT | PRIMARY KEY, AUTO_INCREMENT | 设备ID |
| user_id | BIGINT | NOT NULL, FOREIGN KEY | 用户ID |
| area_id | BIGINT | FOREIGN KEY | 区域ID |
| device_id | VARCHAR(100) | NOT NULL, UNIQUE | 设备唯一ID |
| device_type | VARCHAR(50) | NOT NULL | 设备类型 |
| device_name | VARCHAR(100) | NOT NULL | 设备名称 |
| status_name | VARCHAR(100) | | 状态名称 |
| created_at | DATETIME | | 创建时间 |
| updated_at | DATETIME | | 更新时间 |

```sql
CREATE TABLE devices (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    area_id BIGINT,
    device_id VARCHAR(100) NOT NULL UNIQUE,
    device_type VARCHAR(50) NOT NULL,
    device_name VARCHAR(100) NOT NULL,
    status_name VARCHAR(100),
    created_at DATETIME,
    updated_at DATETIME,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (area_id) REFERENCES house_areas(id)
);
```

### 3.4 自动化规则表 (automation_rules)

| 字段名 | 类型 | 约束 | 说明 |
|--------|------|------|------|
| id | BIGINT | PRIMARY KEY, AUTO_INCREMENT | 规则ID |
| user_id | BIGINT | NOT NULL, FOREIGN KEY | 用户ID |
| rule_name | VARCHAR(100) | NOT NULL | 规则名称 |
| trigger_type | VARCHAR(30) | NOT NULL | 触发类型 |
| trigger_condition | JSON | | 触发条件 |
| target_device_id | VARCHAR(100) | | 目标设备ID |
| target_device_type | VARCHAR(50) | | 目标设备类型 |
| command_type | VARCHAR(50) | | 命令类型 |
| command_parameters | JSON | | 命令参数 |
| is_enabled | TINYINT(1) | DEFAULT 1 | 是否启用 |
| created_at | DATETIME | | 创建时间 |
| updated_at | DATETIME | | 更新时间 |

```sql
CREATE TABLE automation_rules (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    rule_name VARCHAR(100) NOT NULL,
    trigger_type VARCHAR(30) NOT NULL,
    trigger_condition JSON,
    target_device_id VARCHAR(100),
    target_device_type VARCHAR(50),
    command_type VARCHAR(50),
    command_parameters JSON,
    is_enabled TINYINT(1) DEFAULT 1,
    created_at DATETIME,
    updated_at DATETIME,
    FOREIGN KEY (user_id) REFERENCES users(id)
);
```

## 4. 数据访问层实现

### 4.1 JPA Repository 实现

系统使用 Spring Data JPA 提供基础的 CRUD 操作：

```java
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
}

public interface HouseAreaRepository extends JpaRepository<HouseArea, Long> {
    List<HouseArea> findByUserId(Long userId);
}

public interface DeviceRepository extends JpaRepository<Device, Long> {
    List<Device> findByUserId(Long userId);
    List<Device> findByAreaId(Long areaId);
}

public interface AutomationRuleRepository extends JpaRepository<AutomationRule, Long> {
    List<AutomationRule> findByUserId(Long userId);
    List<AutomationRule> findByUserIdAndIsEnabled(Long userId, Boolean isEnabled);
}
```

### 4.2 MyBatis Mapper 实现

对于复杂查询，使用 MyBatis 提供自定义 SQL：

```xml
<!-- DeviceMapper.xml -->
<mapper namespace="com.example.demo.mapper.DeviceMapper">
    <select id="findDevicesWithStatus" resultType="Device">
        SELECT * FROM devices 
        WHERE user_id = #{userId} 
        AND status_name IS NOT NULL
    </select>
    
    <select id="getDeviceStatsByUser" resultType="DeviceStats">
        SELECT 
            COUNT(*) as totalDevices,
            COUNT(CASE WHEN status_name = 'online' THEN 1 END) as onlineDevices
        FROM devices 
        WHERE user_id = #{userId}
    </select>
</mapper>
```

## 5. 数据库操作流程

### 5.1 用户注册流程

1. 检查用户名是否已存在
2. 对密码进行 BCrypt 加密
3. 保存用户信息到数据库
4. 返回用户信息（不含密码）

### 5.2 设备添加流程

1. 验证用户权限
2. 检查设备ID是否已存在
3. 保存设备信息到数据库
4. 更新区域设备关联
5. 返回设备信息

### 5.3 自动化规则执行流程

1. 查询所有启用的自动化规则
2. 检查规则触发条件
3. 如果条件满足，执行相应命令
4. 记录执行日志

## 6. 数据库性能优化

### 6.1 索引优化

- 在经常查询的字段上创建索引，如 `users.username`、`devices.user_id` 等
- 为复合查询创建复合索引

```sql
-- 用户名索引
CREATE INDEX idx_users_username ON users(username);

-- 设备用户ID和区域ID索引
CREATE INDEX idx_devices_user_area ON devices(user_id, area_id);

-- 自动化规则用户ID和启用状态索引
CREATE INDEX idx_rules_user_enabled ON automation_rules(user_id, is_enabled);
```

### 6.2 缓存策略

- 使用 Redis 缓存热点数据，如用户信息、设备状态等
- 设置合理的缓存过期时间
- 使用缓存预热策略

### 6.3 连接池配置

- 最大连接池大小：5
- 最小空闲连接：2
- 连接超时时间：20秒
- 验证超时时间：5秒

## 7. 数据库安全

### 7.1 访问控制

- 使用专用数据库用户，限制权限
- 不使用 root 用户连接数据库
- 限制数据库远程访问

### 7.2 数据保护

- 敏感数据加密存储（如密码）
- 使用预编译语句防止 SQL 注入
- 定期备份数据库

## 8. 数据库监控

### 8.1 健康检查

- 通过 `DatabaseHealthIndicator` 检查数据库连接状态
- 定期执行 `SELECT 1` 验证连接有效性

### 8.2 性能监控

- 监控慢查询日志
- 跟踪数据库连接数
- 监控查询执行时间