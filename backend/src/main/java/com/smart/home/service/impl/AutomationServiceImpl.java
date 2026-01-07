package com.smart.home.service.impl;

import com.smart.home.model.entity.AutomationRule;
import com.smart.home.service.AutomationService;
import com.smart.home.mapper.AutomationRuleMapper;
import com.smart.home.service.MqttService;
import com.smart.home.model.dto.DeviceDataDTO;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

/**
 * 自动化服务实现类
 *
 * @author lingma
 */
@Service
public class AutomationServiceImpl implements AutomationService {

    @Autowired
    private AutomationRuleMapper automationRuleMapper;

    @Autowired
    private MqttService mqttService;

    /**
     * 根据用户ID获取自动化规则列表
     *
     * @param userId 用户ID
     * @return 自动化规则列表
     */
    @Override
    public List<AutomationRule> getAutomationRulesByUserId(Long userId) {
        return automationRuleMapper.selectByUserId(userId);
    }

    /**
     * 根据ID获取自动化规则
     *
     * @param id 规则ID
     * @return 自动化规则实体
     */
    @Override
    public AutomationRule getAutomationRuleById(Long id) {
        return automationRuleMapper.selectById(id);
    }

    /**
     * 添加自动化规则
     *
     * @param rule 自动化规则实体
     * @return 自动化规则实体
     */
    @Override
    @Transactional
    public AutomationRule addAutomationRule(AutomationRule rule) {
        rule.setCreatedAt(LocalDateTime.now());
        rule.setUpdatedAt(LocalDateTime.now());
        rule.setIsEnabled(true); // 默认启用
        automationRuleMapper.insert(rule);
        return rule;
    }

    /**
     * 更新自动化规则
     *
     * @param rule 自动化规则实体
     * @return 自动化规则实体
     */
    @Override
    @Transactional
    public AutomationRule updateAutomationRule(AutomationRule rule) {
        rule.setUpdatedAt(LocalDateTime.now());
        automationRuleMapper.updateById(rule);
        return rule;
    }

    /**
     * 删除自动化规则
     *
     * @param id 规则ID
     */
    @Override
    @Transactional
    public void deleteAutomationRule(Long id) {
        automationRuleMapper.deleteById(id);
    }

    /**
     * 启用自动化规则
     *
     * @param id 规则ID
     * @return 更新后的规则
     */
    @Override
    @Transactional
    public AutomationRule enableAutomationRule(Long id) {
        AutomationRule rule = automationRuleMapper.selectById(id);
        if (rule != null) {
            rule.setIsEnabled(true);
            rule.setUpdatedAt(LocalDateTime.now());
            automationRuleMapper.updateById(rule);
        }
        return rule;
    }

    /**
     * 禁用自动化规则
     *
     * @param id 规则ID
     * @return 更新后的规则
     */
    @Override
    @Transactional
    public AutomationRule disableAutomationRule(Long id) {
        AutomationRule rule = automationRuleMapper.selectById(id);
        if (rule != null) {
            rule.setIsEnabled(false);
            rule.setUpdatedAt(LocalDateTime.now());
            automationRuleMapper.updateById(rule);
        }
        return rule;
    }

    /**
     * 检查并触发自动化规则
     *
     * @param userId 用户ID
     * @param deviceId 设备ID
     * @param deviceType 设备类型
     * @param status 设备状态
     */
    @Override
    public void checkAndTriggerRules(String userId, String deviceId, String deviceType, Object status) {
        // 获取用户启用的自动化规则
        List<AutomationRule> rules = automationRuleMapper.selectEnabledByUserId(Long.parseLong(userId));
        
        for (AutomationRule rule : rules) {
            // 只检查状态触发类型的规则
            if ("device_status".equals(rule.getTriggerType())) {
                // 解析触发条件
                JSONObject triggerCondition = JSON.parseObject(rule.getTriggerCondition());
                
                // 检查是否是目标设备触发
                String triggerDeviceId = triggerCondition.getString("deviceId");
                String triggerDeviceType = triggerCondition.getString("deviceType");
                
                // 检查设备ID或设备类型是否匹配
                if ((triggerDeviceId != null && triggerDeviceId.equals(deviceId)) || 
                    (triggerDeviceType != null && triggerDeviceType.equals(deviceType))) {
                    
                    // 检查触发条件
                    if (checkTriggerCondition(triggerCondition, status)) {
                        // 触发执行命令
                        executeRuleCommand(rule, userId);
                    }
                }
            }
        }
    }

    /**
     * 检查触发条件是否满足
     *
     * @param triggerCondition 触发条件
     * @param status 设备状态
     * @return 是否满足条件
     */
    private boolean checkTriggerCondition(JSONObject triggerCondition, Object status) {
        String conditionType = triggerCondition.getString("conditionType"); // 比较类型：>, <, =, >=, <=, between等
        String conditionField = triggerCondition.getString("field"); // 比较字段，如temperature, humidity等
        Object conditionValue = triggerCondition.get("value"); // 比较值
        Object conditionValue2 = triggerCondition.get("value2"); // 比较值2（用于between等范围条件）
        
        // 解析设备状态
        JSONObject statusObj = null;
        if (status instanceof String) {
            statusObj = JSON.parseObject((String) status);
        } else {
            statusObj = JSON.parseObject(JSON.toJSONString(status));
        }
        
        // 获取要比较的值
        Object actualValue = statusObj.get(conditionField);
        
        if (actualValue == null || conditionValue == null) {
            return false;
        }
        
        // 类型转换为数值进行比较
        double actualNum = convertToNumber(actualValue);
        double conditionNum = convertToNumber(conditionValue);
        
        switch (conditionType) {
            case ">":
                return actualNum > conditionNum;
            case "<":
                return actualNum < conditionNum;
            case "=":
            case "==":
                return actualNum == conditionNum;
            case ">=":
                return actualNum >= conditionNum;
            case "<=":
                return actualNum <= conditionNum;
            case "between":
                if (conditionValue2 != null) {
                    double conditionNum2 = convertToNumber(conditionValue2);
                    return actualNum >= Math.min(conditionNum, conditionNum2) && 
                           actualNum <= Math.max(conditionNum, conditionNum2);
                }
                return false;
            default:
                return false;
        }
    }

    /**
     * 将对象转换为数值
     *
     * @param obj 要转换的对象
     * @return 数值
     */
    private double convertToNumber(Object obj) {
        if (obj instanceof Number) {
            return ((Number) obj).doubleValue();
        } else if (obj instanceof String) {
            try {
                return Double.parseDouble((String) obj);
            } catch (NumberFormatException e) {
                return 0.0;
            }
        } else {
            return 0.0;
        }
    }

    /**
     * 执行规则命令
     *
     * @param rule 规则
     * @param userId 用户ID
     */
    private void executeRuleCommand(AutomationRule rule, String userId) {
        // 构建控制命令
        DeviceDataDTO deviceDataDTO = new DeviceDataDTO();
        deviceDataDTO.setUserId(userId);
        
        DeviceDataDTO.Data data = new DeviceDataDTO.Data();
        data.setDeviceId(rule.getTargetDeviceId());
        data.setDeviceType(rule.getTargetDeviceType());
        
        DeviceDataDTO.Command command = new DeviceDataDTO.Command();
        command.setType(rule.getCommandType());
        
        // 解析命令参数
        JSONObject commandParams = JSON.parseObject(rule.getCommandParameters());
        command.setParameters(commandParams);
        
        data.setCommand(command);
        deviceDataDTO.setData(data);
        
        // 发送控制命令
        mqttService.sendControlCommand(deviceDataDTO);
    }

    /**
     * 批量启用自动化规则
     *
     * @param ids 规则ID列表
     */
    @Override
    @Transactional
    public void batchEnableRules(List<Long> ids) {
        for (Long id : ids) {
            enableAutomationRule(id);
        }
    }

    /**
     * 批量禁用自动化规则
     *
     * @param ids 规则ID列表
     */
    @Override
    @Transactional
    public void batchDisableRules(List<Long> ids) {
        for (Long id : ids) {
            disableAutomationRule(id);
        }
    }

    /**
     * 检查设备是否支持自动化触发
     *
     * @param deviceType 设备类型
     * @return 是否支持
     */
    @Override
    public boolean isDeviceSupportAutomation(String deviceType) {
        // 定义支持自动化的设备类型列表
        String[] supportedTypes = {
            "temperature_sensor", 
            "humidity_sensor", 
            "light_sensor", 
            "motion_sensor", 
            "door_sensor",
            "window_sensor",
            "smoke_detector",
            "led",
            "air_conditioner",
            "curtain",
            "fan",
            "heater"
        };
        
        for (String type : supportedTypes) {
            if (type.equals(deviceType)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 获取所有启用的定时规则
     *
     * @return 定时规则列表
     */
    @Override
    public List<AutomationRule> getAllScheduledRules() {
        return automationRuleMapper.selectScheduledRules();
    }
}