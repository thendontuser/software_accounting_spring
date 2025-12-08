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
import ru.thendont.software_accounting.util.ConstantStrings;
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
    private String username;

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
            username = user.getLogin();
            logger.info("@{}: === ПОЛЬЗОВАТЕЛЬ С ID {} УСПЕШНО НАШЕЛСЯ ===", username, user.getId());

            List<Department> departments = departmentService.findAll();
            logger.info("@{}: === СПИСОК ФАКУЛЬТЕТОВ УСПЕШНО НАШЕЛСЯ ===", username);

            List<User> users = userService.findAll();
            logger.info("@{}: === СПИСОК ПОЛЬЗОВАТЕЛЕЙ УСПЕШНО НАШЕЛСЯ ===", username);

            List<Developer> developers = developerService.findAll();
            logger.info("@{}: === СПИСОК РАЗРАБОТЧИКОВ УСПЕШНО НАШЕЛСЯ ===", username);

            List<Device> devices = deviceService.findAll();
            logger.info("@{}: === СПИСОК УСТРОЙСТВ УСПЕШНО НАШЕЛСЯ ===", username);

            List<InstallationRequest> installationRequests = installationRequestService.findAll();
            logger.info("@{}: === СПИСОК ЗАЯВОК УСПЕШНО НАШЕЛСЯ ===", username);

            List<License> licenses = licenseService.findAll();
            logger.info("@{}: === СПИСОК ЛИЦЕНЗИЙ УСПЕШНО НАШЕЛСЯ ===", username);

            List<Software> softwareList = softwareService.findAll();
            logger.info("@{}: === СПИСОК ПО УСПЕШНО НАШЕЛСЯ ===", username);

            List<SoftwareInstallation> softwareInstallations = softwareInstallationService.findAll();
            logger.info("@{}: === СПИСОК УСТАНОВОК ПО УСПЕШНО НАШЕЛСЯ ===", username);

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
            logger.error("@{}: === ПРОИЗОШЛА ОШИБКА ===", username, ex);
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
            logger.error("@{}: === ПРОИЗОШЛА ОШИБКА ===", username, ex);
            handleException("Ошибка генерации отчета", "При генерации отчета возникла ошибка", model);
        }
        catch (DocumentException ex) {
            logger.error("@{}: === ПРОИЗОШЛА ОШИБКА ===", username, ex);
            handleException("Ошибка создания документа", "При создании документа возникла ошибка", model);
        }
        catch (NullPointerException ex) {
            logger.error("@{}: === ПРОИЗОШЛА ОШИБКА ===", username, ex);
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
            logger.info("@{}: === НАЧАЛО РЕДАКТИРОВАНИЯ ПОЛЬЗОВАТЕЛЯ ===", username);
            return "edit-user";
        }
        catch (NoSuchElementException ex) {
            logger.error("@{}: === ПРОИЗОШЛА ОШИБКА ===", username, ex);
            return handleException("Не найден пользователь", "Пользователь не найден в системе", model);
        }
    }

    @PostMapping("/edit/users")
    public String editUser(@ModelAttribute User user, Model model) {
        logger.info("@{}: === ВЫЗОВ МЕТОДА СОХРАНЕНИЯ ДАННЫХ ПОЛЬЗОВАТЕЛЯ ===", username);
        userService.save(user);
        logger.info("@{}: === ПОЛЬЗОВАТЕЛЬ УСПЕШНО СОХРАНИЛСЯ В БАЗЕ ===", username);
        return Urls.ADMIN_URL + currentUserId;
    }

    @PostMapping("/delete/users/{id}")
    public String deleteUser(@PathVariable Long id, Model model) {
        logger.info("@{}: === ВЫЗОВ МЕТОДА УДАЛЕНИЯ С ID {} ПОЛЬЗОВАТЕЛЯ ===", username, id);
        userService.deleteById(id);
        logger.info("@{}: === ПОЛЬЗОВАТЕЛЬ УСПЕШНО УДАЛИЛСЯ ИЗ БАЗЫ ===", username);
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
            logger.info("@{}: === НАЧАЛО РЕДАКТИРОВАНИЯ ФАКУЛЬТЕТА ===", username);
            return "edit-department";
        }
        catch (NoSuchElementException ex) {
            logger.error("@{}: === ПРОИЗОШЛА ОШИБКА ===", username, ex);
            return handleException("Не найден факультет", "Факультет не найден в системе", model);
        }
    }

    @PostMapping("/edit/departments")
    public String editDepartment(@ModelAttribute Department department, Model model) {
        logger.info("@{}: === ВЫЗОВ МЕТОДА СОХРАНЕНИЯ ДАННЫХ ФАКУЛЬТЕТА ===", username);
        departmentService.save(department);
        logger.info("@{}: === УСПЕШНОЕ СОХРАНЕНИЕ ДАННЫХ ФАКУЛЬТЕТА ===", username);
        return Urls.ADMIN_URL + currentUserId;
    }

    @PostMapping("/delete/departments/{id}")
    public String deleteDepartment(@PathVariable Long id, Model model) {
        logger.info("@{}: === ВЫЗОВ МЕТОДА УДАЛЕНИЯ ФАКУЛЬТЕТА ===", username);
        departmentService.deleteById(id);
        logger.info("@{}: === УСПЕШНОЕ УДАЛЕНИЕ ФАКУЛЬТЕТА ===", username);
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
            logger.info("@{}: === НАЧАЛО РЕДАКТИРОВАНИЯ УСТРОЙСТВА ===", username);
            return "edit-device";
        }
        catch (NoSuchElementException ex) {
            logger.error("@{}: === ПРОИЗОШЛА ОШИБКА ===", username, ex);
            return handleException("Не найдено устройство", "Устройство не найдено в системе", model);
        }
    }

    @PostMapping("/edit/devices")
    public String editDevice(@ModelAttribute Device device, Model model) {
        logger.info("@{}: === ВЫЗОВ МЕТОДА СОХРАНЕНИЯ ДАННЫХ УСТРОЙСТВА ===", username);
        deviceService.save(device);
        logger.info("@{}: === УСПЕШНОЕ СОХРАНЕНИЕ ДАННЫХ УСТРОЙСТВА ===", username);
        return Urls.ADMIN_URL + currentUserId;
    }

    @PostMapping("/delete/devices/{id}")
    public String deleteDevice(@PathVariable Long id, Model model) {
        logger.info("@{}: === ВЫЗОВ МЕТОДА УДАЛЕНИЯ УСТРОЙСТВА ===", username);
        deviceService.deleteById(id);
        logger.info("@{}: === УСПЕШНОЕ УДАЛЕНИЕ УСТРОЙСТВА ===", username);
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
            logger.info("@{}: === НАЧАЛО РЕДАКТИРОВАНИЯ ПО ===", username);
            return "edit-software";
        }
        catch (NoSuchElementException ex) {
            logger.error("@{}: === ПРОИЗОШЛА ОШИБКА ===", username, ex);
            return handleException("Не найдено программное обеспечение", "Программное обеспечение не найдено в системе", model);
        }
    }

    @PostMapping("/edit/software")
    public String editSoftware(@ModelAttribute Software software, Model model) {
        logger.info("@{}: === ВЫЗОВ МЕТОДА СОХРАНЕНИЯ ДАННЫХ ПО ===", username);
        software.setLogoPath(ConstantStrings.LOGO_DIRECTORY_PATH + software.getLogoPath());
        softwareService.save(software);
        logger.info("@{}: === УСПЕШНОЕ СОХРАНЕНИЕ ДАННЫХ ПО ===", username);
        return Urls.ADMIN_URL + currentUserId;
    }

    @PostMapping("/delete/software/{id}")
    public String deleteSoftware(@PathVariable Long id, Model model) {
        logger.info("@{}: === ВЫЗОВ МЕТОДА УДАЛЕНИЯ ПО ===", username);
        softwareService.deleteById(id);
        logger.info("@{}: === УСПЕШНОЕ УДАЛЕНИЕ ПО ===", username);
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
            logger.info("@{}: === НАЧАЛО РЕДАКТИРОВАНИЯ РАЗРАБОТЧИКА ===", username);
            return "edit-developer";
        }
        catch (NoSuchElementException ex) {
            logger.error("@{}: === ПРОИЗОШЛА ОШИБКА ===", username, ex);
            return handleException("Не найден разработчик", "Разработчик не найден в системе", model);
        }
    }

    @PostMapping("/edit/developers")
    public String editDeveloper(@ModelAttribute Developer developer, Model model) {
        logger.info("@{}: === ВЫЗОВ МЕТОДА СОХРАНЕНИЯ ДАННЫХ РАЗРАБОТЧИКА ===", username);
        developerService.save(developer);
        logger.info("@{}: === УСПЕШНОЕ СОХРАНЕНИЕ ДАННЫХ РАЗРАБОТЧИКА ===", username);
        return Urls.ADMIN_URL + currentUserId;
    }

    @PostMapping("/delete/developers/{id}")
    public String deleteDeveloper(@PathVariable Long id, Model model) {
        logger.info("@{}: === ВЫЗОВ МЕТОДА УДАЛЕНИЯ РАЗРАБОТЧИКА ===", username);
        developerService.deleteById(id);
        logger.info("@{}: === УСПЕШНОЕ УДАЛЕНИЕ РАЗРАБОТЧИКА ===", username);
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
            logger.info("@{}: === НАЧАЛО РЕДАКТИРОВАНИЯ ЛИЦЕНЗИИ ===", username);
            return "edit-license";
        }
        catch (NoSuchElementException ex) {
            logger.error("@{}: === ПРОИЗОШЛА ОШИБКА ===", username, ex);
            return handleException("Не найдена лицензия", "Лицензия не найдена в системе", model);
        }
    }

    @PostMapping("/edit/licenses")
    public String editLicense(@ModelAttribute License license, Model model) {
        logger.info("@{}: === ВЫЗОВ МЕТОДА СОХРАНЕНИЯ ДАННЫХ ЛИЦЕНЗИИ ===", username);
        licenseService.save(license);
        logger.info("@{}: === УСПЕШНОЕ СОХРАНЕНИЕ ДАННЫХ ЛИЦЕНЗИИ ===", username);
        return Urls.ADMIN_URL + currentUserId;
    }

    @PostMapping("/delete/licenses/{id}")
    public String deleteLicense(@PathVariable Long id, Model model) {
        logger.info("@{}: === ВЫЗОВ МЕТОДА УДАЛЕНИЯ ЛИЦЕНЗИИ ===", username);
        licenseService.deleteById(id);
        logger.info("@{}: === УСПЕШНОЕ УДАЛЕНИЕ ЛИЦЕНЗИИ ===", username);
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
            logger.info("@{}: === НАЧАЛО РЕДАКТИРОВАНИЯ ЗАЯВКИ ===", username);
            return "edit-installation-request";
        }
        catch (NoSuchElementException ex) {
            logger.error("@{}: === ПРОИЗОШЛА ОШИБКА ===", username, ex);
            return handleException("Не найдена заявка на установку ПО", "Заявка не найдена в системе", model);
        }
    }

    @PostMapping("/edit/installation_requests")
    public String editInstallationRequest(@ModelAttribute InstallationRequest installationRequest, Model model) {
        logger.info("@{}: === ВЫЗОВ МЕТОДА СОХРАНЕНИЯ ДАННЫХ ЗАЯВКИ ===", username);
        installationRequestService.save(installationRequest);
        logger.info("@{}: === УСПЕШНОЕ СОХРАНЕНИЕ ДАННЫХ ЗАЯВКИ ===", username);
        return Urls.ADMIN_URL + currentUserId;
    }

    @PostMapping("/delete/installation_requests/{id}")
    public String deleteInstallationRequest(@PathVariable Long id, Model model) {
        logger.info("@{}: === ВЫЗОВ МЕТОДА УДАЛЕНИЯ ЗАЯВКИ ===", username);
        installationRequestService.deleteById(id);
        logger.info("@{}: === УСПЕШНОЕ УДАЛЕНИЕ ЗАЯВКИ ===", username);
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
            logger.info("@{}: === НАЧАЛО РЕДАКТИРОВАНИЯ УСТАНОВКИ ПО ===", username);
            return "edit-software-installation";
        }
        catch (NoSuchElementException ex) {
            logger.error("@{}: === ПРОИЗОШЛА ОШИБКА ===", username, ex);
            return handleException("Не найдена установка", "Установка не найдена в системе", model);
        }
    }

    @PostMapping("/edit/software_installations")
    public String editSoftwareInstallation(@ModelAttribute SoftwareInstallation softwareInstallation, Model model) {
        logger.info("@{}: === ВЫЗОВ МЕТОДА СОХРАНЕНИЯ ДАННЫХ УСТАНОВКИ ПО ===", username);
        softwareInstallationService.save(softwareInstallation);
        logger.info("@{}: === УСПЕШНОЕ СОХРАНЕНИЕ ДАННЫХ УСТАНОВКИ ПО ===", username);
        return Urls.ADMIN_URL + currentUserId;
    }

    @PostMapping("/delete/software_installations/{id}")
    public String deleteSoftwareInstallation(@PathVariable Long id, Model model) {
        logger.info("@{}: === ВЫЗОВ МЕТОДА УДАЛЕНИЯ УСТАНОВКИ ПО ===", username);
        softwareInstallationService.deleteById(id);
        logger.info("@{}: === УСПЕШНОЕ УДАЛЕНИЕ УСТАНОВКИ ПО ===", username);
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