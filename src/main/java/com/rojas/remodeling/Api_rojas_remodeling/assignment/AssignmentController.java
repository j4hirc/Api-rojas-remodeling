package com.rojas.remodeling.Api_rojas_remodeling.assignment;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;

import static com.rojas.remodeling.Api_rojas_remodeling.assignment.AssignmentService.h;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/job-assignments")
public class AssignmentController {

    private final AssignmentService service;

    @GetMapping("/mine")
    @PreAuthorize("hasRole('EMPLOYEE')")
    public List<AssignmentView> mine(Principal principal) {
        return service.mine(principal.getName());
    }

    @GetMapping("/job/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','JEFE','BODEGUERO')")
    public List<AssignmentView> history(@PathVariable Long id) {
        return service.history(id);
    }

    public record Decision(String decision) {}

    @PostMapping("/{id}/decision")
    @PreAuthorize("hasRole('EMPLOYEE')")
    public Map<String, String> decide(
            @PathVariable Long id,
            @RequestBody Decision body,
            Principal principal
    ) {
        return Map.of(
                "message",
                service.respondUser(
                        id,
                        body.decision(),
                        principal.getName()
                )
        );
    }

    /*
     * Abrir el enlace no acepta ni rechaza.
     * La decisión se confirma mediante POST.
     */
    @GetMapping(
            value = "/respond",
            produces = MediaType.TEXT_HTML_VALUE
    )
    public ResponseEntity<String> preview(
            @RequestParam String token
    ) {
        AssignmentView a = service.preview(token);

        String body = "<h1>Responder asignación</h1>"
                + "<p>Trabajo #" + a.jobId()
                + " — " + h(a.clientName()) + "</p>"
                + "<p>" + h(a.address()) + "</p>"
                + "<p>Asignado a: " + h(a.employeeName()) + "</p>";

        if ("PENDING".equals(a.status())) {
            body += "<p>Vence: "
                    + h(
                    a.expiresAt()
                            .atZone(ZoneId.of("America/Chicago"))
                            .toString()
            )
                    + " (Chicago)</p>"
                    + "<p>Selecciona una opción para confirmar "
                    + "tu decisión.</p>"
                    + "<form method=\"post\" "
                    + "action=\"/api/v1/job-assignments/respond\">"
                    + "<input type=\"hidden\" name=\"token\" value=\""
                    + h(token) + "\">"
                    + "<button name=\"decision\" value=\"ACCEPT\">"
                    + "Aceptar trabajo</button> "
                    + "<button name=\"decision\" value=\"REJECT\">"
                    + "Rechazar trabajo</button>"
                    + "</form>";
        } else {
            body += "<p>Esta asignación ya no está pendiente. "
                    + "Estado: " + h(a.status()) + "</p>";
        }

        return page(body);
    }

    @PostMapping(
            value = "/respond",
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
            produces = MediaType.TEXT_HTML_VALUE
    )
    public ResponseEntity<String> respond(
            @RequestParam String token,
            @RequestParam String decision
    ) {
        return page(
                "<h1>Respuesta de asignación</h1><p>"
                        + h(service.respondToken(token, decision))
                        + "</p>"
        );
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<?> error(
            ResponseStatusException ex,
            HttpServletRequest request
    ) {
        if (request.getRequestURI().endsWith("/respond")) {
            ResponseEntity<String> response = page(
                    "<h1>No se pudo registrar la respuesta</h1><p>"
                            + h(ex.getReason())
                            + "</p>"
            );

            return ResponseEntity.status(ex.getStatusCode())
                    .headers(response.getHeaders())
                    .body(response.getBody());
        }

        return ResponseEntity.status(ex.getStatusCode()).body(
                Map.of(
                        "message",
                        ex.getReason() == null
                                ? "Solicitud inválida"
                                : ex.getReason()
                )
        );
    }

    private ResponseEntity<String> page(String body) {
        return ResponseEntity.ok()
                .cacheControl(CacheControl.noStore())
                .header("Referrer-Policy", "no-referrer")
                .header(
                        "Content-Security-Policy",
                        "default-src 'none'; "
                                + "style-src 'unsafe-inline'; "
                                + "form-action 'self'; "
                                + "frame-ancestors 'none'; "
                                + "base-uri 'none'"
                )
                .body(
                        "<!doctype html><html lang=\"es\">"
                                + "<meta charset=\"utf-8\">"
                                + "<meta name=\"viewport\" "
                                + "content=\"width=device-width,initial-scale=1\">"
                                + "<title>RemoMN · Asignación</title>"
                                + "<style>"
                                + "body{font:18px system-ui;max-width:640px;"
                                + "margin:40px auto;padding:20px;line-height:1.6}"
                                + "button{padding:16px;margin:8px 0;"
                                + "font:inherit;cursor:pointer}"
                                + "h1{font-size:26px}"
                                + "</style><main>"
                                + body
                                + "</main></html>"
                );
    }
}