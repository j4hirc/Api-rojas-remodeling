package com.rojas.remodeling.Api_rojas_remodeling.service;

import com.rojas.remodeling.Api_rojas_remodeling.dto.request.CategoriesRequestDto;
import com.rojas.remodeling.Api_rojas_remodeling.dto.response.CategoriesResponseDto;

import java.util.List;

public interface CategoriesService {


    CategoriesResponseDto createCategories(CategoriesRequestDto categoriesRequestDto);


    List<CategoriesResponseDto> getAllCategories();


    CategoriesResponseDto findById(Long id);


    CategoriesResponseDto updateCategories(Long id, CategoriesRequestDto categoriesRequestDto);


    void deleteCategories(Long id);


    CategoriesResponseDto findByName(String name);

}
