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
     * 根据用户ID查询启用的规则列表
     *
     * @param userId 用户ID
     * @return 启用的规则列表
     */
    List<AutomationRule> selectEnabledRulesByUserId(@Param("userId") Long userId);

    /**
     * 更新规则启用状态
     *
     * @param params 包含id和status的参数
     */
    void updateRuleStatus(@Param("id") Long id, @Param("status") int status);
}