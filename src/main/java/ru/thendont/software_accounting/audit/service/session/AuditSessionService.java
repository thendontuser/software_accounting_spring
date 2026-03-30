package ru.thendont.software_accounting.audit.service.session;

import jakarta.transaction.Transactional;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.thendont.software_accounting.audit.entity.AuditUserSession;
import ru.thendont.software_accounting.entity.User;
import ru.thendont.software_accounting.service.UserService;
import ru.thendont.software_accounting.util.ConstantStrings;
import ru.thendont.software_accounting.util.Util;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;

/**
 * Класс-сервис для ведения аудита пользователей
 * @author thendont
 * @version 1.0
 */
@Service
public class AuditSessionService {

    @Autowired
    private Logger logger;

    @Autowired
    private UserService userService;

    @Autowired
    private AuditUserSessionService auditUserSessionService;

    /**
     * Заносит информацию о входе пользователя в аудит
     * @param username имя пользователя
     */
    @Transactional
    public void logUserLogin(String username) {
        try {
            User user = userService.findByUsername(username).orElseThrow();
            String sessionId = SessionContextHolder.generateSessionId();

            AuditUserSession auditUserSession = new AuditUserSession(
                    null, user, sessionId, Util.getCurrentDateTime(), null, true
            );
            auditUserSessionService.save(auditUserSession);

            SessionContextHolder.setCurrentSessionId(sessionId);
        }
        catch (NoSuchElementException ex) {
            logger.error("logUserLogin. @{}: === ПРОИЗОШЛА ОШИБКА ===: {}", username, ConstantStrings.USER_NOT_FOUND_MESSAGE);
        }
    }

    /**
     * Заносит информацию о выходе пользователя в аудит
     * @param username имя пользователя
     */
    @Transactional
    public void logUserLogout(String username) {
        try {
            User user = userService.findByUsername(username).orElseThrow();
            String oldSessionId = SessionContextHolder.getCurrentSessionId();
            LocalDateTime loginTime = auditUserSessionService.findBySessionId(oldSessionId).orElseThrow().getLoginTime();

            String newSessionId = SessionContextHolder.generateSessionId();
            AuditUserSession auditUserSession = new AuditUserSession(
                    null, user, newSessionId, loginTime, Util.getCurrentDateTime(), false
            );

            auditUserSessionService.save(auditUserSession);

            SessionContextHolder.setCurrentSessionId(newSessionId);
        }
        catch (NoSuchElementException ex) {
            logger.error("logUserLogout. @{}: === ПРОИЗОШЛА ОШИБКА ===: {}", username, ConstantStrings.USER_NOT_FOUND_MESSAGE);
        }
    }
}