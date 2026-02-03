package ru.thendont.software_accounting.controllers;

import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import ru.thendont.software_accounting.entity.Device;
import ru.thendont.software_accounting.entity.InstallationRequest;
import ru.thendont.software_accounting.entity.Software;
import ru.thendont.software_accounting.entity.User;
import ru.thendont.software_accounting.error.ErrorHandler;
import ru.thendont.software_accounting.service.DeviceService;
import ru.thendont.software_accounting.service.InstallationRequestService;
import ru.thendont.software_accounting.service.SoftwareService;
import ru.thendont.software_accounting.service.UserService;
import ru.thendont.software_accounting.util.InstallationRequestsStatus;
import ru.thendont.software_accounting.util.Urls;

import java.time.LocalDate;
import java.util.NoSuchElementException;

@Controller
@RequestMapping("/teacher")
public class TeacherPageController {

    @Autowired
    private Logger logger;

    private String username;

    @Autowired
    private UserService userService;

    @Autowired
    private SoftwareService softwareService;

    @Autowired
    private DeviceService deviceService;

    @Autowired
    private InstallationRequestService installationRequestService;

    @GetMapping("/dashboard")
    public String showDashboard(@RequestParam Long userId, Model model) {
        try {
            User user = userService.findById(userId).orElseThrow();
            username = user.getUsername();
            logger.info("@{}: === ПОЛЬЗОВАТЕЛЬ С ID {} УСПЕШНО НАЙДЕН ===", username, user.getId());
            model.addAttribute("user", user);
            model.addAttribute("software", softwareService.findAll());
            return "teacher-page";
        }
        catch (NoSuchElementException ex) {
            logger.error("@{}: === ПРОИЗОШЛА ОШИБКА ===", username, ex);
            return ErrorHandler.errorPage("Пользователь не найден", "Система не нашла данного пользователя", model);
        }
    }

    @PostMapping("/request")
    public String sendRequest(@RequestParam Long userId, @RequestParam Long softwareId, @RequestParam Long deviceId,
                              @RequestParam(required = false) String comment, Model model) {
        try {
            User user = userService.findById(userId).orElseThrow();
            logger.info("@{}: === ПОЛЬЗОВАТЕЛЬ С ID {} УСПЕШНО НАЙДЕН ===", username, user.getId());

            Software software = softwareService.findById(softwareId).orElseThrow();
            logger.info("@{}: === ПО С ID {} УСПЕШНО НАЙДЕНО ===", username, software.getId());

            Device device = deviceService.findById(deviceId).orElseThrow();
            logger.info("@{}: === УСТРОЙСТВО С ID {} УСПЕШНО НАЙДЕНО ===", username, device.getId());

            InstallationRequest request = new InstallationRequest(null, software, device, user, LocalDate.now(),
                    InstallationRequestsStatus.PENDING, comment);

            installationRequestService.save(request);
            logger.info("@{}: === ЗАЯВКА УСПЕШНО СОХРАНЕНА ===", username);
            return Urls.TEACHER_URL + userId;
        }
        catch (NoSuchElementException ex) {
            logger.error("@{}: === ПРОИЗОШЛА ОШИБКА ===", username, ex);
            return ErrorHandler.errorPage("Не найден требуемый объект", ex.getMessage(), model);
        }
    }
}