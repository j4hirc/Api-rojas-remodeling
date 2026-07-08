package com.rojas.remodeling.Api_rojas_remodeling.service.implementation;

import com.rojas.remodeling.Api_rojas_remodeling.dto.request.JobRequestDto;
import com.rojas.remodeling.Api_rojas_remodeling.dto.request.MaterialSelectionDto;
import com.rojas.remodeling.Api_rojas_remodeling.dto.response.EvidencesResponseDto;
import com.rojas.remodeling.Api_rojas_remodeling.dto.response.JobResponseDto;
import com.rojas.remodeling.Api_rojas_remodeling.dto.response.JobUpdateResponseDto;
import com.rojas.remodeling.Api_rojas_remodeling.dto.response.MaterialsResponseDto;
import com.rojas.remodeling.Api_rojas_remodeling.exception.ResourceNotFoundException;
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

import java.util.Map;
import java.util.stream.Collectors;
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
    public JobResponseDto findById(Long id) {
        Jobs job = findJobById(id);
        List<JobMaterial> jobMaterials = jobMaterialRepository.findByJobId(job.getId());
        List<JobUpdateResponseDto> updates = getJobUpdatesResponse(job.getId());
        return buildSingleJobResponse(job, jobMaterials, updates);
    }

    @Override
    @Transactional(readOnly = true)
    public List<JobResponseDto> findAll() {
        List<Jobs> allJobs = jobsRepository.findAll();
        return buildJobResponses(allJobs);
    }

    @Override
    public List<JobResponseDto> findByNameEmployee(String nameEmployee) {
        List<Jobs> jobs = jobsRepository.findByEmployeeFirstName(nameEmployee);
        return buildJobResponses(jobs);
    }

    @Override
    @Transactional(readOnly = true)
    public List<JobResponseDto> findByEmployeeId(Long employeeId) {
        List<Jobs> jobs = jobsRepository.findByEmployeeId(employeeId);
        return buildJobResponses(jobs);
    }

    @Override
    @Transactional
    public JobResponseDto createJob(JobRequestDto dto) {
        Users employee = findUserById(dto.getEmployeeId(), "Empleado");
        Users manager = findUserById(dto.getManagerId(), "Manager");
        Jobs job = jobMapper.JobRequestDtoToJobs(dto, employee, manager);
        Jobs savedJob = jobsRepository.save(job);

        saveJobMaterials(savedJob, dto.getMaterials());

        List<JobMaterial> finalMaterials = jobMaterialRepository.findByJobId(savedJob.getId());
        return buildSingleJobResponse(savedJob, finalMaterials, List.of());
    }

    @Override
    @Transactional
    public JobResponseDto updateJob(Long id, JobRequestDto dto) {
        Jobs existingJob = findJobById(id);
        Users employee = findUserById(dto.getEmployeeId(), "Empleado");
        Users manager = findUserById(dto.getManagerId(), "Manager");

        jobMapper.updateEntityFromDto(dto, existingJob, employee, manager);

        if (dto.getPriority() != null) {
            existingJob.setPriority(dto.getPriority());
        }else{
            existingJob.setPriority(2);
        }
        Jobs savedJob = jobsRepository.save(existingJob);

        syncJobMaterials(savedJob, dto.getMaterials());

        List<JobMaterial> finalMaterials = jobMaterialRepository.findByJobId(savedJob.getId());
        List<JobUpdateResponseDto> updates = getJobUpdatesResponse(savedJob.getId());
        return buildSingleJobResponse(savedJob, finalMaterials, updates);
    }

    @Override
    @Transactional
    public void deleteJob(Long id) {
        Jobs job = findJobById(id);
        List<JobUpdates> updates = jobUpdateRepository.findByJobId(job.getId());
        List<Long> updateIds = updates.stream().map(JobUpdates::getId).toList();
        if (!updateIds.isEmpty()) { evidencesRepository.deleteAllByJobUpdateIds(updateIds); }
        jobUpdateRepository.deleteByJobId(job.getId());
        jobMaterialRepository.deleteByJobId(job.getId());
        jobsRepository.delete(job);
    }

    private List<JobResponseDto> buildJobResponses(List<Jobs> jobs) {
        if (jobs.isEmpty()) return List.of();
        List<Long> jobIds = jobs.stream().map(Jobs::getId).toList();
        List<JobMaterial> allJobMaterials = jobMaterialRepository.findAllByJobIds(jobIds);

        Map<Long, List<MaterialsResponseDto>> materialsByJobId =
                allJobMaterials.stream().collect(Collectors.groupingBy(jm -> jm.getJob().getId(),
                        Collectors.mapping(jm -> {
                            MaterialsResponseDto mDto = materialsMapper.toResponseDto(jm.getMaterial());
                            mDto.setQuantity(jm.getQuantity());
                            mDto.setUnit(jm.getUnit());
                            return mDto;
                        }, Collectors.toList())));

        List<JobUpdates> allJobUpdates = jobUpdateRepository.findAllByJobIds(jobIds);
        List<Long> updateIds = allJobUpdates.stream().map(JobUpdates::getId).toList();
        List<Evidences> allEvidences = updateIds.isEmpty() ? List.of() : evidencesRepository.findAllByJobUpdateIds(updateIds);

        Map<Long, List<EvidencesResponseDto>> evidencesByUpdateId = allEvidences.stream()
                .collect(Collectors.groupingBy(ev -> ev.getJobUpdate().getId(),
                        Collectors.mapping(evidencesMapper::toResponse, Collectors.toList())));

        Map<Long, List<JobUpdateResponseDto>> updatesByJobId = allJobUpdates.stream()
                .collect(Collectors.groupingBy(update -> update.getJob().getId(),
                        Collectors.mapping(update -> {
                            List<EvidencesResponseDto> evidences = evidencesByUpdateId.getOrDefault(update.getId(), List.of());
                            return jobUpdateMapper.toResponse(update, evidences);
                        }, Collectors.toList())));

        return jobs.stream().map(job -> {
            List<MaterialsResponseDto> materials = materialsByJobId.getOrDefault(job.getId(), List.of());
            List<JobUpdateResponseDto> updates = updatesByJobId.getOrDefault(job.getId(), List.of());
            return jobMapper.jobsToJobResponseDto(job, materials, updates);
        }).toList();
    }

    private void syncJobMaterials(Jobs job, List<MaterialSelectionDto> incomingMaterials) {
        List<JobMaterial> existingJobMaterials = jobMaterialRepository.findByJobId(job.getId());
        jobMaterialRepository.deleteAll(existingJobMaterials);
        jobMaterialRepository.flush();
        saveJobMaterials(job, incomingMaterials);
    }

    // 🔥 AQUÍ ESTABA EL ERROR. AHORA SÍ GUARDARÁ QUANTITY Y UNIT.
    private void saveJobMaterials(Jobs job, List<MaterialSelectionDto> materialDtos) {
        if (materialDtos == null || materialDtos.isEmpty()) return;
        List<JobMaterial> jobMaterials = materialDtos.stream().map(dto -> {
            Materials material = materialsRepository.findById(dto.getMaterialId())
                    .orElseThrow(() -> new ResourceNotFoundException("Material no encontrado"));
            JobMaterial jm = new JobMaterial();
            jm.setJob(job);
            jm.setMaterial(material);
            jm.setQuantity(dto.getQuantity());
            jm.setUnit(dto.getUnit());
            return jm;
        }).toList();
        jobMaterialRepository.saveAll(jobMaterials);
    }

    private List<JobUpdateResponseDto> getJobUpdatesResponse(Long jobId) {
        List<JobUpdates> updates = jobUpdateRepository.findByJobId(jobId);
        return updates.stream().map(update -> {
            List<EvidencesResponseDto> evidences = evidencesRepository
                    .findAllJobUpdateId(update.getId()).stream()
                    .map(evidencesMapper::toResponse).toList();
            return jobUpdateMapper.toResponse(update, evidences);
        }).toList();
    }

    private JobResponseDto buildSingleJobResponse(Jobs job, List<JobMaterial> jobMaterials, List<JobUpdateResponseDto> updates) {
        List<MaterialsResponseDto> materialsResponse = jobMaterials.stream()
                .map(jm -> {
                    MaterialsResponseDto mDto = materialsMapper.toResponseDto(jm.getMaterial());
                    mDto.setQuantity(jm.getQuantity());
                    mDto.setUnit(jm.getUnit());
                    return mDto;
                }).toList();
        return jobMapper.jobsToJobResponseDto(job, materialsResponse, updates);
    }

    private Jobs findJobById(Long jobId) {
        return jobsRepository.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("Trabajo no encontrado con ID: " + jobId));
    }

    private Users findUserById(Long userId, String role) {
        return usersRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(role + " no encontrado con ID: " + userId));
    }
}