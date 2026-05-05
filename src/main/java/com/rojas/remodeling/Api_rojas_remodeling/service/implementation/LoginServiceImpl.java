package com.rojas.remodeling.Api_rojas_remodeling.service.implementation;

import com.rojas.remodeling.Api_rojas_remodeling.dto.response.JwtAuthResponseDto;
import com.rojas.remodeling.Api_rojas_remodeling.dto.request.LoginRequestDto;
import com.rojas.remodeling.Api_rojas_remodeling.security.jwt.JwtGenerator;
import com.rojas.remodeling.Api_rojas_remodeling.service.LoginService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LoginServiceImpl implements LoginService {
    private final AuthenticationManager authenticationManager;
    private final JwtGenerator jwtGenerator;


    @Override
    public JwtAuthResponseDto authenticateUser(LoginRequestDto loginDto) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginDto.getEmail(), loginDto.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String token = jwtGenerator.generateToken(authentication);

        return new JwtAuthResponseDto(token);
    }
}
