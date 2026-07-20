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
import com.rojas.remodeling.Api_rojas_remodeling.service.UserService;
import com.rojas.remodeling.Api_rojas_remodeling.service.mapper.EvidencesMapper;
import com.rojas.remodeling.Api_rojas_remodeling.service.mapper.JobMapper;
import com.rojas.remodeling.Api_rojas_remodeling.service.mapper.JobUpdateMapper;
import com.rojas.remodeling.Api_rojas_remodeling.service.mapper.MaterialsMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

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
    private final JobBlueprintRepository jobBlueprintRepository;
    private final EvidencesMapper evidencesMapper;

    private final SupabaseStorageService supabaseStorageService;

    private final EmailService emailService;

    @Override
    @Transactional(readOnly = true)
    public JobResponseDto findById(Long id) {
        Jobs job = findJobById(id);
        List<JobMaterial> jobMaterials = jobMaterialRepository.findByJobId(job.getId());
        List<JobUpdateResponseDto> updates = getJobUpdatesResponse(job.getId());

        List<String> urls = jobBlueprintRepository.findByJobId(job.getId()).stream().map(JobBlueprint::getUrl).toList();
        return buildSingleJobResponse(job, jobMaterials, updates, urls);
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
    public JobResponseDto createJob(JobRequestDto dto, List<MultipartFile> files) {
        Users employee = findUserById(dto.getEmployeeId(), "Empleado");
        Users manager = findUserById(dto.getManagerId(), "Manager");
        Jobs job = jobMapper.JobRequestDtoToJobs(dto, employee, manager);

        Jobs savedJob = jobsRepository.save(job);

        if (files != null && !files.isEmpty()) {
            List<JobBlueprint> blueprints = files.stream()
                    .filter(file -> !file.isEmpty())
                    .map(file -> {
                        String url = supabaseStorageService.uploadFile(file);
                        JobBlueprint bp = new JobBlueprint();
                        bp.setJob(savedJob);
                        bp.setUrl(url);
                        return bp;
                    }).toList();
            jobBlueprintRepository.saveAll(blueprints);
        }

        saveJobMaterials(savedJob, dto.getMaterials());

        if (employee.getEmail() != null && !employee.getEmail().isBlank()) {
            String subject = "Nuevo trabajo asignado: " + savedJob.getClientName();
            String message = "Hola " + employee.getFirstName() + ",\n\n"
                    + "Se te ha asignado un nuevo trabajo.\n\n"
                    + "📌 Detalles del trabajo:\n"
                    + "- Cliente: " + savedJob.getClientName() + "\n"
                    + "- Teléfono: " + savedJob.getClientPhone() + "\n"
                    + "- Dirección: " + savedJob.getAddress() + "\n"
                    + "- Fecha del trabajo: " + savedJob.getJobDate() + "\n"
                    + "- Descripción: " + savedJob.getDescription() + "\n\n"
                    + "Por favor, ingresa a la aplicación para revisar los detalles.";

            emailService.sendEmail(employee.getEmail(), subject, message);
        }

        List<JobMaterial> finalMaterials = jobMaterialRepository.findByJobId(savedJob.getId());
        List<String> urls = jobBlueprintRepository.findByJobId(savedJob.getId()).stream().map(JobBlueprint::getUrl).toList();

        // 🔥 CORREGIDO: Pasamos los datos correctamente usando el método de apoyo
        return buildSingleJobResponse(savedJob, finalMaterials, List.of(), urls);
    }

    @Override
    @Transactional
    public JobResponseDto updateJob(Long id, JobRequestDto dto, List<MultipartFile> files) {
        Jobs existingJob = findJobById(id);
        Users employee = findUserById(dto.getEmployeeId(), "Empleado");
        Users manager = findUserById(dto.getManagerId(), "Manager");

        jobMapper.updateEntityFromDto(dto, existingJob, employee, manager);
        Jobs savedJob = jobsRepository.save(existingJob);

        if (files != null && !files.isEmpty()) {
            List<JobBlueprint> newBlueprints = files.stream()
                    .filter(file -> !file.isEmpty())
                    .map(file -> {
                        String url = supabaseStorageService.uploadFile(file);
                        JobBlueprint bp = new JobBlueprint();
                        bp.setJob(savedJob);
                        bp.setUrl(url);
                        return bp;
                    }).toList();
            jobBlueprintRepository.saveAll(newBlueprints);
        }

        syncJobMaterials(savedJob, dto.getMaterials());

        if (employee.getEmail() != null && !employee.getEmail().isBlank()) {
            String subject = "Actualización de trabajo: " + savedJob.getClientName();
            String message = "Hola " + employee.getFirstName() + ",\n\n"
                    + "Se han actualizado los detalles de un trabajo asignado a ti.\n\n"
                    + "📌 Información del trabajo:\n"
                    + "- Cliente: " + savedJob.getClientName() + "\n"
                    + "- Dirección: " + savedJob.getAddress() + "\n"
                    + "- Fecha: " + savedJob.getJobDate() + "\n"
                    + "- Descripción: " + savedJob.getDescription() + "\n\n"
                    + "Revisa la aplicación para ver todos los cambios.";

            emailService.sendEmail(employee.getEmail(), subject, message);
        }

        List<JobMaterial> finalMaterials = jobMaterialRepository.findByJobId(savedJob.getId());
        List<JobUpdateResponseDto> updates = getJobUpdatesResponse(savedJob.getId());

        List<String> urls = jobBlueprintRepository.findByJobId(savedJob.getId())
                .stream().map(JobBlueprint::getUrl).toList();

        return buildSingleJobResponse(savedJob, finalMaterials, updates, urls);
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
        jobBlueprintRepository.deleteByJobId(job.getId()); // 🔥 Borramos los planos también
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

            // 🔥 CORREGIDO: Leemos los URLs directamente de la entidad Jobs y se los pasamos al Mapper
            List<String> urls = job.getBlueprints() != null ?
                    job.getBlueprints().stream().map(JobBlueprint::getUrl).toList() :
                    List.of();

            return jobMapper.jobsToJobResponseDto(job, materials, updates, urls);
        }).toList();
    }

    private void syncJobMaterials(Jobs job, List<MaterialSelectionDto> incomingMaterials) {
        List<JobMaterial> existingJobMaterials = jobMaterialRepository.findByJobId(job.getId());
        jobMaterialRepository.deleteAll(existingJobMaterials);
        jobMaterialRepository.flush();
        saveJobMaterials(job, incomingMaterials);
    }

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

    private JobResponseDto buildSingleJobResponse(Jobs job, List<JobMaterial> jobMaterials, List<JobUpdateResponseDto> updates, List<String> blueprintUrls) {
        List<MaterialsResponseDto> materialsResponse = jobMaterials.stream()
                .map(jm -> {
                    MaterialsResponseDto mDto = materialsMapper.toResponseDto(jm.getMaterial());
                    mDto.setQuantity(jm.getQuantity());
                    mDto.setUnit(jm.getUnit());
                    return mDto;
                }).toList();

        return jobMapper.jobsToJobResponseDto(job, materialsResponse, updates, blueprintUrls);
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