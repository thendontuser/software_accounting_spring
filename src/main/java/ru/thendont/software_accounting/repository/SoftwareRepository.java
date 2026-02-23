package ru.thendont.software_accounting.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.thendont.software_accounting.entity.Software;

@Repository
public interface SoftwareRepository extends CrudRepository<Software, Long> { }