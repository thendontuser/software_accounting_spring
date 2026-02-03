package ru.thendont.software_accounting.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import ru.thendont.software_accounting.util.Urls;

@Controller
public class MainController {

    @GetMapping("/")
    public String redirectToVisitorPage() {
        return Urls.VISITOR_URL;
    }
}