package ru.thendont.software_accounting.controllers;

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
import ru.thendont.software_accounting.service.DeviceService;
import ru.thendont.software_accounting.service.InstallationRequestService;
import ru.thendont.software_accounting.service.SoftwareService;
import ru.thendont.software_accounting.service.UserService;
import ru.thendont.software_accounting.util.InstallationRequestsStatus;

import java.time.LocalDate;
import java.util.NoSuchElementException;

@Controller
@RequestMapping("/teacher")
public class TeacherPageController {

    private final UserService userService;
    private final SoftwareService softwareService;
    private final DeviceService deviceService;
    private final InstallationRequestService installationRequestService;

    public TeacherPageController(UserService userService,
                                 SoftwareService softwareService,
                                 DeviceService deviceService,
                                 InstallationRequestService installationRequestService) {
        this.userService = userService;
        this.softwareService = softwareService;
        this.deviceService = deviceService;
        this.installationRequestService = installationRequestService;
    }

    @GetMapping("/dashboard")
    public String showDashboard(@RequestParam Long userId, Model model) {
        try {
            User user = userService.findById(userId).orElseThrow();
            model.addAttribute("user", user);
            model.addAttribute("software", softwareService.findAll());
            return "teacher-page";
        }
        catch (NoSuchElementException ex) {
            return handleException("Пользователь не найден", "Система не нашла данного пользователя", model);
        }
    }

    @PostMapping("/request")
    public String sendRequest(@RequestParam Long userId, @RequestParam Long softwareId, @RequestParam Long deviceId,
                              @RequestParam(required = false) String comment, Model model) {
        try {
            User user = userService.findById(userId).orElseThrow();
            Software software = softwareService.findById(softwareId).orElseThrow();
            Device device = deviceService.findById(deviceId).orElseThrow();

            InstallationRequest request = new InstallationRequest(null, software, device, user, LocalDate.now(),
                    InstallationRequestsStatus.PENDING, comment);

            installationRequestService.save(request);
            return "redirect:/teacher/dashboard?userId=" + userId;
        }
        catch (NoSuchElementException ex) {
            return handleException("Не найден требуемый объект", ex.getMessage(), model);
        }
    }

    private String handleException(String title, String message, Model model) {
        model.addAttribute("errorTitle", title);
        model.addAttribute("errorMessage", message);
        model.addAttribute("timestamp", LocalDate.now());
        return "error-page";
    }
}