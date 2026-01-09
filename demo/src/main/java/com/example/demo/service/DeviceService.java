package com.example.demo.service;

import com.example.demo.entity.Device;
import java.util.List;
import java.util.Optional;

public interface DeviceService {
    List<Device> getAllDevices();

    List<Device> getDevicesByUserId(Long userId);

    List<Device> getDevicesByAreaId(Long areaId);

    Optional<Device> getDeviceById(Long id);

    Device createDevice(Device device);

    Device updateDevice(Device device);

    void deleteDevice(Long id);
    
    void updateDeviceStatus(String deviceId, String deviceData);
}