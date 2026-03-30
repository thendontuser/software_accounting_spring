package ru.thendont.software_accounting.audit.service.session;

import java.util.UUID;

/**
 * Вспомогательный класс для хранения и генерации сессий пользователя
 * @author thendont
 * @version 1.0
 */
public final class SessionContextHolder {

    private static String currentSessionId = "";

    public static void setCurrentSessionId(String sessionId) {
        currentSessionId = sessionId;
    }

    public static String getCurrentSessionId() {
        return currentSessionId;
    }

    public static String generateSessionId() {
        return UUID.randomUUID().toString().replace("-", "");
    }
}