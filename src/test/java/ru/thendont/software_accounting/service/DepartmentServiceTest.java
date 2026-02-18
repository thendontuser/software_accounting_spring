package ru.thendont.software_accounting.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.thendont.software_accounting.entity.Department;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class DepartmentServiceTest {

    @Autowired
    private BaseCrudService<Department> departmentBaseCrudService;

    @Test
    public void testCreateDepartment() {
        Department department = new Department(null, "test");
        Department saved = departmentBaseCrudService.save(department);

        assertEquals(department.getTitle(), saved.getTitle());
        assertNotNull(saved.getDepNumber());

        departmentBaseCrudService.deleteById(saved.getDepNumber());
    }

    @Test
    public void testUpdateDepartment() {
        Department department = new Department(null, "test");
        Department saved = departmentBaseCrudService.save(department);
        Department target = departmentBaseCrudService.findById(Long.valueOf(saved.getDepNumber())).orElse(null);

        target.setTitle("testUpdate");
        target = departmentBaseCrudService.save(target);

        assertNotEquals(department.getTitle(), target.getTitle());

        departmentBaseCrudService.deleteById(saved.getDepNumber());
    }

    @Test
    public void testDeleteDepartment() {
        Department department = new Department(null, "testTitle");
        Department saved = departmentBaseCrudService.save(department);
        Long savedDepNumber = saved.getDepNumber();

        departmentBaseCrudService.deleteById(savedDepNumber);

        assertNull(departmentBaseCrudService.findById(savedDepNumber).orElse(null));
    }
}