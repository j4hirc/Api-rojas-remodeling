package com.rojas.remodeling.Api_rojas_remodeling.repository;

import com.rojas.remodeling.Api_rojas_remodeling.model.JobBlueprint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobBlueprintRepository extends JpaRepository<JobBlueprint, Long> {
    List<JobBlueprint> findByJobId(Long jobId);

    @Modifying
    @Query("DELETE FROM JobBlueprint jb WHERE jb.job.id = :jobId")
    void deleteByJobId(Long jobId);
}