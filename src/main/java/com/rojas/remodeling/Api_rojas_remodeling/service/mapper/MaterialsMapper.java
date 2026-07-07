package com.rojas.remodeling.Api_rojas_remodeling.service.mapper;

import com.rojas.remodeling.Api_rojas_remodeling.dto.request.MaterialsRequestDto;
import com.rojas.remodeling.Api_rojas_remodeling.dto.response.MaterialsResponseDto;
import com.rojas.remodeling.Api_rojas_remodeling.model.Materials;
import org.springframework.stereotype.Component;

@Component
public class MaterialsMapper {

    public Materials toEntity(MaterialsRequestDto materialsRequestDto) {
        Materials materials = new Materials();
        materials.setName(materialsRequestDto.getName());
        materials.setCount(materialsRequestDto.getCount());
        materials.setPrice(materialsRequestDto.getPrice());
        return materials;
    }

    public MaterialsResponseDto toResponseDto(Materials materials) {
        MaterialsResponseDto materialsResponseDto = new MaterialsResponseDto();

        materialsResponseDto.setMaterialId(materials.getId());
        materialsResponseDto.setName(materials.getName());

        materialsResponseDto.setCount(materials.getCount());

        materialsResponseDto.setPrice(materials.getPrice());
        materialsResponseDto.setCategoryName(materials.getCategory().getName());

        return materialsResponseDto;
    }
}