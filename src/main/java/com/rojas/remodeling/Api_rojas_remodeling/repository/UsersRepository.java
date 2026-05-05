package com.rojas.remodeling.Api_rojas_remodeling.repository;


import com.rojas.remodeling.Api_rojas_remodeling.model.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsersRepository extends JpaRepository<Users, Long> {
    Optional<Users> findByEmail(String email);
    Boolean existsByEmail(String email);
    Boolean existsByDni(String dni);
    Boolean existsByPhone(String phone);
}
