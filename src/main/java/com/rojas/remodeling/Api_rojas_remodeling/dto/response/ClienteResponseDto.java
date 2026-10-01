package com.rojas.remodeling.Api_rojas_remodeling.dto.response;

import lombok.Data;

@Data
public class ClienteResponseDto {

    private Long id;

    private String clientName;

    private String companyName;

    private String contactName;

    private String clientPhone;

    private String address;

    private String codeBox;

    private Double latitude;

    private Double longitude;
}