package com.rojas.remodeling.Api_rojas_remodeling.service;

import com.rojas.remodeling.Api_rojas_remodeling.dto.request.UserRequestDto;
import com.rojas.remodeling.Api_rojas_remodeling.dto.response.UserResponseDto;

public interface UserService {
    UserResponseDto createUser(UserRequestDto userRequestDto);

}
