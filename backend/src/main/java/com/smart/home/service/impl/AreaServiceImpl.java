package com.smart.home.service.impl;

import com.smart.home.model.entity.Area;
import com.smart.home.service.AreaService;
import com.smart.home.mapper.AreaMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 区域服务实现类
 *
 * @author lingma
 */
@Service
public class AreaServiceImpl implements AreaService {

    @Autowired
    private AreaMapper areaMapper;

    /**
     * 根据用户ID获取区域列表
     *
     * @param userId 用户ID
     * @return 区域列表
     */
    @Override
    public List<Area> getAreasByUserId(Long userId) {
        return areaMapper.selectByUserId(userId);
    }

    /**
     * 根据ID获取区域
     *
     * @param id 区域ID
     * @return 区域实体
     */
    @Override
    public Area getAreaById(Long id) {
        return areaMapper.selectById(id);
    }

    /**
     * 添加区域
     *
     * @param area 区域实体
     * @return 区域实体
     */
    @Override
    public Area addArea(Area area) {
        area.setCreatedAt(LocalDateTime.now());
        area.setUpdatedAt(LocalDateTime.now());
        areaMapper.insert(area);
        return area;
    }

    /**
     * 更新区域
     *
     * @param area 区域实体
     * @return 区域实体
     */
    @Override
    public Area updateArea(Area area) {
        area.setUpdatedAt(LocalDateTime.now());
        areaMapper.updateById(area);
        return area;
    }

    /**
     * 删除区域
     *
     * @param id 区域ID
     */
    @Override
    public void deleteArea(Long id) {
        areaMapper.deleteById(id);
    }

    /**
     * 根据用户ID和区域名称获取区域
     *
     * @param userId   用户ID
     * @param areaName 区域名称
     * @return 区域实体
     */
    @Override
    public Area getAreaByUserIdAndName(Long userId, String areaName) {
        return areaMapper.selectByUserIdAndName(userId, areaName);
    }
}