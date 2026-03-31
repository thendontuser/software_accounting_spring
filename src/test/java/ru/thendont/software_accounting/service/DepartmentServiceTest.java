package ru.thendont.software_accounting.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.thendont.software_accounting.entity.Department;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Предоставляет методы для модульного тестирования сервиса факультетов
 * @author thendont
 * @version 1.0
 */
@SpringBootTest
public class DepartmentServiceTest {

    @Autowired
    private DepartmentService departmentService;

    /**
     * Тестирует создание новой записи факультета
     */
    @Test
    public void testCreateDepartment() {
        Department department = new Department(null, "test");
        Department saved = departmentService.save(department);

        assertEquals(department.getTitle(), saved.getTitle());
        assertNotNull(saved.getDepNumber());

        departmentService.deleteById(saved.getDepNumber());
    }

    /**
     * Тестирует обновление записи факультета
     */
    @Test
    public void testUpdateDepartment() {
        Department department = new Department(null, "test");
        Department saved = departmentService.save(department);
        Department target = departmentService.findById(saved.getDepNumber()).orElse(null);

        assert target != null;

        target.setTitle("testUpdate");
        target = departmentService.save(target);

        assertNotEquals(department.getTitle(), target.getTitle());

        departmentService.deleteById(saved.getDepNumber());
    }

    /**
     * Тестирует удаление записи факультета
     */
    @Test
    public void testDeleteDepartment() {
        Department department = new Department(null, "testTitle");
        Department saved = departmentService.save(department);
        Long savedDepNumber = saved.getDepNumber();

        departmentService.deleteById(savedDepNumber);

        assertNull(departmentService.findById(savedDepNumber).orElse(null));
    }
}