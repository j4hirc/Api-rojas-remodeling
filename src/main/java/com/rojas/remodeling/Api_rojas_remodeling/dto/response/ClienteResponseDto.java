package com.rojas.remodeling.Api_rojas_remodeling.dto.response;

import lombok.Data;

@Data
public class ClienteResponseDto {

    private Long id;

    private String clientName;

    private String clientPhone;

    private String address;

    private Double latitude;

    private Double longitude;
}
