package ru.thendont.software_accounting.controllers;

import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.thendont.software_accounting.entity.*;
import ru.thendont.software_accounting.error.ErrorHandler;
import ru.thendont.software_accounting.service.*;
import ru.thendont.software_accounting.service.enums.Urls;
import ru.thendont.software_accounting.util.ConstantStrings;

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
    private DepartmentService departmentService;

    @Autowired
    private DeviceService deviceService;

    @Autowired
    private KafedraService kafedraService;

    @Autowired
    private ClassroomService classroomService;

    @Autowired
    private SoftwareInstallationService softwareInstallationService;

    @Autowired
    private DeveloperService developerService;

    @Autowired
    private SoftwareService softwareService;

    @GetMapping("/dashboard")
    public String showDashboard(@RequestParam Long userId, Model model) {
        if (currentUserId == null) {
            currentUserId = userId;
        }
        try {
            User user = userService.findById(userId).orElseThrow();
            List<User> users = userService.findAll();
            username = user.getUsername();
            int pendingUserCount = userService.findPendingUsers().size();

            List<Department> departments = departmentService.findAll();
            List<Device> devices = deviceService.findAll();
            List<Kafedra> kafedras = kafedraService.findAll();
            List<Classroom> classrooms = classroomService.findAll();

            List<Software> softwareList = softwareService.findAll();
            List<SoftwareInstallation> softwareInstallations = softwareInstallationService.findAll();
            List<Developer> developers = developerService.findAll();

            model.addAttribute("user", user);
            model.addAttribute("users", users);
            model.addAttribute("userCount", users.size());
            model.addAttribute("pendingUserCount", pendingUserCount);

            model.addAttribute("departments", departments);
            model.addAttribute("devices", devices);
            model.addAttribute("kafedras", kafedras);
            model.addAttribute("classrooms", classrooms);

            model.addAttribute("softwareList", softwareList);
            model.addAttribute("developers", developers);
            model.addAttribute("softwareInstallations", softwareInstallations);
            model.addAttribute("softwareCount", softwareList.size());
            model.addAttribute("installationCount", softwareInstallations.size());

            return "admin-page";
        }
        catch (NoSuchElementException ex) {
            logger.error("@{}: === ПРОИЗОШЛА ОШИБКА ===", username, ex);
            return ErrorHandler.errorPage(ConstantStrings.USER_NOT_FOUND_TITLE, ConstantStrings.USER_NOT_FOUND_MESSAGE, model);
        }
    }

// Для сущности user ==========================================================================================
    @GetMapping("/edit/users/{id}")
    public String editUser(@PathVariable Long id, Model model) {
        try {
            model.addAttribute("user", userService.findById(id).orElseThrow());
            model.addAttribute("kafedras", kafedraService.findAll());
            model.addAttribute("currentUserId", currentUserId);
            logger.info("@{}: === НАЧАЛО РЕДАКТИРОВАНИЯ ПОЛЬЗОВАТЕЛЯ ===", username);
            return "edit-user";
        }
        catch (NoSuchElementException ex) {
            logger.error("@{}: === ПРОИЗОШЛА ОШИБКА ===", username, ex);
            return ErrorHandler.errorPage(ConstantStrings.USER_NOT_FOUND_TITLE, ConstantStrings.USER_NOT_FOUND_MESSAGE, model);
        }
    }

    @PostMapping("/edit/users")
    public String editUser(@ModelAttribute User user) {
        if (user.getRole() == null) {
            user.setRole(null);
        }
        if (user.getKafedra() != null && user.getKafedra().getId() == null) {
            user.setKafedra(null);
        }
        userService.save(user);
        logger.info("@{}: === ПОЛЬЗОВАТЕЛЬ УСПЕШНО СОХРАНИЛСЯ В БАЗЕ ===", username);
        return Urls.ADMIN_URL.getUrlString() + currentUserId;
    }

    @PostMapping("/delete/users/{id}")
    public String deleteUser(@PathVariable Long id) {
        userService.deleteById(id);
        logger.info("@{}: === ПОЛЬЗОВАТЕЛЬ УСПЕШНО УДАЛИЛСЯ ИЗ БАЗЫ ===", username);
        return Urls.ADMIN_URL.getUrlString() + currentUserId;
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
            return ErrorHandler.errorPage("Не найден факультет", "Факультет не найден в системе", model);
        }
    }

    @PostMapping("/edit/departments")
    public String editDepartment(@ModelAttribute Department department) {
        departmentService.save(department);
        logger.info("@{}: === УСПЕШНОЕ СОХРАНЕНИЕ ДАННЫХ ФАКУЛЬТЕТА ===", username);
        return Urls.ADMIN_URL.getUrlString() + currentUserId;
    }

    @PostMapping("/delete/departments/{id}")
    public String deleteDepartment(@PathVariable Long id) {
        departmentService.deleteById(id);
        logger.info("@{}: === УСПЕШНОЕ УДАЛЕНИЕ ФАКУЛЬТЕТА ===", username);
        return Urls.ADMIN_URL.getUrlString() + currentUserId;
    }
// Для сущности department ==========================================================================================

// Для сущности device ==========================================================================================
    @GetMapping("/edit/devices/{id}")
    public String editDevice(@PathVariable Long id, Model model) {
        try {
            model.addAttribute("device", isNewRecord(id) ? new Device() : deviceService.findById(id).orElseThrow());
            model.addAttribute("classrooms", classroomService.findAll());
            model.addAttribute("kafedras", kafedraService.findAll());
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
    public String editDevice(@ModelAttribute Device device) {
        deviceService.save(device);
        logger.info("@{}: === УСПЕШНОЕ СОХРАНЕНИЕ ДАННЫХ УСТРОЙСТВА ===", username);
        return Urls.ADMIN_URL.getUrlString() + currentUserId;
    }

    @PostMapping("/delete/devices/{id}")
    public String deleteDevice(@PathVariable Long id) {
        deviceService.deleteById(id);
        logger.info("@{}: === УСПЕШНОЕ УДАЛЕНИЕ УСТРОЙСТВА ===", username);
        return Urls.ADMIN_URL.getUrlString() + currentUserId;
    }
// Для сущности device ==========================================================================================

// Для сущности kafedra ==========================================================================================
    @GetMapping("/edit/kafedras/{id}")
    public String editKafedra(@PathVariable Long id, Model model) {
        try {
            model.addAttribute("kafedra", isNewRecord(id) ? new Kafedra() : kafedraService.findById(id).orElseThrow());
            model.addAttribute("departments", departmentService.findAll());
            model.addAttribute("currentUserId", currentUserId);
            logger.info("@{}: === НАЧАЛО РЕДАКТИРОВАНИЯ КАФЕДРЫ ===", username);
            return "edit-kafedra";
        }
        catch (NoSuchElementException ex) {
            logger.error("@{}: === ПРОИЗОШЛА ОШИБКА ===", username, ex);
            return ErrorHandler.errorPage("Не найдена кафедра", "Кафедра не найдена в системе", model);
        }
    }

    @PostMapping("/edit/kafedras")
    public String editKafedra(@ModelAttribute Kafedra kafedra) {
        kafedraService.save(kafedra);
        logger.info("@{}: === УСПЕШНОЕ СОХРАНЕНИЕ ДАННЫХ КАФЕДРЫ ===", username);
        return Urls.ADMIN_URL.getUrlString() + currentUserId;
    }

    @PostMapping("/delete/kafedras/{id}")
    public String deleteKafedra(@PathVariable Long id) {
        kafedraService.deleteById(id);
        logger.info("@{}: === УСПЕШНОЕ УДАЛЕНИЕ КАФЕДРЫ ===", username);
        return Urls.ADMIN_URL.getUrlString() + currentUserId;
    }
// Для сущности kafedra ==========================================================================================

// Для сущности classroom ==========================================================================================
    @GetMapping("/edit/classrooms/{id}")
    public String editClassrooms(@PathVariable Long id, Model model) {
        try {
            model.addAttribute("classroom", isNewRecord(id) ? new Classroom() : classroomService.findById(id).orElseThrow());
            model.addAttribute("kafedras", kafedraService.findAll());
            model.addAttribute("currentUserId", currentUserId);
            logger.info("@{}: === НАЧАЛО РЕДАКТИРОВАНИЯ АУДИТОРИИ ===", username);
            return "edit-classroom";
        }
        catch (NoSuchElementException ex) {
            logger.error("@{}: === ПРОИЗОШЛА ОШИБКА ===", username, ex);
            return ErrorHandler.errorPage("Не найдена аудитория", "Аудитория не найдена в системе", model);
        }
    }

    @PostMapping("/edit/classrooms")
    public String editClassroom(@ModelAttribute Classroom classroom) {
        classroomService.save(classroom);
        logger.info("@{}: === УСПЕШНОЕ СОХРАНЕНИЕ ДАННЫХ АУДИТОРИИ ===", username);
        return Urls.ADMIN_URL.getUrlString() + currentUserId;
    }

    @PostMapping("/delete/classrooms/{id}")
    public String deleteClassroom(@PathVariable Long id) {
        classroomService.deleteById(id);
        logger.info("@{}: === УСПЕШНОЕ УДАЛЕНИЕ АУДИТОРИИ ===", username);
        return Urls.ADMIN_URL.getUrlString() + currentUserId;
    }
// Для сущности classroom ==========================================================================================

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
            return ErrorHandler.errorPage("Не найдено программное обеспечение",
                    "Программное обеспечение не найдено в системе", model);
        }
    }

    @PostMapping("/edit/software")
    public String editSoftware(@ModelAttribute Software software) {
        software.setLogoPath(ConstantStrings.LOGO_DIRECTORY_PATH + software.getLogoPath());
        softwareService.save(software);
        logger.info("@{}: === УСПЕШНОЕ СОХРАНЕНИЕ ДАННЫХ ПО ===", username);
        return Urls.ADMIN_URL.getUrlString() + currentUserId;
    }

    @PostMapping("/delete/software/{id}")
    public String deleteSoftware(@PathVariable Long id, Model model) {
        softwareService.deleteById(id);
        logger.info("@{}: === УСПЕШНОЕ УДАЛЕНИЕ ПО ===", username);
        return Urls.ADMIN_URL.getUrlString() + currentUserId;
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
            return ErrorHandler.errorPage("Не найден разработчик", "Разработчик не найден в системе", model);
        }
    }

    @PostMapping("/edit/developers")
    public String editDeveloper(@ModelAttribute Developer developer) {
        developerService.save(developer);
        logger.info("@{}: === УСПЕШНОЕ СОХРАНЕНИЕ ДАННЫХ РАЗРАБОТЧИКА ===", username);
        return Urls.ADMIN_URL.getUrlString() + currentUserId;
    }

    @PostMapping("/delete/developers/{id}")
    public String deleteDeveloper(@PathVariable Long id, Model model) {
        developerService.deleteById(id);
        logger.info("@{}: === УСПЕШНОЕ УДАЛЕНИЕ РАЗРАБОТЧИКА ===", username);
        return Urls.ADMIN_URL.getUrlString() + currentUserId;
    }
// Для сущности developer ==========================================================================================

    private boolean isNewRecord(Long id) {
        return id == 0;
    }
}