package ru.thendont.software_accounting.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.thendont.software_accounting.entity.Device;
import ru.thendont.software_accounting.repository.DeviceRepository;

import java.util.List;
import java.util.Optional;

@Service
public class DeviceService {
    
    @Autowired
    private DeviceRepository deviceRepository;

    public Optional<Device> findById(Long id) {
        return deviceRepository.findById(id);
    }

    public List<Device> findAll() {
        return (List<Device>) deviceRepository.findAll();
    }

    public Device save(Device device) {
        return deviceRepository.save(device);
    }

    public void deleteById(Long id) {
        deviceRepository.deleteById(id);
    }
}