package com.rojas.remodeling.Api_rojas_remodeling.dto.response;

import com.rojas.remodeling.Api_rojas_remodeling.model.Roles;
import lombok.Data;
import java.time.LocalDate;
import java.util.Set;

@Data
public class UserResponseDto {
    private Long userId;
    private String dni;
    private String name; // Mantenemos este por si acaso

    private String firstName;
    private String middleName;
    private String lastName;
    private String secondSurname;
    private String title;
    private String email;
    private String phone;
    private LocalDate dateOfBirth;
    private LocalDate dateOfEntry;
    private String status;
    // --------------------------------------------------------------

    private Set<Roles> roles;
}