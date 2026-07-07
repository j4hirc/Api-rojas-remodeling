package com.rojas.remodeling.Api_rojas_remodeling.dto.request;

import lombok.Data;

@Data
public class MaterialSelectionDto {
    private Long materialId;
    private Double quantity;
    private String unit;
}
