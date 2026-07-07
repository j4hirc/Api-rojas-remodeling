package com.rojas.remodeling.Api_rojas_remodeling.service.implementation;

import com.rojas.remodeling.Api_rojas_remodeling.dto.request.JobUpdateRequestDto;
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

        // 🔥 1. PRECIO: Solo lo actualiza si viene un número, si mandamos NULL no lo toca
        if (requestDto.getNewPrice() != null) {
            job.setPay(requestDto.getNewPrice());
        }

        if (requestDto.getStatus() != null) {
            job.setStatus(requestDto.getStatus());
            jobsRepository.save(job);
        }

        // 🔥 2. GUARDAR MATERIALES (CON CANTIDAD Y UNIDAD REALES)
        if (requestDto.getMaterials() != null) {

            // Borramos los viejos para evitar duplicados
            List<JobMaterial> existingMaterials = jobMaterialRepository.findByJobId(job.getId());
            if (!existingMaterials.isEmpty()) {
                jobMaterialRepository.deleteAll(existingMaterials);
                jobMaterialRepository.flush();
            }

            // Guardamos los nuevos con su Cantidad y Unidad
            if (!requestDto.getMaterials().isEmpty()) {
                List<JobMaterial> jobMaterials = requestDto.getMaterials().stream().map(matDto -> {
                    Materials material = materialsRepository.findById(matDto.getMaterialId())
                            .orElseThrow(() -> new ResourceNotFoundException("Material no encontrado"));

                    JobMaterial jm = new JobMaterial();
                    jm.setJob(job);
                    jm.setMaterial(material);
                    jm.setQuantity(matDto.getQuantity());
                    jm.setUnit(matDto.getUnit());
                    return jm;
                }).toList();

                jobMaterialRepository.saveAll(jobMaterials);
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

    // ... MANTÉN TU MÉTODO updateJobUpdate y notifyManager IGUALES ...
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
            System.err.println("Advertencia: No se pudo enviar el correo.");
        }
    }
}