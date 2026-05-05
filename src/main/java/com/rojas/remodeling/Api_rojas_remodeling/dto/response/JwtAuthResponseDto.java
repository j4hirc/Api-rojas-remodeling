package com.rojas.remodeling.Api_rojas_remodeling.dto.response;

import lombok.Data;

@Data
public class JwtAuthResponseDto {
    private String accessToken;
    private String tokenType = "Bearer ";

    public JwtAuthResponseDto(String accessToken) {
        this.accessToken = accessToken;
    }
}
