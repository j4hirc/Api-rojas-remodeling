package com.rojas.remodeling.Api_rojas_remodeling.dto.request;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ClienteRequestDto {

    @NotBlank(message = "El nombre del cliente no puede estar vacío")
    private String clientName;

    private String clientPhone;

    @NotBlank(message = "La direccion del cliente no puede estar vacío")
    private String address;

    @NotNull(message = "La latitud es obligatoria para la ubicación")
    private Double latitude;

    @NotNull(message = "La longitud es obligatoria para la ubicación")
    private Double longitude;
}
