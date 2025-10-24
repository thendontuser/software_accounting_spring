package ru.thendont.software_accounting.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/teacher")
public class TeacherPageController {

    @GetMapping("/dashboard")
    public String showDashboard() {
        return "teacher-page";
    }
}