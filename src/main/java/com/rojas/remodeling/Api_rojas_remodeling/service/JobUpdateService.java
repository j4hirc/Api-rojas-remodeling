package com.rojas.remodeling.Api_rojas_remodeling.service;

import com.rojas.remodeling.Api_rojas_remodeling.dto.request.JobUpdateRequestDto;
import com.rojas.remodeling.Api_rojas_remodeling.dto.response.JobUpdateResponseDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface JobUpdateService {
    JobUpdateResponseDto createJobUpdate(JobUpdateRequestDto requestDto, List<MultipartFile> files);

    JobUpdateResponseDto updateJobUpdate(Long id, JobUpdateRequestDto requestDto, List<MultipartFile> files);

}
