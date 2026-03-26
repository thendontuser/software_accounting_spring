package ru.thendont.software_accounting.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.thendont.software_accounting.entity.InstallationReport;
import ru.thendont.software_accounting.entity.User;
import ru.thendont.software_accounting.repository.InstallationReportRepository;

import java.util.List;
import java.util.Optional;

/**
 * Класс-сервис для управления бизнес логикой, связанной с таблицей installation_report
 * @author thendont
 * @version 1.0
 */
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

    /**
     * Находит записи отчетов по идентификатору пользователя, поручившего задачу
     * @param assignedBy идентификатор пользователя, поручившего задачу
     * @return Список отчетов
     */
    public List<InstallationReport> findByTaskAssignedBy(User assignedBy) {
        return (List<InstallationReport>) installationReportRepository.findByTaskAssignedBy(assignedBy.getId());
    }

    /**
     * Находит записи отчетов по идентификатору пользователя, которому поручена задача
     * @param assignedTo идентификатор пользователя, которому поручена задача
     * @return Список отчетов
     */
    public List<InstallationReport> findByTaskAssignedTo(User assignedTo) {
        return (List<InstallationReport>) installationReportRepository.findByTaskAssignedTo(assignedTo.getId());
    }
}