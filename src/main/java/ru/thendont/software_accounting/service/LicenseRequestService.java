package ru.thendont.software_accounting.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.thendont.software_accounting.entity.LicenseRequest;
import ru.thendont.software_accounting.repository.LicenseRequestRepository;

import java.util.List;
import java.util.Optional;

@Service
public class LicenseRequestService {

    @Autowired
    private LicenseRequestRepository licenseRequestRepository;

    public Optional<LicenseRequest> findById(Long id) {
        return licenseRequestRepository.findById(id);
    }

    public List<LicenseRequest> findAll() {
        return (List<LicenseRequest>) licenseRequestRepository.findAll();
    }

    public LicenseRequest save(LicenseRequest licenseRequest) {
        return licenseRequestRepository.save(licenseRequest);
    }

    public void deleteById(Long id) {
        licenseRequestRepository.deleteById(id);
    }
}