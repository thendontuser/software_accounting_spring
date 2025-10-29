package ru.thendont.software_accounting.service;

import org.springframework.stereotype.Service;
import ru.thendont.software_accounting.entity.InstallationRequest;
import ru.thendont.software_accounting.repository.InstallationRequestRepository;

@Service
public class InstallationRequestService {

    private final InstallationRequestRepository installationRequestRepository;

    public InstallationRequestService(InstallationRequestRepository installationRequestRepository) {
        this.installationRequestRepository = installationRequestRepository;
    }

    public InstallationRequest save(InstallationRequest installationRequest) {
        return installationRequestRepository.save(installationRequest);
    }
}