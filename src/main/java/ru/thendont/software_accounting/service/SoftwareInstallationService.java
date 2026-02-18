package ru.thendont.software_accounting.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.thendont.software_accounting.entity.Software;
import ru.thendont.software_accounting.entity.SoftwareInstallation;
import ru.thendont.software_accounting.repository.BaseCrudRepository;

import java.util.ArrayList;
import java.util.List;

@Service
public class SoftwareInstallationService {

    @Autowired
    private BaseCrudRepository<SoftwareInstallation> baseCrudRepository;

    public List<Software> findByDepartmentNumber(Long depNumber) {
        List<Software> softwareList = new ArrayList<>();
        baseCrudRepository.findAll().forEach(softwareInstallation -> {
            if (softwareInstallation.getUser().getDepartment().getDepNumber().equals(depNumber)) {
                softwareList.add(softwareInstallation.getSoftware());
            }
        });
        return softwareList;
    }

    public List<Software> findAllInstalledSoftware() {
        List<Software> softwareList = new ArrayList<>();
        baseCrudRepository.findAll().forEach(softwareInstallation -> {
            softwareList.add(softwareInstallation.getSoftware());
        });
        return softwareList;
    }
}