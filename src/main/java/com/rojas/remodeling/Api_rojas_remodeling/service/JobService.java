package com.rojas.remodeling.Api_rojas_remodeling.service;

import com.rojas.remodeling.Api_rojas_remodeling.dto.request.JobRequestDto;
import com.rojas.remodeling.Api_rojas_remodeling.dto.response.JobResponseDto;

import java.util.List;

public interface JobService {
    List<JobResponseDto> findAll();
    List<JobResponseDto> findByNameEmployee(String nameEmployee);
    List<JobResponseDto> findByEmployeeId(Long employeeId);
    JobResponseDto createJob(JobRequestDto jobRequestDto);
    JobResponseDto updateJob(Long id, JobRequestDto jobRequestDto);
}
