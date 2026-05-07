package com.rojas.remodeling.Api_rojas_remodeling.service.implementation;

import com.rojas.remodeling.Api_rojas_remodeling.dto.request.JobUpdateRequestDto;
import com.rojas.remodeling.Api_rojas_remodeling.dto.response.EvidencesResponseDto;
import com.rojas.remodeling.Api_rojas_remodeling.dto.response.JobUpdateResponseDto;
import com.rojas.remodeling.Api_rojas_remodeling.model.Evidences;
import com.rojas.remodeling.Api_rojas_remodeling.model.JobUpdates;
import com.rojas.remodeling.Api_rojas_remodeling.model.Jobs;
import com.rojas.remodeling.Api_rojas_remodeling.model.Users;
import com.rojas.remodeling.Api_rojas_remodeling.repository.EvidencesRepository;
import com.rojas.remodeling.Api_rojas_remodeling.repository.JobUpdateRepository;
import com.rojas.remodeling.Api_rojas_remodeling.repository.JobsRepository;
import com.rojas.remodeling.Api_rojas_remodeling.repository.UsersRepository;
import com.rojas.remodeling.Api_rojas_remodeling.service.JobUpdateService;
import com.rojas.remodeling.Api_rojas_remodeling.service.mapper.EvidencesMapper;
import com.rojas.remodeling.Api_rojas_remodeling.service.mapper.JobUpdateMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class JobUpdateServiceImpl implements JobUpdateService {
    private final JobUpdateRepository jobUpdateRepository;
    private final JobsRepository jobsRepository;
    private final UsersRepository usersRepository;
    private final EvidencesRepository evidencesRepository;

    private final SupabaseStorageService supabaseStorageService;

    private final JobUpdateMapper jobUpdateMapper;
    private final EvidencesMapper evidencesMapper;

    @Override
    @Transactional
    public JobUpdateResponseDto createJobUpdate(JobUpdateRequestDto requestDto, List<MultipartFile> files) {

        Jobs job = jobsRepository.findById(requestDto.getJobId())
                .orElseThrow(() -> new RuntimeException("Trabajo no encontrado"));
        Users employee = usersRepository.findById(requestDto.getEmployeeId())
                .orElseThrow(() -> new RuntimeException("Empleado no encontrado"));

        JobUpdates jobUpdate = jobUpdateMapper.toEntity(requestDto, job, employee);

        JobUpdates savedUpdate = jobUpdateRepository.save(jobUpdate);

        List<EvidencesResponseDto> evidencesResponseList = new ArrayList<>();

        if (files != null && !files.isEmpty()) {
            for (MultipartFile file : files) {
                String publicUrl = supabaseStorageService.uploadFile(file);

                Evidences evidence = new Evidences();
                evidence.setImageUri(publicUrl);
                evidence.setJobUpdate(savedUpdate);

                Evidences savedEvidence = evidencesRepository.save(evidence);

                evidencesResponseList.add(evidencesMapper.toResponse(savedEvidence));
            }
        }

        return jobUpdateMapper.toResponse(savedUpdate, evidencesResponseList);
    }
}
