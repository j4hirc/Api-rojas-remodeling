package com.rojas.remodeling.Api_rojas_remodeling.service;

import com.rojas.remodeling.Api_rojas_remodeling.dto.response.JwtAuthResponseDto;
import com.rojas.remodeling.Api_rojas_remodeling.dto.request.LoginRequestDto;

public interface LoginService {
    JwtAuthResponseDto authenticateUser(LoginRequestDto loginDto);


    void forgotPassword(String email);


}
