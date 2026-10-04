package com.rojas.remodeling.Api_rojas_remodeling.service.implementation;

import com.rojas.remodeling.Api_rojas_remodeling.dto.request.JobUpdateRequestDto;
import com.rojas.remodeling.Api_rojas_remodeling.dto.request.MaterialSelectionDto;
import com.rojas.remodeling.Api_rojas_remodeling.dto.response.EvidencesResponseDto;
import com.rojas.remodeling.Api_rojas_remodeling.dto.response.JobUpdateResponseDto;
import com.rojas.remodeling.Api_rojas_remodeling.exception.ResourceNotFoundException;
import com.rojas.remodeling.Api_rojas_remodeling.model.*;
import com.rojas.remodeling.Api_rojas_remodeling.repository.*;
import com.rojas.remodeling.Api_rojas_remodeling.service.JobUpdateService;
import com.rojas.remodeling.Api_rojas_remodeling.service.mapper.EvidencesMapper;
import com.rojas.remodeling.Api_rojas_remodeling.service.mapper.JobUpdateMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class JobUpdateServiceImpl implements JobUpdateService {
    private final JobUpdateRepository jobUpdateRepository;
    private final JobsRepository jobsRepository;
    private final UsersRepository usersRepository;
    private final EvidencesRepository evidencesRepository;
    private final MaterialsRepository materialsRepository;
    private final JobsMaterialRepository jobMaterialRepository;
    private final SupabaseStorageService supabaseStorageService;
    private final JobUpdateMapper jobUpdateMapper;
    private final EvidencesMapper evidencesMapper;
    private final EmailService emailService;
    private final MaterialBaselineService baselineService;

    @Override
    @Transactional
    public JobUpdateResponseDto createJobUpdate(JobUpdateRequestDto requestDto, List<MultipartFile> files) {

        Jobs job = jobsRepository.findByIdForUpdate(requestDto.getJobId())
                .orElseThrow(() -> new RuntimeException("Trabajo no encontrado"));
        Users employee = usersRepository.findById(requestDto.getEmployeeId())
                .orElseThrow(() -> new RuntimeException("Empleado no encontrado"));

        List<JobMaterial> currentMaterials = jobMaterialRepository.findByJobId(job.getId());
        List<MaterialSnapshot> reported = baselineService.reportMaterials(job, currentMaterials, requestDto.getMaterials());
        List<MaterialSnapshot> comparison = Boolean.TRUE.equals(job.getOriginalAssignmentAvailable())
                ? job.getOriginalMaterials() : currentMaterials.stream().map(baselineService::snapshot).toList();
        boolean modifications = Boolean.TRUE.equals(requestDto.getHasModifications())
                || baselineService.changed(comparison, reported);
        requestDto.setHasModifications(modifications);
        double calculatedPrice = baselineService.total(reported);
        if (requestDto.getNewPrice() != null && (!Double.isFinite(requestDto.getNewPrice())
                || Math.abs(requestDto.getNewPrice() - calculatedPrice) > 0.01)) {
            throw new IllegalArgumentException("Los precios cambiaron. Recarga el trabajo y vuelve a generar el reporte.");
        }
        requestDto.setNewPrice(calculatedPrice);
        job.setPay(calculatedPrice); // Zero is valid when all reported quantities are zero.

        String effectiveStatus = resolveReportStatus(job, requestDto);

        // El trabajo y el avance deben registrar el mismo estado efectivo.
        job.setStatus(effectiveStatus);
        requestDto.setStatus(effectiveStatus);

        jobsRepository.save(job);

        JobUpdates jobUpdate = jobUpdateMapper.toEntity(requestDto, job, employee);
        jobUpdate.setReportedMaterials(new ArrayList<>(reported));
        jobUpdate.setMaterialSnapshotAvailable(true);
        jobUpdate.setHasModifications(modifications);
        jobUpdate.setInitialPay(job.getInitialPay());
        JobUpdates savedUpdate = jobUpdateRepository.save(jobUpdate);

        // Synchronize the complete reported assignment, including removals.
        java.util.Set<Long> reportedIds = reported.stream().map(MaterialSnapshot::getMaterialId)
                .collect(java.util.stream.Collectors.toSet());
        jobMaterialRepository.deleteAll(currentMaterials.stream()
                .filter(row -> !reportedIds.contains(row.getMaterial().getId())).toList());
        for (MaterialSnapshot row : reported) {
            JobMaterial assignment = currentMaterials.stream()
                    .filter(existing -> existing.getMaterial().getId().equals(row.getMaterialId()))
                    .findFirst().orElseGet(JobMaterial::new);
            assignment.setJob(job);
            if (assignment.getMaterial() == null) assignment.setMaterial(materialsRepository.findById(row.getMaterialId())
                    .orElseThrow(() -> new ResourceNotFoundException("Material no encontrado")));
            assignment.setQuantity(row.getQuantity());
            assignment.setUnit(row.getUnit());
            assignment.setUnitPrice(row.getUnitPrice());
            jobMaterialRepository.save(assignment);
        }

        List<EvidencesResponseDto> evidencesResponseList = new ArrayList<>();
        String pdfPublicUrl = null;

        if (files != null && !files.isEmpty()) {
            for (MultipartFile file : files) {
                String publicUrl = supabaseStorageService.uploadFile(file);
                if (file.getOriginalFilename() != null && file.getOriginalFilename().toLowerCase().endsWith(".pdf")) {
                    pdfPublicUrl = publicUrl;
                }
                Evidences evidence = new Evidences();
                evidence.setImageUri(publicUrl);
                evidence.setJobUpdate(savedUpdate);
                Evidences savedEvidence = evidencesRepository.save(evidence);
                evidencesResponseList.add(evidencesMapper.toResponse(savedEvidence));
            }
        }

        notifyManager(job, employee, pdfPublicUrl);

        return jobUpdateMapper.toResponse(savedUpdate, evidencesResponseList);
    }

    @Override
    @Transactional
    public JobUpdateResponseDto updateJobUpdate(Long id, JobUpdateRequestDto requestDto, List<MultipartFile> files) {
        JobUpdates existingUpdate = jobUpdateRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Actualización no encontrada"));
        if (Boolean.TRUE.equals(existingUpdate.getMaterialSnapshotAvailable())) {
            throw new IllegalArgumentException("Este reporte conserva un historial firmado. Registra un nuevo avance para corregirlo.");
        }
        if (!existingUpdate.getJob().getId().equals(requestDto.getJobId())) {
            throw new IllegalArgumentException("El reporte no pertenece a ese trabajo.");
        }
        Jobs job = jobsRepository.findByIdForUpdate(requestDto.getJobId()).orElseThrow(() -> new ResourceNotFoundException("Trabajo no encontrado"));
        Users employee = usersRepository.findById(requestDto.getEmployeeId()).orElseThrow(() -> new ResourceNotFoundException("Empleado no encontrado"));

        if (requestDto.getNewPrice() != null) {
            double newPrice = requestDto.getNewPrice();

            if (!Double.isFinite(newPrice) || newPrice < 0) {
                throw new IllegalArgumentException(
                        "El precio debe ser un número válido mayor o igual a cero."
                );
            }

            job.setPay(newPrice);
        }

        String effectiveStatus = resolveReportStatus(job, requestDto);

// El trabajo y el avance deben registrar el mismo estado efectivo.
        job.setStatus(effectiveStatus);
        requestDto.setStatus(effectiveStatus);

        jobsRepository.save(job);

        existingUpdate.setJob(job);
        existingUpdate.setEmployee(employee);
        existingUpdate.setComment(requestDto.getComment());
        // Precio y estado históricos de este avance (valores independientes)
        if (requestDto.getNewPrice() != null) {
            existingUpdate.setPrice(requestDto.getNewPrice());
        }
        if (requestDto.getStatus() != null && !requestDto.getStatus().trim().isEmpty()) {
            existingUpdate.setStatus(requestDto.getStatus());
        }

        if (files != null && !files.isEmpty()) {
            List<Evidences> oldEvidences = evidencesRepository.findByJobUpdate(existingUpdate);
            if (oldEvidences != null && !oldEvidences.isEmpty()) { evidencesRepository.deleteAll(oldEvidences); }
            List<Evidences> newEvidences = files.parallelStream().map(file -> {
                String publicUrl = supabaseStorageService.uploadFile(file);
                Evidences evidence = new Evidences();
                evidence.setImageUri(publicUrl);
                evidence.setJobUpdate(existingUpdate);
                return evidence;
            }).toList();
            evidencesRepository.saveAll(newEvidences);
        }

        List<EvidencesResponseDto> evidencesResponseList = new ArrayList<>();
        List<Evidences> allEvidences = evidencesRepository.findByJobUpdate(existingUpdate);
        if (allEvidences != null) { for (Evidences ev : allEvidences) { evidencesResponseList.add(evidencesMapper.toResponse(ev)); } }
        return jobUpdateMapper.toResponse(existingUpdate, evidencesResponseList);
    }

    private String resolveReportStatus(
            Jobs job,
            JobUpdateRequestDto requestDto
    ) {
        String currentStatus = job.getStatus() == null
                ? ""
                : job.getStatus().trim().toUpperCase(java.util.Locale.ROOT);

        if ("COMPLETED".equals(currentStatus)
                || "CANCELLED".equals(currentStatus)) {
            throw new IllegalArgumentException(
                    "No se pueden registrar o editar avances de un trabajo finalizado."
            );
        }

        String requestedStatus = requestDto.getStatus() == null
                ? ""
                : requestDto.getStatus().trim().toUpperCase(java.util.Locale.ROOT);

        if (!java.util.Set.of(
                "IN_PROGRESS",
                "COMPLETED",
                "REVIEW"
        ).contains(requestedStatus)) {
            throw new IllegalArgumentException(
                    "El estado del reporte debe ser IN_PROGRESS, COMPLETED o REVIEW."
            );
        }

        // Un avance nunca puede quitar una revisión pendiente.
        if ("REVIEW".equals(currentStatus)
                || Boolean.TRUE.equals(requestDto.getHasModifications())) {
            return "REVIEW";
        }

        return requestedStatus;
    }

    private void notifyManager(Jobs job, Users employee, String pdfUrl) {
        try {
            Users manager = job.getManager();
            if (manager != null && manager.getEmail() != null) {
                String subject = "Reporte de Avance PDF: " + job.getClientName();
                String body = "Hola " + manager.getFirstName() + ",\n\n" +
                        "El empleado **" + employee.getFirstName() + " " + employee.getLastName() + "** ha reportado un avance en la obra: **" + job.getClientName() + "**.\n\n";
                if(pdfUrl != null) { body += "📄 **DESCARGAR REPORTE FIRMADO (PDF):**\n" + pdfUrl + "\n\n"; }
                body += "Puedes ingresar a tu Portal RemoMN para ver las fotos y detalles en el mapa.\n\nSaludos,\nSistema Automático RemoMN.";
                emailService.sendEmail(manager.getEmail(), subject, body);
            }
        } catch (Exception e) {
            System.err.println("Advertencia: No se pudo enviar el correo.");
        }
    }
}