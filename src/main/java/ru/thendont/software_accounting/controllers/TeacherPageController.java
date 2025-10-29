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
        User user = userService.findById(userId).orElseThrow();
        model.addAttribute("user", user);
        model.addAttribute("software", softwareService.findAll());
        return "teacher-page";
    }

    @PostMapping("/request")
    public String sendRequest(@RequestParam Long userId, @RequestParam Long softwareId, @RequestParam Long deviceId,
                              @RequestParam(required = false) String comment) {
        User user = userService.findById(userId).orElse(null);
        Software software = softwareService.findById(softwareId).orElse(null);
        Device device = deviceService.findById(deviceId).orElse(null);

        InstallationRequest request = new InstallationRequest(null, software, device, user, LocalDate.now(),
                InstallationRequestsStatus.PENDING, comment);

        installationRequestService.save(request);
        return "redirect:/teacher/dashboard?userId=" + userId;
    }
}