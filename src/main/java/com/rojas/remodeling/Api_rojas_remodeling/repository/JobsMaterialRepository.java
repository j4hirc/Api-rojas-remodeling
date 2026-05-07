package com.rojas.remodeling.Api_rojas_remodeling.repository;

import com.rojas.remodeling.Api_rojas_remodeling.model.JobMaterial;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobsMaterialRepository extends JpaRepository<JobMaterial, Long> {

    @Modifying
    @Query("DELETE FROM JobMaterial jm WHERE jm.job.id = :jobId")
    void deleteByJobId(@Param("jobId") Long jobId);

    @EntityGraph(attributePaths = {"job", "material"})
    @Query("SELECT j FROM JobMaterial j WHERE j.job.id = :jobId")
    List<JobMaterial> findByJobId(Long jobId);
}
