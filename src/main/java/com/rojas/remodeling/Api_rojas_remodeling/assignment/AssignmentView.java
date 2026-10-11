package com.rojas.remodeling.Api_rojas_remodeling.assignment;

import java.time.Instant;

public record AssignmentView(
        Long id,
        Long jobId,
        Long employeeId,
        String employeeName,
        String clientName,
        String address,
        String status,
        Instant assignedAt,
        Instant expiresAt,
        Instant respondedAt,
        String reason
) {

    public static AssignmentView of(AssignmentAttempt a) {
        return new AssignmentView(
                a.getId(),
                a.getJob().getId(),
                a.getEmployee().getId(),
                a.getEmployee().getFirstName()
                        + " "
                        + a.getEmployee().getLastName(),
                a.getJob().getClientName(),
                a.getJob().getAddress(),
                a.getStatus(),
                a.getAssignedAt(),
                a.getExpiresAt(),
                a.getRespondedAt(),
                a.getReason()
        );
    }
}