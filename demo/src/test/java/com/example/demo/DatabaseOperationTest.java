package com.example.demo;

import com.example.demo.entity.*;
import com.example.demo.repository.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@TestPropertySource(locations = "classpath:application.properties")
class DatabaseOperationTest {

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private HouseAreaRepository houseAreaRepository;
    
    @Autowired
    private DeviceRepository deviceRepository;
    
    @Autowired
    private AutomationRuleRepository automationRuleRepository;

    @Test
    @Transactional
    void testAllEntitiesDatabaseOperations() {
        // 1. 测试用户表操作
        String testUsername = "test_user_" + System.currentTimeMillis();
        String testPhone = "13800138000";
        
        User user = new User();
        user.setUsername(testUsername);
        user.setPassword("encoded_test_password");
        user.setPhone(testPhone);
        user.setCreatedAt(LocalDateTime.now());
        
        User savedUser = userRepository.save(user);
        
        assertThat(savedUser.getId()).isNotNull();
        assertThat(savedUser.getUsername()).isEqualTo(testUsername);
        assertThat(savedUser.getPhone()).isEqualTo(testPhone);
        
        Optional<User> foundUserOpt = userRepository.findById(savedUser.getId());
        assertThat(foundUserOpt).isPresent();
        assertThat(foundUserOpt.get().getUsername()).isEqualTo(testUsername);
        
        // 2. 测试房屋区域表操作
        String areaName = "测试区域_" + System.currentTimeMillis();
        String areaType = "living_room";
        
        HouseArea area = new HouseArea();
        area.setUserId(savedUser.getId()); // 使用上面创建的用户ID
        area.setAreaName(areaName);
        area.setAreaType(areaType);
        area.setCreatedAt(LocalDateTime.now());
        area.setUpdatedAt(LocalDateTime.now());
        
        HouseArea savedArea = houseAreaRepository.save(area);
        
        assertThat(savedArea.getId()).isNotNull();
        assertThat(savedArea.getUserId()).isEqualTo(savedUser.getId());
        assertThat(savedArea.getAreaName()).isEqualTo(areaName);
        assertThat(savedArea.getAreaType()).isEqualTo(areaType);
        
        Optional<HouseArea> foundAreaOpt = houseAreaRepository.findById(savedArea.getId());
        assertThat(foundAreaOpt).isPresent();
        assertThat(foundAreaOpt.get().getAreaName()).isEqualTo(areaName);
        
        // 3. 测试设备表操作
        String deviceId = "device_" + System.currentTimeMillis();
        String deviceType = "light";
        String deviceName = "测试设备";
        
        Device device = new Device();
        device.setUserId(savedUser.getId()); // 使用上面创建的用户ID
        device.setAreaId(savedArea.getId()); // 使用上面创建的区域ID
        device.setDeviceId(deviceId);
        device.setDeviceType(deviceType);
        device.setDeviceName(deviceName);
        device.setCreatedAt(LocalDateTime.now());
        device.setUpdatedAt(LocalDateTime.now());
        
        Device savedDevice = deviceRepository.save(device);
        
        assertThat(savedDevice.getId()).isNotNull();
        assertThat(savedDevice.getUserId()).isEqualTo(savedUser.getId());
        assertThat(savedDevice.getAreaId()).isEqualTo(savedArea.getId());
        assertThat(savedDevice.getDeviceId()).isEqualTo(deviceId);
        assertThat(savedDevice.getDeviceType()).isEqualTo(deviceType);
        assertThat(savedDevice.getDeviceName()).isEqualTo(deviceName);
        
        Optional<Device> foundDeviceOpt = deviceRepository.findById(savedDevice.getId());
        assertThat(foundDeviceOpt).isPresent();
        assertThat(foundDeviceOpt.get().getDeviceId()).isEqualTo(deviceId);
        
        // 验证用户和区域的设备关联
        var userDevices = deviceRepository.findByUserId(savedUser.getId());
        assertThat(userDevices).hasSize(1);
        assertThat(userDevices.get(0).getDeviceId()).isEqualTo(deviceId);
        
        var areaDevices = deviceRepository.findByAreaId(savedArea.getId());
        assertThat(areaDevices).hasSize(1);
        assertThat(areaDevices.get(0).getDeviceId()).isEqualTo(deviceId);
        
        // 4. 测试自动化规则表操作
        String ruleName = "测试规则_" + System.currentTimeMillis();
        String triggerType = "time_based";
        String triggerCondition = "{\"time\":\"22:00\",\"repeat\":\"daily\"}";
        String targetDeviceId = deviceId; // 使用上面创建的设备ID
        String commandType = "turn_off";
        String commandParameters = "{}";
        
        AutomationRule rule = new AutomationRule();
        rule.setUserId(savedUser.getId()); // 使用上面创建的用户ID
        rule.setRuleName(ruleName);
        rule.setTriggerType(triggerType);
        rule.setTriggerCondition(triggerCondition);
        rule.setTargetDeviceId(targetDeviceId);
        rule.setTargetDeviceType(deviceType);
        rule.setCommandType(commandType);
        rule.setCommandParameters(commandParameters);
        rule.setIsEnabled(true);
        rule.setCreatedAt(LocalDateTime.now());
        rule.setUpdatedAt(LocalDateTime.now());
        
        AutomationRule savedRule = automationRuleRepository.save(rule);
        
        assertThat(savedRule.getId()).isNotNull();
        assertThat(savedRule.getUserId()).isEqualTo(savedUser.getId());
        assertThat(savedRule.getRuleName()).isEqualTo(ruleName);
        assertThat(savedRule.getTriggerType()).isEqualTo(triggerType);
        assertThat(savedRule.getTargetDeviceId()).isEqualTo(targetDeviceId);
        assertThat(savedRule.getIsEnabled()).isTrue();
        
        Optional<AutomationRule> foundRuleOpt = automationRuleRepository.findById(savedRule.getId());
        assertThat(foundRuleOpt).isPresent();
        assertThat(foundRuleOpt.get().getRuleName()).isEqualTo(ruleName);
        
        // 验证用户规则关联
        var userRules = automationRuleRepository.findByUserId(savedUser.getId());
        assertThat(userRules).hasSize(1);
        assertThat(userRules.get(0).getRuleName()).isEqualTo(ruleName);
        
        var enabledRules = automationRuleRepository.findByUserIdAndIsEnabled(savedUser.getId(), true);
        assertThat(enabledRules).hasSize(1);
        assertThat(enabledRules.get(0).getRuleName()).isEqualTo(ruleName);
        
        // 5. 更新操作测试
        String updatedPhone = "13900139000";
        savedUser.setPhone(updatedPhone);
        User updatedUser = userRepository.save(savedUser);
        assertThat(updatedUser.getPhone()).isEqualTo(updatedPhone);
        
        String updatedAreaName = "更新区域_" + System.currentTimeMillis();
        savedArea.setAreaName(updatedAreaName);
        HouseArea updatedArea = houseAreaRepository.save(savedArea);
        assertThat(updatedArea.getAreaName()).isEqualTo(updatedAreaName);
        
        String updatedDeviceName = "更新设备";
        savedDevice.setDeviceName(updatedDeviceName);
        Device updatedDevice = deviceRepository.save(savedDevice);
        assertThat(updatedDevice.getDeviceName()).isEqualTo(updatedDeviceName);
        
        String updatedRuleName = "更新规则_" + System.currentTimeMillis();
        savedRule.setRuleName(updatedRuleName);
        AutomationRule updatedRule = automationRuleRepository.save(savedRule);
        assertThat(updatedRule.getRuleName()).isEqualTo(updatedRuleName);
        
        // 验证所有更新操作
        Optional<User> updatedUserOpt = userRepository.findById(savedUser.getId());
        assertThat(updatedUserOpt).isPresent();
        assertThat(updatedUserOpt.get().getPhone()).isEqualTo(updatedPhone);
        
        Optional<HouseArea> updatedAreaOpt = houseAreaRepository.findById(savedArea.getId());
        assertThat(updatedAreaOpt).isPresent();
        assertThat(updatedAreaOpt.get().getAreaName()).isEqualTo(updatedAreaName);
        
        Optional<Device> updatedDeviceOpt = deviceRepository.findById(savedDevice.getId());
        assertThat(updatedDeviceOpt).isPresent();
        assertThat(updatedDeviceOpt.get().getDeviceName()).isEqualTo(updatedDeviceName);
        
        Optional<AutomationRule> updatedRuleOpt = automationRuleRepository.findById(savedRule.getId());
        assertThat(updatedRuleOpt).isPresent();
        assertThat(updatedRuleOpt.get().getRuleName()).isEqualTo(updatedRuleName);
        
        // 注意：由于使用了@Transactional注解，测试结束后事务会回滚，
        // 所以无需手动删除测试数据，数据库将保持原有状态
    }
    
    @Test
    @Transactional
    void testRelationshipsBetweenEntities() {
        // 创建用户
        User user = new User();
        user.setUsername("rel_test_user_" + System.currentTimeMillis());
        user.setPassword("encoded_test_password");
        user.setPhone("13800138000");
        user.setCreatedAt(LocalDateTime.now());
        User savedUser = userRepository.save(user);
        
        // 创建区域
        HouseArea area = new HouseArea();
        area.setUserId(savedUser.getId());
        area.setAreaName("关联测试区域");
        area.setAreaType("bedroom");
        area.setCreatedAt(LocalDateTime.now());
        area.setUpdatedAt(LocalDateTime.now());
        HouseArea savedArea = houseAreaRepository.save(area);
        
        // 创建多个设备并关联到同一区域
        for (int i = 0; i < 3; i++) {
            Device device = new Device();
            device.setUserId(savedUser.getId());
            device.setAreaId(savedArea.getId());
            device.setDeviceId("rel_test_dev_" + i + "_" + System.currentTimeMillis());
            device.setDeviceType("sensor");
            device.setDeviceName("传感器设备" + i);
            device.setCreatedAt(LocalDateTime.now());
            device.setUpdatedAt(LocalDateTime.now());
            deviceRepository.save(device);
        }
        
        // 验证区域下的设备数量
        var areaDevices = deviceRepository.findByAreaId(savedArea.getId());
        assertThat(areaDevices).hasSize(3);
        
        // 验证用户拥有的设备数量
        var userDevices = deviceRepository.findByUserId(savedUser.getId());
        assertThat(userDevices).hasSize(3);
        
        // 创建规则并关联到用户
        for (int i = 0; i < 2; i++) {
            AutomationRule rule = new AutomationRule();
            rule.setUserId(savedUser.getId());
            rule.setRuleName("关联测试规则" + i);
            rule.setTriggerType("time_based");
            rule.setTriggerCondition("{}");
            rule.setTargetDeviceId(areaDevices.get(i).getDeviceId());
            rule.setTargetDeviceType("sensor");
            rule.setCommandType("check_status");
            rule.setCommandParameters("{}");
            rule.setIsEnabled(true);
            rule.setCreatedAt(LocalDateTime.now());
            rule.setUpdatedAt(LocalDateTime.now());
            automationRuleRepository.save(rule);
        }
        
        // 验证用户拥有的规则数量
        var userRules = automationRuleRepository.findByUserId(savedUser.getId());
        assertThat(userRules).hasSize(2);
        
        var enabledUserRules = automationRuleRepository.findByUserIdAndIsEnabled(savedUser.getId(), true);
        assertThat(enabledUserRules).hasSize(2);
    }
}