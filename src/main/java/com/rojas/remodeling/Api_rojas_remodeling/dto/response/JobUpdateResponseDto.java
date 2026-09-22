package com.rojas.remodeling.Api_rojas_remodeling.dto.response;


import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class JobUpdateResponseDto {

    private Long jobUpdateId;

    private String comment;

    private LocalDateTime date;

    private Double price;

    private String status;

    private List<EvidencesResponseDto> evidences;

}
