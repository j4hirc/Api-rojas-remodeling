package com.rojas.remodeling.Api_rojas_remodeling.service;

import com.rojas.remodeling.Api_rojas_remodeling.dto.request.CategoriesRequestDto;
import com.rojas.remodeling.Api_rojas_remodeling.dto.response.CategoriesResponseDto;

import java.util.List;

public interface CategoriesService {

    CategoriesResponseDto createCategories(CategoriesRequestDto categoriesRequestDto);
    List<CategoriesResponseDto> getAllCategories();
}
