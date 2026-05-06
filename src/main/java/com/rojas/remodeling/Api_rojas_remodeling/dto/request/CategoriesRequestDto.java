package com.rojas.remodeling.Api_rojas_remodeling.dto.request;


import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CategoriesRequestDto {

    @NotBlank(message = "El nombre no puede estar vacio")
    private String name;
}
