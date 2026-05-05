package com.rojas.remodeling.Api_rojas_remodeling.repository;

import com.rojas.remodeling.Api_rojas_remodeling.model.Roles;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import javax.management.relation.Role;
import java.util.Optional;

public interface RolesRepository  extends JpaRepository<Roles, Long> {
    Optional<Roles> findByName(String name);


}
