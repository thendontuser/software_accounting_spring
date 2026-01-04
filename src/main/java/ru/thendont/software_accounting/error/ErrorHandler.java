package ru.thendont.software_accounting.error;

import org.springframework.ui.Model;
import ru.thendont.software_accounting.util.ConstantStrings;

import java.time.LocalDate;

public class ErrorHandler {

    public static String errorPage(String title, String message, Model model) {
        model.addAttribute("errorTitle", title);
        model.addAttribute("errorMessage", message);
        model.addAttribute("timestamp", LocalDate.now());
        return ConstantStrings.ERROR_PAGE;
    }
}