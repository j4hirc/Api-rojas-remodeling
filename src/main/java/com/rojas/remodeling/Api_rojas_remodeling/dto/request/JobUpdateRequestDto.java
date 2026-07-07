package com.rojas.remodeling.Api_rojas_remodeling.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.util.List;

@Data
public class JobUpdateRequestDto {

    @NotBlank(message = "El comentario no puede estar vacio")
    private String comment;
    @NotNull(message = "EL id del trabajo no puede ser nulo")
    private Long jobId;
    @NotNull(message = "EL id del empleado no puede ser nulo")
    private Long employeeId;
    private Double newPrice;
    @NotBlank(message = "El estado del trabajo es requerido")
    private String status;

    private List<MaterialSelectionDto> materials;
}