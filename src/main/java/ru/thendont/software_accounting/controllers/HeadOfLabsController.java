package ru.thendont.software_accounting.controllers;

import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.thendont.software_accounting.entity.*;
import ru.thendont.software_accounting.error.ErrorHandler;
import ru.thendont.software_accounting.service.*;
import ru.thendont.software_accounting.service.email.EmailService;
import ru.thendont.software_accounting.service.enums.*;
import ru.thendont.software_accounting.service.report.ReportService;
import ru.thendont.software_accounting.util.ConstantStrings;
import ru.thendont.software_accounting.util.Util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.NoSuchElementException;

@Controller
@RequestMapping("/hol")
public class HeadOfLabsController {

    @Autowired
    private Logger logger;

    private String username;

    @Autowired
    private UserService userService;

    @Autowired
    private SoftwareService softwareService;

    @Autowired
    private InstallationRequestService installationRequestService;

    @Autowired
    private InstallationTaskService installationTaskService;

    @Autowired
    private InstallationReportService installationReportService;

    @Autowired
    private LicenseRequestService licenseRequestService;

    @Autowired
    private ReportService reportService;

    @Autowired
    private EmailService emailService;

    @GetMapping("/dashboard")
    public String showDashboard(@RequestParam Long userId, Model model) {
        try {
            User user = userService.findById(userId).orElseThrow();
            username = user.getUsername();
            List<User> labAssistants = userService.findByRoleAndKafedra(UserRoles.LABORATORY_ASSISTANT, user.getKafedra());

            Kafedra kafedra = user.getKafedra();

            List<InstallationRequest> installationRequests = installationRequestService.findByKafedra(kafedra);
            List<InstallationTask> installationTasks = installationTaskService.findByAssignedBy(user);
            List<InstallationReport> installationReports = installationReportService.findByTaskAssignedBy(user);

            model.addAttribute("user", user);
            model.addAttribute("labAssistants", labAssistants);
            model.addAttribute("kafedra", kafedra);
            model.addAttribute("installationRequests", installationRequests);
            model.addAttribute("installationTasks", installationTasks);
            model.addAttribute("installationReports", installationReports);

            return "hol-page";
        }
        catch (NoSuchElementException ex) {
            logger.error("@{}: === ПРОИЗОШЛА ОШИБКА ===", username, ex);
            return ErrorHandler.errorPage(ConstantStrings.USER_NOT_FOUND_TITLE, ConstantStrings.USER_NOT_FOUND_MESSAGE, model);
        }
    }

    @GetMapping("/generate-report")
    public ResponseEntity<byte[]> generateReport(
                         @RequestParam Long userId,
                         @RequestParam String type,
                         @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFrom,
                         @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateTo) {
        try {
            User user = userService.findById(userId).orElseThrow();
            byte[] pdfContent = reportService.generateReport(user, type, dateFrom, dateTo);
            String filename = String.format("report_%s_%s.pdf", type,
                    Util.getCurrentDate().format(DateTimeFormatter.ofPattern("yyyyMMdd")));

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                    .contentType(MediaType.APPLICATION_PDF)
                    .contentLength(pdfContent.length)
                    .body(pdfContent);
        }
        catch (Exception ex) {
            logger.error("@{}: === ОШИБКА ПРИ ГЕНЕРИРОВАНИИ ОТЧЕТА ===", username, ex);
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/create-task")
    public String createTask(@RequestParam Long requestId,
                             @RequestParam Long assignedBy,
                             @RequestParam Long assignedTo,
                             @RequestParam LocalDate deadline,
                             Model model) {
        try {
            InstallationRequest installationRequest = installationRequestService.findById(requestId).orElseThrow();
            User by = userService.findById(assignedBy).orElseThrow();
            User to = userService.findById(assignedTo).orElseThrow();

            installationRequest.setStatus(InstallationRequestStatus.APPROVED);
            installationRequestService.save(installationRequest);

            InstallationTask installationTask = new InstallationTask(
                    null, installationRequest, by, to, Util.getCurrentDate(), InstallationTaskStatus.IN_EXECUTION, deadline
            );
            installationTaskService.save(installationTask);

            String emailMessage;

            // Отправляем сообщение преподавателю
            emailMessage = ConstantStrings.HEAD_OF_LABS + " " + Util.getUserInitials(by) +
                    " одобрил Вашу заявку и отправил ее на поручение";

            emailService.sendMessage(installationRequest.getUser().getEmail(),
                    Util.getInstallationRequestEmailSubject(installationRequest),
                    emailMessage);

            // Отправлеям сообщение лаборанту
            emailMessage = ConstantStrings.HEAD_OF_LABS + " " + Util.getUserInitials(by) + " дал Вам поручение на установку " +
                    installationRequest.getSoftware().getTitle() + " в аудиторию №" +
                    installationRequest.getClassroom().getNumber() + ". " + ConstantStrings.GO_PERSONAL_ACCOUNT;

            emailService.sendMessage(to.getEmail(), ConstantStrings.INSTALLATION_TASK, emailMessage);

            return Urls.HEAD_OF_LABORATORIES_URL.getUrlString() + assignedBy;
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

    @PostMapping("/update-task-status")
    public String updateTaskStatus(@RequestParam Long taskId,
                                   @RequestParam Long userId,
                                   @RequestParam InstallationTaskStatus status,
                                   Model model) {
        try {
            User user = userService.findById(userId).orElseThrow();
            InstallationTask installationTask = installationTaskService.findById(taskId).orElseThrow();

            installationTask.setStatus(status);
            installationTaskService.save(installationTask);

            String emailMessage = ConstantStrings.HEAD_OF_LABS + " " + Util.getUserInitials(user) +
                    " сменил статус Вашего поручения на \"" + status.getStatusName() + "\" по поручению №" +
                    installationTask.getId() + ". " + ConstantStrings.GO_PERSONAL_ACCOUNT;

            emailService.sendMessage(installationTask.getAssignedTo().getEmail(), ConstantStrings.INSTALLATION_TASK, emailMessage);

            return Urls.HEAD_OF_LABORATORIES_URL.getUrlString() + userId;
        }
        catch (NoSuchElementException ex) {
            logger.error("@{}: === ПРОИЗОШЛА ОШИБКА ===", username, ex);
            return ErrorHandler.errorPage(ConstantStrings.OBJECT_NOT_FOUND_TITLE, ConstantStrings.OBJECT_NOT_FOUND_MESSAGE, model);
        }
        catch (Exception ex) {
            logger.error("Ошибка при обновлении статуса", ex);
            return ErrorHandler.errorPage(ConstantStrings.EMAIL_ERROR_TITLE, ConstantStrings.EMAIL_ERROR_MESSAGE, model);
        }
    }

    @PostMapping("/reject-request")
    public String rejectInstallationRequest(@RequestParam Long requestId,
                                            @RequestParam Long userId,
                                            @RequestParam String comment,
                                            Model model) {
        try {
            User user = userService.findById(userId).orElseThrow();
            InstallationRequest installationRequest = installationRequestService.findById(requestId).orElseThrow();

            installationRequest.setStatus(InstallationRequestStatus.REJECTED);
            installationRequestService.save(installationRequest);

            String emailMessage = ConstantStrings.HEAD_OF_LABS + " " + Util.getUserInitials(user) +
                    " отклонил заявку. Причина: " + comment;

            emailService.sendMessage(installationRequest.getUser().getEmail(),
                    Util.getInstallationRequestEmailSubject(installationRequest),
                    emailMessage);

            return Urls.HEAD_OF_LABORATORIES_URL.getUrlString() + userId;
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

    @PostMapping("/request-license")
    public String licenseRequest(@RequestParam Long userId,
                                 @RequestParam Long softwareId,
                                 @RequestParam(required = false, defaultValue = "1") Integer quantity,
                                 @RequestParam String reason,
                                 Model model) {
        try {
            User user = userService.findById(userId).orElseThrow();
            Software software = softwareService.findById(softwareId).orElseThrow();
            List<User> sam_managers = userService.findByRole(UserRoles.SAM_MANAGER);

            LicenseRequest licenseRequest = new LicenseRequest(
                    null, software, quantity, user, Util.getCurrentDate(), LicenseRequestStatus.PENDING, reason
            );
            licenseRequestService.save(licenseRequest);

            String emailMessage = ConstantStrings.HEAD_OF_LABS + " " + Util.getUserInitials(user) +
                    " сформировал запрос на закупку лицензии." +
                    "\nПрограммное обеспечение: " + software.getTitle() + " " + software.getVersion() +
                    "\nКоличество лицензий(шт.): " + quantity + "\n" + ConstantStrings.GO_PERSONAL_ACCOUNT;

            for (User u : sam_managers) {
                emailService.sendMessage(u.getEmail(), "Запрос на закупку лицензии", emailMessage);
            }

            return Urls.HEAD_OF_LABORATORIES_URL.getUrlString() + userId;
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
}