package com.rojas.remodeling.Api_rojas_remodeling.repository;

import com.rojas.remodeling.Api_rojas_remodeling.model.Jobs;
import lombok.NonNull;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface JobsRepository extends JpaRepository<Jobs, Long> {
    @Override
    @NonNull
    @EntityGraph(attributePaths = {"employee", "manager"})
    List<Jobs> findAll();

    @Override
    @NonNull
    @EntityGraph(attributePaths = {"employee", "manager"})
    Optional<Jobs> findById(@NonNull Long id);

    @EntityGraph(attributePaths = {"employee", "manager"})
    List<Jobs> findByEmployeeId(@Param("employeeId") Long employeeId);

    @EntityGraph(attributePaths = {"employee", "manager"})
    List<Jobs> findByEmployeeFirstName(@Param("nameEmployee")String name);

}
