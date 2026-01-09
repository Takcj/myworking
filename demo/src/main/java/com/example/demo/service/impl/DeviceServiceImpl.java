package com.example.demo.service.impl;

import com.example.demo.entity.Device;
import com.example.demo.repository.DeviceRepository;
import com.example.demo.service.DeviceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DeviceServiceImpl implements DeviceService {

    @Autowired
    private DeviceRepository deviceRepository;

    @Override
    public List<Device> getAllDevices() {
        return deviceRepository.findAll();
    }

    @Override
    public List<Device> getDevicesByUserId(Long userId) {
        return deviceRepository.findByUserId(userId);
    }

    @Override
    public List<Device> getDevicesByAreaId(Long areaId) {
        return deviceRepository.findByAreaId(areaId);
    }

    @Override
    public Optional<Device> getDeviceById(Long id) {
        return deviceRepository.findById(id);
    }

    @Override
    public Device createDevice(Device device) {
        return deviceRepository.save(device);
    }

    @Override
    public Device updateDevice(Device device) {
        return deviceRepository.save(device);
    }

    @Override
    public void deleteDevice(Long id) {
        deviceRepository.deleteById(id);
    }

    @Override
    public void updateDeviceStatus(String deviceId, String deviceData) {
        Optional<Device> deviceOpt = deviceRepository.findById(Long.parseLong(deviceId));
        if (deviceOpt.isPresent()) {
            Device device = deviceOpt.get();
            // 假设我们将设备状态数据存储在某个字段中
            device.setStatusName(deviceData);
            deviceRepository.save(device);
        }
    }
}