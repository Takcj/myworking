package com.example.demo.service.impl;

import com.example.demo.entity.AutomationRule;
import com.example.demo.repository.AutomationRuleRepository;
import com.example.demo.service.AutomationRuleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class AutomationRuleServiceImpl implements AutomationRuleService {

    @Autowired
    private AutomationRuleRepository automationRuleRepository;

    @Override
    public List<AutomationRule> getAllRules() {
        return automationRuleRepository.findAll();
    }

    @Override
    public List<AutomationRule> getRulesByUserId(Long userId) {
        return automationRuleRepository.findByUserId(userId);
    }

    @Override
    public List<AutomationRule> getEnabledRulesByUserId(Long userId) {
        return automationRuleRepository.findByUserIdAndIsEnabled(userId, true);
    }

    @Override
    public Optional<AutomationRule> getRuleById(Long id) {
        return automationRuleRepository.findById(id);
    }

    @Override
    public AutomationRule createRule(AutomationRule rule) {
        rule.setCreatedAt(LocalDateTime.now());
        rule.setUpdatedAt(LocalDateTime.now());
        return automationRuleRepository.save(rule);
    }

    @Override
    public AutomationRule updateRule(AutomationRule rule) {
        rule.setUpdatedAt(LocalDateTime.now());
        return automationRuleRepository.save(rule);
    }

    @Override
    public void deleteRule(Long id) {
        automationRuleRepository.deleteById(id);
    }

    @Override
    public AutomationRule toggleRuleStatus(Long id) {
        Optional<AutomationRule> ruleOpt = automationRuleRepository.findById(id);
        if (ruleOpt.isPresent()) {
            AutomationRule rule = ruleOpt.get();
            rule.setIsEnabled(!rule.getIsEnabled());
            rule.setUpdatedAt(LocalDateTime.now());
            return automationRuleRepository.save(rule);
        }
        throw new RuntimeException("Rule not found with id: " + id);
    }
}