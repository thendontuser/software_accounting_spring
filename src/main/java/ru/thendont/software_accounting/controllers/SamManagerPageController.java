package ru.thendont.software_accounting.controllers;

import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.thendont.software_accounting.entity.*;
import ru.thendont.software_accounting.error.ErrorHandler;
import ru.thendont.software_accounting.service.*;
import ru.thendont.software_accounting.service.email.EmailService;
import ru.thendont.software_accounting.service.enums.LicenseRequestStatus;
import ru.thendont.software_accounting.service.enums.Urls;
import ru.thendont.software_accounting.service.report.ReportService;
import ru.thendont.software_accounting.util.ConstantStrings;
import ru.thendont.software_accounting.util.Util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Контроллер панели sam-менеджера(менеджер по управлению программными активами)
 * @author thendont
 * @version 1.0
 */
@Controller
@RequestMapping("/manager")
public class SamManagerPageController {

    @Autowired
    private Logger logger;

    private String username;

    @Autowired
    private UserService userService;

    @Autowired
    private SoftwareService softwareService;

    @Autowired
    private LicenseService licenseService;

    @Autowired
    private LicenseRequestService licenseRequestService;

    @Autowired
    private PurchaseService purchaseService;

    @Autowired
    private ReportService reportService;

    @Autowired
    private EmailService emailService;

    /**
     * Отображает html-страницу личного кабинета
     * @param userId идентификатор пользователя, осуществившего вход на страницу
     * @param model экземпляр интерфейса Model для добавления атрибутов в шаблон
     * @return Имя шаблона страницы sam-менеджера
     */
    @GetMapping("/dashboard")
    public String showDashboard(@RequestParam Long userId, Model model) {
        try {
            User user = userService.findById(userId).orElseThrow();

            List<Software> softwareList = softwareService.findAll();

            List<License> licenses = licenseService.findAll();
            List<License> expiringLicenses = licenseService.findExpiringLicenses();
            List<LicenseRequest> licenseRequests = licenseRequestService.findAll();

            List<Purchase> purchases = purchaseService.findAll();

            int expiringCount = expiringLicenses.size();
            int pendingRequestsCount = licenseRequestService.findByStatus(LicenseRequestStatus.PENDING).size();
            int approvedRequestsCount = licenseRequestService.findByStatus(LicenseRequestStatus.APPROVED).size();

            model.addAttribute("user", user);
            model.addAttribute("softwareList", softwareList);
            model.addAttribute("licenses", licenses);
            model.addAttribute("purchases", purchases);
            model.addAttribute("expiringLicenses", expiringLicenses);
            model.addAttribute("licenseRequests", licenseRequests);
            model.addAttribute("expiringCount", expiringCount);
            model.addAttribute("pendingRequestsCount", pendingRequestsCount);
            model.addAttribute("approvedRequestsCount", approvedRequestsCount);

            return "sam-manager-page";
        }
        catch (NoSuchElementException ex) {
            logger.error("@{}: === ПРОИЗОШЛА ОШИБКА ===", username, ex);
            return ErrorHandler.errorPage(ConstantStrings.USER_NOT_FOUND_TITLE, ConstantStrings.USER_NOT_FOUND_MESSAGE, model);
        }
    }

    /**
     * Метод покупки лицензии. Создает новую запись в таблице покупок
     * @param requestId идентификатор заявки на закупку лицензии
     * @param boughtBy идентификатор пользователя, осуществляющего покупку
     * @param licenseId идентификатор лицензии
     * @param amount количество лицензий
     * @param contractNumber контрактный номер покупки
     * @param dateBegin дата начачла дейтсвия лицензии
     * @param dateEnd дата окончания действия лицензии
     * @param totalCost общая стоимость покупки
     * @param model экземпляр интерфейса Model для добавления атрибутов в шаблон
     * @return Url-адрес страницы sam-менеджера
     */
    @PostMapping("/purchase/create")
    public String buyLicense(@RequestParam Long requestId,
                             @RequestParam Long boughtBy,
                             @RequestParam Long licenseId,
                             @RequestParam Integer amount,
                             @RequestParam String contractNumber,
                             @RequestParam LocalDate dateBegin,
                             @RequestParam LocalDate dateEnd,
                             @RequestParam Integer totalCost,
                             Model model) {
        try {
            LicenseRequest licenseRequest = licenseRequestService.findById(requestId).orElseThrow();
            User by = userService.findById(boughtBy).orElseThrow();
            License license = licenseService.findById(licenseId).orElseThrow();

            license.setDateBegin(dateBegin);
            license.setDateEnd(dateEnd);

            licenseRequest.setAmount(amount);
            licenseRequest.setStatus(LicenseRequestStatus.APPROVED);

            Purchase purchase = new Purchase(
                    null, licenseRequest, by, Util.getCurrentDate(), contractNumber, totalCost, license
            );
            purchaseService.save(purchase);

            String emailMessage = "Менеджер по управлению программными активами " + Util.getUserInitials(by) + " " +
                    "отправил заявление на закупку лицензии \"" + license.getSoftware().getTitle() + " " +
                    license.getSoftware().getVersion() + " - " + license.getType() + "\". Статус заявки изменен на \"" +
                    LicenseRequestStatus.APPROVED.getStatusName() + "\".";

            emailService.sendMessage(licenseRequest.getRequestedBy().getEmail(), "Заявка на закупку лицензии",
                    emailMessage);

            return Urls.SAM_MANAGER.getUrlString() + boughtBy;
        }
        catch (NoSuchElementException ex) {
            logger.error("@{}: === ПРОИЗОШЛА ОШИБКА ===", username, ex);
            return ErrorHandler.errorPage(ConstantStrings.USER_NOT_FOUND_TITLE, ConstantStrings.USER_NOT_FOUND_MESSAGE, model);
        }
        catch (Exception ex) {
            logger.error("Ошибка при обновлении статуса", ex);
            return ErrorHandler.errorPage(ConstantStrings.EMAIL_ERROR_TITLE, ConstantStrings.EMAIL_ERROR_MESSAGE, model);
        }
    }

    /**
     * Метод продления действия лицензии
     * @param licenseId идентификатор лицензии
     * @param userId идентийикатор пользователя, продляющего лицензию
     * @param durationMonths количество месяцев, на которое продляется лицензия
     * @param contractNumber контрактный номер
     * @param model экземпляр интерфейса Model для добавления атрибутов в шаблон
     * @return Url-адрес страницы sam-менеджера
     */
    @PostMapping("/renewal/create")
    public String renewalLicense(@RequestParam Long licenseId,
                                 @RequestParam Long userId,
                                 @RequestParam int durationMonths,
                                 @RequestParam String contractNumber,
                                 Model model) {
        try {
            License license = licenseService.findById(licenseId).orElseThrow();
            Purchase purchase = purchaseService.findByLicense(license).orElseThrow();
            LocalDate newDateEnd = licenseService.extendEnd(license, durationMonths);

            license.setDateEnd(newDateEnd);
            purchase.setContractNumber(contractNumber);

            purchaseService.save(purchase);

            return Urls.SAM_MANAGER.getUrlString() + userId;
        }
        catch (NoSuchElementException ex) {
            logger.error("@{}: === ПРОИЗОШЛА ОШИБКА ===", username, ex);
            return ErrorHandler.errorPage(ConstantStrings.USER_NOT_FOUND_TITLE, ConstantStrings.USER_NOT_FOUND_MESSAGE, model);
        }
    }

    /**
     * Метод генерации отчета по закупкам лицензий
     * @param userId идентификатор пользователя, генерирующего отчет
     * @return Установка отчета PDF из браузера
     */
    @GetMapping("/report/purchases")
    public ResponseEntity<byte[]> generateReport(@RequestParam Long userId) {
        try {
            User user = userService.findById(userId).orElseThrow();
            byte[] pdfContent = reportService.generatePurchaseReport(user);
            String filename = String.format("purchase_report_%s.pdf",
                    Util.getCurrentDate().format(DateTimeFormatter.ofPattern("yyyyMMdd")));

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                    .contentType(MediaType.APPLICATION_PDF)
                    .contentLength(pdfContent.length)
                    .body(pdfContent);
        }
        catch (Exception ex) {
            logger.error("@{}: === ОШИБКА ПРИ ГЕНЕРИРОВАНИИ ОТЧЕТА ===", username, ex);
            return ResponseEntity.badRequest().build();
        }
    }
}