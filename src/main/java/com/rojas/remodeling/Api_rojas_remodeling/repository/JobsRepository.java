package com.rojas.remodeling.Api_rojas_remodeling.repository;

import com.rojas.remodeling.Api_rojas_remodeling.model.Jobs;
import com.rojas.remodeling.Api_rojas_remodeling.model.Users;
import lombok.NonNull;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobsRepository extends JpaRepository<Jobs, Long> {
    @Override
    @NonNull
    @EntityGraph(attributePaths = {"employee", "manager"})
    List<Jobs> findAll();
}
