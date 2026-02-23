package ru.thendont.software_accounting.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.thendont.software_accounting.entity.InstallationTask;
import ru.thendont.software_accounting.repository.InstallationTaskRepository;

import java.util.List;
import java.util.Optional;

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
}