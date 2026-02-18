package ru.thendont.software_accounting.service.enums;

import ru.thendont.software_accounting.util.Statusable;

public enum LicenseRequestStatus implements Statusable {

    PENDING("В рассмотрении"),

    APPROVED("Одобено"),

    REJECTED("Отклонено");

    private final String statusName;

    LicenseRequestStatus(String statusName) {
        this.statusName = statusName;
    }

    @Override
    public String getStatusName() {
        return statusName;
    }
}