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
        List<Software> softwareList = new ArrayList<>();
        softwareInstallationRepository.findAll().forEach(softwareInstallation -> {
            if (softwareInstallation.getUser().getDepartment().getDepNumber().equals(depNumber)) {
                softwareList.add(softwareInstallation.getSoftware());
            }
        });
        return softwareList;
    }

    public List<Software> findAllInstalledSoftware() {
        List<Software> softwareList = new ArrayList<>();
        softwareInstallationRepository.findAll().forEach(softwareInstallation -> {
            softwareList.add(softwareInstallation.getSoftware());
        });
        return softwareList;
    }

    public SoftwareInstallation save(SoftwareInstallation softwareInstallation) {
        return softwareInstallationRepository.save(softwareInstallation);
    }
}