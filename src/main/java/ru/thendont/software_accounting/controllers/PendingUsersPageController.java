package ru.thendont.software_accounting.controllers;

import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.thendont.software_accounting.entity.Kafedra;
import ru.thendont.software_accounting.entity.User;
import ru.thendont.software_accounting.error.ErrorHandler;
import ru.thendont.software_accounting.service.KafedraService;
import ru.thendont.software_accounting.service.UserService;
import ru.thendont.software_accounting.service.email.EmailService;
import ru.thendont.software_accounting.service.enums.Urls;
import ru.thendont.software_accounting.service.enums.UserRoles;
import ru.thendont.software_accounting.util.ConstantStrings;

import java.util.List;
import java.util.NoSuchElementException;

/**
 * Контроллер страницы подтверждения регистрации пользователей
 * @author thendont
 * @version 1.0
 */
@Controller
@RequestMapping("/pending-users")
public class PendingUsersPageController {

    @Autowired
    private Logger logger;

    @Autowired
    private KafedraService kafedraService;

    @Autowired
    private UserService userService;

    @Autowired
    private EmailService emailService;

    private String username;

    private Long currentUserId;

    /**
     * Отображает html-страницу подтверждения регистрации пользователй
     * @param userId идентификатор пользователя, вошедшего на страницу
     * @param model экземпляр интерфейса Model для добавления атрибутов в шаблон
     * @return Имя шаблона страницы подтверждения регистрации пользователй
     */
    @GetMapping("/dashboard")
    public String pendingUsers(@RequestParam Long userId, Model model) {
        try {
            User user = userService.findById(userId).orElseThrow();
            currentUserId = user.getId();
            username = user.getUsername();
            List<User> pendingUsers = userService.findPendingUsers();
            List<Kafedra> kafedraList = kafedraService.findAll();
            int pendingUserCount = pendingUsers.size();

            model.addAttribute("user", user);
            model.addAttribute("pendingUsers", pendingUsers);
            model.addAttribute("pendingUserCount", pendingUserCount);
            model.addAttribute("kafedraList", kafedraList);
            return "pending-users";
        }
        catch (NoSuchElementException ex) {
            logger.error("@{}: === ПРОИЗОШЛА ОШИБКА ===", username, ex);
            return ErrorHandler.errorPage(ConstantStrings.USER_NOT_FOUND_TITLE, ConstantStrings.USER_NOT_FOUND_MESSAGE, model);
        }
    }

    /**
     * Метод для подтверждения регистрации пользователя. Сохраняет роль и привязку к кафедре(в зависимости от роли) пользователя
     * @param userId идентификатор сохраняемого пользователя
     * @param role роль пользователя
     * @param kafId идентификатор кафедры
     * @param model экземпляр интерфейса Model для добавления атрибутов в шаблон
     * @return Url-адрес страницы подтверждения регистрации пользователей
     */
    @PostMapping("/approve-user")
    public String approveUser(@RequestParam Long userId,
                              @RequestParam String role,
                              @RequestParam(required = false) Long kafId,
                              Model model) {
        try {
            User user = userService.findById(userId).orElseThrow();
            user.setRole(UserRoles.valueOf(UserRoles.class, role));
            user.setKafedra(kafId != null ? kafedraService.findById(kafId).orElseThrow() : null);
            userService.save(user);

            emailService.sendMessage(user.getEmail(), ConstantStrings.REGISTER_IN_SYSTEM,
                    "Администратор подтвердил вашу заявку на регистрацию в системе");
            return Urls.PENDING_USERS_URL.getUrlString() + currentUserId;
        }
        catch (NoSuchElementException ex) {
            logger.error("@{}: === ПРОИЗОШЛА ОШИБКА ===", username, ex);
            return ErrorHandler.errorPage(ConstantStrings.OBJECT_NOT_FOUND_TITLE, ConstantStrings.OBJECT_NOT_FOUND_MESSAGE, model);
        }
        catch (MailException ex) {
            logger.error("@{}: === ПРОИЗОШЛА ОШИБКА ===", username, ex);
            return ErrorHandler.errorPage(ConstantStrings.EMAIL_ERROR_TITLE, ConstantStrings.EMAIL_ERROR_MESSAGE, model);
        }
    }

    /**
     * Метод для отклонения регистрации пользователя
     * @param userId идентификатор пользователя, которогло отклоняем
     * @param model экземпляр интерфейса Model для добавления атрибутов в шаблон
     * @return Url-адрес страницы подтверждения регистрации пользователей
     */
    @PostMapping("/reject-user/{userId}")
    public String rejectUser(@PathVariable Long userId, Model model) {
        try {
            User user = userService.findById(userId).orElseThrow();
            emailService.sendMessage(user.getEmail(), ConstantStrings.REGISTER_IN_SYSTEM,
                    "Администратор отклонил вашу заявку на регистрацию в системе");
            userService.deleteById(userId);
            return Urls.PENDING_USERS_URL.getUrlString() + currentUserId;
        }
        catch (NoSuchElementException ex) {
            logger.error("@{}: === ПРОИЗОШЛА ОШИБКА ===", username, ex);
            return ErrorHandler.errorPage(ConstantStrings.USER_NOT_FOUND_TITLE, ConstantStrings.USER_NOT_FOUND_MESSAGE, model);
        }
        catch (MailException ex) {
            logger.error("@{}: === ПРОИЗОШЛА ОШИБКА ===", username, ex);
            return ErrorHandler.errorPage(ConstantStrings.EMAIL_ERROR_TITLE, ConstantStrings.EMAIL_ERROR_MESSAGE, model);
        }
    }
}