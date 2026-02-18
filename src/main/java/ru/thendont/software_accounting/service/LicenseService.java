package ru.thendont.software_accounting.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.thendont.software_accounting.repository.LicenseRepository;

import java.math.BigDecimal;

@Service
public class LicenseService {

    @Autowired
    private LicenseRepository licenseRepository;

    public BigDecimal getTotalCost() {
        return licenseRepository.getTotalCost();
    }
}