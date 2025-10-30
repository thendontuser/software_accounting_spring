package ru.thendont.software_accounting.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.thendont.software_accounting.entity.InstallationRequest;

import java.util.List;

@Repository
public interface InstallationRequestRepository extends CrudRepository<InstallationRequest, Long> {

    List<InstallationRequest> findByStatus(String status);
}