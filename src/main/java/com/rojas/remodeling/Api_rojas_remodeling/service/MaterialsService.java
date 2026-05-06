package com.rojas.remodeling.Api_rojas_remodeling.service;

import com.rojas.remodeling.Api_rojas_remodeling.dto.request.MaterialsRequestDto;
import com.rojas.remodeling.Api_rojas_remodeling.dto.response.MaterialsResponseDto;

import java.util.List;

public interface MaterialsService {

    //Crear
    MaterialsResponseDto createMaterials(MaterialsRequestDto materialsRequestDto);

    // Leer todos
    List<MaterialsResponseDto> findAll();

    //Leer por ID
    MaterialsResponseDto findById(long id);

    //Actualizar
    MaterialsResponseDto updateMaterials(Long id, MaterialsRequestDto materialsRequestDto);

    //Eliminar
    void deleteMaterials(Long id);

    // Busquedas personalizadas
    List<MaterialsResponseDto> findByCategoriesId(Long id);
    List<MaterialsResponseDto> findByName(String name);


}
