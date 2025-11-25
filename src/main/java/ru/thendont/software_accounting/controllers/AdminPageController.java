package ru.thendont.software_accounting.controllers;

import com.itextpdf.text.DocumentException;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.thendont.software_accounting.entity.*;
import ru.thendont.software_accounting.service.*;
import ru.thendont.software_accounting.service.report.ReportService;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;

@Controller
@RequestMapping("/admin")
public class AdminPageController {

    private final UserService userService;
    private final DepartmentService departmentService;
    private final DeveloperService developerService;
    private final DeviceService deviceService;
    private final InstallationRequestService installationRequestService;
    private final LicenseService licenseService;
    private final SoftwareService softwareService;
    private final SoftwareInstallationService softwareInstallationService;

    public AdminPageController(UserService userService,
                               DepartmentService departmentService,
                               DeveloperService developerService,
                               DeviceService deviceService,
                               InstallationRequestService installationRequestService,
                               LicenseService licenseService,
                               SoftwareService softwareService,
                               SoftwareInstallationService softwareInstallationService) {
        this.userService = userService;
        this.departmentService = departmentService;
        this.developerService = developerService;
        this.deviceService = deviceService;
        this.installationRequestService = installationRequestService;
        this.licenseService = licenseService;
        this.softwareService = softwareService;
        this.softwareInstallationService = softwareInstallationService;
    }

    @GetMapping("/dashboard")
    public String showDashboard(@RequestParam Long userId, Model model) {
        try {
            User user = userService.findById(userId).orElseThrow();
            List<Department> departments = departmentService.findAll();
            List<User> users = userService.findAll();
            List<Developer> developers = developerService.findAll();
            List<Device> devices = deviceService.findAll();
            List<InstallationRequest> installationRequests = installationRequestService.findAll();
            List<License> licenses = licenseService.findAll();
            List<Software> softwareList = softwareService.findAll();
            List<SoftwareInstallation> softwareInstallations = softwareInstallationService.findAll();

            model.addAttribute("user", user);
            model.addAttribute("userCount", users.size());
            model.addAttribute("softwareCount", softwareList.size());
            model.addAttribute("departments", departments);
            model.addAttribute("users", users);
            model.addAttribute("devices", devices);
            model.addAttribute("softwareList", softwareList);
            model.addAttribute("developers", developers);
            model.addAttribute("licenses", licenses);
            model.addAttribute("installationRequests", installationRequests);
            model.addAttribute("softwareInstallations", softwareInstallations);
            return "admin-page";
        }
        catch (NoSuchElementException ex) {
            return handleException("Не найден пользователь", "Пользователь не найден в системе", model);
        }
    }

    @GetMapping("/reports/software")
    public void generateReport(@RequestParam String type, @RequestParam(required = false) Long departmentNumber,
                               @RequestParam Long userId, HttpServletResponse response, Model model) {
        try {
            response.setContentType("application/pdf");
            response.setHeader("Content-Disposition", "inline; filename=software_report.pdf");
            if (type.equals("all")) {
                ReportService.generateSoftwareReport(response, softwareInstallationService.findAllInstalledSoftware(),
                        "Полный перечень программного обеспечения");
            } else {
                ReportService.generateSoftwareReport(response,
                        softwareInstallationService.findByDepartmentNumber(departmentNumber),
                        "Перечень программного обеспечения в " +
                                departmentService.findById(departmentNumber).orElse(null).getTitle());
            }
        }
        catch (IOException ex) {
            handleException("Ошибка генерации отчета", "При генерации отчета возникла ошибка", model);
        }
        catch (DocumentException ex) {
            handleException("Ошибка создания документа", "При создании документа возникла ошибка", model);
        }
        catch (NullPointerException ex) {
            handleException("Не найден факультет", "Не найден факультет в системе", model);
        }
    }

    private String handleException(String title, String message, Model model) {
        model.addAttribute("errorTitle", title);
        model.addAttribute("errorMessage", message);
        model.addAttribute("timestamp", LocalDate.now());
        return "error-page";
    }
}