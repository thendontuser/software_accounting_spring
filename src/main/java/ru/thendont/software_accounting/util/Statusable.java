package ru.thendont.software_accounting.util;

/**
 * Интерфейс, который наследуют перечисления статусов
 * @author thendont
 * @version 1.0
 */
public interface Statusable {

    /**
     * Возвращает имя перечисления
     * @return Имя перечисления
     */
    String getStatusName();
}