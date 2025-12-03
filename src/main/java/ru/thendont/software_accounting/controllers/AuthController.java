package ru.thendont.software_accounting.controllers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import ru.thendont.software_accounting.entity.User;
import ru.thendont.software_accounting.service.DepartmentService;
import ru.thendont.software_accounting.service.UserService;
import ru.thendont.software_accounting.util.Urls;
import ru.thendont.software_accounting.util.UserRoles;

@Controller
@RequestMapping("/auth")
public class AuthController {

    private static final Logger logger = LogManager.getLogger(AuthController.class);

    private final DepartmentService departmentService;
    private final UserService userService;

    public AuthController(DepartmentService departmentService, UserService userService) {
        this.departmentService = departmentService;
        this.userService = userService;
    }

    @GetMapping("/login")
    public String showLoginPage(Model model) {
        logger.info("=== ПЕРЕХОД НА СТРАНИЦУ ВХОДА === ");
        model.addAttribute("user", new User());
        return "sign-in";
    }

    @PostMapping("/login")
    public String showLoginPage(@ModelAttribute User user, Model model) {
        User u = userService.isAuthorise(user).orElse(null);
        if (u == null) {
            logger.warn("=== НЕВЕРНЫЙ ЛОГИН ИЛИ ПАРОЛЬ === ");
            model.addAttribute("error", "Неверный логин или пароль");
            return "sign-in";
        }
        return getPageFromUser(u);
    }

    @GetMapping("/register")
    public String showRegisterPage(Model model) {
        model.addAttribute("user", new User());
        model.addAttribute("departments", departmentService.findAll());
        logger.info("=== ПЕРЕХОД НА СТРАНИЦУ РЕГИСТРАЦИИ === ");
        return "sign-up";
    }

    @PostMapping("/register")
    public String showRegisterPage(@ModelAttribute User user, Model model) {
        User u = userService.findByLogin(user.getLogin()).orElse(null);
        if (u != null) {
            logger.warn("=== ПОЛЬЗОВАТЕЛЬ С ID {} УЖЕ СУЩЕСТВУЕТ === ", u.getId());
            model.addAttribute("error", "Логин уже занят");
            model.addAttribute("departments", departmentService.findAll());
            return "sign-up";
        }
        userService.processDepartment(user);
        logger.debug("=== УСПЕШНАЯ ОБРАБОТКА ПРИВЯЗКИ ПОЛЬЗВАТЕЛЯ К ФАКУЛЬТЕТУ ===");

        userService.hashPassword(user);
        logger.debug("=== УСПЕШНОЕ ХЕШИРОВАНИЕ ПАРОЛЯ ===");

        userService.save(user);
        logger.debug("=== УСПЕШНОЕ СОХРАНЕНИЕ ПОЛЬЗОВАТЕЛЯ В БАЗУ ДАННЫХ ===");
        return getPageFromUser(user);
    }

    private String getPageFromUser(User user) {
        return switch (user.getRole()) {
            case UserRoles.ADMIN -> Urls.ADMIN_URL + user.getId();
            case UserRoles.ACCOUNTANT -> Urls.ACCOUNTANT_URL + user.getId();
            case UserRoles.MANAGER -> Urls.MANAGER_URL + user.getId();
            case UserRoles.TEACHER -> Urls.TEACHER_URL + user.getId();
            default -> "";
        };
    }
}