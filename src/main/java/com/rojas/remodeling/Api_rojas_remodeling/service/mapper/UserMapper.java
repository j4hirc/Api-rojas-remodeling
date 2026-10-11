package com.rojas.remodeling.Api_rojas_remodeling.service.mapper;

import com.rojas.remodeling.Api_rojas_remodeling.dto.request.EditProfileDto;
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
        users.setCompanyName(normalizeCompany(userRequestDto.getCompanyName()));
        users.setColor(userRequestDto.getColor());
        users.setHierarchyLevel(userRequestDto.getHierarchyLevel());

        return users;
    }


    public UserResponseDto entityToUserResponse(Users users){
        UserResponseDto userResponseDto = new UserResponseDto();
        userResponseDto.setUserId(users.getId());
        userResponseDto.setDni(users.getDni());
        userResponseDto.setName(users.getFirstName() + " " + users.getLastName());

        userResponseDto.setFirstName(users.getFirstName());
        userResponseDto.setMiddleName(users.getMiddleName());
        userResponseDto.setLastName(users.getLastName());
        userResponseDto.setSecondSurname(users.getSecondSurname());
        userResponseDto.setTitle(users.getTitle());
        userResponseDto.setCompanyName(users.getCompanyName());
        userResponseDto.setDateOfBirth(users.getDateOfBirth());
        userResponseDto.setDateOfEntry(users.getDateOfEntry());
        userResponseDto.setStatus(users.getStatus());
        userResponseDto.setEmail(users.getEmail());
        userResponseDto.setPhone(users.getPhone());
        userResponseDto.setColor(users.getColor());
        userResponseDto.setRoles(users.getRoles());
        userResponseDto.setHierarchyLevel(users.getHierarchyLevel());
        return userResponseDto;
    }


    public void updateEntityFromEditProfile(EditProfileDto dto, Users entity) {
        entity.setDni(dto.getDni());
        entity.setFirstName(dto.getFirstName());
        entity.setMiddleName(dto.getMiddleName());
        entity.setLastName(dto.getLastName());
        entity.setSecondSurname(dto.getSecondSurname());
        entity.setEmail(dto.getEmail());
        entity.setPhone(dto.getPhone());
        entity.setDateOfBirth(dto.getDateOfBirth());
        entity.setTitle(dto.getTitle());
        entity.setColor(dto.getColor());
    }

    public void updateEntityFromRequest(UserRequestDto dto, Users entity) {
        entity.setDni(dto.getDni());
        entity.setFirstName(dto.getFirstName());
        entity.setMiddleName(dto.getMiddleName());
        entity.setLastName(dto.getLastName());
        entity.setSecondSurname(dto.getSecondSurname());
        entity.setEmail(dto.getEmail());
        entity.setPhone(dto.getPhone());
        entity.setDateOfBirth(dto.getDateOfBirth());
        entity.setDateOfEntry(dto.getDateOfEntry());
        entity.setStatus(dto.getStatus());
        entity.setTitle(dto.getTitle());
        entity.setCompanyName(normalizeCompany(dto.getCompanyName()));
        entity.setColor(dto.getColor());
        entity.setHierarchyLevel(dto.getHierarchyLevel());
    }


    private String normalizeCompany(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        return value.trim();
    }

}
