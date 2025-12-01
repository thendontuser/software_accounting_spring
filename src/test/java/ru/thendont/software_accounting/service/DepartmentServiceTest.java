package ru.thendont.software_accounting.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.thendont.software_accounting.entity.Department;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class DepartmentServiceTest {

    @Autowired
    private DepartmentService departmentService;

    @Test
    public void testDeleteDepartment() {
        Department department = new Department(null, "testTitle");
        Department saved = departmentService.save(department);
        Long savedDepNumber = saved.getDepNumber();
        departmentService.deleteById(savedDepNumber);

        assertNotEquals(saved, departmentService.findById(savedDepNumber).orElse(null));
    }
}