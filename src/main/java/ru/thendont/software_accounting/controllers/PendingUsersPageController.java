package ru.thendont.software_accounting.controllers;

import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.thendont.software_accounting.entity.Department;
import ru.thendont.software_accounting.entity.User;
import ru.thendont.software_accounting.error.ErrorHandler;
import ru.thendont.software_accounting.service.BaseCrudService;
import ru.thendont.software_accounting.service.UserService;
import ru.thendont.software_accounting.service.email.EmailHelper;
import ru.thendont.software_accounting.service.enums.Urls;
import ru.thendont.software_accounting.service.enums.UserRoles;

import java.util.List;
import java.util.NoSuchElementException;

@Controller
@RequestMapping("/pending-users")
public class PendingUsersPageController {

    @Autowired
    private Logger logger;

    @Autowired
    private BaseCrudService<User> userBaseCrudService;

    @Autowired
    private BaseCrudService<Department> departmentBaseCrudService;

    @Autowired
    private UserService userService;

    @Autowired
    private EmailHelper emailHelper;

    private String username;

    private Long currentUserId;

    @GetMapping("/dashboard")
    public String pendingUsers(@RequestParam Long userId, Model model) {
        try {
            User user = userBaseCrudService.findById(userId).orElseThrow();
            currentUserId = user.getId();
            username = user.getUsername();
            List<User> pendingUsers = userService.findPendingUsers();
            List<Department> departments = departmentBaseCrudService.findAll();
            int pendingUserCount = pendingUsers.size();

            model.addAttribute("user", user);
            model.addAttribute("pendingUsers", pendingUsers);
            model.addAttribute("pendingUserCount", pendingUserCount);
            model.addAttribute("departments", departments);
            return "pending-users";
        }
        catch (NoSuchElementException ex) {
            logger.error("@{}: === ПРОИЗОШЛА ОШИБКА ===", username, ex);
            return ErrorHandler.errorPage("Не найден пользователь", "Пользователь не найден в системе", model);
        }
    }

    @PostMapping("/approve-user")
    public String approveUser(@RequestParam Long userId,
                              @RequestParam String role,
                              @RequestParam(required = false) Long departmentNumber,
                              Model model) {
        try {
            User user = userBaseCrudService.findById(userId).orElseThrow();
            user.setRole(UserRoles.valueOf(UserRoles.class, role));
            user.setDepartment(departmentNumber != null ?
                    departmentBaseCrudService.findById(departmentNumber).orElseThrow() : null);
            userBaseCrudService.save(user);

            emailHelper.sendMessage(user.getEmail(), "Регистрация в системе",
                    "Администратор подтвердил вашу заявку на регистрацию в системе");
            return Urls.PENDING_USERS_URL.getUrlString() + currentUserId;
        }
        catch (NoSuchElementException ex) {
            logger.error("@{}: === ПРОИЗОШЛА ОШИБКА ===", username, ex);
            return ErrorHandler.errorPage("Не найден пользователь или факультет",
                    "Пользователь или факультет не найден в системе", model);
        }
        catch (MailException ex) {
            logger.error("@{}: === ПРОИЗОШЛА ОШИБКА ===", username, ex);
            return ErrorHandler.errorPage("Ошибка отправки сообщения на почту", "Плохое подключение к сети", model);
        }
    }

    @PostMapping("/reject-user/{userId}")
    public String rejectUser(@PathVariable Long userId, Model model) {
        try {
            User user = userBaseCrudService.findById(userId).orElseThrow();
            emailHelper.sendMessage(user.getEmail(), "Регистрация в системе",
                    "Администратор отклонил вашу заявку на регистрацию в системе");
            userBaseCrudService.deleteById(userId);
            return Urls.PENDING_USERS_URL.getUrlString() + currentUserId;
        }
        catch (NoSuchElementException ex) {
            logger.error("@{}: === ПРОИЗОШЛА ОШИБКА ===", username, ex);
            return ErrorHandler.errorPage("Не найден пользователь", "Пользователь не найден в системе", model);
        }
        catch (MailException ex) {
            logger.error("@{}: === ПРОИЗОШЛА ОШИБКА ===", username, ex);
            return ErrorHandler.errorPage("Ошибка отправки сообщения на почту", "Плохое подключение к сети", model);
        }
    }
}