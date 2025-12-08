package ru.thendont.software_accounting.controllers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.mail.MailException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.thendont.software_accounting.entity.InstallationRequest;
import ru.thendont.software_accounting.entity.SoftwareInstallation;
import ru.thendont.software_accounting.entity.User;
import ru.thendont.software_accounting.service.InstallationRequestService;
import ru.thendont.software_accounting.service.SoftwareInstallationService;
import ru.thendont.software_accounting.service.UserService;
import ru.thendont.software_accounting.service.email.EmailHelper;
import ru.thendont.software_accounting.util.InstallationRequestsStatus;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;

@Controller
@RequestMapping("/manager")
public class ManagerPageController {

    private static final Logger logger = LogManager.getLogger(ManagerPageController.class);

    private final UserService userService;
    private final SoftwareInstallationService softwareInstallationService;
    private final InstallationRequestService installationRequestService;
    private final EmailHelper emailHelper;

    private String username;

    public ManagerPageController(UserService userService,
                                 SoftwareInstallationService softwareInstallationService,
                                 InstallationRequestService installationRequestService,
                                 EmailHelper emailHelper) {
        this.userService = userService;
        this.softwareInstallationService = softwareInstallationService;
        this.installationRequestService = installationRequestService;
        this.emailHelper = emailHelper;
    }

    @GetMapping("/dashboard")
    public String showDashboard(@RequestParam Long userId, Model model) {
        try {
            User user = userService.findById(userId).orElseThrow();
            username = user.getLogin();
            logger.info("@{}: === ПОЛЬЗОВАТЕЛЬ С ID {} УСПЕШНО НАЙДЕН ===", username, user.getId());

            List<InstallationRequest> requests = installationRequestService.findByDepartmentNumber(
                    user.getDepartment().getDepNumber()
            );
            logger.info("@{}: === ЗАЯВКИ ПОЛЬЗОВАТЕЛЕЙ УСПЕШНО НАЙДЕНЫ ===", username);

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
            logger.error("@{}: === ПРОИЗОШЛА ОШИБКА ===", username, ex);
            return handleException("Не найден объект", ex.getMessage(), model);
        }
    }

    @PostMapping("/request/handle")
    public String handleRequest(@RequestParam Long requestId, @RequestParam Long userId,
                                 @RequestParam String status, Model model) {
        try {
            InstallationRequest request = installationRequestService.findById(requestId).orElseThrow();
            logger.info("@{}: === ЗАЯВКА С ID {} УСПЕШНО НАЙДЕНА ===", username, request.getId());

            if (status.equals(InstallationRequestsStatus.APPROVED)) {
                if (!installationRequestService.isPossibleInstallSoftware(request)) {
                    model.addAttribute("installFlag", false);
                    model.addAttribute("installFlagMessage",
                            "Нельзя установить ПО \"" + request.getSoftware().getTitle() +
                            "\" на устройство \"" + request.getDevice().getTitle() + "\"\nЗаявка автоматически отклонена");
                    status = InstallationRequestsStatus.REJECTED;
                    logger.info("@{}: === СТАТУС ЗАЯВКИ ИЗМЕНЕН НА \"{}\" ===", username, status);
                } else {
                    softwareInstallationService.save(new SoftwareInstallation(null, request.getSoftware(),
                            request.getDevice(), request.getUser(), LocalDate.now()));
                    logger.info("@{}: === НОВАЯ УСТАНОВКА СПЕШНО СОХРАНЕНА ===", username);
                }
            }
            request.setStatus(status);
            installationRequestService.save(request);
            logger.info("@{}: === ЗАЯВКА УСПЕШНО ИЗМЕНЕНА ===", username);

            String toAddress = request.getUser().getEmail();
            String subject = "Решение по заявке \"Установка " + request.getSoftware().getTitle() +
                    " на " + request.getDevice().getTitle() + "\"";
            String statusInMsg = status.equals(InstallationRequestsStatus.APPROVED) ? "одобрена" : "отклонена";
            emailHelper.sendMessage(toAddress, subject, "Ваша заявка " + statusInMsg + " руководителем");
            return showDashboard(userId, model);
        }
        catch (NoSuchElementException ex) {
            logger.error("@{}: === ПРОИЗОШЛА ОШИБКА ===", username, ex);
            return handleException("Заявка не найдена", "Не найдена требуемая заявка", model);
        }
        catch (MailException ex) {
            logger.error("@{}: === ПРОИЗОШЛА ОШИБКА ===", username, ex);
            return handleException("Ошибка отправки сообщения на почту", "Плохое подключение к сети", model);
        }
    }

    private String handleException(String title, String message, Model model) {
        model.addAttribute("errorTitle", title);
        model.addAttribute("errorMessage", message);
        model.addAttribute("timestamp", LocalDate.now());
        return "error-page";
    }
}