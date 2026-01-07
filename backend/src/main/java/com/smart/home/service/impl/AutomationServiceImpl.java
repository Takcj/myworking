package com.smart.home.service.impl;

import com.smart.home.model.entity.AutomationRule;
import com.smart.home.model.dto.AutomationRuleDTO;
import com.smart.home.service.AutomationService;
import com.smart.home.mapper.AutomationRuleMapper;
import com.smart.home.service.ConnectionService;
import com.smart.home.service.DeviceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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
    private ConnectionService connectionService;
    
    @Autowired
    private DeviceService deviceService;

    /**
     * 根据用户ID获取自动化规则列表
     *
     * @param userId 用户ID
     * @return 自动化规则列表
     */
    @Override
    public List<AutomationRule> getRulesByUserId(Long userId) {
        return automationRuleMapper.selectList(
            new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<AutomationRule>()
                .eq("user_id", userId)
        );
    }

    /**
     * 根据ID获取自动化规则
     *
     * @param id 规则ID
     * @return 自动化规则实体
     */
    @Override
    public AutomationRule getRuleById(Long id) {
        return automationRuleMapper.selectById(id);
    }

    /**
     * 创建自动化规则
     *
     * @param ruleDTO 自动化规则DTO
     * @return 自动化规则实体
     */
    @Override
    public AutomationRule createRule(AutomationRuleDTO ruleDTO) {
        AutomationRule rule = new AutomationRule();
        rule.setUserId(ruleDTO.getUserId());
        rule.setRuleName(ruleDTO.getRuleName());
        rule.setTriggerType(ruleDTO.getTriggerType());
        rule.setTriggerCondition(ruleDTO.getTriggerCondition());
        rule.setTargetDeviceId(ruleDTO.getTargetDeviceId());
        rule.setTargetDeviceType(ruleDTO.getTargetDeviceType());
        rule.setCommandType(ruleDTO.getCommandType());
        rule.setCommandParameters(ruleDTO.getCommandParameters());
        rule.setEnabled(ruleDTO.isEnabled());
        
        automationRuleMapper.insert(rule);
        return rule;
    }

    /**
     * 更新自动化规则
     *
     * @param id      规则ID
     * @param ruleDTO 自动化规则DTO
     * @return 自动化规则实体
     */
    @Override
    public AutomationRule updateRule(Long id, AutomationRuleDTO ruleDTO) {
        AutomationRule rule = automationRuleMapper.selectById(id);
        if (rule != null) {
            rule.setRuleName(ruleDTO.getRuleName());
            rule.setTriggerType(ruleDTO.getTriggerType());
            rule.setTriggerCondition(ruleDTO.getTriggerCondition());
            rule.setTargetDeviceId(ruleDTO.getTargetDeviceId());
            rule.setTargetDeviceType(ruleDTO.getTargetDeviceType());
            rule.setCommandType(ruleDTO.getCommandType());
            rule.setCommandParameters(ruleDTO.getCommandParameters());
            rule.setEnabled(ruleDTO.isEnabled());
            
            automationRuleMapper.updateById(rule);
        }
        return rule;
    }

    /**
     * 删除自动化规则
     *
     * @param id 规则ID
     */
    @Override
    public void deleteRule(Long id) {
        automationRuleMapper.deleteById(id);
    }

    /**
     * 启用自动化规则
     *
     * @param id 规则ID
     */
    @Override
    public void enableRule(Long id) {
        automationRuleMapper.updateRuleStatus(id, 1);
    }

    /**
     * 禁用自动化规则
     *
     * @param id 规则ID
     */
    @Override
    public void disableRule(Long id) {
        automationRuleMapper.updateRuleStatus(id, 0);
    }

    /**
     * 检查并触发自动化规则
     *
     * @param userId     用户ID
     * @param deviceId   设备ID
     * @param deviceType 设备类型
     * @param status     设备状态
     */
    @Override
    public void checkAndTriggerRules(String userId, String deviceId, String deviceType, Object status) {
        // 获取用户的所有启用的自动化规则
        List<AutomationRule> enabledRules = automationRuleMapper.selectEnabledRulesByUserId(Long.parseLong(userId));
        
        for (AutomationRule rule : enabledRules) {
            // 检查规则触发条件是否满足
            if (shouldTriggerRule(rule, deviceId, deviceType, status)) {
                // 检查目标设备是否在线
                if (rule.getTargetDeviceId() != null && connectionService.isDeviceOnline(rule.getTargetDeviceId())) {
                    // 执行相应的控制命令
                    executeRuleAction(rule);
                } else {
                    // 目标设备不在线，记录日志
                    System.out.println("目标设备 " + rule.getTargetDeviceId() + " 不在线，无法执行自动化规则");
                }
            }
        }
    }

    /**
     * 检查规则是否应该被触发
     */
    private boolean shouldTriggerRule(AutomationRule rule, String deviceId, String deviceType, Object status) {
        // 这里应该根据规则的触发条件来判断
        // 简化实现：检查设备类型和状态是否匹配规则条件
        // 实际应用中需要更复杂的条件匹配逻辑
        
        // 示例：如果规则的触发设备类型与当前设备类型匹配，则认为满足条件
        return rule.getTriggerType().equals(deviceType);
    }

    /**
     * 执行规则动作
     */
    private void executeRuleAction(AutomationRule rule) {
        // 执行规则对应的控制命令
        // 这里需要根据规则的命令类型和参数来构造控制命令
        System.out.println("执行自动化规则: " + rule.getRuleName());
    }
}