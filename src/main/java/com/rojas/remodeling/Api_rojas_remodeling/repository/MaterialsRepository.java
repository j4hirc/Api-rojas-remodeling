package com.rojas.remodeling.Api_rojas_remodeling.repository;

import com.rojas.remodeling.Api_rojas_remodeling.model.Materials;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MaterialsRepository extends JpaRepository<Materials, Long> {

    boolean existsByName(String name);

}
