package ru.thendont.software_accounting.service.enums;

/**
 * Перечисление url-строк, используемых в системе
 * @author thendont
 * @version 1.0
 */
public enum Urls {

    /**
     * Url панели администратора
     */
    ADMIN_URL("redirect:/admin/dashboard?userId="),

    /**
     * Url панели преподавателя
     */
    TEACHER_URL("redirect:/teacher/dashboard?userId="),

    /**
     * Url панели заведующего лабораториями
     */
    HEAD_OF_LABORATORIES_URL("redirect:/hol/dashboard?userId="),

    /**
     * Url панели лаборанта
     */
    LABORATORY_ASSISTANT("redirect:/assistant/dashboard?userId="),

    /**
     * Url панели менеджера по управлению программными активами
     */
    SAM_MANAGER("redirect:/manager/dashboard?userId="),

    /**
     * Url страницы посетителей и неподтвержденных пользователей
     */
    VISITOR_URL("redirect:/visitor/dashboard?userId="),

    /**
     * Url страницы подтверждения пользователей
     */
    PENDING_USERS_URL("redirect:/pending-users/dashboard?userId=");

    private final String urlString;

    Urls(String urlString) {
        this.urlString = urlString;
    }

    public String getUrlString() {
        return urlString;
    }
}