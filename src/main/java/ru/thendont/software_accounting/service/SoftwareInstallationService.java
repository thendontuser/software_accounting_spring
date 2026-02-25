package ru.thendont.software_accounting.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.thendont.software_accounting.entity.Software;
import ru.thendont.software_accounting.entity.SoftwareInstallation;
import ru.thendont.software_accounting.repository.SoftwareInstallationRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class SoftwareInstallationService {

    @Autowired
    private SoftwareInstallationRepository softwareInstallationRepository;

    public Optional<SoftwareInstallation> findById(Long id) {
        return softwareInstallationRepository.findById(id);
    }

    public List<SoftwareInstallation> findAll() {
        return (List<SoftwareInstallation>) softwareInstallationRepository.findAll();
    }

    public SoftwareInstallation save(SoftwareInstallation softwareInstallation) {
        return softwareInstallationRepository.save(softwareInstallation);
    }

    public void deleteById(Long id) {
        softwareInstallationRepository.deleteById(id);
    }

    /*public List<Software> findByDepartmentNumber(Long depNumber) {
        List<Software> softwareList = new ArrayList<>();
        softwareInstallationRepository.findAll().forEach(softwareInstallation -> {
            if (softwareInstallation.getUser().getDepartment().getDepNumber().equals(depNumber)) {
                softwareList.add(softwareInstallation.getSoftware());
            }
        });
        return softwareList;
    }*/

    public List<Software> findAllInstalledSoftware() {
        List<Software> softwareList = new ArrayList<>();
        softwareInstallationRepository.findAll().forEach(softwareInstallation -> {
            softwareList.add(softwareInstallation.getSoftware());
        });
        return softwareList;
    }
}