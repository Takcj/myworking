package com.example.demo.repository;

import com.example.demo.entity.HouseArea;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HouseAreaRepository extends JpaRepository<HouseArea, Long> {
    List<HouseArea> findByUserId(Long userId);
}