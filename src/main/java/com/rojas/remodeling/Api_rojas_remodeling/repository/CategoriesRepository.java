package com.rojas.remodeling.Api_rojas_remodeling.repository;

import com.rojas.remodeling.Api_rojas_remodeling.model.Categories;
import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface CategoriesRepository extends JpaRepository<Categories, Long> {

    boolean existsByName(String name);

    @Query("SELECT c FROM Categories c WHERE c.name = :name")
    Optional<Categories> findByName(String name);

    @Override
    @NonNull
    List<Categories> findAll();

    @Override
    @NonNull
    Optional<Categories> findById(@NonNull Long id);



}
