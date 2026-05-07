package com.rojas.remodeling.Api_rojas_remodeling.controller;


import com.rojas.remodeling.Api_rojas_remodeling.dto.request.MaterialsRequestDto;
import com.rojas.remodeling.Api_rojas_remodeling.dto.response.MaterialsResponseDto;
import com.rojas.remodeling.Api_rojas_remodeling.service.MaterialsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/materials")
public class MaterialsController {

    private final MaterialsService materialsService;

    @PostMapping("/create")
    @PreAuthorize("hasAnyRole('ADMIN', 'JEFE')")
    public ResponseEntity<MaterialsResponseDto> create(@Valid @RequestBody MaterialsRequestDto materialsRequestDto) {
        MaterialsResponseDto materialsResponseDto = materialsService.createMaterials(materialsRequestDto);
        return new ResponseEntity<>(materialsResponseDto, HttpStatus.CREATED);
    }


    @GetMapping("/all")
    @PreAuthorize("hasAnyRole('ADMIN', 'JEFE', 'EMPLOYEE')")
    public ResponseEntity<List<MaterialsResponseDto>> findAll() {
        return ResponseEntity.ok(materialsService.findAll());
    }

    // Obtener material por ID
    @GetMapping("/id/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'JEFE', 'EMPLOYEE')")
    public ResponseEntity<MaterialsResponseDto> findById(@PathVariable Long id) {
        return ResponseEntity.ok(materialsService.findById(id));
    }

    // Actualizar un material
    @PutMapping("/update/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'JEFE')")
    public ResponseEntity<MaterialsResponseDto> updateMaterials(@PathVariable Long id, @Valid @RequestBody MaterialsRequestDto materialsRequestDto) {
        return ResponseEntity.ok(materialsService.updateMaterials(id, materialsRequestDto));
    }

    // Eliminar un material
    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'JEFE')")
    public ResponseEntity<?> deleteMaterials(@PathVariable Long id) {
        materialsService.deleteMaterials(id);
        return ResponseEntity.ok("Eliminado correctamente");
    }




    // Buscar materiales por ID de categoría
    @GetMapping("/category/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'JEFE', 'EMPLOYEE')")
    public ResponseEntity<List<MaterialsResponseDto>> findByCategoriesId(@PathVariable Long id) {
        return ResponseEntity.ok(materialsService.findByCategoriesId(id));
    }



    // Buscar materiales por nombre
    @GetMapping("/search/{name}")
    @PreAuthorize("hasAnyRole('ADMIN', 'JEFE', 'EMPLOYEE')")
    public ResponseEntity<List<MaterialsResponseDto>> findByName(@PathVariable String name) {
        return ResponseEntity.ok(materialsService.findByName(name));
    }


    @GetMapping("/category-name/{name}")
    @PreAuthorize("hasAnyRole('ADMIN', 'JEFE', 'EMPLOYEE')")
    public ResponseEntity<List<MaterialsResponseDto>> findByCategoryName(@PathVariable String name) {
        return ResponseEntity.ok(materialsService.findByCategoriesName(name));
    }


}
