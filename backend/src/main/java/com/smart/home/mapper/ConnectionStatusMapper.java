package com.smart.home.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.smart.home.model.entity.ConnectionStatus;
import org.apache.ibatis.annotations.Mapper;

/**
 * 连接状态Mapper接口
 *
 * @author lingma
 */
@Mapper
public interface ConnectionStatusMapper extends BaseMapper<ConnectionStatus> {
    // 继承BaseMapper后，已自动包含基本的CRUD方法
    // 如需特殊查询方法，可在此处添加
}