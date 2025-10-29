package ru.thendont.software_accounting.service;

import org.springframework.stereotype.Service;
import ru.thendont.software_accounting.entity.Department;
import ru.thendont.software_accounting.repository.DepartmentRepository;

import java.util.List;

@Service
public class DepartmentService {

    private final DepartmentRepository departmentRepository;

    public DepartmentService(DepartmentRepository departmentRepository) {
        this.departmentRepository = departmentRepository;
    }

    public List<Department> findAll() {
        return (List<Department>) departmentRepository.findAll();
    }
}