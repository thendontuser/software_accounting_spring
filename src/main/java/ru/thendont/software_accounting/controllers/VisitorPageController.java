package ru.thendont.software_accounting.controllers;

import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.thendont.software_accounting.entity.User;
import ru.thendont.software_accounting.service.BaseCrudService;

@Controller
@RequestMapping("/visitor")
public class VisitorPageController {

    @Autowired
    private Logger logger;

    @Autowired
    private BaseCrudService<User> userBaseCrudService;

    @GetMapping("/dashboard")
    public String showDashboard(@RequestParam(required = false) Long userId, Model model) {
        logger.info("=== ПЕРЕХОД НА ГОСТЕВУЮ СТРАНИЦУ ===");
        User user = userId != null ? userBaseCrudService.findById(userId).orElse(null) : null;
        model.addAttribute("user", user);
        return "visitor-page";
    }
}