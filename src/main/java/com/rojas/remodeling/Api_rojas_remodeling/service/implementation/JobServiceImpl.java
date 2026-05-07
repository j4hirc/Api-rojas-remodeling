package com.rojas.remodeling.Api_rojas_remodeling.service.implementation;

import com.rojas.remodeling.Api_rojas_remodeling.dto.request.JobRequestDto;
import com.rojas.remodeling.Api_rojas_remodeling.dto.response.EvidencesResponseDto;
import com.rojas.remodeling.Api_rojas_remodeling.dto.response.JobResponseDto;
import com.rojas.remodeling.Api_rojas_remodeling.dto.response.JobUpdateResponseDto;
import com.rojas.remodeling.Api_rojas_remodeling.dto.response.MaterialsResponseDto;
import com.rojas.remodeling.Api_rojas_remodeling.model.*;
import com.rojas.remodeling.Api_rojas_remodeling.repository.*;
import com.rojas.remodeling.Api_rojas_remodeling.service.JobService;
import com.rojas.remodeling.Api_rojas_remodeling.service.mapper.EvidencesMapper;
import com.rojas.remodeling.Api_rojas_remodeling.service.mapper.JobMapper;
import com.rojas.remodeling.Api_rojas_remodeling.service.mapper.JobUpdateMapper;
import com.rojas.remodeling.Api_rojas_remodeling.service.mapper.MaterialsMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class JobServiceImpl implements JobService {
    private final JobsRepository jobsRepository;
    private final UsersRepository usersRepository;
    private final MaterialsRepository materialsRepository;
    private final JobsMaterialRepository jobMaterialRepository;
    private final JobUpdateRepository jobUpdateRepository;
    private final EvidencesRepository evidencesRepository;
    private final JobMapper jobMapper;
    private final JobUpdateMapper jobUpdateMapper;
    private final MaterialsMapper materialsMapper;
    private final EvidencesMapper evidencesMapper;


    @Override
    @Transactional(readOnly = true)
    public List<JobResponseDto> findAll() {
        List<Jobs> allJobs = jobsRepository.findAll();

        return allJobs.stream().map(job -> {
            List<JobMaterial> jobMaterials = jobMaterialRepository.findByJobId(job.getId());
            List<MaterialsResponseDto> materialsResponseList = jobMaterials.stream()
                    .map(jm -> materialsMapper.toResponseDto(jm.getMaterial()))
                    .toList();
            List<JobUpdates> jobUpdatesHistory = jobUpdateRepository.findByJobId(job.getId());
            List<JobUpdateResponseDto> jobUpdateResponseList = jobUpdatesHistory.stream()
                    .map(update -> {
                        List<Evidences> updateEvidences = evidencesRepository.finAllJobUpdateId(update.getId());
                        List<EvidencesResponseDto> evResponse = updateEvidences.stream()
                                .map(evidencesMapper::toResponse)
                                .toList();
                        return jobUpdateMapper.toResponse(update, evResponse);
                    })
                    .toList();
            return jobMapper.jobsToJobResponseDto(job, materialsResponseList, jobUpdateResponseList);

        }).toList();
    }

    @Override
    public List<JobResponseDto> findByEmployeeId() {
        return List.of();
    }


    @Override
    @Transactional
    public JobResponseDto createJob(JobRequestDto jobRequestDto) {

        Users employee = usersRepository.findById(jobRequestDto.getEmployeeId())
                .orElseThrow(() -> new RuntimeException("Empleado no encontrado con ID: " + jobRequestDto.getEmployeeId()));
        Users manager = usersRepository.findById(jobRequestDto.getManagerId())
                .orElseThrow(() -> new RuntimeException("Manager no encontrado con ID: " + jobRequestDto.getManagerId()));


        Jobs jobEntity = jobMapper.JobRequestDtoToJobs(jobRequestDto, employee, manager);
        Jobs savedJob = jobsRepository.save(jobEntity);


        List<Materials> foundMaterials = materialsRepository.findAllById(jobRequestDto.getMaterialIds());

        List<JobMaterial> jobMaterialsToSave = foundMaterials.stream()
                .map(foundMaterial -> new JobMaterial(null, savedJob, foundMaterial))
                .toList();

        jobMaterialRepository.saveAll(jobMaterialsToSave);

        List<MaterialsResponseDto> materialsResponseList = foundMaterials.stream()
                .map(materialsMapper::toResponseDto)
                .toList();


        return jobMapper.jobsToJobResponseDto(savedJob, materialsResponseList, new ArrayList<>());
    }




    @Override
    @Transactional
    public JobResponseDto updateJob(Long id, JobRequestDto jobRequestDto) {
        Jobs existingJob = jobsRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Trabajo no encontrado con ID: " + id));

        Users employee = usersRepository.findById(jobRequestDto.getEmployeeId())
                .orElseThrow(() -> new RuntimeException("Empleado no encontrado con ID: " + jobRequestDto.getEmployeeId()));
        Users manager = usersRepository.findById(jobRequestDto.getManagerId())
                .orElseThrow(() -> new RuntimeException("Manager no encontrado con ID: " + jobRequestDto.getManagerId()));

        jobMapper.updateEntityFromDto(jobRequestDto, existingJob, employee, manager);


        List<JobMaterial> existingJobMaterials = jobMaterialRepository.findByJobId(existingJob.getId());
        List<Long> incomingMaterialIds = jobRequestDto.getMaterialIds();

        List<JobMaterial> materialsToDelete = existingJobMaterials.stream()
                .filter(jm -> !incomingMaterialIds.contains(jm.getMaterial().getId()))
                .toList();

        jobMaterialRepository.deleteAll(materialsToDelete);

        List<Long> existingMaterialIds = existingJobMaterials.stream()
                .map(jm -> jm.getMaterial().getId())
                .toList();

        List<Long> newMaterialIdsToInsert = incomingMaterialIds.stream()
                .filter(incomingId -> !existingMaterialIds.contains(incomingId))
                .toList();


        if (!newMaterialIdsToInsert.isEmpty()) {
            List<Materials> newMaterials = materialsRepository.findAllById(newMaterialIdsToInsert);
            List<JobMaterial> newJobMaterials = newMaterials.stream()
                    .map(newMaterial -> new JobMaterial(null, existingJob, newMaterial))
                    .toList();
            jobMaterialRepository.saveAll(newJobMaterials);
        }


        Jobs savedJob = jobsRepository.save(existingJob);

        List<Materials> finalMaterialsForResponse = materialsRepository.findAllById(incomingMaterialIds);
        List<MaterialsResponseDto> materialsResponseList = finalMaterialsForResponse.stream()
                .map(materialsMapper::toResponseDto)
                .toList();


        List<JobUpdates> jobUpdatesHistory = jobUpdateRepository.findByJobId(savedJob.getId());

        List<JobUpdateResponseDto> jobUpdateResponseList = jobUpdatesHistory.stream()
                .map(update -> {
                    List<Evidences> updateEvidences = evidencesRepository.finAllJobUpdateId(update.getId());
                    List<EvidencesResponseDto> evResponse = updateEvidences.stream()
                            .map(evidencesMapper::toResponse)
                            .toList();
                    return jobUpdateMapper.toResponse(update, evResponse);
                })
                .toList();


        return jobMapper.jobsToJobResponseDto(savedJob, materialsResponseList, jobUpdateResponseList);
    }

}
