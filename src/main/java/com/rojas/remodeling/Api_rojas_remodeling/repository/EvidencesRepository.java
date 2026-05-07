package com.rojas.remodeling.Api_rojas_remodeling.repository;

import com.rojas.remodeling.Api_rojas_remodeling.model.Evidences;
import lombok.NonNull;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EvidencesRepository extends JpaRepository<Evidences, Long> {


    @Query("SELECT e FROM Evidences e Where e.jobUpdate.id = :jobUpdateId")
    List<Evidences> finAllJobUpdateId(Long jobUpdateId);
}
