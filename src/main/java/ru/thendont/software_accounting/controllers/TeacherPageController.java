package ru.thendont.software_accounting.controllers;

import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import ru.thendont.software_accounting.entity.*;
import ru.thendont.software_accounting.error.ErrorHandler;
import ru.thendont.software_accounting.service.ClassroomService;
import ru.thendont.software_accounting.service.InstallationRequestService;
import ru.thendont.software_accounting.service.SoftwareService;
import ru.thendont.software_accounting.service.UserService;
import ru.thendont.software_accounting.service.enums.InstallationRequestStatus;
import ru.thendont.software_accounting.service.enums.Urls;
import ru.thendont.software_accounting.util.ConstantStrings;
import ru.thendont.software_accounting.util.Util;

import java.util.NoSuchElementException;

/**
 * Контроллер панели преподавателя
 * @author thendont
 * @version 1.2
 */
@Controller
@RequestMapping("/teacher")
public class TeacherPageController {

    @Autowired
    private Logger logger;

    private String username;

    @Autowired
    private UserService userService;

    @Autowired
    private SoftwareService softwareService;

    @Autowired
    private InstallationRequestService installationRequestService;

    @Autowired
    private ClassroomService classroomService;

    /**
     * Отображает html-страницу личного кабинета
     * @param userId идентификатор пользователя, осуществившего вход на страницу
     * @param model экземпляр интерфейса Model для добавления атрибутов в шаблон
     * @return Имя шаблона страницы преподавателя
     */
    @GetMapping("/dashboard")
    public String showDashboard(@RequestParam Long userId, Model model) {
        try {
            User user = userService.findById(userId).orElseThrow();
            username = user.getUsername();
            model.addAttribute("user", user);
            model.addAttribute("software", softwareService.findAll());
            model.addAttribute("classroomList", classroomService.findByKafedra(user.getKafedra()));
            return "teacher-page";
        }
        catch (NoSuchElementException ex) {
            logger.error("@{}: === ПРОИЗОШЛА ОШИБКА ===", username, ex);
            return ErrorHandler.errorPage(ConstantStrings.USER_NOT_FOUND_TITLE, ConstantStrings.USER_NOT_FOUND_MESSAGE, model);
        }
    }

    /**
     * Метод подачи заявки на установку ПО. Сохраняет новую запись в таблице заявок на установку ПО
     * @param userId идентификатор пользователя, подающего заявку
     * @param softwareId идентификатор ПО
     * @param classroomId идентификатор аудитории
     * @param comment комментарий
     * @param model экземпляр интерфейса Model для добавления атрибутов в шаблон
     * @return Url-адрес страницы преподавателя
     */
    @PostMapping("/request")
    public String sendRequest(@RequestParam Long userId, @RequestParam Long softwareId, @RequestParam Long classroomId,
                              @RequestParam(required = false) String comment, Model model) {
        try {
            User user = userService.findById(userId).orElseThrow();
            Software software = softwareService.findById(softwareId).orElseThrow();
            Classroom classroom = classroomService.findById(classroomId).orElseThrow();

            if (installationRequestService.existsBy(software, classroom)) {
                model.addAttribute("errorMessage", "Заявка на установку данного ПО в выбранной аудитории уже существует!");
                model.addAttribute("errorType", "duplicate_request");

                model.addAttribute("user", user);
                model.addAttribute("software", softwareService.findAll());
                model.addAttribute("classroomList", classroomService.findByKafedra(user.getKafedra()));

                model.addAttribute("lastSoftwareId", softwareId);
                model.addAttribute("lastClassroomId", classroomId);
                model.addAttribute("lastComment", comment);

                logger.warn("@{}: === ПОПЫТКА СОЗДАНИЯ ДУБЛИКАТА ЗАЯВКИ: ПО={}, Аудитория={} ===",
                        username, software.getTitle(), classroom.getNumber());
                return "teacher-page";
            }

            InstallationRequest request = new InstallationRequest(null, software, classroom, user, Util.getCurrentDate(),
                    InstallationRequestStatus.PENDING, comment);

            installationRequestService.save(request);
            logger.info("@{}: === ЗАЯВКА УСПЕШНО СОХРАНЕНА ===", username);
            return Urls.TEACHER_URL.getUrlString() + userId;
        }
        catch (NoSuchElementException ex) {
            logger.error("@{}: === ПРОИЗОШЛА ОШИБКА ===", username, ex);
            return ErrorHandler.errorPage(ConstantStrings.OBJECT_NOT_FOUND_TITLE, ConstantStrings.OBJECT_NOT_FOUND_MESSAGE, model);
        }
    }
}