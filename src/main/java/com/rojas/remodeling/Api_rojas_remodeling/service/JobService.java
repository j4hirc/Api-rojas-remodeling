package com.rojas.remodeling.Api_rojas_remodeling.service;

import com.rojas.remodeling.Api_rojas_remodeling.dto.request.JobRequestDto;
import com.rojas.remodeling.Api_rojas_remodeling.dto.response.JobResponseDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface JobService {
    JobResponseDto findById(Long id);

    List<JobResponseDto> findAll();

    List<JobResponseDto> findByNameEmployee(String nameEmployee);

    List<JobResponseDto> findByEmployeeId(Long employeeId);

    JobResponseDto createJob(JobRequestDto jobRequestDto, MultipartFile file);

    JobResponseDto updateJob(Long id, JobRequestDto jobRequestDto, MultipartFile file);

    void deleteJob(Long id);
}
