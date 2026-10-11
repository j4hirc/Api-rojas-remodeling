package com.rojas.remodeling.Api_rojas_remodeling.assignment;

import com.rojas.remodeling.Api_rojas_remodeling.service.implementation.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;

@Service
@RequiredArgsConstructor
@Slf4j
public class AssignmentMailWorker {

    private final AssignmentMailRepository mails;
    private final EmailService email;
    private final Clock clock;

    @Transactional
    public void send(Long id) {
        AssignmentMail mail = mails.lockById(id).orElse(null);

        if (
                mail == null
                        || mail.getSentAt() != null
                        || mail.getNextAttemptAt().isAfter(clock.instant())
        ) {
            return;
        }

        AssignmentAttempt attempt = mail.getAttempt();

        if (
                mail.isInvitation()
                        && (
                        !"PENDING".equals(attempt.getStatus())
                                || !clock.instant().isBefore(attempt.getExpiresAt())
                )
        ) {
            mail.setSentAt(clock.instant());
            mail.setBody(
                    "Invitación vencida o reemplazada; no enviada."
            );
            return;
        }

        try {
            email.sendEmailOrThrow(
                    mail.getRecipient(),
                    mail.getSubject(),
                    mail.getBody()
            );

            mail.setSentAt(clock.instant());
            mail.setBody("Correo enviado.");
        } catch (Exception e) {
            mail.setFailures(mail.getFailures() + 1);

            mail.setNextAttemptAt(
                    clock.instant().plusSeconds(
                            Math.min(900, 30L * mail.getFailures())
                    )
            );

            log.warn(
                    "Falló correo de asignación id={}, intento={}; se reintentará",
                    id,
                    mail.getFailures()
            );
        }
    }
}