package ru.thendont.software_accounting.util;

import ru.thendont.software_accounting.entity.InstallationRequest;
import ru.thendont.software_accounting.entity.User;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Общий утилитный класс для часто используемых методов
 * @author thendont
 * @version 1.0
 */
public final class Util {

    /**
     * Возвращает текущую дату
     * @return Текущая дата
     */
    public static LocalDate getCurrentDate() {
        return LocalDate.now();
    }

    /**
     * Возвращает текущую дату и время
     * @return Текущая дата и время
     */
    public static LocalDateTime getCurrentDateTime() {
        return LocalDateTime.now();
    }

    /**
     * Возвращает фамилию и инициалы пользователя. Пример: Иванов Иван Александович -> Иванов И.А.
     * @param user пользователь
     * @return Строка в формате "Фамилия первая_буква_имени.первая_буква_отчества."
     */
    public static String getUserInitials(User user) {
        return user.getLastName() + " " + user.getFirstName().charAt(0) + "." + user.getPatronymic().charAt(0) + ".";
    }

    /**
     * Возвращает сообщение о заявке на установку ПО в аудиторию, которые берутся из параметра request
     * @param request заявка на установку ПО
     * @return Строка в формате "Заявка на установку _ПО_ в аудиторию № _номер_аудитории_"
     */
    public static String getInstallationRequestEmailSubject(InstallationRequest request) {
        return "Заявка на установку " + request.getSoftware().getTitle() + " в аудиторию №" + request.getClassroom().getNumber();
    }
}