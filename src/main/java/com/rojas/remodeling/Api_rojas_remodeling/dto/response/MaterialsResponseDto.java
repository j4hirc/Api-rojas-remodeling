package com.rojas.remodeling.Api_rojas_remodeling.dto.response;

import lombok.Data;

@Data
public class MaterialsResponseDto {

    private Long materialId;
    private String name;


    private Integer count;
    private Double price;

    private Double quantity;
    private String unit;

    private String categoryName;
}
