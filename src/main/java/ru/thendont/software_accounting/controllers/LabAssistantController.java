package ru.thendont.software_accounting.controllers;

import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.thendont.software_accounting.entity.*;
import ru.thendont.software_accounting.error.ErrorHandler;
import ru.thendont.software_accounting.service.*;
import ru.thendont.software_accounting.service.email.EmailService;
import ru.thendont.software_accounting.service.enums.Urls;
import ru.thendont.software_accounting.util.ConstantStrings;
import ru.thendont.software_accounting.util.Util;

import java.util.*;

@Controller
@RequestMapping("/assistant")
public class LabAssistantController {

    @Autowired
    private Logger logger;

    private String username;

    @Autowired
    private UserService userService;

    @Autowired
    private InstallationTaskService installationTaskService;

    @Autowired
    private InstallationReportService installationReportService;

    @Autowired
    private SoftwareInstallationService softwareInstallationService;

    @Autowired
    private DeviceService deviceService;

    @Autowired
    private EmailService emailService;

    @GetMapping("/dashboard")
    public String showDashboard(@RequestParam Long userId, Model model) {
        try {
            User user = userService.findById(userId).orElseThrow();
            username = user.getUsername();

            List<InstallationTask> installationTasks = installationTaskService.findByAssignedTo(user);
            List<InstallationReport> myReports = installationReportService.findByTaskAssignedTo(user);

            Map<Long, List<Device>> taskDevices = new HashMap<>();
            for (InstallationTask task : installationTasks) {
                List<Device> devices = task.getInstallationRequest().getClassroom().getDevices();
                taskDevices.put(task.getId(), devices);
            }

            model.addAttribute("user", user);
            model.addAttribute("installationTasks", installationTasks);
            model.addAttribute("myReports", myReports);
            model.addAttribute("taskDevices", taskDevices);

            return "lab-assistant";
        }
        catch (NoSuchElementException ex) {
            logger.error("@{}: === ПРОИЗОШЛА ОШИБКА ===", username, ex);
            return ErrorHandler.errorPage(ConstantStrings.USER_NOT_FOUND_TITLE, ConstantStrings.USER_NOT_FOUND_MESSAGE, model);
        }
    }

    @PostMapping("/submit-report")
    public String submitReport(@RequestParam Long taskId,
                               @RequestParam Long userId,
                               @RequestParam(required = false) Boolean licenseRequired,
                               @RequestParam(required = false) String notes,
                               Model model) {
        try {
            User user = userService.findById(userId).orElseThrow();
            InstallationTask installationTask = installationTaskService.findById(taskId).orElseThrow();

            InstallationReport installationReport = new InstallationReport(
                    null, installationTask, Util.getCurrentDate(), licenseRequired != null ? licenseRequired : false, notes
            );
            installationReportService.save(installationReport);

            String emailMessage = "Лаборант " + Util.getUserInitials(user) + " сформировал отчет по установке " +
                    installationTask.getInstallationRequest().getSoftware().getTitle() + " " +
                    installationTask.getInstallationRequest().getSoftware().getVersion() + " в аудиторию №" +
                    installationTask.getInstallationRequest().getClassroom().getNumber() + ". " +
                    ConstantStrings.GO_PERSONAL_ACCOUNT;

            emailService.sendMessage(installationTask.getAssignedBy().getEmail(), "Отчет по установке ПО", emailMessage);

            return Urls.LABORATORY_ASSISTANT.getUrlString() + userId;
        }
        catch (NoSuchElementException ex) {
            logger.error("@{}: === ПРОИЗОШЛА ОШИБКА ===", username, ex);
            return ErrorHandler.errorPage(ConstantStrings.OBJECT_NOT_FOUND_TITLE, ConstantStrings.OBJECT_NOT_FOUND_MESSAGE, model);
        }
        catch (MailException ex) {
            logger.error("@{}: === ПРОИЗОШЛА ОШИБКА ===", username, ex);
            return ErrorHandler.errorPage(ConstantStrings.EMAIL_ERROR_TITLE, ConstantStrings.EMAIL_ERROR_MESSAGE, model);
        }
    }

    @PostMapping("/confirm-devices")
    public String confirmDevices(@RequestParam Long taskId,
                                 @RequestParam Long userId,
                                 @RequestParam(value = "deviceIds", required = false) List<Long> deviceIds,
                                 Model model) {
        try {
            if (deviceIds != null) {
                InstallationTask installationTask = installationTaskService.findById(taskId).orElseThrow();
                User user = userService.findById(userId).orElseThrow();
                Software software = installationTask.getInstallationRequest().getSoftware();

                for (Long deviceId : deviceIds) {
                    Device device = deviceService.findById(deviceId).orElseThrow();
                    SoftwareInstallation softwareInstallation = new SoftwareInstallation(
                            null, software, device, user, Util.getCurrentDate()
                    );
                    softwareInstallationService.save(softwareInstallation);
                }
            }
            return Urls.LABORATORY_ASSISTANT.getUrlString() + userId;
        }
        catch (NoSuchElementException ex) {
            logger.error("@{}: === ПРОИЗОШЛА ОШИБКА ===", username, ex);
            return ErrorHandler.errorPage(ConstantStrings.OBJECT_NOT_FOUND_TITLE, ConstantStrings.OBJECT_NOT_FOUND_MESSAGE, model);
        }
    }
}