package com.rojas.remodeling.Api_rojas_remodeling.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequestDto {

    @NotBlank(message = "El Email no puede estar vacío")
    @Email(message = "Email no es valido")
    private String email;

    @NotBlank(message = "La contraseña no puede estar vacía")
    private String password;
}
