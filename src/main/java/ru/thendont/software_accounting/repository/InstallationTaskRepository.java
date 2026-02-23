package ru.thendont.software_accounting.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.thendont.software_accounting.entity.InstallationTask;

@Repository
public interface InstallationTaskRepository extends CrudRepository<InstallationTask, Long> { }