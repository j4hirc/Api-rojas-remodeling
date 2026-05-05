package com.rojas.remodeling.Api_rojas_remodeling.dto.request;

import jakarta.persistence.Column;
import lombok.Data;

import java.time.LocalDate;
import java.util.Set;

@Data
public class UserRequestDto {
    private String dni;

    private String firstName;

    private String middleName;

    private String lastName;

    private String secondSurname;

    private String email;

    private String password;

    private String phone;

    private LocalDate dateOfBirth;

    private LocalDate dateOfEntry;

    private String status;

    private String title;

    private Set<String> roles;

}
