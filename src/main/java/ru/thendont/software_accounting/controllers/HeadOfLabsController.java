package ru.thendont.software_accounting.controllers;

import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
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

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
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
            return ErrorHandler.errorPage("Не найден пользователь", "Пользователь не найден в системе", model);
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
                    LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")));

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
                    null, installationRequest, by, to, LocalDate.now(), InstallationTaskStatus.IN_EXECUTION, deadline
            );
            installationTaskService.save(installationTask);

            String emailMessage;

            // Отправляем сообщение преподавателю
            emailMessage = "Заведующий лабораториями " + by.getLastName() + " " + by.getFirstName().charAt(0) + "." +
                    by.getPatronymic().charAt(0) + "." + " одобрил Вашу заявку и отправил ее на поручение";

            emailService.sendMessage(installationRequest.getUser().getEmail(), "Заявка на установку " +
                    installationRequest.getSoftware().getTitle() + " в аудиторию №" +
                    installationRequest.getClassroom().getNumber(), emailMessage);

            // Отправлеям сообщение лаборанту
            emailMessage = "Заведующий лабораториями " + by.getLastName() + " " + by.getFirstName().charAt(0) + "." +
                    by.getPatronymic().charAt(0) + "." + " дал Вам поручение на установку " +
                    installationRequest.getSoftware().getTitle() + " в аудиторию №" +
                    installationRequest.getClassroom().getNumber() + ". Загляните в личный кабинет";

            emailService.sendMessage(to.getEmail(), "Поручение по установке ПО в аудиторию", emailMessage);

            return Urls.HEAD_OF_LABORATORIES_URL.getUrlString() + assignedBy;
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

            String emailMessage = "Заведующий лабораториями " + user.getLastName() + " " + user.getFirstName().charAt(0) + "." +
                    user.getPatronymic().charAt(0) + "." + " сменил статус Вашего поручения на \"" + status.getStatusName() +
                    "\" по поручению №" + installationTask.getId() + ". Загляните в личный кабинет";

            emailService.sendMessage(installationTask.getAssignedTo().getEmail(),
                    "Поручение по установке ПО в аудиторию",
                    emailMessage);

            return Urls.HEAD_OF_LABORATORIES_URL.getUrlString() + userId;
        }
        catch (NoSuchElementException ex) {
            logger.error("@{}: === ПРОИЗОШЛА ОШИБКА ===", username, ex);
            return ErrorHandler.errorPage("Не найден объект", "Объект не найден в системе", model);
        }
        catch (Exception ex) {
            logger.error("Ошибка при обновлении статуса", ex);
            return ErrorHandler.errorPage("Ошибка при отправке сообщения на почту",
                    "Сообщение не отправлено на указанный адрес, проверьте соединение с интернетом", model);
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

            installationRequest.setComment(comment);
            installationRequest.setStatus(InstallationRequestStatus.REJECTED);
            installationRequestService.save(installationRequest);

            String emailMessage = "Заведующий лабораториями " + user.getLastName() + " " + user.getFirstName().charAt(0) +
                    "." + user.getPatronymic().charAt(0) + "." + " отклонил заявку. Причина: " + comment;

            emailService.sendMessage(installationRequest.getUser().getEmail(),
                    "Заявка на установку " + installationRequest.getSoftware().getTitle() + " в аудиторию №" +
                    installationRequest.getClassroom().getNumber(), emailMessage);

            return Urls.HEAD_OF_LABORATORIES_URL.getUrlString() + userId;
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
                    null, software, quantity, user, LocalDate.now(), LicenseRequestStatus.PENDING, reason
            );
            licenseRequestService.save(licenseRequest);

            String emailMessage = "Заведующий лабораториями " + user.getLastName() + " " + user.getFirstName().charAt(0) + "."
                    + user.getPatronymic().charAt(0) + "." + " сформировал запрос на закупку лицензии." +
                    "\nПрограммное обеспечение: " + software.getTitle() + " " + software.getVersion() +
                    "\nКоличество лицензий(шт.): " + quantity + "\nЗагляните в личный кабинет";

            for (User u : sam_managers) {
                emailService.sendMessage(u.getEmail(), "Запрос на закупку лицензии", emailMessage);
            }

            return Urls.HEAD_OF_LABORATORIES_URL.getUrlString() + userId;
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