package com.rojas.remodeling.Api_rojas_remodeling.assignment;

import com.rojas.remodeling.Api_rojas_remodeling.model.*;
import com.rojas.remodeling.Api_rojas_remodeling.repository.*;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.HtmlUtils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.*;
import java.util.*;

@Service
@RequiredArgsConstructor
public class AssignmentService {

    private final AssignmentRepository attempts;
    private final AssignmentMailRepository mails;
    private final JobsRepository jobs;
    private final UsersRepository users;
    private final AssignmentSettings settings;
    private final Clock clock;
    private final EntityManager entityManager;

    private static final SecureRandom RANDOM = new SecureRandom();

    /*
     * Se llama dentro de la transacción de crear/actualizar trabajo.
     * En actualización, el trabajo debe estar bloqueado con
     * findByIdForUpdate.
     */
    public boolean onSaved(Jobs job, Long previousEmployeeId) {
        if (!settings.enabled) {
            return false;
        }

        boolean changed = previousEmployeeId == null
                || !previousEmployeeId.equals(job.getEmployee().getId());

        if (!"PENDING".equalsIgnoreCase(job.getStatus())) {
            cancelPending(job, "El trabajo dejó de estar pendiente");
            return false;
        }

        // Editar dirección, materiales, etc. no reinicia las 24 horas.
        if (!changed) {
            return false;
        }

        // Antes de iniciar, comprobar que existe un admin de respaldo.
        fallbackAdmin();

        cancelPending(job, "Reasignación manual por administración");

        if (hasRole(job.getEmployee(), "ROLE_ADMIN")) {
            createAttempt(
                    job,
                    job.getEmployee(),
                    "ADMIN",
                    "Asignación manual a administración"
            );
        } else {
            if (!eligible(job.getEmployee())) {
                throw conflict(
                        "Selecciona un subcontratista activo con jerarquía y correo válido"
                );
            }

            invite(job, job.getEmployee(), "Asignación manual");
        }

        return true;
    }

    private void cancelPending(Jobs job, String reason) {
        if (job.getAssignmentId() == null) {
            return;
        }

        attempts.findById(job.getAssignmentId())
                .filter(a -> "PENDING".equals(a.getStatus()))
                .ifPresent(a -> {
                    a.setStatus("SUPERSEDED");
                    a.setRespondedAt(clock.instant());
                    a.setReason(reason);

                    job.setAssignmentStatus("CANCELLED");
                    job.setAssignmentExpiresAt(null);
                });
    }

    private AssignmentAttempt createAttempt(
            Jobs job,
            Users employee,
            String status,
            String reason
    ) {
        AssignmentAttempt a = new AssignmentAttempt();

        a.setJob(job);
        a.setEmployee(employee);
        a.setAssignedAt(clock.instant());
        a.setStatus(status);
        a.setReason(reason);

        if ("PENDING".equals(status)) {
            a.setExpiresAt(
                    clock.instant().plus(Duration.ofHours(24))
            );
        }

        a = attempts.saveAndFlush(a);

        job.setEmployee(employee);
        job.setAssignmentId(a.getId());
        job.setAssignmentStatus(status);
        job.setAssignmentExpiresAt(a.getExpiresAt());

        if ("ADMIN".equals(status)) {
            enqueue(
                    a,
                    employee.getEmail(),
                    "Trabajo requiere atención: " + job.getClientName(),
                    details(a)
                            + "<p>El trabajo queda asignado directamente "
                            + "a administración. No tiene plazo de aceptación.</p>",
                    false
            );
        }

        return a;
    }

    private void invite(Jobs job, Users employee, String reason) {
        AssignmentAttempt a = createAttempt(
                job,
                employee,
                "PENDING",
                reason
        );

        byte[] bytes = new byte[32];
        RANDOM.nextBytes(bytes);

        String token = Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(bytes);

        a.setTokenHash(hash(token));

        String link = settings.publicApiUrl.replaceAll("/$", "")
                + "/api/v1/job-assignments/respond?token="
                + token;

        String body = details(a)
                + "<p>Tienes 24 horas corridas para responder. "
                + "Si rechazas o no respondes, el trabajo pasará "
                + "al siguiente subcontratista.</p>"
                + "<p>Plazo: "
                + h(
                a.getExpiresAt()
                        .atZone(ZoneId.of("America/Chicago"))
                        .toString()
        )
                + " (hora de Chicago).</p>"
                + "<p><a href=\"" + h(link) + "\">"
                + "Aceptar o rechazar el trabajo</a></p>"
                + "<p>Abre el enlace y confirma tu decisión. "
                + "Responder a este correo no registra la aceptación.</p>";

        enqueue(
                a,
                employee.getEmail(),
                "Responde a tu asignación: " + job.getClientName(),
                body,
                true
        );
    }

    private void enqueue(
            AssignmentAttempt a,
            String recipient,
            String subject,
            String body,
            boolean invitation
    ) {
        if (recipient == null || recipient.isBlank()) {
            return;
        }

        AssignmentMail mail = new AssignmentMail();

        mail.setAttempt(a);
        mail.setRecipient(recipient);
        mail.setSubject(subject);
        mail.setBody(body);
        mail.setInvitation(invitation);
        mail.setNextAttemptAt(clock.instant());

        mails.save(mail);
    }

    private void notifyManager(
            AssignmentAttempt a,
            String decision
    ) {
        enqueue(
                a,
                a.getJob().getManager().getEmail(),
                "Asignación: " + decision
                        + " — " + a.getJob().getClientName(),
                details(a)
                        + "<p>Resultado: " + h(decision)
                        + ". Revisa el historial del trabajo "
                        + "en la plataforma.</p>",
                false
        );
    }

    private void next(Jobs job) {
        Set<Long> tried = new HashSet<>();

        attempts.findByJobIdOrderByIdAsc(job.getId())
                .forEach(a -> tried.add(a.getEmployee().getId()));

        Optional<Users> candidate =
                users.findUsersByRoleName("ROLE_EMPLOYEE")
                        .stream()
                        .filter(this::eligible)
                        .filter(u -> !tried.contains(u.getId()))
                        .min(
                                Comparator.comparing(Users::getHierarchyLevel)
                                        .thenComparing(Users::getId)
                        );

        if (candidate.isPresent()) {
            invite(
                    job,
                    candidate.get(),
                    "Reasignación automática por jerarquía"
            );
        } else {
            createAttempt(
                    job,
                    fallbackAdmin(),
                    "ADMIN",
                    "No quedan subcontratistas disponibles sin intentar"
            );
        }
    }

    @Transactional
    public void expire(Long jobId) {
        Jobs job = jobs.findByIdForUpdate(jobId).orElse(null);

        if (job == null || job.getAssignmentId() == null) {
            return;
        }

        AssignmentAttempt a = attempts
                .findById(job.getAssignmentId())
                .orElse(null);

        if (a == null || !"PENDING".equals(a.getStatus())) {
            return;
        }

        if (!"PENDING".equalsIgnoreCase(job.getStatus())) {
            cancelPending(job, "El trabajo ya no está pendiente");
            return;
        }

        if (!clock.instant().isBefore(a.getExpiresAt())) {
            expireLocked(a);
        }
    }

    private void expireLocked(AssignmentAttempt a) {
        a.setStatus("EXPIRED");
        a.setRespondedAt(clock.instant());
        a.setReason("Sin respuesta durante 24 horas");

        notifyManager(a, "Plazo vencido");

        enqueue(
                a,
                a.getEmployee().getEmail(),
                "Asignación vencida: " + a.getJob().getClientName(),
                details(a)
                        + "<p>Tu plazo venció. "
                        + "Esta asignación ya no te corresponde.</p>",
                false
        );

        next(a.getJob());
    }

    @Transactional(readOnly = true)
    public AssignmentView preview(String token) {
        return AssignmentView.of(byToken(token));
    }

    @Transactional
    public String respondToken(
            String token,
            String decision
    ) {
        AssignmentAttempt found = byToken(token);

        lockAndRefresh(found);

        return respondLocked(found, decision);
    }

    @Transactional
    public String respondUser(
            Long id,
            String decision,
            String email
    ) {
        AssignmentAttempt found = attempts.findById(id)
                .orElseThrow(() -> conflict("Asignación inexistente"));

        if (!found.getEmployee().getEmail().equalsIgnoreCase(email)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Esta asignación no te corresponde"
            );
        }

        lockAndRefresh(found);

        return respondLocked(found, decision);
    }

    private void lockAndRefresh(AssignmentAttempt attempt) {
        Jobs job = jobs.findByIdForUpdate(
                attempt.getJob().getId()
        ).orElseThrow(() -> conflict("Trabajo eliminado"));

        // Leer nuevamente después de obtener el bloqueo.
        entityManager.refresh(job);
        entityManager.refresh(attempt);
    }

    private String respondLocked(
            AssignmentAttempt a,
            String decision
    ) {
        if (
                !"ACCEPT".equals(decision)
                        && !"REJECT".equals(decision)
        ) {
            throw conflict("Respuesta no válida");
        }

        Jobs job = a.getJob();

        if (
                !"PENDING".equals(a.getStatus())
                        || !Objects.equals(job.getAssignmentId(), a.getId())
        ) {
            return "Esta asignación ya fue respondida, venció "
                    + "o fue reemplazada. No se realizaron cambios.";
        }

        if (!"PENDING".equalsIgnoreCase(job.getStatus())) {
            cancelPending(job, "El trabajo ya no está pendiente");

            return "El trabajo ya no admite esta respuesta.";
        }

        if (!eligible(a.getEmployee())) {
            a.setStatus("SUPERSEDED");
            a.setRespondedAt(clock.instant());
            a.setReason("El subcontratista dejó de estar disponible");

            next(job);

            return "El usuario ya no está disponible para esta "
                    + "asignación. El trabajo fue reasignado.";
        }

        if (!clock.instant().isBefore(a.getExpiresAt())) {
            expireLocked(a);

            return "El plazo de 24 horas venció. "
                    + "El trabajo fue reasignado.";
        }

        a.setRespondedAt(clock.instant());

        if ("ACCEPT".equals(decision)) {
            a.setStatus("ACCEPTED");
            a.setReason("Aceptado por el subcontratista");

            job.setAssignmentStatus("ACCEPTED");
            job.setAssignmentExpiresAt(null);

            notifyManager(a, "Aceptado");

            return "Trabajo aceptado. Queda asignado a ti.";
        }

        a.setStatus("REJECTED");
        a.setReason("Rechazado por el subcontratista");

        notifyManager(a, "Rechazado");
        next(job);

        return "Rechazo registrado. El trabajo fue reasignado.";
    }

    @Transactional(readOnly = true)
    public List<AssignmentView> history(Long jobId) {
        return attempts.findByJobIdOrderByIdAsc(jobId)
                .stream()
                .map(AssignmentView::of)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<AssignmentView> mine(String email) {
        Users user = users.findByEmail(email)
                .orElseThrow(() -> conflict("Usuario inexistente"));

        return attempts
                .findByEmployeeIdAndStatusOrderByExpiresAtAsc(
                        user.getId(),
                        "PENDING"
                )
                .stream()
                .map(AssignmentView::of)
                .toList();
    }

    private AssignmentAttempt byToken(String token) {
        if (
                token == null
                        || !token.matches("[A-Za-z0-9_-]{43}")
        ) {
            throw conflict("Enlace inválido");
        }

        return attempts.findByTokenHash(hash(token))
                .orElseThrow(() -> conflict("Enlace inválido"));
    }

    private Users fallbackAdmin() {
        Users admin = users.findById(settings.adminId)
                .orElseThrow(
                        () -> conflict(
                                "Administrador de respaldo no configurado"
                        )
                );

        if (
                !hasRole(admin, "ROLE_ADMIN")
                        || !"Active".equalsIgnoreCase(admin.getStatus())
                        || admin.getEmail() == null
                        || admin.getEmail().isBlank()
        ) {
            throw conflict(
                    "El administrador de respaldo debe estar activo y tener correo"
            );
        }

        return admin;
    }

    private boolean eligible(Users user) {
        return hasRole(user, "ROLE_EMPLOYEE")
                && "Active".equalsIgnoreCase(user.getStatus())
                && user.getHierarchyLevel() != null
                && user.getHierarchyLevel() > 0
                && user.getEmail() != null
                && !user.getEmail().isBlank();
    }

    private boolean hasRole(Users user, String role) {
        return user.getRoles()
                .stream()
                .anyMatch(r -> role.equals(r.getName()));
    }

    public static String h(String value) {
        return HtmlUtils.htmlEscape(value == null ? "" : value);
    }

    private String details(AssignmentAttempt a) {
        Jobs job = a.getJob();

        return "<h2>Trabajo #" + job.getId()
                + " — " + h(job.getClientName()) + "</h2>"
                + "<p>Subcontratista: "
                + h(a.getEmployee().getFirstName()) + " "
                + h(a.getEmployee().getLastName()) + "</p>"
                + "<p>Dirección: " + h(job.getAddress())
                + "<br>Edificio: " + h(job.getBuildingNumber())
                + "<br>Departamento: " + h(job.getApartment())
                + "<br>Fecha: " + job.getJobDate()
                + "<br>Descripción: " + h(job.getDescription())
                + "<br>Pago: $" + job.getPay() + "</p>";
    }

    static String hash(String token) {
        try {
            return HexFormat.of().formatHex(
                    MessageDigest.getInstance("SHA-256")
                            .digest(token.getBytes(StandardCharsets.UTF_8))
            );
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }

    private ResponseStatusException conflict(String message) {
        return new ResponseStatusException(
                HttpStatus.CONFLICT,
                message
        );
    }
}