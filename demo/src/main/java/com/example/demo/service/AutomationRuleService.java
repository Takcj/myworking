package com.example.demo.service;

import com.example.demo.entity.AutomationRule;
import java.util.List;
import java.util.Optional;

public interface AutomationRuleService {
    List<AutomationRule> getAllRules();
    List<AutomationRule> getRulesByUserId(Long userId);
    List<AutomationRule> getEnabledRulesByUserId(Long userId);
    Optional<AutomationRule> getRuleById(Long id);
    AutomationRule createRule(AutomationRule rule);
    AutomationRule updateRule(AutomationRule rule);
    void deleteRule(Long id);
    AutomationRule toggleRuleStatus(Long id);
}