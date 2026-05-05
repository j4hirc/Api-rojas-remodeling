package com.rojas.remodeling.Api_rojas_remodeling.controller;

import com.rojas.remodeling.Api_rojas_remodeling.dto.request.UserRequestDto;
import com.rojas.remodeling.Api_rojas_remodeling.dto.response.UserResponseDto;
import com.rojas.remodeling.Api_rojas_remodeling.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/user")
public class UserController {

    private final UserService userService;

    @GetMapping("/debug")
    public Object debug(Authentication authentication){
        return authentication;
    }

    @PostMapping("/create-user")
    @PreAuthorize("hasAnyRole('ADMIN', 'JEFE')")
    public ResponseEntity<UserResponseDto> createUser(@RequestBody UserRequestDto userRequestDto){
        UserResponseDto userResponseDto = userService.createUser(userRequestDto);
        return new ResponseEntity<>(userResponseDto, HttpStatus.CREATED);
    }


}
