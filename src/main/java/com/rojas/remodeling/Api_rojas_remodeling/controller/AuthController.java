package com.rojas.remodeling.Api_rojas_remodeling.controller;

import com.rojas.remodeling.Api_rojas_remodeling.dto.response.JwtAuthResponseDto;
import com.rojas.remodeling.Api_rojas_remodeling.dto.request.LoginRequestDto;
import com.rojas.remodeling.Api_rojas_remodeling.service.LoginService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final LoginService loginService;

    @PostMapping("/login")
    public ResponseEntity<JwtAuthResponseDto> authenticateUser(@RequestBody LoginRequestDto loginDto){
       JwtAuthResponseDto jwtAuthResponseDto =  loginService.authenticateUser(loginDto);
       return new ResponseEntity<>(jwtAuthResponseDto, HttpStatus.OK);
    }


}
