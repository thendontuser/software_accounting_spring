package ru.thendont.software_accounting.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.thendont.software_accounting.entity.LicenseRequest;
import ru.thendont.software_accounting.service.enums.LicenseRequestStatus;

@Repository
public interface LicenseRequestRepository extends CrudRepository<LicenseRequest, Long> {

    Iterable<LicenseRequest> findByStatus(LicenseRequestStatus status);
}