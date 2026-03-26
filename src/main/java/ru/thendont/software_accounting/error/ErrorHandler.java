package ru.thendont.software_accounting.error;

import org.springframework.ui.Model;
import ru.thendont.software_accounting.util.ConstantStrings;

import java.time.LocalDate;

/**
 * Класс, обеспечивающий работу с ошибками, возникающих при работе в системе
 * @author thendont
 * @version 1.0
 */
public class ErrorHandler {

    /**
     * Отображает страницу с информацией об ошибках
     * @param title название ошибки
     * @param message описание ошибки
     * @param model экземпляр интерфейса Model для добавления атрибутов в шаблон
     * @return html-шаблон страницы ошибок
     */
    public static String errorPage(String title, String message, Model model) {
        model.addAttribute("errorTitle", title);
        model.addAttribute("errorMessage", message);
        model.addAttribute("timestamp", LocalDate.now());
        return ConstantStrings.ERROR_PAGE;
    }
}