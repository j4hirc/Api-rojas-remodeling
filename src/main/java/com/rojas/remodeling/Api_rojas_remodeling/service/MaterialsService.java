package com.rojas.remodeling.Api_rojas_remodeling.service;

import com.rojas.remodeling.Api_rojas_remodeling.dto.request.MaterialsRequestDto;
import com.rojas.remodeling.Api_rojas_remodeling.dto.response.MaterialsResponseDto;

import java.util.List;

public interface MaterialsService {

    //Crear
    MaterialsResponseDto createMaterials(MaterialsRequestDto materialsRequestDto);


    List<MaterialsResponseDto> findAll();


    MaterialsResponseDto findById(long id);


    MaterialsResponseDto updateMaterials(Long id, MaterialsRequestDto materialsRequestDto);


    void deleteMaterials(Long id);

    // Busquedas personalizadas
    List<MaterialsResponseDto> findByCategoriesId(Long id);
    List<MaterialsResponseDto> findByName(String name);

    List<MaterialsResponseDto> findByCategoriesName(String name);


}
