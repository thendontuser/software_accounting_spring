package ru.thendont.software_accounting.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.thendont.software_accounting.entity.InstallationRequest;
import ru.thendont.software_accounting.entity.User;
import ru.thendont.software_accounting.service.InstallationRequestService;
import ru.thendont.software_accounting.service.SoftwareInstallationService;
import ru.thendont.software_accounting.service.UserService;
import ru.thendont.software_accounting.util.InstallationRequestsStatus;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;

@Controller
@RequestMapping("/manager")
public class ManagerPageController {

    private final UserService userService;
    private final SoftwareInstallationService softwareInstallationService;
    private final InstallationRequestService installationRequestService;

    public ManagerPageController(UserService userService,
                                 SoftwareInstallationService softwareInstallationService,
                                 InstallationRequestService installationRequestService) {
        this.userService = userService;
        this.softwareInstallationService = softwareInstallationService;
        this.installationRequestService = installationRequestService;
    }

    @GetMapping("/dashboard")
    public String showDashboard(@RequestParam Long userId, Model model) {
        try {
            User user = userService.findById(userId).orElseThrow();
            List<InstallationRequest> requests = installationRequestService.findByDepartmentNumber(
                    user.getDepartment().getDepNumber()
            );

            model.addAttribute("user", user);
            model.addAttribute("departmentRequests", requests);

            model.addAttribute("pendingRequests", installationRequestService.findByStatusFromList(
                    requests, InstallationRequestsStatus.PENDING
            ));
            model.addAttribute("installedSoftware", softwareInstallationService.findByDepartmentNumber(
                    user.getDepartment().getDepNumber()
            ));
            return "manager-page";
        }
        catch (NoSuchElementException ex) {
            return handleException("Не найден объект", ex.getMessage(), model);
        }
    }

    private String handleException(String title, String message, Model model) {
        model.addAttribute("errorTitle", title);
        model.addAttribute("errorMessage", message);
        model.addAttribute("timestamp", LocalDate.now());
        return "error-page";
    }
}