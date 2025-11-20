package ru.thendont.software_accounting.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.thendont.software_accounting.entity.Department;
import ru.thendont.software_accounting.entity.User;
import ru.thendont.software_accounting.service.DepartmentService;
import ru.thendont.software_accounting.service.UserService;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;

@Controller
@RequestMapping("/admin")
public class AdminPageController {

    private final UserService userService;
    private final DepartmentService departmentService;

    public AdminPageController(UserService userService,
                               DepartmentService departmentService) {
        this.userService = userService;
        this.departmentService = departmentService;
    }

    @GetMapping("/dashboard")
    public String showDashboard(@RequestParam Long userId, Model model) {
        try {
            User user = userService.findById(userId).orElseThrow();
            List<Department> departments = departmentService.findAll();
            return "admin-page";
        }
        catch (NoSuchElementException ex) {
            return handleException("Не найден объект", ex.getMessage(), model);
        }
    }

    private String handleException(String title, String message, Model model) {
        model.addAttribute("errorTitle", title);
        model.addAttribute("errorMessage", message);
        model.addAttribute("timestamp", LocalDate.now());
        return "error-page";
    }
}