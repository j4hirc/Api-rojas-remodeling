package com.rojas.remodeling.Api_rojas_remodeling.service.implementation;

import com.rojas.remodeling.Api_rojas_remodeling.dto.request.UserRequestDto;
import com.rojas.remodeling.Api_rojas_remodeling.dto.response.UserResponseDto;
import com.rojas.remodeling.Api_rojas_remodeling.model.Roles;
import com.rojas.remodeling.Api_rojas_remodeling.model.Users;
import com.rojas.remodeling.Api_rojas_remodeling.repository.RolesRepository;
import com.rojas.remodeling.Api_rojas_remodeling.repository.UsersRepository;
import com.rojas.remodeling.Api_rojas_remodeling.service.UserService;
import com.rojas.remodeling.Api_rojas_remodeling.service.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UsersRepository usersRepository;
    private final RolesRepository rolesRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    @Override
    public UserResponseDto createUser(UserRequestDto userRequestDto) {
        validateUniqueFields(userRequestDto.getDni(), userRequestDto.getEmail(), userRequestDto.getPhone());

        Users user = userMapper.userRequestToEntity(userRequestDto);
        user.setPassword(passwordEncoder.encode(userRequestDto.getPassword()));

        Set<Roles> roles = userRequestDto.getRoles().stream()
                .map(roleName -> rolesRepository.findByName(roleName)
                        .orElseThrow(() -> new RuntimeException("Rol no encontrado: " + roleName)))
                .collect(Collectors.toSet());

        user.setRoles(roles);

        Users savedUser = usersRepository.save(user);

        return userMapper.entityToUserResponse(savedUser);
    }



    private void validateUniqueFields(String dni, String email, String phone){

        if(usersRepository.existsByDni(dni)){
            throw new RuntimeException("DNI already exists");
        }

        if(usersRepository.existsByEmail(email)){
            throw new RuntimeException("Email already exists");
        }

        if(usersRepository.existsByPhone(phone)){
            throw new RuntimeException("Phone already exists");
        }
    }

}
