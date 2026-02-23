package ru.thendont.software_accounting.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.thendont.software_accounting.entity.InstallationReport;
import ru.thendont.software_accounting.repository.InstallationReportRepository;

import java.util.List;
import java.util.Optional;

@Service
public class InstallationReportService {

    @Autowired
    private InstallationReportRepository installationReportRepository;

    public Optional<InstallationReport> findById(Long id) {
        return installationReportRepository.findById(id);
    }

    public List<InstallationReport> findAll() {
        return (List<InstallationReport>) installationReportRepository.findAll();
    }

    public InstallationReport save(InstallationReport installationReport) {
        return installationReportRepository.save(installationReport);
    }

    public void deleteById(Long id) {
        installationReportRepository.deleteById(id);
    }
}