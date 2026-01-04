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
    public void testCreateDepartment() {
        Department department = new Department(null, "test");
        Department saved = departmentService.save(department);

        assertEquals(department.getTitle(), saved.getTitle());
        assertNotNull(saved.getDepNumber());

        departmentService.deleteById(saved.getDepNumber());
    }

    @Test
    public void testUpdateDepartment() {
        Department department = new Department(null, "test");
        Department saved = departmentService.save(department);
        Department target = departmentService.findById(Long.valueOf(saved.getDepNumber())).orElse(null);

        target.setTitle("testUpdate");
        target = departmentService.save(target);

        assertNotEquals(department.getTitle(), target.getTitle());

        departmentService.deleteById(saved.getDepNumber());
    }

    @Test
    public void testDeleteDepartment() {
        Department department = new Department(null, "testTitle");
        Department saved = departmentService.save(department);
        Long savedDepNumber = saved.getDepNumber();

        departmentService.deleteById(savedDepNumber);

        assertNull(departmentService.findById(savedDepNumber).orElse(null));
    }
}