package ru.thendont.software_accounting.service;

import org.springframework.stereotype.Service;
import ru.thendont.software_accounting.entity.Device;
import ru.thendont.software_accounting.repository.DeviceRepository;

import java.util.Optional;

@Service
public class DeviceService {

    private final DeviceRepository deviceRepository;

    public DeviceService(DeviceRepository deviceRepository) {
        this.deviceRepository = deviceRepository;
    }

    public Optional<Device> findById(Long id) {
        return deviceRepository.findById(id);
    }
}