package com.rojas.remodeling.Api_rojas_remodeling.assignment;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface AssignmentRepository
        extends JpaRepository<AssignmentAttempt, Long> {

    List<AssignmentAttempt> findByJobIdOrderByIdAsc(Long jobId);

    Optional<AssignmentAttempt> findByTokenHash(String tokenHash);

    @Query("""
        select a.job.id
        from AssignmentAttempt a
        where a.status = 'PENDING'
          and a.expiresAt <= :now
        order by a.expiresAt
        """)
    List<Long> expiredJobs(Instant now, Pageable page);

    List<AssignmentAttempt> findByEmployeeIdAndStatusOrderByExpiresAtAsc(
            Long employeeId,
            String status
    );
}