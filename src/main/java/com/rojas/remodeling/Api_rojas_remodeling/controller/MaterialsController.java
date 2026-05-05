package com.rojas.remodeling.Api_rojas_remodeling.controller;


import com.rojas.remodeling.Api_rojas_remodeling.dto.request.MaterialsRequestDto;
import com.rojas.remodeling.Api_rojas_remodeling.dto.response.MaterialsResponseDto;
import com.rojas.remodeling.Api_rojas_remodeling.service.MaterialsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/materials")
public class MaterialsController {

    private final MaterialsService materialsService;

    @PostMapping
    public ResponseEntity<MaterialsResponseDto> create(@RequestBody MaterialsRequestDto materialsRequestDto) {
        return ResponseEntity.ok(materialsService.createMaterials(materialsRequestDto));
    }

    @GetMapping
    public ResponseEntity<List<MaterialsResponseDto>> listAll() {
        return ResponseEntity.ok(materialsService.getAllMaterials());
    }
}
