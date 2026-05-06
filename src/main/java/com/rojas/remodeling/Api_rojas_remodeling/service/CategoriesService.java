package com.rojas.remodeling.Api_rojas_remodeling.service;

import com.rojas.remodeling.Api_rojas_remodeling.dto.request.CategoriesRequestDto;
import com.rojas.remodeling.Api_rojas_remodeling.dto.response.CategoriesResponseDto;

import java.util.List;

public interface CategoriesService {

    //Crear
    CategoriesResponseDto createCategories(CategoriesRequestDto categoriesRequestDto);

    //Lee todos
    List<CategoriesResponseDto> getAllCategories();

    //Lee por el ID
    CategoriesResponseDto findById(Long id);

    //Actualizar
    CategoriesResponseDto updateCategories(Long id, CategoriesRequestDto categoriesRequestDto);

    //Eliminar
    void deleteCategories(Long id);

    // Busqueda personalizada
    CategoriesResponseDto findByName(String name);

}
