package ru.thendont.software_accounting.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import ru.thendont.software_accounting.entity.User;
import ru.thendont.software_accounting.repository.DepartmentRepository;
import ru.thendont.software_accounting.repository.UserRepository;
import ru.thendont.software_accounting.service.UserService;

@Controller
@RequestMapping("/auth")
public class AuthController {

    private final DepartmentRepository departmentRepository;
    private final UserRepository userRepository;

    public AuthController(DepartmentRepository departmentRepository, UserRepository userRepository) {
        this.departmentRepository = departmentRepository;
        this.userRepository = userRepository;
    }

    @GetMapping("/login")
    public String showLoginPage(Model model) {
        model.addAttribute("user", new User());
        return "sign-in";
    }

    @PostMapping("/login")
    public String showLoginPage(@ModelAttribute User user, Model model) {
        User u = UserService.isAuthorise(user, userRepository).orElse(null);
        if (u == null) {
            model.addAttribute("error", "Неверный логин или пароль");
            return "sign-in";
        }
        return "sign-in";
    }

    @GetMapping("/register")
    public String showRegisterPage(Model model) {
        model.addAttribute("user", new User());
        model.addAttribute("departments", departmentRepository.findAll());
        return "sign-up";
    }

    @PostMapping("/register")
    public String showRegisterPage(@ModelAttribute User user, Model model) {
        User u = userRepository.findByLogin(user.getLogin()).orElse(null);
        if (u != null) {
            model.addAttribute("error", "Логин уже занят");
            model.addAttribute("departments", departmentRepository.findAll());
            return "sign-up";
        }
        UserService.processDepartment(user);
        UserService.hashPassword(user);
        userRepository.save(user);
        return "sign-in";
    }
}