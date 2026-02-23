package ru.thendont.software_accounting.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.thendont.software_accounting.entity.LicenseRequest;

@Repository
public interface LicenseRequestRepository extends CrudRepository<LicenseRequest, Long> { }