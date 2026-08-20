package com.rojas.remodeling.Api_rojas_remodeling.service.implementation;

import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendEmail(String toEmail, String subject, String content) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom("info@remomn.com <inforojasremomn@gmail.com>");
            helper.setTo(toEmail);
            helper.setSubject(subject);
            helper.setText(content, true);

            mailSender.send(message);
            System.out.println("¡Correo enviado con éxito vía SMTP de Brevo! Destinatario: " + toEmail + ", Asunto: " + subject);
        } catch (Exception e) {
            System.err.println("Error al enviar el correo por SMTP: " + e.getMessage());
        }
    }
}