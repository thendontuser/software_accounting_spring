package ru.thendont.software_accounting.audit.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.LogoutSuccessEvent;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import ru.thendont.software_accounting.audit.service.session.AuditSessionService;

/**
 * Представляет из себя слушателя выхода пользователей из системы
 * @author thendont
 * @version 1.0
 */
@Component
public class LogoutSuccessListener {

    @Autowired
    private AuditSessionService auditSessionService;

    /**
     * Метод представляет из себя обработчик события успешного выхода пользователя из системы
     * @param event событие успешного выхода пользователя
     */
    @EventListener
    public void onLogoutSuccess(LogoutSuccessEvent event) {
        Authentication authentication = event.getAuthentication();
        String username = authentication.getName();

        if (!"anonymousUser".equals(username)) {
            auditSessionService.logUserLogout(username);
        }
    }
}