package com.smart.home.service;

import com.smart.home.model.entity.AutomationRule;

import java.util.List;

/**
 * 自动化服务接口
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
    List<AutomationRule> getAutomationRulesByUserId(Long userId);

    /**
     * 根据ID获取自动化规则
     *
     * @param id 规则ID
     * @return 自动化规则实体
     */
    AutomationRule getAutomationRuleById(Long id);

    /**
     * 添加自动化规则
     *
     * @param rule 自动化规则实体
     * @return 自动化规则实体
     */
    AutomationRule addAutomationRule(AutomationRule rule);

    /**
     * 更新自动化规则
     *
     * @param rule 自动化规则实体
     * @return 自动化规则实体
     */
    AutomationRule updateAutomationRule(AutomationRule rule);

    /**
     * 删除自动化规则
     *
     * @param id 规则ID
     */
    void deleteAutomationRule(Long id);

    /**
     * 启用自动化规则
     *
     * @param id 规则ID
     * @return 更新后的规则
     */
    AutomationRule enableAutomationRule(Long id);

    /**
     * 禁用自动化规则
     *
     * @param id 规则ID
     * @return 更新后的规则
     */
    AutomationRule disableAutomationRule(Long id);

    /**
     * 检查并触发自动化规则
     *
     * @param userId 用户ID
     * @param deviceId 设备ID
     * @param deviceType 设备类型
     * @param status 设备状态
     */
    void checkAndTriggerRules(String userId, String deviceId, String deviceType, Object status);

    /**
     * 批量启用自动化规则
     *
     * @param ids 规则ID列表
     */
    void batchEnableRules(List<Long> ids);

    /**
     * 批量禁用自动化规则
     *
     * @param ids 规则ID列表
     */
    void batchDisableRules(List<Long> ids);

    /**
     * 检查设备是否支持自动化触发
     *
     * @param deviceType 设备类型
     * @return 是否支持
     */
    boolean isDeviceSupportAutomation(String deviceType);
    
    /**
     * 获取所有启用的定时规则
     * 
     * @return 定时规则列表
     */
    List<AutomationRule> getAllScheduledRules();
}