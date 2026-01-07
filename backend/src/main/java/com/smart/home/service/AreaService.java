package com.smart.home.service;

import com.smart.home.model.entity.Area;
import java.util.List;

/**
 * 区域服务接口
 *
 * @author lingma
 */
public interface AreaService {

    /**
     * 根据用户ID获取区域列表
     *
     * @param userId 用户ID
     * @return 区域列表
     */
    List<Area> getAreasByUserId(Long userId);

    /**
     * 根据ID获取区域
     *
     * @param id 区域ID
     * @return 区域实体
     */
    Area getAreaById(Long id);

    /**
     * 添加区域
     *
     * @param area 区域实体
     * @return 区域实体
     */
    Area addArea(Area area);

    /**
     * 更新区域
     *
     * @param area 区域实体
     * @return 区域实体
     */
    Area updateArea(Area area);

    /**
     * 删除区域
     *
     * @param id 区域ID
     */
    void deleteArea(Long id);

    /**
     * 根据用户ID和区域名称获取区域
     *
     * @param userId 用户ID
     * @param areaName 区域名称
     * @return 区域实体
     */
    Area getAreaByUserIdAndName(Long userId, String areaName);
}