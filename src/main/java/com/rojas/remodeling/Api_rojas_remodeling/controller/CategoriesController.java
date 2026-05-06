package com.rojas.remodeling.Api_rojas_remodeling.controller;


import com.rojas.remodeling.Api_rojas_remodeling.dto.request.CategoriesRequestDto;
import com.rojas.remodeling.Api_rojas_remodeling.dto.response.CategoriesResponseDto;
import com.rojas.remodeling.Api_rojas_remodeling.service.CategoriesService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/categories")
public class CategoriesController {
    private final CategoriesService categoriesService;

    @GetMapping
    public ResponseEntity<List<CategoriesResponseDto>> listAll(){
        return ResponseEntity.ok(categoriesService.getAllCategories());
    }

    @PostMapping
    public ResponseEntity<CategoriesResponseDto> create(@RequestBody CategoriesRequestDto categoriesRequestDto) {
        CategoriesResponseDto categoriesResponseDto = categoriesService.createCategories(categoriesRequestDto);
        return new ResponseEntity<>(categoriesResponseDto, HttpStatus.CREATED);
    }
}
