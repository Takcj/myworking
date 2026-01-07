package com.smart.home.controller;

import com.smart.home.common.Result;
import com.smart.home.model.entity.Area;
import com.smart.home.service.AreaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 区域控制器
 *
 * @author lingma
 */
@RestController
@RequestMapping("/api/areas")
public class AreaController {

    @Autowired
    private AreaService areaService;

    /**
     * 获取用户所有区域
     *
     * @param userId 用户ID
     * @return 区域列表
     */
    @GetMapping
    public Result<List<Area>> getUserAreas(@RequestParam Long userId) {
        List<Area> areas = areaService.getAreasByUserId(userId);
        return Result.success(areas);
    }

    /**
     * 获取特定区域信息
     *
     * @param id 区域ID
     * @return 区域信息
     */
    @GetMapping("/{id}")
    public Result<Area> getArea(@PathVariable Long id) {
        Area area = areaService.getAreaById(id);
        if (area != null) {
            return Result.success(area);
        }
        return Result.error("区域不存在");
    }

    /**
     * 创建区域
     *
     * @param area 区域信息
     * @return 创建结果
     */
    @PostMapping
    public Result<Area> createArea(@RequestBody Area area) {
        Area newArea = areaService.addArea(area);
        return Result.success("区域创建成功", newArea);
    }

    /**
     * 更新区域
     *
     * @param id 区域ID
     * @param area 区域信息
     * @return 更新结果
     */
    @PutMapping("/{id}")
    public Result<Area> updateArea(@PathVariable Long id, @RequestBody Area area) {
        area.setId(id);
        Area updatedArea = areaService.updateArea(area);
        return Result.success("区域更新成功", updatedArea);
    }

    /**
     * 删除区域
     *
     * @param id 区域ID
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    public Result<String> deleteArea(@PathVariable Long id) {
        areaService.deleteArea(id);
        return Result.success("区域删除成功", null);
    }
}