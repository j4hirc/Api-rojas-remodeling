package com.rojas.remodeling.Api_rojas_remodeling.repository;


import com.rojas.remodeling.Api_rojas_remodeling.model.Users;
import lombok.NonNull;
import org.apache.catalina.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UsersRepository extends JpaRepository<Users, Long> {

    @EntityGraph(attributePaths = {"roles"})
    @Query("SELECT u FROM Users u  WHERE u.email = :email")
    Optional<Users> findByEmail(@Param("email") String email);


    @EntityGraph(attributePaths = {"roles"})
    @Query("SELECT u FROM Users u JOIN u.roles r WHERE r.name = :roleName")
    List<Users> findUsersByRoleName(@Param("roleName") String roleName);

    @EntityGraph(attributePaths = {"roles"})
    @Query("SELECT u FROM Users u  WHERE u.firstName = :name")
    List<Users> findByName(@Param("name") String name);

    @EntityGraph(attributePaths = {"roles"})
    @Query("SELECT u FROM Users u  WHERE u.dni = :dni")
    Optional<Users> findByDni(@Param("dni") String dni);


    @Override
    @NonNull
    @EntityGraph(attributePaths = {"roles"})
    List<Users> findAll();


    @Override
    @NonNull
    @EntityGraph(attributePaths = {"roles"})
    Optional<Users> findById(@NonNull Long id);


    Boolean existsByEmail(String email);
    Boolean existsByDni(String dni);
    Boolean existsByPhone(String phone);
}
