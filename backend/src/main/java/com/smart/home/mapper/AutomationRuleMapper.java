package com.smart.home.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.smart.home.model.entity.AutomationRule;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 自动化规则Mapper接口
 *
 * @author lingma
 */
@Mapper
public interface AutomationRuleMapper extends BaseMapper<AutomationRule> {

    /**
     * 根据用户ID获取自动化规则列表
     *
     * @param userId 用户ID
     * @return 自动化规则列表
     */
    List<AutomationRule> selectByUserId(@Param("userId") Long userId);

    /**
     * 根据用户ID获取启用的自动化规则列表
     *
     * @param userId 用户ID
     * @return 启用的自动化规则列表
     */
    List<AutomationRule> selectEnabledByUserId(@Param("userId") Long userId);

    /**
     * 查询所有启用的定时规则
     *
     * @return 定时规则列表
     */
    List<AutomationRule> selectScheduledRules();
}