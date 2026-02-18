package ru.thendont.software_accounting.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.thendont.software_accounting.entity.InstallationRequest;
import ru.thendont.software_accounting.repository.InstallationRequestRepository;

import java.util.ArrayList;
import java.util.List;

@Service
public class InstallationRequestService {

    @Autowired
    private InstallationRequestRepository installationRequestRepository;

    public List<InstallationRequest> findByDepartmentNumber(Long depNumber) {
        List<InstallationRequest> requests = new ArrayList<>();
        installationRequestRepository.findAll().forEach(request -> {
            if (request.getUser().getDepartment().getDepNumber().equals(depNumber)) {
                requests.add(request);
            }
        });
        return requests;
    }

    public List<InstallationRequest> findByStatusFromList(List<InstallationRequest> requests, String status) {
        List<InstallationRequest> requestsByStatus = new ArrayList<>();
        requests.forEach(request -> {
            if (request.getStatus().equals(status)) {
                requestsByStatus.add(request);
            }
        });
        return requestsByStatus;
    }

    public List<InstallationRequest> findByStatus(String status) {
        return installationRequestRepository.findByStatus(status);
    }

    /*public boolean isPossibleInstallSoftware(InstallationRequest installationRequest) {
        return installationRequest.getDevice().getRamSize() > 1;
    }*/
}