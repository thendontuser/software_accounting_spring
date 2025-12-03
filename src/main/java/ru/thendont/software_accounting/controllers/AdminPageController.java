package ru.thendont.software_accounting.controllers;

import com.itextpdf.text.DocumentException;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.thendont.software_accounting.entity.*;
import ru.thendont.software_accounting.service.*;
import ru.thendont.software_accounting.service.report.ReportService;
import ru.thendont.software_accounting.util.Urls;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;

@Controller
@RequestMapping("/admin")
public class AdminPageController {

    private static final Logger logger = LogManager.getLogger(AdminPageController.class);

    private final UserService userService;
    private final DepartmentService departmentService;
    private final DeveloperService developerService;
    private final DeviceService deviceService;
    private final InstallationRequestService installationRequestService;
    private final LicenseService licenseService;
    private final SoftwareService softwareService;
    private final SoftwareInstallationService softwareInstallationService;

    private Long currentUserId;

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
        if (currentUserId == null) {
            currentUserId = userId;
        }
        try {
            User user = userService.findById(userId).orElseThrow();
            logger.debug("=== ПОЛЬЗОВАТЕЛЬ С ID {} УСПЕШНО НАШЕЛСЯ ===", user.getId());

            List<Department> departments = departmentService.findAll();
            logger.debug("=== СПИСОК ФАКУЛЬТЕТОВ УСПЕШНО НАШЕЛСЯ ===");

            List<User> users = userService.findAll();
            logger.debug("=== СПИСОК ПОЛЬЗОВАТЕЛЕЙ УСПЕШНО НАШЕЛСЯ ===");

            List<Developer> developers = developerService.findAll();
            logger.debug("=== СПИСОК РАЗРАБОТЧИКОВ УСПЕШНО НАШЕЛСЯ ===");

            List<Device> devices = deviceService.findAll();
            logger.debug("=== СПИСОК УСТРОЙСТВ УСПЕШНО НАШЕЛСЯ ===");

            List<InstallationRequest> installationRequests = installationRequestService.findAll();
            logger.debug("=== СПИСОК ЗАЯВОК УСПЕШНО НАШЕЛСЯ ===");

            List<License> licenses = licenseService.findAll();
            logger.debug("=== СПИСОК ЛИЦЕНЗИЙ УСПЕШНО НАШЕЛСЯ ===");

            List<Software> softwareList = softwareService.findAll();
            logger.debug("=== СПИСОК ПО УСПЕШНО НАШЕЛСЯ ===");

            List<SoftwareInstallation> softwareInstallations = softwareInstallationService.findAll();
            logger.debug("=== СПИСОК УСТАНОВОК ПО УСПЕШНО НАШЕЛСЯ ===");

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
            logger.error("=== ПРОИЗОШЛА ОШИБКА ===", ex);
            return handleException("Не найден пользователь", "Пользователь не найден в системе", model);
        }
    }

    @GetMapping("/reports/software")
    public void generateReport(@RequestParam String type, @RequestParam(required = false) Long departmentNumber,
                               HttpServletResponse response, Model model) {
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
            logger.error("=== ПРОИЗОШЛА ОШИБКА ===", ex);
            handleException("Ошибка генерации отчета", "При генерации отчета возникла ошибка", model);
        }
        catch (DocumentException ex) {
            logger.error("=== ПРОИЗОШЛА ОШИБКА ===", ex);
            handleException("Ошибка создания документа", "При создании документа возникла ошибка", model);
        }
        catch (NullPointerException ex) {
            logger.error("=== ПРОИЗОШЛА ОШИБКА ===", ex);
            handleException("Не найден факультет", "Не найден факультет в системе", model);
        }
    }

// Для сущности user ==========================================================================================
    @GetMapping("/edit/users/{id}")
    public String editUser(@PathVariable Long id, Model model) {
        try {
            model.addAttribute("user", userService.findById(id).orElseThrow());
            model.addAttribute("departments", departmentService.findAll());
            model.addAttribute("currentUserId", currentUserId);
            logger.debug("=== НАЧАЛО РЕДАКТИРОВАНИЯ ПОЛЬЗОВАТЕЛЯ ===");
            return "edit-user";
        }
        catch (NoSuchElementException ex) {
            logger.error("=== ПРОИЗОШЛА ОШИБКА ===", ex);
            return handleException("Не найден пользователь", "Пользователь не найден в системе", model);
        }
    }

    @PostMapping("/edit/users")
    public String editUser(@ModelAttribute User user, Model model) {
        logger.debug("=== ВЫЗОВ МЕТОДА СОХРАНЕНИЯ ДАННЫХ ПОЛЬЗОВАТЕЛЯ ===");
        userService.save(user);
        logger.debug("=== ПОЛЬЗОВАТЕЛЬ УСПЕШНО СОХРАНИЛСЯ В БАЗЕ ===");
        return Urls.ADMIN_URL + currentUserId;
    }

    @PostMapping("/delete/users/{id}")
    public String deleteUser(@PathVariable Long id, Model model) {
        logger.debug("=== ВЫЗОВ МЕТОДА УДАЛЕНИЯ С ID {} ПОЛЬЗОВАТЕЛЯ ===", id);
        userService.deleteById(id);
        logger.debug("=== ПОЛЬЗОВАТЕЛЬ УСПЕШНО УДАЛИЛСЯ ИЗ БАЗЫ ===");
        return Urls.ADMIN_URL + currentUserId;
    }
// Для сущности user ==========================================================================================

// Для сущности department ==========================================================================================
    @GetMapping("/edit/departments/{id}")
    public String editDepartment(@PathVariable Long id, Model model) {
        try {
            model.addAttribute("department", isNewRecord(id) ? new Department() :
                    departmentService.findById(id).orElseThrow());
            model.addAttribute("currentUserId", currentUserId);
            logger.debug("=== НАЧАЛО РЕДАКТИРОВАНИЯ ФАКУЛЬТЕТА ===");
            return "edit-department";
        }
        catch (NoSuchElementException ex) {
            logger.error("=== ПРОИЗОШЛА ОШИБКА ===", ex);
            return handleException("Не найден факультет", "Факультет не найден в системе", model);
        }
    }

    @PostMapping("/edit/departments")
    public String editDepartment(@ModelAttribute Department department, Model model) {
        logger.debug("=== ВЫЗОВ МЕТОДА СОХРАНЕНИЯ ДАННЫХ ФАКУЛЬТЕТА ===");
        departmentService.save(department);
        logger.debug("=== УСПЕШНОЕ СОХРАНЕНИЕ ДАННЫХ ФАКУЛЬТЕТА ===");
        return Urls.ADMIN_URL + currentUserId;
    }

    @PostMapping("/delete/departments/{id}")
    public String deleteDepartment(@PathVariable Long id, Model model) {
        logger.debug("=== ВЫЗОВ МЕТОДА УДАЛЕНИЯ ФАКУЛЬТЕТА ===");
        departmentService.deleteById(id);
        logger.debug("=== УСПЕШНОЕ УДАЛЕНИЕ ФАКУЛЬТЕТА ===");
        return Urls.ADMIN_URL + currentUserId;
    }
// Для сущности department ==========================================================================================

// Для сущности device ==========================================================================================
    @GetMapping("/edit/devices/{id}")
    public String editDevice(@PathVariable Long id, Model model) {
        try {
            model.addAttribute("device", isNewRecord(id) ? new Device() : deviceService.findById(id).orElseThrow());
            model.addAttribute("departments", departmentService.findAll());
            model.addAttribute("currentUserId", currentUserId);
            logger.debug("=== НАЧАЛО РЕДАКТИРОВАНИЯ УСТРОЙСТВА ===");
            return "edit-device";
        }
        catch (NoSuchElementException ex) {
            logger.error("=== ПРОИЗОШЛА ОШИБКА ===", ex);
            return handleException("Не найдено устройство", "Устройство не найдено в системе", model);
        }
    }

    @PostMapping("/edit/devices")
    public String editDevice(@ModelAttribute Device device, Model model) {
        logger.debug("=== ВЫЗОВ МЕТОДА СОХРАНЕНИЯ ДАННЫХ УСТРОЙСТВА ===");
        deviceService.save(device);
        logger.debug("=== УСПЕШНОЕ СОХРАНЕНИЕ ДАННЫХ УСТРОЙСТВА ===");
        return Urls.ADMIN_URL + currentUserId;
    }

    @PostMapping("/delete/devices/{id}")
    public String deleteDevice(@PathVariable Long id, Model model) {
        logger.debug("=== ВЫЗОВ МЕТОДА УДАЛЕНИЯ УСТРОЙСТВА ===");
        deviceService.deleteById(id);
        logger.debug("=== УСПЕШНОЕ УДАЛЕНИЕ УСТРОЙСТВА ===");
        return Urls.ADMIN_URL + currentUserId;
    }
// Для сущности device ==========================================================================================

// Для сущности software ==========================================================================================
    @GetMapping("/edit/software/{id}")
    public String editSoftware(@PathVariable Long id, Model model) {
        try {
            model.addAttribute("software", isNewRecord(id) ? new Software() :
                    softwareService.findById(id).orElseThrow());
            model.addAttribute("developers", developerService.findAll());
            model.addAttribute("currentUserId", currentUserId);
            logger.debug("=== НАЧАЛО РЕДАКТИРОВАНИЯ ПО ===");
            return "edit-software";
        }
        catch (NoSuchElementException ex) {
            logger.error("=== ПРОИЗОШЛА ОШИБКА ===", ex);
            return handleException("Не найдено программное обеспечение", "Программное обеспечение не найдено в системе", model);
        }
    }

    @PostMapping("/edit/software")
    public String editSoftware(@ModelAttribute Software software, Model model) {
        logger.debug("=== ВЫЗОВ МЕТОДА СОХРАНЕНИЯ ДАННЫХ ПО ===");
        softwareService.save(software);
        logger.debug("=== УСПЕШНОЕ СОХРАНЕНИЕ ДАННЫХ ПО ===");
        return Urls.ADMIN_URL + currentUserId;
    }

    @PostMapping("/delete/software/{id}")
    public String deleteSoftware(@PathVariable Long id, Model model) {
        logger.debug("=== ВЫЗОВ МЕТОДА УДАЛЕНИЯ ПО ===");
        softwareService.deleteById(id);
        logger.debug("=== УСПЕШНОЕ УДАЛЕНИЕ ПО ===");
        return Urls.ADMIN_URL + currentUserId;
    }
// Для сущности software ==========================================================================================

// Для сущности developer ==========================================================================================
    @GetMapping("/edit/developers/{id}")
    public String editDeveloper(@PathVariable Long id, Model model) {
        try {
            model.addAttribute("developer", isNewRecord(id) ? new Developer() :
                    developerService.findById(id).orElseThrow());
            model.addAttribute("currentUserId", currentUserId);
            logger.debug("=== НАЧАЛО РЕДАКТИРОВАНИЯ РАЗРАБОТЧИКА ===");
            return "edit-developer";
        }
        catch (NoSuchElementException ex) {
            logger.error("=== ПРОИЗОШЛА ОШИБКА ===", ex);
            return handleException("Не найден разработчик", "Разработчик не найден в системе", model);
        }
    }

    @PostMapping("/edit/developers")
    public String editDeveloper(@ModelAttribute Developer developer, Model model) {
        logger.debug("=== ВЫЗОВ МЕТОДА СОХРАНЕНИЯ ДАННЫХ РАЗРАБОТЧИКА ===");
        developerService.save(developer);
        logger.debug("=== УСПЕШНОЕ СОХРАНЕНИЕ ДАННЫХ РАЗРАБОТЧИКА ===");
        return Urls.ADMIN_URL + currentUserId;
    }

    @PostMapping("/delete/developers/{id}")
    public String deleteDeveloper(@PathVariable Long id, Model model) {
        logger.debug("=== ВЫЗОВ МЕТОДА УДАЛЕНИЯ РАЗРАБОТЧИКА ===");
        developerService.deleteById(id);
        logger.debug("=== УСПЕШНОЕ УДАЛЕНИЕ РАЗРАБОТЧИКА ===");
        return Urls.ADMIN_URL + currentUserId;
    }
// Для сущности developer ==========================================================================================

// Для сущности license ==========================================================================================
    @GetMapping("/edit/licenses/{id}")
    public String editLicense(@PathVariable Long id, Model model) {
        try {
            model.addAttribute("license", isNewRecord(id) ? new License() :
                    licenseService.findById(id).orElseThrow());
            model.addAttribute("software", softwareService.findAll());
            model.addAttribute("currentUserId", currentUserId);
            logger.debug("=== НАЧАЛО РЕДАКТИРОВАНИЯ ЛИЦЕНЗИИ ===");
            return "edit-license";
        }
        catch (NoSuchElementException ex) {
            logger.error("=== ПРОИЗОШЛА ОШИБКА ===", ex);
            return handleException("Не найдена лицензия", "Лицензия не найдена в системе", model);
        }
    }

    @PostMapping("/edit/licenses")
    public String editLicense(@ModelAttribute License license, Model model) {
        logger.debug("=== ВЫЗОВ МЕТОДА СОХРАНЕНИЯ ДАННЫХ ЛИЦЕНЗИИ ===");
        licenseService.save(license);
        logger.debug("=== УСПЕШНОЕ СОХРАНЕНИЕ ДАННЫХ ЛИЦЕНЗИИ ===");
        return Urls.ADMIN_URL + currentUserId;
    }

    @PostMapping("/delete/licenses/{id}")
    public String deleteLicense(@PathVariable Long id, Model model) {
        logger.debug("=== ВЫЗОВ МЕТОДА УДАЛЕНИЯ ЛИЦЕНЗИИ ===");
        licenseService.deleteById(id);
        logger.debug("=== УСПЕШНОЕ УДАЛЕНИЕ ЛИЦЕНЗИИ ===");
        return Urls.ADMIN_URL + currentUserId;
    }
// Для сущности license ==========================================================================================

// Для сущности InstallationRequest ==========================================================================================
    @GetMapping("/edit/installation_requests/{id}")
    public String editInstallationRequest(@PathVariable Long id, Model model) {
        try {
            model.addAttribute("request", installationRequestService.findById(id).orElseThrow());
            model.addAttribute("software", softwareService.findAll());
            model.addAttribute("devices", deviceService.findAll());
            model.addAttribute("users", userService.findAll());
            model.addAttribute("currentUserId", currentUserId);
            logger.debug("=== НАЧАЛО РЕДАКТИРОВАНИЯ ЗАЯВКИ ===");
            return "edit-installation-request";
        }
        catch (NoSuchElementException ex) {
            logger.error("=== ПРОИЗОШЛА ОШИБКА ===", ex);
            return handleException("Не найдена заявка на установку ПО", "Заявка не найдена в системе", model);
        }
    }

    @PostMapping("/edit/installation_requests")
    public String editInstallationRequest(@ModelAttribute InstallationRequest installationRequest, Model model) {
        logger.debug("=== ВЫЗОВ МЕТОДА СОХРАНЕНИЯ ДАННЫХ ЗАЯВКИ ===");
        installationRequestService.save(installationRequest);
        logger.debug("=== УСПЕШНОЕ СОХРАНЕНИЕ ДАННЫХ ЗАЯВКИ ===");
        return Urls.ADMIN_URL + currentUserId;
    }

    @PostMapping("/delete/installation_requests/{id}")
    public String deleteInstallationRequest(@PathVariable Long id, Model model) {
        logger.debug("=== ВЫЗОВ МЕТОДА УДАЛЕНИЯ ЗАЯВКИ ===");
        installationRequestService.deleteById(id);
        logger.debug("=== УСПЕШНОЕ УДАЛЕНИЕ ЗАЯВКИ ===");
        return Urls.ADMIN_URL + currentUserId;
    }
// Для сущности InstallationRequest ==========================================================================================

// Для сущности SoftwareInstallation ==========================================================================================
    @GetMapping("/edit/software_installations/{id}")
    public String editSoftwareInstallation(@PathVariable Long id, Model model) {
        try {
            model.addAttribute("installation", isNewRecord(id) ? new SoftwareInstallation() :
                    softwareInstallationService.findById(id).orElseThrow());
            model.addAttribute("software", softwareService.findAll());
            model.addAttribute("devices", deviceService.findAll());
            model.addAttribute("users", userService.findAll());
            model.addAttribute("currentUserId", currentUserId);
            logger.debug("=== НАЧАЛО РЕДАКТИРОВАНИЯ УСТАНОВКИ ПО ===");
            return "edit-software-installation";
        }
        catch (NoSuchElementException ex) {
            logger.error("=== ПРОИЗОШЛА ОШИБКА ===", ex);
            return handleException("Не найдена установка", "Установка не найдена в системе", model);
        }
    }

    @PostMapping("/edit/software_installations")
    public String editSoftwareInstallation(@ModelAttribute SoftwareInstallation softwareInstallation, Model model) {
        logger.debug("=== ВЫЗОВ МЕТОДА СОХРАНЕНИЯ ДАННЫХ УСТАНОВКИ ПО ===");
        softwareInstallationService.save(softwareInstallation);
        logger.debug("=== УСПЕШНОЕ СОХРАНЕНИЕ ДАННЫХ УСТАНОВКИ ПО ===");
        return Urls.ADMIN_URL + currentUserId;
    }

    @PostMapping("/delete/software_installations/{id}")
    public String deleteSoftwareInstallation(@PathVariable Long id, Model model) {
        logger.debug("=== ВЫЗОВ МЕТОДА УДАЛЕНИЯ УСТАНОВКИ ПО ===");
        softwareInstallationService.deleteById(id);
        logger.debug("=== УСПЕШНОЕ УДАЛЕНИЕ УСТАНОВКИ ПО ===");
        return Urls.ADMIN_URL + currentUserId;
    }
// Для сущности SoftwareInstallation ==========================================================================================

    private boolean isNewRecord(Long id) {
        return id == 0;
    }

    private String handleException(String title, String message, Model model) {
        model.addAttribute("errorTitle", title);
        model.addAttribute("errorMessage", message);
        model.addAttribute("timestamp", LocalDate.now());
        return "error-page";
    }
}