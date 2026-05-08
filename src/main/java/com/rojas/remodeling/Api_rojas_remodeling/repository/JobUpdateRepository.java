package com.rojas.remodeling.Api_rojas_remodeling.repository;

import com.rojas.remodeling.Api_rojas_remodeling.model.JobUpdates;
import lombok.NonNull;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface JobUpdateRepository extends JpaRepository<JobUpdates, Long> {

    @Override
    @NonNull
    @EntityGraph(attributePaths = {"job", "employee"})
    Optional<JobUpdates> findById(@NonNull Long id);

    @EntityGraph(attributePaths = {"job", "employee"})
    @Query("SELECT j FROM JobUpdates j WHERE j.job.id = :jobId")
    List<JobUpdates> findByJobId(Long jobId);

    @Query("SELECT ju FROM JobUpdates ju WHERE ju.job.id IN :jobIds")
    @EntityGraph(attributePaths = {"job", "employee"})
    List<JobUpdates> findAllByJobIds(@Param("jobIds") List<Long> jobIds);

}
