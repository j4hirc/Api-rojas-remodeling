package com.rojas.remodeling.Api_rojas_remodeling.controller;


import com.rojas.remodeling.Api_rojas_remodeling.dto.request.CategoriesRequestDto;
import com.rojas.remodeling.Api_rojas_remodeling.dto.response.CategoriesResponseDto;
import com.rojas.remodeling.Api_rojas_remodeling.service.CategoriesService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/categories")
public class CategoriesController {
    private final CategoriesService categoriesService;

    @GetMapping("/all")
    @PreAuthorize("hasAnyRole('ADMIN', 'JEFE', 'EMPLOYEE')")
    public ResponseEntity<List<CategoriesResponseDto>> listAll(){
        return ResponseEntity.ok(categoriesService.getAllCategories());
    }

    @GetMapping("/id/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'JEFE', 'EMPLOYEE')")
    public ResponseEntity<CategoriesResponseDto> findById(@PathVariable Long id) {
        return ResponseEntity.ok(categoriesService.findById(id));
    }

    @GetMapping("/name/{name}")
    @PreAuthorize("hasAnyRole('ADMIN', 'JEFE', 'EMPLOYEE')")
    public ResponseEntity<CategoriesResponseDto> findByName(@PathVariable String name) {
        return ResponseEntity.ok(categoriesService.findByName(name));
    }



    @PostMapping("/create")
    @PreAuthorize("hasAnyRole('ADMIN', 'JEFE')")
    public ResponseEntity<CategoriesResponseDto> create(@RequestBody CategoriesRequestDto categoriesRequestDto) {
        CategoriesResponseDto categoriesResponseDto = categoriesService.createCategories(categoriesRequestDto);
        return new ResponseEntity<>(categoriesResponseDto, HttpStatus.CREATED);
    }

    @PutMapping("/update/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'JEFE')")
    public ResponseEntity<CategoriesResponseDto> update(@RequestBody CategoriesRequestDto categoriesRequestDto, @PathVariable Long id) {

        return ResponseEntity.ok(categoriesService.updateCategories(id, categoriesRequestDto));
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'JEFE')")
    public ResponseEntity<CategoriesResponseDto> delete(@PathVariable Long id) {
        categoriesService.deleteCategories(id);
        return ResponseEntity.noContent().build();
    }
}
