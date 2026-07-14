package com.rojas.remodeling.Api_rojas_remodeling.controller;

import com.rojas.remodeling.Api_rojas_remodeling.dto.request.JobRequestDto;
import com.rojas.remodeling.Api_rojas_remodeling.dto.response.JobResponseDto;
import com.rojas.remodeling.Api_rojas_remodeling.service.JobService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/jobs")
public class JobsController {
    private final JobService service;

    @GetMapping("/find-by-id-employee/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'JEFE')")
    public ResponseEntity<List<JobResponseDto>> findByEmployeeId(@PathVariable Long id){
        return ResponseEntity.ok(service.findByEmployeeId(id));
    }

    @GetMapping("/find-id/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'JEFE')")
    public ResponseEntity<JobResponseDto> findById(@PathVariable Long id){
        return ResponseEntity.ok(service.findById(id));
    }

    @GetMapping("/find-name-employee/{nameEmployee}")
    @PreAuthorize("hasAnyRole('ADMIN', 'JEFE')")
    public ResponseEntity<List<JobResponseDto>> findByNameEmployee(@PathVariable String nameEmployee){
        return ResponseEntity.ok(service.findByNameEmployee(nameEmployee));
    }

    @GetMapping("/all")
    @PreAuthorize("hasAnyRole('ADMIN', 'JEFE', 'EMPLOYEE')")
    public ResponseEntity<List<JobResponseDto>> findAll(){
        return ResponseEntity.ok(service.findAll());
    }

    // 🔥 MODIFICADO PARA RECIBIR EL PLANO
    @PostMapping(value = "/create-job", consumes = org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('ADMIN', 'JEFE')")
    public ResponseEntity<JobResponseDto> createJob(
            @RequestPart("data") @Valid JobRequestDto jobRequestDto,
            @RequestPart(value = "files", required = false) List<MultipartFile> files){ // 🔥 AHORA ES UNA LISTA
        JobResponseDto jobResponseDto = service.createJob(jobRequestDto, files);
        return new ResponseEntity<>(jobResponseDto, HttpStatus.CREATED);
    }

    // 🔥 MODIFICADO PARA RECIBIR EL PLANO
    @PutMapping(value = "/update-job/{id}", consumes = org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('ADMIN', 'JEFE')")
    public ResponseEntity<JobResponseDto> updateJob(
            @PathVariable Long id,
            @RequestPart("data") @Valid JobRequestDto jobRequestDto,
            @RequestPart(value = "files", required = false) List<MultipartFile> files){ // 🔥 AHORA ES UNA LISTA
        return ResponseEntity.ok(service.updateJob(id, jobRequestDto, files));
    }

    @DeleteMapping("/delete-job/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'JEFE')")
    public ResponseEntity<String> deleteJob(@PathVariable Long id){
        service.deleteJob(id);
        return ResponseEntity.ok("Trabajo eliminado con exito");
    }
}