package ru.thendont.software_accounting.service.report;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfWriter;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.thendont.software_accounting.entity.Software;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

public final class ReportService {

    private static final Logger logger = LogManager.getLogger(ReportService.class);

    public static void generateSoftwareReport(HttpServletResponse response, List<Software> softwareList, String title)
            throws IOException, DocumentException {
        Document document = new Document();
        PdfWriter.getInstance(document, response.getOutputStream());

        BaseFont baseFont = BaseFont.createFont("static/fonts/ctetbi.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
        Font font = new Font(baseFont, 12, Font.NORMAL);

        document.open();

        document.add(new Paragraph(title, font));
        document.add(new Paragraph("Дата формирования: " + LocalDate.now(), font));
        document.add(new Paragraph(" "));

        for (Software software : softwareList) {
            document.add(new Paragraph("Название: " + software.getTitle(), font));
            document.add(new Paragraph("Версия: " + software.getVersion(), font));
            document.add(new Paragraph("Разработчик: " + software.getDeveloper().getTitle(), font));
            document.add(new Paragraph(" "));
        }
        document.close();
        logger.info("=== ДОКУМЕНТ PDF УСПЕШНО СОЗДАЛСЯ ===");
    }
}