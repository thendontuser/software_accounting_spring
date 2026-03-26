package ru.thendont.software_accounting.service.email;

import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

/**
 * Класс, предоставляющий работу с электронной почтой
 * @author thendont
 * @version 1.0
 */
@Service
public class EmailService {

    @Autowired
    private Logger logger;

    @Autowired
    private JavaMailSender javaMailSender;

    @Value("${spring.mail.from}")
    private String fromUser;

    /**
     * Отправляет сообщение на почту
     * @param toAddress email-адрес адресата
     * @param subject тема сообщения
     * @param message текст сообщения
     * @throws MailException возникает при ошибках отправки сообщения, например, если отсутствует соединение с интернетом
     */
    public void sendMessage(String toAddress, String subject, String message) throws MailException {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setFrom(fromUser);
        mailMessage.setTo(toAddress);
        mailMessage.setSubject(subject);
        mailMessage.setText(message);
        javaMailSender.send(mailMessage);
        logger.info("=== ОТПРАВЛЕНО СООБЩЕНИЕ НА ПОЧТУ <{}> ===", toAddress);
    }
}