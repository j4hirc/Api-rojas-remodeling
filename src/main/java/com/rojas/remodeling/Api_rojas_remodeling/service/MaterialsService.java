package com.rojas.remodeling.Api_rojas_remodeling.service;

import com.rojas.remodeling.Api_rojas_remodeling.dto.request.MaterialsRequestDto;
import com.rojas.remodeling.Api_rojas_remodeling.dto.response.MaterialsResponseDto;

import java.util.List;

public interface MaterialsService {

    MaterialsResponseDto createMaterials(MaterialsRequestDto materialsRequestDto);
    List<MaterialsResponseDto> getAllMaterials();
}
