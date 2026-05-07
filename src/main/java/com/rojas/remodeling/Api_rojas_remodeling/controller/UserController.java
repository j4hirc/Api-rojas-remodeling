package com.rojas.remodeling.Api_rojas_remodeling.controller;

import com.rojas.remodeling.Api_rojas_remodeling.dto.request.EditProfileDto;
import com.rojas.remodeling.Api_rojas_remodeling.dto.request.UserRequestDto;
import com.rojas.remodeling.Api_rojas_remodeling.dto.response.UserResponseDto;
import com.rojas.remodeling.Api_rojas_remodeling.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/user")
public class UserController {

    private final UserService userService;

    @GetMapping("/debug")
    public Object debug(Authentication authentication){

        return authentication;
    }


    @GetMapping("/all-users")
    @PreAuthorize("hasAnyRole('ADMIN', 'JEFE')")
    public ResponseEntity<List<UserResponseDto>> findAll(){
        return ResponseEntity.ok(userService.findAll());
    }

    @GetMapping("/dni-user/{dni}")
    @PreAuthorize("hasAnyRole('ADMIN', 'JEFE')")
    public ResponseEntity<UserResponseDto> findByDni(@PathVariable String dni){
        return ResponseEntity.ok(userService.findByDni(dni));
    }



    @GetMapping("/id-user/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'JEFE')")
    public ResponseEntity<UserResponseDto> findById(@PathVariable Long id){
        return ResponseEntity.ok(userService.findById(id));
    }


    @GetMapping("/name-user/{name}")
    @PreAuthorize("hasAnyRole('ADMIN', 'JEFE')")
    public ResponseEntity<List<UserResponseDto>> findByName(@PathVariable String name){
        return ResponseEntity.ok(userService.findByName(name));
    }

    @GetMapping("/name-rol-user/{roleName}")
    @PreAuthorize("hasAnyRole('ADMIN', 'JEFE')")
    public ResponseEntity<List<UserResponseDto>> findByRoleName(@PathVariable String roleName){
        return ResponseEntity.ok(userService.findByRoleName(roleName));
    }



    @PostMapping("/create-user")
    @PreAuthorize("hasAnyRole('ADMIN', 'JEFE')")
    public ResponseEntity<UserResponseDto> createUser(@Valid @RequestBody UserRequestDto userRequestDto){
        UserResponseDto userResponseDto = userService.createUser(userRequestDto);
        return new ResponseEntity<>(userResponseDto, HttpStatus.CREATED);
    }


    @PutMapping("/update-user/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'JEFE')")
    public ResponseEntity<UserResponseDto> updateUser(@PathVariable Long id, @Valid @RequestBody UserRequestDto userRequestDto){
        UserResponseDto userResponseDto = userService.updateUser(id,userRequestDto);
        return ResponseEntity.ok(userResponseDto);
    }


    @PutMapping("/edit-user/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'JEFE', 'EMPLOYEE')")
    public ResponseEntity<UserResponseDto> editUser(@PathVariable Long id, @Valid @RequestBody EditProfileDto editProfileDto){
        return ResponseEntity.ok(userService.editProfile(id, editProfileDto));
    }







}
