package com.rojas.remodeling.Api_rojas_remodeling.dto.request;

import jakarta.persistence.Column;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.util.Set;

@Data
public class UserRequestDto {
    @NotBlank(message = "El dni no puede estar vacío")
    private String dni;

    @NotBlank(message = "El primer nombre no puede estar vacío")
    private String firstName;

    private String middleName;

    @NotBlank(message = "El apellido no puede estar vacío")
    private String lastName;

    private String secondSurname;

    @NotBlank(message = "El Email  no puede estar vacío")
    @Email(message = "Email no es valido")
    private String email;

    private String password;

    @NotBlank(message = "El numero de celular no puede estar vacío")
    private String phone;

    @NotNull(message = "La fecha de nacimiento no puede estar vacía")
    private LocalDate dateOfBirth;

    @NotNull(message = "La fecha de entrada del empleado/jefe no puede estar vacía")
    private LocalDate dateOfEntry;

    @NotBlank(message = "El estado no puede estar vacío")
    private String status;

    @NotBlank(message = "El titulo no puede estar vacío")
    private String title;

    @NotNull(message = "Debe tener al menos un rol")
    private Set<String> roles;

}
