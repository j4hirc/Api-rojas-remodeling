package com.rojas.remodeling.Api_rojas_remodeling.repository;

import com.rojas.remodeling.Api_rojas_remodeling.model.Jobs;
import jakarta.persistence.LockModeType;
import lombok.NonNull;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface JobsRepository extends JpaRepository<Jobs, Long> {
    @Override
    @NonNull
    @EntityGraph(attributePaths = {"employee", "manager"})
    @Query("SELECT j FROM Jobs j ORDER BY j.priority ASC")
    List<Jobs> findAll();

    @Override
    @NonNull
    @EntityGraph(attributePaths = {"employee", "manager"})
    Optional<Jobs> findById(@NonNull Long id);

    @EntityGraph(attributePaths = {"employee", "manager"})
    @Query("SELECT j FROM Jobs j WHERE j.employee.id = :employeeId ORDER BY j.priority ASC")
    List<Jobs> findByEmployeeId(@Param("employeeId") Long employeeId);

    @EntityGraph(attributePaths = {"employee", "manager"})
    @Query("SELECT j FROM Jobs j WHERE j.employee.firstName = :nameEmployee ORDER BY j.priority ASC")
    List<Jobs> findByEmployeeFirstName(@Param("nameEmployee")String nameEmployee);
    
    @Modifying
    @Query("DELETE FROM Jobs j WHERE j.id = :jobId")
    void deleteJobByIdCustom(@Param("jobId") Long jobId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT j FROM Jobs j WHERE j.id = :id")
    Optional<Jobs> findByIdForUpdate(@Param("id") Long id);
}
