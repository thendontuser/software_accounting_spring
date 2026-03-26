package ru.thendont.software_accounting.service.enums;

/**
 * Перечисление ролей пользователей
 * @author thendont
 * @version 1.0
 */
public enum UserRoles {

    /**
     * Администратор
     */
    ADMIN,

    /**
     * Преподаватель
     */
    TEACHER,

    /**
     * Заведующий лабораториями
     */
    HEAD_OF_LABORATORIES,

    /**
     * Лаборант
     */
    LABORATORY_ASSISTANT,

    /**
     * Менеджер по управлению программынми активами
     */
    SAM_MANAGER;
}