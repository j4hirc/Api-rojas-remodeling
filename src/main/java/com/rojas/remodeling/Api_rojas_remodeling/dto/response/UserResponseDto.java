package com.rojas.remodeling.Api_rojas_remodeling.dto.response;

import com.rojas.remodeling.Api_rojas_remodeling.model.Roles;
import lombok.Data;

import java.util.Set;

@Data
public class UserResponseDto {
    private Long id;
    private String dni;
    private String name;
    private String email;
    private String phone;
    private Set<Roles> roles;
}
