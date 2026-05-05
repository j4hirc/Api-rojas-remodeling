package com.rojas.remodeling.Api_rojas_remodeling.service.implementation;


import com.rojas.remodeling.Api_rojas_remodeling.dto.request.MaterialsRequestDto;
import com.rojas.remodeling.Api_rojas_remodeling.dto.response.MaterialsResponseDto;
import com.rojas.remodeling.Api_rojas_remodeling.model.Categories;
import com.rojas.remodeling.Api_rojas_remodeling.model.Materials;
import com.rojas.remodeling.Api_rojas_remodeling.repository.CategoriesRepository;
import com.rojas.remodeling.Api_rojas_remodeling.repository.MaterialsRepository;
import com.rojas.remodeling.Api_rojas_remodeling.service.MaterialsService;
import com.rojas.remodeling.Api_rojas_remodeling.service.mapper.MaterialsMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MaterialsServiceImpl implements MaterialsService {

    private final MaterialsRepository materialsRepository;
    private final CategoriesRepository categoriesRepository;
    private final MaterialsMapper materialsMapper;


    @Override
    public MaterialsResponseDto createMaterials(MaterialsRequestDto materialsRequestDto) {
        if (materialsRepository.existsByName(materialsRequestDto.getName())) {
            throw new RuntimeException(materialsRequestDto.getName() + " already exists");
        }

        Categories categories = categoriesRepository.findById(materialsRequestDto.getCategoryId())
                .orElseThrow(() -> new RuntimeException(materialsRequestDto.getCategoryId() + " does not exist"));

        Materials material = materialsMapper.toEntity(materialsRequestDto);
        material.setCategory(categories);
        return materialsMapper.toResponseDto(materialsRepository.save(material));
    }

    @Override
    public List<MaterialsResponseDto> getAllMaterials() {
        return materialsRepository.findAll().stream()
                .map(materialsMapper::toResponseDto)
                .collect(Collectors.toList());
    }
}
