package ru.thendont.software_accounting.repository;

import org.springframework.data.repository.CrudRepository;
import ru.thendont.software_accounting.entity.InstallationRequest;

public interface InstallationRequestRepository extends CrudRepository<InstallationRequest, Long> { }