package com.rojas.remodeling.Api_rojas_remodeling.service.implementation;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Async
    public void sendEmail(String to, String subject, String body) {
        try {
            MimeMessage message = mailSender.createMimeMessage();

            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom("Rojas Remodeling <" + fromEmail + ">");
            helper.setReplyTo(fromEmail);
            helper.setTo(to);
            helper.setSubject(subject);

            String htmlBody = "<div style='font-family: Arial, sans-serif; color: #333;'>"
                    + "<h2>Actualización de Proyecto</h2>"
                    + "<p>" + body.replace("\n", "<br>") + "</p>"
                    + "<hr>"
                    + "<p style='font-size: 12px; color: #777;'>Este es un mensaje automático del sistema. Por favor no respondas a este correo.</p>"
                    + "</div>";

            helper.setText(htmlBody, true);

            mailSender.send(message);
        } catch (Exception e) {
            System.err.println("Error al enviar el correo a " + to + ": " + e.getMessage());
        }
    }
}