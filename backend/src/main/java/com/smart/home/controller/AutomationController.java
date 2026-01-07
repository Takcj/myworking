package com.smart.home.controller;

import com.smart.home.common.Result;
import com.smart.home.model.entity.AutomationRule;
import com.smart.home.model.dto.AutomationRuleDTO;
import com.smart.home.service.AutomationService;
import com.smart.home.service.DeviceService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 自动化规则控制器
 *
 * @author lingma
 */
@RestController
@RequestMapping("/api/v1/automation")
public class AutomationController {

    @Autowired
    private AutomationService automationService;

    @Autowired
    private DeviceService deviceService;

    /**
     * 获取用户自动化规则列表
     *
     * @param userId 用户ID（从认证上下文获取）
     * @return 规则列表
     */
    @GetMapping("/rules")
    public Result<List<AutomationRule>> getAutomationRules(@RequestParam(required = false) Long userId) {
        if (userId == null) {
            return Result.error("用户ID不能为空");
        }

        List<AutomationRule> rules = automationService.getAutomationRulesByUserId(userId);
        return Result.success("获取自动化规则列表成功", rules);
    }

    /**
     * 获取特定自动化规则
     *
     * @param id 规则ID
     * @param userId 用户ID（从认证上下文获取）
     * @return 规则信息
     */
    @GetMapping("/rules/{id}")
    public Result<AutomationRule> getAutomationRule(@PathVariable Long id, @RequestParam(required = false) Long userId) {
        if (userId == null) {
            return Result.error("用户ID不能为空");
        }

        AutomationRule rule = automationService.getAutomationRuleById(id);
        if (rule != null) {
            if (!rule.getUserId().equals(userId)) {
                return Result.error("没有访问此规则的权限");
            }
            return Result.success("获取自动化规则成功", rule);
        }
        return Result.error("规则不存在");
    }

    /**
     * 创建自动化规则
     *
     * @param ruleDTO 规则DTO
     * @param userId 用户ID（从认证上下文获取）
     * @return 创建结果
     */
    @PostMapping("/rules")
    public Result<AutomationRule> createAutomationRule(@RequestBody AutomationRuleDTO ruleDTO, @RequestParam(required = false) Long userId) {
        if (userId == null) {
            return Result.error("用户ID不能为空");
        }

        // 验证触发设备是否支持自动化
        if ("device_status".equals(ruleDTO.getTriggerType())) {
            String triggerDeviceType = getDeviceTypeFromCondition(ruleDTO.getTriggerCondition());
            if (!automationService.isDeviceSupportAutomation(triggerDeviceType)) {
                return Result.error("触发设备类型不支持自动化触发");
            }
        }

        // 验证目标设备是否存在
        if (!deviceService.hasDevicePermission(userId, ruleDTO.getTargetDeviceId())) {
            return Result.error("目标设备不存在或没有访问权限");
        }

        AutomationRule rule = new AutomationRule();
        BeanUtils.copyProperties(ruleDTO, rule);
        rule.setUserId(userId);

        AutomationRule newRule = automationService.addAutomationRule(rule);
        return Result.success("自动化规则创建成功", newRule);
    }

    /**
     * 从触发条件中获取设备类型
     *
     * @param triggerCondition 触发条件JSON字符串
     * @return 设备类型
     */
    private String getDeviceTypeFromCondition(String triggerCondition) {
        try {
            com.alibaba.fastjson.JSONObject obj = com.alibaba.fastjson.JSONObject.parseObject(triggerCondition);
            return obj.getString("deviceType");
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 更新自动化规则
     *
     * @param id 规则ID
     * @param ruleDTO 规则DTO
     * @param userId 用户ID（从认证上下文获取）
     * @return 更新结果
     */
    @PutMapping("/rules/{id}")
    public Result<AutomationRule> updateAutomationRule(@PathVariable Long id, @RequestBody AutomationRuleDTO ruleDTO, @RequestParam(required = false) Long userId) {
        if (userId == null) {
            return Result.error("用户ID不能为空");
        }

        AutomationRule existingRule = automationService.getAutomationRuleById(id);
        if (existingRule == null) {
            return Result.error("规则不存在");
        }

        if (!existingRule.getUserId().equals(userId)) {
            return Result.error("没有更新此规则的权限");
        }

        // 验证触发设备是否支持自动化
        if ("device_status".equals(ruleDTO.getTriggerType())) {
            String triggerDeviceType = getDeviceTypeFromCondition(ruleDTO.getTriggerCondition());
            if (!automationService.isDeviceSupportAutomation(triggerDeviceType)) {
                return Result.error("触发设备类型不支持自动化触发");
            }
        }

        // 验证目标设备是否存在
        if (!deviceService.hasDevicePermission(userId, ruleDTO.getTargetDeviceId())) {
            return Result.error("目标设备不存在或没有访问权限");
        }

        AutomationRule rule = new AutomationRule();
        BeanUtils.copyProperties(ruleDTO, rule);
        rule.setId(id);
        rule.setUserId(userId);

        AutomationRule updatedRule = automationService.updateAutomationRule(rule);
        return Result.success("自动化规则更新成功", updatedRule);
    }

    /**
     * 删除自动化规则
     *
     * @param id 规则ID
     * @param userId 用户ID（从认证上下文获取）
     * @return 删除结果
     */
    @DeleteMapping("/rules/{id}")
    public Result<String> deleteAutomationRule(@PathVariable Long id, @RequestParam(required = false) Long userId) {
        if (userId == null) {
            return Result.error("用户ID不能为空");
        }

        AutomationRule rule = automationService.getAutomationRuleById(id);
        if (rule == null) {
            return Result.error("规则不存在");
        }

        if (!rule.getUserId().equals(userId)) {
            return Result.error("没有删除此规则的权限");
        }

        automationService.deleteAutomationRule(id);
        return Result.success("自动化规则删除成功", null);
    }

    /**
     * 启用自动化规则
     *
     * @param id 规则ID
     * @param userId 用户ID（从认证上下文获取）
     * @return 启用结果
     */
    @PutMapping("/rules/{id}/enable")
    public Result<AutomationRule> enableAutomationRule(@PathVariable Long id, @RequestParam(required = false) Long userId) {
        if (userId == null) {
            return Result.error("用户ID不能为空");
        }

        AutomationRule rule = automationService.getAutomationRuleById(id);
        if (rule == null) {
            return Result.error("规则不存在");
        }

        if (!rule.getUserId().equals(userId)) {
            return Result.error("没有操作此规则的权限");
        }

        AutomationRule enabledRule = automationService.enableAutomationRule(id);
        return Result.success("自动化规则已启用", enabledRule);
    }

    /**
     * 禁用自动化规则
     *
     * @param id 规则ID
     * @param userId 用户ID（从认证上下文获取）
     * @return 禁用结果
     */
    @PutMapping("/rules/{id}/disable")
    public Result<AutomationRule> disableAutomationRule(@PathVariable Long id, @RequestParam(required = false) Long userId) {
        if (userId == null) {
            return Result.error("用户ID不能为空");
        }

        AutomationRule rule = automationService.getAutomationRuleById(id);
        if (rule == null) {
            return Result.error("规则不存在");
        }

        if (!rule.getUserId().equals(userId)) {
            return Result.error("没有操作此规则的权限");
        }

        AutomationRule disabledRule = automationService.disableAutomationRule(id);
        return Result.success("自动化规则已禁用", disabledRule);
    }

    /**
     * 批量启用自动化规则
     *
     * @param ids 规则ID列表
     * @param userId 用户ID（从认证上下文获取）
     * @return 批量启用结果
     */
    @PutMapping("/rules/batch-enable")
    public Result<String> batchEnableRules(@RequestBody List<Long> ids, @RequestParam(required = false) Long userId) {
        if (userId == null) {
            return Result.error("用户ID不能为空");
        }

        // 验证所有规则是否属于当前用户
        for (Long id : ids) {
            AutomationRule rule = automationService.getAutomationRuleById(id);
            if (rule == null || !rule.getUserId().equals(userId)) {
                return Result.error("存在不属于当前用户的规则");
            }
        }

        automationService.batchEnableRules(ids);
        return Result.success("批量启用自动化规则成功", null);
    }

    /**
     * 批量禁用自动化规则
     *
     * @param ids 规则ID列表
     * @param userId 用户ID（从认证上下文获取）
     * @return 批量禁用结果
     */
    @PutMapping("/rules/batch-disable")
    public Result<String> batchDisableRules(@RequestBody List<Long> ids, @RequestParam(required = false) Long userId) {
        if (userId == null) {
            return Result.error("用户ID不能为空");
        }

        // 验证所有规则是否属于当前用户
        for (Long id : ids) {
            AutomationRule rule = automationService.getAutomationRuleById(id);
            if (rule == null || !rule.getUserId().equals(userId)) {
                return Result.error("存在不属于当前用户的规则");
            }
        }

        automationService.batchDisableRules(ids);
        return Result.success("批量禁用自动化规则成功", null);
    }

    /**
     * 检查设备是否支持自动化
     *
     * @param deviceType 设备类型
     * @return 检查结果
     */
    @GetMapping("/check-device-support")
    public Result<Boolean> checkDeviceSupport(@RequestParam String deviceType) {
        boolean isSupport = automationService.isDeviceSupportAutomation(deviceType);
        return Result.success("设备支持状态查询成功", isSupport);
    }
}