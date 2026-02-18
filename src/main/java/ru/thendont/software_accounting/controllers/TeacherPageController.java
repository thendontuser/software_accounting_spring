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
import ru.thendont.software_accounting.entity.Software;
import ru.thendont.software_accounting.entity.User;
import ru.thendont.software_accounting.error.ErrorHandler;
import ru.thendont.software_accounting.service.BaseCrudService;
import ru.thendont.software_accounting.service.InstallationRequestService;
import ru.thendont.software_accounting.service.enums.Urls;

import java.util.NoSuchElementException;

@Controller
@RequestMapping("/teacher")
public class TeacherPageController {

    @Autowired
    private Logger logger;

    private String username;

    @Autowired
    private BaseCrudService<User> userBaseCrudService;

    @Autowired
    private BaseCrudService<Software> softwareBaseCrudService;

    @Autowired
    private BaseCrudService<Device> deviceBaseCrudService;

    @Autowired
    private InstallationRequestService installationRequestService;

    @GetMapping("/dashboard")
    public String showDashboard(@RequestParam Long userId, Model model) {
        try {
            User user = userBaseCrudService.findById(userId).orElseThrow();
            username = user.getUsername();
            model.addAttribute("user", user);
            model.addAttribute("software", softwareBaseCrudService.findAll());
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
            User user = userBaseCrudService.findById(userId).orElseThrow();
            Software software = softwareBaseCrudService.findById(softwareId).orElseThrow();
            Device device = deviceBaseCrudService.findById(deviceId).orElseThrow();
            /*InstallationRequest request = new InstallationRequest(null, software, device, user, LocalDate.now(),
                    InstallationRequestStatus.PENDING, comment);

            installationRequestService.save(request);*/
            logger.info("@{}: === ЗАЯВКА УСПЕШНО СОХРАНЕНА ===", username);
            return Urls.TEACHER_URL.getUrlString() + userId;
        }
        catch (NoSuchElementException ex) {
            logger.error("@{}: === ПРОИЗОШЛА ОШИБКА ===", username, ex);
            return ErrorHandler.errorPage("Не найден требуемый объект", ex.getMessage(), model);
        }
    }
}