package com.rojas.remodeling.Api_rojas_remodeling.controller;

import com.rojas.remodeling.Api_rojas_remodeling.dto.request.JobRequestDto;
import com.rojas.remodeling.Api_rojas_remodeling.dto.response.JobResponseDto;
import com.rojas.remodeling.Api_rojas_remodeling.service.JobService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;

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
    @PreAuthorize("hasAnyRole('ADMIN', 'JEFE')")
    public ResponseEntity<List<JobResponseDto>> findAll(){
        return ResponseEntity.ok(service.findAll());
    }


    @PostMapping("/create-job")
    @PreAuthorize("hasAnyRole('ADMIN', 'JEFE')")
    public ResponseEntity<JobResponseDto> createJob(@Valid @RequestBody JobRequestDto jobRequestDto){
        JobResponseDto jobResponseDto = service.createJob(jobRequestDto);
        return new ResponseEntity<>(jobResponseDto, HttpStatus.CREATED);
    }

    @PutMapping("/update-job/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'JEFE')")
    public ResponseEntity<JobResponseDto> updateJob(@PathVariable Long id,@Valid @RequestBody JobRequestDto jobRequestDto){
        return ResponseEntity.ok(service.updateJob(id,jobRequestDto));
    }


    @DeleteMapping("/delete-job/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'JEFE')")
    public ResponseEntity<String> deleteJob(@PathVariable Long id){
        service.deleteJob(id);
        return ResponseEntity.ok("Trabajo eliminado con exito");
    }


}
