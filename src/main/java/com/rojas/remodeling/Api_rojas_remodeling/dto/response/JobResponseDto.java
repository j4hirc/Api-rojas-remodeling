package com.rojas.remodeling.Api_rojas_remodeling.dto.response;

import lombok.Data;
import java.time.LocalDate; // <-- IMPORTANTE
import java.util.List;

@Data
public class JobResponseDto {

    private Long jobId;

    private String clientName;

    private String clientPhone;

    private String description;

    private String address;

    private Double latitude;

    private Double longitude;

    private String safeDepositBoxCodes;

    private String status;

    private Double pay;

    private LocalDate jobDate;

    private Long employeeId;

    private Long managerId;

    private String nameEmployee;

    private String nameManager;

    private List<MaterialsResponseDto> materials;

    private List<JobUpdateResponseDto> updateJob;
}