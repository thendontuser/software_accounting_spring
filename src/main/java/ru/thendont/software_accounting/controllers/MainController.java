package ru.thendont.software_accounting.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import ru.thendont.software_accounting.service.enums.Urls;

/**
 * Контроллер, определяющий первоначальную страницу, на которую входит пользователь
 * @author thendont
 * @version 1.0
 */
@Controller
public class MainController {

    /**
     * Перенаправяет на страницу посетителя
     * @return Url-адрес страницы посетителя
     */
    @GetMapping("/")
    public String redirectToVisitorPage() {
        return Urls.VISITOR_URL.getUrlString();
    }
}