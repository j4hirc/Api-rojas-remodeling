package com.rojas.remodeling.Api_rojas_remodeling.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ClienteRequestDto {

    @NotBlank(message = "El nombre del cliente es obligatorio")
    private String clientName;

    @Size(max = 255, message = "La compañía admite hasta 255 caracteres")
    private String companyName;

    @Size(max = 255, message = "El encargado admite hasta 255 caracteres")
    private String contactName;

    private String clientPhone;

    @NotBlank(message = "La dirección del cliente es obligatoria")
    private String address;

    @Size(max = 255, message = "La caja de código admite hasta 255 caracteres")
    private String codeBox;

    @NotNull(message = "La latitud es obligatoria para la ubicación")
    private Double latitude;

    @NotNull(message = "La longitud es obligatoria para la ubicación")
    private Double longitude;
}