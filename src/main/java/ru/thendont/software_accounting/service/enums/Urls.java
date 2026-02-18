package ru.thendont.software_accounting.service.enums;

public enum Urls {

    ADMIN_URL("redirect:/admin/dashboard?userId="),

    TEACHER_URL("redirect:/teacher/dashboard?userId="),

    HEAD_OF_LABORATORIES_URL("redirect:/hol/dashboard?userId="),

    LABORATORY_ASSISTANT("redirect:/assistant/dashboard?userId="),

    SAM_MANAGER("redirect:/manager/dashboard?userId="),

    VISITOR_URL("redirect:/visitor/dashboard?userId="),

    PENDING_USERS_URL("redirect:/pending-users/dashboard?userId=");

    private final String urlString;

    Urls(String urlString) {
        this.urlString = urlString;
    }

    public String getUrlString() {
        return urlString;
    }
}