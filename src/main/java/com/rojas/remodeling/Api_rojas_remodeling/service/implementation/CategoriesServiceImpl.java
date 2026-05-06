package com.rojas.remodeling.Api_rojas_remodeling.service.implementation;


import com.rojas.remodeling.Api_rojas_remodeling.dto.request.CategoriesRequestDto;
import com.rojas.remodeling.Api_rojas_remodeling.dto.response.CategoriesResponseDto;
import com.rojas.remodeling.Api_rojas_remodeling.model.Categories;
import com.rojas.remodeling.Api_rojas_remodeling.repository.CategoriesRepository;
import com.rojas.remodeling.Api_rojas_remodeling.service.CategoriesService;
import com.rojas.remodeling.Api_rojas_remodeling.service.mapper.CategoriesMapper;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class CategoriesServiceImpl implements CategoriesService {
     private final CategoriesRepository categoriesRepository;
     private final CategoriesMapper categoriesMapper;


    @Override
    public CategoriesResponseDto createCategories(CategoriesRequestDto categoriesRequestDto) {
        if (categoriesRepository.existsByName(categoriesRequestDto.getName())){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Category already exists");
        }

        Categories category = categoriesMapper.toEntity(categoriesRequestDto);
        Categories savedCategory = categoriesRepository.save(category);

        return categoriesMapper.toResponseDto(savedCategory);
    }

    @Override
    public List<CategoriesResponseDto> getAllCategories(){
        return categoriesRepository.findAll().stream()
                .map(categoriesMapper::toResponseDto)
                .collect(Collectors.toList());
     }

    @Override
    public CategoriesResponseDto findById(Long id) {
        return categoriesRepository.findById(id)
                .map(categoriesMapper::toResponseDto)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"Category not found"));
    }

    @Override
    public CategoriesResponseDto updateCategories(Long id, CategoriesRequestDto categoriesRequestDto) {
        Categories existingCategory = categoriesRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"Category not found"));

        if (categoriesRepository.existsByName(categoriesRequestDto.getName()) &&
                !existingCategory.getName().equals(categoriesRequestDto.getName())){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Category already exists");
        }
        existingCategory.setName(categoriesRequestDto.getName());

        Categories updatedCategory = categoriesRepository.save(existingCategory);
        return categoriesMapper.toResponseDto(updatedCategory);
    }

    @Override
    public void deleteCategories(Long id) {

        if (!categoriesRepository.existsById(id)){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Category not found");
        }

        categoriesRepository.deleteById(id);

    }

    @Override
    public CategoriesResponseDto findByName(String name) {
        return categoriesRepository.findByName(name)
                .map(categoriesMapper::toResponseDto)
                .orElseThrow(()  -> new ResponseStatusException(HttpStatus.NOT_FOUND,"Category not found" + name));
    }


}
