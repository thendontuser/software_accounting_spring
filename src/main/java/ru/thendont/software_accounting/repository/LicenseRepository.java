package ru.thendont.software_accounting.repository;

import org.springframework.data.repository.CrudRepository;
import ru.thendont.software_accounting.entity.License;

public interface LicenseRepository extends CrudRepository<License, Long> { }