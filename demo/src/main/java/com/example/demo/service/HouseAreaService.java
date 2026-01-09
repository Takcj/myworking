package com.example.demo.service;

import com.example.demo.entity.HouseArea;
import java.util.List;
import java.util.Optional;

public interface HouseAreaService {
    List<HouseArea> getAllAreas();
    List<HouseArea> getAreasByUserId(Long userId);
    Optional<HouseArea> getAreaById(Long id);
    HouseArea createArea(HouseArea area);
    HouseArea updateArea(HouseArea area);
    void deleteArea(Long id);
}