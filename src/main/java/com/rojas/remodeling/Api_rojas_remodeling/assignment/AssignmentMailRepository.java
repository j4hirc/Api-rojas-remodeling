package com.rojas.remodeling.Api_rojas_remodeling.assignment;

import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface AssignmentMailRepository
        extends JpaRepository<AssignmentMail, Long> {

    @Query("""
        select m.id
        from AssignmentMail m
        where m.sentAt is null
          and m.nextAttemptAt <= :now
        order by m.id
        """)
    List<Long> due(Instant now, Pageable page);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select m from AssignmentMail m where m.id = :id")
    Optional<AssignmentMail> lockById(Long id);
}