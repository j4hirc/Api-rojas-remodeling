package com.rojas.remodeling.Api_rojas_remodeling.service.implementation;

import com.rojas.remodeling.Api_rojas_remodeling.model.Jobs;
import org.springframework.stereotype.Service;
import java.time.Clock;
import java.time.LocalDate;
import java.time.ZoneId;

/** Una única regla de cierre para la oficina y los reportes del empleado. */
@Service
public class JobCompletionService {
    private static final ZoneId BUSINESS_ZONE = ZoneId.of("America/Chicago");
    private final Clock clock;

    public JobCompletionService() {
        this(Clock.system(BUSINESS_ZONE));
    }

    JobCompletionService(Clock clock) {
        this.clock = clock.withZone(BUSINESS_ZONE);
    }

    public void apply(Jobs job, String previousStatus, LocalDate requestedDate) {
        if (!completed(job.getStatus())) {
            // Si se reabre, el próximo cierre debe registrar una nueva fecha.
            job.setCompletedDate(null);
            return;
        }

        LocalDate today = LocalDate.now(clock);
        if (requestedDate != null) {
            if (requestedDate.isAfter(today)) {
                throw new IllegalArgumentException("La fecha de finalización no puede estar en el futuro.");
            }
            job.setCompletedDate(requestedDate);
        } else if (!completed(previousStatus)) {
            job.setCompletedDate(today);
        }
        // COMPLETED -> COMPLETED: conservar la fecha existente, incluso NULL.
        // Una edición de precio no debe fechar hoy un cierre histórico desconocido.
    }

    private boolean completed(String status) {
        return status != null && "COMPLETED".equalsIgnoreCase(status.trim());
    }
}
