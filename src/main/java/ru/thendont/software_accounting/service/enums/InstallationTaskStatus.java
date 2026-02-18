package ru.thendont.software_accounting.service.enums;

import ru.thendont.software_accounting.util.Statusable;

public enum InstallationTaskStatus implements Statusable {

    DONE("Выполнено"),

    NOT_COMPLETED("Не выполнено"),

    IN_EXECUTION("В выполнении");

    private final String statusName;

    InstallationTaskStatus(String statusName) {
        this.statusName = statusName;
    }

    @Override
    public String getStatusName() {
        return statusName;
    }
}