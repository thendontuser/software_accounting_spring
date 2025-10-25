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
import ru.thendont.software_accounting.repository.DeviceRepository;
import ru.thendont.software_accounting.repository.InstallationRequestRepository;
import ru.thendont.software_accounting.repository.SoftwareRepository;
import ru.thendont.software_accounting.repository.UserRepository;
import ru.thendont.software_accounting.util.InstallationRequestsStatus;

import java.time.LocalDate;

@Controller
@RequestMapping("/teacher")
public class TeacherPageController {

    private final UserRepository userRepository;
    private final SoftwareRepository softwareRepository;
    private final DeviceRepository deviceRepository;
    private final InstallationRequestRepository installationRequestRepository;

    public TeacherPageController(UserRepository userRepository,
                                 SoftwareRepository softwareRepository,
                                 DeviceRepository deviceRepository,
                                 InstallationRequestRepository installationRequestRepository) {
        this.userRepository = userRepository;
        this.softwareRepository = softwareRepository;
        this.deviceRepository = deviceRepository;
        this.installationRequestRepository = installationRequestRepository;
    }

    @GetMapping("/dashboard")
    public String showDashboard(@RequestParam Long userId, Model model) {
        User user = userRepository.findById(userId).orElseThrow();
        model.addAttribute("user", user);
        model.addAttribute("software", softwareRepository.findAll());
        return "teacher-page";
    }

    @PostMapping("/request")
    public String sendRequest(@RequestParam Long userId, @RequestParam Long softwareId, @RequestParam Long deviceId,
                              @RequestParam(required = false) String comment) {
        User user = userRepository.findById(userId).orElse(null);
        Software software = softwareRepository.findById(softwareId).orElse(null);
        Device device = deviceRepository.findById(deviceId).orElse(null);

        InstallationRequest request = new InstallationRequest(null, software, device, user, LocalDate.now(),
                InstallationRequestsStatus.PENDING, comment);

        installationRequestRepository.save(request);
        return "redirect:/teacher/dashboard?userId=" + userId;
    }
}