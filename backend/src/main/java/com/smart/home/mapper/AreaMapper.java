package com.smart.home.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.smart.home.model.entity.Area;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 区域Mapper接口
 *
 * @author lingma
 */
@Mapper
public interface AreaMapper extends BaseMapper<Area> {
    /**
     * 根据用户ID查询区域列表
     *
     * @param userId 用户ID
     * @return 区域列表
     */
    List<Area> selectByUserId(@Param("userId") Long userId);

    /**
     * 根据用户ID和区域名称查询区域
     *
     * @param userId   用户ID
     * @param areaName 区域名称
     * @return 区域实体
     */
    Area selectByUserIdAndName(@Param("userId") Long userId, @Param("areaName") String areaName);
}