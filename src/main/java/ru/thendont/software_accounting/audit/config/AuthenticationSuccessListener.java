package ru.thendont.software_accounting.audit.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import ru.thendont.software_accounting.audit.service.session.AuditSessionService;

/**
 * Представляет из себя слушателя аутентификации пользователей
 * @author thendont
 * @version 1.0
 */
@Component
public class AuthenticationSuccessListener {

    @Autowired
    private AuditSessionService auditSessionService;

    /**
     * Метод представляет из себя обработчик события успешной аутентификации пользователя
     * @param event событие успешной аутентификации
     */
    @EventListener
    public void onAuthenticationSuccess(AuthenticationSuccessEvent event) {
        Authentication authentication = event.getAuthentication();
        String username = authentication.getName();

        if (!"anonymousUser".equals(username)) {
            auditSessionService.logUserLogin(username);
        }
    }
}