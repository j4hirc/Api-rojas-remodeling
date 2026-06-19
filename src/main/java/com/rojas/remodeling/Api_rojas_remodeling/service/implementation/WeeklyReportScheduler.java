package com.rojas.remodeling.Api_rojas_remodeling.service.implementation;

import com.rojas.remodeling.Api_rojas_remodeling.model.Jobs;
import com.rojas.remodeling.Api_rojas_remodeling.model.Users;
import com.rojas.remodeling.Api_rojas_remodeling.repository.JobsRepository;
import com.rojas.remodeling.Api_rojas_remodeling.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class WeeklyReportScheduler {

    private final JobsRepository jobsRepository;
    private final UsersRepository usersRepository;
    private final EmailService emailService;


    @Scheduled(cron = "0 0 8 * * MON")
    public void sendWeeklyPaymentReport() {
        List<Jobs> allJobs = jobsRepository.findAll();
        List<Jobs> completedJobs = allJobs.stream()
                .filter(job -> "COMPLETED".equals(job.getStatus()))
                .collect(Collectors.toList());

        if (completedJobs.isEmpty()) return;

        Map<Users, Double> paymentsByEmployee = completedJobs.stream()
                .filter(job -> job.getEmployee() != null)
                .collect(Collectors.groupingBy(
                        Jobs::getEmployee,
                        Collectors.summingDouble(Jobs::getPay)
                ));


        StringBuilder emailBody = new StringBuilder();
        emailBody.append("<h2>💰 Resumen Semanal de Pagos a Subcontratistas</h2>");
        emailBody.append("<p>Este es el reporte automático de los trabajos completados:</p>");
        emailBody.append("<table border='1' cellpadding='10' cellspacing='0' style='border-collapse: collapse; width: 100%;'>");
        emailBody.append("<tr style='background-color: #0f4c81; color: white;'><th>Empleado / Subcontratista</th><th>Total a Pagar ($)</th></tr>");

        for (Map.Entry<Users, Double> entry : paymentsByEmployee.entrySet()) {
            Users emp = entry.getKey();
            Double totalPay = entry.getValue();
            emailBody.append("<tr>")
                    .append("<td>").append(emp.getFirstName()).append(" ").append(emp.getLastName()).append("</td>")
                    .append("<td style='color: green; font-weight: bold;'>$").append(String.format("%.2f", totalPay)).append("</td>")
                    .append("</tr>");
        }
        emailBody.append("</table>");
        emailBody.append("<br><p>Saludos,<br>Sistema Automático RemoMN.</p>");

        List<Users> admins = usersRepository.findAll().stream()
                .filter(u -> u.getRoles().stream().anyMatch(r -> r.getName().equals("ROLE_ADMIN") & r.getName().equals("ROLE_JEFE")))
                .collect(Collectors.toList());

        for (Users admin : admins) {
            if (admin.getEmail() != null) {
                emailService.sendEmail(admin.getEmail(), "Reporte de Nómina Semanal", emailBody.toString());
            }
        }
    }
}
