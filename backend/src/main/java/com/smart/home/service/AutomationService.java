package com.smart.home.service;

import com.smart.home.model.entity.AutomationRule;
import com.smart.home.model.dto.AutomationRuleDTO;

import java.util.List;

/**
 * 自动化规则服务接口
 *
 * @author lingma
 */
public interface AutomationService {

    /**
     * 根据用户ID获取自动化规则列表
     *
     * @param userId 用户ID
     * @return 自动化规则列表
     */
    List<AutomationRule> getRulesByUserId(Long userId);

    /**
     * 根据ID获取自动化规则
     *
     * @param id 规则ID
     * @return 自动化规则实体
     */
    AutomationRule getRuleById(Long id);

    /**
     * 创建自动化规则
     *
     * @param ruleDTO 自动化规则DTO
     * @return 自动化规则实体
     */
    AutomationRule createRule(AutomationRuleDTO ruleDTO);

    /**
     * 更新自动化规则
     *
     * @param id 规则ID
     * @param ruleDTO 自动化规则DTO
     * @return 自动化规则实体
     */
    AutomationRule updateRule(Long id, AutomationRuleDTO ruleDTO);

    /**
     * 删除自动化规则
     *
     * @param id 规则ID
     */
    void deleteRule(Long id);

    /**
     * 启用自动化规则
     *
     * @param id 规则ID
     */
    void enableRule(Long id);

    /**
     * 禁用自动化规则
     *
     * @param id 规则ID
     */
    void disableRule(Long id);

    /**
     * 检查并触发自动化规则
     *
     * @param userId 用户ID
     * @param deviceId 设备ID
     * @param deviceType 设备类型
     * @param status 设备状态
     */
    void checkAndTriggerRules(String userId, String deviceId, String deviceType, Object status);
}