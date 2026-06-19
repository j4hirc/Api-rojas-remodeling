package com.rojas.remodeling.Api_rojas_remodeling.service.implementation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendEmail(String toEmail, String subject, String content) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("juanalberto25423@gmail.com");
            message.setTo(toEmail);
            message.setSubject(subject);
            message.setText(content);

            mailSender.send(message);
            System.out.println("¡Correo enviado con éxito vía SMTP de Brevo!");
        } catch (Exception e) {
            System.err.println("Error al enviar el correo por SMTP: " + e.getMessage());
        }
    }
}