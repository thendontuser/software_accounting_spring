package ru.thendont.software_accounting.controllers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.thendont.software_accounting.entity.*;
import ru.thendont.software_accounting.service.*;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;

@Controller
@RequestMapping("/accountant")
public class AccountantPageController {

    private static final Logger logger = LogManager.getLogger(AccountantPageController.class);

    private final UserService userService;
    private final DepartmentService departmentService;
    private final LicenseService licenseService;
    private final DeveloperService developerService;
    private final SoftwareInstallationService softwareInstallationService;

    public AccountantPageController(UserService userService, DepartmentService departmentService,
                                    LicenseService licenseService, DeveloperService developerService,
                                    SoftwareInstallationService softwareInstallationService) {
        this.userService = userService;
        this.departmentService = departmentService;
        this.licenseService = licenseService;
        this.developerService = developerService;
        this.softwareInstallationService = softwareInstallationService;
    }

    @GetMapping("/dashboard")
    public String showDashboard(@RequestParam Long userId, Model model) {
        try {
            User user = userService.findById(userId).orElseThrow();
            logger.debug("=== ПОЛЬЗОВАТЕЛЬ С ID {} УСПЕШНО НАШЕЛСЯ ===", user.getId());

            List<Department> departments = departmentService.findAll();
            logger.debug("=== СПИСОК ФАКУЛЬТЕТОВ УСПЕШНО НАШЕЛСЯ ===");

            List<License> licenses = licenseService.findAll();
            logger.debug("=== СПИСОК ЛИЦЕНЗИЙ УСПЕШНО НАШЕЛСЯ ===");

            List<Developer> developers = developerService.findAll();
            logger.debug("=== СПИСОК РАЗРАБОТЧИКОВ УСПЕШНО НАШЕЛСЯ ===");

            List<Software> allSoftware = softwareInstallationService.findAllInstalledSoftware();
            logger.debug("=== СПИСОК УСТАНОВЛЕННЫХ ПО УСПЕШНО НАШЕЛСЯ ===");

            model.addAttribute("user", user);
            model.addAttribute("departments", departments);
            model.addAttribute("allLicenses", licenses);
            model.addAttribute("developers", developers);
            model.addAttribute("allSoftware", allSoftware);
            model.addAttribute("filteredSoftware", allSoftware);
            model.addAttribute("totalCost", licenseService.getTotalCost());
            return "accountant-page";
        }
        catch (NoSuchElementException ex) {
            logger.error("=== ПРОИЗОШЛА ОШИБКА ===", ex);
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