package com.rojas.remodeling.Api_rojas_remodeling.dto.request;

import lombok.Data;

import java.time.LocalDate;

@Data
public class EditProfileDto {
    private String dni;

    private String firstName;

    private String middleName;

    private String lastName;

    private String secondSurname;

    private String email;

    private String password;

    private String phone;

    private LocalDate dateOfBirth;

    private String title;
}
