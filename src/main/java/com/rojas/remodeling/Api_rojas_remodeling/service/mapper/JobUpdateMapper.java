package com.rojas.remodeling.Api_rojas_remodeling.service.mapper;

import com.rojas.remodeling.Api_rojas_remodeling.dto.request.JobUpdateRequestDto;
import com.rojas.remodeling.Api_rojas_remodeling.dto.response.EvidencesResponseDto;
import com.rojas.remodeling.Api_rojas_remodeling.dto.response.JobUpdateResponseDto;
import com.rojas.remodeling.Api_rojas_remodeling.model.JobUpdates;
import com.rojas.remodeling.Api_rojas_remodeling.model.Jobs;
import com.rojas.remodeling.Api_rojas_remodeling.model.Users;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class JobUpdateMapper {
    public JobUpdateResponseDto toResponse(JobUpdates entity, List<EvidencesResponseDto> evidencesResponse){
        JobUpdateResponseDto jobUpdateResponseDto = new JobUpdateResponseDto();
        jobUpdateResponseDto.setJobUpdateId(entity.getId());
        jobUpdateResponseDto.setDate(entity.getDate());
        jobUpdateResponseDto.setComment(entity.getComment());
        jobUpdateResponseDto.setEvidences(evidencesResponse);
        return jobUpdateResponseDto;
    }

    public JobUpdates toEntity(JobUpdateRequestDto jobUpdateRequestDto, Jobs job, Users employee){
        JobUpdates entity = new JobUpdates();
        entity.setComment(jobUpdateRequestDto.getComment());
        entity.setDate(LocalDateTime.now());
        entity.setEmployee(employee);
        entity.setJob(job);
        return entity;
    }

}
