package com.rojas.remodeling.Api_rojas_remodeling.service.mapper;

import com.rojas.remodeling.Api_rojas_remodeling.dto.request.JobRequestDto;
import com.rojas.remodeling.Api_rojas_remodeling.dto.response.JobResponseDto;
import com.rojas.remodeling.Api_rojas_remodeling.dto.response.JobUpdateResponseDto;
import com.rojas.remodeling.Api_rojas_remodeling.dto.response.MaterialsResponseDto;
import com.rojas.remodeling.Api_rojas_remodeling.model.Jobs;
import com.rojas.remodeling.Api_rojas_remodeling.model.Users;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class JobMapper {

    public Jobs JobRequestDtoToJobs(JobRequestDto jobRequestDto, Users employee, Users manager){
        Jobs entity = new Jobs();
        entity.setClientName(jobRequestDto.getClientName());
        entity.setClientPhone(jobRequestDto.getClientPhone());
        entity.setDescription(jobRequestDto.getDescription());
        entity.setAddress(jobRequestDto.getAddress());
        entity.setLatitude(jobRequestDto.getLatitude());
        entity.setLongitude(jobRequestDto.getLongitude());
        entity.setSafeDepositBoxCodes(jobRequestDto.getSafeDepositBoxCodes());
        entity.setStatus(jobRequestDto.getStatus());
        entity.setPay(jobRequestDto.getPay());

        entity.setJobDate(jobRequestDto.getJobDate());

        entity.setPriority(jobRequestDto.getPriority());

        entity.setEmployee(employee);
        entity.setManager(manager);
        return entity;
    }

    public JobResponseDto jobsToJobResponseDto(Jobs jobs, List<MaterialsResponseDto> materials, List<JobUpdateResponseDto> jobUpdate){
        JobResponseDto dto = new JobResponseDto();
        dto.setJobId(jobs.getId());
        dto.setClientName(jobs.getClientName());
        dto.setClientPhone(jobs.getClientPhone());
        dto.setDescription(jobs.getDescription());
        dto.setAddress(jobs.getAddress());
        dto.setLatitude(jobs.getLatitude());
        dto.setLongitude(jobs.getLongitude());
        dto.setSafeDepositBoxCodes(jobs.getSafeDepositBoxCodes());
        dto.setStatus(jobs.getStatus());
        dto.setPay(jobs.getPay());

        dto.setJobDate(jobs.getJobDate());

        dto.setPriority(jobs.getPriority());

        // 🔥 ¡LA LÍNEA MÁGICA QUE FALTABA! 🔥
        dto.setBlueprintUrl(jobs.getBlueprintUrl());

        dto.setEmployeeId(jobs.getEmployee().getId());
        dto.setManagerId(jobs.getManager().getId());
        dto.setNameEmployee(jobs.getEmployee().getFirstName() + " " + jobs.getEmployee().getLastName());
        dto.setNameManager(jobs.getManager().getFirstName() + " " + jobs.getManager().getLastName());
        dto.setMaterials(materials);
        dto.setUpdateJob(jobUpdate);
        return dto;
    }


    public void updateEntityFromDto(JobRequestDto dto, Jobs entity, Users employee, Users manager) {
        entity.setClientName(dto.getClientName());
        entity.setClientPhone(dto.getClientPhone());
        entity.setDescription(dto.getDescription());
        entity.setAddress(dto.getAddress());
        entity.setLatitude(dto.getLatitude());
        entity.setLongitude(dto.getLongitude());
        entity.setSafeDepositBoxCodes(dto.getSafeDepositBoxCodes());
        entity.setStatus(dto.getStatus());
        entity.setPay(dto.getPay());

        entity.setJobDate(dto.getJobDate());
        entity.setPriority(dto.getPriority());

        entity.setEmployee(employee);
        entity.setManager(manager);
    }
}