package ru.thendont.software_accounting.audit.controller;

import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.thendont.software_accounting.audit.entity.AuditDeletedArchive;
import ru.thendont.software_accounting.audit.entity.AuditLog;
import ru.thendont.software_accounting.audit.entity.AuditLogDetails;
import ru.thendont.software_accounting.audit.entity.AuditUserSession;
import ru.thendont.software_accounting.audit.service.AuditDeletedArchiveService;
import ru.thendont.software_accounting.audit.service.AuditLogDetailsService;
import ru.thendont.software_accounting.audit.service.AuditLogService;
import ru.thendont.software_accounting.audit.service.session.AuditUserSessionService;
import ru.thendont.software_accounting.entity.User;
import ru.thendont.software_accounting.error.ErrorHandler;
import ru.thendont.software_accounting.service.UserService;
import ru.thendont.software_accounting.util.ConstantStrings;

import java.util.List;
import java.util.NoSuchElementException;

/**
 * Контроллер панели аудита
 * @author thendont
 * @version 1.0
 */
@Controller
@RequestMapping("/audit")
public class AuditPageController {

    @Autowired
    private Logger logger;

    @Autowired
    private UserService userService;

    @Autowired
    private AuditUserSessionService auditUserSessionService;

    @Autowired
    private AuditDeletedArchiveService auditDeletedArchiveService;

    @Autowired
    private AuditLogDetailsService auditLogDetailsService;

    @Autowired
    private AuditLogService auditLogService;

    /**
     * Отображает html-страницу панели аудита
     * @param userId идентификатор пользователя, осуществившего вход на страницу
     * @param model экземпляр интерфейса Model для добавления атрибутов в шаблон
     * @return Имя шаблона страницы панели аудита
     */
    @GetMapping("/dashboard")
    public String showDashboard(@RequestParam Long userId, Model model) {
        try {
            User user = userService.findById(userId).orElseThrow();

            List<AuditUserSession> auditUserSessions = auditUserSessionService.findAll();
            List<AuditDeletedArchive> auditDeletedArchives = auditDeletedArchiveService.findAll();
            List<AuditLogDetails> auditLogDetails = auditLogDetailsService.findAll();
            List<AuditLog> auditLogs = auditLogService.findAll();

            model.addAttribute("user", user);
            model.addAttribute("auditUserSessions", auditUserSessions);
            model.addAttribute("auditDeletedArchives", auditDeletedArchives);
            model.addAttribute("auditLogDetails", auditLogDetails);
            model.addAttribute("auditLogs", auditLogs);

            return "audit-page";
        }
        catch (NoSuchElementException ex) {
            logger.error("@admin: === ПРОИЗОШЛА ОШИБКА ===", ex);
            return ErrorHandler.errorPage(ConstantStrings.USER_NOT_FOUND_TITLE, ConstantStrings.USER_NOT_FOUND_MESSAGE, model);
        }
    }
}