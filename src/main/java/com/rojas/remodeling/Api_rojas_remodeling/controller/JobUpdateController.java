package com.rojas.remodeling.Api_rojas_remodeling.controller;

import com.rojas.remodeling.Api_rojas_remodeling.dto.request.JobUpdateRequestDto;
import com.rojas.remodeling.Api_rojas_remodeling.dto.response.JobUpdateResponseDto;
import com.rojas.remodeling.Api_rojas_remodeling.service.JobUpdateService;
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
@RequestMapping("/api/v1/job-updates")
public class JobUpdateController {

    private final JobUpdateService service;


    @PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('EMPLOYEE')")
    public ResponseEntity<JobUpdateResponseDto> createJobUpdate(
            @Valid @RequestPart("data") JobUpdateRequestDto requestDto,
            @RequestPart(value = "files") List<MultipartFile> files) {

        return new ResponseEntity<>(service.createJobUpdate(requestDto, files), HttpStatus.CREATED);
    }

    @PutMapping(value = "/update/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('EMPLOYEE')")
    public ResponseEntity<JobUpdateResponseDto> updateJobUpdate(@PathVariable Long id,
                                                                @Valid @RequestPart("data") JobUpdateRequestDto requestDto,
                                                                @RequestPart(value = "files") List<MultipartFile> files){
        return ResponseEntity.ok(service.updateJobUpdate(id,requestDto,files));
    }


}