package com.rojas.remodeling.Api_rojas_remodeling.service.mapper;

import com.rojas.remodeling.Api_rojas_remodeling.dto.request.CategoriesRequestDto;
import com.rojas.remodeling.Api_rojas_remodeling.dto.response.CategoriesResponseDto;
import com.rojas.remodeling.Api_rojas_remodeling.model.Categories;
import org.springframework.stereotype.Component;

@Component
public class CategoriesMapper {

    public Categories toEntity(CategoriesRequestDto dto) {
        Categories category = new Categories();
        category.setName(dto.getName());
        return category;
    }

    public CategoriesResponseDto toResponseDto(Categories entity) {
        CategoriesResponseDto dto = new CategoriesResponseDto();
        dto.setCategoryId(entity.getId());
        dto.setName(entity.getName());
        return dto;
    }
}
