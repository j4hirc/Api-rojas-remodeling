package com.rojas.remodeling.Api_rojas_remodeling.service;

import com.rojas.remodeling.Api_rojas_remodeling.dto.request.EditProfileDto;
import com.rojas.remodeling.Api_rojas_remodeling.dto.request.UserRequestDto;
import com.rojas.remodeling.Api_rojas_remodeling.dto.response.UserResponseDto;

import java.util.List;

public interface UserService {
    List<UserResponseDto> findAll();

    List<UserResponseDto> findUnemployed();

    UserResponseDto findById(Long id);

    UserResponseDto updateUser(Long id, UserRequestDto userRequestDto);

    UserResponseDto createUser(UserRequestDto userRequestDto);

    UserResponseDto editProfile(Long id, EditProfileDto editProfileDto);

    List<UserResponseDto> findByName(String name);

    List<UserResponseDto> findByRoleName(String role);

    UserResponseDto findByDni(String dni);




}
