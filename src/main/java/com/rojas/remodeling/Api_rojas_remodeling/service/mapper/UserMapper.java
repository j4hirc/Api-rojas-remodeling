package com.rojas.remodeling.Api_rojas_remodeling.service.mapper;

import com.rojas.remodeling.Api_rojas_remodeling.dto.request.UserRequestDto;
import com.rojas.remodeling.Api_rojas_remodeling.dto.response.UserResponseDto;
import com.rojas.remodeling.Api_rojas_remodeling.model.Users;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public Users userRequestToEntity(UserRequestDto userRequestDto){
        Users users = new Users();
        users.setDni(userRequestDto.getDni());
        users.setFirstName(userRequestDto.getFirstName());
        users.setMiddleName(userRequestDto.getMiddleName());
        users.setLastName(userRequestDto.getLastName());
        users.setSecondSurname(userRequestDto.getSecondSurname());
        users.setEmail(userRequestDto.getEmail());
        users.setPassword(userRequestDto.getPassword());
        users.setPhone(userRequestDto.getPhone());
        users.setDateOfBirth(userRequestDto.getDateOfBirth());
        users.setDateOfEntry(userRequestDto.getDateOfEntry());
        users.setStatus(userRequestDto.getStatus());
        users.setTitle(userRequestDto.getTitle());

        return users;
    }


    public UserResponseDto entityToUserResponse(Users users){
        UserResponseDto userResponseDto = new UserResponseDto();
        userResponseDto.setId(users.getId());
        userResponseDto.setDni(users.getDni());
        userResponseDto.setName(users.getFirstName() + " " + users.getLastName());
        userResponseDto.setEmail(users.getEmail());
        return userResponseDto;
    }

}
