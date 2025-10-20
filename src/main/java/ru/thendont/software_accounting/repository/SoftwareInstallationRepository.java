package ru.thendont.software_accounting.repository;

import org.springframework.data.repository.CrudRepository;
import ru.thendont.software_accounting.entity.SoftwareInstallation;

public interface SoftwareInstallationRepository extends CrudRepository<SoftwareInstallation, Long> { }