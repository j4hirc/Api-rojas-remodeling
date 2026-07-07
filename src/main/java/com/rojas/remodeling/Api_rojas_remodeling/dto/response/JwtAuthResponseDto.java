package com.rojas.remodeling.Api_rojas_remodeling.dto.response;

import lombok.Data;
import java.util.List;

@Data
public class JwtAuthResponseDto {
    private String accessToken;
    private String tokenType = "Bearer ";
    private String email;
    private List<String> roles;


    public JwtAuthResponseDto(String accessToken, String email, List<String> roles) {
        this.accessToken = accessToken;
        this.email = email;
        this.roles = roles;
    }
}