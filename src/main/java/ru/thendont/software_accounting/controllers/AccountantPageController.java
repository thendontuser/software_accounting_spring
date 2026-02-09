package ru.thendont.software_accounting.controllers;

import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.thendont.software_accounting.entity.*;
import ru.thendont.software_accounting.error.ErrorHandler;
import ru.thendont.software_accounting.service.*;

import java.util.List;
import java.util.NoSuchElementException;

@Controller
@RequestMapping("/accountant")
public class AccountantPageController {

    @Autowired
    private Logger logger;

    private String username;

    @Autowired
    private UserService userService;

    @Autowired
    private DepartmentService departmentService;

    @Autowired
    private LicenseService licenseService;

    @Autowired
    private DeveloperService developerService;

    @Autowired
    private SoftwareInstallationService softwareInstallationService;

    @GetMapping("/dashboard")
    public String showDashboard(@RequestParam Long userId, Model model) {
        try {
            User user = userService.findById(userId).orElseThrow();
            username = user.getUsername();

            List<Department> departments = departmentService.findAll();
            List<License> licenses = licenseService.findAll();
            List<Developer> developers = developerService.findAll();
            List<Software> allSoftware = softwareInstallationService.findAllInstalledSoftware();

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
            logger.error("@{}: === ПРОИЗОШЛА ОШИБКА ===", username, ex);
            return ErrorHandler.errorPage("Не найден объект", ex.getMessage(), model);
        }
    }
}