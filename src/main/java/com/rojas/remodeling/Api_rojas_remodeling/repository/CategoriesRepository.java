package com.rojas.remodeling.Api_rojas_remodeling.repository;

import com.rojas.remodeling.Api_rojas_remodeling.model.Categories;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface CategoriesRepository extends JpaRepository<Categories, Long> {

    boolean existsByName(String name);
}
