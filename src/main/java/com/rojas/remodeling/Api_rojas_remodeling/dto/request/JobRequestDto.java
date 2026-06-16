package com.rojas.remodeling.Api_rojas_remodeling.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.time.LocalDate; // <-- IMPORTANTE
import java.util.List;

@Data
public class JobRequestDto {

    @NotBlank(message = "El nombre del cliente no puede estar vacío")
    private String clientName;

    @NotBlank(message = "El teléfono del cliente no puede estar vacío")
    private String clientPhone;

    @NotBlank(message = "La descripción del trabajo no puede estar vacía")
    private String description;

    @NotBlank(message = "La dirección no puede estar vacía")
    private String address;

    @NotNull(message = "La latitud es obligatoria para la ubicación")
    private Double latitude;

    @NotNull(message = "La longitud es obligatoria para la ubicación")
    private Double longitude;

    private String safeDepositBoxCodes;

    @NotBlank(message = "El estado no puede estar vacío")
    private String status;

    @Positive(message = "El pago debe ser un valor positivo")
    private Double pay;

    // NUEVO CAMPO
    @NotNull(message = "La fecha del trabajo es obligatoria")
    private LocalDate jobDate;

    @NotNull(message = "El ID del empleado es obligatorio")
    private Long employeeId;

    @NotNull(message = "El ID del manager es obligatorio")
    private Long managerId;

    @NotEmpty(message = "Debes incluir al menos un material para el trabajo")
    private List<Long> materialIds;

    private Long jobUpdateId;


}