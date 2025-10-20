package ru.thendont.software_accounting.repository;

import org.springframework.data.repository.CrudRepository;
import ru.thendont.software_accounting.entity.Software;

public interface SoftwareRepository extends CrudRepository<Software, Long> { }