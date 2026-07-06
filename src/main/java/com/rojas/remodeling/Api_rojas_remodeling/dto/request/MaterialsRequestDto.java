package com.rojas.remodeling.Api_rojas_remodeling.dto.request;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class MaterialsRequestDto {

    @NotBlank(message = "El nombre del material no puede estar vacío")
    private String name;

    @NotNull(message = "La cantidad no puede ser nula")
    private Integer count;

    @NotNull(message = "El precio no puede ser nulo")
    private Double price;


    @NotNull(message = "El ID de la categoría es obligatorio")
    private Long categoryId;
}
