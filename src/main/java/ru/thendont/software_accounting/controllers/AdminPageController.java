package ru.thendont.software_accounting.controllers;

import com.itextpdf.text.DocumentException;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.thendont.software_accounting.entity.*;
import ru.thendont.software_accounting.error.ErrorHandler;
import ru.thendont.software_accounting.service.*;
import ru.thendont.software_accounting.service.enums.Urls;
import ru.thendont.software_accounting.service.report.ReportService;
import ru.thendont.software_accounting.util.ConstantStrings;

import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;

@Controller
@RequestMapping("/admin")
public class AdminPageController {

    @Autowired
    private Logger logger;

    private Long currentUserId;

    private String username;

    @Autowired
    private UserService userService;

    @Autowired
    private SoftwareInstallationService softwareInstallationService;

    @Autowired
    private InstallationRequestService installationRequestService;

    @Autowired
    private BaseCrudService<User> userBaseCrudService;

    @Autowired
    private BaseCrudService<Department> departmentBaseCrudService;

    @Autowired
    private BaseCrudService<Developer> developerBaseCrudService;

    @Autowired
    private BaseCrudService<Software> softwareBaseCrudService;

    @Autowired
    private BaseCrudService<Device> deviceBaseCrudService;

    @Autowired
    private BaseCrudService<InstallationRequest> installationRequestBaseCrudService;

    @Autowired
    private BaseCrudService<License> licenseBaseCrudService;

    @Autowired
    private BaseCrudService<SoftwareInstallation> softwareInstallationBaseCrudService;

    @GetMapping("/dashboard")
    public String showDashboard(@RequestParam Long userId, Model model) {
        if (currentUserId == null) {
            currentUserId = userId;
        }
        try {
            User user = userBaseCrudService.findById(userId).orElseThrow();
            username = user.getUsername();

            List<Department> departments = departmentBaseCrudService.findAll();
            List<User> users = userBaseCrudService.findAll();
            List<Developer> developers = developerBaseCrudService.findAll();
            List<Device> devices = deviceBaseCrudService.findAll();
            List<InstallationRequest> installationRequests = installationRequestBaseCrudService.findAll();
            List<License> licenses = licenseBaseCrudService.findAll();
            List<Software> softwareList = softwareBaseCrudService.findAll();
            List<SoftwareInstallation> softwareInstallations = softwareInstallationBaseCrudService.findAll();
            int pendingUserCount = userService.findPendingUsers().size();

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
            model.addAttribute("pendingUserCount", pendingUserCount);
            return "admin-page";
        }
        catch (NoSuchElementException ex) {
            logger.error("@{}: === ПРОИЗОШЛА ОШИБКА ===", username, ex);
            return ErrorHandler.errorPage("Не найден пользователь", "Пользователь не найден в системе", model);
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
                                departmentBaseCrudService.findById(departmentNumber).orElse(null).getTitle());
            }
        }
        catch (IOException ex) {
            logger.error("@{}: === ПРОИЗОШЛА ОШИБКА ===", username, ex);
            ErrorHandler.errorPage("Ошибка генерации отчета", "При генерации отчета возникла ошибка", model);
        }
        catch (DocumentException ex) {
            logger.error("@{}: === ПРОИЗОШЛА ОШИБКА ===", username, ex);
            ErrorHandler.errorPage("Ошибка создания документа", "При создании документа возникла ошибка", model);
        }
        catch (NullPointerException ex) {
            logger.error("@{}: === ПРОИЗОШЛА ОШИБКА ===", username, ex);
            ErrorHandler.errorPage("Не найден факультет", "Не найден факультет в системе", model);
        }
    }

// Для сущности user ==========================================================================================
    @GetMapping("/edit/users/{id}")
    public String editUser(@PathVariable Long id, Model model) {
        try {
            model.addAttribute("user", userBaseCrudService.findById(id).orElseThrow());
            model.addAttribute("departments", departmentBaseCrudService.findAll());
            model.addAttribute("currentUserId", currentUserId);
            logger.info("@{}: === НАЧАЛО РЕДАКТИРОВАНИЯ ПОЛЬЗОВАТЕЛЯ ===", username);
            return "edit-user";
        }
        catch (NoSuchElementException ex) {
            logger.error("@{}: === ПРОИЗОШЛА ОШИБКА ===", username, ex);
            return ErrorHandler.errorPage("Не найден пользователь", "Пользователь не найден в системе", model);
        }
    }

    @PostMapping("/edit/users")
    public String editUser(@ModelAttribute User user, Model model) {
        /*if (user.getRole().isEmpty()) {
            user.setRole(null);
        }*/
        userBaseCrudService.save(user);
        logger.info("@{}: === ПОЛЬЗОВАТЕЛЬ УСПЕШНО СОХРАНИЛСЯ В БАЗЕ ===", username);
        return Urls.ADMIN_URL.getUrlString() + currentUserId;
    }

    @PostMapping("/delete/users/{id}")
    public String deleteUser(@PathVariable Long id, Model model) {
        userBaseCrudService.deleteById(id);
        logger.info("@{}: === ПОЛЬЗОВАТЕЛЬ УСПЕШНО УДАЛИЛСЯ ИЗ БАЗЫ ===", username);
        return Urls.ADMIN_URL.getUrlString() + currentUserId;
    }
// Для сущности user ==========================================================================================

// Для сущности department ==========================================================================================
    @GetMapping("/edit/departments/{id}")
    public String editDepartment(@PathVariable Long id, Model model) {
        try {
            model.addAttribute("department", isNewRecord(id) ? new Department() :
                    departmentBaseCrudService.findById(id).orElseThrow());
            model.addAttribute("currentUserId", currentUserId);
            logger.info("@{}: === НАЧАЛО РЕДАКТИРОВАНИЯ ФАКУЛЬТЕТА ===", username);
            return "edit-department";
        }
        catch (NoSuchElementException ex) {
            logger.error("@{}: === ПРОИЗОШЛА ОШИБКА ===", username, ex);
            return ErrorHandler.errorPage("Не найден факультет", "Факультет не найден в системе", model);
        }
    }

    @PostMapping("/edit/departments")
    public String editDepartment(@ModelAttribute Department department, Model model) {
        departmentBaseCrudService.save(department);
        logger.info("@{}: === УСПЕШНОЕ СОХРАНЕНИЕ ДАННЫХ ФАКУЛЬТЕТА ===", username);
        return Urls.ADMIN_URL.getUrlString() + currentUserId;
    }

    @PostMapping("/delete/departments/{id}")
    public String deleteDepartment(@PathVariable Long id, Model model) {
        departmentBaseCrudService.deleteById(id);
        logger.info("@{}: === УСПЕШНОЕ УДАЛЕНИЕ ФАКУЛЬТЕТА ===", username);
        return Urls.ADMIN_URL.getUrlString() + currentUserId;
    }
// Для сущности department ==========================================================================================

// Для сущности device ==========================================================================================
    @GetMapping("/edit/devices/{id}")
    public String editDevice(@PathVariable Long id, Model model) {
        try {
            model.addAttribute("device", isNewRecord(id) ? new Device() : deviceBaseCrudService.findById(id).orElseThrow());
            model.addAttribute("departments", departmentBaseCrudService.findAll());
            model.addAttribute("currentUserId", currentUserId);
            logger.info("@{}: === НАЧАЛО РЕДАКТИРОВАНИЯ УСТРОЙСТВА ===", username);
            return "edit-device";
        }
        catch (NoSuchElementException ex) {
            logger.error("@{}: === ПРОИЗОШЛА ОШИБКА ===", username, ex);
            return ErrorHandler.errorPage("Не найдено устройство", "Устройство не найдено в системе", model);
        }
    }

    @PostMapping("/edit/devices")
    public String editDevice(@ModelAttribute Device device, Model model) {
        deviceBaseCrudService.save(device);
        logger.info("@{}: === УСПЕШНОЕ СОХРАНЕНИЕ ДАННЫХ УСТРОЙСТВА ===", username);
        return Urls.ADMIN_URL.getUrlString() + currentUserId;
    }

    @PostMapping("/delete/devices/{id}")
    public String deleteDevice(@PathVariable Long id, Model model) {
        deviceBaseCrudService.deleteById(id);
        logger.info("@{}: === УСПЕШНОЕ УДАЛЕНИЕ УСТРОЙСТВА ===", username);
        return Urls.ADMIN_URL.getUrlString() + currentUserId;
    }
// Для сущности device ==========================================================================================

// Для сущности software ==========================================================================================
    @GetMapping("/edit/software/{id}")
    public String editSoftware(@PathVariable Long id, Model model) {
        try {
            model.addAttribute("software", isNewRecord(id) ? new Software() :
                    softwareBaseCrudService.findById(id).orElseThrow());
            model.addAttribute("developers", developerBaseCrudService.findAll());
            model.addAttribute("currentUserId", currentUserId);
            logger.info("@{}: === НАЧАЛО РЕДАКТИРОВАНИЯ ПО ===", username);
            return "edit-software";
        }
        catch (NoSuchElementException ex) {
            logger.error("@{}: === ПРОИЗОШЛА ОШИБКА ===", username, ex);
            return ErrorHandler.errorPage("Не найдено программное обеспечение",
                    "Программное обеспечение не найдено в системе", model);
        }
    }

    @PostMapping("/edit/software")
    public String editSoftware(@ModelAttribute Software software, Model model) {
        software.setLogoPath(ConstantStrings.LOGO_DIRECTORY_PATH + software.getLogoPath());
        softwareBaseCrudService.save(software);
        logger.info("@{}: === УСПЕШНОЕ СОХРАНЕНИЕ ДАННЫХ ПО ===", username);
        return Urls.ADMIN_URL.getUrlString() + currentUserId;
    }

    @PostMapping("/delete/software/{id}")
    public String deleteSoftware(@PathVariable Long id, Model model) {
        softwareBaseCrudService.deleteById(id);
        logger.info("@{}: === УСПЕШНОЕ УДАЛЕНИЕ ПО ===", username);
        return Urls.ADMIN_URL.getUrlString() + currentUserId;
    }
// Для сущности software ==========================================================================================

// Для сущности developer ==========================================================================================
    @GetMapping("/edit/developers/{id}")
    public String editDeveloper(@PathVariable Long id, Model model) {
        try {
            model.addAttribute("developer", isNewRecord(id) ? new Developer() :
                    developerBaseCrudService.findById(id).orElseThrow());
            model.addAttribute("currentUserId", currentUserId);
            logger.info("@{}: === НАЧАЛО РЕДАКТИРОВАНИЯ РАЗРАБОТЧИКА ===", username);
            return "edit-developer";
        }
        catch (NoSuchElementException ex) {
            logger.error("@{}: === ПРОИЗОШЛА ОШИБКА ===", username, ex);
            return ErrorHandler.errorPage("Не найден разработчик", "Разработчик не найден в системе", model);
        }
    }

    @PostMapping("/edit/developers")
    public String editDeveloper(@ModelAttribute Developer developer, Model model) {
        developerBaseCrudService.save(developer);
        logger.info("@{}: === УСПЕШНОЕ СОХРАНЕНИЕ ДАННЫХ РАЗРАБОТЧИКА ===", username);
        return Urls.ADMIN_URL.getUrlString() + currentUserId;
    }

    @PostMapping("/delete/developers/{id}")
    public String deleteDeveloper(@PathVariable Long id, Model model) {
        developerBaseCrudService.deleteById(id);
        logger.info("@{}: === УСПЕШНОЕ УДАЛЕНИЕ РАЗРАБОТЧИКА ===", username);
        return Urls.ADMIN_URL.getUrlString() + currentUserId;
    }
// Для сущности developer ==========================================================================================

// Для сущности license ==========================================================================================
    @GetMapping("/edit/licenses/{id}")
    public String editLicense(@PathVariable Long id, Model model) {
        try {
            model.addAttribute("license", isNewRecord(id) ? new License() :
                    licenseBaseCrudService.findById(id).orElseThrow());
            model.addAttribute("software", softwareBaseCrudService.findAll());
            model.addAttribute("currentUserId", currentUserId);
            logger.info("@{}: === НАЧАЛО РЕДАКТИРОВАНИЯ ЛИЦЕНЗИИ ===", username);
            return "edit-license";
        }
        catch (NoSuchElementException ex) {
            logger.error("@{}: === ПРОИЗОШЛА ОШИБКА ===", username, ex);
            return ErrorHandler.errorPage("Не найдена лицензия", "Лицензия не найдена в системе", model);
        }
    }

    @PostMapping("/edit/licenses")
    public String editLicense(@ModelAttribute License license, Model model) {
        licenseBaseCrudService.save(license);
        logger.info("@{}: === УСПЕШНОЕ СОХРАНЕНИЕ ДАННЫХ ЛИЦЕНЗИИ ===", username);
        return Urls.ADMIN_URL.getUrlString() + currentUserId;
    }

    @PostMapping("/delete/licenses/{id}")
    public String deleteLicense(@PathVariable Long id, Model model) {
        licenseBaseCrudService.deleteById(id);
        logger.info("@{}: === УСПЕШНОЕ УДАЛЕНИЕ ЛИЦЕНЗИИ ===", username);
        return Urls.ADMIN_URL.getUrlString() + currentUserId;
    }
// Для сущности license ==========================================================================================

// Для сущности InstallationRequest ==========================================================================================
    @GetMapping("/edit/installation_requests/{id}")
    public String editInstallationRequest(@PathVariable Long id, Model model) {
        try {
            model.addAttribute("request", installationRequestBaseCrudService.findById(id).orElseThrow());
            model.addAttribute("software", softwareBaseCrudService.findAll());
            model.addAttribute("devices", deviceBaseCrudService.findAll());
            model.addAttribute("users", userBaseCrudService.findAll());
            model.addAttribute("currentUserId", currentUserId);
            logger.info("@{}: === НАЧАЛО РЕДАКТИРОВАНИЯ ЗАЯВКИ ===", username);
            return "edit-installation-request";
        }
        catch (NoSuchElementException ex) {
            logger.error("@{}: === ПРОИЗОШЛА ОШИБКА ===", username, ex);
            return ErrorHandler.errorPage("Не найдена заявка на установку ПО", "Заявка не найдена в системе", model);
        }
    }

    @PostMapping("/edit/installation_requests")
    public String editInstallationRequest(@ModelAttribute InstallationRequest installationRequest, Model model) {
        installationRequestBaseCrudService.save(installationRequest);
        logger.info("@{}: === УСПЕШНОЕ СОХРАНЕНИЕ ДАННЫХ ЗАЯВКИ ===", username);
        return Urls.ADMIN_URL.getUrlString() + currentUserId;
    }

    @PostMapping("/delete/installation_requests/{id}")
    public String deleteInstallationRequest(@PathVariable Long id, Model model) {
        installationRequestBaseCrudService.deleteById(id);
        logger.info("@{}: === УСПЕШНОЕ УДАЛЕНИЕ ЗАЯВКИ ===", username);
        return Urls.ADMIN_URL.getUrlString() + currentUserId;
    }
// Для сущности InstallationRequest ==========================================================================================

// Для сущности SoftwareInstallation ==========================================================================================
    @GetMapping("/edit/software_installations/{id}")
    public String editSoftwareInstallation(@PathVariable Long id, Model model) {
        try {
            model.addAttribute("installation", isNewRecord(id) ? new SoftwareInstallation() :
                    softwareInstallationBaseCrudService.findById(id).orElseThrow());
            model.addAttribute("software", softwareBaseCrudService.findAll());
            model.addAttribute("devices", deviceBaseCrudService.findAll());
            model.addAttribute("users", userBaseCrudService.findAll());
            model.addAttribute("currentUserId", currentUserId);
            logger.info("@{}: === НАЧАЛО РЕДАКТИРОВАНИЯ УСТАНОВКИ ПО ===", username);
            return "edit-software-installation";
        }
        catch (NoSuchElementException ex) {
            logger.error("@{}: === ПРОИЗОШЛА ОШИБКА ===", username, ex);
            return ErrorHandler.errorPage("Не найдена установка", "Установка не найдена в системе", model);
        }
    }

    @PostMapping("/edit/software_installations")
    public String editSoftwareInstallation(@ModelAttribute SoftwareInstallation softwareInstallation, Model model) {
        softwareInstallationBaseCrudService.save(softwareInstallation);
        logger.info("@{}: === УСПЕШНОЕ СОХРАНЕНИЕ ДАННЫХ УСТАНОВКИ ПО ===", username);
        return Urls.ADMIN_URL.getUrlString() + currentUserId;
    }

    @PostMapping("/delete/software_installations/{id}")
    public String deleteSoftwareInstallation(@PathVariable Long id, Model model) {
        softwareInstallationBaseCrudService.deleteById(id);
        logger.info("@{}: === УСПЕШНОЕ УДАЛЕНИЕ УСТАНОВКИ ПО ===", username);
        return Urls.ADMIN_URL.getUrlString() + currentUserId;
    }
// Для сущности SoftwareInstallation ==========================================================================================

    private boolean isNewRecord(Long id) {
        return id == 0;
    }
}