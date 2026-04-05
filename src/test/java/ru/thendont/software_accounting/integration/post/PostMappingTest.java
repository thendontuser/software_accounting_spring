package ru.thendont.software_accounting.integration.post;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import ru.thendont.software_accounting.entity.Department;
import ru.thendont.software_accounting.entity.User;
import ru.thendont.software_accounting.service.DepartmentService;
import ru.thendont.software_accounting.service.UserService;
import ru.thendont.software_accounting.service.enums.UserRoles;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Предоставляет методы для интеграционного тестирования POST запросов
 * @author thendont
 * @version 1.0
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class PostMappingTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserService userService;

    @Autowired
    private DepartmentService departmentService;

    /**
     * Тестирует успешное сохранение (создание) факультета
     * @throws Exception возникает при выполнении метода perform экземпляра MockMvc
     */
    @Test
    public void createDepartmentSuccess() throws Exception {
        User user = userService.findByRole(UserRoles.ADMIN).getFirst();

        Department department = new Department();
        department.setTitle("new department");

        mockMvc.perform(post("/admin/edit/departments")
                        .flashAttr("department", department)
                        .param("currentUserId", Long.toString(user.getId()))
                        .with(user(user.getUsername()).roles(UserRoles.ADMIN.name()))
                        .with(csrf()))
                .andExpect(status().is3xxRedirection());
    }
}