package com.rojas.remodeling.Api_rojas_remodeling.repository;

import com.rojas.remodeling.Api_rojas_remodeling.model.Materials;
import lombok.NonNull;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MaterialsRepository extends JpaRepository<Materials, Long> {

    @EntityGraph(attributePaths = {"category"})
    @Query("SELECT m FROM Materials m WHERE m.name LIKE %:name%")
    List<Materials> findByNameContainingIgnoreCase(@Param("name") String name);

    @EntityGraph(attributePaths = {"category"})
    @Query("SELECT m FROM Materials m WHERE m.category.id = :categoryId")
    List<Materials> findByCategoryId(@Param("categoryId") Long categoryId);

    @Override
    @NonNull
    @EntityGraph(attributePaths = {"category"})
    List<Materials> findAll();

    @Override
    @NonNull
    @EntityGraph(attributePaths = {"category"})
    Optional<Materials> findById(@NonNull Long id);

    @Query("SELECT CASE WHEN COUNT(m) > 0 THEN true ELSE false END FROM Materials m WHERE m.name = :name")
    Boolean existsByName(@Param("name") String name);

}
