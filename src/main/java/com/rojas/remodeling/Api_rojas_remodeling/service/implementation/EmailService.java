package com.rojas.remodeling.Api_rojas_remodeling.service.implementation; //[cite: 1]

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class EmailService {

    // Lee la variable que pondremos en Render o en tu .env local
    @Value("${brevo.api.key}")
    private String apiKey;

    public void sendEmail(String toEmail, String subject, String content) {
        RestTemplate restTemplate = new RestTemplate();
        String url = "https://api.brevo.com/v3/smtp/email";

        // Configurar los headers con tu API Key
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("api-key", apiKey);

        // Armar el JSON del cuerpo de la petición según la doc de Brevo
        Map<String, Object> body = new HashMap<>();
        body.put("sender", Map.of("name", "Api Rojas Remodeling", "email", "tu_correo@gmail.com")); // Pon el correo que registraste en Brevo
        body.put("to", List.of(Map.of("email", toEmail)));
        body.put("subject", subject);
        body.put("htmlContent", content); // Usa "textContent" si mandas texto sin formato

        // Empaquetar todo y disparar el POST
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        try {
            restTemplate.postForEntity(url, request, String.class);
            System.out.println("¡Correo enviado con éxito vía Brevo!");
        } catch (Exception e) {
            System.err.println("Error al enviar el correo: " + e.getMessage());
        }
    }
}