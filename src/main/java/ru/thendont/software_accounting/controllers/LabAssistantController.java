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
import ru.thendont.software_accounting.entity.InstallationReport;
import ru.thendont.software_accounting.entity.InstallationTask;
import ru.thendont.software_accounting.entity.User;
import ru.thendont.software_accounting.error.ErrorHandler;
import ru.thendont.software_accounting.service.InstallationReportService;
import ru.thendont.software_accounting.service.InstallationTaskService;
import ru.thendont.software_accounting.service.UserService;
import ru.thendont.software_accounting.service.email.EmailService;
import ru.thendont.software_accounting.service.enums.Urls;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;

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
    private EmailService emailService;

    @GetMapping("/dashboard")
    public String showDashboard(@RequestParam Long userId, Model model) {
        try {
            User user = userService.findById(userId).orElseThrow();
            username = user.getUsername();

            List<InstallationTask> installationTasks = installationTaskService.findByAssignedTo(user);
            List<InstallationReport> myReports = installationReportService.findByTaskAssignedTo(user);

            model.addAttribute("user", user);
            model.addAttribute("installationTasks", installationTasks);
            model.addAttribute("myReports", myReports);

            return "lab-assistant";
        }
        catch (NoSuchElementException ex) {
            logger.error("@{}: === ПРОИЗОШЛА ОШИБКА ===", username, ex);
            return ErrorHandler.errorPage("Не найден пользователь", "Пользователь не найден в системе", model);
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
                    null, installationTask, LocalDate.now(), licenseRequired != null ? licenseRequired : false, notes
            );
            installationReportService.save(installationReport);

            String emailMessage = "Лаборант " + user.getLastName() + " " + user.getFirstName().charAt(0) + "." +
                    user.getPatronymic().charAt(0) + "." + " сформировал отчет по установке " +
                    installationTask.getInstallationRequest().getSoftware().getTitle() + " " +
                    installationTask.getInstallationRequest().getSoftware().getVersion() + " в аудиторию №" +
                    installationTask.getInstallationRequest().getClassroom().getNumber() + ". Загляните в личный кабинет";

            emailService.sendMessage(installationTask.getAssignedBy().getEmail(), "Отчет по установке ПО", emailMessage);

            return Urls.LABORATORY_ASSISTANT.getUrlString() + userId;
        }
        catch (NoSuchElementException ex) {
            logger.error("@{}: === ПРОИЗОШЛА ОШИБКА ===", username, ex);
            return ErrorHandler.errorPage("Не найден объект", "Объект не найден в системе", model);
        }
        catch (MailException ex) {
            logger.error("@{}: === ПРОИЗОШЛА ОШИБКА ===", username, ex);
            return ErrorHandler.errorPage("Ошибка при отправке сообщения на почту",
                    "Сообщение не отправлено на указанный адрес, проверьте соединение с интернетом", model);
        }
    }
}