package ru.thendont.software_accounting.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.thendont.software_accounting.entity.InstallationTask;
import ru.thendont.software_accounting.entity.Kafedra;
import ru.thendont.software_accounting.entity.User;
import ru.thendont.software_accounting.repository.InstallationTaskRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Класс-сервис для управления бизнес логикой, связанной с таблицей installation_task
 * @author thendont
 * @version 1.0
 */
@Service
public class InstallationTaskService {
    
    @Autowired
    private InstallationTaskRepository installationTaskRepository;

    public Optional<InstallationTask> findById(Long id) {
        return installationTaskRepository.findById(id);
    }

    public List<InstallationTask> findAll() {
        return (List<InstallationTask>) installationTaskRepository.findAll();
    }

    public InstallationTask save(InstallationTask installationTask) {
        return installationTaskRepository.save(installationTask);
    }

    public void deleteById(Long id) {
        installationTaskRepository.deleteById(id);
    }

    public List<InstallationTask> findByAssignedBy(User assignedBy) {
        return (List<InstallationTask>) installationTaskRepository.findByAssignedBy(assignedBy);
    }

    public List<InstallationTask> findByAssignedTo(User assignedTo) {
        return (List<InstallationTask>) installationTaskRepository.findByAssignedTo(assignedTo);
    }

    /**
     * Находит задачи пользователей по кафедре и по промежутку дат
     * @param kafedra кафедра
     * @param dateFrom дата начала выборки
     * @param dateTo дата окончания выборки
     * @return Список задач пользователей по кафедре и по промежутку дат
     */
    public List<InstallationTask> findByKafedraAndDateBetween(Kafedra kafedra, LocalDate dateFrom, LocalDate dateTo) {
        return (List<InstallationTask>) installationTaskRepository.findByKafedraAndDateBetween(
                kafedra.getId(), dateFrom, dateTo
        );
    }
}