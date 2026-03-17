package ru.thendont.software_accounting.util;

import ru.thendont.software_accounting.entity.InstallationRequest;
import ru.thendont.software_accounting.entity.User;

import java.time.LocalDate;

public final class Util {

    public static LocalDate getCurrentDate() {
        return LocalDate.now();
    }

    public static String getUserInitials(User user) {
        return user.getLastName() + " " + user.getFirstName().charAt(0) + "." + user.getPatronymic().charAt(0) + ".";
    }

    public static String getInstallationRequestEmailSubject(InstallationRequest request) {
        return "Заявка на установку " + request.getSoftware().getTitle() + " в аудиторию №" + request.getClassroom().getNumber();
    }
}