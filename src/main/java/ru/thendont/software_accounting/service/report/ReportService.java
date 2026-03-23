package ru.thendont.software_accounting.service.report;

import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.thendont.software_accounting.entity.*;
import ru.thendont.software_accounting.service.InstallationRequestService;
import ru.thendont.software_accounting.service.InstallationTaskService;
import ru.thendont.software_accounting.service.PurchaseService;
import ru.thendont.software_accounting.service.SoftwareInstallationService;
import ru.thendont.software_accounting.util.ConstantStrings;
import ru.thendont.software_accounting.util.Util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class ReportService {

    @Autowired
    private PurchaseService purchaseService;

    @Autowired
    private InstallationRequestService installationRequestService;

    @Autowired
    private InstallationTaskService installationTaskService;

    @Autowired
    private SoftwareInstallationService softwareInstallationService;

    private PdfFont font;

    public byte[] generatePurchaseReport(User user) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try {
            font = getFont();

            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            document.add(new Paragraph("Отчет по закупкам лицензий")
                    .setFontSize(18)
                    .setBold()
                    .setTextAlignment(TextAlignment.CENTER)
                    .setFont(font));

            document.add(new Paragraph("Сформировал: " + user.getLastName() + " " + user.getFirstName() + " " +
                    user.getPatronymic())
                    .setFontSize(10)
                    .setMarginBottom(20)
                    .setFont(font));

            List<Purchase> purchases = purchaseService.findAll();
            if (purchases.isEmpty()) {
                document.add(new Paragraph("Список закупок лицензий пуст")
                        .setFont(font));
            }

            Table table = new Table(UnitValue.createPercentArray(new float[]{2, 2, 3, 2, 3}));
            table.setWidth(UnitValue.createPercentValue(100));

            table.addHeaderCell(new Cell().add(new Paragraph("Менеджер"))
                    .setBold()
                    .setFont(font));
            table.addHeaderCell(new Cell().add(new Paragraph("Дата"))
                    .setBold()
                    .setFont(font));
            table.addHeaderCell(new Cell().add(new Paragraph("Контрактный номер"))
                    .setBold()
                    .setFont(font));
            table.addHeaderCell(new Cell().add(new Paragraph("Стоимость"))
                    .setBold()
                    .setFont(font));
            table.addHeaderCell(new Cell().add(new Paragraph("Лицензия"))
                    .setBold()
                    .setFont(font));

            for (Purchase purchase : purchases) {
                table.addCell(new Cell().add(new Paragraph(Util.getUserInitials(user))
                        .setFont(font)));
                table.addCell(new Cell().add(new Paragraph(purchase.getBoughtAt().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")))
                        .setFont(font)));
                table.addCell(new Cell().add(new Paragraph(purchase.getContractNumber())
                        .setFont(font)));
                table.addCell(new Cell().add(new Paragraph(purchase.getCost().toString() + " руб.")
                        .setFont(font)));
                table.addCell(new Cell().add(new Paragraph(purchase.getLicense().getSoftware().getTitle() +
                        " " + purchase.getLicense().getSoftware().getVersion() + " - " + purchase.getLicense().getType())
                        .setFont(font)));
            }

            document.add(table);

            document.add(new Paragraph("\n\nОтчет сгенерирован: " +
                    Util.getCurrentDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")))
                    .setFontSize(8)
                    .setTextAlignment(TextAlignment.RIGHT)
                    .setFont(font));

            document.close();
        }
        catch (Exception ex) {
            throw new RuntimeException(ConstantStrings.PDF_CREATE_ERROR, ex);
        }

        return baos.toByteArray();
    }

    public byte[] generateReport(User user, String type, LocalDate dateFrom, LocalDate dateTo) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try {
            font = getFont();

            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            document.add(new Paragraph("Отчет по кафедре: " + user.getKafedra().getTitle())
                    .setFontSize(18)
                    .setBold()
                    .setTextAlignment(TextAlignment.CENTER)
                    .setFont(font));

            document.add(new Paragraph("Тип отчета: " + getReportTypeName(type))
                    .setFontSize(14)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setFont(font));

            document.add(new Paragraph("Период: " + formatDate(dateFrom) + " - " + formatDate(dateTo))
                    .setFontSize(12)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginBottom(20)
                    .setFont(font));

            document.add(new Paragraph("Сформировал: " + user.getLastName() + " " + user.getFirstName() + " " +
                    user.getPatronymic())
                    .setFontSize(10)
                    .setMarginBottom(20)
                    .setFont(font));

            switch (type) {
                case "requests":
                    generateRequestsReport(document, user.getKafedra(), dateFrom, dateTo);
                    break;
                case "tasks":
                    generateTasksReport(document, user.getKafedra(), dateFrom, dateTo);
                    break;
                case "software":
                    generateSoftwareReport(document, user.getKafedra());
                    break;
            }

            document.add(new Paragraph("\n\nОтчет сгенерирован: " +
                    Util.getCurrentDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")))
                    .setFontSize(8)
                    .setTextAlignment(TextAlignment.RIGHT)
                    .setFont(font));

            document.close();
        }
        catch (Exception ex) {
            throw new RuntimeException(ConstantStrings.PDF_CREATE_ERROR, ex);
        }

        return baos.toByteArray();
    }

    private void generateRequestsReport(Document document, Kafedra kafedra, LocalDate dateFrom, LocalDate dateTo) {
        List<InstallationRequest> requests = installationRequestService.findByKafedraAndDateBetween(
                kafedra, dateFrom, dateTo
        );

        document.add(new Paragraph("Отчет по заявкам")
                .setFontSize(16)
                .setBold()
                .setMarginTop(20)
                .setFont(font));

        if (requests.isEmpty()) {
            document.add(new Paragraph("Нет заявок за указанный период")
                    .setFont(font));
            return;
        }

        Table table = new Table(UnitValue.createPercentArray(new float[]{1, 2, 3, 2, 2, 2}));
        table.setWidth(UnitValue.createPercentValue(100));

        table.addHeaderCell(new Cell().add(new Paragraph("№"))
                .setBold()
                .setFont(font));
        table.addHeaderCell(new Cell().add(new Paragraph("Дата"))
                .setBold()
                .setFont(font));
        table.addHeaderCell(new Cell().add(new Paragraph("Преподаватель"))
                .setBold()
                .setFont(font));
        table.addHeaderCell(new Cell().add(new Paragraph("ПО"))
                .setBold()
                .setFont(font));
        table.addHeaderCell(new Cell().add(new Paragraph("Аудитория"))
                .setBold()
                .setFont(font));
        table.addHeaderCell(new Cell().add(new Paragraph("Статус"))
                .setBold()
                .setFont(font));

        int i = 1;
        for (InstallationRequest req : requests) {
            table.addCell(new Cell().add(new Paragraph(String.valueOf(i++))
                    .setFont(font)));
            table.addCell(new Cell().add(new Paragraph(req.getDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")))
                    .setFont(font)));
            table.addCell(new Cell().add(new Paragraph(req.getUser().getLastName() + " " +
                    req.getUser().getFirstName().charAt(0) + "." + req.getUser().getPatronymic().charAt(0) + ".")
                    .setFont(font)));
            table.addCell(new Cell().add(new Paragraph(req.getSoftware().getTitle())
                    .setFont(font)));
            table.addCell(new Cell().add(new Paragraph("№" + req.getClassroom().getNumber())
                    .setFont(font)));
            table.addCell(new Cell().add(new Paragraph(req.getStatus().getStatusName())
                    .setFont(font)));
        }

        document.add(table);

        document.add(new Paragraph("\nСтатистика:")
                .setBold()
                .setMarginTop(15)
                .setFont(font));
        document.add(new Paragraph("Всего заявок: " + requests.size())
                .setFont(font));
        document.add(new Paragraph("Утверждено: " + requests.stream()
                .filter(r -> r.getStatus().name().equals("APPROVED")).count())
                .setFont(font));
        document.add(new Paragraph("Отклонено: " + requests.stream()
                .filter(r -> r.getStatus().name().equals("REJECTED")).count())
                .setFont(font));
        document.add(new Paragraph("В ожидании: " + requests.stream()
                .filter(r -> r.getStatus().name().equals("PENDING")).count())
                .setFont(font));
    }

    private void generateTasksReport(Document document, Kafedra kafedra, LocalDate dateFrom, LocalDate dateTo) {
        List<InstallationTask> tasks = installationTaskService.findByKafedraAndDateBetween(kafedra, dateFrom, dateTo);

        document.add(new Paragraph("Отчет по поручениям")
                .setFontSize(16)
                .setBold()
                .setMarginTop(20)
                .setFont(font));

        if (tasks.isEmpty()) {
            document.add(new Paragraph("Нет поручений за указанный период")
                    .setFont(font));
            return;
        }

        Table table = new Table(UnitValue.createPercentArray(new float[]{1, 2, 2, 3, 2, 2, 2}));
        table.setWidth(UnitValue.createPercentValue(100));

        table.addHeaderCell(new Cell().add(new Paragraph("№"))
                .setBold()
                .setFont(font));
        table.addHeaderCell(new Cell().add(new Paragraph("Создано"))
                .setBold()
                .setFont(font));
        table.addHeaderCell(new Cell().add(new Paragraph("Срок"))
                .setBold()
                .setFont(font));
        table.addHeaderCell(new Cell().add(new Paragraph("Лаборант"))
                .setBold()
                .setFont(font));
        table.addHeaderCell(new Cell().add(new Paragraph("ПО"))
                .setBold()
                .setFont(font));
        table.addHeaderCell(new Cell().add(new Paragraph("Аудитория"))
                .setBold()
                .setFont(font));
        table.addHeaderCell(new Cell().add(new Paragraph("Статус"))
                .setBold()
                .setFont(font));

        int i = 1;
        for (InstallationTask task : tasks) {
            table.addCell(new Cell().add(new Paragraph(String.valueOf(i++))
                    .setFont(font)));
            table.addCell(new Cell().add(new Paragraph(task.getCreatedAt().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")))
                    .setFont(font)));
            table.addCell(new Cell().add(new Paragraph(task.getDeadline().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")))
                    .setFont(font)));
            table.addCell(new Cell().add(new Paragraph(task.getAssignedTo().getLastName() + " " +
                    task.getAssignedTo().getFirstName().charAt(0) + "." + task.getAssignedTo().getPatronymic().charAt(0) + ".")
                    .setFont(font)));
            table.addCell(new Cell().add(new Paragraph(task.getInstallationRequest().getSoftware().getTitle()))
                    .setFont(font));
            table.addCell(new Cell().add(new Paragraph("№" + task.getInstallationRequest().getClassroom().getNumber())
                    .setFont(font)));
            table.addCell(new Cell().add(new Paragraph(task.getStatus().getStatusName())
                    .setFont(font)));
        }

        document.add(table);
    }

    private void generateSoftwareReport(Document document, Kafedra kafedra) {
        List<SoftwareInstallation> installations = softwareInstallationService.findByKafedra(kafedra);

        document.add(new Paragraph("Отчет по установленному ПО")
                .setFontSize(16)
                .setBold()
                .setMarginTop(20)
                .setFont(font));

        if (installations.isEmpty()) {
            document.add(new Paragraph("Нет установленного ПО")
                    .setFont(font));
            return;
        }

        Table table = new Table(UnitValue.createPercentArray(new float[]{1, 3, 2, 2, 2}));
        table.setWidth(UnitValue.createPercentValue(100));

        table.addHeaderCell(new Cell().add(new Paragraph("№"))
                .setBold()
                .setFont(font));
        table.addHeaderCell(new Cell().add(new Paragraph("ПО"))
                .setBold()
                .setFont(font));
        table.addHeaderCell(new Cell().add(new Paragraph("Устройство"))
                .setBold()
                .setFont(font));
        table.addHeaderCell(new Cell().add(new Paragraph("Аудитория"))
                .setBold()
                .setFont(font));
        table.addHeaderCell(new Cell().add(new Paragraph("Дата установки"))
                .setBold()
                .setFont(font));

        int i = 1;
        for (SoftwareInstallation inst : installations) {
            table.addCell(new Cell().add(new Paragraph(String.valueOf(i++))
                    .setFont(font)));
            table.addCell(new Cell().add(new Paragraph(inst.getSoftware().getTitle())
                    .setFont(font)));
            table.addCell(new Cell().add(new Paragraph(inst.getDevice().getTitle())
                    .setFont(font)));
            table.addCell(new Cell().add(new Paragraph("№" + inst.getDevice().getClassroom().getNumber())
                    .setFont(font)));
            table.addCell(new Cell().add(new Paragraph(inst.getDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")))
                    .setFont(font)));
        }

        document.add(table);
    }

    private String getReportTypeName(String type) {
        return switch (type) {
            case "requests" -> "По заявкам";
            case "tasks" -> "По поручениям";
            case "software" -> "По установленному ПО";
            default -> type;
        };
    }

    private String formatDate(LocalDate date) {
        return date != null ? date.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")) : "не указано";
    }

    private PdfFont getFont() throws IOException {
        return PdfFontFactory.createFont("static/fonts/ctetbi.ttf", PdfEncodings.IDENTITY_H,
                PdfFontFactory.EmbeddingStrategy.PREFER_EMBEDDED);
    }
}