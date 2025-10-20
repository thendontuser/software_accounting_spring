package ru.thendont.software_accounting.repository;

import org.springframework.data.repository.CrudRepository;
import ru.thendont.software_accounting.entity.Device;

public interface DeviceRepository extends CrudRepository<Device, Long> { }