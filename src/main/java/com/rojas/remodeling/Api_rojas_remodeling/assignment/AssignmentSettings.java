package com.rojas.remodeling.Api_rojas_remodeling.assignment;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.time.Clock;

@Component
public class AssignmentSettings {

    @Value("${assignments.enabled:false}")
    public boolean enabled;

    @Value("${assignments.admin-id:0}")
    public long adminId;

    @Value("${assignments.public-api-url:}")
    public String publicApiUrl;

    @PostConstruct
    void validate() {
        if (!enabled) {
            return;
        }

        if (adminId <= 0) {
            throw new IllegalStateException(
                    "Configura ASSIGNMENTS_ADMIN_ID con el usuario administrador responsable"
            );
        }

        URI url = URI.create(publicApiUrl);

        if (
                !"https".equals(url.getScheme())
                        || url.getHost() == null
                        || url.getQuery() != null
                        || url.getFragment() != null
        ) {
            throw new IllegalStateException(
                    "ASSIGNMENTS_PUBLIC_API_URL debe ser la URL HTTPS pública de la API, sin /api/v1"
            );
        }
    }

    @Bean
    public Clock assignmentClock() {
        return Clock.systemUTC();
    }
}