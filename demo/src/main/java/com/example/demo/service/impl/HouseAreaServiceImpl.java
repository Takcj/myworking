package com.example.demo.service.impl;

import com.example.demo.entity.HouseArea;
import com.example.demo.repository.HouseAreaRepository;
import com.example.demo.service.HouseAreaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class HouseAreaServiceImpl implements HouseAreaService {

    @Autowired
    private HouseAreaRepository houseAreaRepository;

    @Override
    public List<HouseArea> getAllAreas() {
        return houseAreaRepository.findAll();
    }

    @Override
    public List<HouseArea> getAreasByUserId(Long userId) {
        return houseAreaRepository.findByUserId(userId);
    }

    @Override
    public Optional<HouseArea> getAreaById(Long id) {
        return houseAreaRepository.findById(id);
    }

    @Override
    public HouseArea createArea(HouseArea area) {
        return houseAreaRepository.save(area);
    }

    @Override
    public HouseArea updateArea(HouseArea area) {
        return houseAreaRepository.save(area);
    }

    @Override
    public void deleteArea(Long id) {
        houseAreaRepository.deleteById(id);
    }
}