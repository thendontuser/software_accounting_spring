package ru.thendont.software_accounting.controllers;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import ru.thendont.software_accounting.entity.User;
import ru.thendont.software_accounting.service.UserService;
import ru.thendont.software_accounting.service.enums.Urls;

/**
 * Контроллер системы аутентификации и авторизации пользователей в системе
 * @author thendont
 * @version 1.0
 */
@Controller
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private Logger logger;

    @Autowired
    private UserService userService;

    /**
     * Отображает страницу входа в систему
     * @param error хранит ошибку при входе в систему
     * @param logout хранит сообщение о выходе из системы
     * @param registered хранит сообщение об успешной регистрации пользователя в системе
     * @param model экземпляр интерфейса Model для добавления атрибутов в шаблон
     * @param request хранит информацию об http-запросе
     * @return Имя шаблона страницы входа в систему
     */
    @GetMapping("/login")
    public String showLoginPage(@RequestParam(value = "error", required = false) String error,
                                @RequestParam(value = "logout", required = false) String logout,
                                @RequestParam(value = "registered", required = false) String registered,
                                Model model,
                                HttpServletRequest request) {
        logger.info("=== ПЕРЕХОД НА СТРАНИЦУ ВХОДА === ");
        model.addAttribute("user", new User());

        HttpSession session = request.getSession(true);

        if (error != null) {
            model.addAttribute("error", "Неверный логин или пароль");
            logger.warn("=== НЕВЕРНЫЙ ЛОГИН ИЛИ ПАРОЛЬ ===");
        }
        if (logout != null) {
            model.addAttribute("message", "Вы успешно вышли из системы");
            logger.info("=== ПОЛЬЗОВАТЕЛЬ ВЫШЕЛ ИЗ СИСТЕМЫ ===");
        }
        if (registered != null) {
            model.addAttribute("message", "Регистрация успешна! Теперь вы можете войти.");
            logger.info("=== ПОЛЬЗОВАТЕЛЬ ПЕРЕШЕЛ НА СТРАНИЦУ ВХОДА ПОСЛЕ РЕГИСТРАЦИИ ===");
        }
        return "sign-in";
    }

    /**
     * Метод-обработчик входа пользователя в систему. Определяет верность ввода корретных данных пользователя
     * @param authentication хранит данные об аутентификации пользователя
     * @return Страница личного кабинета пользователя, если данные корректны. Страница входа с параметром error, если данные
     * некорректны
     */
    @GetMapping("/login/success")
    public String handleLoginSuccess(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            logger.warn("=== ПОПЫТКА ДОСТУПА К /auth/login/success БЕЗ АУТЕНТИФИКАЦИИ ===");
            return "redirect:/auth/login";
        }
        User user = userService.findByUsername(authentication.getName()).orElse(null);
        if (user == null) {
            logger.error("=== ПОЛЬЗОВАТЕЛЬ {} НЕ НАЙДЕН В БАЗЕ ===", authentication.getName());
            return "redirect:/auth/login?error";
        }
        return getPageFromUser(user);
    }

    /**
     * Отображает страницу регистрации в системе
     * @param model экземпляр интерфейса Model для добавления атрибутов в шаблон
     * @param request хранит информацию об http-запросе
     * @return Шаблон страницы регистрации в системе
     */
    @GetMapping("/register")
    public String showRegisterPage(Model model, HttpServletRequest request) {
        HttpSession session = request.getSession(true);
        model.addAttribute("user", new User());
        logger.info("=== ПЕРЕХОД НА СТРАНИЦУ РЕГИСТРАЦИИ === ");
        return "sign-up";
    }

    /**
     * Метод-обработчик регистрации пользователя в системе
     * @param user пользователь, осуществивший регистрацию в системе
     * @param model экземпляр интерфейса Model для добавления атрибутов в шаблон
     * @return Страница входа в систему с сообщением об успешной регистрации, если данные введены корректно.
     * Страница регистрации, если данные некорректные
     */
    @PostMapping("/register")
    public String showRegisterPage(@ModelAttribute User user, Model model) {
        User u = userService.findByUsername(user.getUsername()).orElse(null);
        if (u != null) {
            logger.warn("=== ПОЛЬЗОВАТЕЛЬ С ID {} УЖЕ СУЩЕСТВУЕТ === ", u.getId());
            model.addAttribute("error", "Логин уже занят");
            return "sign-up";
        }
        userService.hashPassword(user);
        userService.save(user);
        logger.info("=== УСПЕШНОЕ СОХРАНЕНИЕ ПОЛЬЗОВАТЕЛЯ В БАЗУ ДАННЫХ ===");
        return "redirect:/auth/login?registered";
    }

    private String getPageFromUser(User user) {
        return user.getRole() == null ? Urls.VISITOR_URL.getUrlString() + user.getId() :
            switch (user.getRole()) {
                case ADMIN -> Urls.ADMIN_URL.getUrlString() + user.getId();
                case TEACHER -> Urls.TEACHER_URL.getUrlString() + user.getId();
                case HEAD_OF_LABORATORIES -> Urls.HEAD_OF_LABORATORIES_URL.getUrlString() + user.getId();
                case LABORATORY_ASSISTANT -> Urls.LABORATORY_ASSISTANT.getUrlString() + user.getId();
                case SAM_MANAGER -> Urls.SAM_MANAGER.getUrlString() + user.getId();
            };
    }
}