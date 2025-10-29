package ru.thendont.software_accounting.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.thendont.software_accounting.entity.Developer;

@Repository
public interface DeveloperRepository extends CrudRepository<Developer, Long> { }