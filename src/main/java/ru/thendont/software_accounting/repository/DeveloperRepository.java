package ru.thendont.software_accounting.repository;

import org.springframework.data.repository.CrudRepository;
import ru.thendont.software_accounting.entity.Developer;

public interface DeveloperRepository extends CrudRepository<Developer, Long> { }