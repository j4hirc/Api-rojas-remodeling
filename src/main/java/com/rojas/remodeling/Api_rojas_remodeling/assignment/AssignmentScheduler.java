package com.rojas.remodeling.Api_rojas_remodeling.assignment;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Clock;

@Component
@RequiredArgsConstructor
@Slf4j
public class AssignmentScheduler {

    private final AssignmentRepository attempts;
    private final AssignmentMailRepository mails;
    private final AssignmentService service;
    private final AssignmentMailWorker mailWorker;
    private final Clock clock;
    private final AssignmentSettings settings;

    @Scheduled(fixedDelayString = "${assignments.scan-ms:15000}")
    public void expire() {
        if (!settings.enabled) {
            return;
        }

        for (
                Long jobId : attempts.expiredJobs(
                clock.instant(),
                PageRequest.of(0, 100)
        )
        ) {
            try {
                service.expire(jobId);
            } catch (Exception e) {
                log.error(
                        "No se pudo reasignar trabajo {}; se reintentará",
                        jobId,
                        e
                );
            }
        }
    }

    @Scheduled(fixedDelayString = "${assignments.mail-scan-ms:10000}")
    public void mail() {
        if (!settings.enabled) {
            return;
        }

        for (
                Long mailId : mails.due(
                clock.instant(),
                PageRequest.of(0, 50)
        )
        ) {
            try {
                mailWorker.send(mailId);
            } catch (Exception e) {
                log.error(
                        "No se pudo procesar correo {}",
                        mailId,
                        e
                );
            }
        }
    }
}