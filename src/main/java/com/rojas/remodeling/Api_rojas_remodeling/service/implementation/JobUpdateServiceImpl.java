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

    @Override
    @Transactional
    public JobUpdateResponseDto createJobUpdate(JobUpdateRequestDto requestDto, List<MultipartFile> files) {

        Jobs job = jobsRepository.findById(requestDto.getJobId())
                .orElseThrow(() -> new RuntimeException("Trabajo no encontrado"));
        Users employee = usersRepository.findById(requestDto.getEmployeeId())
                .orElseThrow(() -> new RuntimeException("Empleado no encontrado"));

        // 🔥 CORRECCIÓN 1: EL PRECIO
        // Solo si el Empleado MANDÓ una alerta de cambio de precio (> 0), lo actualizamos.
        // Si mandó null o 0, SIGNIFICA QUE NO LO TOCÓ, así que dejamos el que ya estaba.
        if (requestDto.getNewPrice() != null && requestDto.getNewPrice() > 0) {
            job.setPay(requestDto.getNewPrice());
        }

        if (requestDto.getStatus() != null && !requestDto.getStatus().trim().isEmpty()) {
            job.setStatus(requestDto.getStatus());
        }
        jobsRepository.save(job);

        // 🔥 CORRECCIÓN 2: LOS MATERIALES
        // NO BORRAMOS TODOS. Si el empleado reportó un material, buscamos si ya estaba
        // y le actualizamos la cantidad. Si es nuevo, lo agregamos.
        if (requestDto.getMaterials() != null && !requestDto.getMaterials().isEmpty()) {
            List<JobMaterial> existingMaterials = jobMaterialRepository.findByJobId(job.getId());

            for (MaterialSelectionDto dtoMat : requestDto.getMaterials()) {
                Materials material = materialsRepository.findById(dtoMat.getMaterialId())
                        .orElseThrow(() -> new ResourceNotFoundException("Material no encontrado"));

                // Busca si este material ya estaba asignado a este trabajo
                Optional<JobMaterial> existingJm = existingMaterials.stream()
                        .filter(jm -> jm.getMaterial().getId().equals(material.getId()))
                        .findFirst();

                if (existingJm.isPresent()) {
                    // Si ya estaba, le actualizamos la cantidad y unidad
                    JobMaterial jmToUpdate = existingJm.get();
                    jmToUpdate.setQuantity(dtoMat.getQuantity());
                    jmToUpdate.setUnit(dtoMat.getUnit() != null ? dtoMat.getUnit() : "N/A");
                    jobMaterialRepository.save(jmToUpdate);
                } else {
                    // Si el empleado agregó un material que el jefe no le había puesto, lo creamos
                    JobMaterial newJm = new JobMaterial();
                    newJm.setJob(job);
                    newJm.setMaterial(material);
                    newJm.setQuantity(dtoMat.getQuantity());
                    newJm.setUnit(dtoMat.getUnit() != null ? dtoMat.getUnit() : "N/A");
                    jobMaterialRepository.save(newJm);
                }
            }
        }

        JobUpdates jobUpdate = jobUpdateMapper.toEntity(requestDto, job, employee);
        JobUpdates savedUpdate = jobUpdateRepository.save(jobUpdate);

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

    // ... (El resto de tus métodos updateJobUpdate y notifyManager quedan igual) ...
    @Override
    @Transactional
    public JobUpdateResponseDto updateJobUpdate(Long id, JobUpdateRequestDto requestDto, List<MultipartFile> files) {
        JobUpdates existingUpdate = jobUpdateRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Actualización de trabajo no encontrada"));
        Jobs job = jobsRepository.findById(requestDto.getJobId()).orElseThrow(() -> new ResourceNotFoundException("Trabajo no encontrado"));
        Users employee = usersRepository.findById(requestDto.getEmployeeId()).orElseThrow(() -> new ResourceNotFoundException("Empleado no encontrado"));

        if (requestDto.getStatus() != null) {
            job.setStatus(requestDto.getStatus());
            jobsRepository.save(job);
        }

        existingUpdate.setJob(job);
        existingUpdate.setEmployee(employee);
        existingUpdate.setComment(requestDto.getComment());

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
            System.err.println("Advertencia: No se pudo enviar el correo. Causa: " + e.getMessage());
        }
    }
}