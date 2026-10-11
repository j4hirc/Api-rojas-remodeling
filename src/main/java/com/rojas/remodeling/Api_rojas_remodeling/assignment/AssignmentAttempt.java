package com.rojas.remodeling.Api_rojas_remodeling.assignment;

import com.rojas.remodeling.Api_rojas_remodeling.model.Jobs;
import com.rojas.remodeling.Api_rojas_remodeling.model.Users;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Entity
@Getter
@Setter
@Table(
        name = "job_assignment_attempts",
        indexes = {
                @Index(
                        name = "idx_assignment_deadline",
                        columnList = "status,expires_at"
                ),
                @Index(
                        name = "idx_assignment_job",
                        columnList = "job_id"
                )
        }
)
public class AssignmentAttempt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "job_id", nullable = false)
    @org.hibernate.annotations.OnDelete(
            action = org.hibernate.annotations.OnDeleteAction.CASCADE
    )
    private Jobs job;

    @ManyToOne(optional = false)
    @JoinColumn(name = "employee_id", nullable = false)
    private Users employee;

    @Column(nullable = false)
    private String status;

    @Column(name = "assigned_at", nullable = false)
    private Instant assignedAt;

    @Column(name = "expires_at")
    private Instant expiresAt;

    private Instant respondedAt;

    private String reason;

    @Column(unique = true, length = 64)
    private String tokenHash;
}