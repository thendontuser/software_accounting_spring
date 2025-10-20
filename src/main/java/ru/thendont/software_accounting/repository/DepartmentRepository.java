package ru.thendont.software_accounting.repository;

import org.springframework.data.repository.CrudRepository;
import ru.thendont.software_accounting.entity.Department;

public interface DepartmentRepository extends CrudRepository<Department, Long> { }