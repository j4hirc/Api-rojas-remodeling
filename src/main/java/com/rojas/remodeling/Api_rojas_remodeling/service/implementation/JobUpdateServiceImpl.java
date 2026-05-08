    package com.rojas.remodeling.Api_rojas_remodeling.service.implementation;

    import com.rojas.remodeling.Api_rojas_remodeling.dto.request.JobUpdateRequestDto;
    import com.rojas.remodeling.Api_rojas_remodeling.dto.response.EvidencesResponseDto;
    import com.rojas.remodeling.Api_rojas_remodeling.dto.response.JobUpdateResponseDto;
    import com.rojas.remodeling.Api_rojas_remodeling.exception.ResourceNotFoundException;
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
        private final EmailService emailService;


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

            notifyManager(job, employee);

            return jobUpdateMapper.toResponse(savedUpdate, evidencesResponseList);
        }



        @Override
        @Transactional
        public JobUpdateResponseDto updateJobUpdate(Long id, JobUpdateRequestDto requestDto, List<MultipartFile> files) {

            JobUpdates existingUpdate = jobUpdateRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Actualización de trabajo no encontrada"));

            Jobs job = jobsRepository.findById(requestDto.getJobId())
                    .orElseThrow(() -> new ResourceNotFoundException("Trabajo no encontrado"));
            Users employee = usersRepository.findById(requestDto.getEmployeeId())
                    .orElseThrow(() -> new ResourceNotFoundException("Empleado no encontrado"));

            existingUpdate.setJob(job);
            existingUpdate.setEmployee(employee);
            existingUpdate.setComment(requestDto.getComment());

            if (files != null && !files.isEmpty()) {

                List<Evidences> oldEvidences = evidencesRepository.findByJobUpdate(existingUpdate);

                if (oldEvidences != null && !oldEvidences.isEmpty()) {
                    evidencesRepository.deleteAll(oldEvidences);
                }

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

            if (allEvidences != null) {
                for (Evidences ev : allEvidences) {
                    evidencesResponseList.add(evidencesMapper.toResponse(ev));
                }
            }

            return jobUpdateMapper.toResponse(existingUpdate, evidencesResponseList);
        }




        private void notifyManager(Jobs job, Users employee) {
            Users manager = job.getManager();

            if (manager != null && manager.getEmail() != null) {
                String managerEmail = manager.getEmail();
                String employeeName = employee.getFirstName() + " " + employee.getLastName();
                String jobName = job.getDescription();

                String subject = "Nueva actualización en el trabajo: " + jobName;
                String body = "Hola " + manager.getFirstName() + " " + manager.getLastName()  +",\n\n" +
                        "El empleado " + employeeName + " acaba de registrar una nueva actualización " +
                        "para el trabajo '" + jobName + "'.\n\n" +
                        "Por favor, revisa el sistema para ver las evidencias y detalles.\n\n" +
                        "Saludos,\n" +
                        "Sistema Rojas Remodeling";

                emailService.sendEmail(managerEmail, subject, body);
            }
        }



    }
