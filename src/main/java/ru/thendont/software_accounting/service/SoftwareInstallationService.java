package ru.thendont.software_accounting.service;

import org.springframework.stereotype.Service;
import ru.thendont.software_accounting.entity.Software;
import ru.thendont.software_accounting.entity.SoftwareInstallation;
import ru.thendont.software_accounting.repository.SoftwareInstallationRepository;

import java.util.ArrayList;
import java.util.List;

@Service
public class SoftwareInstallationService {

    private final SoftwareInstallationRepository softwareInstallationRepository;

    public SoftwareInstallationService(SoftwareInstallationRepository softwareInstallationRepository) {
        this.softwareInstallationRepository = softwareInstallationRepository;
    }

    public List<Software> findByDepartmentNumber(Long depNumber) {
        List<Software> software = new ArrayList<>();
        for (SoftwareInstallation softwareInstallation : softwareInstallationRepository.findAll()) {
            if (softwareInstallation.getUser().getDepartment().getDepNumber().equals(depNumber)) {
                software.add(softwareInstallation.getSoftware());
            }
        }
        return software;
    }
}