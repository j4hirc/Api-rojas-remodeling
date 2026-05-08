package com.rojas.remodeling.Api_rojas_remodeling.repository;

import com.rojas.remodeling.Api_rojas_remodeling.model.Evidences;
import lombok.NonNull;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EvidencesRepository extends JpaRepository<Evidences, Long> {


    @EntityGraph(attributePaths = {"jobUpdate"})
    @Query("SELECT e FROM Evidences e Where e.jobUpdate.id = :jobUpdateId")
    List<Evidences> finAllJobUpdateId(Long jobUpdateId);

    @Query("SELECT e FROM Evidences e WHERE e.jobUpdate.id IN :updateIds")
    @EntityGraph(attributePaths = {"jobUpdate"})
    List<Evidences> findAllByJobUpdateIds(@Param("updateIds") List<Long> updateIds);

}
