package ru.thendont.software_accounting.service;

import org.springframework.stereotype.Service;
import ru.thendont.software_accounting.entity.License;
import ru.thendont.software_accounting.repository.LicenseRepository;

import java.math.BigDecimal;
import java.util.List;

@Service
public class LicenseService {

    private final LicenseRepository licenseRepository;

    public LicenseService(LicenseRepository licenseRepository) {
        this.licenseRepository = licenseRepository;
    }

    public List<License> findAll() {
        return (List<License>) licenseRepository.findAll();
    }

    public BigDecimal getTotalCost() {
        return licenseRepository.getTotalCost();
    }
}