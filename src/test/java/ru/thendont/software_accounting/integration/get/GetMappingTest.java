package ru.thendont.software_accounting.integration.get;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import ru.thendont.software_accounting.entity.User;
import ru.thendont.software_accounting.service.UserService;
import ru.thendont.software_accounting.service.enums.UserRoles;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Предоставляет методы для интеграционного тестирования GET запросов
 * @author thendont
 * @version 1.0
 */
@SpringBootTest
@AutoConfigureMockMvc
public class GetMappingTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserService userService;

    /**
     * Тестирует успешный GET запрос на url /admin/dashboard
     * @throws Exception возникает при выполнении метода perform экземпляра MockMvc
     */
    @Test
    public void showAdminDashboardSuccess() throws Exception {
        User user = userService.findByRole(UserRoles.ADMIN).getFirst();

        mockMvc.perform(get("/admin/dashboard")
                        .param("userId", Long.toString(user.getId()))
                        .with(user(user.getUsername()).roles(UserRoles.ADMIN.name())))
                .andExpect(status().isOk())
                .andExpect(view().name("admin-page"));
    }

    /**
     * Тестирует успешный GET запрос на url /manager/dashboard
     * @throws Exception возникает при выполнении метода perform экземпляра MockMvc
     */
    @Test
    public void showSamManagerDashboardSuccess() throws Exception {
        User user = userService.findByRole(UserRoles.SAM_MANAGER).getFirst();

        mockMvc.perform(get("/manager/dashboard")
                        .param("userId", Long.toString(user.getId()))
                        .with(user(user.getUsername()).roles(UserRoles.SAM_MANAGER.name())))
                .andExpect(status().isOk())
                .andExpect(view().name("sam-manager-page"));
    }
}